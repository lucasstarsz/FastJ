package tech.fastj.asset;

public record AssetUserDefinition<AT extends AssetType<AT>, T>(AT assetType, Class<T> returnedClass) {}
