package spriteless;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.game.EventType.UnitCreateEvent;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.UnitType;
import static mindustry.Vars.*;

public class MultiSpawnAbility extends Ability {
    public UnitType[] units;
    public float[] spawnTimes;
    public float spawnX, spawnY;
    public Effect spawnEffect = Fx.spawn;
    public boolean parentizeEffects;

    protected float timer;
    protected int index;

    public MultiSpawnAbility(UnitType[] units, float[] spawnTimes, float spawnX, float spawnY){
        this.units = units;
        this.spawnTimes = spawnTimes;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public MultiSpawnAbility(){
    }

    @Override
    public void update(Unit unit){
        timer += Time.delta * state.rules.unitBuildSpeed(unit.team);

        if(timer >= spawnTimes[index] && Units.canCreate(unit.team, this.units[index])){
            float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX), y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
            spawnEffect.at(x, y, 0f, parentizeEffects ? unit : null);
            Unit u = this.units[index].create(unit.team);
            u.set(x, y);
            u.rotation = unit.rotation;
            Events.fire(new UnitCreateEvent(u, null, unit));
            if(!net.client()){
                u.add();
            }

            timer = 0f;
            index = (index + 1) % units.length;
        }
    }

    @Override
    public void draw(Unit unit){
        if(Units.canCreate(unit.team, this.units[index])){
            Draw.draw(Draw.z(), () -> {
                float x = unit.x + Angles.trnsx(unit.rotation, spawnY, spawnX), y = unit.y + Angles.trnsy(unit.rotation, spawnY, spawnX);
                Drawf.construct(x, y, this.units[index].fullIcon, unit.rotation - 90, timer / spawnTimes[index], 1f, timer);
            });
        }
    }

    @Override
    public String localized(){
        return Core.bundle.get("ability.multispawn");
    }
}