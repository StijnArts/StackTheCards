package drai.dev.stackthecards.client.screen;

import com.mojang.blaze3d.systems.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import net.fabricmc.api.*;
import net.minecraft.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.core.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import org.lwjgl.glfw.*;

import static drai.dev.stackthecards.items.CardBinder.MAX_CARDS_PER_PAGE;

@Environment(EnvType.CLIENT)
public class CardBinderScreen extends AbstractContainerScreen<CardBinderScreenHandler> {
    private final NonNullList<ItemStack> inventory;

    public CardBinderScreen(CardBinderScreenHandler handler, Inventory playerInventory, Component text){
        super(handler, playerInventory, text);
        var cardBinderInventory = new CardBinderInventory(playerInventory.player);
        this.inventory = cardBinderInventory.getInventory();
        BINDER_TEXTURE = new ResourceLocation("stack_the_cards", "textures/gui/binder"+cardBinderInventory.getColorAffix(playerInventory.player)+".png");
        BINDER_CARD_SLOT = new ResourceLocation("stack_the_cards", "textures/gui/binder_slot.png");
        BINDER_TIES = new ResourceLocation("stack_the_cards", "textures/gui/binder_binds.png");
    }
    public final ResourceLocation BINDER_TEXTURE;
    public final ResourceLocation BINDER_CARD_SLOT;
    public final ResourceLocation BINDER_TIES;

    public PageButton nextPageButton;
    public PageButton previousPageButton;

    @Override
    protected void init() {
        this.imageHeight = 256;
        this.imageWidth = 256;
        inventoryLabelY = this.imageHeight - 84;
        inventoryLabelX = 48;
        titleLabelY = -500;
        super.init();
        this.addPageButtons();
    }

    public double getYOrigin(){
        return (height + (height%2==0?-2:0) - 256) / (double)2 + 2;
    }

    public double getXOrigin(){
        return (width - 256) / (double)2;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        int x = (int) getXOrigin();
        int y = (int) getYOrigin();
        context.blit(BINDER_TEXTURE, x, y, 0, 0, 256, 256);
        RenderSystem.disableDepthTest();
        var poseStack = context.pose();
        poseStack.pushPose();
        poseStack.translate(this.leftPos+9, this.topPos+14, 100);
        var slots = menu.cardSlots;
        int slotIndex = 0;
        for (int page = 0; page < 2; page++) {
            for (int j = 0; j < 2; ++j) {
                for (int i = 0; i < 2; i++) {
                    var slot = slots.get(slotIndex);
                    if(!slot.isEnabled()){
                        slotIndex++;
                        continue;
                    }
                    poseStack.pushPose();
                    context.blit(BINDER_CARD_SLOT, 54 * i + (132*page),73*j, 0, 0, 52, 71, 52, 71);
                    context.blit(BINDER_TIES, 54 * i + (132*page),73*j-1, 0, 0, 52, 54, 52, 54);

                    var stack1 = slot.getItem();
                    poseStack.translate(54 * i + (132*page),73*j,200);
                    if(!stack1.isEmpty()){
                        StackTheCardsClient.CARD_RENDERER.draw(poseStack, context.bufferSource(), stack1, 15728880, 52/(float)128);
                    }
                    poseStack.scale(0.5f,0.5f, 0);
                    context.drawString(font, Component.literal(slotIndex % MAX_CARDS_PER_PAGE + MAX_CARDS_PER_PAGE * StackTheCardsClient.PAGE_INDEX + 1 + ""),
                            74, 110, ChatFormatting.BLACK.getColor(), false);
                    poseStack.popPose();
                    slotIndex++;
                }
            }
        }
        poseStack.popPose();
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {

    }

    private int getPageCount() {
        int leftOvers = inventory.size()%MAX_CARDS_PER_PAGE;
        return inventory.size()/MAX_CARDS_PER_PAGE + (leftOvers > 0 ? 1 : 0);
    }

    protected void addPageButtons() {
        StackTheCardsClient.PAGE_INDEX = Math.min(StackTheCardsClient.PAGE_INDEX, this.getPageCount() - 1);
        int i = (this.width - 256) / 2;
        int j = 2;
        this.nextPageButton = this.addRenderableWidget(new PageButton(i + 233, 175, true, button -> this.goToNextPage(), true));
        this.previousPageButton = this.addRenderableWidget(new PageButton(i, 175, false, button -> this.goToPreviousPage(), true));
        this.updatePageButtons();
    }

    protected void goToPreviousPage() {
        if (StackTheCardsClient.PAGE_INDEX > 0) {
            --StackTheCardsClient.PAGE_INDEX;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (StackTheCardsClient.PAGE_INDEX < this.getPageCount() - 1) {
            ++StackTheCardsClient.PAGE_INDEX;
        }
        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.menu.checkEnabledSlots();
        this.nextPageButton.visible = StackTheCardsClient.PAGE_INDEX < this.getPageCount() - 1;
        this.previousPageButton.visible = StackTheCardsClient.PAGE_INDEX > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT: {
                this.previousPageButton.onPress();
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT: {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
