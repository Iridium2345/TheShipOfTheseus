package data.scripts.system.ai;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.util.IntervalUtil;

public class TSOT_RebuildAI implements ShipSystemAIScript {

	private ShipAPI ship;
	private ShipwideAIFlags flags;
	private ShipSystemAPI system;
	private IntervalUtil tracker = new IntervalUtil(0.5f, 1f);
	
	public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
		this.ship = ship;
		this.flags = flags;
		this.system = system;
	}
	
	public void TTK_YZMTTK(){
		if (flags == null) return;
		flags.setFlag(AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING,1f);
	}

	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		tracker.advance(amount);
		if (system == null) return;
		if (tracker.intervalElapsed()) {
			if (system.isActive()){
				TTK_YZMTTK();
				return;
			}
			if (system.getCooldownRemaining() > 0) return;
			if (system.isOutOfAmmo()) return;
			else{
				TTK_YZMTTK();
			}
			if (ship.getHitpoints()/ship.getMaxHitpoints() <= 0.3f) {
				system.forceState(ShipSystemAPI.SystemState.ACTIVE,system.getChargeActiveDur());
				system.setAmmo(system.getAmmo()-1);
				return;
			}
		}
	}

}
