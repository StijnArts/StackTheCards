package drai.dev.stackthecards.fabric.client;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.client.screen.*;
import drai.dev.stackthecards.tooltips.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.*;
import net.minecraft.client.gui.screens.*;

import java.util.concurrent.*;

import static drai.dev.stackthecards.StackTheCards.CARD_BINDER_SCREEN_HANDLER;

public final class StackTheCardsFabricClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        StackTheCardsClient.initClient();
        registerModelLoadingPlugin();
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        MenuScreens.register(CARD_BINDER_SCREEN_HANDLER, CardBinderScreen::new);
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CardTooltipData previewData)
                return new CardTooltipComponent(previewData);
            return null;
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> ScreenMouseEvents.afterMouseScroll(screen).register((_screen, x, y, horiz, vert) -> {
            StackTheCardsClient.scrollModifier+= (int) vert;
        }));
    }

    private static void registerModelLoadingPlugin() {
        var plugin = new STCFabricModelLoadingPlugin();
        PreparableModelLoadingPlugin.register((resourceManager, executor) -> CompletableFuture.completedFuture(resourceManager),
                plugin::onInitializeModelLoader);
        ModelLoadingPlugin.register(plugin);
    }


}
