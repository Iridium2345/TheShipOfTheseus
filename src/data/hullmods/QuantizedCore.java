package data.hullmods;

import java.util.Map;
import java.util.HashMap;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class QuantizedCore extends BaseHullMod{

    private static Map<HullSize,Float> range = new HashMap<HullSize,Float>();

    static {
        range.put(HullSize.FRIGATE, 0.15f);
        range.put(HullSize.DESTROYER, 0.2f);
        range.put(HullSize.CRUISER, 0.4f);
        range.put(HullSize.CAPITAL_SHIP, 0.8f);
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyMult(id,1f+range.get(hullSize));
	 	stats.getEnergyWeaponRangeBonus().modifyMult(id,1f+range.get(hullSize));
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        return String.format("%.1f",(float)range.get(hullSize)*100);    
    }
}
