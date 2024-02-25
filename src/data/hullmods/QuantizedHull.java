package data.hullmods;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.impl.campaign.ids.Stats;


public class QuantizedHull extends BaseHullMod{

    private static final IntervalUtil interval = new IntervalUtil(0.033f, 0.033f);
    
    private static final float REPAIR = 0.001f;
    private static final float BASE_ALPHA = 0.1f;
    private static final float ALPHA = 0.8f;
    private static final Color color = new Color(90,255,255,35);

    private static final float DAMAGE_TAKEN = 0.2f;
    private static final float MAX_TAKEN_REDUCE = 0.4f;
    private static final float DAMAGE_TAKEN_MULT=2;
    private static final float MAX_TAKEN_MULT = 0.6f;

    private static final float HITPOINTS_MULT=1.2f;
    public static final float RADIUS_MULT = 1.2f;
	public static final float DAMAGE_MULT = 10f;
    
    public static final float BASE_RANGE=2f;
    public static final float RANGE=6f;

    public static final float WEAPON_ENGINE_BONUS=0.95f;

    public static final float FRAG_TAKEN=0.5f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getHullBonus().modifyMult(id, HITPOINTS_MULT);
        stats.getDynamic().getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyMult(id, DAMAGE_MULT);
		stats.getDynamic().getStat(Stats.EXPLOSION_RADIUS_MULT).modifyMult(id, RADIUS_MULT);
        stats.getEmpDamageTakenMult().modifyMult(id, 1f-WEAPON_ENGINE_BONUS);
        stats.getFragmentationDamageTakenMult().modifyMult(id,1-FRAG_TAKEN);
    }

    @Override
	public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        MutableShipStatsAPI stats = ship.getMutableStats();
        float HitPointLevel = Math.min(1,(ship.getMaxHitpoints()-ship.getHitpoints())/(ship.getMaxHitpoints()*(1f-MAX_TAKEN_REDUCE))); 
        float TakenMult = HitPointLevel*DAMAGE_TAKEN;
        float mult = 1f+Math.min(1f,(ship.getHardFluxLevel())/MAX_TAKEN_MULT)*DAMAGE_TAKEN_MULT;
        stats.getHullDamageTakenMult().modifyMult(spec.getId(),1f-TakenMult*mult);
        
        if(player){
            engine.maintainStatusForPlayerShip(this,
					"graphics/icons/hullsys/fortress_shield.png", spec.getDisplayName(), String.format("伤害减免 %.1f%%", TakenMult*mult*100), false);
        }

        if(ship.isAlive()){
            ship.setHitpoints(Math.min(
            ship.getHitpoints() + interval.getIntervalDuration() * ship.getMaxHitpoints() * REPAIR /ship.getMutableStats().getTimeMult().getModifiedValue(),
            ship.getMaxHitpoints()
            ));
        }
        ship.setExtraAlphaMult(BASE_ALPHA+ALPHA*(1-HitPointLevel));
        float range=BASE_RANGE + RANGE*HitPointLevel;
        ship.setJitterUnder(this,color , 2f, 3, range,range);
	}

    public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", REPAIR*100);
        if(index==1)return String.format("%.1f", (HITPOINTS_MULT-1)*100);
        if(index==2)return String.format("%.1f", FRAG_TAKEN*100);
        if(index==3)return String.format("%.1f", WEAPON_ENGINE_BONUS*100);
        if(index==4)return String.format("%.1f", DAMAGE_TAKEN*100);
		return null;
	}
}
