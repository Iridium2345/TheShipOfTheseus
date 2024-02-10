package data.scripts.system.ai;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;

public class TSOT_RebuildAI implements ShipSystemAIScript {

	private ShipAPI ship;
	private CombatEngineAPI engine;
	private ShipwideAIFlags flags;
	private ShipSystemAPI system;
	
	private IntervalUtil tracker = new IntervalUtil(0.5f, 1f);
	
	public void init(ShipAPI ship, ShipSystemAPI system, ShipwideAIFlags flags, CombatEngineAPI engine) {
		this.ship = ship;
		this.flags = flags;
		this.engine = engine;
		this.system = system;
	}
	
	public void TTK_YZMTTK(){
		if (flags == null) return;
		flags.setFlag(AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING,1f);
		if(engine.getPlayerShip().equals(ship)){
			engine.maintainStatusForPlayerShip(this,
					"graphics/icons/hullsys/flare_launcher.png","TTK","战斗 爽", false);
		}
	}

	@SuppressWarnings("unchecked")
	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		tracker.advance(amount);
		
		if (tracker.intervalElapsed()) {
			ShipSystemAPI cloak = ship.getPhaseCloak();
			if (cloak == null) cloak = ship.getSystem();
			if (cloak == null) return;
			if (cloak.isActive()){
				TTK_YZMTTK();
				return;
			}
			if (cloak.getCooldownRemaining() > 0) return;
			if (cloak.isOutOfAmmo()) return;
			else{
				TTK_YZMTTK();
			}
			if (ship.getHitpoints()/ship.getMaxHitpoints() <= 0.45f) {
				cloak.forceState(ShipSystemAPI.SystemState.ACTIVE,cloak.getChargeActiveDur());
				cloak.setAmmo(cloak.getAmmo()-1);
				return;
			}
		}
	}

}
