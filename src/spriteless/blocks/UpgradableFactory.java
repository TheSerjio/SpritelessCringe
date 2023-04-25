package spriteless.blocks;

import mindustry.Vars;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.blocks.units.*;
import spriteless.units.*;

public class UpgradableFactory extends UnitFactory {

    public UpgradableFactory() {
        super("upgradable-factory");
        requirements(Category.units, ItemStack.with(Items.silicon, 100, Items.graphite, 100));
        size = 5;
        itemCapacity = 100;
        consumePower(10f);
        for(var unitType : Vars.content.units())
            if(unitType instanceof BlockUnitType but)
                if(!(unitType instanceof MimicUnitType) && !but.sandbox && WeirdUnitSystem.parent(but.sourceBlock) == null)
                    plans.add(new UnitPlan(but, 60, but.cost));
    }

    @Override 
    public void load(){
        super.load();
        for(var plan : plans){
            var b = ((BlockUnitType)plan.unit).sourceBlock;
            plan.time = b.buildCost;
        }
        Utils.transferPayload(Blocks.primeRefabricator, this);
    }

    public class FactoryBuild extends UnitFactory.UnitFactoryBuild{

    }
}