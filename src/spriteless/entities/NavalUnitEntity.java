package spriteless.entities;

import spriteless.units.WeirdUnitSystem;

public class NavalUnitEntity extends mindustry.gen.UnitWaterMove {
    
    @Override
	public int classId() {
		return WeirdUnitSystem.classID(getClass());
	}
}
