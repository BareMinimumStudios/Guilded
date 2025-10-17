package keno.guildedparties.api.compat;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.impl.data.guilds.Guild;
import keno.guildedparties.impl.data.guilds.GuildContainer;
import keno.guildedparties.impl.data.guilds.GuildSettings;
import keno.guildedparties.impl.data.guilds.Rank;

import java.util.List;
import java.util.Map;
import java.util.Random;

/** Example of using the common-side compat entrypoint */
public class GPGuildedCompat implements GuildedPlugin {
    @Override
    public void init() {
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
            case 7 -> GuildedParties.LOGGER.info("Answer me, Steve: 'what does The Overworld need?'");
            case 8 -> GuildedParties.LOGGER.info("I will make you suffer, just as I have (from arthritis)");
        }

        // A more practical example of using this entrypoint, the dev-env guilds here are used to test experimental features
        GuildedParties.registerDevEnvGuild("miners_guild_senmonten", new GuildContainer(
                () -> new Guild(
                        "Miners Guild",
                        Map.of("D1amonds4ever", new Rank("Netherite Miner", 1)),
                        List.of(new Rank("Netherite Miner", 1),
                                new Rank("Diamond Miner", 10),
                                new Rank("Golden Miner", 20),
                                new Rank("Iron Miner", 30),
                                new Rank("Copper Miner", 40),
                                new Rank("Stone Miner", 45),
                                new Rank("Wooden Miner", 49)),
                        "We specialize in the harvesting, refinement, and sale of ores mined from caverns within the Overworld and Nether. Join us if you excel at resource collection and/or enjoy a routine work-life."),
                () -> new GuildSettings(true,
                        10, 10, 1, 30,
                        false)
        ));

        GuildedParties.registerDevEnvGuild("warriors_guild_senmonten", new GuildContainer(
                () -> new Guild(
                        "Warriors Guild",
                        Map.of("Dr0g0nSl4yer", new Rank("Netherite Sword", 1)),
                        List.of(new Rank("Netherite Sword", 1),
                                new Rank("Diamond Sword", 10),
                                new Rank("Golden Sword", 20),
                                new Rank("Iron Sword", 30),
                                new Rank("Copper Sword", 40),
                                new Rank("Stone Sword", 45),
                                new Rank("Wooden Sword", 49)),
                        "'Fight, Kill, Win' is our motto. If you wish to fight for glory and honor, join the Warriors Guild for training, tournaments, and total-combat."),
                GuildSettings::getDefaultSettings
        ));
    }
}
