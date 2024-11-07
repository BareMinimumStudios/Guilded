package keno.guildedparties.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import keno.guildedparties.server.commands.suggestions.GuildSuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Consumer;

public class GPCommandRegistry {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            LiteralCommandNode<ServerCommandSource> guildRootNode = CommandManager
                    .literal("guilded")
                    .build();


            LiteralCommandNode<ServerCommandSource> leaveGuildNode = CommandManager
                    .literal("leave")
                    .executes(new LeaveGuildCommand())
                    .build();

            LiteralCommandNode<ServerCommandSource> view = CommandManager
                    .literal("view")
                    .executes(ViewPlayerGuildCommand::viewCallerData)
                    .build();

            LiteralCommandNode<ServerCommandSource> joinGuildNode = CommandManager
                    .literal("join")
                    .build();

            // Root command, all other commands are children of this one
            commandDispatcher.getRoot().addChild(guildRootNode);

            // Leave command
            guildRootNode.addChild(leaveGuildNode);

            // View command, with it's sub-branches
            guildRootNode.addChild(view);

            // Join command
            guildRootNode.addChild(joinGuildNode);
            joinGuildNode.addChild(getGuildSuggestionNode(new JoinGuildCommand()));
        });
    }

    private static CommandNode<ServerCommandSource> getGuildSuggestionNode(Command<ServerCommandSource> command) {
        // Creates a guild argument node that suggests guilds, then executes a command
        return CommandManager
                .argument("guild_name", StringArgumentType.string())
                .suggests(new GuildSuggestionProvider())
                .executes(command)
                .build();
    }

    private static CommandNode<ServerCommandSource> getGuildSuggestionNode(Command<ServerCommandSource> command,
                                                                              Consumer<CommandNode<ServerCommandSource>> nodeChain) {
        // Use this method overload if you want to chain nodes after the suggestion node
        CommandNode<ServerCommandSource> node = CommandManager
                .argument("guild_name", StringArgumentType.string())
                .suggests(new GuildSuggestionProvider())
                .executes(command).build();
        nodeChain.accept(node);
        return node;
    }
}
