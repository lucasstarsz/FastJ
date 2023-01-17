package tech.fastj.asset.old.image.sprite;

import tech.fastj.asset.old.OldAssetLoader;
import tech.fastj.asset.old.OldAssetManager;
import tech.fastj.asset.old.OldAssetType;
import tech.fastj.asset.AssetUtils;
import tech.fastj.asset.old.properties.PropertyOldAsset;
import tech.fastj.engine.FastJEngine;
import tech.fastj.logging.Log;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

public class SpriteOldAssetLoader implements OldAssetLoader<RawSprite, SpriteOldAsset> {
    @Override
    public SpriteOldAssetInfo load(String initialAlias, String path, String[] possibleAliases) {
        URL assetURL = AssetUtils.findAssetURL(path, possibleAliases);

        if (assetURL == null) {
            return null;
        }

        PropertyOldAsset spriteProperties;

//        try {
//            OldAssetManager oldAssetManager = FastJEngine.getAssetManager();
//            oldAssetManager.load(OldAssetType.Properties, initialAlias + path).get();
//
//            spriteProperties = oldAssetManager.get(OldAssetType.Properties, initialAlias + path);
//        } catch (InterruptedException | ExecutionException exception) {
//            Log.warn(
//                "IO Error while trying to load spriteImage properties at path \"{}{}\": \"{}\"",
//                initialAlias,
//                path,
//                exception.getMessage()
//            );
//
//            return null;
//        }

//        try {
//            String spritePath = spriteProperties.getRawAsset().getProperty("sprite.path");
//            URL spriteURL = new URL(spritePath);
//            BufferedImage image = ImageIO.read(spriteURL);
//
//            return new SpriteOldAssetInfo(initialAlias, image);
//        } catch (IOException exception) {
//            Log.warn(
//                "IO Error while trying to load sprite image at path \"{}{}\": \"{}\"",
//                initialAlias,
//                path,
//                exception.getMessage()
//            );
//        }

        return null;
    }
}
