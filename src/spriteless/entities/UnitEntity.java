package spriteless.entities;

import spriteless.units.WeirdUnitSystem;

public class UnitEntity extends mindustry.gen.UnitEntity {
    
    @Override
	public int classId() {
		return WeirdUnitSystem.classID(getClass());
	}
}
