package data.scripts.system;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.FighterWingAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class TSOT_QuantumRaid extends BaseShipSystemScript{
    
    protected Object STATUSKEY1 = new Object();

    public static final Color COLOR = new Color(240,240,240,120);

    private ShipAPI target = null;
    
    private Boolean finish;

    private void getTarget(ShipAPI ship){
        ShipAPI tmp=ship.getShipTarget();
        if(tmp==null)return;
        if(tmp.getOwner()==1)return;
        target=tmp;
    }

    public List<ShipAPI> getWings(ShipAPI ship){
        List<ShipAPI> result = new ArrayList<ShipAPI>();
        for(FighterWingAPI wing :ship.getAllWings())result.addAll(wing.getWingMembers());
        return result;
    }

    public void teleport(ShipAPI ship,Vector2f pos,ShipAPI TargetShip){
        ship.getLocation().set(pos.x,pos.y);
        if(TargetShip!=null&&TargetShip!=ship){
            Vector2f pos2=TargetShip.getLocation();
            ship.setFacing((float)Math.toDegrees(Math.atan2(pos2.y-pos.y,pos2.x-pos.x)));
        }
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
        if(state.equals(State.IN)){
            target=null;
            finish=false;
            if(player)engine.maintainStatusForPlayerShip(STATUSKEY1,ship.getSystem().getSpecAPI().getIconSpriteName(), ship.getSystem().getDisplayName(),"loading", false);
        }
        if(state.equals(State.ACTIVE)){
            getTarget(ship);
            if(target==null)return;
            if(player)engine.maintainStatusForPlayerShip(STATUSKEY1,ship.getSystem().getSpecAPI().getIconSpriteName(), ship.getSystem().getDisplayName(),String.format("target: [ %s-级 ] %s",target.getHullSpec().getHullName() ,target.getName()), false);
            return;
        }
        if(state.equals(State.OUT)){
            ship.setJitter(this, COLOR, 0.5f , 3, 5f,5f);    
            if(target==null)target=ship;
            target.setJitter(this, COLOR, 0.5f , 3, 5f,5f);
            if(!finish){
                teleport(target,ship.getMouseTarget(),ship.getShipTarget());
                finish=true;
            }
        }
        if(state.equals(State.COOLDOWN))return;
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        return true;
    }

    @Override
    public void unapply(MutableShipStatsAPI stats,String id){
    }
}
