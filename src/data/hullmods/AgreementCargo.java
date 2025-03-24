package data.hullmods;

import java.util.EnumMap;
import java.util.Map;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public class AgreementCargo extends TSOTAgreement{
    static{
		SubMods.add("TSOT_agreement_cargo");
	}

    private static class CF{
        protected final float cargo;
        protected final float fuel;
        protected CF(float c,float f){
            cargo = c;
            fuel = f;
        }
    }

    public static final Map<HullSize,CF> cf4hullsize = new EnumMap<>(HullSize.class);

    public static final float WP_REDUCE=0.8f;

    static {
        cf4hullsize.put(HullSize.DEFAULT, new CF(0, 0));
        cf4hullsize.put(HullSize.FIGHTER, new CF(0, 0));
        cf4hullsize.put(HullSize.FRIGATE, new CF(30f, 30f));
        cf4hullsize.put(HullSize.DESTROYER, new CF(50f, 50f));
        cf4hullsize.put(HullSize.CRUISER, new CF(100f, 100f));
        cf4hullsize.put(HullSize.CAPITAL_SHIP, new CF(250f, 250f));
    }    

    private float cargo = 0;
    private float fuel = 0;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        super.applyEffectsBeforeShipCreation(hullSize, stats, id);
        cargo = cf4hullsize.get(hullSize).cargo*FluxVents;
        fuel = cf4hullsize.get(hullSize).fuel*FluxCapacitors;
        stats.getCargoMod().modifyFlat(id, cargo);
        stats.getFuelMod().modifyFlat(id, fuel);   
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
        return  String.format("额外货舱容量 : %.0f",cargo);
    }

    @Override
    protected String getDescriptionCapacitors(float numFluxCapacitors) {
        return  String.format("额外燃料容量 : %.0f",fuel);
    }

    @Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.0f", cf4hullsize.get(hullSize).cargo);
		if(index==1)return String.format("%.0f", cf4hullsize.get(hullSize).fuel);
        if(index==2)return String.format("%.1f", WP_REDUCE);
		return null;
	}
}
