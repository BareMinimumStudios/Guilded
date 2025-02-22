package keno.guildedparties.server.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.GuildedParties;
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
        String guildName = StringArgumentType.getString(context, "guildName");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player == null) return 0;

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            GuildedParties.LOGGER.info("First checkpoint passed");
            MinecraftServer server = source.getServer();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            if (state.getBanlist(guildName).isPlayerBanned(player.getGameProfile().getName())) {
                player.sendMessageToClient(Text.of("You are banned from this guild"), true);
                return 0;
            }

            if (state.hasGuild(guildName)) {
                GuildedParties.LOGGER.info("Second checkpoint passed");
                if (!state.getSettings(guildName).isPrivate()) {
                    if (!state.getGuild(guildName).getPlayers().containsKey(player.getGameProfile().getName())) {
                        state.getGuild(guildName).addPlayerToGuild(player, "Recruit");
                        state.markDirty();
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
