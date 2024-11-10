package keno.guildedparties.server.commands.invites;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class InviteResponseCommands {
    public static int declineInviteCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            if (player.hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
                player.removeAttached(GPAttachmentTypes.INVITE_ATTACHMENT);
                player.sendMessageToClient(Text.of("Invite has been declined successfully"), true);
                return 1;
            } else {
                player.sendMessageToClient(Text.of("There's no invite to decline"), true);
            }
        }
        return 0;
    }
}
