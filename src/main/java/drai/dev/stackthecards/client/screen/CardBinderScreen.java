package drai.dev.stackthecards.client.screen;

import com.mojang.blaze3d.systems.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.tooltip.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import org.lwjgl.glfw.*;

import static drai.dev.stackthecards.items.CardBinder.MAX_CARDS_PER_PAGE;

@Environment(EnvType.CLIENT)
public class CardBinderScreen extends HandledScreen<CardBinderScreenHandler> {
    private final DefaultedList<ItemStack> inventory;

    public CardBinderScreen(CardBinderScreenHandler handler, PlayerInventory playerInventory, Text text){
        super(handler, playerInventory, text);
        var cardBinderInventory = new CardBinderInventory(playerInventory.player);
        this.inventory = cardBinderInventory.getInventory();
        BINDER_TEXTURE = new Identifier("stack_the_cards", "textures/gui/binder"+cardBinderInventory.getColorAffix(playerInventory.player)+".png");
        BINDER_CARD_SLOT = new Identifier("stack_the_cards", "textures/gui/binder_slot.png");
        BINDER_TIES = new Identifier("stack_the_cards", "textures/gui/binder_binds.png");
    }
    public final Identifier BINDER_TEXTURE;
    public final Identifier BINDER_CARD_SLOT;
    public final Identifier BINDER_TIES;

    public PageTurnWidget nextPageButton;
    public PageTurnWidget previousPageButton;

    @Override
    protected void init() {
        this.backgroundHeight = 256;
        this.backgroundWidth = 256;
        playerInventoryTitleY = this.backgroundHeight - 84;
        playerInventoryTitleX = 48;
        titleY = -500;
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = (int) getXOrigin();
        int y = (int) getYOrigin();
        context.drawTexture(BINDER_TEXTURE, x, y, 0, 0, 256, 256);
        RenderSystem.disableDepthTest();
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.x+9, this.y+14, 100);
        var slots = handler.cardSlots;
        int slotIndex = 0;
        for (int page = 0; page < 2; page++) {
            for (int j = 0; j < 2; ++j) {
                for (int i = 0; i < 2; i++) {
                    var slot = slots.get(slotIndex);
                    if(!slot.isEnabled()){
                        slotIndex++;
                        continue;
                    }
                    matrices.push();
                    context.drawTexture(BINDER_CARD_SLOT, 54 * i + (132*page),73*j, 0, 0, 52, 71, 52, 71);
                    context.drawTexture(BINDER_TIES, 54 * i + (132*page),73*j-1, 0, 0, 52, 54, 52, 54);

                    var stack1 = slot.getStack();
                    matrices.translate(54 * i + (132*page),73*j,200);
                    if(!stack1.isEmpty()){
                        StackTheCardsClient.CARD_RENDERER.draw(matrices, context.getVertexConsumers(), stack1, 15728880, 52/(float)128);
                    }
                    matrices.scale(0.5f,0.5f, 0);
                    context.drawText(textRenderer,Text.literal(slotIndex % MAX_CARDS_PER_PAGE + MAX_CARDS_PER_PAGE * StackTheCardsClient.PAGE_INDEX + 1 + ""),
                            74, 110, Formatting.BLACK.getColorValue(), false);
                    matrices.pop();
                    slotIndex++;
                }
            }
        }
        matrices.pop();
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    private int getPageCount() {
        int leftOvers = inventory.size()%MAX_CARDS_PER_PAGE;
        return inventory.size()/MAX_CARDS_PER_PAGE + (leftOvers > 0 ? 1 : 0);
    }

    protected void addPageButtons() {
        StackTheCardsClient.PAGE_INDEX = Math.min(StackTheCardsClient.PAGE_INDEX, this.getPageCount()-1);
        int i = (this.width - 256) / 2;
        int j = 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 233, 175, true, button -> this.goToNextPage(), true));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i, 175, false, button -> this.goToPreviousPage(), true));
        this.updatePageButtons();
    }BookScreen

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
        this.handler.checkEnabledSlots();
        this.nextPageButton.visible = StackTheCardsClient.PAGE_INDEX < this.getPageCount() - 1;
        this.previousPageButton.visible = StackTheCardsClient.PAGE_INDEX > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
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
        return false;
    }
}
