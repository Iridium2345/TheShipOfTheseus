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
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class AgreementBuilder extends BaseHullMod {
	
	private static final int EXTEND_DECK = 2;
	private static final float REFIT_RATE = 0.004f;
 	private static final Color GREEN = new Color(68, 183, 188, 255);

	@Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float FluxVents=(float)ship.getVariant().getNumFluxVents();
		float FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
		if (ship != null) {
			tooltip.addPara(String.format("额外整备速率: %.1f", REFIT_RATE*FluxCapacitors*100)+"%",GREEN,10f);
			tooltip.addPara(String.format("额外整备速率: %.1f", REFIT_RATE*FluxVents*100)+"%",GREEN,10f);
		} 
	}

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getNumFighterBays().modifyFlat(id, EXTEND_DECK);
		float FluxVents;
		float FluxCapacitors;
		if(stats.getEntity() instanceof ShipAPI){
			ShipAPI ship=(ShipAPI)stats.getEntity();
			FluxVents=(float)ship.getVariant().getNumFluxVents();
			FluxCapacitors=(float)ship.getVariant().getNumFluxCapacitors();
		}else{
			return;
		}
		stats.getFighterRefitTimeMult().modifyMult(id+"_1",(1-REFIT_RATE*FluxCapacitors));
		stats.getFighterRefitTimeMult().modifyMult(id+"_2",(1-REFIT_RATE*FluxVents));
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%d", EXTEND_DECK);
		if(index==1)return String.format("%.1f", REFIT_RATE*100);
		if(index==2)return String.format("%.1f", REFIT_RATE*100);
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
