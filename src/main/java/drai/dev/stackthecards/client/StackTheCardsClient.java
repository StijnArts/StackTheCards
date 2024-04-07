package drai.dev.stackthecards.client;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.screen.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.models.*;
import drai.dev.stackthecards.renderers.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.util.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.resource.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class StackTheCardsClient implements ClientModInitializer {
    public static CardRenderer CARD_RENDERER;
    public static CardTooltipRenderer CARD_TOOLTIP_RENDERER;
    public static List<Identifier> CARD_BACK_MODELS = new ArrayList<>();
    public static List<Identifier> CARD_PACK_MODELS = new ArrayList<>();
    public static boolean shiftKeyPressed;
    public static boolean ctrlKeyPressed;
    public static int scrollModifier = 0;
    private static ItemStack previousStack = null;
    public static boolean shiftKeyReleased = false;

    @Override
    public void onInitializeClient() {
        CARD_RENDERER = new CardRenderer();
        CARD_TOOLTIP_RENDERER = new CardTooltipRenderer(CARD_RENDERER);
        HandledScreens.register(StackTheCards.CARD_BINDER_SCREEN_HANDLER, CardBinderScreen::new);
        var plugin = new StackTheCardsModelLoadingPlugin();
        PreparableModelLoadingPlugin.register((resourceManager, executor) -> CompletableFuture.completedFuture(resourceManager),
                plugin::onInitializeModelLoader);
        ModelLoadingPlugin.register(plugin);

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CardTooltipData previewData)
                return new CardTooltipComponent(previewData);
            return null;
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenMouseEvents.afterMouseScroll(screen).register((_screen, x, y, horiz, vert) -> {
            scrollModifier+= vert;
//            System.out.println("scrollModifier: " + scrollModifier);
        }));

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("stack_the_cards", "card_resources_client");
            }

            @Override
            public void reload(ResourceManager manager) {
                StackTheCardsClient.CARD_RENDERER.clearStateTextures();
            }
        });
    }

    private static boolean isKeyPressed(@Nullable Key key) {
        if (key == null || key.equals(Key.UNKNOWN_KEY) || key.get().equals(InputUtil.UNKNOWN_KEY))
            return false;
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key.get().getCode());
    }

    public static void updateKeys() {
        var previousShiftKeyState = shiftKeyPressed;
        shiftKeyPressed = isKeyPressed(Key.cardLoreKey()) || isKeyPressed(Key.rightCardLoreKey());;
        if(previousShiftKeyState && !shiftKeyPressed) shiftKeyReleased = true;
        ctrlKeyPressed = isKeyPressed(Key.flipCardKey());
    }

    public static void modifyCardStackTooltip(Consumer<Collection<Text>> tooltip) {
        MutableText loreKeyHint = Text.literal("Control: ");
        loreKeyHint.fillStyle(Style.EMPTY.withColor(Formatting.GOLD));
        loreKeyHint.append(Text.literal("view card lore").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        tooltip.accept(List.of(loreKeyHint));
    }



    public static void modifyPackStackTooltip(Consumer<Collection<Text>> tooltip) {
        MutableText loreKeyHint = Text.literal("Control: ");
        loreKeyHint.fillStyle(Style.EMPTY.withColor(Formatting.GOLD));
        loreKeyHint.append(Text.literal("view pack details").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        tooltip.accept(List.of(loreKeyHint));
    }

    public static void checkToolTipForScrolling(ItemStack stack){
        if (previousStack == null || !ItemStack.areEqual(stack, previousStack) || stack.isEmpty()) {
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
