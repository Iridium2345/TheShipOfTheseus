package data.hullmods;

import java.util.Map;
import java.util.HashMap;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.util.IntervalUtil;

public class test extends BaseHullMod{
    public static Map<String,String> MOD_SLOT = new HashMap<String,String>();
    static{
        MOD_SLOT.put("WS0021", "TSOT_Infinite_bow_station");
        MOD_SLOT.put("WS0022", "TSOT_Infinite_stern_station");
        MOD_SLOT.put("WS0023", "TSOT_Infinite_board_station");
        MOD_SLOT.put("WS0024", "TSOT_Infinite_board_station");
        MOD_SLOT.put("WS0025", "TSOT_Infinite_weapon_station");
        MOD_SLOT.put("WS0026", "TSOT_Infinite_weapon_station");
    }
    
    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if(stats.getVariant().getHullSpec().getHullId()!="TSOT_Infinite")return;
        for(String ms :MOD_SLOT.keySet()){
            Global.getLogger(getClass()).info(String.format("%s : %s",ms,Global.getSettings().getVariant(MOD_SLOT.get(ms))));
            stats.getVariant().setModuleVariant(ms,Global.getSettings().getVariant(MOD_SLOT.get(ms)));
        }
    }
}