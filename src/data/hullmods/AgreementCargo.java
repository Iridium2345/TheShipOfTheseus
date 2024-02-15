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
    public static final float FUEL = 30f;

    private float num = 0;

    @Override
    protected void increase(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
        num=numFluxVents+numFluxCapacitors;
        stats.getCargoMod().modifyFlat(id,CARGO*num);
        stats.getFuelMod().modifyFlat(id,FUEL*num);
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
        return  String.format("额外货舱容量 : %.0f",CARGO*num);
    }

    @Override
    protected String getDescriptionCapacitors(float numFluxCapacitors) {
        return  String.format("额外燃料容量 : %.0f",FUEL*num);
    }

    @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", CARGO);
		if(index==1)return String.format("%.1f", FUEL);
		return null;
	}
}
