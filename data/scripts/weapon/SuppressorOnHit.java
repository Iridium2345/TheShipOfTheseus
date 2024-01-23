package data.scripts.weapon;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;

public class SuppressorOnHit  implements OnHitEffectPlugin{
    public void onHit(
        DamagingProjectileAPI projectile,
        CombatEntityAPI target,
	    Vector2f point,
        boolean shieldHit,
        ApplyDamageResultAPI damageResult,
        CombatEngineAPI engine) {
            if(shieldHit)engine.spawnDamagingExplosion(Explosion(),projectile.getSource(), point);
    }

    public DamagingExplosionSpec Explosion() {
		float damage = 2000f;
		DamagingExplosionSpec spec = new DamagingExplosionSpec(
				0.8f, // duration
				500f, // radius
				100f, // coreRadius
				damage, // maxDamage
				damage*0.2f, // minDamage
				CollisionClass.PROJECTILE_FF, // collisionClass
				CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
				20f, // particleSizeMin
				12f, // particleSizeRange
				5f, // particleDuration
				300, // particleCount
				new Color(245,100,245,225), // particleColor
				new Color(230,45,225,150)  // explosionColor
		);

		spec.setDamageType(DamageType.KINETIC);
		spec.setUseDetailedExplosion(false);
		return spec;		
	}
}