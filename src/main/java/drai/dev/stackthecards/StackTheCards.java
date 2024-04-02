package drai.dev.stackthecards;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.resource.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

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
                        for (var setResource : manager.findResources("stc_cards/"+cardGame.getGameId(), path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObjectSet = (JSONObject) jsonParser.parse(new InputStreamReader(setResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardSet cardSet = CardSet.parse(jsonObjectSet);
                                cardSet.setGame(cardGame);
                                for (var cardResource : manager.findResources("stc_cards/"+cardGame.getGameId()+"/"+cardSet.setId, path-> path.getPath().endsWith(".json")).entrySet()){
                                    try{
                                        JSONObject jsonObjectCard = (JSONObject) jsonParser.parse(new InputStreamReader(cardResource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                        CardData cardData = CardData.parse(jsonObjectSet);
                                        cardSet.setSet(CardGameRegistry.getCardGame(cardSet.cardGameId));
                                    } catch (Exception e){
                                        System.out.println("game json file "+cardResource.getKey() + " was invalid.");
                                    }
                                }
                            } catch (Exception e){
                                System.out.println("set json file "+setResource.getKey() + " was invalid.");
                            }
                        }
                        for (var resource : manager.findResources("stc_cards/"+cardGame.getGameId(), path-> path.getPath().endsWith(".json")).entrySet()){
                            try{
                                JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(resource.getValue().getInputStream(), StandardCharsets.UTF_8));
                                CardSet cardSet = CardSet.parse(jsonObject);
                                cardSet.setGame(CardGameRegistry.getCardGame(cardSet.cardGameId));
                            } catch (Exception e){
                                System.out.println("game json file "+resource.getKey() + " was invalid.");
                            }
                        }
                    } catch (Exception e){
                        System.out.println("game json file "+gameResource.getKey() + " was invalid.");
                    }
                }


            }
        });


    }
}
