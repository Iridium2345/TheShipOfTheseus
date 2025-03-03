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

public class SupportOnHit  implements OnHitEffectPlugin{
    private static final Color COLOR_Core = new Color(255, 255, 255,150);
    private static final Color COLOR_Fringe = new Color(255, 25, 255,150);
	public static final int NUM_OF_ARC = 10;
	
	public void onHit(
        DamagingProjectileAPI projectile,
        CombatEntityAPI target,
	    Vector2f point,
        boolean shieldHit,
        ApplyDamageResultAPI damageResult,
        CombatEngineAPI engine) {
			float damage = projectile.getDamageAmount();
			DamagingExplosionSpec spec = new DamagingExplosionSpec(
				0.05f, // duration
				1000f, // radius
				100f, // coreRadius
				damage*.5f, // maxDamage
				damage*.2f, // minDamage
				CollisionClass.PROJECTILE_FF, // collisionClass
				CollisionClass.PROJECTILE_FIGHTER, // collisionClassByFighter
				5f, // particleSizeMin
				2f, // particleSizeRange
				0.8f, // particleDuration
				10, // particleCount
				new Color(55,55,55), // particleColor
				new Color(15,15,15)  // explosionColor
			);
		spec.setDamageType(DamageType.FRAGMENTATION);
		spec.setUseDetailedExplosion(true);
        engine.spawnDamagingExplosion(spec,projectile.getSource(), point);

		for(int i = 0 ; i < NUM_OF_ARC ; i++)
		engine.spawnEmpArcPierceShields(
			projectile.getSource(), 
			point, 
			projectile, 
			target, 
			DamageType.FRAGMENTATION, 
			damage * .05f, 
			damage * .05f, 
			10000f, 
			null, 
			40f, 
			COLOR_Fringe, 
			COLOR_Core);
    }
}