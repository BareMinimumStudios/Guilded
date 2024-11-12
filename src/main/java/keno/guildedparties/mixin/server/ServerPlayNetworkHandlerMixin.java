package keno.guildedparties.mixin.server;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.Guild;
import keno.guildedparties.data.player.Member;
import keno.guildedparties.server.StateSaverAndLoader;
import keno.guildedparties.utils.GuildUtils;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler implements ServerPlayPacketListener, PlayerAssociatedNetworkHandler, TickablePacketListener {
    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @WrapWithCondition(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    public boolean guildedparties$wontSendToGuildmates(PlayerManager instance, SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        if (sender == null) return true;

        if (sender.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
            if (sender.getAttachedOrCreate(GPAttachmentTypes.GC_TOGGLE_ATTACHMENT)) {
                StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(this.server);
                Member member = sender.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                Guild senderGuild = state.guilds.get(member.guildKey());
                Text newMessage = Text.of("[%s][%s]: ".formatted(sender.getGameProfile().getName(),
                        member.rank().name())).copy().append(message.getContent());
                GuildUtils.broadcastToGuildmates(this.server, senderGuild, newMessage);
                return false;
            }
        }
        return true;
    }
}
