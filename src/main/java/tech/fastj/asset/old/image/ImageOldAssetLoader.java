package tech.fastj.asset.old.image;

import tech.fastj.asset.old.BaseOldAssetInfo;
import tech.fastj.asset.old.OldAssetLoader;
import tech.fastj.asset.AssetUtils;
import tech.fastj.logging.Log;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageOldAssetLoader implements OldAssetLoader<BufferedImage, ImageOldAsset> {
    @Override
    public BaseOldAssetInfo<BufferedImage, ImageOldAsset> load(String initialAlias, String path, String[] possibleAliases) {
        URL assetURL = AssetUtils.findAssetURL(path, possibleAliases);

        if (assetURL == null) {
            return null;
        }

        try {
            BufferedImage image = ImageIO.read(assetURL);
            return new BaseOldAssetInfo<>(initialAlias, image);
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
