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

public class AgreementFearless extends BaseHullMod {
	
	private static final float SPEED_BOOST=0.75f;
	private static final float ROF_BONUS=1f;
	private static final float FLUX_REDUCTION=0.005f;
	private static final Color GREEN = new Color(68, 183, 188, 255);
	@Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float FluxVents=(float)ship.getVariant().getNumFluxVents();
		float FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
		if (ship != null) {
			tooltip.addPara(String.format("航速加成: %.1f", FluxVents*SPEED_BOOST),GREEN,10f);
			tooltip.addPara(String.format("射速加成: %.1f", FluxCapacitors*ROF_BONUS)+"%",GREEN,10f);
			tooltip.addPara(String.format("武器幅能减免: %.1f", (FluxCapacitors*FLUX_REDUCTION)*100)+"%",GREEN,10f);
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
		stats.getMaxSpeed().modifyFlat(id,FluxVents*SPEED_BOOST);
		stats.getBallisticRoFMult().modifyPercent(id, ROF_BONUS * FluxCapacitors);
        stats.getEnergyRoFMult().modifyPercent(id, ROF_BONUS * FluxCapacitors);
		stats.getBallisticWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * FluxCapacitors));
		stats.getEnergyWeaponFluxCostMod().modifyMult(id, 1f - (FLUX_REDUCTION * FluxCapacitors));
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return ""+SPEED_BOOST;
		if(index==1)return ""+ROF_BONUS;
		if(index==2)return ""+FLUX_REDUCTION*100;
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
