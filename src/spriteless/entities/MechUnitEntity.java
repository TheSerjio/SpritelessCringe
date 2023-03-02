package spriteless.entities;

import spriteless.units.WeirdUnitSystem;

public class MechUnitEntity extends mindustry.gen.MechUnit {
    
    @Override
	public int classId() {
		return WeirdUnitSystem.classID(getClass());
	}
}
