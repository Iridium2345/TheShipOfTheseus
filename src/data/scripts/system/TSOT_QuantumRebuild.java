package data.scripts.system;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.combat.FluxTrackerAPI;

public class TSOT_QuantumRebuild extends BaseShipSystemScript{

    protected Object STATUSKEY1 = new Object();
	protected Object STATUSKEY2 = new Object();
	protected Object STATUSKEY3 = new Object();
	protected Object STATUSKEY4 = new Object();

    private static final IntervalUtil interval = new IntervalUtil(0.066f, 0.066f);
    
    protected float getRepairRate(){
        return 0.1f;
    }

    private static final float FLUX = 3f;
    private static final float SOFT2HARD = 0.2f;

    private static final float MIN_USEABLE_HITPOINT=0.7f;
    
    private static final float REPAIR_RATE_BONUS = 95f;

    private static final float SHIP_ALPHA_MULT = 0.75f;

    public static final Color JITTER_COLOR = new Color(120,200,255,45);

	public static final Color JITTER_UNDER_COLOR = new Color(90,255,255,70);

    private void jitter(State state,float effectLevel,ShipAPI ship){
        float jitterLevel = effectLevel;
		float jitterRangeBonus = 0;
		float maxRangeBonus = 10f;
		if (state == State.IN) {
			jitterLevel = effectLevel / (1f / ship.getSystem().getChargeUpDur());
			if (jitterLevel > 1) {
				jitterLevel = 1f;
			}
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		} else if (state == State.ACTIVE) {
			jitterLevel = 1f;
			jitterRangeBonus = maxRangeBonus;
		} else if (state == State.OUT) {
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		}
		jitterLevel = (float) Math.sqrt(jitterLevel);
		effectLevel *= effectLevel;
		
		ship.setJitter(this, JITTER_COLOR, jitterLevel, 3, 0, 0 + jitterRangeBonus);
		ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel,2, 8f, 8f);
    }


    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine = Global.getCombatEngine();
        String actualId = id;
        ShipAPI ship= null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            actualId = actualId + "_" + ship.getId();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else return;
        if(player)maintainStatus(ship, state, effectLevel);
        jitter(state,effectLevel,ship);
        if(!ship.isAlive())return;
        
        interval.advance(Global.getCombatEngine().getElapsedInLastFrame());
        if(!interval.intervalElapsed())return;

        if (engine.isPaused())return;
        if (state == State.COOLDOWN || state == State.IDLE) {
            unapply(stats, actualId);
            return;
        }
        
        stats.getCombatEngineRepairTimeMult().modifyMult(id, 1f - REPAIR_RATE_BONUS * 0.01f);
		stats.getCombatWeaponRepairTimeMult().modifyMult(id, 1f - REPAIR_RATE_BONUS * 0.01f);
        
        ship.setExtraAlphaMult(SHIP_ALPHA_MULT);
		ship.setApplyExtraAlphaToEngines(true);

        float increasedHitpoint = interval.getElapsed() * getRepairRate() * (ship.getMaxHitpoints()-ship.getHitpoints());

        ship.setHitpoints(Math.min(
            ship.getHitpoints() + increasedHitpoint,
            ship.getMaxHitpoints()
            ));

        FluxTrackerAPI tracker=ship.getFluxTracker();
        float decreasedFlux=Math.min(
            tracker.getCurrFlux()-tracker.getHardFlux(),
             FLUX * increasedHitpoint
        );
        tracker.decreaseFlux(decreasedFlux);
        tracker.increaseFlux(decreasedFlux*SOFT2HARD, true);
    }
    @Override
    public void unapply(MutableShipStatsAPI stats,String id){
        stats.getCombatEngineRepairTimeMult().unmodify(id);
        stats.getCombatWeaponRepairTimeMult().unmodify(id);
    }
    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return ship.getHitpoints() < ship.getMaxHitpoints()*MIN_USEABLE_HITPOINT;
    }

    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		ShipSystemAPI cloak = playerShip.getPhaseCloak();
		if (cloak == null) cloak = playerShip.getSystem();
		if (cloak == null) return;
		Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "武器和引擎修复时间 -" + (int) REPAIR_RATE_BONUS + "%", false);
		Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY3,
					cloak.getSpecAPI().getIconSpriteName(),
					"重构中", 
					String.format("幅能转换中... 修复速率: %.1f ",
                    getRepairRate() * (playerShip.getMaxHitpoints()-playerShip.getHitpoints())), 
                    true);
	}

    public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}
}
