package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.Automated;

public class AutomatedHacked extends Automated{

    public static final float CR = -0.2f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMinCrewMod().modifyMult(id,0);
        stats.getMaxCombatReadiness().modifyFlat(id,CR);

        if (isInPlayerFleet(stats) && !isAutomatedNoPenalty(stats)) {
			stats.getMaxCombatReadiness().modifyFlat(id, CR, "Automated ship penalty");
		}
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        return CR + "";
    }
}
