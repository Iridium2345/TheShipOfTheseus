package data.hullmods;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;
import com.fs.starfarer.api.util.IntervalUtil;

public class AdvancedBeam extends BaseHullMod{

    public static final float FLUX_MULT = 2f;

    public static final float RANGE_BONUS = 1.2f;

    public static final float LVL_1 = .15f;
    public static final float LVL_2 = .3f;
    public static final float LVL_3 = .45f;

    public static final float DAMAGE_BONUS = 1.15f;
    public static final float SHIELD_HIT_BONUS = 15f;

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBeamWeaponRangeBonus().modifyMult(id, RANGE_BONUS);
        stats.getBeamWeaponFluxCostMult().modifyMult(id, FLUX_MULT);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new AdvBeamDamage(ship));
    }

    public static class AdvBeamDamage implements DamageDealtModifier {
        
        private static final Color Particle_Core = new Color(255, 40, 255,255);
        private static final Color Particle_Fringe = new Color(255, 255, 255,255);
        
        private final ShipAPI ship;
        private final float maxFlax;
        private final float lvl1;
        private final float lvl2;
        private final float lvl3;

        private final IntervalUtil interval; 

        public AdvBeamDamage(ShipAPI shipAPI){
            ship = shipAPI;
            maxFlax = ship.getHullSpec().getFluxCapacity();
            lvl1 = maxFlax * LVL_1;
            lvl2 = maxFlax * LVL_2;
            lvl3 = maxFlax * LVL_3;
            int num = 1;
            for(final WeaponAPI weapon : ship.getAllWeapons())
                if(weapon.isBeam()) num ++;
            final float _interval = 1.f / num;
            interval = new IntervalUtil(_interval,_interval);
        }

        @Override
        public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point,
                boolean shieldHit) {
            if (param instanceof DamagingProjectileAPI) return null;
            if (!(param instanceof BeamAPI)) return null;
            final float currFlux = ship.getFluxTracker().getHardFlux();
            if (currFlux > lvl1) damage.getModifier().modifyMult(getClass().getName(), DAMAGE_BONUS);
            if (currFlux > lvl2) damage.setForceHardFlux(true);
            if (currFlux > lvl3) {
                final CombatEngineAPI engine = Global.getCombatEngine();
                interval.advance(engine.getElapsedInLastFrame());
                if (interval.intervalElapsed()) {
                    engine.spawnEmpArc(
                        ship, 
                        ship.getShieldCenterEvenIfNoShield(), 
                        ship, 
                        target, 
                        DamageType.FRAGMENTATION, 
                        SHIELD_HIT_BONUS, 
                        SHIELD_HIT_BONUS, 
                        1000000f, 
                        null,
                        35f,
                        Particle_Fringe,
                        Particle_Core);
                }
            }
            return null;
        }
        
    }

}
