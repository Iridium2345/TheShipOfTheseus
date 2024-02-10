package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class WeaponPower extends BaseHullMod{

    public static final String KEY="WEAPON_POWER";

    public static final float BASE=1.10f;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.setCustomData(KEY,new MutableStat(BASE));
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
        // Mutable.getDamageToCapital().modifyMult(KEY, wp.getModifiedValue());
        // Mutable.getDamageToCruisers().modifyMult(KEY, wp.getModifiedValue());
        // Mutable.getDamageToDestroyers().modifyMult(KEY,wp.getModifiedValue());
        // Mutable.getDamageToFrigates().modifyMult(KEY,wp.getModifiedValue());
        // Mutable.getDamageToFighters().modifyMult(KEY,wp.getModifiedValue());
        // Mutable.getDamageToMissiles().modifyMult(KEY,wp.getModifiedValue());
        if(player){
            engine.maintainStatusForPlayerShip(this,
					"graphics/icons/hullsys/flare_launcher.png", spec.getDisplayName(),String.format("武器效率:%.1f%%",wp.getModifiedValue()*100), false);
        }
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if(index==0)return "1";
        if(index==1)return "1";
        if(index==2)return "1";
        if(index==3)return "1";
        if(index==4)return String.format("%.1f", BASE*100);
        return null;
    }
}