package data.scripts.weapon;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;

public class DustOnHit implements OnHitEffectPlugin{
    private static final Color Particle_Core = new Color(255, 25, 255,150);
    private static final Color Particle_Fringe = new Color(255, 255, 255,150);
    private static final Random rand = new Random();
    private static final int explosion_rate = 50;
    private static final float DAM = 200;
    private static final float FLUX = 60;

    private static final DamagingExplosionSpec spec = new DamagingExplosionSpec(
        0.25f, // duration
        50f, // radius
        10f, // coreRadius
        DAM, // maxDamage
        DAM*.5f, // minDamage
        CollisionClass.PROJECTILE_FF, // collisionClass
        CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
        2f, // particleSizeMin
        12f, // particleSizeRange
        0.25f, // particleDuration
        5, // particleCount
        new Color(235,100,50,225), // particleColor
        new Color(230,230,45,150)  // explosionColor
    );

    static{
        spec.setUseDetailedExplosion(false);
        spec.setDamageType(DamageType.FRAGMENTATION);
    }

    public void onHit(
        DamagingProjectileAPI projectile,
        CombatEntityAPI target,
	    Vector2f point,
        boolean shieldHit,
        ApplyDamageResultAPI damageResult,
        CombatEngineAPI engine) {
            final ShipAPI source = projectile.getSource();
            if (target instanceof ShipAPI) {
                if (((ShipAPI)target).isFighter()) 
                    genArc4Fighter(source, engine, (ShipAPI)target);
                if (rand.nextInt(0,1000) >= explosion_rate) return;
                engine.spawnDamagingExplosion( spec , source, point );
                final FluxTrackerAPI tracker = source.getFluxTracker();
                tracker.increaseFlux(FLUX * 1 - tracker.getFluxLevel() , false);
            } else if (!(target instanceof MissileAPI)) 
                genArc(source, engine, projectile, target);
        }

    private static final void genArc(
        final ShipAPI source,
        final CombatEngineAPI engine,
        final CombatEntityAPI from,
        final CombatEntityAPI to) {
            engine.spawnEmpArc(
                source, 
                from.getLocation(), 
                from, 
                to, 
                DamageType.FRAGMENTATION, 
                20f,
                0,
                100000f, 
                null,
                20f, 
                Particle_Fringe, 
                Particle_Core
            );
    }

    private static final void genArc4Fighter(
        final ShipAPI source,
        final CombatEngineAPI engine,
        final ShipAPI target) {
            if(!target.isFighter()) return;
            final FighterWingAPI ship = target.getWing();
            if(ship == null) return;
            final List<ShipAPI> wings = ship.getWingMembers();
            for (final ShipAPI wing : wings) {
               if (wing == target) continue;
               genArc(source, engine, target, wing);
            }
    }
}
