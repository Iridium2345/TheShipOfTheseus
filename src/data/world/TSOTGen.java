package data.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;

public class TSOTGen {
    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Hermit");
        system.addTag(Tags.THEME_CORE_POPULATED);
        system.getLocation().set(5000f,-5000f);
        PlanetAPI star = system.initStar("TSOT_Hermit",
        "star_blue_supergiant",
        600f,
        1200f);
        system.setLightColor(new Color(230, 200, 230));

        system.addRingBand(star, "misc", "rings_dust0", 256f, 2, Color.gray, 400f, 1600f, 200f);
        system.addRingBand(star, "misc", "rings_dust0", 256f, 3, Color.gray, 400f, 1800f, 200f);
        PlanetAPI planet = system.addPlanet("TSOT_IX",
                star,
                "IX",
                "terran",
                125f,
                240,
                3600,
                125);
        
        PlanetAPI moon1 = system.addPlanet("TSOT_IX_I",
                planet,
                "I - IX",
                "barren",
                125f,
                66,
                1060,
                60);
        
        PlanetAPI moon2 = system.addPlanet("TSOT_IX_II",
                planet,
                "II - IX",
                "barren",
                125f,
                60,
                800,
                64);
        
        PlanetAPI moon3 = system.addPlanet("TSOT_IX_III",
                planet,
                "III - IX",
                "barren",
                125f,
                72,
                550,
                34);
        
        MarketAPI market1 = addMarketplace("TSOT_Hermit", planet, null,
                "IX",
                7,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_7,
                                Conditions.HABITABLE,
                                Conditions.MILD_CLIMATE,
                                Conditions.SOLAR_ARRAY,
                                Conditions.FARMLAND_BOUNTIFUL,
                                Conditions.REGIONAL_CAPITAL
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
                                Industries.STARFORTRESS_HIGH,
                                Industries.HEAVYBATTERIES,
                                Industries.HIGHCOMMAND,
                                Industries.LIGHTINDUSTRY,
                                Industries.FUELPROD,
                                Industries.WAYSTATION
                        )
                ),
                0.1f,
                true,
                true);
                MarketAPI market2 = addMarketplace("TSOT_Hermit", moon1, null,
                "I - IX",
                6,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_6
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
                                Industries.STARFORTRESS_HIGH,
                                Industries.HEAVYBATTERIES,
                                Industries.HIGHCOMMAND,
                                Industries.LIGHTINDUSTRY,
                                Industries.ORBITALWORKS,
                                Industries.WAYSTATION
                        )
                ),
                0.1f,
                true,
                true);
                MarketAPI market3 = addMarketplace("TSOT_Hermit", moon2, null,
                "II - IX",
                6,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_7
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
                                Industries.STARFORTRESS_HIGH,
                                Industries.HEAVYBATTERIES,
                                Industries.HIGHCOMMAND,
                                Industries.ORBITALWORKS,
                                Industries.FUELPROD,
                                Industries.WAYSTATION
                        )
                ),
                0.1f,
                true,
                true);
                MarketAPI market4 = addMarketplace("TSOT_Hermit", moon3, null,
                "III - IX",
                6,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_7
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
                                Industries.STARFORTRESS_HIGH,
                                Industries.HEAVYBATTERIES,
                                Industries.HIGHCOMMAND,
                                Industries.ORBITALWORKS,
                                Industries.FUELPROD,
                                Industries.WAYSTATION
                        )
                ),
                0.1f,
                true,
                true);
        
        JumpPointAPI jumpPointCharkha = Global.getFactory().createJumpPoint("TSOT_IX_jp", "The Fool's Road");
        jumpPointCharkha.setCircularOrbit(star, 165f, 3600, 240);
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
