package drai.dev.stackthecards.mixin.client;

import com.mojang.blaze3d.vertex.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.extensions.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.tooltips.restrict.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import org.joml.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(GuiGraphics.class)
public abstract class DrawContextMixinCommon implements DrawContextExtensions {

    @Shadow @Deprecated public abstract void drawManaged(Runnable drawCallback);

    @Shadow public abstract int guiWidth();

    @Shadow public abstract int guiHeight();

    @Shadow protected abstract void renderTooltipInternal(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner);

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

    @ModifyVariable(method = "renderTooltipInternal", at = @At(value = "HEAD"), index = 2, argsOnly = true)
    public List<ClientTooltipComponent> makeListMutable(List<ClientTooltipComponent> value) {
        return new ArrayList<>(value);
    }

    @Inject(method =            "renderTooltipInternal", at = @At(value = "HEAD"))
    public void fix(Font textRenderer, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo ci) {
        TooltipScreenRestrictor.fix(components, textRenderer, x, guiWidth());
    }

    /*@Inject(method = "renderTooltipInternal",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"),
            cancellable = true)
    private void injectRenderTooltip(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo ci) {
        int newX = TooltipScreenRestrictor.shouldFlip(list, font, i);

        // Re-run the method with the modified X position
        this.renderTooltipInternal(font, list, newX, j, clientTooltipPositioner);

        // Cancel the original call to avoid double rendering
        ci.cancel();
    }*/

//    @ModifyVariable(method =    "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"), index = 11)
//    public int modifyRenderX(int value, Font textRenderer, List<ClientTooltipComponent> components, int x) {
//        return TooltipScreenRestrictor.shouldFlip(components, textRenderer, x);
//    }

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
