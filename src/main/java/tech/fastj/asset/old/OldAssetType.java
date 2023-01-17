package tech.fastj.asset.old;

import tech.fastj.asset.old.image.ImageOldAsset;
import tech.fastj.asset.old.image.sprite.SpriteOldAsset;
import tech.fastj.asset.old.properties.PropertyOldAsset;

public enum OldAssetType {
    Image(ImageOldAsset.class),
    Sprite(SpriteOldAsset.class),
    Audio(null),
    Mesh(null),
    Properties(PropertyOldAsset.class);

    private final Class<? extends OldAsset<?>> assetClass;

    OldAssetType(Class<? extends OldAsset<?>> assetClass) {
        this.assetClass = assetClass;
    }

    public Class<? extends OldAsset<?>> getAssetClass() {
        return assetClass;
    }
}
