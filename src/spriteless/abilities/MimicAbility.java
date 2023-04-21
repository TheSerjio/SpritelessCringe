package spriteless.abilities;

import arc.math.geom.Vec2;
import mindustry.entities.abilities.EnergyFieldAbility;
import mindustry.gen.Unit;

public class MimicAbility extends EnergyFieldAbility {

    public MimicAbility() {
        super(10f, 60f, 16f);
        statusDuration = 60f;
        healPercent = 1f;
        maxTargets = 2;
        sectors = 3;
        range = 16f;
    }
    
    @Override
    public void draw(Unit unit){
        if(unit.isShooting)
            super.draw(unit);
    }

    @Override
    public void update(Unit unit){
        unit.lookAt(90);
        if(unit.isShooting){
            super.update(unit); 
            if(!unit.vel.isZero())
                super.update(unit);
            return;
        }
        if(!unit.vel.isZero())
            return;
        var tile = unit.tileOn();
        var x = tile.worldx() - unit.x;
        var y = tile.worldy() - unit.y;
        unit.x += x * 0.1f;
        unit.y += y * 0.1f;
    }
}