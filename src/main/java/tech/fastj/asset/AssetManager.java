package tech.fastj.asset;

import tech.fastj.asset.event.AssetChangeEvent;
import tech.fastj.asset.event.AssetChangeType;
import tech.fastj.engine.FastJEngine;
import tech.fastj.gameloop.CoreLoopState;
import tech.fastj.graphics.Drawable;
import tech.fastj.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class AssetManager {

    public static final String ResourcesPathAlias = "res:///";
    public static final String[] ResourcesPathLocations = {"/", "res/", "resources/"};

    private final Map<String, Function<String, String[]>> pathAliases;
    private final Map<AssetType<?>, AssetCreator<?, ? extends Asset<?>>> assetCreators;
    private final Map<AssetType<?>, AssetLoader<?, ? extends Asset<?>>> assetLoaders;
    private final Map<AssetDefinition<? extends AssetType<?>>, Asset<?>> assets;
    private final Map<AssetUserDefinition<? extends AssetType<?>, ?>, AssetUser<?, ? extends Asset<?>, ?>> assetUsers;

    private ExecutorService asyncAssetLoader;

    public AssetManager() {
        pathAliases = new ConcurrentHashMap<>();
        assetCreators = new ConcurrentHashMap<>();
        assetLoaders = new ConcurrentHashMap<>();
        assets = new ConcurrentHashMap<>();
        assetUsers = new ConcurrentHashMap<>();
        pathAliases.put(ResourcesPathAlias, (alias) -> ResourcesPathLocations);

        reset();
    }

    public void reset() {
        if (asyncAssetLoader != null) {
            asyncAssetLoader.shutdownNow();
        }

        asyncAssetLoader = Executors.newWorkStealingPool();
        assets.clear();
    }

    public <AT extends AssetType<AT>, RawAsset, A extends Asset<RawAsset>>
    void addAssetCreator(AT assetType, AssetCreator<RawAsset, A> assetCreator) {
        var replacedAssetLoader = assetCreators.put(assetType, assetCreator);

        if (replacedAssetLoader != null) {
            Log.debug("Replaced existing asset loader for \"{}\" asset type", assetType.getName());
        }
    }

    public <AT extends AssetType<AT>, RawAsset, A extends Asset<RawAsset>>
    void addAssetLoader(AT assetType, AssetLoader<RawAsset, A> assetLoader) {
        var replacedAssetLoader = assetLoaders.put(assetType, assetLoader);

        if (replacedAssetLoader != null) {
            Log.debug("Replaced existing asset loader for \"{}\" asset type", assetType.getName());
        }
    }

    public <AT extends AssetType<AT>, D extends Drawable, RawAsset, A extends Asset<RawAsset>>
    void addAssetUser(Class<D> drawableClass, AT assetType, AssetUser<RawAsset, A, D> assetUser) {
        var assetUserDefinition = new AssetUserDefinition<>(assetType, drawableClass);
        var replacedAssetUser = assetUsers.put(assetUserDefinition, assetUser);

        if (replacedAssetUser != null) {
            Log.debug("Replaced existing asset loader for \"{}\" asset type", assetType.getName());
        }
    }

    public <AT extends AssetType<AT>> Future<?> load(AT assetType, String path) {
        // create asset instance

        String pathAlias = getPathAlias(path);

        if (!addAssetEarly(assetType, path, pathAlias)) {
            Log.warn(AssetManager.class, "Unable to load {} asset {} from \"{}\".", path);
            return CompletableFuture.completedFuture(null);
        }

        // spin thread
        //:
        // resolve path aliases
        // try find asset content
        // load asset content
        // update asset instance with content
        //:
        // return future task

        return asyncAssetLoader.submit(() -> tryLoadAsset(assetType, pathAlias, path));
    }

    @SuppressWarnings("unchecked")
    private <AT extends AssetType<AT>, RawAsset, A extends Asset<RawAsset>>
    void tryLoadAsset(AT assetType, String pathAlias, String pathWithoutAlias) {
        String[] possibleDirectories;

        if (pathAliases.containsKey(pathAlias)) {
            possibleDirectories = pathAliases.get(pathAlias).apply(pathAlias);
        } else {
            possibleDirectories = pathAliases.get(ResourcesPathAlias).apply(pathAlias);
        }

        AssetLoader<RawAsset, A> assetLoader = (AssetLoader<RawAsset, A>) assetLoaders.get(assetType);

        if (assetLoader == null) {
            Log.warn(
                AssetManager.class,
                "Unable to find {} asset loader for \"{}{}\", using default asset instead",
                assetType,
                pathAlias,
                pathWithoutAlias
            );

            return;
        }

        AssetBase<RawAsset, A> assetBase = assetLoader.loadAsset(pathAlias, pathWithoutAlias, possibleDirectories);

        if (assetBase == null) {
            Log.warn(
                AssetManager.class,
                "Unable to load {} asset from \"{}{}\", using default asset instead",
                assetType,
                pathAlias,
                pathWithoutAlias
            );

            return;
        }

        A asset = (A) assets.get(new AssetDefinition<>(assetType, pathWithoutAlias));
        asset.updateAsset(assetBase);

        AssetChangeEvent<RawAsset, A> assetLoadEvent = new AssetChangeEvent<>(asset, AssetChangeType.Modified);
        FastJEngine.getGameLoop().fireEvent(assetLoadEvent, CoreLoopState.EarlyUpdate);
    }

    private <AT extends AssetType<AT>> boolean addAssetEarly(AT assetType, String path, String pathAlias) {
        var assetCreator = assetCreators.get(assetType);

        if (assetCreator == null) {
            return false;
        }

        Asset<?> asset = assetCreator.createFromPath(this, path, pathAlias);
        AssetDefinition<AT> assetDefinition = new AssetDefinition<>(assetType, path);
        assets.put(assetDefinition, asset);

        return true;
    }

    @SuppressWarnings("unchecked")
    public <AT extends AssetType<AT>, RawAsset, A extends Asset<RawAsset>> A get(AT assetType, String path) {
        // if does not exist, load

        String pathAlias = getPathAlias(path);
        AssetDefinition<AT> assetDefinition = new AssetDefinition<>(assetType, pathAlias);

        if (!assets.containsKey(assetDefinition)) {
            load(assetType, path);
        }

        // return asset instance

        return (A) assets.get(assetDefinition);
    }

    @SuppressWarnings("unchecked")
    public <AT extends AssetType<AT>, RawAsset, A extends Asset<RawAsset>, T> T use(Class<T> returnedClass, AT assetType, String path) {
        // if does not exist, load

        String pathAlias = getPathAlias(path);
        AssetDefinition<AT> assetDefinition = new AssetDefinition<>(assetType, pathAlias);

        if (!assets.containsKey(assetDefinition)) {
            load(assetType, path);
        }

        // try construct instance from asset

        A asset = (A) assets.get(assetDefinition);

        AssetUserDefinition<AT, T> assetUserDefinition = new AssetUserDefinition<>(assetType, returnedClass);
        AssetUser<RawAsset, A, T> assetUser = (AssetUser<RawAsset, A, T>) assetUsers.get(assetUserDefinition);

        if (assetUser == null) {
            Log.warn(
                AssetManager.class,
                "Unable to find asset user for {}, looking for default method...",
                assetUserDefinition
            );

            try {
                return (T) returnedClass.getMethod("createDefaultInstance", Asset.class).invoke(null, asset);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Log.warn(
                    AssetManager.class,
                    "Unable to find method \"createDefaultInstance\" in {}, returning null",
                    returnedClass.getName()
                );

                return null;
            }
        }

        // return result or null

        return assetUser.createFromAsset(this, asset);
    }

    private boolean hasPathAlias(String path) {
        // TODO: less magic constants

        int aliasIndex = path.indexOf(":///");
        if (aliasIndex == -1) {
            return false;
        }

        String pathAlias = path.substring(0, aliasIndex + 4);
        return pathAliases.containsKey(pathAlias);
    }

    private String getPathAlias(String path) {
        // TODO: less magic constants

        if (!hasPathAlias(path)) {
            return "";
        }

        int aliasIndex = path.indexOf(":///");
        return path.substring(0, aliasIndex + 4);
    }
}
