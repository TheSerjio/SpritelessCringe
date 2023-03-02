package spriteless.entities;

import spriteless.units.WeirdUnitSystem;

public class TankUnitEntity extends mindustry.gen.TankUnit {
    
    @Override
	public int classId() {
		return WeirdUnitSystem.classID(getClass());
	}
}
