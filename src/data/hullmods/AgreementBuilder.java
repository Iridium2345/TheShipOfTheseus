package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AgreementBuilder extends TSOTAgreement {
	
	private static final float REFIT_RATE = 0.008f;

	private static final float HULL_REDUCE = 0.2f;

	static{
		SubMods.add("TSOT_agreement_builder");
	}

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		int BonusBay=0;
		if(hullSize.equals(HullSize.FRIGATE)||hullSize.equals(HullSize.DESTROYER))BonusBay=1;
		else if(hullSize.equals(HullSize.CRUISER)||hullSize.equals(HullSize.CAPITAL_SHIP))BonusBay=2;
		stats.getNumFighterBays().modifyFlat(id, BonusBay);
		super.applyEffectsBeforeShipCreation(hullSize, stats, id);
	}

	@Override
	protected void increase(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getFighterRefitTimeMult().modifyMult(id+"_1",(1-REFIT_RATE*numFluxCapacitors));
		stats.getFighterRefitTimeMult().modifyMult(id+"_2",(1-REFIT_RATE*numFluxVents));
	}

	@Override
	protected void decrease(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getHullBonus().modifyMult(id,1f-HULL_REDUCE);
	}

	@Override
	protected String getDescriptionCapacitors(float numFluxCapacitors) {
		return String.format("额外整备速率: %.1f", REFIT_RATE*numFluxCapacitors*100)+"%";
	}

	@Override
	protected String getDescriptionVents(float numFluxVents) {
		return String.format("额外整备速率: %.1f", REFIT_RATE*numFluxVents*100)+"%";
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", REFIT_RATE*100);
		if(index==1)return String.format("%.1f", REFIT_RATE*100);
		if(index==2)return String.format("%.1f", HULL_REDUCE*100);
		return null;
	}
}
