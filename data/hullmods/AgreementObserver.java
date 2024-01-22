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

public class AgreementObserver extends BaseHullMod {
	
	private static final float RANGE_BONUS_1=6f;
	private static final float RANGE_BONUS_2=0.005f;
	private static final Color GREEN = new Color(68, 183, 188, 255);
	@Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float FluxVents=(float)ship.getVariant().getNumFluxVents();
		float FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
		if (ship != null) {
			tooltip.addPara(String.format("额外射程: %.1f", RANGE_BONUS_1*FluxCapacitors),GREEN,10f);
			tooltip.addPara(String.format("范围增加: %.1f", RANGE_BONUS_2*FluxVents*100)+"%",GREEN,10f);
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
		stats.getBallisticWeaponRangeBonus().modifyFlat(id, RANGE_BONUS_1*FluxCapacitors);
		stats.getEnergyWeaponRangeBonus().modifyFlat(id, RANGE_BONUS_1*FluxCapacitors);

		stats.getBallisticWeaponRangeBonus().modifyMult(id, 1+RANGE_BONUS_2*FluxVents);
		stats.getEnergyWeaponRangeBonus().modifyMult(id, 1+RANGE_BONUS_2*FluxVents);
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", RANGE_BONUS_1);
		if(index==1)return String.format("%.1f", RANGE_BONUS_2*100);
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
