package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class AgreementFearless extends TSOTAgreement {
	
	private static final float SPEED_BOOST=1f;
	private static final float WP_BONUS=0.015f;
	private static final float CAPACITY_REDUCE=0.2f; 

	static{
		SubMods.add("TSOT_agreement_fearless");
	}

	@Override
	public void advanceInCombat(ShipAPI ship, float amount){
		CombatEngineAPI engine = Global.getCombatEngine();
		Object test=ship.getCustomData().get(WeaponPower.KEY);
		if(test==null){
			engine.maintainStatusForPlayerShip(this,
					"graphics/icons/hullsys/flare_launcher.png", spec.getDisplayName(),"NULL", false);
			return;
		}
		if(!(test instanceof MutableStat)){
			engine.maintainStatusForPlayerShip(this,
					"graphics/icons/hullsys/flare_launcher.png", spec.getDisplayName(),"NOT INSTANCE", false);
			return;
		}
		MutableStat wp=(MutableStat)test;
		wp.modifyFlat(this.spec.getId(),ship.getVariant().getNumFluxCapacitors()*WP_BONUS);
		ship.setCustomData(WeaponPower.KEY, wp);
	}

	@Override
	protected void decrease(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getFluxCapacity().modifyMult(id,1f-CAPACITY_REDUCE);
	}

	@Override
	protected void increase(MutableShipStatsAPI stats, String id, float numFluxVents, float numFluxCapacitors) {
		stats.getMaxSpeed().modifyFlat(id,numFluxVents*SPEED_BOOST);
	}

	@Override
	protected String getDescriptionCapacitors(float numFluxCapacitors) {
		return String.format("武器效率: %.1f%%", numFluxCapacitors*WP_BONUS*100);
	}

	@Override
	protected String getDescriptionVents(float numFluxVents) {
		return String.format("航速加成: %.1f", numFluxVents*SPEED_BOOST);
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return ""+SPEED_BOOST;
		if(index==1)return ""+WP_BONUS*100;
		if(index==2)return ""+CAPACITY_REDUCE*100;
		return null;
	}
}
