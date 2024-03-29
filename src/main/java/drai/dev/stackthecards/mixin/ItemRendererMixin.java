package drai.dev.stackthecards.mixin;

import com.llamalad7.mixinextras.sugar.*;
import com.llamalad7.mixinextras.sugar.ref.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.fabricmc.fabric.api.renderer.v1.model.*;
import net.minecraft.client.*;
import net.minecraft.client.color.item.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.*;
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
    /*@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At("TAIL"), cancellable = false)
    private void setCardItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            if (stack.isOf(Items.CARD)) {
                System.out.println(model);
            }
        }
    }*/
    /*@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", shift = At.Shift.AFTER))
    private void injected(CallbackInfo ci, @Local LocalRef<BakedModel> localRef, @Local LocalRef<ItemStack> stack,
                          @Local LocalRef<ModelTransformationMode> renderMode) {
        if (!stack.get().isEmpty()) {
            if (stack.get().isOf(Items.CARD)) {
                var card = (Card) stack.get().getItem();
                var cardModelIdentifier = card.getIdentifier();
                boolean bl = renderMode.get() == ModelTransformationMode.GUI || renderMode.get() == ModelTransformationMode.GROUND || renderMode.get() == ModelTransformationMode.FIXED;
                if (bl) {
//                    System.out.println(cardModelIdentifier);
                    var model = this.models.getModelManager().getModel(cardModelIdentifier);
                    if(model != null && model != this.models.getModelManager().getMissingModel()){
                        localRef.set(model);
                    }
                }
            }
        }
    }*/

    @Inject(method = "getModel", at = @At("RETURN"), cancellable = true)
    private void injected(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (!stack.isEmpty()) {
            if (stack.isOf(Items.CARD)) {
                var card = (Card) stack.getItem();
                var cardModelIdentifier = card.getIdentifier();
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
