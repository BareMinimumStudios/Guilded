package keno.guildedparties.server.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
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

            // "/leave_guild" command
            commandDispatcher.getRoot().addChild(leaveGuildNode);

            // "/view_player_guild" command
            commandDispatcher.getRoot().addChild(viewPlayerGuildNode);
        });
    }
}
