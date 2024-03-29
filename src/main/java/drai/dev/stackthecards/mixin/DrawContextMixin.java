package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(DrawContext.class)
public class DrawContextMixin implements DrawContextExtensions{
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

   /* @Inject(method = "drawtool", at = @At("HEAD"), cancellable = true)
    private void reset(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci){
        System.out.println("mixin into drawcontext");
        if(stack.isOf(Items.CARD)){
            System.out.println("tried to draw a card tooltip");
            var card = (Card)stack.getItem();
            StackTheCardsClient.CARD_TOOLTIP_RENDERER.drawTooltip(card, textRenderer, stack, x, y);
            ci.cancel();
        }
        ci.cancel();
    }*/
    @Unique
    private int tooltipTopYPosition = 0;
    @Unique
    private int tooltipTopXPosition = 0;
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
