package data.scripts.weapon;

import java.awt.Color;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CollisionClass;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;

public class DestroyerOnHit  implements OnHitEffectPlugin{
    public void onHit(
        DamagingProjectileAPI projectile,
        CombatEntityAPI target,
	    Vector2f point,
        boolean shieldHit,
        ApplyDamageResultAPI damageResult,
        CombatEngineAPI engine) {
			if(!(target instanceof ShipAPI))return;
			float damage = projectile.getDamageAmount();
			DamagingExplosionSpec spec = new DamagingExplosionSpec(
				0.05f, // duration
				800f, // radius
				100f, // coreRadius
				damage, // maxDamage
				damage*0.2f, // minDamage
				CollisionClass.PROJECTILE_FF, // collisionClass
				CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
				1f, // particleSizeMin
				2f, // particleSizeRange
				5f, // particleDuration
				300, // particleCount
				new Color(235,100,235,20), // particleColor
				new Color(230,20,230,10)  // explosionColor
			);
		spec.setDamageType(DamageType.HIGH_EXPLOSIVE);
		spec.setUseDetailedExplosion(true);
        engine.spawnDamagingExplosion(spec,projectile.getSource(), point);
    }
}