package spriteless.blocks;

import mindustry.Vars;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.blocks.units.*;
import spriteless.units.*;

public class UpgradableFactory extends UnitFactory {

    public UpgradableFactory() {
        super("upgradable-factory");
        requirements(Category.units, new ItemStack[]{new ItemStack(Items.silicon, 100)});
        size = 5;
        itemCapacity = 100;
        consumePower(10f);
        for(var unitType : Vars.content.units())
            if(unitType instanceof BlockUnitType but)
                if(!(unitType instanceof MimicUnitType) && WeirdUnitSystem.parent(but.sourceBlock) == null)
                    plans.add(new UnitPlan(but, 60, but.sourceBlock.requirements));
    }

    @Override 
    public void load(){
        super.load();
        for(var plan : plans){
            var b = ((BlockUnitType)plan.unit).sourceBlock;
            plan.time = b.buildCost * b.buildCostMultiplier;
        }
        Utils.transfer(Blocks.multiplicativeReconstructor, this);
    }

    public class FactoryBuild extends UnitFactory.UnitFactoryBuild{

    }
}