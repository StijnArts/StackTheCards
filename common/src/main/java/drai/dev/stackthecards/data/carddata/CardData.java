package drai.dev.stackthecards.data.carddata;

import com.google.gson.stream.*;
import com.mojang.datafixers.util.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.renderers.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import org.jetbrains.annotations.*;
import org.json.simple.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardRarity.*;

public class CardData {
    public static final String JSON_CARD_ID_KEY = "cardId";
    public static final String JSON_CARD_HOVER_TOOLTIP_KEY = "textSectionsForHoverTooltip";
    public static final String JSON_CARD_DETAIL_TOOLTIP_KEY = "textSectionsForDetailTooltip";
    public static final String JSON_ROUNDED_CORNERS_ID_KEY = "hasRoundedCorners";
    public static final String JSON_DETAIL_HEADER_KEY = "detailHeader";
    public static final String JSON_INDEX_KEY = "index";
    public static final String JSON_NAME_HEADER_KEY = "name";
    public String nameSpace;
    //    private static CardSet TEST_CARD_SET = new CardSet();
    protected CardSet cardSet = new CardSet("missing");
    protected String cardId;
    protected String gameId = "missing";
    private Optional<Boolean> hasRoundedCorners = Optional.empty();
    private final List<CardTooltipSection> hoverTooltipSections = new ArrayList<>();
    private final List<CardTooltipSection> detailTooltipSections = new ArrayList<>();
    private CardTooltipLine detailHeader;
    public String cardName = "Missing Card Data";
    public List<String> cardRarityIds = new ArrayList<>();
    public String rarity;
    public int index;

    public static Component NEW_LINE = Component.literal(" ");
    public static Component TAB = Component.literal("      ");

    public CardData(String cardId, String nameSpace) {
//        this.cardSet = cardSet;
        this.cardId = cardId;
        this.nameSpace = nameSpace;
    }

    public static CardData parse(JSONObject json, CardGame game, String nameSpace) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_CARD_ID_KEY) || !json.containsKey(JSON_INDEX_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        CardData cardData;
        try{
            cardData = new CardData((String) json.get(JSON_CARD_ID_KEY), nameSpace);
        } catch (Exception e){
            throw new MalformedJsonException("Card game id was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_NAME_HEADER_KEY)){
            try{
                cardData.cardName = (String) json.get(JSON_NAME_HEADER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card name header was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_RARITY_ID_KEY)){
            try{
                var retrievedJson = json.get(JSON_RARITY_ID_KEY);
                if(retrievedJson instanceof String jsonString){
                    cardData.cardRarityIds.add(jsonString);
                } else if(retrievedJson instanceof JSONArray jsonArray){
                    for (var jsonContent :
                            jsonArray) {
                        cardData.cardRarityIds.add((String) jsonContent);
                    }
                }
                cardData.rarity = cardData.cardRarityIds.get(0);
            } catch (Exception e){
                throw new MalformedJsonException("Card rarity id was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_ROUNDED_CORNERS_ID_KEY)){
            try{
                cardData.setHasRoundedCorners((boolean) json.get(JSON_ROUNDED_CORNERS_ID_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_INDEX_KEY)){
            try{
                cardData.index =  (int)(long) json.get(JSON_INDEX_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card index value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CARD_HOVER_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_HOVER_TOOLTIP_KEY);
                for (var section : contents) {
                    cardData.hoverTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CARD_DETAIL_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_DETAIL_TOOLTIP_KEY);
                for (var section : contents) {
                    cardData.detailTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail tooltip value was malformed: "+e.getMessage());
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
                throw new MalformedJsonException("Card detail header value was malformed: "+e.getMessage());
            }
        }
//        if(json.containsKey(JSON_GAME_CARD_BACK_CARD_KEY)){
//            try{
//                cardData.setCardBackTextureName((String) json.get(JSON_GAME_CARD_BACK_CARD_KEY));
//            } catch (Exception e){
//                throw new MalformedJsonException("Card back cardId was malformed: "+e.getMessage());
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
        return cardSet.getSetResourceLocation() + "_" + cardId;
    }

    public CardIdentifier getCardIdentifier() {
        return new CardIdentifier(gameId, getCardSet().getSetId(), cardId, this.rarity);
    }

    public String getCardName() {
        return cardName;
    }

    public int getMaxSide() {
        return Math.max(getHeight(), getWidth());
    }

    public int getWidth() {
        return getCardImage().getSecond();
    }

    public double getYOffset() {
        return (getMaxSide()-(double) getHeight())/2;
    }

    public double getXOffset() {
        return (getMaxSide()-(double) getWidth())/2;
    }

    public int getHeight() {
        return getCardImage().getFirst();
    }

    public List<? extends Component> getDetailToolTips() {
        return getTexts(detailTooltipSections, this);
    }

    @NotNull
    public static ArrayList<Component> getTexts(List<CardTooltipSection> sections, CardData self) {
        var tooltips = new ArrayList<Component>();
        var rarity = self.rarity;
        if(rarity == null || rarity.isEmpty() || rarity.isBlank()){
            if(self.cardRarityIds != null && self.cardRarityIds.size() > 0){
                rarity = self.cardRarityIds.get(0);
            }
        }
        if(rarity!=null){
            tooltips.addAll(CardGameRegistry.getCardGame(self.gameId).getRarity(rarity).getText());
        }
        for (int i = 0; i < sections.size(); i++) {
            var section = sections.get(i);
            tooltips.addAll(section.getText());
            if(i < sections.size()-1 && !section.noLineBreak){
                tooltips.add(NEW_LINE);
            }
        }

        return tooltips;
    }

    public static ArrayList<Component> getTexts(List<CardTooltipSection> sections) {
        var tooltips = new ArrayList<Component>();
        for (int i = 0; i < sections.size(); i++) {
            var section = sections.get(i);
            tooltips.addAll(section.getText());
            if(i < sections.size()-1 && !section.noLineBreak){
                tooltips.add(NEW_LINE);
            }
        }

        return tooltips;
    }

    public Component getCardNameLabel() {
        if(!StackTheCardsClient.cardLoreKeyPressed){
            return Component.literal(getCardName()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        } else {
            if(detailHeader == null) return Component.literal(getCardName()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
            return detailHeader.getTextComponent();
        }
    }

    public List<Component> getTooltipsDescriptors(){
        return getTexts(hoverTooltipSections, this);
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

    public ModelResourceLocation getModelResourceLocation() {
        if(cardSet!=null){
            var cardSetBackModel = this.cardSet.getCardBackModel();
            if(cardSetBackModel != null) return new ModelResourceLocation(cardSetBackModel, "");
        }
        var cardGame = this.getCardGame();
        if(cardGame!=null) {
            var cardGameBackModel = cardGame.getCardBackModel();
            if(cardGameBackModel !=null) return new ModelResourceLocation(cardGameBackModel, "");
        }

        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("stack_the_cards", "stc_cards/backs/fallback"), "");
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

    public ModelResourceLocation getFallbackModel() {
        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("stack_the_cards", "stc_cards/backs/fallback"), "");
    }

    public int getCountInGroup() {
        return getCardSet().getCards().size();
    }
}
