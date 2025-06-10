package keno.guildedparties.mixin.server;

/*? >= 1.21.3 {*/import blue.endless.jankson.annotation.Nullable;/*?}*/
import com.mojang.authlib.GameProfile;
import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.items.GPComponents;
import keno.guildedparties.data.player.Member;
/*? >= 1.21.3 {*/import net.minecraft.entity.ItemEntity;/*?}*/
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Stream;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements PlayerTicksImpl {

    //? if >=1.21.3
    @Shadow @Nullable protected abstract ItemEntity dropPlayerItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership);

    @Shadow public abstract void sendMessage(Text message, boolean overlay);

    @Unique
    private int guildedparties$invite_ticks = 1800;

    @Unique
    private int guildedparties$item_ticks = 0;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void guildedparties$tick(CallbackInfo ci) {
        if (hasAttached(GPAttachmentTypes.INVITE_ATTACHMENT)) {
            if (--this.guildedparties$invite_ticks <= 0) {
                removeAttached(GPAttachmentTypes.INVITE_ATTACHMENT);
                this.guildedparties$invite_ticks = 1800;
            }
        } else if (this.guildedparties$invite_ticks != 1800) {
            this.guildedparties$invite_ticks = 1800;
        }

        if (GuildedParties.CONFIG.enableGuildItems()) {
            if (--this.guildedparties$item_ticks <= 0) {
                if (!getInventory().isEmpty()) {
                    Stream<ItemStack> stream = Stream.concat(Stream.concat(getInventory().main.stream(), getInventory().armor.stream()),
                            getInventory().offHand.stream());

                    stream = stream.filter(itemstack -> itemstack.getItem().getComponents().contains(GPComponents.GUILD_COMPONENT));

                    if (!hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                        stream.forEach(this::guildedparties$removeItemStack);
                    } else {
                        Member member = getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT);
                        String guildName = member.getGuildKey();
                        stream.forEach(stack -> guildedparties$canKeepItemStack(stack, guildName));
                    }
                }
                this.guildedparties$item_ticks = 36000;
            }
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

    @Unique
    public void guildedparties$removeItemStack(ItemStack stack) {
        int i = getInventory().getSlotWithStack(stack);

        //? if >= 1.21.3
        dropPlayerItem(stack, false ,false);
        //? if < 1.21.3
        /*this.dropItem(stack, false, false);*/
        getInventory().removeStack(i);
    }

    @Unique
    public void guildedparties$canKeepItemStack(ItemStack stack, String guildName) {
        List<Identifier> ids = stack.get(GPComponents.GUILD_COMPONENT);

        if (ids == null || ids.isEmpty()) throw new InvalidIdentifierException("No ids found, despite being a guild item. Item: " + stack.getItem());

        if (ids.stream().noneMatch(id -> id.getPath().equals(guildName))) {
            int i = getInventory().getSlotWithStack(stack);
            sendMessage(Text.translatable("guildedparties.cannot_use_item"), false);
            //? if < 1.21.3
            /*this.dropItem(stack, false, false);*/
            //? if >= 1.21.3
            dropPlayerItem(stack, false ,false);
            getInventory().removeStack(i);
        }
    }
}