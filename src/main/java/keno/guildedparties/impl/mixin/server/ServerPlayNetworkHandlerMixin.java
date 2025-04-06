package keno.guildedparties.impl.mixin.server;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import keno.guildedparties.api.data.GPComponents;
import keno.guildedparties.api.data.guilds.Guild;
import keno.guildedparties.api.data.player.Member;
import keno.guildedparties.api.data.player.attachments.MemberComponent;
import keno.guildedparties.api.server.StateSaverAndLoader;
import keno.guildedparties.api.utils.GuildApi;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements EntityTrackingListener, TickablePacketListener, ServerPlayPacketListener {
    @Shadow @Final private MinecraftServer server;

    @WrapWithCondition(method = "handleDecoratedMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    public boolean guildedparties$wontSendToGuildmates(PlayerManager instance, SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters params) {
        if (sender == null) return true;

        if (GPComponents.MEMBER_KEY.isProvidedBy(sender)) {
            MemberComponent component = GPComponents.MEMBER_KEY.get(sender);
            if (component.hasMemberData()) {
                if (GPComponents.GC_KEY.get(sender).isToggled()) {
                    StateSaverAndLoader state = StateSaverAndLoader.getStateFromServer(this.server);
                    Member member = component.getMemberData();
                    Guild senderGuild = state.getGuild(member.getGuildKey());
                    Text newMessage = Text.of("[%s][%s]: ".formatted(sender.getGameProfile().getName(),
                            member.getRank().name())).copy().append(message.getContent());
                    GuildApi.broadcastToGuildmates(this.server, senderGuild, newMessage);
                    return false;
                }
            }
        }
        return true;
    }
}
