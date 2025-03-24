package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

public class AutomatedManager extends BaseHullMod{
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (!isSMod(ship)) return; 
        ship.getVariant().addPermaMod("automated", true);
    }
}
