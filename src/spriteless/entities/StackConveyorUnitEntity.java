package spriteless.entities;

import mindustry.entities.abilities.*;

public class StackConveyorUnitEntity extends MechUnitEntity {

    @Override
    public void update() {
        super.update();
        for(var a : abilities)
        if(a instanceof ForceFieldAbility q)
        {
            q.rotation = rotation + 45;
            q.regen = 1 - elevation;
        }
        else if(a instanceof RegenAbility q)
            q.amount = 1 - elevation;
    }
}
