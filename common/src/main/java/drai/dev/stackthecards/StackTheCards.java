package drai.dev.stackthecards;

import dev.architectury.registry.*;
import drai.dev.stackthecards.client.screen.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.components.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.recipes.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.server.packs.*;
import net.minecraft.world.flag.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.*;

public final class StackTheCards {
    public static final String MOD_ID = "stackthecards";
    public static final MenuType<CardBinderScreenHandler> CARD_BINDER_SCREEN_HANDLER;
    static {
        CARD_BINDER_SCREEN_HANDLER =  Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath("stack_the_cards", "card_binder_screen"),
                new MenuType<>(CardBinderScreenHandler::new, FeatureFlags.VANILLA_SET) );
    }
    public static final RecipeSerializer<CardBinderColoringRecipe> BINDER_COLORING =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("stack_the_cards", "color_card_binder"),
                    new SimpleCraftingRecipeSerializer<CardBinderColoringRecipe>(CardBinderColoringRecipe::new));
    public static final RecipeSerializer<CardBinderRemoveCustomizationRecipe> BINDER_REMOVE_CUSTOM =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("stack_the_cards", "remove_custom_card_binder"),
                    new SimpleCraftingRecipeSerializer<CardBinderRemoveCustomizationRecipe>(CardBinderRemoveCustomizationRecipe::new));
    public static final RecipeSerializer<CardBinderCustomizationRecipe> CUSTOM_BINDER =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("stack_the_cards", "custom_card_binder"),
                    new SimpleCraftingRecipeSerializer<CardBinderCustomizationRecipe>(CardBinderCustomizationRecipe::new));
    public static final RecipeSerializer<CardPackMultiplierRecipe> PACK_MULTIPLYING_RECIPE =
            Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, ResourceLocation.fromNamespaceAndPath("stack_the_cards", "pack_multiplying"),
                    new SimpleCraftingRecipeSerializer<CardPackMultiplierRecipe>(CardPackMultiplierRecipe::new));

    public static void init() {
        ReloadListenerRegistry.register(PackType.SERVER_DATA, new CardResourceReloadListener());
        StackTheCardsComponentTypes.touch();
        StackTheCardsComponentTypes.register();
        StackTheCardsItems.touch();
        StackTheCardsItems.register();
        Registry.register(BuiltInRegistries.SOUND_EVENT, CardPackItem.PACK_RIP_IDENTIFIER, CardPackItem.PACK_RIP);
    }
}
