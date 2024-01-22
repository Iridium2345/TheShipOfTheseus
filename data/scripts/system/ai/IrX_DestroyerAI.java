package data.scripts.system.ai;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAIScript;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponSize;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.util.IntervalUtil;

public class IrX_DestroyerAI implements ShipSystemAIScript {

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
	
	private float bestFractionEver = 0f;
	private float sinceLast = 0f;
	
	@SuppressWarnings("unchecked")
	public void advance(float amount, Vector2f missileDangerDir, Vector2f collisionDangerDir, ShipAPI target) {
		tracker.advance(amount);
		
		sinceLast += amount;
		
		if (tracker.intervalElapsed()) {
			if (system.getCooldownRemaining() > 0) return;
			if (system.isOutOfAmmo()) return;
			if (system.isActive()) return;
			
			if (target == null) return;
			
			List weapons = ship.getAllWeapons();
			int OutOfAmmo = 0;
			int UsesAmmo = 0;
			for (int i = 0; i < weapons.size(); i++) {
				WeaponAPI w = (WeaponAPI) (weapons.get(i));
				if(w.usesAmmo()){
					UsesAmmo+=1;
					if(w.getAmmo()==0){
						OutOfAmmo+=1;
					}
				}
			}
			if (OutOfAmmo/UsesAmmo >= 0.5) {
				ship.useSystem();
				sinceLast = 0f;
				return;
			}
		}
	}

}
