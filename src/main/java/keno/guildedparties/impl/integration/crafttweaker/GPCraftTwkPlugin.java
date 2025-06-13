package keno.guildedparties.impl.integration.crafttweaker;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.plugin.CraftTweakerPlugin;
import com.blamejared.crafttweaker.api.plugin.ICraftTweakerPlugin;
import keno.guildedparties.GuildedParties;

@CraftTweakerPlugin(GuildedParties.MOD_ID + ":ct_plugin")
public class GPCraftTwkPlugin implements ICraftTweakerPlugin {
    @Override
    public void initialize() {
        ICraftTweakerPlugin.super.initialize();
        GuildedParties.LOGGER.info("CraftTweaker detected! Loading integration");
    }
}
*///?}

//? if > 1.21.1
public class GPCraftTwkPlugin {}

