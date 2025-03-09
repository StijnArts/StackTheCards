package drai.dev.stackthecards.mixin.client;

import drai.dev.stackthecards.extensions.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(TooltipRenderUtil.class)
public class TooltipBackgroundRendererMixin {
    @Inject(at = @At(value = "HEAD"), method = "renderTooltipBackground")
    private static void updateTooltipLeftAndBottomPos(GuiGraphics context, int x, int y, int width, int height, int z,
                                                      CallbackInfo ci) {
        DrawContextExtensions posAccess = (DrawContextExtensions) context;
        posAccess.setTooltipTopYPosition(y - 3);
        posAccess.setTooltipBottomYPosition(posAccess.getTooltipTopYPosition() + height + 6);
    }
}
