package drai.dev.stackthecards.neoforge.client;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.*;
import net.neoforged.api.distmarker.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.*;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = StackTheCards.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ForgeClientEvents {

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.MouseScrolled.Post event) {
        // Register mouse scroll event when a screen is initialized
        StackTheCardsClient.scrollModifier+= (int) event.getScrollDeltaY();
    }
}
