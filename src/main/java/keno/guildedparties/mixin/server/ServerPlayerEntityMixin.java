package keno.guildedparties.mixin.server;

import com.mojang.authlib.GameProfile;
import keno.guildedparties.data.GPAttachmentTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Unique
    private int guildedparties$ticks = 1800;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void guildedparties$tick(CallbackInfo ci) {
        if (hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
            if (--this.guildedparties$ticks == 0) {
                removeAttached(GPAttachmentTypes.INVITE_ATTACHMENT);
                this.guildedparties$ticks = 1800;
            }
        } else if (this.guildedparties$ticks != 1800) {
            this.guildedparties$ticks = 1800;
        }
    }
}
