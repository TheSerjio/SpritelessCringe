package spriteless.units;

import arc.math.geom.Vec2;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import spriteless.abilities.MimicAbility;
import spriteless.entities.TankUnitEntity;

public class MimicUnitType extends BlockUnitType {

    public MimicUnitType(Block block) {
        super(block);
        targetable = false;
        constructor = TankUnitEntity::new;
        speed = 0.5f;
        rotateSpeed = 6f;
        omniMovement = true;
        drawMinimap = false;
        faceTarget = false;
        drag = 0.05f;
        if (block instanceof TreeBlock){
            hitSize = 64;
            health = 1000;
            cost = ItemStack.with(Items.sporePod, 1000);
        }
        else if (block instanceof SteamVent || block instanceof TallBlock){
            hitSize = 24;
            health = 500;
            cost = ItemStack.with(Items.silicon, 100, Items.graphite, 100, Items.thorium, 100);
        }
        else{
            hitSize = 8f;
            health = 100;
            cost = ItemStack.with(Items.silicon, 100, Items.graphite, 100);
        }
        var attackStatus = StatusEffects.shocked;
        if (block instanceof Floor f) {
            if (f.status != StatusEffects.none)
            {
                attackStatus = f.status;
                immunities.add(f.status);
            }
            if (f.liquidDrop != null)
                canDrown = false;
        }
        var aS = attackStatus;// java moment
        abilities.add(new MimicAbility(){{
            status = aS;
            color = block.mapColor;
        }});
    }

    @Override
    public void load() {
        super.load();
        cellRegion = arc.Core.atlas.find("window-empty");
    }

    // thanks Anuke for this
    @Override
    public boolean targetable(Unit unit, Team targeter) {
        return targeter == unit.team || unit.isShooting || !unit.vel.isZero();
    }
}