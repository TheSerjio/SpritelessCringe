package spriteless.blocks;

import mindustry.Vars;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.blocks.units.*;
import spriteless.units.*;

public class Upgrader extends Reconstructor {

    public final int tier;

    public Upgrader(int tier) {
        super("upgrader-" + tier);
        this.tier = tier;
        requirements(Category.units, new ItemStack[]{new ItemStack(Items.silicon, 100)});
        size = 5;
        constructTime = 300f;
        liquidCapacity = 1000;
        consumePower(10f);
        consumeItems(new ItemStack(Items.silicon, 100));
        consumeLiquid(Liquids.water, 1f);
        consumeLiquid(Liquids.slag, 1f);
    }

    @Override 
    public void load(){
        super.load();
        for(var tree : WeirdUnitSystem.trees)
            if(tree.length > tier)
                upgrades.add(new UnitType[]{BlockUnitType.map.get(tree[tier - 1], BlockUnitType.map.get(Blocks.scrapWallGigantic)), BlockUnitType.map.get(tree[tier], BlockUnitType.map.get(Blocks.scrapWall))});
        Utils.transfer(Blocks.multiplicativeReconstructor, this);
    }

    public class UpgraderBuild extends Reconstructor.ReconstructorBuild{

    }
}