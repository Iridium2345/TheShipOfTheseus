package data.item;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import data.hullmods.TSOTAgreement;

public class TSOTAgreementPack extends BaseSpecialItemPlugin{

    public static final Color COLOR = new Color(255,255,255);

    @Override
    public String getDesignType() {
        return null;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float pad = 10f;
        tooltip.addTitle(getName());
        tooltip.addPara("包含了全部 TSOT 协议", COLOR, pad);
    }

    @Override
    public float getTooltipWidth() {
        return super.getTooltipWidth();
    }

    @Override
    public boolean isTooltipExpandable() {
        return false;
    }

    @Override
    public boolean hasRightClickAction() {
        return true;
    }

    @Override
    public boolean shouldRemoveOnRightClickAction() {
        return true;
    }

    @Override
    public void performRightClickAction() {
        for(String itemId:TSOTAgreement.SubMods){
            Global.getSector().getPlayerFleet().getCargo().addHullmods(itemId, 1);
        }
    }
}
