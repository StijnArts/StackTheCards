package drai.dev.stackthecards.data;

import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.nio.charset.*;

public class CardResourceReloadListener extends SimplePreparableReloadListener<Void> {
    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        return null;
    }

    @Override
    protected void apply(Void object, ResourceManager manager, ProfilerFiller profilerFiller) {
        CardGameRegistry.clear();
        for (var gameResource : manager.listResources("stc_cards/games", path-> path.getPath().endsWith(".json")).entrySet()){
            JSONParser jsonParser = new JSONParser();
            try{
                JSONObject jsonObjectGame = (JSONObject) jsonParser.parse(new InputStreamReader(gameResource.getValue().open(), StandardCharsets.UTF_8));
                CardGame cardGame = CardGame.parse(jsonObjectGame, gameResource.getKey().getNamespace());
                CardGameRegistry.registerGame(cardGame);

                for (var formattingResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/formatting", path-> path.getPath().endsWith(".json")).entrySet()){
                    try{
                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(formattingResource.getValue().open(), StandardCharsets.UTF_8));
                        CardTextFormatting formatting = CardTextFormatting.parse(jsonObjectCard);
                        cardGame.addFormatting(formatting);
                    } catch (Exception e){
                        System.out.println("formatting json file "+formattingResource.getKey() + " was invalid: "+e.getMessage());
                    }
                }

                for (var rarityResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/rarities", path-> path.getPath().endsWith(".json")).entrySet()){
                    try{
                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(rarityResource.getValue().open(), StandardCharsets.UTF_8));
                        CardRarity rarity = CardRarity.parse(jsonObjectCard, cardGame);
                        cardGame.addRarity(rarity);
                    } catch (Exception e){
                        System.out.println("formatting json file "+rarityResource.getKey() + " was invalid: "+e.getMessage());
                    }
                }

                for (var setResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/sets/_ids", path-> path.getPath().endsWith(".json")).entrySet()){
                    try{
                        JSONObject jsonObjectSet = (JSONObject) jsonParser.parse(new InputStreamReader(setResource.getValue().open(), StandardCharsets.UTF_8));
                        CardSet cardSet = CardSet.parse(jsonObjectSet);
                        cardSet.setGame(cardGame);
                        cardGame.addSet(cardSet);
                        for (var cardResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/sets/"+cardSet.getSetId()+"/cards", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(cardResource.getValue().open(), StandardCharsets.UTF_8));
                                CardData cardData = CardData.parse(jsonObjectCard, cardGame, cardResource.getKey().getNamespace());
                                cardData.setSet(cardSet);
                                cardData.setGame(cardGame);
                                cardSet.addCard(cardData);
                            } catch (Exception e){
                                System.out.println("card json file "+cardResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        //Parent Packs
                        for (var packResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/sets/"+cardSet.getSetId()+"/parent_packs", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().open(), StandardCharsets.UTF_8));
                                CardPack cardPack = CardPack.parse(jsonObjectCard, cardGame, cardSet, packResource.getKey().getNamespace());
                                cardPack.setSet(cardSet.getSetId());
                                cardSet.addParentPacks(cardPack);
                            } catch (Exception e){
                                System.out.println("pack json file "+packResource.getKey() + " was invalid: "+e.getMessage());
                            }
                        }

                        //Packs
                        for (var packResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/sets/"+cardSet.getSetId()+"/packs", path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().open(), StandardCharsets.UTF_8));
                                CardPack cardPack = CardPack.parse(jsonObjectCard, cardGame, cardSet, packResource.getKey().getNamespace());
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

                for (var cardResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/cards", path-> path.getPath().endsWith(".json")).entrySet()){
                    try{
                        //TODO do this last
                        //todo make it possible for a pack to inherit from a parent. deep copy
//                                JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(cardResource.getValue().open(), StandardCharsets.UTF_8));
//                                GameCardData cardData = GameCardData.parse(jsonObjectCard);
//                                cardData.setGame(cardGame);
//                                cardGame.addCard(cardData);
                    } catch (Exception e){
                        System.out.println("game card json file "+cardResource.getKey() + " was invalid: "+e.getMessage());
                    }
                }

                for (var connectionsResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/connections", path-> path.getPath().endsWith(".json")).entrySet()){
                    try{
                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(connectionsResource.getValue().open(), StandardCharsets.UTF_8));
                        CardConnection connection = CardConnection.parse(jsonObjectCard, cardGame);
                        connection.setGame(cardGame);
                        cardGame.addConnection(connection);
                        for (var row : connection.getLayout()) {
                            for (var card : row) {
                                System.out.println("Command for Connection testing: /give @a stack_the_cards:card{CardData:[{card_id:"+card.self.cardId+", set_id:"+card.self.setId+", game_id:pokemon_tcg}]}");
                            }
                        }
                    } catch (Exception e){
                        System.out.println("connection json file "+connectionsResource.getKey() + " was invalid: "+e.getMessage());
                    }
                }

                //parent packs
                for (var packResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/parent_packs", path-> path.getPath().endsWith(".json")).entrySet()){
                    try{
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().open(), StandardCharsets.UTF_8));
                        CardPack cardPack = GameCardPack.parse(jsonObject, cardGame, packResource.getKey().getNamespace());
                        cardPack.setGame(cardGame);
                        cardGame.addParentPacks(cardPack);
                    } catch (Exception e){
                        System.out.println("game pack json file "+packResource.getKey() + " was invalid: "+e.getMessage());
                    }
                }

                //packs
                for (var packResource : manager.listResources("stc_cards/"+cardGame.getGameId()+"/packs", path-> path.getPath().endsWith(".json")).entrySet()){
                    try{
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(packResource.getValue().open(), StandardCharsets.UTF_8));
                        CardPack cardPack = GameCardPack.parse(jsonObject, cardGame, packResource.getKey().getNamespace());
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
}
