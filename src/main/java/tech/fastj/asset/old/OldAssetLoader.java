package tech.fastj.asset.old;

@FunctionalInterface
public interface OldAssetLoader<RawAsset, A extends OldAsset<RawAsset>> {
    OldAssetInfo<RawAsset, A> load(String initialAlias, String path, String[] possibleAliases);
}
