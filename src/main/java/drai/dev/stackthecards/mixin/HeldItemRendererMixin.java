package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.item.map.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {

    @Shadow private ItemStack offHand;

    @Shadow protected abstract float getMapAngle(float tickDelta);

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm);

    @Shadow private ItemStack mainHand;

    @Shadow protected abstract void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm);

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), cancellable = true)
    private void setCardItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item,
                             float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
        if (!player.isUsingSpyglass()) {
            if (item.isOf(Items.CARD) || item.isOf(Items.CARD_PACK)) {
                boolean bl = hand == Hand.MAIN_HAND;
                Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
                matrices.push();
                var offhand = this.offHand;
                if (bl && offhand.isEmpty() && item.isOf(Items.CARD)) {
                    this.renderCardInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
                } else {
                    this.renderCardInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
                }
                matrices.pop();
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void setCardItem(LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci){
            if (item.isOf(Items.CARD_PACK)) {
                matrices.push();
                matrices.translate(0, 0.2f, 0.1F);
                matrices.scale(1.6f, 1.6f, 1.6f);
                renderFirstPersonCard(matrices, vertexConsumers, light, item);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
                matrices.translate(-128, 0, 0.0F);
//                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
                StackTheCardsClient.CARD_RENDERER.draw(matrices, vertexConsumers, item, light);
                matrices.pop();
                ci.cancel();
            }
    }
    private void renderCardInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
        float f = MathHelper.sqrt(swingProgress);
        float g = -0.2F * MathHelper.sin(swingProgress * 3.1415927F);
        float h = -0.4F * MathHelper.sin(f * 3.1415927F);
        matrices.translate(0.0F, -g / 2.0F, h);
        float i = this.getMapAngle(pitch);
        matrices.translate(0.0F, 0.04F + equipProgress * -1.2F + i * -0.5F, -0.72F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * -85.0F));
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
            this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
            matrices.pop();
        }

        float j = MathHelper.sin(f * 3.1415927F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * 20.0F));
        matrices.scale(2.0F, 2.0F, 2.0F);
        renderFirstPersonCard(matrices, vertexConsumers, light, this.mainHand);
    }

    private void renderCardInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack) {
        float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
        matrices.translate(f * 0.125F, -0.125F, 0.0F);
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 10.0F));
            this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.pop();
        }

        matrices.push();
        matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);
        float g = MathHelper.sqrt(swingProgress);
        float h = MathHelper.sin(g * 3.1415927F);
        float i = -0.5F * h;
        float j = 0.4F * MathHelper.sin(g * 6.2831855F);
        float k = -0.3F * MathHelper.sin(swingProgress * 3.1415927F);
        matrices.translate(f * i, j - 0.3F * h, k);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * -45.0F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * h * -30.0F));
        this.renderFirstPersonCard(matrices, vertexConsumers, light, stack);
        matrices.pop();
    }

    private void renderFirstPersonCard(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-0.5F, -0.5F, 0.0F);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
//        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("stack_the_cards","textures/card/card_background.png")));
//        System.out.println("got the vertexConsumer");
//        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
//        System.out.println("got the matrix4f");
//        vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).light(swingProgress).next();
//        vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).light(swingProgress).next();
//        vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).light(swingProgress).next();
//        vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).light(swingProgress).next();
//        System.out.println("got through the texture matrix");
//        //        if (cardData != null) {
            StackTheCardsClient.CARD_RENDERER.draw(matrices, vertexConsumers, stack, swingProgress);
//        }
    }
}
