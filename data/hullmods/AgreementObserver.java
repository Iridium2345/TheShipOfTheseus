package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AgreementObserver extends IrXAgreement {
	
	private static final float RANGE_BONUS_1=6f;
	private static final float RANGE_BONUS_2=0.005f;
	private static final float DISSIPATION_REDUCE=0.15f;

	static{
		SubMods.add("IrX_agreement_observer");
	}

	@Override
	protected void increase(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getBallisticWeaponRangeBonus().modifyFlat(id+"_1", RANGE_BONUS_1*numFluxCapacitors);
		stats.getEnergyWeaponRangeBonus().modifyFlat(id+"_1", RANGE_BONUS_1*numFluxCapacitors);

		stats.getBallisticWeaponRangeBonus().modifyMult(id, 1+RANGE_BONUS_2*numFluxVents);
		stats.getEnergyWeaponRangeBonus().modifyMult(id, 1+RANGE_BONUS_2*numFluxVents);
	}

	@Override
	protected void decrease(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getFluxDissipation().modifyMult(id,1f-DISSIPATION_REDUCE);
	}
	
	@Override
	protected String getDescriptionCapacitors(float numFluxCapacitors) {
		return String.format("额外射程: %.1f", RANGE_BONUS_1*numFluxCapacitors);
	}

	@Override
	protected String getDescriptionVents(float numFluxVents) {
		return String.format("范围增加: %.1f", RANGE_BONUS_2*numFluxVents*100)+"%";
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", RANGE_BONUS_2*100);
		if(index==1)return String.format("%.1f", RANGE_BONUS_1);
		if(index==2)return String.format("%.1f", DISSIPATION_REDUCE*100);
		return null;
	}
}
