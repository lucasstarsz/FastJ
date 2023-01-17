package tech.fastj.asset.old;

public interface OldAssetInfo<RawAsset, A extends OldAsset<RawAsset>> {
    String pathAlias();

    RawAsset rawAsset();
}
