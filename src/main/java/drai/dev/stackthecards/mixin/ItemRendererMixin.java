package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
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
            if (stack.isOf(Items.CARD)) {
                var cardData = Card.getCardData(stack);
                if(cardData == null) return;
                var cardModelIdentifier = cardData.getModelIdentifier();
                var model = this.models.getModelManager().getModel(cardModelIdentifier);
                cir.setReturnValue(model);/*
                boolean bl = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED;
                if (bl) {
//                    System.out.println(cardModelIdentifier);
                    if(model != null && model != this.models.getModelManager().getMissingModel()){
                        localRef.set(model);
                    }
                }*/
            }
        }
    }
}
