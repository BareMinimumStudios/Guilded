package keno.guildedparties.api.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import keno.guildedparties.api.server.commands.general.MessageGuildmatesCommand;
import keno.guildedparties.api.server.commands.invites.InvitePlayerCommand;
import keno.guildedparties.api.server.commands.invites.InviteResponseCommands;
import keno.guildedparties.api.server.commands.suggestions.PlayerSuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class GPCommandRegistry {
    public static void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access,
                            CommandManager.RegistrationEnvironment env, boolean dedicated) {
        LiteralCommandNode<ServerCommandSource> guildRootNode = CommandManager
                .literal("guilded")
                .build();

        // General commands
        LiteralCommandNode<ServerCommandSource> guildChatNode = CommandManager
                .literal("sendMsgsToGC")
                .executes(new MessageGuildmatesCommand())
                .build();

        // Invite nodes
        LiteralCommandNode<ServerCommandSource> inviteRootNode = CommandManager.literal("invites").build();

        LiteralCommandNode<ServerCommandSource> sendInviteNode = CommandManager.literal("send").build();

        ArgumentCommandNode<ServerCommandSource, String> playerToInviteNode = CommandManager
                .argument("player", StringArgumentType.string())
                .suggests(new PlayerSuggestionProvider())
                .executes(new InvitePlayerCommand())
                .build();

        LiteralCommandNode<ServerCommandSource> declineInviteNode = CommandManager
                .literal("decline")
                .executes(InviteResponseCommands::declineInviteCommand)
                .build();

        LiteralCommandNode<ServerCommandSource> acceptInviteNode = CommandManager
                .literal("accept")
                .executes(InviteResponseCommands::acceptInviteCommand)
                .build();

        if (dedicated) {
            return;
        }

        dispatcher.getRoot().addChild(guildRootNode);
        guildRootNode.addChild(guildChatNode);

        dispatcher.getRoot().addChild(inviteRootNode);
        inviteRootNode.addChild(sendInviteNode);
        sendInviteNode.addChild(playerToInviteNode);

        inviteRootNode.addChild(declineInviteNode);
        inviteRootNode.addChild(acceptInviteNode);
    }
}
