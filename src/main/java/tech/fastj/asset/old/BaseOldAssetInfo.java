package tech.fastj.asset.old;

public record BaseOldAssetInfo<RawAsset, A extends OldAsset<RawAsset>>(String pathAlias, RawAsset rawAsset) implements OldAssetInfo<RawAsset, A> {}
