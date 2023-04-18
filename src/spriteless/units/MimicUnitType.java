package spriteless.units;

import mindustry.content.*;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
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
        drawMinimap = false;
        engineOffset = 0;
        if (block instanceof Floor f) {
            if (block instanceof SteamVent)
                hitSize = 24;
            if (f.status != StatusEffects.none)
                immunities.add(f.status);
            if (f.liquidDrop != null)
                canDrown = false;
        }
        crushDamage = 64 / hitSize / hitSize;
    }

    @Override
    public void load() {
        super.load();
        cellRegion = arc.Core.atlas.find("window-empty");
    }
}
