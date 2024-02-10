package data.scripts.system;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

import data.hullmods.WeaponPower;

public class IrX_ttk extends BaseShipSystemScript{
    
    public static final float MAX_WEAPON_POWER = 1.5f;
    public static final float WEAPON_FLUX_REDUCE = 1f;
    public static final float MAX_RANGE_REDUCE = 0.5f;

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = (stats.getEntity() instanceof ShipAPI)?(ShipAPI)stats.getEntity():null;
        if(ship==null)return;
        MutableStat wp = (ship.getCustomData().get(WeaponPower.KEY) instanceof MutableStat)?(MutableStat)ship.getCustomData().get(WeaponPower.KEY):null;
        if(wp==null)return;
        wp.modifyMult(id,effectLevel*MAX_WEAPON_POWER);
        ship.setCustomData(WeaponPower.KEY, wp);
        stats.getEnergyWeaponRangeBonus().modifyMult(id, 1f-effectLevel*MAX_RANGE_REDUCE);
        stats.getBallisticWeaponRangeBonus().modifyMult(id, 1f-effectLevel*MAX_RANGE_REDUCE);
        stats.getEnergyWeaponFluxCostMod().modifyMult(id,WEAPON_FLUX_REDUCE);
        stats.getBallisticWeaponFluxCostMod().modifyMult(id,WEAPON_FLUX_REDUCE);
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (stats.getEntity() instanceof ShipAPI)?(ShipAPI)stats.getEntity():null;
        if(ship==null)return;
        MutableStat wp = (ship.getCustomData().get(WeaponPower.KEY) instanceof MutableStat)?(MutableStat)ship.getCustomData().get(WeaponPower.KEY):null;
        if(wp==null)return;
        wp.unmodify(id);
        ship.setCustomData(WeaponPower.KEY, wp);
        stats.getEnergyWeaponRangeBonus().unmodify(id);
        stats.getBallisticWeaponRangeBonus().unmodify(id);
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return !(ship.getCustomData().get(WeaponPower.KEY)==null);
    }
}
