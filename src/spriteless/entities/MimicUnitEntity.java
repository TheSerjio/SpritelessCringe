package spriteless.entities;

import arc.math.geom.Vec2;
import spriteless.units.WeirdUnitSystem;

public class MimicUnitEntity extends mindustry.gen.TankUnit {

	private static final Vec2 tmp = new Vec2();

	@Override
	public int classId() {
		return WeirdUnitSystem.classID(getClass());
	}

	@Override
	public void update() {
		super.update();
		lookAt(90);
		if (vel.isZero())
		{
			var tile = this.tileOn();
			var x = tile.worldx();
			var y = tile.worldy();
			tmp.set(x, y).sub(this.x, this.y).scl(0.125f);
			vel.add(tmp);
		}
	}
}
