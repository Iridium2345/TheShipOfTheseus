package data.scripts.weapon;

import java.awt.Color;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class DustOnHit implements OnHitEffectPlugin{
    private static final Color Particle_Core = new Color(255, 25, 255,150);
    private static final Color Particle_Fringe = new Color(255, 255, 255,150);

    public void onHit(
        DamagingProjectileAPI projectile,
        CombatEntityAPI target,
	    Vector2f point,
        boolean shieldHit,
        ApplyDamageResultAPI damageResult,
        CombatEngineAPI engine) {
            final ShipAPI source = projectile.getSource();
            if (target instanceof ShipAPI)
                if (((ShipAPI)target).isFighter()) 
                    genArc4Fighter(source, engine, (ShipAPI)target);
                else 
                genArc(source, engine, projectile, target);
            else if (!(target instanceof MissileAPI)) return;
                genArc(source, engine, source, target);
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
            final List<ShipAPI> wings = target.getWing().getWingMembers();
            for (final ShipAPI wing : wings) {
               if (wing == target) continue;
               genArc(source, engine, target, wing);
            }
    }
}
