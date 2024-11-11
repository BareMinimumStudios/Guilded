package keno.guildedparties.server.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class JoinGuildCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String guild_name = StringArgumentType.getString(context, "guildName");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) return 0;

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            MinecraftServer server = source.getServer();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            if (state.banLists.get(guild_name).isPlayerBanned(player.getUuid())) {
                player.sendMessageToClient(Text.of("You are banned from this guild"), true);
                return 0;
            }

            if (state.guilds.containsKey(guild_name)) {
                if (!state.guildSettingsMap.get(guild_name).isPrivate()) {
                    if (!state.guilds.get(guild_name).getPlayers().containsKey(player.getUuid())) {
                        state.guilds.get(guild_name).addPlayerToGuild(player, "Recruit");
                        player.sendMessageToClient(Text.of("Successfully joined guild!"), true);
                        return 1;
                    }
                } else {
                    player.sendMessageToClient(Text.of("This guild is private, you must be invited"), true);
                }
            } else {
                player.sendMessageToClient(Text.of("This guild does not exist..."), true);
            }
        } else {
            player.sendMessageToClient(Text.of("You are already in a guild..."), true);
        }
        return 0;
    }
}
