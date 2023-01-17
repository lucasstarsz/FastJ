package tech.fastj.asset;

import tech.fastj.asset.event.AssetChangeEvent;
import tech.fastj.asset.event.AssetChangeType;
import tech.fastj.engine.FastJEngine;
import tech.fastj.gameloop.CoreLoopState;
import tech.fastj.gameloop.event.EventObserver;

@SuppressWarnings("rawtypes")
public abstract class Asset<RawAsset> implements EventObserver<AssetChangeEvent> {

    protected String assetPath;
    protected String assetPathAlias;
    protected RawAsset rawAsset;

    protected Asset(String assetPath, String assetPathAlias) {
        this.assetPath = assetPath;
        this.assetPathAlias = assetPathAlias;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getAssetPathAlias() {
        return assetPathAlias;
    }

    public RawAsset getRawAsset() {
        return rawAsset;
    }

    protected <DAT extends AssetType<DAT>> void addDependency(AssetManager assetManager, DAT dependentAssetType, String path) {
        Asset<?> assetDependency = assetManager.get(dependentAssetType, path);

        FastJEngine.getGameLoop().addEventObserver(
            AssetChangeEvent.class,
            (AssetChangeEvent event) -> assetDependency.assetPath.equals(event.getAsset().assetPath)
                && assetDependency.assetPathAlias.equals(event.getAsset().assetPathAlias),
            this
        );
    }

    public <A extends Asset<RawAsset>, AB extends AssetBase<RawAsset, A>> void updateAsset(AB assetBase) {
        this.assetPath = assetBase.path();
        this.assetPathAlias = assetBase.pathAlias();
        this.rawAsset = assetBase.rawAsset();

        fireAssetModified();
    }

    private void fireAssetModified() {
        AssetChangeEvent<RawAsset, ? extends Asset<RawAsset>> assetModifiedEvent = new AssetChangeEvent<>(this, AssetChangeType.Modified);
        FastJEngine.getGameLoop().fireEvent(assetModifiedEvent, CoreLoopState.EarlyUpdate);
    }

    @Override
    public void eventReceived(AssetChangeEvent event) {
        fireAssetModified();
    }
}
