package data.econ;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class ResearchStation extends BaseMarketConditionPlugin implements MarketImmigrationModifier {
    private IntervalUtil Interval = new IntervalUtil(1f,30f);
    
    public ResearchStation() {
    }

    public void apply(String id) {
        this.market.addTransientImmigrationModifier(this);
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(id, 2f);
    }
    
    public void unapply(String id) {
        this.market.removeTransientImmigrationModifier(this);
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(id);
    }

    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.getWeight().modifyMultAlways(this.getModId(), 1.2f, "吸引游客前来观光");
    }


}