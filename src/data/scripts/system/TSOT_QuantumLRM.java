package data.scripts.system;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;

public class TSOT_QuantumLRM extends BaseShipSystemScript{
    public static final float MAX_CHARGE = 3000f;
    public static final float HIT_POINT_PER_S=0.002f;
    public IntervalUtil interval = new IntervalUtil(0.033f, 0.033f);
    
    public static final Color F = new Color(0,0,0,255);
    public static final Color C = new Color(255,255,255,255);

    public static final float RANGE = 4000f;

    private float Charged = 0f;

    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
		ShipSystemAPI sys = playerShip.getSystem();
		
        Global.getCombatEngine().maintainStatusForPlayerShip(
            this,
			sys.getSpecAPI().getIconSpriteName(), 
            sys.getDisplayName(),
            String.format("已充能 : %.1f",Charged),
            false);
	}

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        CombatEngineAPI engine = Global.getCombatEngine();
        ShipAPI ship= null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else return;
        if(!ship.isAlive())return;
        if(player)maintainStatus(ship, state, effectLevel);
        if (engine.isPaused())return;
        interval.advance(engine.getElapsedInLastFrame());
        if(!interval.intervalElapsed())return;
        if(state == State.ACTIVE){
            for(int i = 0 ;i<3;i++){
                if(Charged>0){
                    engine.spawnProjectile(ship, null, "TSOT_Quantum_lrm", ship.getLocation(),(float)Math.random()*360f, null);
                    Charged -= 75f;
                }else{
                    ship.getSystem().deactivate();
                    return;
                }
            }
        }        
        if(Charged>=MAX_CHARGE)return;
        float ch=ship.getHitpoints()*HIT_POINT_PER_S*interval.getElapsed();
        ship.setHitpoints(ship.getHitpoints()-ch);
        Charged=Math.min(Charged+ch,MAX_CHARGE);
        return;
    }
}
