package keno.guildedparties.server.commands.general;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class LeaveGuildCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ServerCommandSource source = commandContext.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            MinecraftServer server = source.getServer();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

            // Get the attached data and remove it
            Member playerData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            if (playerData.rank().priority() <= 1) {
                player.sendMessageToClient(Text.of("You can't leave since you're the leader"), true);
                return 0;
            }
            state.guilds.get(playerData.guildKey()).removePlayerFromGuild(player);
            player.sendMessageToClient(Text.of("Successfully left guild!"), true);
            return 1;
        } else {
            player.sendMessageToClient(Text.of("There is no guild to leave..."), true);
        }
        return 0;
    }
}
