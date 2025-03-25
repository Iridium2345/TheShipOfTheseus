package data.industries;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

public class PhaseRing extends BaseIndustry {

	private static final String SUBMARKET = "storage";

	private static final Random random = new Random();

	private static final Map<String,Integer[]> weight = new HashMap<>();

	private static final Map<String,Integer> num = new HashMap<>();

	private static final Map<String,Float> day = new HashMap<>();

	private static final Integer[] DEFAULT_WEIGHT = new Integer[]{70,25,5,0};

	private static final String[] name = new String[]{
		Commodities.GAMMA_CORE,
		Commodities.BETA_CORE,
		Commodities.ALPHA_CORE,
		Commodities.OMEGA_CORE
	};

	static {
		num.put(Commodities.GAMMA_CORE, 1);
		num.put(Commodities.BETA_CORE, 2);
		num.put(Commodities.ALPHA_CORE, 3);
		num.put(Commodities.OMEGA_CORE, 4);
		day.put(Commodities.GAMMA_CORE, 1.5f);
		day.put(Commodities.BETA_CORE, .2f);
		day.put(Commodities.ALPHA_CORE, 3f);
		day.put(Commodities.OMEGA_CORE, 4f);
		weight.put(Commodities.GAMMA_CORE , new Integer[]{50,40,10,0});
		weight.put(Commodities.BETA_CORE  , new Integer[]{40,40,20,0});
		weight.put(Commodities.ALPHA_CORE , new Integer[]{30,40,25,5});
		weight.put(Commodities.OMEGA_CORE , new Integer[]{20,20,50,10});
	}

	public void apply() {
		super.apply(true);
		int size = market.getSize();
		
		demand("TSOT_Quantize_Matter", size);

		supply(Commodities.METALS, (size+1)*2+2);
		supply(Commodities.RARE_METALS, (size+1)*2+2);
		supply(Commodities.ORGANICS, size*2+1);
		supply(Commodities.VOLATILES, size*2+2);
		supply(Commodities.FOOD,size + 1);
		supply(Commodities.SHIPS,size + 2);

		market.getAccessibilityMod().modifyMult(getId(), 1.2f ,"PhaseRing");
		market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getId(), .5f , "PhaseRing");

		if (!isFunctional()) {
			supply.clear();
		}
	}

	
	@Override
	public void unapply() {
		market.getAccessibilityMod().unmodify(getId());
		market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodify(getId());
		super.unapply();
	}
	
	protected float runtime = 30f;
	protected static final float production_day_taken = 30f; 
	@Override
	public void advance(float amount) {
		
		float x = Global.getSector().getClock().convertToDays(amount);
		String s = Optional.ofNullable(getAICoreId()).orElse("null");
		int num = PhaseRing.num.getOrDefault(s, 1);
		x *= day.getOrDefault(s, 1f);
		
		Integer[] w = DEFAULT_WEIGHT;
		w = weight.getOrDefault(s, DEFAULT_WEIGHT);
		runtime += x;
		if (runtime > production_day_taken) {
			runtime -= production_day_taken;
			if (!market.isPlayerOwned()) return;
			final SubmarketAPI subMarket = market.getSubmarket(SUBMARKET);
			for (int i = 0; i < random.nextInt(1,num); i++)
				subMarket.getPlugin().addAllCargo(getCargo(w));
		}
	}

	private static CargoAPI getCargo(Integer[] w) {
		final CargoAPI cargo = Global.getFactory().createCargo(false);
		final int a = random.nextInt(0,100);
		int tmp = 0;
		for (int i = 0; i < w.length; i++) {
			tmp += w[i];
			if (a < tmp) {
				cargo.addCommodity(name[i], 1f);
				return cargo;
			}
		}
		return cargo;
	}


	@Override
	public String getCurrentImage() {
		return super.getCurrentImage();
	}


	public boolean isDemandLegal(CommodityOnMarketAPI com) {
		return true;
	}

	public boolean isSupplyLegal(CommodityOnMarketAPI com) {
		return true;
	}

//	@Override
//	public List<InstallableIndustryItemPlugin> getInstallableItems() {
//		ArrayList<InstallableIndustryItemPlugin> list = new ArrayList<InstallableIndustryItemPlugin>();
//		list.add(new GenericInstallableItemPlugin(this));
//		return list;
//	}

	@Override
	protected boolean canImproveToIncreaseProduction() {
		return true;
	}

    @Override
	public boolean isAvailableToBuild() {
		if (!super.isAvailableToBuild()) return false;
		return market.getPrimaryEntity().getMemoryWithoutUpdate().get("$usable")!=null;
	}

    @Override
	public String getUnavailableReason() {
		if (!super.isAvailableToBuild()) return super.getUnavailableReason();
		
		return "必须在激活的星冕分流器上建造";
	}
}
