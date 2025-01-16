package data.scripts.system;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.ShipwideAIFlags.AIFlags;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.IntervalUtil;

import data.hullmods.StationSpawn;

public class TSOT_RRI extends BaseShipSystemScript{
    
    protected Object STATUSKEY1 = new Object();

    public static final Color COLOR = new Color(240,240,240,120);
    
    private IntervalUtil interval = new IntervalUtil(0.5f, 0.5f);

    private boolean spawned = false;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship= null;
        CombatEngineAPI engine=Global.getCombatEngine();
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
        } else return;
        if(state.equals(State.IN)){
            if(!spawned){
                spawned=true;
                ship.setCustomData(StationSpawn.NEED_SPAWN, true);
            }
            
            // for(ShipAPI module:station.getChildModulesCopy()){
            //     module.setCollisionClass(CollisionClass.NONE);
            //     module.setCollisionRadius(0);
            //     module.getShield().toggleOff();
            // }
        };
        if(state.equals(State.ACTIVE));
        if(state.equals(State.OUT))spawned=false;
        if(state.equals(State.COOLDOWN));
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return true;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats,String id){
    }
}
