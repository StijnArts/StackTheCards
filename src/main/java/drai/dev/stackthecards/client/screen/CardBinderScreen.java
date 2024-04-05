package drai.dev.stackthecards.client.screen;

import drai.dev.stackthecards.items.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;

@Environment(EnvType.CLIENT)
public class CardBinderScreen extends HandledScreen<CardBinderScreenHandler> {
    private final DefaultedList<ItemStack> inventory;
    private static int cardsPerPage = 4;

    public CardBinderScreen(CardBinderScreenHandler handler, PlayerInventory playerInventory, Text text){
        super(handler, playerInventory, text);
        this.inventory = new CardBinderInventory().getInventory();
    }
    public static final Identifier BINDER_TEXTURE = new Identifier("stack_the_cards","textures/gui/binder.png");
    protected static final int MAX_TEXT_WIDTH = 114;
    protected static final int MAX_TEXT_HEIGHT = 128;
    protected static final int WIDTH = 192;
    protected static final int HEIGHT = 192;
    private int pageIndex;

    public PageTurnWidget nextPageButton;
    public PageTurnWidget previousPageButton;

    @Override
    protected void init() {
        this.addCloseButton();
        this.addPageButtons();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        int i = (this.width - 192) / 2;
        int j = 2;
        context.drawTexture(BINDER_TEXTURE, i, 2, 0, 0, 192, 192);
        //TODO draw card cells
        final MultilineText multilineText = MultilineText.create(textRenderer, Text.literal("The text is pretty long ".repeat(20)), width - 20);
        multilineText.drawWithShadow(context, 10, height / 2, 16, 0xffffff);
        /*if (this.cachedPageIndex != this.pageIndex) {
            StringVisitable stringVisitable = getPage(this.pageIndex);
            this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 114);
            this.pageIndexText = Text.translatable("book.pageIndicator", this.pageIndex + 1, Math.max(this.getPageCount(), 1));
        }
        this.cachedPageIndex = this.pageIndex;
        int k = this.textRenderer.getWidth(this.pageIndexText);
        context.drawText(this.textRenderer, this.pageIndexText, i - k + 192 - 44, 18, 0, false);
        int l = Math.min(128 / this.textRenderer.fontHeight, this.cachedPage.size());
        for (int m = 0; m < l; ++m) {
            OrderedText orderedText = this.cachedPage.get(m);
            context.drawText(this.textRenderer, orderedText, i + 36, 32 + m * this.textRenderer.fontHeight, 0, false);
        }
        Style style = this.getTextStyleAt(mouseX, mouseY);
        if (style != null) {
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
        }*/
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    /*private StringVisitable getPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < this.getPageCount()) {
            String string = this.pages.get(pageIndex);
            try {
                MutableText stringVisitable = Text.Serializer.fromJson(string);
                if (stringVisitable != null) {
                    return stringVisitable;
                }
            } catch (Exception exception) {
                // empty catch block
            }
            return StringVisitable.plain(string);
        }
        return StringVisitable.EMPTY;
    }*/

    private int getPageCount() {
        int leftOvers = inventory.size()%cardsPerPage;
        return inventory.size()/cardsPerPage + (leftOvers > 0 ? 1 : 0);
    }

    protected void addPageButtons() {
        int i = (this.width - 192) / 2;
        int j = 2;
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(i + 116, 159, true, button -> this.goToNextPage(), true));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(i + 43, 159, false, button -> this.goToPreviousPage(), true));
        this.updatePageButtons();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }
        this.updatePageButtons();
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.close()).dimensions(this.width / 2 - 100, 196, 200, 20).build());
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case 266: {
                this.previousPageButton.onPress();
                return true;
            }
            case 267: {
                this.nextPageButton.onPress();
                return true;
            }
        }
        return false;
    }
}
