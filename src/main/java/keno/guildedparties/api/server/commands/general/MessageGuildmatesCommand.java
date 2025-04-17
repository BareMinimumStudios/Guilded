package keno.guildedparties.api.server.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.networking.GPNetworking;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MessageGuildmatesCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            if (GPNetworking.doesPlayerHaveMemberData(player)) {
                boolean GCisToggled = GPComponents.GC_KEY.get(player).isToggled();
                GPComponents.GC_KEY.get(player).setToggle(!GCisToggled);
                player.sendMessageToClient(Text.of("Send future messages to guild chat: %b".formatted(!GCisToggled)), true);
                return 1;
            } else {
                player.sendMessageToClient(Text.of("You aren't in a guild"), true);
            }
        }
        return 0;
    }
}
