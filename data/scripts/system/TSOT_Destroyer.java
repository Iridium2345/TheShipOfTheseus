package data.scripts.system;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseCombatLayeredRenderingPlugin;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.EmpArcEntityAPI;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI.SystemState;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.impl.combat.*;

public class TSOT_Destroyer extends BaseShipSystemScript implements DroneStrikeStatsAIInfoProvider {
	
	public static class DroneMissileScript extends BaseCombatLayeredRenderingPlugin {
		protected ShipAPI drone;
		protected MissileAPI missile;
		protected boolean done;
		public DroneMissileScript(ShipAPI drone, MissileAPI missile) {
			super();
			this.drone = drone;
			this.missile = missile;
			missile.setNoFlameoutOnFizzling(true);
			//missile.setFlightTime(missile.getMaxFlightTime() - 1f);
		}

		@Override
		public void advance(float amount) {
			super.advance(amount);
			
			if (done) return;
			
			CombatEngineAPI engine = Global.getCombatEngine();
			
			missile.setEccmChanceOverride(1f);
			missile.setOwner(drone.getOriginalOwner());
			
			drone.getLocation().set(missile.getLocation());
			drone.getVelocity().set(missile.getVelocity());
			drone.setCollisionClass(CollisionClass.FIGHTER);
			drone.setFacing(missile.getFacing());
			drone.getEngineController().fadeToOtherColor(this, new Color(0,0,0,0), new Color(0,0,0,0), 1f, 1f);

			float dist = Misc.getDistance(missile.getLocation(), missile.getStart());
			float jitterFraction = dist / missile.getMaxRange();
			jitterFraction = Math.max(jitterFraction, missile.getFlightTime() / missile.getMaxFlightTime());
			
			missile.setSpriteAlphaOverride(0f);
			float jitterMax = 1f + 10f * jitterFraction;
			drone.setJitter(this, new Color(255,100,50, (int)(25 + 50 * jitterFraction)), 1f, 10, 1f, jitterMax);

			boolean droneDestroyed = drone.isHulk() || drone.getHitpoints() <= 0;
			if (missile.isFizzling() || (missile.getHitpoints() <= 0 && !missile.didDamage()) || droneDestroyed) {
				drone.getVelocity().set(0, 0);
				missile.getVelocity().set(0, 0);
				
				if (!droneDestroyed) {
					Vector2f damageFrom = new Vector2f(drone.getLocation());
					damageFrom = Misc.getPointWithinRadius(damageFrom, 20);
					engine.applyDamage(drone, damageFrom, 1000000f, DamageType.ENERGY, 0, true, false, drone, false);
				}
				missile.interruptContrail();
				engine.removeEntity(drone);
				engine.removeEntity(missile);
				
				missile.explode();
				
				done = true;
				return;
			}
			if (missile.didDamage()) {
				drone.getVelocity().set(0, 0);
				missile.getVelocity().set(0, 0);
				
				Vector2f damageFrom = new Vector2f(drone.getLocation());
				damageFrom = Misc.getPointWithinRadius(damageFrom, 20);
				engine.applyDamage(drone, damageFrom, 1000000f, DamageType.ENERGY, 0, true, false, drone, false);
				missile.interruptContrail();
				engine.removeEntity(drone);
				engine.removeEntity(missile);
				done = true;
				return;
			}
			
		}

		@Override
		public boolean isExpired() {
			return done;
		}
		
		
	}

	protected String getWeaponId() {
		return "TSOT_Destroyer";
	}
	
	protected WeaponAPI weapon;
	protected boolean fired = false;
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship = null;
		//boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			//player = ship == Global.getCombatEngine().getPlayerShip();
		} else {
			return;
		}
		
		if (weapon == null) {
			weapon = Global.getCombatEngine().createFakeWeapon(ship, getWeaponId());
		}
		ship.setExplosionScale(0.67f);
		ship.setExplosionVelocityOverride(new Vector2f());
		ship.setExplosionFlashColorOverride(new Color(255, 100, 50, 255));
			if (ship.isAlive()&&!fired) {
				ShipAPI target = findTarget(ship);
				convertDrones(ship, target);
				fired=true;
		}
	}
	
	public void convertDrones(ShipAPI ship, final ShipAPI target) {
		CombatEngineAPI engine = Global.getCombatEngine();
		fired = true;
		int num = 0;
		
		MissileAPI missile = (MissileAPI) engine.spawnProjectile(
				ship, weapon, getWeaponId(), 
				new Vector2f(ship.getLocation()), ship.getFacing(), new Vector2f(ship.getVelocity()));
		if (target != null && missile.getAI() instanceof GuidedMissileAI) {
			GuidedMissileAI ai = (GuidedMissileAI) missile.getAI();
			ai.setTarget(target);
		}
		
		missile.setEmpResistance(10000);
				
		float base = missile.getMaxRange();
		float max = getMaxRange(ship);
		missile.setMaxRange(max);
		missile.setMaxFlightTime(missile.getMaxFlightTime() * max/base);
		ship.setExplosionFlashColorOverride(new Color(255, 100, 50, 255));
		engine.addLayeredRenderingPlugin(new DroneMissileScript(ship, missile));
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		// never called
	}
	
	protected ShipAPI forceNextTarget = null;
	protected ShipAPI findTarget(ShipAPI ship) {
		if (getDrones(ship).isEmpty()) {
			return null;
		}
		
		if (forceNextTarget != null && forceNextTarget.isAlive()) {
			return forceNextTarget;
		}
		
		float range = getMaxRange(ship);
		boolean player = ship == Global.getCombatEngine().getPlayerShip();
		ShipAPI target = ship.getShipTarget();
		
		// If not the player:
		// The AI sets forceNextTarget, so if we're here, that target got destroyed in the last frame
		// or it's using a different AI
		// so, find *something* as a failsafe
		
		if (!player) {
			Object test = ship.getAIFlags().getCustom(AIFlags.MANEUVER_TARGET);
			if (test instanceof ShipAPI) {
				target = (ShipAPI) test;
				float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
				float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
				if (dist > range + radSum) target = null;
			}
			if (target == null) {
				target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), HullSize.FRIGATE, range, true);
			}
			return target;
		}
		
		// Player ship
		
		if (target != null) return target; // was set with R, so, respect that
		
		// otherwise, find the nearest thing to the mouse cursor, regardless of if it's in range
		
		target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), HullSize.FIGHTER, Float.MAX_VALUE, true);
		if (target != null && target.isFighter()) {
			ShipAPI nearbyShip = Misc.findClosestShipEnemyOf(ship, target.getLocation(), HullSize.FRIGATE, 100, false);
			if (nearbyShip != null) target = nearbyShip;
		}
		if (target == null) {
			target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), HullSize.FIGHTER, range, true);
		}
		
		return target;
	}

	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}

	@Override
	public String getInfoText(ShipSystemAPI system, ShipAPI ship) {
		float range = getMaxRange(ship);
		ShipAPI target = findTarget(ship);
		if (target == null) {
			if (ship.getMouseTarget() != null) {
				float dist = Misc.getDistance(ship.getLocation(), ship.getMouseTarget());
				float radSum = ship.getCollisionRadius();
				if (dist + radSum > range) {
					return "OUT OF RANGE";
				}
			}
			return "NO TARGET";
		}
		
		float dist = Misc.getDistance(ship.getLocation(), target.getLocation());
		float radSum = ship.getCollisionRadius() + target.getCollisionRadius();
		if (dist > range + radSum) {
			return "OUT OF RANGE";
		}
		
		return "READY";
	}

	
	@Override
	public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
		return ship.isAlive();
	}
	
	public float getMaxRange(ShipAPI ship) {
		if (weapon == null) {
			weapon = Global.getCombatEngine().createFakeWeapon(ship, getWeaponId());
		}
		//return weapon.getRange();
		return ship.getMutableStats().getSystemRangeBonus().computeEffective(weapon.getRange());
	}
	public boolean dronesUsefulAsPD() {
		return true;
	}
	public boolean droneStrikeUsefulVsFighters() {
		return false;
	}
	public float getMissileSpeed() {
		return weapon.getProjectileSpeed();
	}
	public void setForceNextTarget(ShipAPI forceNextTarget) {
		this.forceNextTarget = forceNextTarget;
	}
	public ShipAPI getForceNextTarget() {
		return forceNextTarget;
	}
	public int getMaxDrones(){
		return 1;
	}
	public List<ShipAPI> getDrones(ShipAPI ship){
		List<ShipAPI> drones=new ArrayList();
		drones.add(ship);
		return  drones;
	}
}








