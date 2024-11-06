package keno.guildedparties.server.commands;

import com.mojang.brigadier.Command;
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
public class JoinGuildCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String guild_name = StringArgumentType.getString(context, "guild_name");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (!player.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            MinecraftServer server = source.getServer();
            StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(server);
            if (state.guilds.containsKey(guild_name)) {
                if (!state.guilds.get(guild_name).players.containsKey(player.getUuid())) {
                    Rank recruit = new Rank("Recruit", 50);
                    state.guilds.get(guild_name).players.put(player.getUuid(), recruit);
                    player.setAttached(GPAttachmentTypes.MEMBER_ATTACHMENT, new Member(guild_name, recruit));
                    player.sendMessageToClient(Text.of("Successfully joined guild!"), true);
                }
            } else {
                player.sendMessageToClient(Text.of("This guild does not exist..."), true);
            }
        } else {
            player.sendMessageToClient(Text.of("You are already in a guild..."), true);
        }
        return 1;
    }
}
