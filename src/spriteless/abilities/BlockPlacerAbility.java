package spriteless.abilities;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.world.*;

public class BlockPlacerAbility extends Ability {
    public Block block;
    public float reload;

    protected float timer;

    private static final Seq<Point2> vecs = Seq.with(
        new Point2(-1, 1),  new Point2(0,  1), new Point2(1,  1), 
        new Point2(-1, 0),                     new Point2(1,  0),
        new Point2(-1, -1), new Point2(0, -1), new Point2(1, -1));

    public BlockPlacerAbility(){
        
    }

    public BlockPlacerAbility(Block b, int reload){
        block = b;
        this.reload = reload;
        if(b.size > 3)
            arc.util.Log.info(b.name + " is too large for BlockPlacerAbility");
    }

    boolean tryPlace(Tile tile, Unit unit){
        if(tile == null)
            return false;
        if(!tile.block().alwaysReplace)
        //TODO checking if large block can fit here
            return false;
        switch(block.size){
            case 1:
                break;
            case 2:
            case 3:
                for(var vec : vecs){
                    var t = tile.nearby(vec.x, vec.y);
                    if(t == null)
                        return false;
                    if(!t.block().alwaysReplace)
                        return false;
                }
                break;
        }
        
        Call.constructFinish(tile, block, unit, (byte)0, unit.team, null);
        timer = 0f;
        return true;
    }

    @Override
    public void update(Unit unit){
        timer += Time.delta * mindustry.Vars.state.rules.buildSpeed(unit.team);
        if(timer >= reload){
            timer *= arc.math.Mathf.random();
            var tile = unit.tileOn();
            if(tryPlace(tile, unit))
                return;
            vecs.shuffle();
            for(var vec : vecs)
                if(tryPlace(mindustry.Vars.world.tileWorld(unit.x + vec.x * 8, unit.y + vec.y * 8), unit))
                    return;
        }
    }

    @Override
    public void draw(Unit unit){
        Draw.draw(Draw.z(), () -> {
            Drawf.construct(unit.x, unit.y, block.fullIcon, unit.rotation - timer * 90 / reload, timer / reload, 1f, timer);
        });
    }

    @Override
    public String localized(){
        return Core.bundle.format("ability.blockspawn", block.localizedName, reload / 60);
    }
}