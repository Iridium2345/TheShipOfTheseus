package data.hullmods;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FluxTrackerAPI;
import com.fs.starfarer.api.combat.HullModFleetEffect;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class QuantizedHull extends BaseHullMod implements HullModFleetEffect{

    private static final float DetectedRange = .25f;
    private static final float SensorRange = 2f;

    private static final float REPAIR = .001f;
    private static final float BASE_ALPHA = .1f;
    private static final float ALPHA = .8f;
    private static final Color color = new Color(90,255,255,35);

    private static final float SensorProfile = 0.05f;
    private static final float SensorStrength = 2f;

    private static final Map<HullSize,Float> DAMAGE_TAKEN = new EnumMap<>(HullSize.class);
    private static final Map<HullSize,Float> ARMOR = new EnumMap<>(HullSize.class);

    private static final float MAX_TAKEN_REDUCE = .25f;

    private static final float HITPOINTS_MULT=1.2f;
    public static final float RADIUS_MULT = 15f;
	public static final float DAMAGE_MULT = 1.5f;
    
    public static final float BASE_RANGE=2f;
    public static final float RANGE=6f;

    public static final float WEAPON_ENGINE_BONUS= .95f;

    private static final float FLUXDISSIPATION = .5f;

    private static final float SOFT2HARD = .1f;
    private static final float SOFT2HARD_RATE = .4f;
    private static final float BASEARMOR = .005f;

    static {
        DAMAGE_TAKEN.put(HullSize.DEFAULT, 0f);
        DAMAGE_TAKEN.put(HullSize.FIGHTER, .5f);
        DAMAGE_TAKEN.put(HullSize.FRIGATE, .55f);
        DAMAGE_TAKEN.put(HullSize.DESTROYER, .6f);
        DAMAGE_TAKEN.put(HullSize.CRUISER, .65f);
        DAMAGE_TAKEN.put(HullSize.CAPITAL_SHIP, .75f);

        ARMOR.put(HullSize.DEFAULT, 1f);
        ARMOR.put(HullSize.FIGHTER, 1.5f);
        ARMOR.put(HullSize.FRIGATE, .90f);
        ARMOR.put(HullSize.DESTROYER, .70f);
        ARMOR.put(HullSize.CRUISER, .50f);
        ARMOR.put(HullSize.CAPITAL_SHIP, .30f);
    }

    private static final float HARDFLUX2ARMOR = 0.75f;
    private static final float hardFlux2Armor(float flux){
        return (float) Math.pow(flux*HARDFLUX2ARMOR , HARDFLUX2ARMOR);
    } 

    public void onFleetSync(CampaignFleetAPI fleet) {
        fleet.getDetectedRangeMod().modifyMult("Quantized", DetectedRange, "Quantized ships in fleet");
        fleet.getSensorRangeMod().modifyMult("Quantized", SensorRange ,"Quantized ships in fleet");
    };

    public void advanceInCampaign(CampaignFleetAPI fleet) {
        String key = "$updatedQuantizedModifier";
		if (fleet.isPlayerFleet() && fleet.getMemoryWithoutUpdate() != null &&
				!fleet.getMemoryWithoutUpdate().getBoolean(key) &&
				fleet.getMemoryWithoutUpdate().getBoolean(MemFlags.JUST_TOGGLED_TRANSPONDER)) {
			onFleetSync(fleet);
			fleet.getMemoryWithoutUpdate().set(key, true, 0.1f);
		}
    };

    public boolean withAdvanceInCampaign() {
		return true;
	}
    
	public boolean withOnFleetSync() {
		return true;
	}
    
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getHullBonus().modifyMult(id, HITPOINTS_MULT);
        stats.getDynamic().getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyMult(id, DAMAGE_MULT);
		stats.getDynamic().getStat(Stats.EXPLOSION_RADIUS_MULT).modifyMult(id, RADIUS_MULT);
        stats.getWeaponDamageTakenMult().modifyMult(id, 1f-WEAPON_ENGINE_BONUS);
        stats.getEngineDamageTakenMult().modifyMult(id, 1f-WEAPON_ENGINE_BONUS);
        stats.getSensorProfile().modifyMult(id, SensorProfile);
        stats.getSensorStrength().modifyMult(id, SensorStrength);
		stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(id, 1);
    }

    @SuppressWarnings("unused")
    @Override
	public void advanceInCombat(ShipAPI ship, float amount) {
        if (!ship.isAlive())return;

        final CombatEngineAPI engine = Global.getCombatEngine();
        final boolean player = ship == Global.getCombatEngine().getPlayerShip();
        final MutableShipStatsAPI stats = ship.getMutableStats();
        final FluxTrackerAPI tracker=ship.getFluxTracker();
        final float HitPointLevel = Math.min(1,(ship.getMaxHitpoints()-ship.getHitpoints())/(ship.getMaxHitpoints()*(1f-MAX_TAKEN_REDUCE))); 
        final float takenMult = HitPointLevel*DAMAGE_TAKEN.get(ship.getHullSize());
        final float armor = hardFlux2Armor(
            Math.min( tracker.getHardFlux() * 2f,tracker.getMaxFlux() )
            * ARMOR.get(ship.getHullSize()) 
        ) + tracker.getMaxFlux() * BASEARMOR;
        
        Reduce_Damage_Taken:stats.getHullDamageTakenMult().modifyMult(spec.getId(),1f-takenMult);
        Repair_ship:ship.setHitpoints(Math.min(
            ship.getHitpoints() + amount * ship.getMaxHitpoints() * REPAIR,
            ship.getMaxHitpoints()
        ));

        Setup_jitter:{
            ship.setExtraAlphaMult(BASE_ALPHA+ALPHA*(1-HitPointLevel));
            final float range=BASE_RANGE + RANGE * HitPointLevel;
            ship.setJitterUnder(this,color , 2f, 3, range,range);
        }

        FluxDiss : if (tracker.getFluxLevel() < 0.5f) {
            stats.getFluxDissipation().modifyMult(spec.getId(), 1 - FLUXDISSIPATION);
        } else {
            stats.getFluxDissipation().modifyMult(spec.getId(), 1 + FLUXDISSIPATION);
        }

        SoftFlux2HardFlux:{
            final float softFlux = tracker.getCurrFlux()-tracker.getHardFlux();
            final float decreasedFlux= softFlux * SOFT2HARD_RATE * amount;
            tracker.decreaseFlux(decreasedFlux);
            tracker.increaseFlux(decreasedFlux*SOFT2HARD, true);
        }

        Setup_Armor:{
            stats.getEffectiveArmorBonus().modifyFlat(spec.getId(), armor);
        }

        if(player){
            engine.maintainStatusForPlayerShip(this,
					"graphics/icons/hullsys/fortress_shield.png", spec.getDisplayName(), 
                    String.format("伤害减免 %.1f%% 装甲 %.0f", takenMult * 100 , armor),
                    false
            );
        }
	}

    public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0)return String.format("%.1f", REPAIR*100);
        if(index==1)return String.format("%.1f", (HITPOINTS_MULT-1)*100);
        if(index==2)return String.format("%.1f", WEAPON_ENGINE_BONUS*100);
        if(index==3)return String.format("%.1f", DAMAGE_TAKEN.get(hullSize)*100);
		return null;
	}
}
