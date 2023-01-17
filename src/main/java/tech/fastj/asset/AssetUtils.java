package tech.fastj.asset;

import java.io.InputStream;
import java.net.URL;

public class AssetUtils {

    public static URL findAssetURL(String path, String... possibleAliases) {
        for (String possibleAlias : possibleAliases) {
            URL assetURL = ClassLoader.getSystemResource(possibleAlias + path);

            if (assetURL != null) {
                return assetURL;
            }
        }

        return null;
    }

    public static InputStream findAssetStream(String path, String... possibleAliases) {
        for (String possibleAlias : possibleAliases) {
            InputStream assetStream = ClassLoader.getSystemResourceAsStream(possibleAlias + path);

            if (assetStream != null) {
                return assetStream;
            }
        }

        return null;
    }
}
