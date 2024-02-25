package data.industries;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

public class ReverseSpiral extends BaseIndustry {

	public void apply() {
		super.apply(true);
		int size = market.getSize();
		
		demand(Commodities.HEAVY_MACHINERY, size - 1);
		
		supply("TSOT_Quantize_Matter", size*2-4);
		
		if (!isFunctional()) {
			supply.clear();
		}
	}

	
	@Override
	public void unapply() {
		super.unapply();
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
