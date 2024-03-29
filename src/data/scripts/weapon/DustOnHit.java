package data.scripts.weapon;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
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
            engine.spawnEmpArc(
                projectile.getSource(),
                point,
                projectile.getSource(),
                target,
                DamageType.FRAGMENTATION,
                60f,
                10f,
                300f,
                null,
                30f,
                Particle_Core,
                Particle_Fringe);   
        }
}
