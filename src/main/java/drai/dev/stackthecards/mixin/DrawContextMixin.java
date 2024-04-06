package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.extensions.*;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.*;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin implements DrawContextExtensions {

    @Shadow @Deprecated public abstract void draw(Runnable drawCallback);

    @Shadow public abstract int getScaledWindowWidth();

    @Shadow public abstract int getScaledWindowHeight();

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;"
            + "drawItems(Lnet/minecraft/client/font/TextRenderer;IILnet/minecraft/client/gui/DrawContext;)V"), method =
            "Lnet/minecraft/client/gui/DrawContext;drawTooltip("
                    + "Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II"
                    + "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
    private void drawPosAwareComponent(TooltipComponent component, TextRenderer textRenderer, int x, int y,
                                       DrawContext context) {
        if(component instanceof CardTooltipComponent cardTooltipComponent){
            cardTooltipComponent.drawItemsWithTooltipPosition(textRenderer, x, y, context, this.getTooltipTopYPosition(),
                    this.getTooltipBottomYPosition(), this.getMouseX(), this.getMouseY());
        } else {
            component.drawItems(textRenderer, x, y, context);
        }
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw(Ljava/lang/Runnable;)V"), method =
            "Lnet/minecraft/client/gui/DrawContext;drawTooltip("
                    + "Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II"
                    + "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
    private void drawPosAwareComponent(DrawContext context, Runnable drawCallback, TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {
        int i = 0;
        int j = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components) {
            int k = tooltipComponent.getWidth(textRenderer);
            if (k > i) {
                i = k;
            }
            j += tooltipComponent.getHeight();
        }
        int l = i;
        int m = j;
        Vector2ic vector2ic = positioner.getPosition(this.getScaledWindowWidth(), this.getScaledWindowHeight(), x, y, l, m);
        int n = vector2ic.x();
        int o = vector2ic.y();
        this.draw(() -> TooltipBackgroundRenderer.render((DrawContext) (Object)this, n, o + StackTheCardsClient.getScrollModifier(), l, m, 400));
    }



    /*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/tooltip/TooltipComponent;drawText(Lnet/minecraft/client/font/TextRenderer;IILorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V"), method =
            "Lnet/minecraft/client/gui/DrawContext;drawTooltip("
                    + "Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II"
                    + "Lnet/minecraft/client/gui/tooltip/TooltipPositioner;)V")
    private void modifyText(TooltipComponent component, TextRenderer textRenderer, int x, int y,
                                       DrawContext context) {
        if(component instanceof CardTooltipComponent cardTooltipComponent){
            cardTooltipComponent.drawItemsWithTooltipPosition(textRenderer, x, y, context, this.getTooltipTopYPosition(),
                    this.getTooltipBottomYPosition(), this.getMouseX(), this.getMouseY());
        } else {
            component.drawItems(textRenderer, x, y+StackTheCardsClient.scrollModifier, context);
        }
    }*/

    /*@ModifyVariable(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V", at = @At("STORE"), ordinal = 9)
    private int injected(int n) {
        if(StackTheCardsClient.shiftKeyPressed){
            var newY = n + StackTheCardsClient.scrollModifier*3;
            System.out.println("modified Y, was "+n+", is "+newY);
            return newY;
        }
        return n;
    }*/
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
