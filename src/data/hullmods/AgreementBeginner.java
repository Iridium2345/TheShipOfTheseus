package data.hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AgreementBeginner extends TSOTAgreement {
	
	private static final float BONUS_V=0.01f;
	private static final float BONUS_C=0.01f;

	@Override
	protected void increase(MutableShipStatsAPI stats,String id,float numFluxVents, float numFluxCapacitors) {
		stats.getFluxCapacity().modifyMult(id,1+numFluxCapacitors*BONUS_C);
		stats.getFluxDissipation().modifyMult(id,1+numFluxVents*BONUS_V);
	}

	@Override
	protected void decrease(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		return ;
	}

	@Override
	protected String getDescriptionVents(float numFluxVents) {
		return String.format("幅能耗散: %.1f", BONUS_V*numFluxVents*100)+"%";
	}

	@Override
	protected String getDescriptionCapacitors(float numFluxCapacitors) {
		return String.format("幅能容量: %.1f", BONUS_C*numFluxCapacitors*100)+"%";
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", BONUS_C*100);
		if(index==1)return String.format("%.1f", BONUS_V*100);
		return null;
	}
}
