package keno.guildedparties.compat;

import keno.guildedparties.GuildedParties;

import java.util.Random;

/** Example of using the common-side compat entrypoint */
public class GPGuildedCompat implements GuildedCompatEntrypoint {
    @Override
    public void init() {
        Random random = new Random();
        int num = random.nextInt(0, 10);
        switch (num) {
            case 0 -> GuildedParties.LOGGER.info("This is this, and that is that");
            case 1 -> GuildedParties.LOGGER.info("Also check out: RoA Ruinous Call");
            case 2 -> GuildedParties.LOGGER.info("Watashi no namae wa Kira Yoshikage");
            case 3 -> GuildedParties.LOGGER.info("The Future is Liminal");
            case 4 -> GuildedParties.LOGGER.info("Also check out: PlayerEx");
            case 5 -> GuildedParties.LOGGER.info("GALLOP FORTH, ROCINANTE!!!");
            case 6 -> GuildedParties.LOGGER.info("Godspeed, you magnificent bastard");
            case 7 -> GuildedParties.LOGGER.info("A genuine thank you to those who brought me here");
            case 8 -> GuildedParties.LOGGER.info("'This world's order shall be perfected' - unknown");
            case 9 -> GuildedParties.LOGGER.info("I will make you suffer, just as I have (from arthritis)");
        }
    }
}
