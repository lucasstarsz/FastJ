package tech.fastj.asset;

public record AssetBase<RawAsset, A extends Asset<RawAsset>>(String path, String pathAlias, RawAsset rawAsset) {}
