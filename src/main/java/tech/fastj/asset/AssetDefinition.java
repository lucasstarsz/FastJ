package tech.fastj.asset;

public record AssetDefinition<AT extends AssetType<?>>(AssetType<AT> assetType, String path) {}