package drai.dev.stackthecards.mixin.client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.registry.StackTheCardsItems;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameEntityRendererMixin<T extends ItemFrame> {
    @Shadow protected abstract int getLightVal(T itemFrame, int glowLight, int regularLight);

    @Inject(method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"), cancellable = true)
    private void renderInterceptForCardItems(T itemFrameEntity, float f, float g, PoseStack poseStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci){
        ItemStack itemStack = itemFrameEntity.getItem();
        if(!itemStack.isEmpty() && (itemStack.is(StackTheCardsItems.CARD)||itemStack.is(StackTheCardsItems.CARD_PACK))){
            boolean bl = itemFrameEntity.isInvisible();
            if (bl) {
                poseStack.translate(0.0F, 0.0F, 0.5F);
            } else {
                poseStack.translate(0.0F, 0.0F, 0.4375F);
            }
            int j = itemFrameEntity.getRotation();
//            System.out.println("ItemFrame Rotation ="+j);
            var modifierRotation = (float)j * 360.0F / 8.0F;
//            System.out.println("ItemFrame modified Rotation = "+modifierRotation);
            var rotationDegrees = Axis.ZP.rotationDegrees(modifierRotation);
            poseStack.mulPose(rotationDegrees);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
            poseStack.translate(-64.0F, -64.0F, 0.0F);
            poseStack.translate(0.0F, 0.0F, -1.0F);
            if(itemStack.is(StackTheCardsItems.CARD_PACK)) {
                poseStack.scale(0.5F,0.5F,0.5F);
                poseStack.translate(64F,64F,3F);
            }
            int k = this.getLightVal(itemFrameEntity, LightTexture.FULL_SKY | 210, i);
            StackTheCardsClient.CARD_RENDERER.draw(poseStack, vertexConsumerProvider, itemStack, k);
            poseStack.popPose();
            ci.cancel();
        }
    }
}
