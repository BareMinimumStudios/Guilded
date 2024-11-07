package keno.guildedparties.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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

@SuppressWarnings({"UnstableApiUsage"})
public class GuildCreationCommands {
    public static int createGuildRankCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        ServerPlayerEntity sender = source.getPlayer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (!sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            sender.sendMessageToClient(Text.of("You aren't in a guild"), true);
            return 0;
        }
        Member senderData = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
        if (senderData.rank().priority() <= 3) {
            String rankName = StringArgumentType.getString(context, "rankName");
            int rankPriority = IntegerArgumentType.getInteger(context, "rankPriority");
            if (rankPriority > senderData.rank.priority()) {
                Rank rank = new Rank(rankName, rankPriority);
                int status = state.guilds.get(senderData.guildKey()).addRank(rank);
                if (status == 1) {
                    sender.sendMessageToClient(Text.of("Rank added successfully!"), true);
                } else if (status == 0) {
                    sender.sendMessageToClient(Text.of("Can only add ranks lowers than yours"), true);
                }
                return status;
            }
        } else {
            sender.sendMessageToClient(Text.of("Your rank is too low priority"), true);
        }
        return 0;
    }
}
