package data.scripts.system;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class TSOT_QuantumTunneling extends BaseShipSystemScript{

    private static final float SHIPTIMEMULT = 5f;

    private static final float MAXSPEED = 1.5f;

    private static final float ACCELERATION = 2f;

    private static final float TURN_RATE = 2f;

    private static final float TURN_ACCELERATION = 2f;

    private static final float SHIP_ALPHA_MULT = 0.5f;

    public static final Color JITTER_COLOR = new Color(50,30,180,30);
	public static final Color JITTER_UNDER_COLOR = new Color(160,120,255,45);

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
		ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 25, 0f, 7f + jitterRangeBonus);
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
        } else {
            return;
        }
        jitter(state,effectLevel,ship);
        if (engine.isPaused()) {
            return;
        }
        ship.setPhased(true);
        
        stats.getMaxSpeed().modifyMult(actualId,MAXSPEED);
		stats.getAcceleration().modifyMult(actualId,ACCELERATION);
        stats.getTurnAcceleration().modifyMult(id,TURN_ACCELERATION);
        stats.getMaxTurnRate().modifyMult(id,TURN_RATE);

        ship.setExtraAlphaMult(SHIP_ALPHA_MULT);
		ship.setApplyExtraAlphaToEngines(true);
        
        stats.getTimeMult().modifyMult(actualId, SHIPTIMEMULT);
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(actualId, 1f / SHIPTIMEMULT);
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(actualId);
		}
        
    }
    @Override
    public void unapply(MutableShipStatsAPI stats,String id){
        ShipAPI ship = null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			id = id + "_" + ship.getId();
		} else {
			return;
		}
        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);
		Global.getCombatEngine().getTimeMult().unmodify(id);
		stats.getMaxSpeed().unmodify(id);
		stats.getAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTimeMult().unmodify(id);
        ship.setExtraAlphaMult(1);
    }
    public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData("提高机动性", false);
		} else if (index == 1) {
			return new StatusData("增加时间流速", false);
		}
		return null;
	}
}
