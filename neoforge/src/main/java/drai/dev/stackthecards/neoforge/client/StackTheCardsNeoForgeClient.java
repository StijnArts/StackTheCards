package drai.dev.stackthecards.neoforge.client;

import com.mojang.datafixers.util.*;
import dev.architectury.registry.registries.*;
import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.client.screen.*;
import drai.dev.stackthecards.models.*;
import drai.dev.stackthecards.neoforge.*;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.server.packs.resources.*;
import net.minecraft.world.flag.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.*;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.neoforge.client.event.*;

import java.util.*;
import java.util.concurrent.*;

import static drai.dev.stackthecards.StackTheCards.MOD_ID;

@EventBusSubscriber(modid = StackTheCards.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class StackTheCardsNeoForgeClient {
    public static boolean hasBeenInitialized = false;
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        if (hasBeenInitialized) return;
        StackTheCardsClient.initClient();
        hasBeenInitialized = true;
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CardTooltipComponentServerSafe.class, component -> new CardTooltipComponent(component.getCardTooltipData()));
    }

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(StackTheCards.CARD_BINDER_SCREEN_HANDLER.get(), CardBinderScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((barrier, manager, pro, profilerFiller, ex, _d) -> {
            return CompletableFuture.runAsync(() -> {
                StackTheCardsModelLoader.loadModels(manager);
            }, ex);
        });
    }

    // Client-side mod bus event handler
    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        // The model id, relative to `assets/<namespace>/models/<path>.json`

        StackTheCardsClient.CARD_BACK_MODELS.forEach(model->event.register(ModelResourceLocation.standalone(model)));
        StackTheCardsClient.CARD_PACK_MODELS.forEach(model->event.register(ModelResourceLocation.standalone(model)));
//        event.register(new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("examplemod", "block/example_unused_model")));
    }
}
