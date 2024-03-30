package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.*;
import net.minecraft.client.render.*;
import net.minecraft.text.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(OrderedTextTooltipComponent.class)
public class OrderedTextComponentRenderer {

    @Shadow @Final private OrderedText text;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"),
            method = "drawText")
    private int modifyTextDrawing(TextRenderer instance, OrderedText text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light) {
        instance.draw(this.text, (float)x, (float)y + StackTheCardsClient.getScrollModifier(), -1, true, matrix, (VertexConsumerProvider)vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
        return 0;
    }
}
