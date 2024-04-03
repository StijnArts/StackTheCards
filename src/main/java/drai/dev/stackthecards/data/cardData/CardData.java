package drai.dev.stackthecards.data.cardData;

import com.google.gson.stream.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.renderers.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.json.simple.*;

import java.util.*;

public class CardData {
    private static final String JSON_CARD_ID_KEY = "cardId";
    private static final String JSON_CARD_HOVER_TOOLTIP_KEY = "textSectionsForHoverTooltip";
    private static final String JSON_CARD_DETAIL_TOOLTIP_KEY = "textSectionsForDetailTooltip";
    public static final String JSON_ROUNDED_CORNERS_ID_KEY = "hasRoundedCorners";
    public static final String JSON_DETAIL_HEADER_KEY = "detailHeader";
    public static final String JSON_NAME_HEADER_KEY = "name";
    //    private static CardSet TEST_CARD_SET = new CardSet();
    protected CardSet cardSet = new CardSet("missing");
    protected String cardId;
    protected String gameId = "missing";
    private Optional<Boolean> hasRoundedCorners = Optional.empty();
    private final List<CardTooltipSection> hoverTooltipSections = new ArrayList<>();
    private final List<CardTooltipSection> detailTooltipSections = new ArrayList<>();
    private CardTooltipLine detailHeader;
    public String cardName = "Missing Card Data";

    public CardData(String cardId) {
//        this.cardSet = cardSet;
        this.cardId = cardId;
    }

    public static CardData parse(JSONObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_CARD_ID_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        CardData cardData;
        try{
            cardData = new CardData((String) json.get(JSON_CARD_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card game id was malformed");
        }
        if(json.containsKey(JSON_NAME_HEADER_KEY)){
            try{
                cardData.cardName = (String) json.get(JSON_NAME_HEADER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed");
            }
        }
        if(json.containsKey(JSON_ROUNDED_CORNERS_ID_KEY)){
            try{
                cardData.setHasRoundedCorners((boolean) json.get(JSON_ROUNDED_CORNERS_ID_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed");
            }
        }
        if(json.containsKey(JSON_CARD_HOVER_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_HOVER_TOOLTIP_KEY);
                for (var section : contents) {
                    cardData.hoverTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed");
            }
        }
        if(json.containsKey(JSON_CARD_DETAIL_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_DETAIL_TOOLTIP_KEY);
                for (var section : contents) {
                    cardData.detailTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail tooltip value was malformed");
            }
        }
        if(json.containsKey(JSON_DETAIL_HEADER_KEY)){
            try{
                cardData.detailHeader = new CardTooltipLine();
                var textContents = json.get(JSON_DETAIL_HEADER_KEY);
                if(textContents instanceof String contentsAsString){
                    cardData.detailHeader.text = contentsAsString;
                } else if(textContents instanceof JSONArray contentsAsJsonArray){
                    for (var textSegment: contentsAsJsonArray) {
                        cardData.detailHeader.lineSegments.add(CardTooltipLine.parse((JSONObject) textSegment, game));
                    }
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail header value was malformed");
            }
        }
//        if(json.containsKey(JSON_GAME_CARD_BACK_CARD_KEY)){
//            try{
//                cardData.setCardBackTextureName((String) json.get(JSON_GAME_CARD_BACK_CARD_KEY));
//            } catch (Exception e){
//                throw new MalformedJsonException("Card back cardId was malformed");
//            }
//        }
        return cardData;
    }

    private void setHasRoundedCorners(boolean b) {
        this.hasRoundedCorners = Optional.of(b);
    }

    public CardSet getCardSet() {
        return this.cardSet;
    }
    public Pair<Integer, Integer> getCardImage(){
        var cardTexture = CardRenderer.getCardTexture(this, false);
        return new Pair<>(cardTexture.getOriginalImageHeight(),
                cardTexture.getOriginalImageWidth());
//        return StackTheCardsClient.TEST;
    }
    public String getTextureId() {
        return cardSet.getSetIdentifier() + "_" + cardId;
    }

    public CardIdentifier getCardIdentifier() {
        return new CardIdentifier(gameId, getCardSet().getSetId(), cardId);
    }

    public String getCardName() {
        return cardName;
    }

    public int getMaxSide() {
        return Math.max(getHeight(), getWidth());
    }

    public int getWidth() {
        return getCardImage().getRight();
    }

    public double getYOffset() {
        return (getMaxSide()-(double) getHeight())/2;
    }

    public double getXOffset() {
        return (getMaxSide()-(double) getWidth())/2;
    }

    public int getHeight() {
        return getCardImage().getLeft();
    }

    public static Text NEW_LINE = Text.literal(" ");
    public static Text TAB = Text.literal("      ");
    public List<? extends Text> getDetailToolTips() {
        return getTexts(detailTooltipSections);
    }

    @NotNull
    private ArrayList<Text> getTexts(List<CardTooltipSection> sections) {
        var tooltips = new ArrayList<Text>();
        for (int i = 0; i < sections.size(); i++) {
            var section = sections.get(i);
            tooltips.addAll(section.getText());
            if(i < sections.size()-1 && !section.noLineBreak){
                tooltips.add(NEW_LINE);
            }
        }

        return tooltips;
    }

    public Text getCardNameLabel() {
        if(!StackTheCardsClient.shiftKeyPressed){
            return Text.literal(getCardName()).fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
        } else {
            if(detailHeader == null) return Text.literal(getCardName()).fillStyle(Style.EMPTY.withColor(Formatting.WHITE));
            return detailHeader.getTextComponent();
        }
    }

    public List<Text> getTooltipsDescriptors(){
        return getTexts(hoverTooltipSections);
    }

    public boolean hasRoundedCorners() {
        return hasRoundedCorners.orElseGet(() -> getCardSet().hasRoundedCorners.orElseGet(() -> getCardGame().hasRoundedCorners.orElse(false)));

    }

    public CardGame getCardGame() {
        var cardGame = CardGameRegistry.getCardGame(gameId);
        if(cardGame != null){
            return cardGame;
        } else {
            var cardSet = getCardSet();
            if(cardSet!=null) return cardSet.getCardGame();
        }
        return null;
    }

    public Identifier getModelIdentifier() {
        if(cardSet!=null){
            var cardSetBackModel = this.cardSet.getCardBackModel();
            if(cardSetBackModel != null) return cardSetBackModel;
        }
        var cardGame = this.getCardGame();
        if(cardGame!=null) {
            var cardGameBackModel = cardGame.getCardBackModel();
            if(cardGameBackModel !=null) return cardGameBackModel;
        }

        return new Identifier("stack_the_cards", "stc_cards/backs/fallback");
    }

    public CardData getCardBackData() {
        if(cardSet!=null) {
            var setBackData = cardSet.getCardBackTextureName();
            if(setBackData != null) return setBackData;
        }
        return getCardGame().getCardBackData();
    }


    public String getCardId() {
        return cardId;
    }

    public void setGame(CardGame cardGame) {
        this.gameId = cardGame.getGameId();
    }

    public void setSet(CardSet cardSet) {
        this.cardSet = cardSet;
    }

    public String getCardTextureLocation() {
        return gameId + "/" + cardSet.getSetId() + "/" + cardId;
    }
}
