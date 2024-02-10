package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AgreementGuardian extends IrXAgreement {
	
	private static final float HULL_BONUS=0.01f;
	private static final float TOKEN_REDUCE=0.005f;
	private static final float MAX_SPEED = 0.2f;

	static{
		SubMods.add("IrX_agreement_guardian");
	}

	@Override
	protected void increase(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getHullDamageTakenMult().modifyMult(spec.getId(),1f-numFluxCapacitors*TOKEN_REDUCE);
		stats.getHullBonus().modifyMult(id, 1f+numFluxVents*HULL_BONUS);
	}

	@Override
	protected void decrease(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getMaxSpeed().modifyMult(id,1f-MAX_SPEED);
	}
	
	@Override
	protected String getDescriptionCapacitors(float numFluxCapacitors) {
		return String.format("额外减伤: %.1f", (numFluxCapacitors*TOKEN_REDUCE)*100)+"%";
	}

	@Override
	protected String getDescriptionVents(float numFluxVents) {
		return String.format("额外结构: %.1f", (numFluxVents*HULL_BONUS)*100)+"%";
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", HULL_BONUS*100);
		if(index==1)return String.format("%.1f", TOKEN_REDUCE*100);
		if(index==2)return String.format("%.1f", MAX_SPEED*100);
		return null;
	}
}
