package spriteless.blocks;

import mindustry.Vars;
import mindustry.content.*;
import mindustry.type.*;
import mindustry.world.blocks.units.*;
import spriteless.units.*;

public class MimicFactory extends UnitFactory {

    public MimicFactory() {
        super("mimic-factory");
        requirements(Category.units, ItemStack.with(Items.silicon, 100, Items.graphite, 100));
        size = 3;
        itemCapacity = 100;
        consumePower(10f);
        for(var unitType : Vars.content.units())
            if(unitType instanceof MimicUnitType mut)
                plans.add(new UnitPlan(mut, 60, mut.cost){{time = 360;}});
    }

    @Override 
    public void load(){
        super.load();
        Utils.transferPayload(Blocks.additiveReconstructor, this);
    }

    public class FactoryBuild extends UnitFactory.UnitFactoryBuild{

    }
}