package keno.guildedparties.server.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Rank;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class ViewPlayerGuildCommand {
    public static int viewCallerData(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            return viewGuild(source, player);
        }
        return 0;
    }

    public static int viewPlayerData(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(StringArgumentType.getString(context, "player"));
        if (player != null) {
            return viewGuild(source, player);
        }
        return 0;
    }

    private static int viewGuild(ServerCommandSource source, ServerPlayerEntity player) {
        if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            MinecraftServer server = source.getServer();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

            // Get player data
            Member playerData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            Rank rank = playerData.rank();

            // Get variables for message
            String guild_name = playerData.guildKey();
            String rank_name = rank.name();
            int members = state.guilds.get(guild_name).getPlayers().size();
            boolean isCoLeader = rank.isCoLeader();

            String response = "Guild: %s, Members: %d, Rank: %s, CoLeader: %b".formatted(guild_name, members, rank_name, isCoLeader);
            player.sendMessageToClient(Text.of(response), true);
        } else {
            player.sendMessageToClient(Text.of("You are not in a guild"), true);
        }
        return 1;
    }
}
