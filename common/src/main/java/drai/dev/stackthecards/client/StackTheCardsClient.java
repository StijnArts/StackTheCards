package drai.dev.stackthecards.client;

import com.mojang.blaze3d.platform.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.renderers.*;
import drai.dev.stackthecards.util.*;
import net.fabricmc.api.*;
import net.minecraft.*;
import net.minecraft.client.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;

public class StackTheCardsClient {
    public static CardRenderer CARD_RENDERER;
    public static CardTooltipRenderer CARD_TOOLTIP_RENDERER;
    public static List<ResourceLocation> CARD_BACK_MODELS = new ArrayList<>();
    public static List<ResourceLocation> CARD_PACK_MODELS = new ArrayList<>();
    public static boolean cardLoreKeyPressed;
    public static boolean ctrlKeyPressed;
    public static int scrollModifier = 0;
    private static ItemStack previousStack = null;
    public static boolean shiftKeyReleased = false;
    public static boolean shiftKeyPressed = false;

    public static void initClient() {
        CARD_RENDERER = new CardRenderer();
        CARD_TOOLTIP_RENDERER = new CardTooltipRenderer(CARD_RENDERER);
        ItemGroups.touch();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("stack_the_cards", "item_group"), ItemGroups.CARD_ITEM_GROUP);
    }

    private static boolean isKeyPressed(@Nullable Key key) {
        if (key == null || key.equals(Key.UNKNOWN_KEY) || key.get().equals(InputConstants.UNKNOWN))
            return false;
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.get().getValue());
    }

    public static void updateKeys() {
        var previousShiftKeyState = cardLoreKeyPressed;
        cardLoreKeyPressed = isKeyPressed(Key.cardLoreKey()) || isKeyPressed(Key.rightCardLoreKey());
        shiftKeyPressed = isKeyPressed(Key.cardPlacementKey());
        if(previousShiftKeyState && !cardLoreKeyPressed) shiftKeyReleased = true;
        ctrlKeyPressed = isKeyPressed(Key.flipCardKey());
    }

    public static void modifyCardStackTooltip(Consumer<Collection<Component>> tooltip) {
        MutableComponent loreKeyHint = Component.literal("Control: ");
        loreKeyHint.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        loreKeyHint.append(Component.literal("view card lore").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.accept(List.of(loreKeyHint));
    }

    public static void modifyPackStackTooltip(Consumer<Collection<Component>> tooltip) {
        MutableComponent loreKeyHint = Component.literal("Control: ");
        loreKeyHint.setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
        loreKeyHint.append(Component.literal("view pack details").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        tooltip.accept(List.of(loreKeyHint));
    }

    public static void checkToolTipForScrolling(ItemStack stack){
        if (previousStack == null || !ItemStack.isSameItem(stack, previousStack) || stack.isEmpty()) {
            scrollModifier = 0;
            previousStack = stack;
        }
        if(shiftKeyReleased) {
            scrollModifier = 0;
            shiftKeyReleased = false;
        }
    }

    public static int PAGE_INDEX = 0;

    public static int getScrollModifier(){
        return scrollModifier * 5;
    }
}
