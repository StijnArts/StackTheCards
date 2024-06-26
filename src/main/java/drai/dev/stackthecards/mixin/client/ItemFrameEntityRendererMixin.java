package drai.dev.stackthecards.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.decoration.*;
import net.minecraft.item.*;
import net.minecraft.item.map.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemFrameEntityRenderer.class)
public abstract class ItemFrameEntityRendererMixin<T extends ItemFrameEntity> {
    @Shadow protected abstract int getLight(T itemFrame, int glowLight, int regularLight);

    @Inject(method = "render(Lnet/minecraft/entity/decoration/ItemFrameEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"), cancellable = true)
    private void renderInterceptForCardItems(T itemFrameEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        ItemStack itemStack = itemFrameEntity.getHeldItemStack();
        if(!itemStack.isEmpty() && (itemStack.isOf(Items.CARD)||itemStack.isOf(Items.CARD_PACK))){
            boolean bl = itemFrameEntity.isInvisible();
            if (bl) {
                matrixStack.translate(0.0F, 0.0F, 0.5F);
            } else {
                matrixStack.translate(0.0F, 0.0F, 0.4375F);
            }
            int j = itemFrameEntity.getRotation();
//            System.out.println("ItemFrame Rotation ="+j);
            var modifierRotation = (float)j * 360.0F / 8.0F;
//            System.out.println("ItemFrame modified Rotation = "+modifierRotation);
            var rotationDegrees = RotationAxis.POSITIVE_Z.rotationDegrees(modifierRotation);
            matrixStack.multiply(rotationDegrees);
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
            matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
            matrixStack.translate(-64.0F, -64.0F, 0.0F);
            matrixStack.translate(0.0F, 0.0F, -1.0F);
            if(itemStack.isOf(Items.CARD_PACK)) {
                matrixStack.scale(0.5F,0.5F,0.5F);
                matrixStack.translate(64F,64F,3F);
            }
            int k = this.getLight(itemFrameEntity, LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE | 210, i);
            StackTheCardsClient.CARD_RENDERER.draw(matrixStack, vertexConsumerProvider, itemStack, k);
            matrixStack.pop();
            ci.cancel();
        }
    }
}
