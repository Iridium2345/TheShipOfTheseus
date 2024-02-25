package data.econ;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;

public class ResearchStation extends BaseMarketConditionPlugin implements MarketImmigrationModifier {
    private IntervalUtil Interval = new IntervalUtil(1f,30f);
    
    public ResearchStation() {
    }

    public void apply(String id) {
        this.market.addTransientImmigrationModifier(this);
        this.market.addTransientImmigrationModifier(this);
    }
    
    public void unapply(String id) {
        this.market.removeTransientImmigrationModifier(this);
        this.market.removeTransientImmigrationModifier(this);
    }

    @Override
    public void advance(float amount) {
        Interval.advance(amount);
        if(Interval.intervalElapsed()){
            if(market.getCondition("pather_cells")!=null){
                market.removeCondition("pather_cells");
            }
        }
    }

    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.getWeight().modifyMultAlways(this.getModId(), 0f, "研究站");
    }
}