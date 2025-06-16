package keno.guildedparties.impl.integration.crafttweaker;

//? if =1.21.1 {
/*import com.blamejared.crafttweaker.api.plugin.CraftTweakerPlugin;
import com.blamejared.crafttweaker.api.plugin.ICraftTweakerPlugin;
import com.blamejared.crafttweaker.api.plugin.IEventRegistrationHandler;
import keno.guildedparties.GuildedParties;

@CraftTweakerPlugin(GuildedParties.MOD_ID + ":ct_plugin")
public class GPCraftTwkPlugin implements ICraftTweakerPlugin {
    @Override
    public void initialize() {
        ICraftTweakerPlugin.super.initialize();
        GuildedParties.LOGGER.info("CraftTweaker detected! Loading integration");
    }

    //TODO expose events to CraftTweaker
    @Override
    public void registerEvents(IEventRegistrationHandler handler) {
        ICraftTweakerPlugin.super.registerEvents(handler);

    }
}
*///?}

//? if > 1.21.1
public class GPCraftTwkPlugin {}

