package tech.fastj.asset.event;

import tech.fastj.asset.Asset;
import tech.fastj.gameloop.event.Event;

public class AssetChangeEvent<RawAsset, A extends Asset<RawAsset>> extends Event {

    private final A asset;
    private final AssetChangeType assetChangeType;

    public AssetChangeEvent(A asset, AssetChangeType assetChangeType) {
        this.asset = asset;
        this.assetChangeType = assetChangeType;
    }

    public A getAsset() {
        return asset;
    }

    public AssetChangeType getAssetChangeType() {
        return assetChangeType;
    }
}
