package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.FighterLaunchBayAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.util.IntervalUtil;

public class FighterIntegration extends BaseHullMod{

    private final IntervalUtil interval = new IntervalUtil(1f, 1f);

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getNumFighterBays().modifyFlat(id, 2);
    }
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        interval.advance(amount);
        if (!interval.intervalElapsed()) return;
        if (ship.hasLaunchBays()) for(final FighterLaunchBayAPI bay : ship.getLaunchBaysCopy()){
            if (bay.getWing() == null) continue;
            final FighterWingSpecAPI spec = bay.getWing().getSpec();
            if (bay.getExtraDeploymentLimit() > spec.getNumFighters()) continue;
			final int extraAdd = (int) Math.ceil(spec.getNumFighters() * .5);
            final int maxTotal = spec.getNumFighters() + extraAdd;
			final int actualAdd = maxTotal - bay.getWing().getWingMembers().size();
            bay.setFastReplacements(bay.getFastReplacements() + extraAdd);
            bay.setExtraDeployments(actualAdd);
			bay.setExtraDeploymentLimit(maxTotal);
			bay.setExtraDuration(100000f);
        }
    }

}