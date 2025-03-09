package drai.dev.stackthecards.mixin.client;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.extensions.*;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.world.inventory.tooltip.*;
import org.apache.logging.log4j.core.pattern.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.*;

@Mixin(GuiGraphics.class)
public abstract class DrawContextMixin implements DrawContextExtensions {

    @Shadow @Deprecated public abstract void drawManaged(Runnable drawCallback);

    @Shadow public abstract int guiWidth();

    @Shadow public abstract int guiHeight();

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderImage(Lnet/minecraft/client/gui/Font;IILnet/minecraft/client/gui/GuiGraphics;)V"), method =
            "renderTooltipInternal")
    private void drawPosAwareComponent(ClientTooltipComponent component, Font textRenderer, int x, int y,
                                       GuiGraphics context) {
        if(component instanceof CardTooltipComponent cardClientTooltipComponent){
            cardClientTooltipComponent.drawItemsWithTooltipPosition(textRenderer, x, y, context, this.getTooltipTopYPosition(),
                    this.getTooltipBottomYPosition(), this.getMouseX(), this.getMouseY());
        } else {
            component.renderImage(textRenderer, x, y, context);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawManaged(Ljava/lang/Runnable;)V"), method =
            "renderTooltipInternal")
    private void drawPosAwareComponent(GuiGraphics context, Runnable drawCallback, Font textRenderer, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner) {
        int i = 0;
        int j = components.size() == 1 ? -2 : 0;
        for (ClientTooltipComponent clientTooltipComponent : components) {
            int k = clientTooltipComponent.getWidth(textRenderer);
            if (k > i) {
                i = k;
            }
            j += clientTooltipComponent.getHeight();
        }
        int l = i;
        int m = j;
        Vector2ic vector2ic = positioner.positionTooltip(this.guiWidth(), this.guiHeight(), x, y, l, m);
        int n = vector2ic.x();
        int o = vector2ic.y();
        this.drawManaged(() -> TooltipRenderUtil.renderTooltipBackground((GuiGraphics) (Object)this, n, o + StackTheCardsClient.getScrollModifier(), l, m, 400));
    }

    @Unique
    private int tooltipTopYPosition = 0;
    @Unique
    private int tooltipBottomYPosition = 0;
    @Unique
    private int mouseX = 0;
    @Unique
    private int mouseY = 0;
    @Override
    @Intrinsic
    public void setTooltipTopYPosition(int topY) {
        this.tooltipTopYPosition = topY;
    }

    @Override
    @Intrinsic
    public void setTooltipBottomYPosition(int bottomY) {
        this.tooltipBottomYPosition = bottomY;
    }

    @Override
    @Intrinsic
    public int getTooltipTopYPosition() {
        return this.tooltipTopYPosition;
    }

    @Override
    @Intrinsic
    public int getTooltipBottomYPosition() {
        return this.tooltipBottomYPosition;
    }

    @Override
    @Intrinsic
    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    @Override
    @Intrinsic
    public int getMouseX() {
        return this.mouseX;
    }

    @Override
    @Intrinsic
    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }

    @Override
    @Intrinsic
    public int getMouseY() {
        return this.mouseY;
    }
}
