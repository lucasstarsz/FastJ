package tech.fastj.asset.old.image.sprite;

import tech.fastj.graphics.game.Sprite2D;

import java.util.function.Function;

public class Sprite2DCreator implements Function<SpriteOldAsset, Sprite2D> {
    @Override
    public Sprite2D apply(SpriteOldAsset spriteAsset) {
        return Sprite2D.fromSpriteAsset(spriteAsset);
    }
}
