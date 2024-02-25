package data.hullmods;

import java.util.Map;
import java.util.HashMap;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.util.IntervalUtil;

public class WeaponPower extends BaseHullMod{

    private static final IntervalUtil interval = new IntervalUtil(0.033f, 0.033f);
    
    public static final String KEY="WEAPON_POWER";

    public static final Map<HullSize,Float> BASE=new HashMap<HullSize,Float>();

    public static final float WP_PER_WOP=0.001f;

    public static final float BASE_CR=0.3f; 

    private Object key = new Object();

    static {
        BASE.put(HullSize.FIGHTER, 1.1f);
        BASE.put(HullSize.FRIGATE, 1.05f);
        BASE.put(HullSize.DESTROYER, 1f);
        BASE.put(HullSize.CRUISER, 0.95f);
        BASE.put(HullSize.CAPITAL_SHIP, 0.9f);
        BASE.put(HullSize.DEFAULT, 1f);
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (stats.getEntity() instanceof ShipAPI)?(ShipAPI)stats.getEntity():null;
        if(ship==null)return;
        ship.setCustomData(KEY,new MutableStat(BASE.get(hullSize)));
    }
    
    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        MutableStat wp=(MutableStat)ship.getCustomData().get(KEY);
        wp.modifyFlat(id,WP_PER_WOP*ship.getVariant().computeWeaponOPCost(BaseSkillEffectDescription.getCommanderStats(ship.getMutableStats())));
        ship.setCustomData(KEY, wp);
    }

    

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        interval.advance(amount);
        CombatEngineAPI engine = Global.getCombatEngine();
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        MutableShipStatsAPI Mutable = ship.getMutableStats();
        MutableStat wp=(MutableStat)ship.getCustomData().get(KEY);
        
        float cr=(ship.getCurrentCR()-BASE_CR)<=0?(1f+(ship.getCurrentCR()-BASE_CR)):(1f+(ship.getCurrentCR()-BASE_CR)*0.5f);
        wp.modifyMultAlways(KEY,cr, KEY);
        if(player){
            engine.maintainStatusForPlayerShip(this,
				"graphics/icons/hullsys/flare_launcher.png", 
                spec.getDisplayName(),
                String.format("武器效率:%.1f%%",wp.getModifiedValue()*100), false);
            engine.maintainStatusForPlayerShip(key,
				"graphics/icons/hullsys/flare_launcher.png",
                spec.getDisplayName(),
                String.format("Cr:%.1f%% 武器效率x%.1f%%",ship.getCurrentCR()*100,cr*100), cr>=1?false:true);
        }

        if(!interval.intervalElapsed())return;
        Mutable.getBallisticRoFMult().modifyMult(KEY,wp.getModifiedValue());
        Mutable.getBallisticWeaponFluxCostMod().modifyMult(KEY, 1f+(wp.getModifiedValue()-1f)*0.33f);
        Mutable.getBallisticWeaponDamageMult().modifyMult(KEY, wp.getModifiedValue());
        Mutable.getEnergyRoFMult().modifyMult(KEY,wp.getModifiedValue());
        Mutable.getEnergyWeaponFluxCostMod().modifyMult(KEY, 1f+(wp.getModifiedValue()-1f)*0.33f);
        Mutable.getEnergyWeaponDamageMult().modifyMult(KEY, wp.getModifiedValue());
        ship.setCustomData(KEY, wp);
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if(index==0)return String.format("%.1f", WP_PER_WOP*100);
        if(index==1)return "1";
        if(index==2)return "1";
        if(index==3)return "1";
        if(index==4)return "0.33";
        if(index==5)return String.format("%.1f", BASE.get(hullSize)*100);
        return null;
    }
}