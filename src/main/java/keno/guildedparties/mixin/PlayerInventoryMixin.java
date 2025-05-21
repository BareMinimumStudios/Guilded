package keno.guildedparties.mixin;

import keno.guildedparties.GuildedParties;
import keno.guildedparties.data.GPAttachmentTypes;
import keno.guildedparties.data.guilds.items.GPComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory {
    @Shadow @Final public PlayerEntity player;

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void guildedparties$insertStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            Item item = stack.getItem();
            if (item.getComponents().contains(GPComponents.GUILD_COMPONENT)) {
                if (serverPlayer.hasAttached(GPAttachmentTypes.MEMBER_ATTACHMENT)) {
                    List<Identifier> ids = item.getComponents().get(GPComponents.GUILD_COMPONENT);
                    String guildName = serverPlayer.getAttached(GPAttachmentTypes.MEMBER_ATTACHMENT).getGuildKey();

                    if (ids != null) {
                        if (ids.stream().noneMatch(id -> id.getPath().equals(guildName))) {
                            cir.setReturnValue(false);
                        }
                    } else {
                        GuildedParties.LOGGER.warn("No guild ids, despite being marked as a guild item. \nItem: {}", item.getName().toString());
                    }
                } else {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
