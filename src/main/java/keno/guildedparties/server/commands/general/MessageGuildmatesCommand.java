package keno.guildedparties.server.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.server.StateSaverAndLoader;
import keno.guildedparties.utils.GuildUtils;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MessageGuildmatesCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity player = source.getPlayer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (player != null) {
            if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                Guild guild = state.guilds.get(player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT).guildKey());
                GuildUtils.broadcastToGuildmates(server, guild, MessageArgumentType.getMessage(context, "message").getLiteralString(), player);
                return 1;
            } else {
                player.sendMessageToClient(Text.of("You aren't in a guild"), true);
            }
        }
        return 0;
    }
}
