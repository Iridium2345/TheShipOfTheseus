package data;

import data.world.TSOTGen;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
//import exerelin.campaign.SectorManager;

public class TSOTPlugin extends BaseModPlugin{
    @Override
    public void onNewGame() {
        //boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        //if (!haveNexerelin || SectorManager.getManager().isCorvusMode()){
            new TSOTGen().generate(Global.getSector());
        //}

    }
}

