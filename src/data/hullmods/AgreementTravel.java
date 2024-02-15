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

    public static final float CREW = 30f;
    public static final float FLEET_GROUND_SUPPORT = 10f;

    private float num = 0;
    
    @Override
    protected void increase(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
        num=numFluxVents+numFluxCapacitors;
        stats.getMaxCrewMod().modifyFlat(id,CREW*num);
        stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, FLEET_GROUND_SUPPORT*num);
    }
    
    @Override
    protected void decrease(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
        ShipAPI ship = (stats.getEntity() instanceof ShipAPI)?(ShipAPI)stats.getEntity():null;
        if(ship==null)return;
        MutableStat wp = (ship.getCustomData().get(WeaponPower.KEY) instanceof MutableStat)?(MutableStat)ship.getCustomData().get(WeaponPower.KEY):null;
        if(wp==null)return;
        wp.modifyMult(id,0.5f);
        ship.setCustomData(WeaponPower.KEY, wp);
    }

    @Override
    protected String getDescriptionVents(float numFluxVents) {
        return  String.format("额外船员容量 : %.0f",CREW*num);
    }

    @Override
    protected String getDescriptionCapacitors(float numFluxCapacitors) {
        return  String.format("额外突袭强度 : %.0f",FLEET_GROUND_SUPPORT*num);
    }

    @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.0f", CREW);
		if(index==1)return String.format("%.0f", FLEET_GROUND_SUPPORT);
		return null;
	}
}

