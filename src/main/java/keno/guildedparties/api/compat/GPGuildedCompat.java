package keno.guildedparties.api.compat;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.api.events.items.GuildItemEvents;
import keno.guildedparties.api.events.items.ItemListContainer;
import keno.guildedparties.impl.data.guilds.items.GuildItemList;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Random;

/** Example of using the common-side compat entrypoint */
public class GPGuildedCompat implements GuildedPlugin {
    @Override
    public void init() {
        //Wrap this in a if statement after testing
        if (GuildedParties.DEV_ENV) {
            GuildItemEvents.ADD.register(() -> new ItemListContainer(GuildedParties.GPLoc("miners_guild"),
                    new GuildItemList(List.of(Identifier.ofVanilla("diamond_pickaxe")))));
        }

        Random random = new Random();
        int num = random.nextInt(0, 9);
        switch (num) {
            case 0 -> GuildedParties.LOGGER.info("This is this, and that is that");
            case 1 -> GuildedParties.LOGGER.info("Watashi no namae wa Kira Yoshikage");
            case 2 -> GuildedParties.LOGGER.info("The Future is Liminal");
            case 3 -> GuildedParties.LOGGER.info("Also check out: PlayerEx");
            case 4 -> GuildedParties.LOGGER.info("GALLOP FORTH, ROCINANTE!!!");
            case 5 -> GuildedParties.LOGGER.info("Godspeed, you magnificent bastard");
            case 6 -> GuildedParties.LOGGER.info("A genuine thank you to those who brought me here");
            case 7 -> GuildedParties.LOGGER.info("'This world's order shall be perfected' - unknown");
            case 8 -> GuildedParties.LOGGER.info("I will make you suffer, just as I have (from arthritis)");
        }
    }
}
