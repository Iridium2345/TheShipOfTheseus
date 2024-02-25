package data.hullmods;

import java.util.Map;
import java.util.HashMap;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class QuantizedCore extends BaseHullMod{

    private static Map<HullSize,Float> range = new HashMap<HullSize,Float>();

    private static final float PD_range = 0.2f;

    static {
        range.put(HullSize.FRIGATE, 0.15f);
        range.put(HullSize.DESTROYER, 0.2f);
        range.put(HullSize.CRUISER, 0.4f);
        range.put(HullSize.CAPITAL_SHIP, 0.6f);
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyMult(id,1f+range.get(hullSize));
	 	stats.getEnergyWeaponRangeBonus().modifyMult(id,1f+range.get(hullSize));
        stats.getNonBeamPDWeaponRangeBonus().modifyMult(id,1f+PD_range);
        stats.getBeamPDWeaponRangeBonus().modifyMult(id,1f+PD_range);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if(index==0)return String.format("%.1f",(float)range.get(hullSize)*100);
        if(index==1)return String.format("%.1f",(float)(range.get(hullSize)+PD_range)*100);
        return null;
    }
}
