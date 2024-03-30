package drai.dev.stackthecards.client;

import drai.dev.stackthecards.models.*;
import drai.dev.stackthecards.registry.Items;
import drai.dev.stackthecards.renderers.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.util.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.resource.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.lwjgl.glfw.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class StackTheCardsClient implements ClientModInitializer {
    public static CardRenderer CARD_RENDERER;
    public static CardTooltipRenderer CARD_TOOLTIP_RENDERER;
    public static List<Identifier> CARD_BACK_MODELS = new ArrayList<>();
    public static Sprite TEST_BACK_SPRITE;
    public static NativeImage TEST_BACK;
    public static ItemModels MODELS;
//    public static Map<Identifier, > cardTextures = new HashMap<>();
    /**
     * Runs the mod initializer on the client environment.
     */
    public static NativeImage TEST;
    public static NativeImage TOOLTIP_TEXTURE;
    public static boolean shiftKeyPressed;
    public static boolean ctrlKeyPressed;
    public static int scrollModifier = 0;
    public static int getScrollModifier(){
        return scrollModifier * 3;
    }
    private static ItemStack previousStack = null;
    public static boolean shiftKeyReleased = false;

    public static void modifyStackTooltip(ItemStack stack, Consumer<Collection<Text>> tooltip) {
        MutableText loreKeyHint = Text.literal("Shift: ");
        loreKeyHint.fillStyle(Style.EMPTY.withColor(Formatting.GOLD));
        loreKeyHint.append(Text.literal("view card lore").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
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

    @Override
    public void onInitializeClient() {
        CARD_RENDERER = new CardRenderer();
        CARD_TOOLTIP_RENDERER = new CardTooltipRenderer(CARD_RENDERER);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("stack_the_cards", "card_resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                CARD_RENDERER.clearStateTextures();
                for (var resource : manager.findResources("stc_cards/cards", path-> path.getPath().endsWith(".png")).entrySet()){

                }
            }
        });
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
        Items.register();
    }

    private static boolean isKeyPressed(@Nullable Key key) {
        if (key == null || key.equals(Key.UNKNOWN_KEY) || key.get().equals(InputUtil.UNKNOWN_KEY))
            return false;
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key.get().getCode());
    }

    //TODO add card tooltip scrolling by tracking which stack was viewed and resetting the value if a new stack is viewed
    public static void updateKeys() {
        var previousShiftKeyState = shiftKeyPressed;
        shiftKeyPressed = isKeyPressed(Key.cardLoreKey());
        if(previousShiftKeyState && !shiftKeyPressed) shiftKeyReleased = true;
        ctrlKeyPressed = isKeyPressed(Key.flipCardKey());
    }
}
