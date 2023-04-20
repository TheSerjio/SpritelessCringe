package spriteless;

import arc.util.Log;
import mindustry.content.*;
import mindustry.mod.*;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import spriteless.blocks.*;
import spriteless.units.*;

public class SpritelessCringeMod extends Mod{

    public SpritelessCringeMod(){

    }

    @Override
    public void loadContent(){
        Log.info("load content");
        WeirdUnitSystem.setupID();
        spriteless.units.BlockToUnit.init();
        new MimicFactory();
        new UpgradableFactory();
        new Upgrader(Blocks.multiplicativeReconstructor, 1){{
            consumeLiquid(Liquids.water, 1f / 60f);
            consumeLiquid(Liquids.slag, 1f / 60f);
            requirements(Category.units, ItemStack.with(Items.silicon, 100, Items.graphite, 100));
            consumePower(1f);
        }};
        new Upgrader(Blocks.exponentialReconstructor, 3){{
            consumeLiquid(Liquids.water, 10f / 60f);
            consumeLiquid(Liquids.slag, 10f / 60f);
            requirements(Category.units, ItemStack.with(Items.silicon, 500, Items.graphite, 500, Items.thorium, 100));
            consumePower(10f);
        }};
        new Upgrader(Blocks.tetrativeReconstructor, 10){{
            consumeLiquid(Liquids.water, 60f / 60f);
            consumeLiquid(Liquids.slag, 60f / 60f);
            requirements(Category.units, ItemStack.with(Items.silicon, 1000, Items.graphite, 1000, Items.thorium, 500, Items.surgeAlloy, 100, Items.phaseFabric, 100));
            consumePower(100f);
        }};
    }

    @Override
    public void init() {
        Log.info("init");
    }
}