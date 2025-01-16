package data.hullmods;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.thoughtworks.xstream.mapper.Mapper.Null;
import com.fs.starfarer.api.impl.campaign.ids.Stats;


public class StationSpawn extends BaseHullMod{

    public static final String VARIANTS="remnant_station2_Damaged";

    public static final String SPAWNED="TSOT_SPAWNED";

    public static final String NEED_SPAWN="TSOT_NEED_SPAWN";

    public static final String SPAWNED_STATION="TSOT_SPAWNED_STATION";

    private Boolean inited=false;

    public static ShipAPI spawnShip(ShipAPI ship){
        CombatEngineAPI engine=Global.getCombatEngine();
        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, VARIANTS);
        member.getCrewComposition().addCrew(member.getNeededCrew());
        return engine.getFleetManager(ship.getOwner()).spawnFleetMember(member,ship.getMouseTarget() , 0, 0);
    }

    @Override
	public void advanceInCombat(ShipAPI ship, float amount) {
        if(!inited){
            ship.setCustomData(SPAWNED, false);
            ship.setCustomData(NEED_SPAWN, false);
            ship.setCustomData(SPAWNED_STATION, null);
            inited=true;
        }
        Boolean need_spawn = (Boolean)ship.getCustomData().get(NEED_SPAWN);
        if(need_spawn!=null&&need_spawn){
            ship.setCustomData(SPAWNED_STATION, spawnShip(ship));
            ship.setCustomData(SPAWNED, true);
            ship.setCustomData(NEED_SPAWN, false);
        }
	}
}
