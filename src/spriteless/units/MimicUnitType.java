package spriteless.units;

import arc.math.geom.Vec2;
import mindustry.content.*;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.environment.*;
import spriteless.entities.TankUnitEntity;
import mindustry.entities.bullet.*;

public class MimicUnitType extends BlockUnitType {

    private static final Vec2 tmp = new Vec2();

    public MimicUnitType(Block block) {
        super(block);
        targetable = false;
        constructor = TankUnitEntity::new;
        speed = 0.5f;
        rotateSpeed = 6f;
        omniMovement = true;
        drawMinimap = false;
        faceTarget = false;
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
        final var aS = attackStatus;
        weapons.add(new Weapon(){{
            reload = 5f;
            inaccuracy = 180;
            shootCone = 360;
            x = y = shootX = shootY = 0;
            mirror = false;
            bullet = new LightningBulletType(){{
                lightningColor = hitColor = block.mapColor.cpy();
                damage = reload;
                lightningLength = 4;
                lightningLengthRand = 4;

                lightningType = new BulletType(0.0001f, damage){{
                    lifetime = Fx.lightning.lifetime;
                    hitEffect = Fx.hitLancer;
                    despawnEffect = Fx.none;
                    hittable = false;
                    status = aS;
                    statusDuration = 30f;
                }};
            }};
        }});
    }

    @Override
    public void load() {
        super.load();
        cellRegion = arc.Core.atlas.find("window-empty");
    }

    @Override
    public boolean targetable(Unit unit, Team targeter) {
        return targeter == unit.team || unit.isShooting || !unit.vel.isZero();
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);
        unit.lookAt(90);
        if(unit.isShooting || !unit.vel.isZero())
            return;
        var tile = unit.tileOn();
        var x = tile.worldx();
        var y = tile.worldy();
        tmp.set(x, y).sub(unit.x, unit.y).scl(0.125f);
        unit.vel.add(tmp);
    }
}