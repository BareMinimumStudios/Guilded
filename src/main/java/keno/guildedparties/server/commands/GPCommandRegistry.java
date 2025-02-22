package keno.guildedparties.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.server.commands.general.JoinGuildCommand;
import keno.guildedparties.server.commands.general.LeaveGuildCommand;
import keno.guildedparties.server.commands.general.MessageGuildmatesCommand;
import keno.guildedparties.server.commands.general.ViewPlayerGuildCommand;
import keno.guildedparties.server.commands.invites.InvitePlayerCommand;
import keno.guildedparties.server.commands.invites.InviteResponseCommands;
import keno.guildedparties.server.commands.suggestions.GuildSuggestionProvider;
import keno.guildedparties.server.commands.suggestions.GuildmateSuggestionProvider;
import keno.guildedparties.server.commands.suggestions.PlayerSuggestionProvider;
import keno.guildedparties.server.commands.suggestions.RankSuggestionProvider;
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

            // General commands
            LiteralCommandNode<ServerCommandSource> guildChatNode = CommandManager
                    .literal("sendMsgsToGC")
                    .executes(new MessageGuildmatesCommand())
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

            // Invite nodes
            LiteralCommandNode<ServerCommandSource> inviteRootNode = CommandManager.literal("invites").build();

            LiteralCommandNode<ServerCommandSource> sendInviteNode = CommandManager.literal("send").build();

            LiteralCommandNode<ServerCommandSource> declineInviteNode = CommandManager
                    .literal("decline")
                    .executes(InviteResponseCommands::declineInviteCommand)
                    .build();

            LiteralCommandNode<ServerCommandSource> acceptInviteNode = CommandManager
                    .literal("accept")
                    .executes(InviteResponseCommands::acceptInviteCommand)
                    .build();


            // Guildmate management nodes
            LiteralCommandNode<ServerCommandSource> guildmateRootNode = CommandManager.literal("guildmates").build();

            // Demote command
            LiteralCommandNode<ServerCommandSource> demotePlayerNode = CommandManager
                    .literal("demote")
                    .build();

            // Promote command
            LiteralCommandNode<ServerCommandSource> promotePlayerNode = CommandManager
                    .literal("promote")
                    .build();

            // Kick command
            LiteralCommandNode<ServerCommandSource> kickPlayerNode = CommandManager.literal("kick").build();

            // Ban command
            LiteralCommandNode<ServerCommandSource> banPlayerNode = CommandManager.literal("ban").build();

            // Guild management nodes
            LiteralCommandNode<ServerCommandSource> managementRootNode = CommandManager.literal("management").build();

            // Rank Creation
            LiteralCommandNode<ServerCommandSource> createRankNode = CommandManager
                    .literal("createRank").build();

            CommandNode<ServerCommandSource> rankNameNode = CommandManager
                    .argument("rankName", StringArgumentType.string()).build();

            CommandNode<ServerCommandSource> rankPriorityNode = CommandManager
                    .argument("rankPriority", IntegerArgumentType.integer())
                            .executes(GuildManagementCommands::createGuildRankCommand).build();

            // Rank removal
            LiteralCommandNode<ServerCommandSource> removeRankNode = CommandManager.literal("removeRank").build();
            CommandNode<ServerCommandSource> rankNode = CommandManager
                    .argument("rank", StringArgumentType.string())
                    .suggests(new RankSuggestionProvider())
                    .executes(GuildManagementCommands::removeGuildRankCommand)
                    .build();

            // Guild creation
            LiteralCommandNode<ServerCommandSource> createGuildNode = CommandManager
                    .literal("createGuild")
                    .build();

            CommandNode<ServerCommandSource> guildNameNode = CommandManager
                    .argument("guildName", StringArgumentType.string())
                    .build();

            CommandNode<ServerCommandSource> leaderRankNameNode = CommandManager
                    .argument("leaderRankName", StringArgumentType.string())
                    .executes(GuildManagementCommands::createGuildCommand)
                    .build();

            // Guild disbanding
            LiteralCommandNode<ServerCommandSource> disbandGuildNode = CommandManager.literal("disband").build();

            LiteralCommandNode<ServerCommandSource> confirmDisbandNode = CommandManager
                    .literal("confirm")
                    .executes(GuildManagementCommands::disbandGuildCommand)
                    .build();

            // Guild settings
            LiteralCommandNode<ServerCommandSource> settingsNode = CommandManager.literal("settings").build();

            // Guild privacy
            LiteralCommandNode<ServerCommandSource> privacyNode = CommandManager.literal("privacy").build();
            CommandNode<ServerCommandSource> setPrivacyNode = CommandManager
                    .argument("isPrivate", BoolArgumentType.bool())
                    .executes(GuildSettingCommands::changeGuildPrivacySetting)
                    .build();

            LiteralCommandNode<ServerCommandSource> managePlayerRankNode = CommandManager.literal("managePlayerRank")
                    .build();

            CommandNode<ServerCommandSource> managePlayerRankPriorityNode = CommandManager
                    .argument("managePlayerRankPriority", IntegerArgumentType.integer(1, 50))
                    .executes(GuildSettingCommands::changeManagePlayerRankPriority)
                    .build();

            LiteralCommandNode<ServerCommandSource> standDownNode = CommandManager.literal("standdown")
                    .executes(GuildmateManagementCommands::standDownCommand)
                    .build();

            // Command registry
            boolean enableServerCommands = GuildedParties.CONFIG.enableServerCommands();

            // Root command, all other commands are children of this one
            commandDispatcher.getRoot().addChild(guildRootNode);

            // guild chat messages
            guildRootNode.addChild(guildChatNode);

            if (enableServerCommands) {
                // Leave command
                guildRootNode.addChild(leaveGuildNode);

                // View command, with it's sub-branch
                guildRootNode.addChild(viewNode);
                viewNode.addChild(viewPlayerNode);

                // Join command
                guildRootNode.addChild(joinGuildNode);
                joinGuildNode.addChild(getGuildSuggestionNode(new JoinGuildCommand()));
            }

            // Invites
            commandDispatcher.getRoot().addChild(inviteRootNode);
            inviteRootNode.addChild(sendInviteNode);
            inviteRootNode.addChild(declineInviteNode);
            inviteRootNode.addChild(acceptInviteNode);
            sendInviteNode.addChild(getPlayerSuggestionNode(new InvitePlayerCommand()));

            if (enableServerCommands) {
                // Guildmate management commands
                guildRootNode.addChild(guildmateRootNode);

                // Demote command
                guildmateRootNode.addChild(demotePlayerNode);
                demotePlayerNode.addChild(getGuildmateSuggestionNode(GuildmateManagementCommands::demotePlayerCommand));

                // Promote command
                guildmateRootNode.addChild(promotePlayerNode);
                promotePlayerNode.addChild(getGuildmateSuggestionNode(GuildmateManagementCommands::promotePlayerCommand));

                // Kick command
                guildmateRootNode.addChild(kickPlayerNode);
                kickPlayerNode.addChild(getGuildmateSuggestionNode(GuildmateManagementCommands::kickPlayerCommand));

                // Ban command
                guildmateRootNode.addChild(banPlayerNode);
                banPlayerNode.addChild(getGuildmateSuggestionNode(GuildmateManagementCommands::banPlayerCommand));

                // Guild management commands
                guildRootNode.addChild(managementRootNode);

                // Guild disbanding command
                managementRootNode.addChild(disbandGuildNode);
                disbandGuildNode.addChild(confirmDisbandNode);

                // Guild creation command
                managementRootNode.addChild(createGuildNode);
                createGuildNode.addChild(guildNameNode);
                guildNameNode.addChild(leaderRankNameNode);

                // Rank creation command
                managementRootNode.addChild(createRankNode);
                createRankNode.addChild(rankNameNode);
                rankNameNode.addChild(rankPriorityNode);

                // Rank removal command
                managementRootNode.addChild(removeRankNode);
                removeRankNode.addChild(rankNode);

                // Guild settings command
                managementRootNode.addChild(settingsNode);
                settingsNode.addChild(privacyNode);
                privacyNode.addChild(setPrivacyNode);

                settingsNode.addChild(managePlayerRankNode);
                managePlayerRankNode.addChild(managePlayerRankPriorityNode);

                managementRootNode.addChild(standDownNode);
            }
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
