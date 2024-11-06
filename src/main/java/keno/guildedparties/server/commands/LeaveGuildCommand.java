package keno.guildedparties.server.commands;

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
public class LeaveGuildCommand implements Command<ServerCommandSource>{
    @Override
    public int run(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ServerCommandSource source = commandContext.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            MinecraftServer server = source.getServer();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);

            // Get the attached data and remove it
            Member playerData = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            state.guilds.get(playerData.guild_key()).players.remove(player.getUuid());
            player.removeAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            player.sendMessageToClient(Text.of("Successfully left guild!"), true);
            return 1;
        } else {
            player.sendMessageToClient(Text.of("There is no guild to leave..."), true);
        }
        return 0;
    }
}
