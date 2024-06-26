package drai.dev.stackthecards.mixin.client;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow @Final private ItemModels models;

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void injected(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!stack.isEmpty()) {
            if (stack.isOf(Items.CARD) || stack.isOf(Items.CARD_PACK)) {
                Identifier modelIdentifier;
                Identifier fallBackModel;
                if(stack.isOf(Items.CARD)){
                    var cardData = Card.getCardData(stack);
                    if(cardData == null) return;
                    modelIdentifier = cardData.getModelIdentifier();
                    fallBackModel = cardData.getFallbackModel();
                } else /*if(stack.isOf(Items.CARD_PACK))*/{
                    var cardPack = CardPack.getCardPack(stack);
                    modelIdentifier = cardPack.getModelIdentifier();
                    fallBackModel = cardPack.getFallbackModel();
                } /*else {
                    var cardBinder = (CardBinder) stack.getItem();
                    modelIdentifier = fallBackModel = cardBinder.getModelIdentifier();
                }*/
                var model = this.models.getModelManager().getModel(modelIdentifier);
                if(model == null){
                    model = this.models.getModelManager().getModel(fallBackModel);
                }
                cir.setReturnValue(model);
            }
        }
    }
}
