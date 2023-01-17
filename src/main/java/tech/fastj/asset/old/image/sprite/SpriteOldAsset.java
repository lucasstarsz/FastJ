package tech.fastj.asset.old.image.sprite;

import tech.fastj.animation.sprite.SpriteAnimData;
import tech.fastj.asset.old.OldAsset;
import tech.fastj.asset.old.OldAssetInfo;
import tech.fastj.asset.old.OldAssetManager;
import tech.fastj.asset.old.OldAssetType;
import tech.fastj.engine.FastJEngine;

public class SpriteOldAsset extends OldAsset<RawSprite> {

    private SpriteAnimData spriteAnimData;

    public SpriteOldAsset(String assetPath, String assetPathAlias) {
        super(assetPath, assetPathAlias);
//        OldAssetManager oldAssetManager = FastJEngine.getAssetManager();
//        oldAssetManager.get(OldAssetType.Properties, assetPathAlias + assetPath.substring(0, ));
    }

    public SpriteAnimData getSpriteAnimData() {
        return spriteAnimData;
    }

    public void setSpriteAnimData(SpriteAnimData spriteAnimData) {
        // TODO: fire asset change event
    }

    @Override
    public <A extends OldAsset<RawSprite>, AI extends OldAssetInfo<RawSprite, A>> void setAssetInfo(AI assetInfo) {
        if (assetInfo instanceof SpriteOldAssetInfo spriteAssetInfo) {
            this.spriteAnimData = spriteAssetInfo.spriteAnimData();
        }

        super.setAssetInfo(assetInfo);
    }
}
