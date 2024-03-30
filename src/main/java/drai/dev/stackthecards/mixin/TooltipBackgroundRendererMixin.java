package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(TooltipBackgroundRenderer.class)
public class TooltipBackgroundRendererMixin {
    @Inject(at = @At(value = "HEAD"), method = "Lnet/minecraft/client/gui/tooltip/TooltipBackgroundRenderer;"
            + "render(Lnet/minecraft/client/gui/DrawContext;IIIII)V")
    private static void updateTooltipLeftAndBottomPos(DrawContext context, int x, int y, int width, int height, int z,
                                                      CallbackInfo ci) {
        DrawContextExtensions posAccess = (DrawContextExtensions) context;
        posAccess.setTooltipTopYPosition(y - 3);
        posAccess.setTooltipBottomYPosition(posAccess.getTooltipTopYPosition() + height + 6);
    }
}
