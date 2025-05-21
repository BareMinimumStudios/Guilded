package keno.guildedparties.mixin.server;

import com.mojang.authlib.GameProfile;
import keno.guildedparties.data.GPAttachmentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerTicksImpl {
    @Unique
    private int guildedparties$invite_ticks = 1800;

    @Unique
    private int guildedparties$item_ticks = 1200;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void guildedparties$tick(CallbackInfo ci) {
        if (hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
            if (--this.guildedparties$invite_ticks == 0) {
                removeAttached(GPAttachmentTypes.INVITE_ATTACHMENT);
                this.guildedparties$invite_ticks = 1800;
            }
        } else if (this.guildedparties$invite_ticks != 1800) {
            this.guildedparties$invite_ticks = 1800;
        }
    }

    @Override
    public int guildedparties$getTick(int flag) {
        return switch (flag) {
            case 0 -> this.guildedparties$invite_ticks;
            case 1 -> this.guildedparties$item_ticks;
            default -> throw new IllegalStateException("Invalid flag, only flags within range [0-1] are valid: " + flag);
        };
    }

    @Override
    public void guildedparties$decrementTick(int flag) {
        switch (flag) {
            case 0 -> this.guildedparties$invite_ticks--;
            case 1 -> this.guildedparties$item_ticks--;
            default -> throw new IllegalStateException("Invalid flag, only flags within range [0-1] are valid: " + flag);
        }
    }
}
