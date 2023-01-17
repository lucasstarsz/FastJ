package tech.fastj.asset;

@FunctionalInterface
public interface AssetLoader<RawAsset, A extends Asset<RawAsset>> {
    AssetBase<RawAsset, A> loadAsset(String pathAlias, String path, String... possibleDirectories);
}
