package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;

public class WeaponPower extends BaseHullMod{

    public static final String KEY="WEAPON_POWER";

    public static final float BASE=1f;

    public static final float WP_PER_WOP=0.001f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (stats.getEntity() instanceof ShipAPI)?(ShipAPI)stats.getEntity():null;
        if(ship==null)return;
        ship.setCustomData(KEY,new MutableStat(BASE+WP_PER_WOP*ship.getVariant().computeWeaponOPCost(BaseSkillEffectDescription.getCommanderStats(stats))));
    }
    
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        CombatEngineAPI engine = Global.getCombatEngine();
        boolean player = ship == Global.getCombatEngine().getPlayerShip();
        MutableShipStatsAPI Mutable = ship.getMutableStats();
        MutableStat wp=(MutableStat)ship.getCustomData().get(KEY);
        Mutable.getBallisticRoFMult().modifyMult(KEY,wp.getModifiedValue());
        Mutable.getBallisticWeaponFluxCostMod().modifyMult(KEY, wp.getModifiedValue());
        Mutable.getBallisticWeaponDamageMult().modifyMult(KEY, wp.getModifiedValue());
        
        Mutable.getEnergyRoFMult().modifyMult(KEY,wp.getModifiedValue());
        Mutable.getEnergyWeaponFluxCostMod().modifyMult(KEY, wp.getModifiedValue());
        Mutable.getEnergyWeaponDamageMult().modifyMult(KEY, wp.getModifiedValue());
        if(player){
            engine.maintainStatusForPlayerShip(this,
					"graphics/icons/hullsys/flare_launcher.png", spec.getDisplayName(),String.format("武器效率:%.1f%%",wp.getModifiedValue()*100), false);
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if(index==0)return String.format("%.1f", WP_PER_WOP*100);
        if(index==1)return "1";
        if(index==2)return "1";
        if(index==3)return "1";
        if(index==4)return "1";
        if(index==5)return String.format("%.1f", BASE*100);
        return null;
    }
}