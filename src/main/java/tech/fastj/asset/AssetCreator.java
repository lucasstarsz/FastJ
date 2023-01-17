package tech.fastj.asset;

public interface AssetCreator<RawAsset, A extends Asset<RawAsset>> {
    A createFromPath(AssetManager assetManager, String pathAlias, String path);
}
