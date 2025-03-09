package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.core.pattern.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ClientTextTooltip.class)
public class OrderedTextComponentRendererMixin {

    @Shadow @Final private FormattedCharSequence text;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"),
            method = "renderText")
    private int modifyTextDrawing(Font instance, FormattedCharSequence text, float x, float y, int color, boolean shadow,
                                  Matrix4f matrix, MultiBufferSource vertexConsumers, Font.DisplayMode layerType, int backgroundColor, int light) {
        instance.drawInBatch(this.text, (float)x, (float)y + StackTheCardsClient.getScrollModifier(), -1, true, matrix, (MultiBufferSource)vertexConsumers, Font.DisplayMode.NORMAL, 0, 0xF000F0);
        return 0;
    }
}
