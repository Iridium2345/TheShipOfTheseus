package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class AgreementTravel extends TSOTAgreement{
    static{
		SubMods.add("TSOT_agreement_travel");
	}

    public static final float CREW = 40f;
    public static final float FLEET_GROUND_SUPPORT = 20f;
    public static final float WP_REDUCE=0.8f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        super.applyEffectsBeforeShipCreation(hullSize, stats, id);
        stats.getMaxCrewMod().modifyFlat(id,(float)(CREW*FluxVents));
        stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, (float)FLEET_GROUND_SUPPORT*FluxCapacitors);
    }

    @Override
    protected void increase(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
        
    }
    
    @Override
    protected void decrease(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
        ShipAPI ship = (stats.getEntity() instanceof ShipAPI)?(ShipAPI)stats.getEntity():null;
        if(ship==null)return;
        MutableStat wp = (ship.getCustomData().get(WeaponPower.KEY) instanceof MutableStat)?(MutableStat)ship.getCustomData().get(WeaponPower.KEY):null;
        if(wp==null)return;
        wp.modifyMult(id,1f-WP_REDUCE);
        ship.setCustomData(WeaponPower.KEY, wp);
    }

    @Override
    protected String getDescriptionVents(float numFluxVents) {
        return  String.format("额外船员容量 : %.0f",CREW*numFluxVents);
    }

    @Override
    protected String getDescriptionCapacitors(float numFluxCapacitors) {
        return  String.format("额外突袭强度 : %.0f",FLEET_GROUND_SUPPORT*numFluxCapacitors);
    }

    @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.0f", CREW);
		if(index==1)return String.format("%.0f", FLEET_GROUND_SUPPORT);
        if(index==2)return String.format("%.1f", WP_REDUCE);
		return null;
	}
}

