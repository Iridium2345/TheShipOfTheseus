package data.scripts.system;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class TSOT_QuantumRaid extends BaseShipSystemScript{
    
    protected Object STATUSKEY1 = new Object();

    public static final Color COLOR = new Color(240,240,240,120);

    public List<ShipAPI> getWings(ShipAPI ship){
        final List<ShipAPI> result = new ArrayList<ShipAPI>();
        for(FighterWingAPI wing :ship.getAllWings()){
            result.addAll(wing.getWingMembers());
        }
        return result;
    }

    private boolean done = false;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship= null;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else return;
        switch (state) {
            case IN:{
                done = false;
                break;
            }
            case ACTIVE:{
                if (done) break;
                for(final ShipAPI wing : getWings(ship)){
                    wing.getLocation().set(ship.getMouseTarget());
                }
                done = true;
                break;
            }
            default:
                break;
        }
        
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return true;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats,String id){
    }
}
