package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.client.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(at = @At("HEAD"), method = "getTooltipData()Ljava/util/Optional;", cancellable = true)
    private void onGetTooltipData(CallbackInfoReturnable<Optional<TooltipData>> ci) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.isOf(Items.CARD))
            ci.setReturnValue(Optional.of(
                    CardTooltipData.of(self)));
    }

    @Inject(at = @At("RETURN"), method = "Lnet/minecraft/item/ItemStack;getTooltip"
            + "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;")
    private void onGetTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> ci) {
        var self  =(ItemStack) (Object) this;
        if(self.isOf(Items.CARD)){
            var tooltip = ci.getReturnValue();
            if(StackTheCardsClient.shiftKeyPressed){
                tooltip.addAll(Card.getCardData(self).getLoreToolTips());
            } else {
                tooltip.addAll(Card.getCardData(self).getTooltipsDescriptors());
                StackTheCardsClient.modifyStackTooltip(tooltip::addAll);
            }
        }
        StackTheCardsClient.checkToolTipForScrolling(self);

    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void customCardName(CallbackInfoReturnable<Text> cir){
        var self  =(ItemStack) (Object) this;
        if(self.isOf(Items.CARD)){
            cir.setReturnValue(Card.getCardData(self).getCardNameLabel());
        }
    }
}
