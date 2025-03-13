package drai.dev.stackthecards;

import dev.architectury.event.events.common.*;
import dev.architectury.platform.*;
import dev.architectury.registry.*;
import dev.architectury.registry.registries.*;
import dev.architectury.utils.*;
import drai.dev.stackthecards.client.screen.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.components.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.network.*;
import drai.dev.stackthecards.recipes.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.*;
import net.minecraft.server.packs.*;
import net.minecraft.sounds.*;
import net.minecraft.world.flag.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.*;

import static drai.dev.stackthecards.items.CardPackItem.PACK_RIP_IDENTIFIER;

public final class StackTheCards {
    public static final String MOD_ID = "stack_the_cards";
    public static final DeferredRegister<MenuType<?>> MENU_TYPE_DEFERRED_REGISTER = DeferredRegister.create(MOD_ID, Registries.MENU);
    public static final DeferredRegister<SoundEvent> SOUND_EVENT_DEFERRED_REGISTER = DeferredRegister.create(MOD_ID, Registries.SOUND_EVENT);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER_DEFERRED_REGISTER = DeferredRegister.create(MOD_ID, Registries.RECIPE_SERIALIZER);
    public static final RegistrySupplier<MenuType<CardBinderScreenHandler>> CARD_BINDER_SCREEN_HANDLER = MENU_TYPE_DEFERRED_REGISTER.register("card_binder_screen", ()->
            new MenuType<>(CardBinderScreenHandler::new, FeatureFlags.VANILLA_SET));

    public static RegistrySupplier<SoundEvent> PACK_RIP =
        SOUND_EVENT_DEFERRED_REGISTER.register(PACK_RIP_IDENTIFIER.getPath(), ()-> SoundEvent.createVariableRangeEvent(PACK_RIP_IDENTIFIER));

    public static final RegistrySupplier<RecipeSerializer<CardBinderColoringRecipe>> BINDER_COLORING =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register("color_card_binder", ()->
                    new SimpleCraftingRecipeSerializer<CardBinderColoringRecipe>(CardBinderColoringRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<CardBinderRemoveCustomizationRecipe>> BINDER_REMOVE_CUSTOM =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register("remove_custom_card_binder",()->
                    new SimpleCraftingRecipeSerializer<CardBinderRemoveCustomizationRecipe>(CardBinderRemoveCustomizationRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<CardBinderCustomizationRecipe>> CUSTOM_BINDER =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register("custom_card_binder",()->
                    new SimpleCraftingRecipeSerializer<CardBinderCustomizationRecipe>(CardBinderCustomizationRecipe::new));
    public static final RegistrySupplier<RecipeSerializer<CardPackMultiplierRecipe>> PACK_MULTIPLYING_RECIPE =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register("pack_multiplying",()->
                    new SimpleCraftingRecipeSerializer<CardPackMultiplierRecipe>(CardPackMultiplierRecipe::new));

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new CardResourceReloadListener());
        StackTheCardsComponentTypes.touch();
        StackTheCardsComponentTypes.register();
        StackTheCardsItems.touch();
        StackTheCardsItems.register();
        MENU_TYPE_DEFERRED_REGISTER.register();
        RECIPE_SERIALIZER_DEFERRED_REGISTER.register();
        SOUND_EVENT_DEFERRED_REGISTER.register();
        if(Platform.getEnvironment() == Env.SERVER) {
            StackTheCardsNetworking.registerPackets();
            PlayerEvent.PLAYER_JOIN.register(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
//                StackTheCardsNetworking.CHANNEL.sendToPlayer(serverPlayer);
                    StackTheCardsNetworking.syncRegistryToClient(serverPlayer);
                }
            });
        }
         
//        StackTheCardsNetworking.CHANNEL.register(CardGameRegistryMessage.class, CardGameRegistryMessage::encode, CardGameRegistryMessage::new, CardGameRegistryMessage::apply);
    }
}
