package keno.guildedparties.server.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.GuildSettings;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@SuppressWarnings("UnstableApiUsage")
public class GuildSettingCommands {
    public static int changeGuildAccessSetting(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (player == null) return 0;

        if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            if (member.rank().isCoLeader()) {
                GuildSettings settings = state.guildSettingsMap.get(member.guildKey());
                boolean isPrivate = context.getArgument("isPrivate", Boolean.class);
                state.guildSettingsMap.put(member.guildKey(), new GuildSettings(isPrivate,
                        settings.managePlayerRankPriority(),
                        settings.managePlayerPriority(),
                        settings.manageGuildPriority(),
                        settings.invitePlayersPriority()));
                String accessibility = isPrivate ? "private" : "public";
                player.sendMessageToClient(Text.of("Your guild is now " + accessibility), true);
                return 1;
            } else {
                player.sendMessageToClient(Text.of("Must be the Leader of the guild"), true);
            }
        } else {
            player.sendMessageToClient(Text.of("You must be in a guild"), true);
        }

        return 0;
    }
}
