package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.item.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.json.simple.*;

import java.util.*;

import static drai.dev.stackthecards.data.carddata.CardData.*;
import static drai.dev.stackthecards.items.Card.getCardDataNBT;

public class CardPack {
    private static final String JSON_PACK_ID_KEY = "packId";
    private static final String STORED_CARD_PACK_DATA_KEY = "CardPackData";
    private String packId;
    private String gameId;
    private String setId;
    private final List<CardTooltipSection> hoverTooltipSections = new ArrayList<>();
    private final List<CardTooltipSection> detailTooltipSections = new ArrayList<>();
    private String packName;

    public CardPack(String gameId, String setId, String packId) {
        this.packId = packId;
        this.gameId = gameId;
        this.setId = setId;
        this.packName = packId;
    }

    public static CardPack getCardPack(ItemStack stack) {
        var cardNBTData = getCardDataNBT(stack, STORED_CARD_PACK_DATA_KEY);
        var cardIdentifier = CardIdentifier.getCardIdentifier(cardNBTData);
        return CardGameRegistry.getPackData(cardIdentifier);
    }

    public static CardPack parse(JSONObject json, CardGame game, CardSet cardSet) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_PACK_ID_KEY)) throw new MalformedJsonException("Card pack Json was empty");
        CardPack cardPack;
        try{
            cardPack = new CardPack(game.getGameId(), cardSet.getSetId(), (String) json.get(JSON_PACK_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card pack id was malformed");
        }
        if(json.containsKey(JSON_NAME_HEADER_KEY)){
            try{
                cardPack.packName = (String) json.get(JSON_NAME_HEADER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed");
            }
        }
        if(json.containsKey(JSON_CARD_HOVER_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_HOVER_TOOLTIP_KEY);
                for (var section : contents) {
                    cardPack.hoverTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed");
            }
        }
        if(json.containsKey(JSON_CARD_DETAIL_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_DETAIL_TOOLTIP_KEY);
                for (var section : contents) {
                    cardPack.detailTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail tooltip value was malformed");
            }
        }
        if(json.containsKey(JSON_DETAIL_HEADER_KEY)){
            try{
                cardPack.detailHeader = new CardTooltipLine();
                var textContents = json.get(JSON_DETAIL_HEADER_KEY);
                if(textContents instanceof String contentsAsString){
                    cardPack.detailHeader.text = contentsAsString;
                } else if(textContents instanceof JSONArray contentsAsJsonArray){
                    for (var textSegment: contentsAsJsonArray) {
                        cardPack.detailHeader.lineSegments.add(CardTooltipLine.parse((JSONObject) textSegment, game));
                    }
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail header value was malformed");
            }
        }
        return cardPack;
    }

    public String getPackId() {
        return packId;
    }

    public void setGame(CardGame cardGame) {
        this.gameId = cardGame.getGameId();
    }

    public void setSet(String cardSet) {
        this.setId = cardSet;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Collection<? extends Text> getDetailToolTips() {
        return getTexts(detailTooltipSections);
    }

    public Collection<? extends Text> getTooltipsDescriptors() {
        return getTexts(hoverTooltipSections);
    }

    public Identifier getModelIdentifier() {
        if(packId!=null && !packId.equals("missing")) return new Identifier("stack_the_cards", "stc_cards/packs/"+packId);

        var cardGame = this.getCardGame();
        if(cardGame!=null) {
            var cardGamePackModel = cardGame.getCardPackModel();
            if(cardGamePackModel !=null) return cardGamePackModel;
        }

        return new Identifier("stack_the_cards", "stc_cards/packs/fallback");
    }

    private CardGame getCardGame() {
        var cardGame = CardGameRegistry.getCardGame(gameId);
        if(cardGame != null){
            return cardGame;
        } else {
            var cardSet = getCardSet();
            if(cardSet!=null) return cardSet.getCardGame();
        }
        return null;
    }

    private CardSet getCardSet() {
        return CardGameRegistry.getCardGame(gameId).getCardSet(setId);
    }

    public String getTextureId() {
        return getCardSet().getSetIdentifier() + "_" + packId;
    }

    public String getPackTextureLocation() {
        return gameId + "/" + getCardSet().getSetId() + "/" + packId;
    }

    public Identifier getFallbackModel() {
        return new Identifier("stack_the_cards", "stc_cards/packs/fallback");
    }

    private CardTooltipLine detailHeader;
    public Text getPackNameLabel() {
        if(!StackTheCardsClient.shiftKeyPressed){
            return Text.literal(getPackName()).fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        } else {
            if(detailHeader == null) return Text.literal(getPackName()).fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
            return detailHeader.getTextComponent();
        }
    }

    private String getPackName() {
        return packName;
    }
}
