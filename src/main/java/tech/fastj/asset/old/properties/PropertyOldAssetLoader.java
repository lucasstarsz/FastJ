package tech.fastj.asset.old.properties;

import tech.fastj.asset.old.BaseOldAssetInfo;
import tech.fastj.asset.old.OldAssetLoader;
import tech.fastj.asset.AssetUtils;
import tech.fastj.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyOldAssetLoader implements OldAssetLoader<Properties, PropertyOldAsset> {
    @Override
    public BaseOldAssetInfo<Properties, PropertyOldAsset> load(String initialAlias, String path, String[] possibleAliases) {
        try (InputStream propertyAssetStream = AssetUtils.findAssetStream(path, possibleAliases)) {
            if (propertyAssetStream == null) {
                return null;
            }

            Properties properties = new Properties();
            properties.load(propertyAssetStream);

            return new BaseOldAssetInfo<>(initialAlias, properties);
        } catch (IOException exception) {
            Log.warn(
                "IO Error while trying to load image at path \"{}{}\": \"{}\"",
                initialAlias,
                path,
                exception.getMessage()
            );
        }

        return null;
    }
}
