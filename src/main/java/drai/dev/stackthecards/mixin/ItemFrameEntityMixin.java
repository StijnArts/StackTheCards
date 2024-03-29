package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.render.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends Entity {
    @Shadow public abstract ItemStack getHeldItemStack();

    public ItemFrameEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = false)
    private void setCardItem(ItemStack stack, CallbackInfo ci){
        if(stack.isOf(Items.CARD)){
            this.setFlag(5, true);
        }
    }

    @Inject(method = "dropHeldStack", at = @At("HEAD"), cancellable = false)
    private void setCardItem(Entity entity, boolean alwaysDrop, CallbackInfo ci){
        ItemStack stack = this.getHeldItemStack();
        if(stack.isOf(Items.CARD)){
            this.setFlag(5, false);
        }
    }
}
