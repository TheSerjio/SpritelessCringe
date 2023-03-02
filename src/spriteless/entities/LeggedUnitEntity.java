package spriteless.entities;

import spriteless.units.WeirdUnitSystem;

public class LeggedUnitEntity extends mindustry.gen.LegsUnit {
    
    @Override
	public int classId() {
		return WeirdUnitSystem.classID(getClass());
	}
}
