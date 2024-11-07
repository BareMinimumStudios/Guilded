package keno.guildedparties.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import keno.guildedparties.server.commands.suggestions.GuildSuggestionProvider;
import keno.guildedparties.server.commands.suggestions.GuildmateSuggestionProvider;
import keno.guildedparties.server.commands.suggestions.PlayerSuggestionProvider;
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

            LiteralCommandNode<ServerCommandSource> viewNode = CommandManager
                    .literal("view")
                    .executes(ViewPlayerGuildCommand::viewCallerData)
                    .build();

            CommandNode<ServerCommandSource> viewPlayerNode = CommandManager
                    .argument("player", StringArgumentType.string())
                    .suggests(new PlayerSuggestionProvider())
                    .executes(ViewPlayerGuildCommand::viewPlayerData)
                    .build();

            LiteralCommandNode<ServerCommandSource> joinGuildNode = CommandManager
                    .literal("join")
                    .build();

            LiteralCommandNode<ServerCommandSource> demotePlayerNode = CommandManager
                    .literal("demote")
                    .build();

            LiteralCommandNode<ServerCommandSource> createRankNode = CommandManager
                    .literal("createRank")
                            .build();

            // Root command, all other commands are children of this one
            commandDispatcher.getRoot().addChild(guildRootNode);

            // Leave command
            guildRootNode.addChild(leaveGuildNode);

            // View command, with it's sub-branch
            guildRootNode.addChild(viewNode);
            viewNode.addChild(viewPlayerNode);

            // Join command
            guildRootNode.addChild(joinGuildNode);
            joinGuildNode.addChild(getGuildSuggestionNode(new JoinGuildCommand()));

            // Demote command
            guildRootNode.addChild(demotePlayerNode);
            demotePlayerNode.addChild(getGuildmateSuggestionNode(ChangeRankCommand::demotePlayerCommand));
        });
    }

    private static CommandNode<ServerCommandSource> getGuildSuggestionNode(Command<ServerCommandSource> command) {
        // Creates a guild argument node that suggests guilds, then executes a command
        return CommandManager
                .argument("guildName", StringArgumentType.string())
                .suggests(new GuildSuggestionProvider())
                .executes(command)
                .build();
    }

    private static CommandNode<ServerCommandSource> getGuildSuggestionNode(Command<ServerCommandSource> command,
                                                                              Consumer<CommandNode<ServerCommandSource>> nodeChain) {
        // Use this method overload if you want to chain nodes after the suggestion node
        CommandNode<ServerCommandSource> node = getGuildSuggestionNode(command);
        nodeChain.accept(node);
        return node;
    }

    private static CommandNode<ServerCommandSource> getPlayerSuggestionNode(Command<ServerCommandSource> command) {
        return CommandManager
                .argument("player", StringArgumentType.string())
                .suggests(new PlayerSuggestionProvider())
                .executes(command)
                .build();
    }

    private static CommandNode<ServerCommandSource> getPlayerSuggestionNode(Command<ServerCommandSource> command,
                                                                            Consumer<CommandNode<ServerCommandSource>> nodeChain) {
        CommandNode<ServerCommandSource> node = getPlayerSuggestionNode(command);
        nodeChain.accept(node);
        return node;
    }

    private static CommandNode<ServerCommandSource> getGuildmateSuggestionNode(Command<ServerCommandSource> command) {
        return CommandManager.argument("player", StringArgumentType.string())
                .suggests(new GuildmateSuggestionProvider())
                .executes(command)
                .build();
    }

    private static CommandNode<ServerCommandSource> getGuildmateSuggestionNode(Command<ServerCommandSource> command,
                                                                               Consumer<CommandNode<ServerCommandSource>> nodeChain) {
        CommandNode<ServerCommandSource> node = getGuildmateSuggestionNode(command);
        nodeChain.accept(node);
        return node;
    }


}
