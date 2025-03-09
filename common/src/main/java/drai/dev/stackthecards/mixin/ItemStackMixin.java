package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.StackTheCardsItems;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.*;
import net.minecraft.core.registries.*;
import net.minecraft.locale.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(at = @At("HEAD"), method = "getTooltipImage", cancellable = true)
    private void onGetTooltipData(CallbackInfoReturnable<Optional<TooltipComponent>> ci) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.is(StackTheCardsItems.CARD) || self.is(StackTheCardsItems.CARD_PACK))
            ci.setReturnValue(Optional.of(
                    CardTooltipData.of(self)));
    }

    @Inject(at = @At("HEAD"), method = "isSameItemSameComponents", cancellable = true)
    private static void onGetTooltipData(ItemStack itemStack, ItemStack itemStack2, CallbackInfoReturnable<Boolean> cir) {
        if ((itemStack.is(StackTheCardsItems.CARD)/* || itemStack.is(StackTheCardsItems.CARD_PACK)*/)
                && (itemStack2.is(StackTheCardsItems.CARD)/* || itemStack2.is(StackTheCardsItems.CARD_PACK)*/)){
            cir.setReturnValue(CardIdentifier.isSameItem(CardIdentifier.getCardIdentifier(itemStack), CardIdentifier.getCardIdentifier(itemStack2)));
        }
    }

    @Inject(at = @At("RETURN"), method = "getTooltipLines")
    private void onGetTooltip(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> ci) {
        var self  =(ItemStack) (Object) this;
        if(self.is(StackTheCardsItems.CARD)){
            var tooltip = ci.getReturnValue();
            if(StackTheCardsClient.cardLoreKeyPressed){
                tooltip.addAll(Card.getCardData(self).getDetailToolTips());
            } else {
                tooltip.addAll(Card.getCardData(self).getTooltipsDescriptors());
                StackTheCardsClient.modifyCardStackTooltip(tooltip::addAll);
            }
        }
        if(self.is(StackTheCardsItems.CARD_PACK)){
            var tooltip = ci.getReturnValue();
            if(StackTheCardsClient.cardLoreKeyPressed){
                var pack = CardPack.getCardPack(self);
                var effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryParse(pack.getEffectResourceLocation()));
                if(effect != null){
                    tooltip.add(Component.literal("When completed in a binder grants: " + Language.getInstance().getOrDefault(effect.getDescriptionId())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                }
                tooltip.addAll(pack.getDetailToolTips());
            } else {
                var pack = CardPack.getCardPack(self);
                var effect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.tryParse(pack.getEffectResourceLocation()));
                if(effect != null){
                    tooltip.add(Component.literal("When completed in a binder grants: " +Language.getInstance().getOrDefault(effect.getDescriptionId())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                }
                tooltip.addAll(pack.getTooltipsDescriptors());
                StackTheCardsClient.modifyPackStackTooltip(tooltip::addAll);
            }
        }
        StackTheCardsClient.checkToolTipForScrolling(self);

    }

    @Inject(method = "getHoverName", at = @At("HEAD"), cancellable = true)
    private void customCardName(CallbackInfoReturnable<Component> cir){
        var self  =(ItemStack) (Object) this;
        if(self.is(StackTheCardsItems.CARD)){
            cir.setReturnValue(Card.getCardData(self).getCardNameLabel());
        } else if (self.is(StackTheCardsItems.CARD_PACK)){
            cir.setReturnValue(CardPack.getCardPack(self).getPackNameLabel());
        }
    }
}
