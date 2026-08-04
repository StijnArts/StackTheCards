package drai.dev.stackthecards.data.carddata;

import com.google.gson.stream.*;
import com.mojang.datafixers.util.*;
import dev.architectury.platform.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.renderers.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import org.jetbrains.annotations.*;
import com.google.gson.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardRarity.*;

public class CardData {
    public static final String Json_CARD_ID_KEY = "cardId";
    public static final String Json_CARD_HOVER_TOOLTIP_KEY = "textSectionsForHoverTooltip";
    public static final String Json_CARD_DETAIL_TOOLTIP_KEY = "textSectionsForDetailTooltip";
    public static final String Json_ROUNDED_CORNERS_ID_KEY = "hasRoundedCorners";
    public static final String Json_REMOTE_TEXTURE_ID_KEY = "usesRemoteTexture";
    public static final String Json_CARD_TEXTURE_LOCATION_ID_KEY = "cardTextureLocation";
    public static final String Json_DETAIL_HEADER_KEY = "detailHeader";
    public static final String Json_INDEX_KEY = "index";
    public static final String Json_NAME_HEADER_KEY = "name";
    public String nameSpace;
    //    private static CardSet TEST_CARD_SET = new CardSet();
    protected transient CardSet cardSet = new CardSet("missing");
    protected String cardId;
    protected String gameId = "missing";
    private String cardTextureLocation;
    private boolean hasRoundedCorners = false;
    private boolean usesRemoteTexture = false;
    private ArrayList<CardTooltipSection> hoverTooltipSections = new ArrayList<>();
    private ArrayList<CardTooltipSection> detailTooltipSections = new ArrayList<>();
    private CardTooltipLine detailHeader;
    public String cardName = "Missing Card Data";
    public ArrayList<String> cardRarityIds = new ArrayList<>();
    public String rarity;
    public int index;

    public static final StreamCodec<FriendlyByteBuf, CardData> SYNC_CODEC = new StreamCodec<FriendlyByteBuf, CardData>() {
        @Override
        public void encode(FriendlyByteBuf buffer, CardData value) {
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.nameSpace);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.cardId);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.gameId);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.cardTextureLocation);
            ByteBufCodecs.BOOL.encode(buffer, value.hasRoundedCorners);
            ByteBufCodecs.BOOL.encode(buffer, value.usesRemoteTexture);

            if (CardTooltipSection.SYNC_CODEC == null) {
                throw new IllegalStateException("CardTooltipSection.SYNC_CODEC is null!");
            }

            ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC)
                    .encode(buffer, value.hoverTooltipSections);
            ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC)
                    .encode(buffer,
                            value.detailTooltipSections);

            ByteBufCodecs.optional(CardTooltipLine.SYNC_CODEC)
                    .encode(buffer, Optional.of(value.detailHeader)); // detailHeader is nullable

            ByteBufCodecs.STRING_UTF8.encode(buffer, value.cardName);
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8)
                    .encode(buffer, value.cardRarityIds);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.rarity);
            ByteBufCodecs.INT.encode(buffer, value.index);
        }

        @Override
        public CardData decode(FriendlyByteBuf buffer) {
            String nameSpace = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String cardId = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String gameId = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String cardTextureLocation = ByteBufCodecs.STRING_UTF8.decode(buffer);
            boolean hasRoundedCorners = ByteBufCodecs.BOOL.decode(buffer);
            boolean usesRemoteTexture = ByteBufCodecs.BOOL.decode(buffer);

            ArrayList<CardTooltipSection> hoverTooltipSections =
                    ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC).decode(buffer);
            ArrayList<CardTooltipSection> detailTooltipSections =
                    ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC).decode(buffer);

            CardTooltipLine detailHeader = ByteBufCodecs.optional(CardTooltipLine.SYNC_CODEC).decode(buffer).orElse(null);

            String cardName = ByteBufCodecs.STRING_UTF8.decode(buffer);
            ArrayList<String> cardRarityIds =
                    ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8).decode(buffer);
            String rarity = ByteBufCodecs.STRING_UTF8.decode(buffer);
            int index = ByteBufCodecs.INT.decode(buffer);

            return new CardData(nameSpace, cardId, gameId, cardTextureLocation, hasRoundedCorners, usesRemoteTexture,
                    hoverTooltipSections, detailTooltipSections, detailHeader,
                    cardName, cardRarityIds, rarity, index);
        }
    };

    public CardData(
            String nameSpace,
            String cardId,
            String gameId,
            String cardTextureLocation,
            boolean hasRoundedCorners,
            boolean usesRemoteTexture,
            ArrayList<CardTooltipSection> hoverTooltipSections,
            ArrayList<CardTooltipSection> detailTooltipSections,
            CardTooltipLine detailHeader,
            String cardName,
            ArrayList<String> cardRarityIds,
            String rarity,
            int index
    ) {
        this.nameSpace = nameSpace;
        this.cardId = cardId;
        this.gameId = gameId;
        this.cardTextureLocation = cardTextureLocation;
        this.hasRoundedCorners = hasRoundedCorners;
        this.usesRemoteTexture = usesRemoteTexture;
        this.hoverTooltipSections = hoverTooltipSections;
        this.detailTooltipSections = detailTooltipSections;
        this.detailHeader = detailHeader;
        this.cardName = cardName;
        this.cardRarityIds = cardRarityIds;
        this.rarity = rarity;
        this.index = index;
    }

    public static Component NEW_LINE = Component.literal(" ");
    public static Component TAB = Component.literal("      ");

    public CardData(String cardId, String nameSpace) {
//        this.cardSet = cardSet;
        this.cardId = cardId;
        this.nameSpace = nameSpace;
    }

    public static CardData parse(JsonObject json, CardGame game, String nameSpace) throws MalformedJsonException {
        if(json.isEmpty() || !json.has(Json_CARD_ID_KEY) || !json.has(Json_INDEX_KEY)) throw new MalformedJsonException("Card data was missing Index or cardId");
        CardData cardData;
        try{
            cardData = new CardData(json.get(Json_CARD_ID_KEY).getAsString(), nameSpace);
        } catch (Exception e){
            throw new MalformedJsonException("Card game id was malformed: "+e.getMessage());
        }
        if(json.has(Json_NAME_HEADER_KEY)){
            try{
                cardData.cardName = json.get(Json_NAME_HEADER_KEY).getAsString();
            } catch (Exception e){
                throw new MalformedJsonException("Card name header was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_CARD_TEXTURE_LOCATION_ID_KEY)){
            try{
                cardData.cardTextureLocation = json.get(Json_CARD_TEXTURE_LOCATION_ID_KEY).getAsString();
            } catch (Exception e){
                throw new MalformedJsonException("Card name header was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_RARITY_ID_KEY)){
            try{
                var retrievedJson = json.get(Json_RARITY_ID_KEY);
                if(retrievedJson.isJsonArray()){
                    for (var jsonContent : retrievedJson.getAsJsonArray()) {
                        cardData.cardRarityIds.add(jsonContent.getAsString());
                    }
                } else {
                    cardData.cardRarityIds.add(retrievedJson.getAsString());
                }
                cardData.rarity = cardData.cardRarityIds.get(0);
            } catch (Exception e){
                throw new MalformedJsonException("Card rarity id was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_ROUNDED_CORNERS_ID_KEY)){
            try{
                cardData.setHasRoundedCorners(json.get(Json_ROUNDED_CORNERS_ID_KEY).getAsBoolean());
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_REMOTE_TEXTURE_ID_KEY)){
            try{
                cardData.setUsesRemoteTexture(json.get(Json_REMOTE_TEXTURE_ID_KEY).getAsBoolean());
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_INDEX_KEY)){
            try{
                cardData.index =  json.get(Json_INDEX_KEY).getAsInt();
            } catch (Exception e){
                throw new MalformedJsonException("Card index value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_CARD_HOVER_TOOLTIP_KEY)){
            try{
                JsonArray contents =  json.get(Json_CARD_HOVER_TOOLTIP_KEY).getAsJsonArray();
                for (var section : contents) {
                    cardData.hoverTooltipSections.add(CardTooltipSection.parse( section.getAsJsonObject(), game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_CARD_DETAIL_TOOLTIP_KEY)){
            try{
                JsonArray contents = json.get(Json_CARD_DETAIL_TOOLTIP_KEY).getAsJsonArray();
                for (var section : contents) {
                    if(section.isJsonObject()) cardData.detailTooltipSections.add(CardTooltipSection.parse( section.getAsJsonObject(), game));
                }
            } catch (MalformedJsonException e){
                throw new MalformedJsonException("Card detail tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_DETAIL_HEADER_KEY)){
            try{
                cardData.detailHeader = new CardTooltipLine();
                var textContents = json.get(Json_DETAIL_HEADER_KEY);
                if(textContents.isJsonArray()){
                    for (var textSegment: textContents.getAsJsonArray()) {
                        cardData.detailHeader.lineSegments.add(CardTooltipLine.parse(textSegment.getAsJsonObject(), game));
                    }
                } else {
                    cardData.detailHeader.text = textContents.getAsString();
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail header value was malformed: "+e.getMessage());
            }
        }
//        if(json.has(Json_GAME_CARD_BACK_CARD_KEY)){
//            try{
//                cardData.setCardBackTextureName(json.get(Json_GAME_CARD_BACK_CARD_KEY));
//            } catch (Exception e){
//                throw new MalformedJsonException("Card back cardId was malformed: "+e.getMessage());
//            }
//        }
        return cardData;
    }

    private void setUsesRemoteTexture(boolean asBoolean) {
        this.usesRemoteTexture = asBoolean;
    }

    private void setHasRoundedCorners(boolean b) {
        this.hasRoundedCorners = b;
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
            tooltips.addAll(CardGameRegistry.getCardGame(self.gameId).getRarity(rarity).getTextAsComponents());
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
        if(hasRoundedCorners) return hasRoundedCorners;
        if(getCardSet().hasRoundedCorners) return true;
        return getCardGame().hasRoundedCorners;

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
            if(cardSetBackModel != null) return new ModelResourceLocation(cardSetBackModel, Platform.isFabric() ? "" : "standalone");
        }
        var cardGame = this.getCardGame();
        if(cardGame!=null) {
            var cardGameBackModel = cardGame.getCardBackModel();
            if(cardGameBackModel != null) return new ModelResourceLocation(cardGameBackModel, Platform.isFabric() ? "" : "standalone");
        }

        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("stack_the_cards", "stc_cards/backs/fallback"), Platform.isFabric() ? "" : "standalone");
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
        if(null != cardTextureLocation) return cardTextureLocation;
        return gameId + "/" + cardSet.getSetId() + "/" + cardId;
    }

    public ModelResourceLocation getFallbackModel() {
        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("stack_the_cards", "stc_cards/backs/fallback"), "");
    }

    public int getCountInGroup() {
        return getCardSet().getCards().size();
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getGameId() {
        return gameId;
    }

    public boolean isHasRoundedCorners() {
        return hasRoundedCorners;
    }

    public List<CardTooltipSection> getHoverTooltipSections() {
        return hoverTooltipSections;
    }

    public List<CardTooltipSection> getDetailTooltipSections() {
        return detailTooltipSections;
    }

    public CardTooltipLine getDetailHeader() {
        return detailHeader;
    }

    public List<String> getCardRarityIds() {
        return cardRarityIds;
    }

    public String getRarity() {
        return rarity;
    }

    public int getIndex() {
        return index;
    }

    public static Component getNewLine() {
        return NEW_LINE;
    }

    public static Component getTAB() {
        return TAB;
    }

    public void relink(CardGame value, CardSet set) {
        setSet(set);
        setGame(value);
    }

    public boolean getUsesRemoteTexture() {
        return usesRemoteTexture;
    }

    public void preload() {
        CardRenderer.getCardTexture(this, false);
    }
}
