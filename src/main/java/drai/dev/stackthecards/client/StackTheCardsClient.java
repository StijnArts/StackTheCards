package drai.dev.stackthecards.client;

import drai.dev.stackthecards.models.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import drai.dev.stackthecards.renderers.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.util.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.*;
import net.minecraft.client.resource.metadata.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.atlas.*;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.resource.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.lang.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static net.minecraft.client.texture.SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;

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
    public static boolean cardLoreKeyPressed;

    public static void modifyStackTooltip(ItemStack itemStack, Consumer<Collection<Text>> tooltip) {
        MutableText loreKeyHint = Text.literal("Shift: ");
        loreKeyHint.fillStyle(Style.EMPTY.withColor(Formatting.GOLD));
        loreKeyHint.append(Text.literal("view card lore").setStyle(Style.EMPTY.withColor(Formatting.GRAY)));
        tooltip.accept(List.of(loreKeyHint));
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
//                cardTextures.clear();
                for (var resource : manager.findResources("stc_cards/cards", path-> path.getPath().endsWith(".png")).entrySet()){
                    MinecraftClient.getInstance().getTextureManager().registerTexture(resource.getKey(), new SpriteAtlasTexture(resource.getKey()));
                    try(InputStream is = resource.getValue().getInputStream()){
                        TEST = NativeImage.read(is);
                    } catch (Exception e){

                    }
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
        Items.register();
    }

    private static boolean isKeyPressed(@Nullable Key key) {
        if (key == null || key.equals(Key.UNKNOWN_KEY) || key.get().equals(InputUtil.UNKNOWN_KEY))
            return false;
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key.get().getCode());
    }

    public static void updateKeys() {
        cardLoreKeyPressed = isKeyPressed(Key.cardLoreKey());
    }
}
