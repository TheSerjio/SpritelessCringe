package spriteless.abilities;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.Block;

public class RouterHellAbility extends Ability {
    public Block block;

    private final Shadow[] shadows = new Shadow[100];

    private static final Vec2 vec = new Vec2();

    private static final class Shadow {

        public Shadow() {
            time = arc.math.Mathf.random(60f, 300f);
        }

        public float rot0, rot1, relRot0, relRot1, dist0, dist1, time, progress, x, y, velX, velY;
        public boolean placed;

        public void reset(Unit unit) {
            x = unit.x;
            y = unit.y;
            velX = unit.vel.x;
            velY = unit.vel.y;
            rot0 = arc.math.Mathf.random(360f);
            rot1 = arc.math.Mathf.random(360f);
            relRot0 = arc.math.Mathf.random(360f);
            relRot1 = relRot0 + arc.math.Mathf.random(720f) - arc.math.Mathf.random(720f);
            dist0 = arc.math.Mathf.random(64f);
            dist1 = arc.math.Mathf.random(256f);
            time = arc.math.Mathf.random(60f, 300f);
            progress = 0;
            placed = false;
        }

        public void setPos(){
            var f = progress / time;
            vec.set(0, Mathf.lerp(dist0, dist1, f)).setAngle(Mathf.lerp(relRot0, relRot1, f)).add(x, y).add(velX * progress, velY * progress);
        }
    }

    public RouterHellAbility(Block b) {
        this();
        block = b;
    }

    public RouterHellAbility() {
        for (int i = 0; i < shadows.length; i++)
            shadows[i] = new Shadow();
    }

    @Override
    public void draw(Unit unit) {
        for (var s : shadows) {
            var f = s.progress / s.time;
            var alpha = s.placed ? 1f - f : f;
            s.setPos();
            Drawf.additive(block.region, Color.white, alpha * 2f, vec.x, vec.y, Mathf.lerp(s.rot0, s.rot1, f), Layer.effect);
        }
    }

    @Override
    public void update(Unit unit) {
        main: for (var s : shadows) {
            s.progress += Time.delta;
            if(!s.placed && s.progress * 2 > s.time){
                s.placed = true;
                s.setPos();
                var tile = mindustry.Vars.world.tileWorld(vec.x, vec.y);
                if(tile == null)
                    continue;
                if(!tile.block().alwaysReplace)
                    continue;
                for(int r = 0; r < 4; r++)
                {
                    var n = tile.nearby(r);
                    if(n != null && n.build != null)
                        continue main;
                }
                Call.constructFinish(tile, block, unit, (byte)0, unit.team, null);
            }
            if (s.progress > s.time)
                s.reset(unit);
        }
    }
}