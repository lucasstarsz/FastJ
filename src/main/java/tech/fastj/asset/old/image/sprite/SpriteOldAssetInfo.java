package tech.fastj.asset.old.image.sprite;

import tech.fastj.animation.sprite.SpriteAnimData;
import tech.fastj.asset.old.OldAssetInfo;

public record SpriteOldAssetInfo(String pathAlias, RawSprite rawAsset, SpriteAnimData spriteAnimData)
    implements OldAssetInfo<RawSprite, SpriteOldAsset> {
}
