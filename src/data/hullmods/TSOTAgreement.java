package data.hullmods;

import java.awt.Color;

import java.util.Set;
import java.util.HashSet;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public abstract class TSOTAgreement extends BaseHullMod {

    private static Color color = new Color(20,240,100);

    protected abstract void increase(MutableShipStatsAPI stats,String id,float numFluxVents,float numFluxCapacitors);

    protected abstract void decrease(MutableShipStatsAPI stats,String id,float numFluxVents,float numFluxCapacitors);

    protected abstract String getDescriptionVents(float numFluxVents);

    protected abstract String getDescriptionCapacitors(float numFluxCapacitors);

    public static Set<String> SubMods = new HashSet<String>();

	protected float FluxVents;
	protected float FluxCapacitors;

	@Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (ship == null)return;
		float FluxVents=(float)ship.getVariant().getNumFluxVents();
		float FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
		if(isSMod(ship)) {
			FluxVents += 15;
			FluxCapacitors += 15;
		}
		tooltip.addPara(getDescriptionVents(FluxVents),color,10f);
		tooltip.addPara(getDescriptionCapacitors(FluxCapacitors),color,10f);
	}

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		if(stats.getEntity() instanceof ShipAPI){
			ShipAPI ship=(ShipAPI)stats.getEntity();
			FluxVents=(float)ship.getVariant().getNumFluxVents();
			FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
			increase(stats,id,FluxVents,FluxCapacitors);
		}else{
			return;
		}
        //decrease(stats,id+"de",FluxVents,FluxCapacitors);
    }
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (!ship.getVariant().hasHullMod("TSOT_quantized_hull"))return false;
        for(String id:ship.getVariant().getHullMods()){
            if(SubMods.contains(id)&&id!=this.spec.getId())return false;
        }
        return true;
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if(ship.getVariant().hasHullMod(this.spec.getId()))return null;
		if (!ship.getVariant().hasHullMod("TSOT_quantized_hull"))return "只能被安装在量子化的舰体上";
		for(String id:ship.getVariant().getHullMods()){
            if(SubMods.contains(id)&&id!=this.spec.getId())return "协议互斥";
        }
        return null;
	}

}
