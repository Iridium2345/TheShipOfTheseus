package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;

public class InfStation extends BaseHullMod{

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getSuppliesToRecover().modifyMult(id, 0f);
        stats.getMaxTurnRate().modifyMult(id, .3f);
    };

    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship.hasListenerOfClass(InfStationT.class)) return;
        ship.addListener(new InfStationT(ship));
    };

    public class InfStationT implements AdvanceableListener{
        private final ShipAPI ship;
        private boolean deploy = false;
        private InfStationT(ShipAPI ship){
            this.ship = ship;
        };

        public void advance(float amount) {
            ship.giveCommand(ShipCommand.ACCELERATE, null , 0);
            ship.giveCommand(ShipCommand.TURN_LEFT, null , 0);
            if (deploy) return;
            deploy = true;
            ship.getLocation().set(350.0f,0.0f);
            ship.setStation(false);
        };
    }
}
