package spriteless.units;

import mindustry.world.Block;
import spriteless.entities.MimicUnitEntity;

public class MimicUnitType extends BlockUnitType {

    public MimicUnitType(Block block) {
        super(block);
        targetable = false;
        health = 100;
        armor = 10;
        constructor = MimicUnitEntity::new;
        speed = 0.5f;
        rotateSpeed = 1f;
        omniMovement = true;
        crushDamage = 1f;
        drawMinimap = false;
    }

    @Override
    public void load() {
        super.load();
        cellRegion = arc.Core.atlas.find("window-empty");
    }
}
