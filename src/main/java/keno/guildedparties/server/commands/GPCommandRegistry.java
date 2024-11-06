package keno.guildedparties.server.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import keno.guildedparties.server.commands.suggestions.GuildSuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GPCommandRegistry {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            LiteralCommandNode<ServerCommandSource> leaveGuildNode = CommandManager
                    .literal("leave_guild")
                    .executes(new LeaveGuildCommand())
                    .build();

            LiteralCommandNode<ServerCommandSource> viewPlayerGuildNode = CommandManager
                    .literal("view_player_guild")
                    .executes(ViewPlayerGuildCommand::viewCallerGuild)
                    .build();

            LiteralCommandNode<ServerCommandSource> joinGuildNode = CommandManager
                    .literal("join_guild")
                    .build();

            CommandNode<ServerCommandSource> guildNameNode = CommandManager
                    .argument("guild_name", StringArgumentType.string())
                    .suggests(new GuildSuggestionProvider())
                    .executes(new JoinGuildCommand())
                    .build();

            // "/leave_guild" command
            commandDispatcher.getRoot().addChild(leaveGuildNode);

            // "/view_player_guild" command
            commandDispatcher.getRoot().addChild(viewPlayerGuildNode);

            // "/join_guild" command
            commandDispatcher.getRoot().addChild(joinGuildNode);
            joinGuildNode.addChild(guildNameNode);
        });
    }
}
