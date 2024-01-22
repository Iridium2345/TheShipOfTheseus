package data.hullmods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class AgreementBeginner extends BaseHullMod {
	
	private static final float BONUS_V=0.02f;
	private static final float BONUS_C=0.02f;
	private static final Color GREEN = new Color(68, 183, 188, 255);
	
	@Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float FluxVents=(float)ship.getVariant().getNumFluxVents();
		float FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
		if (ship != null) {
			tooltip.addPara(String.format("幅能容量: %.1f", BONUS_C*FluxCapacitors*100),GREEN,10f);
			tooltip.addPara(String.format("幅能耗散: %.1f", BONUS_V*FluxVents*100)+"%",GREEN,10f);
		} 
	}

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float FluxVents;
		float FluxCapacitors;
		if(stats.getEntity() instanceof ShipAPI){
			ShipAPI ship=(ShipAPI)stats.getEntity();
			FluxVents=(float)ship.getVariant().getNumFluxVents();
			FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
		}else{
			return;
		}
		stats.getFluxCapacity().modifyMult(id,1+FluxCapacitors*BONUS_C);
		stats.getFluxDissipation().modifyMult(id,1+FluxVents*BONUS_V);
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", BONUS_C*100);
		if(index==1)return String.format("%.1f", BONUS_V*100);
		return null;
	}
	
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().hasHullMod("IrX_quantized_hull"))return true;
		return false;
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (!ship.getVariant().hasHullMod("IrX_quantized_hull"))return "只能被安装在量子化的舰体上";
		return null;
	}

}
