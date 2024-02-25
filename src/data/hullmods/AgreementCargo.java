package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AgreementCargo extends TSOTAgreement{
    static{
		SubMods.add("TSOT_agreement_cargo");
	}

    public static final float CARGO = 30f;
    public static final float FUEL = 35f;
    public static final float WP_REDUCE=0.8f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        super.applyEffectsBeforeShipCreation(hullSize, stats, id);
        stats.getCargoMod().modifyFlat(id, CARGO*FluxVents);
        stats.getFuelMod().modifyFlat(id, FUEL*FluxCapacitors);
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
        return  String.format("额外货舱容量 : %.0f",CARGO*numFluxVents);
    }

    @Override
    protected String getDescriptionCapacitors(float numFluxCapacitors) {
        return  String.format("额外燃料容量 : %.0f",FUEL*numFluxCapacitors);
    }

    @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.0f", CARGO);
		if(index==1)return String.format("%.0f", FUEL);
        if(index==2)return String.format("%.1f", WP_REDUCE);
		return null;
	}
}
