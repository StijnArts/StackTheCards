package drai.dev.stackthecards.mixin.client;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static drai.dev.stackthecards.models.StackTheCardsModelLoader.*;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow @Final private ItemModelShaper itemModelShaper;

    @Shadow public abstract ItemModelShaper getItemModelShaper();

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void injected(ItemStack stack, Level level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!stack.isEmpty()) {
            if (stack.is(StackTheCardsItems.CARD) || stack.is(StackTheCardsItems.CARD_PACK)) {
                ModelResourceLocation modelResourceLocation;
                ModelResourceLocation fallBackModel;
                if(stack.is(StackTheCardsItems.CARD)){
                    var cardData = Card.getCardData(stack);
                    if(cardData == null) return;
                    modelResourceLocation = cardData.getModelResourceLocation();
                    fallBackModel = cardData.getFallbackModel();
                } else /*if(stack.is(Items.CARD_PACK))*/{
                    var cardPack = CardPack.getCardPack(stack);
                    modelResourceLocation = cardPack.getModelResourceLocation();
                    fallBackModel = cardPack.getFallbackModel();
                } /*else {
                    var cardBinder = (CardBinder) stack.getItem();
                    modelResourceLocation = fallBackModel = cardBinder.getModelResourceLocation();
                }*/
                var model = getBakedModel(((ItemRenderer)(Object)this), modelResourceLocation, fallBackModel);
                cir.setReturnValue(model);
            }
        }
    }
}
