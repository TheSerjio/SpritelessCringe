package spriteless.entities;

import mindustry.entities.abilities.ForceFieldAbility;

public class StackConveyorUnitEntity extends MechUnitEntity {

    @Override
    public void update() {
        super.update();
        var a = abilities[0];
        if(a instanceof ForceFieldAbility f)
        {
            f.rotation = rotation + 45;
            f.regen = 1 - elevation;
        }
    }
}
