package tech.fastj.asset.sprite;

import tech.fastj.asset.Asset;
import tech.fastj.asset.AssetBase;

import java.awt.image.BufferedImage;

public class SpriteAsset extends Asset<BufferedImage> {

    public SpriteAsset(String assetPath, String assetPathAlias) {
        super(assetPath, assetPathAlias);
    }

    @Override
    public <A extends Asset<BufferedImage>, AB extends AssetBase<BufferedImage, A>> void updateAsset(AB assetBase) {
        super.updateAsset(assetBase);
    }
}
