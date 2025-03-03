package data.scripts.weapon;

import java.awt.Color;

import java.util.EnumMap;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class TSOT_Hil_BeamEffect implements BeamEffectPlugin {
	
	private static final EnumMap<DamageType,Color> Colors = new EnumMap<>(DamageType.class);

	static {
		Colors.put(DamageType.FRAGMENTATION, new Color(75,75,75));
		Colors.put(DamageType.HIGH_EXPLOSIVE, new Color(255,100,80));
		Colors.put(DamageType.KINETIC, new Color(80,100,255));
	}

	public void setBeam(BeamAPI beam , DamageType type){
		beam.getDamage().setType(type);
		beam.setFringeColor(Colors.get(type));
	}

	public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
		final CombatEntityAPI target = beam.getDamageTarget();
		if (beam.getBrightness() >= 1f)
		if (target instanceof MissileAPI) setBeam(beam, DamageType.FRAGMENTATION);
		else if (target instanceof ShipAPI)
		if (target.getShield() != null && target.getShield().isWithinArc(beam.getTo()))
			setBeam(beam, DamageType.KINETIC);
		else setBeam(beam, DamageType.HIGH_EXPLOSIVE);
	}
	
}
