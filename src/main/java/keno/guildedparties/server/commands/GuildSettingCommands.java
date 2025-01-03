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
    public static int changeGuildPrivacySetting(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (player == null) return 0;

        if (checkPlayerPermissions(player)) {
            Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            GuildSettings settings = state.getSettings(member.getGuildKey());
            boolean isPrivate = context.getArgument("isPrivate", Boolean.class);
            state.addSettings(new GuildSettings(isPrivate,
                    settings.managePlayerRankPriority(),
                    settings.managePlayerPriority(),
                    settings.manageGuildPriority(),
                    settings.invitePlayersPriority(),
                    settings.hasCustomTextures()), member.getGuildKey());
            state.markDirty();
            String accessibility = isPrivate ? "private" : "public";
            player.sendMessageToClient(Text.of("Your guild is now " + accessibility), true);
            return 1;
        }
        return 0;
    }

    public static int changeManagePlayerRankPriority(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = source.getServer();
        StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
        if (player == null) return 0;

        if (checkPlayerPermissions(player)) {
            Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            GuildSettings settings = state.getSettings(member.getGuildKey());
            int managePlayerRankPriority = context.getArgument("managePlayerRankPriority", Integer.class);
            state.addSettings(new GuildSettings(settings.isPrivate(),
                    managePlayerRankPriority,
                            settings.managePlayerPriority(),
                            settings.manageGuildPriority(),
                            settings.invitePlayersPriority(),
                            settings.hasCustomTextures()),
                    member.getGuildKey());
            state.markDirty();
            player.sendMessageToClient(Text.of("Priority to manage player ranks is now %d".formatted(managePlayerRankPriority)), true);
            return 1;
        }
        return 0;
    }

    private static boolean checkPlayerPermissions(ServerPlayerEntity player) {
        if (player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            Member member = player.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
            if (member.getRank().isCoLeader()) {
                return true;
            } else {
                player.sendMessageToClient(Text.of("Must be the guild leader"), true);
            }
        }
        player.sendMessageToClient(Text.of("You aren't in a guild"), true);
        return false;
    }
}
