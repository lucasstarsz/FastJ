package tech.fastj.asset;

@FunctionalInterface
public interface AssetUser<RawAsset, A extends Asset<RawAsset>, T> {
    T createFromAsset(AssetManager assetManager, A asset);
}
