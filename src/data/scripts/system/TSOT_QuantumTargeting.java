package data.scripts.system;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class TSOT_QuantumTargeting extends BaseShipSystemScript{

    protected Object STATUSKEY1 = new Object();
	protected Object STATUSKEY2 = new Object();
	protected Object STATUSKEY3 = new Object();
	protected Object STATUSKEY4 = new Object();

    public static final float RANGE_BONUES = 1f;

    public static final Color JITTER_COLOR = new Color(255,200,255,45);

	public static final Color JITTER_UNDER_COLOR = new Color(255,150,255,70);

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
        jitter(state,effectLevel,ship);
        if(!ship.isAlive())return;
        if(player)maintainStatus(ship, state, effectLevel);
        if (engine.isPaused())return;
        if (state == State.COOLDOWN || state == State.IDLE) {
            unapply(stats, actualId);
            return;
        }

        stats.getEnergyWeaponRangeBonus().modifyMult(id, 1f+RANGE_BONUES*effectLevel);
		stats.getBallisticWeaponRangeBonus().modifyMult(id, 1f+RANGE_BONUES*effectLevel);
        
    }

    @Override
    public void unapply(MutableShipStatsAPI stats,String id){
        stats.getEnergyWeaponRangeBonus().unmodify(id);
		stats.getBallisticWeaponRangeBonus().unmodify(id);
    }

    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		ShipSystemAPI sys= playerShip.getSystem();
		Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
					sys.getSpecAPI().getIconSpriteName(), sys.getDisplayName(), "武器射程 +" + (int)(RANGE_BONUES*100) + "%", false);
	}

    public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}
}
