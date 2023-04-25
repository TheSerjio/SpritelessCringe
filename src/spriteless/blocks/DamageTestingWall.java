package spriteless.blocks;

import arc.math.WindowedMean;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.ui.Bar;
import mindustry.graphics.Pal;
import mindustry.world.meta.*;

public class DamageTestingWall extends Block {

    public DamageTestingWall(String name, int size) {
        super(name);
        solid = true;
        update = true;
        destructible = true;
        group = BlockGroup.walls;
        category = mindustry.type.Category.defense;
        canOverdrive = false;
        drawDisabled = false;
        buildVisibility = BuildVisibility.sandboxOnly;
        this.size = size;
        health = 100000;
    }

    @Override
    public void load() {
        super.load();
        Utils.transfer(new Block[]{Blocks.scrapWall, Blocks.scrapWallLarge,Blocks.scrapWallHuge,Blocks.scrapWallGigantic}[size - 1], this);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("DPS", (DamageTestingWallBuild dtwb) -> new Bar(() -> (int)dtwb.m1.mean() + " ~ " + (int)dtwb.m2.mean() + " ~ " + (int)dtwb.m3.mean(), () -> Pal.accent, () -> 0f));
    }

    public class DamageTestingWallBuild extends Building {
        public WindowedMean m1 = new WindowedMean(300);
        public WindowedMean m2 = new WindowedMean(300);
        public WindowedMean m3 = new WindowedMean(300);

        public DamageTestingWallBuild(){
            m1.fill(0);
            m2.fill(0);
            m3.fill(0);
        }

        @Override
        public void update() {
            super.update();
            m1.add((maxHealth - health) * 60);
            m2.add(m1.mean());
            m3.add(m2.mean());
            health = maxHealth;
        }
    }
}