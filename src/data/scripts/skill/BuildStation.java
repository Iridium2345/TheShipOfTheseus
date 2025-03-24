package data.scripts.skill;

import java.util.Arrays;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;

public class BuildStation extends BaseDurationAbility {

    protected void cleanupImpl() {}
    protected void activateImpl() {
        final StarSystemAPI system = getEntity().getStarSystem();
        for(SectorEntityToken entity : system.getEntitiesWithTag("coronal_tap")) {
            if (entity.getFaction().getId().equals(Factions.PLAYER)) continue;
            final MarketAPI newMarket = Global.getFactory().createMarket(entity.getId()+"_market", entity.getName() , 3);
            entity.setFaction(Factions.PLAYER);
            newMarket.setFactionId(Factions.PLAYER);

            newMarket.setPrimaryEntity(entity);
            entity.setMarket(newMarket);

            newMarket.addSubmarket("local_resources");
            newMarket.addSubmarket("storage");

            newMarket.addCondition("TSOT_ResearchStation");
            newMarket.addIndustry(Industries.POPULATION,Arrays.asList(Items.CORONAL_PORTAL));
            newMarket.addIndustry(Industries.SPACEPORT);
            newMarket.addIndustry("TSOT_Reverse_Spiral");
            newMarket.addIndustry("TSOT_Phase_Ring");
            
            newMarket.setPlayerOwned(true);

            final PersonAPI person = Global.getFactory().createPerson();
            person.setName(new FullName("Omega", "Core", Gender.ANY));
            person.setAICoreId(Commodities.OMEGA_CORE);
            person.setFaction(Factions.PLAYER);
            person.setPortraitSprite("graphics/portraits/characters/omega.png");
            final var states = person.getStats();
            states.setLevel(6);
            states.setSkillLevel("hypercognition", 1f);
            states.setSkillLevel("industrial_planning", 1f);

            newMarket.setAdmin(person);
            newMarket.addCondition(Conditions.AI_CORE_ADMIN);
            Global.getSector().getEconomy().addMarket(newMarket, false);
        }
    }

    protected void applyEffect(float amount, float level) {}
    protected void deactivateImpl() {}
}
