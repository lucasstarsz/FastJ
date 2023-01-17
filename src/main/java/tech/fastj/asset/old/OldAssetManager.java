package tech.fastj.asset.old;

import tech.fastj.graphics.Drawable;
import tech.fastj.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageWriterSpi;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

public class OldAssetManager {

    public static final String ResourcesPathAlias = "res:///";
    public static final String[] ResourcesPathLocations = {"/", "res/", "resources/"};

    private static final Set<String> SupportedImageFormats = new CopyOnWriteArraySet<>();
    private static final Set<String> SupportedAudioFormats = new CopyOnWriteArraySet<>();

    private static final Set<AudioFileFormat.Type> AudioTypes = Set.of(
        AudioFileFormat.Type.WAVE,
        AudioFileFormat.Type.AU,
        AudioFileFormat.Type.SND,
        AudioFileFormat.Type.AIFC,
        AudioFileFormat.Type.AIFF,
        new AudioFileFormat.Type("MP3", "mp3"),
        new AudioFileFormat.Type("OGG Vorbis", "ogg"),
        new AudioFileFormat.Type("Free Lossless Audio Codec", "flac")
    );

    static {
        IIORegistry registry = IIORegistry.getDefaultInstance();
        Iterator<ImageWriterSpi> serviceProviders = registry.getServiceProviders(ImageWriterSpi.class, false);

        while (serviceProviders.hasNext()) {
            ImageWriterSpi imageWriterSpi = serviceProviders.next();
            SupportedImageFormats.addAll(Arrays.asList(imageWriterSpi.getFormatNames()));
        }

        for (AudioFileFormat.Type audioType : AudioTypes) {
            if (AudioSystem.isFileTypeSupported(audioType)) {
                SupportedAudioFormats.add(audioType.getExtension());
            }
        }
    }

    private final Set<String> knownPathAliases;
    private final Map<String, Function<String, String[]>> pathAliases;
    private final EnumMap<OldAssetType, OldAssetLoader<?, ? extends OldAsset<?>>> assetLoaders;
    private final Map<String, OldAsset<?>> assets;

    private final Map<String, Function<? extends OldAsset<?>, ? extends Drawable>> drawableCreators;
    private ExecutorService asyncAssetLoader;

    public OldAssetManager() {
        pathAliases = new ConcurrentHashMap<>();
        knownPathAliases = new CopyOnWriteArraySet<>();

        assetLoaders = new EnumMap<>(OldAssetType.class);
        assets = new ConcurrentHashMap<>();

        knownPathAliases.add(ResourcesPathAlias);
        pathAliases.put(ResourcesPathAlias, (alias) -> ResourcesPathLocations);

        drawableCreators = new ConcurrentHashMap<>();

        asyncAssetLoader = Executors.newWorkStealingPool();
    }

    public void reset() {
        asyncAssetLoader.shutdownNow();
        asyncAssetLoader = Executors.newWorkStealingPool();
        assets.clear();
        // TODO: fire asset change event?
    }

    public <RawAsset, A extends OldAsset<RawAsset>> void addAssetLoader(OldAssetType oldAssetType, OldAssetLoader<RawAsset, A> oldAssetLoader) {
        var replacedAssetLoader = assetLoaders.put(oldAssetType, oldAssetLoader);

        if (replacedAssetLoader != null) {
            Log.debug("Replaced existing asset loader for asset type \"{}\"", oldAssetType.name());
        }
    }

    public <D extends Drawable, RawAsset, A extends OldAsset<RawAsset>> void addDrawableCreator(OldAssetType oldAssetType, Class<D> drawableClass,
                                                                                                Function<A, D> drawableCreator) {
        var replacedDrawableCreator = drawableCreators.put(oldAssetType.getAssetClass().getName() + drawableClass.getName(), drawableCreator);

        if (replacedDrawableCreator != null) {
            Log.debug(
                "Replaced existing drawable creator for drawable class \"{}\" on asset type \"{}\"",
                drawableClass,
                oldAssetType.name()
            );
        }
    }

    @SuppressWarnings("unchecked")
    public <RawAsset, A extends OldAsset<RawAsset>> Future<?> load(OldAssetType oldAssetType, String path) {
        if (assets.containsKey(oldAssetType.getAssetClass().getName() + path)) {
            Log.trace(OldAssetManager.class, "Tried loading asset \"{}\", but it was already loaded.", path);
            return CompletableFuture.completedFuture(null);
        }

        assets.put(oldAssetType.getAssetClass().getName() + path, createAssetInstance(oldAssetType, path));

        // resolve path aliases
        String pathAlias = getPathAlias(path);
        String[] possiblePathAliases;

        if (pathAliases.containsKey(pathAlias)) {
            possiblePathAliases = pathAliases.get(pathAlias).apply(pathAlias);
        } else {
            possiblePathAliases = pathAliases.get(ResourcesPathAlias).apply(pathAlias);
        }

        return asyncAssetLoader.submit(() -> {
            // try load asset based on asset type, location, and directory
            OldAssetLoader<RawAsset, A> oldAssetLoader = (OldAssetLoader<RawAsset, A>) assetLoaders.get(oldAssetType);

            if (oldAssetLoader == null) {
                Log.warn(
                    OldAssetManager.class,
                    "Unable to find asset loader for asset \"{}\" of type {}. Replacing with default asset.",
                    path,
                    oldAssetType
                );

                return;
            }

            String assetPathWithoutAlias = path.substring(pathAlias.length());
            OldAssetInfo<RawAsset, A> oldAssetInfo = oldAssetLoader.load(pathAlias, assetPathWithoutAlias, possiblePathAliases);

            if (oldAssetInfo == null) {
                Log.warn(
                    OldAssetManager.class,
                    "Couldn't find asset of type \"{}\" at path \"{}\", sticking to default asset",
                    oldAssetType,
                    path
                );

                return;
            }

            A asset = (A) assets.get(oldAssetType.getAssetClass().getName() + path);
            asset.setAssetInfo(oldAssetInfo);

            // TODO: fire asset change event
        });
    }

    private OldAsset<?> createAssetInstance(OldAssetType oldAssetType, String path) {
        try {
            return oldAssetType.getAssetClass().getConstructor(String.class).newInstance(path);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
            Log.warn(
                OldAssetManager.class,
                "Unable to create default asset for asset \"{}\" of type {}.",
                path,
                oldAssetType
            );

            return null;
        }
    }

    private boolean hasPathAlias(String path) {
        // TODO: less magic constants

        int aliasIndex = path.indexOf(":///");
        if (aliasIndex == -1) {
            return false;
        }

        String pathAlias = path.substring(0, aliasIndex + 4);
        return knownPathAliases.contains(pathAlias);
    }

    private String getPathAlias(String path) {
        // TODO: less magic constants

        if (!hasPathAlias(path)) {
            return "";
        }

        int aliasIndex = path.indexOf(":///");
        return path.substring(0, aliasIndex + 4);
    }

    @SuppressWarnings("unchecked")
    public <RawAsset, A extends OldAsset<RawAsset>, D extends Drawable> D use(Class<D> drawableClass, OldAssetType oldAssetType, String path) {
        if (!assets.containsKey(oldAssetType.getAssetClass().getName() + path)) {
            load(oldAssetType, path);
        }

        A asset = (A) assets.get(oldAssetType.getAssetClass().getName() + path);
        Function<A, D> drawableCreator = (Function<A, D>) drawableCreators.get(
            oldAssetType.getAssetClass().getName() + drawableClass.getName()
        );

        if (drawableCreator == null) {
            Log.warn(
                OldAssetManager.class,
                "Unable to find drawable creator for class {}, providing default instance instead.",
                drawableClass.getName()
            );

            try {
                return (D) drawableClass.getMethod("createDefaultInstance", OldAsset.class).invoke(null, asset);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Log.warn(
                    OldAssetManager.class,
                    "Unable to find method \"createDefaultInstance\" for class {}, returning null drawable",
                    drawableClass.getName()
                );

                return null;
            }
        }

        return drawableCreator.apply(asset);
    }

    @SuppressWarnings("unchecked")
    public <RawAsset, A extends OldAsset<RawAsset>> A get(OldAssetType oldAssetType, String path) {
        if (!assets.containsKey(oldAssetType.getAssetClass().getName() + path)) {
            load(oldAssetType, path);
        }

        return (A) assets.get(oldAssetType.getAssetClass().getName() + path);
    }
}
