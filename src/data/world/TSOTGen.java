package data.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class TSOTGen {
    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Hermit");
        system.addTag(Tags.THEME_CORE_POPULATED);
        system.getLocation().set(5000f,-5000f);
        PlanetAPI star = system.initStar("TSOT_Hermit",
        "star_blue_supergiant",
        800f,
        600f);
        system.setLightColor(new Color(230, 200, 230));

        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.gray, 400f, 1100f, 150f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.gray, 400f, 1400f, 100f);

        List<String> CustomEntity = Arrays.asList(
        "comm_relay",
        "nav_buoy",
        "sensor_array",
        "inactive_gate"
        );

        for(int i=0;i<4;i++){
                system.addCustomEntity("TSOS_SL_"+i,null,CustomEntity.get(i),null).setCircularOrbitPointingDown(star, i*90, 1600, -120);
        }
        
        CustomCampaignEntityAPI station_1 = system.addCustomEntity("TSOT_station_1", "研究站 - alpha", "TSOT_coronal_tap", null);
        station_1.setCircularOrbitPointingDown(star,0,2000,150);
        station_1.getMemory().set("$usable", true);
        
        CustomCampaignEntityAPI station_2 = system.addCustomEntity("TSOT_station_2", "研究站 - beta", "TSOT_coronal_tap", null);
        station_2.setCircularOrbitPointingDown(star,90,2000,150);
        station_2.getMemory().set("$usable", true);

        CustomCampaignEntityAPI station_3 = system.addCustomEntity("TSOT_station_3", "研究站 - gamma", "TSOT_coronal_tap", null);
        station_3.setCircularOrbitPointingDown(star,180,2000,150);
        station_3.getMemory().set("$usable", true);

        CustomCampaignEntityAPI station_4 = system.addCustomEntity("TSOT_station_4", "研究站 - omega", "TSOT_coronal_tap", null);
        station_4.setCircularOrbitPointingDown(star,270,2000,150);
        station_4.getMemory().set("$usable", true);

        PlanetAPI planet = system.addPlanet("TSOT_IX",
                star,
                "IX",
                "terran",
                125f,
                240,
                3200,
                125);
        
        StarSystemAPI sys2 = Global.getSector().getStarSystem("aztlan");
        
        PlanetAPI planet2 = sys2.addPlanet("TSOT_1",sys2.getStar(),"XII","terran",100f,285,3500,155);
        PlanetAPI planet3 = sys2.addPlanet("TSOT_2",planet2,"XII","terran",0f,80,800,20);
        PlanetAPI planet4 = sys2.addPlanet("TSOT_3",planet2,"XIV","terran",120f,80,800,20);
        PlanetAPI planet5 = sys2.addPlanet("TSOT_4",planet2,"XVI","terran",240f,80,800,20);
        boolean tmp = true;
        Queue<String> industries = new ConcurrentLinkedQueue<>(Arrays.asList(Industries.LIGHTINDUSTRY,Industries.FUELPROD,Industries.MINING,Industries.REFINING));
        for (PlanetAPI planetx : Arrays.asList(planet2,planet3,planet4,planet5)) {
            MarketAPI market1 = addMarketplace("TSOT_Hermit", planetx, null,
            "IX",
            6,
            new ArrayList<>(
                    Arrays.asList(
                            Conditions.POPULATION_6,
                            Conditions.SOLAR_ARRAY,
                            Conditions.FARMLAND_BOUNTIFUL,
                            Conditions.REGIONAL_CAPITAL,
                            Conditions.ORE_RICH,
                            Conditions.RARE_ORE_RICH,
                            Conditions.ORGANICS_ABUNDANT,
                            Conditions.VOLATILES_ABUNDANT
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            Submarkets.SUBMARKET_OPEN,
                            Submarkets.GENERIC_MILITARY,
                            Submarkets.SUBMARKET_BLACK,
                            Submarkets.SUBMARKET_STORAGE
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            Industries.POPULATION,
                            Industries.MEGAPORT,
                            Industries.FARMING,
                            Industries.HEAVYBATTERIES,
                            Industries.HIGHCOMMAND,
                            industries.poll(),
                            Industries.WAYSTATION,
                            "TSOT_starfortress"
                    )
            ),
            0.1f,
            true,
            true);
            if (tmp) market1.addIndustry(Industries.ORBITALWORKS,Arrays.asList(Items.PRISTINE_NANOFORGE));
            market1.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);
            market1.getIndustry(Industries.ORBITALWORKS).setAICoreId(Commodities.ALPHA_CORE);
        }
       

        // MarketAPI market2 = addMarketplace("TSOT_Hermit", station_1, null,
        //         "研究站 - 星币",
        //         4,
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Conditions.POPULATION_4,
        //                         "TSOT_ResearchStation"
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Submarkets.SUBMARKET_OPEN,
        //                         Submarkets.SUBMARKET_BLACK,
        //                         Submarkets.SUBMARKET_STORAGE
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Industries.MEGAPORT,
        //                         Industries.STARFORTRESS_HIGH,
        //                         Industries.HEAVYBATTERIES,
        //                         Industries.PATROLHQ,
        //                         Industries.WAYSTATION,
        //                         "TSOT_Reverse_Spiral",
        //                         "TSOT_Phase_Ring",
        //                         "TSOT_starfortress"
        //                 )
        //         ),
        //         0.1f,
        //         false,
        //         true);
        // market2.addIndustry(Industries.POPULATION,Arrays.asList(Items.CORONAL_PORTAL));
        // market2.getIndustry(Industries.POPULATION).setAICoreId(Commodities.ALPHA_CORE);
        // market2.addIndustry(Industries.LIGHTINDUSTRY);
        // market2.getIndustry(Industries.LIGHTINDUSTRY).setAICoreId(Commodities.ALPHA_CORE);

        // MarketAPI market3 = addMarketplace("TSOT_Hermit", station_2, null,
        //         "研究站 - 圣杯",
        //         4,
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Conditions.POPULATION_4,
        //                         "TSOT_ResearchStation"
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Submarkets.SUBMARKET_OPEN,
        //                         Submarkets.SUBMARKET_BLACK,
        //                         Submarkets.SUBMARKET_STORAGE
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Industries.MEGAPORT,
        //                         Industries.STARFORTRESS_HIGH,
        //                         Industries.HEAVYBATTERIES,
        //                         Industries.PATROLHQ,
        //                         Industries.WAYSTATION,
        //                         "TSOT_Reverse_Spiral",
        //                         "TSOT_Phase_Ring",
        //                         "TSOT_starfortress"
        //                 )
        //         ),
        //         0.1f,
        //         false,
        //         true);
        // market3.addIndustry(Industries.POPULATION,Arrays.asList(Items.CORONAL_PORTAL));
        // market3.getIndustry(Industries.POPULATION).setAICoreId(Commodities.ALPHA_CORE);
        // market3.addIndustry(Industries.ORBITALWORKS,Arrays.asList(Items.PRISTINE_NANOFORGE));
        // market3.getIndustry(Industries.ORBITALWORKS).setAICoreId(Commodities.ALPHA_CORE);

        // MarketAPI market4 = addMarketplace("TSOT_Hermit", station_3, null,
        //         "研究站 - 权杖",
        //         4,
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Conditions.POPULATION_4,
        //                         "TSOT_ResearchStation"
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Submarkets.SUBMARKET_OPEN,
        //                         Submarkets.SUBMARKET_BLACK,
        //                         Submarkets.SUBMARKET_STORAGE
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Industries.MEGAPORT,
        //                         Industries.STARFORTRESS_HIGH,
        //                         Industries.HEAVYBATTERIES,
        //                         Industries.PATROLHQ,
        //                         Industries.WAYSTATION,
        //                         "TSOT_Reverse_Spiral",
        //                         "TSOT_Phase_Ring",
        //                         "TSOT_starfortress"
        //                 )
        //         ),
        //         0.1f,
        //         false,
        //         true);
        // market4.addIndustry(Industries.POPULATION,Arrays.asList(Items.CORONAL_PORTAL));
        // market4.getIndustry(Industries.POPULATION).setAICoreId(Commodities.ALPHA_CORE);
        // market4.addIndustry(Industries.FUELPROD);
        // market4.getIndustry(Industries.FUELPROD).setAICoreId(Commodities.ALPHA_CORE);

        // MarketAPI market5 = addMarketplace("TSOT_Hermit", station_4, null,
        //         "研究站 - 利剑",
        //         4,
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Conditions.POPULATION_4,
        //                         "TSOT_ResearchStation"
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Submarkets.SUBMARKET_OPEN,
        //                         Submarkets.SUBMARKET_BLACK,
        //                         Submarkets.SUBMARKET_STORAGE
        //                 )
        //         ),
        //         new ArrayList<>(
        //                 Arrays.asList(
        //                         Industries.MEGAPORT,
        //                         Industries.STARFORTRESS_HIGH,
        //                         Industries.HEAVYBATTERIES,
        //                         Industries.WAYSTATION,
        //                         "TSOT_Reverse_Spiral",
        //                         "TSOT_Phase_Ring",
        //                         "TSOT_starfortress"
        //                 )
        //         ),
        //         0.1f,
        //         false,
        //         true);
        // market5.addIndustry(Industries.POPULATION,Arrays.asList(Items.CORONAL_PORTAL));
        // market5.getIndustry(Industries.POPULATION).setAICoreId(Commodities.ALPHA_CORE);
        // market5.addIndustry(Industries.HIGHCOMMAND);
        // market5.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);
        
        JumpPointAPI jumpPointCharkha = Global.getFactory().createJumpPoint("TSOT_IX_jp", "The Fool's Road");
        jumpPointCharkha.setCircularOrbit(planet, 165f, 600, 240);
        jumpPointCharkha.setRelatedPlanet(planet);
        system.addEntity(jumpPointCharkha);
        system.autogenerateHyperspaceJumpPoints(true, true);
        system.generateAnchorIfNeeded();

    }
    
    public static MarketAPI addMarketplace(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name,
                                           int size, ArrayList<String> marketConditions, ArrayList<String> submarkets, ArrayList<String> industries, float tarrif,
                                           boolean freePort, boolean withJunkAndChatter) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", tarrif);

        //Adds submarkets
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //Adds market conditions
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //Add market industries
        for (String industry : industries) {
            newMarket.addIndustry(industry);
        }

        //Sets us to a free port, if we should
        newMarket.setFreePort(freePort);

        //Adds our connected entities, if any
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        globalEconomy.addMarket(newMarket, withJunkAndChatter);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);

        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        //Finally, return the newly-generated market
        return newMarket;
    }

}
