package drai.dev.stackthecards;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.cardData.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.nio.charset.*;

public class StackTheCards implements ModInitializer {

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
                                for (var packResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/"+cardSet.getSetId()+"/packs", path-> path.getPath().endsWith(".json")).entrySet()){
                                    try{
//                                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().getInputStream(), StandardCharsets.UTF_8));
//                                        CardPack cardPack = CardPack.parse(jsonObjectCard);
//                                        cardPack.setSet(cardSet);
//                                        cardSet.addPack(cardPack);
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
//                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(connectionsResource.getValue().getInputStream(), StandardCharsets.UTF_8));
//                                CardConnection connection = CardConnection.parse(jsonObjectCard);
//                                connection.setGame(cardGame);
//                                cardGame.addConnection(connection);
                            } catch (Exception e){
                                System.out.println("connection json file "+connectionsResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        //packs
                        for (var packResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/packs", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
//                                JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().getInputStream(), StandardCharsets.UTF_8));
//                                CardPack cardPack = CardPack.parse(jsonObject);
//                                cardPack.setGame(cardGame);
//                                cardGame.addPack(cardPack);
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
                            System.out.println("/give @a stack_the_cards:card{CardData:[{card_id:"+card+", set_id:base, game_id:pokemon_tcg}]}");
                        }
                    }
                }
            }
        });
        Items.register();
    }
}
