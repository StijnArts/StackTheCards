package drai.dev.stackthecards.mixin.client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.registry.StackTheCardsItems;
import net.minecraft.client.*;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow private ItemStack offHandItem;

    @Shadow protected abstract float calculateMapTilt(float tickDelta);

    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderMapHand(PoseStack matrices, MultiBufferSource vertexConsumers, int light, HumanoidArm arm);

    @Shadow private ItemStack mainHandItem;

    @Shadow protected abstract void renderPlayerArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, float swingProgress, HumanoidArm arm);

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void setCardItem(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item,
                             float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci){
        if (!player.isScoping()) {
            if (item.is(StackTheCardsItems.CARD) || item.is(StackTheCardsItems.CARD_PACK)) {
                boolean bl = hand == InteractionHand.MAIN_HAND;
                HumanoidArm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
                matrices.pushPose();
                var offhand = this.offHandItem;
                if (bl && offhand.isEmpty() && item.is(StackTheCardsItems.CARD)) {
                    this.renderCardInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
                } else {
                    this.renderCardInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
                }
                matrices.popPose();
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void setCardItem(LivingEntity entity, ItemStack item, ItemDisplayContext renderMode, boolean leftHanded,
                             PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci){
            if (item.is(StackTheCardsItems.CARD_PACK)) {
                matrices.pushPose();
                matrices.translate(0, 0.2f, 0.1F);
                matrices.scale(1.6f, 1.6f, 1.6f);
                renderFirstPersonCard(matrices, vertexConsumers, light, item);
                matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
                matrices.translate(-128, 0, 0.0F);
//                matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
                StackTheCardsClient.CARD_RENDERER.draw(matrices, vertexConsumers, item, light);
                matrices.popPose();
                ci.cancel();
            }
    }
    private void renderCardInBothHands(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
        float f = Mth.sqrt(swingProgress);
        float g = -0.2F * Mth.sin(swingProgress * 3.1415927F);
        float h = -0.4F * Mth.sin(f * 3.1415927F);
        matrices.translate(0.0F, -g / 2.0F, h);
        float i = this.calculateMapTilt(pitch);
        matrices.translate(0.0F, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.72F);
        matrices.mulPose(Axis.XP.rotationDegrees(i * -85.0F));
        if (!this.minecraft.player.isInvisible()) {
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees(90.0F));
            this.renderMapHand(matrices, vertexConsumers, light, HumanoidArm.RIGHT);
            this.renderMapHand(matrices, vertexConsumers, light, HumanoidArm.LEFT);
            matrices.popPose();
        }

        float j = Mth.sin(f * 3.1415927F);
        matrices.mulPose(Axis.XP.rotationDegrees(j * 20.0F));
        matrices.scale(2.0F, 2.0F, 2.0F);
        renderFirstPersonCard(matrices, vertexConsumers, light, this.mainHandItem);
    }

    private void renderCardInOneHand(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, HumanoidArm arm, float swingProgress, ItemStack stack) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        matrices.translate(f * 0.125F, -0.125F, 0.0F);
        if (!this.minecraft.player.isInvisible()) {
            matrices.pushPose();
            matrices.mulPose(Axis.ZP.rotationDegrees(f * 10.0F));
            this.renderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.popPose();
        }

        matrices.pushPose();
        matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);
        float g = Mth.sqrt(swingProgress);
        float h = Mth.sin(g * 3.1415927F);
        float i = -0.5F * h;
        float j = 0.4F * Mth.sin(g * 6.2831855F);
        float k = -0.3F * Mth.sin(swingProgress * 3.1415927F);
        matrices.translate(f * i, j - 0.3F * h, k);
        matrices.mulPose(Axis.XP.rotationDegrees(h * -45.0F));
        matrices.mulPose(Axis.YP.rotationDegrees(f * h * -30.0F));
        this.renderFirstPersonCard(matrices, vertexConsumers, light, stack);
        matrices.popPose();
    }

    private void renderFirstPersonCard(PoseStack matrices, MultiBufferSource vertexConsumers, int swingProgress, ItemStack stack) {
        matrices.mulPose(Axis.YP.rotationDegrees(180.0F));
        matrices.mulPose(Axis.ZP.rotationDegrees(180.0F));
        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-0.5F, -0.5F, 0.0F);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
//        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.getText(ResourceLocation.fromNamespaceAndPath("stack_the_cards","textures/card/card_background.png")));
//        System.out.println("got the vertexConsumer");
//        Matrix4f matrix4f = matrices.last().getPositionMatrix();
//        System.out.println("got the matrix4f");
//        vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(swingProgressendVertex();
//        vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(swingProgressendVertex();
//        vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(swingProgressendVertex();
//        vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(swingProgressendVertex();
//        System.out.println("got through the texture matrix");
//        //        if (cardData != null) {
            StackTheCardsClient.CARD_RENDERER.draw(matrices, vertexConsumers, stack, swingProgress);
//        }
    }
}
