package drai.dev.stackthecards;

import drai.dev.stackthecards.client.screen.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.recipes.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.resource.*;
import net.fabricmc.fabric.api.screenhandler.v1.*;
import net.minecraft.recipe.*;
import net.minecraft.registry.*;
import net.minecraft.registry.Registry;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.nio.charset.*;
import java.rmi.registry.*;

public class StackTheCards implements ModInitializer {
    public static final ScreenHandlerType<CardBinderScreenHandler> CARD_BINDER_SCREEN_HANDLER;
    static {
        CARD_BINDER_SCREEN_HANDLER =  Registry.register(Registries.SCREEN_HANDLER, new Identifier("stack_the_cards", "card_binder_screen"),
                new ScreenHandlerType<>(CardBinderScreenHandler::new, FeatureFlags.VANILLA_FEATURES) );
    }
    public static final RecipeSerializer<CardBinderColoringRecipe> BINDER_COLORING =
            Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("stack_the_cards", "color_card_binder"),
                    new SpecialRecipeSerializer<CardBinderColoringRecipe>(CardBinderColoringRecipe::new));
    public static final RecipeSerializer<CardBinderRemoveCustomizationRecipe> BINDER_REMOVE_CUSTOM =
            Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("stack_the_cards", "remove_custom_card_binder"),
                    new SpecialRecipeSerializer<CardBinderRemoveCustomizationRecipe>(CardBinderRemoveCustomizationRecipe::new));
    public static final RecipeSerializer<CardBinderCustomizationRecipe> CUSTOM_BINDER =
            Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("stack_the_cards", "custom_card_binder"),
                    new SpecialRecipeSerializer<CardBinderCustomizationRecipe>(CardBinderCustomizationRecipe::new));
    public static final RecipeSerializer<CardPackMultiplierRecipe> PACK_MULTIPLYING_RECIPE =
            Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("stack_the_cards", "pack_multiplying"),
                    new SpecialRecipeSerializer<CardPackMultiplierRecipe>(CardPackMultiplierRecipe::new));
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("stack_the_cards", "card_resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                CardGameRegistry.clear();
                for (var gameResource : manager.findResources("stc_cards/games", path-> path.getPath().endsWith(".json")).entrySet()){
                    JSONParser jsonParser = new JSONParser();
                    try{
                        JSONObject jsonObjectGame = (JSONObject) jsonParser.parse(new InputStreamReader(gameResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                        CardGame cardGame = CardGame.parse(jsonObjectGame);
                        CardGameRegistry.registerGame(cardGame);

                        for (var formattingResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/formatting", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(formattingResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardTextFormatting formatting = CardTextFormatting.parse(jsonObjectCard);
                                cardGame.addFormatting(formatting);
                            } catch (Exception e){
                                System.out.println("formatting json file "+formattingResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        for (var rarityResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/rarities", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(rarityResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardRarity rarity = CardRarity.parse(jsonObjectCard, cardGame);
                                cardGame.addRarity(rarity);
                            } catch (Exception e){
                                System.out.println("formatting json file "+rarityResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        for (var setResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/sets", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectSet = (JSONObject) jsonParser.parse(new InputStreamReader(setResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardSet cardSet = CardSet.parse(jsonObjectSet);
                                cardSet.setGame(cardGame);
                                cardGame.addSet(cardSet);
                                for (var cardResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/"+cardSet.getSetId()+"/cards", path-> path.getPath().endsWith(".json")).entrySet()){
                                    try{
                                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(cardResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                        CardData cardData = CardData.parse(jsonObjectCard, cardGame);
                                        cardData.setSet(cardSet);
                                        cardData.setGame(cardGame);
                                        cardSet.addCard(cardData);
                                    } catch (Exception e){
                                        System.out.println("card json file "+cardResource.getKey() + " was invalid: "+e.getMessage());
                                    }
                                }

                                //Parent Packs
                                for (var packResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/"+cardSet.getSetId()+"/parent_packs", path-> path.getPath().endsWith(".json")).entrySet()){
                                    try{
                                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                        CardPack cardPack = CardPack.parse(jsonObjectCard, cardGame, cardSet);
                                        cardPack.setSet(cardSet.getSetId());
                                        cardSet.addParentPacks(cardPack);
                                    } catch (Exception e){
                                        System.out.println("pack json file "+packResource.getKey() + " was invalid: "+e.getMessage());
                                    }
                                }

                                //Packs
                                for (var packResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/"+cardSet.getSetId()+"/packs", path-> path.getPath().endsWith(".json")).entrySet()){
                                    try{
                                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                        CardPack cardPack = CardPack.parse(jsonObjectCard, cardGame, cardSet);
                                        cardPack.setSet(cardSet.getSetId());
                                        cardSet.addPack(cardPack);
                                    } catch (Exception e){
                                        System.out.println("pack json file "+packResource.getKey() + " was invalid: "+e.getMessage());
                                    }
                                }
                            } catch (Exception e){
                                System.out.println("set json file "+setResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        for (var cardResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/cards", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                //TODO do this last
                                //todo make it possible for a pack to inherit from a parent. deep copy
//                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(cardResource.getValue().getInputStream(), StandardCharsets.UTF_8));
//                                GameCardData cardData = GameCardData.parse(jsonObjectCard);
//                                cardData.setGame(cardGame);
//                                cardGame.addCard(cardData);
                            } catch (Exception e){
                                System.out.println("game card json file "+cardResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        for (var connectionsResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/connections", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(connectionsResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardConnection connection = CardConnection.parse(jsonObjectCard, cardGame);
                                connection.setGame(cardGame);
                                cardGame.addConnection(connection);
                            } catch (Exception e){
                                System.out.println("connection json file "+connectionsResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        //parent packs
                        for (var packResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/parent_packs", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardPack cardPack = GameCardPack.parse(jsonObject, cardGame);
                                cardPack.setGame(cardGame);
                                cardGame.addParentPacks(cardPack);
                            } catch (Exception e){
                                System.out.println("game pack json file "+packResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        //packs
                        for (var packResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/packs", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardPack cardPack = GameCardPack.parse(jsonObject, cardGame);
                                cardPack.setGame(cardGame);
                                cardGame.addPack(cardPack);
                            } catch (Exception e){
                                System.out.println("game pack json file "+packResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }


                    } catch (Exception e){
                        System.out.println("game json file "+gameResource.getKey() + " was invalid: "+e.getMessage());
                    }
                }

                System.out.println("Testing-Cards give commands");
                for (var game : CardGameRegistry.getCardGames().values()) {
                    for (var card : game.getCards().keySet() ) {
                        System.out.println("/give @a stack_the_cards:card{CardData:[{card_id:"+card+", set_id:base, game_id:pokemon_tcg}]}");
                    }
                    for (var set : game.getCardSets().values() ) {
                        for (var card : set.getCards().keySet() ) {
                            System.out.println("/give @a stack_the_cards:card{CardData:[{card_id:"+card+", set_id:"+set.getSetId()+", game_id:pokemon_tcg}]}");
                        }
                        for (var pack : set.getCardPacks().keySet() ) {
                            System.out.println("/give @a stack_the_cards:card_pack{CardPackData:[{card_id:"+pack+", set_id:base, game_id:pokemon_tcg}]}");
                        }
                    }
                }
            }
        });
        Items.register();
        Registry.register(Registries.SOUND_EVENT, CardPackItem.PACK_RIP_IDENTIFIER, CardPackItem.PACK_RIP);
    }
}
