package data.scripts.system.ai;

import java.util.Optional;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class TSOT_ttkAI implements ShipSystemAIScript {

	private ShipAPI ship;
	private ShipSystemAPI system;

	private IntervalUtil tracker = new IntervalUtil(3f, 3f);
	
	public static final float BASE_RANGE = 550f;

	public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
		this.ship = ship;
		this.system = system;
	}

	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		tracker.advance(amount);
		if (tracker.intervalElapsed()) {
			final var s = Optional.ofNullable(Misc.findClosestShipEnemyOf(
				ship,
				ship.getLocation(),
				HullSize.FIGHTER,
				BASE_RANGE,
				true
			));
			if (s.isPresent())
				ship.useSystem();
			else
				system.deactivate();
		}
	}

}
