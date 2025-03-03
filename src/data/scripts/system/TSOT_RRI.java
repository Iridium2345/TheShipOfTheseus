package data.scripts.system;

import java.awt.Color;
import java.util.Random;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;


public class TSOT_RRI extends BaseShipSystemScript{
    
    public static final Color COLOR = new Color(240,240,240,120);
    public static final String WPN_ID = "TSOT_Support";
    public static final Random random = new Random(System.nanoTime());
    private boolean spawn = false;
    private final float genYPos = 11000f;

    public static enum Status {
        PLAYER("Support Fire: Deployed", "graphics/icons/hullsys/active_flare_launcher.png",false),
        ENEMY("Support Fire: Incoming", "graphics/icons/mission_marker.png",true);

        protected final String title;
        protected final String sprite;
        protected final boolean debuff;
        private Status(String title,String sprite,boolean debuff){
            this.title = title;
            this.sprite = sprite;
            this.debuff = debuff;
        }
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        if (!(stats.getEntity() instanceof ShipAPI))return;
        final ShipAPI ship = (ShipAPI)stats.getEntity();
        final CombatEngineAPI engine = Global.getCombatEngine();
        switch (state) {
            case IN:{
                spawn = false;
                break;
            }
            case ACTIVE:{
                if (!spawn) {
                    final int owner = ship.getOwner() == 1 ? 1 : -1;
                    final Vector2f spawnPos = new Vector2f(100f*(random.nextFloat() - .5f) , genYPos * owner);
                    final float genFacing = -90 * owner + 30f*(random.nextFloat()-.5f);
                    engine.spawnProjectile(
                        ship,
                        null,
                        WPN_ID,
                        spawnPos, 
                        genFacing, 
                        null
                    );
                    spawn = true;
                    final Status status = owner == -1 ? Status.PLAYER : Status.ENEMY;
                    engine.maintainStatusForPlayerShip(
                        status, status.sprite, status.title , ship.getSystem().getDisplayName() , status.debuff);
                }
                break;
            }
            case OUT:{
                break;
            }
            default:
                
                break;
        }
    }
}
