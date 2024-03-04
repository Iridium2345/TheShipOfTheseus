package data.scripts.system;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

public class TSOT_QuantumImpact extends BaseShipSystemScript{
    public static final float MAX_CHARGE = 10000f;
    public static final float HIT_POINT_PER_S=0.02f;
    public IntervalUtil interval = new IntervalUtil(0.033f, 0.033f);
    
    public static final Color F = new Color(0,0,0,255);
    public static final Color C = new Color(255,255,255,255);

    public static final float RANGE = 4000f;

    private float Charged = 0f;
    private boolean launched = false; 

    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		ShipSystemAPI sys = playerShip.getSystem();
		Global.getCombatEngine().maintainStatusForPlayerShip(
            this,
			sys.getSpecAPI().getIconSpriteName(), 
            sys.getDisplayName(),
            String.format("已充能 : %.1f",Charged),
            false);
	}

    protected ShipAPI findTarget(ShipAPI ship) {
		
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
				if (dist > RANGE + radSum) target = null;
			}
			if (target == null) {
				target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), HullSize.FRIGATE, RANGE, true);
			}
			return target;
		}
        if (target != null) return target; // was set with R, so, respect that
		
		// otherwise, find the nearest thing to the mouse cursor, regardless of if it's in range
		
		target = Misc.findClosestShipEnemyOf(ship, ship.getMouseTarget(), HullSize.FIGHTER, Float.MAX_VALUE, true);
		if (target != null && target.isFighter()) {
			ShipAPI nearbyShip = Misc.findClosestShipEnemyOf(ship, target.getLocation(), HullSize.FRIGATE, 100, false);
			if (nearbyShip != null) target = nearbyShip;
		}
		if (target == null) {
			target = Misc.findClosestShipEnemyOf(ship, ship.getLocation(), HullSize.FIGHTER, RANGE, true);
		}
		
		return target;
	}



    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship= null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else return;
        if(!ship.isAlive())return;
        if(player)maintainStatus(ship, state, effectLevel);
        if (engine.isPaused())return;
        ShipAPI target =findTarget(ship);
        if(target==null)return;
        interval.advance(engine.getElapsedInLastFrame());
        if(!interval.intervalElapsed())return;
        if(state == State.ACTIVE){
            if(launched){engine.spawnEmpArc(
                ship,
                ship.getLocation(),
                ship,
                target,
                DamageType.ENERGY,
                Charged,
                Charged,
                RANGE,
                null,
                15f+100f*Charged/MAX_CHARGE,
                F, C);
                return;
            }
            engine.spawnEmpArc(
                ship,
                ship.getLocation(),
                ship,
                target,
                DamageType.ENERGY,
                Charged,
                Charged,
                RANGE,
                null,
                10f+100f*Charged/MAX_CHARGE,
                F, 
                C);
            launched=true;
            Charged=0f;
            return;
        }        
        if(Charged>=MAX_CHARGE)return;
        float ch=ship.getHitpoints()*HIT_POINT_PER_S*interval.getElapsed();
        ship.setHitpoints(ship.getHitpoints()-ch);
        Charged=Math.min(Charged+ch,MAX_CHARGE);
        launched=false;
        return;
    }
}
