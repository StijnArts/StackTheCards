package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import org.json.simple.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static drai.dev.stackthecards.data.CardConnectionEntry.*;
import static drai.dev.stackthecards.data.carddata.CardData.*;
import static drai.dev.stackthecards.data.components.StackTheCardsComponentTypes.*;

public class CardPack {
    protected static final String JSON_PACK_ID_KEY = "packId";
    protected static final String JSON_PACK_POOLS_KEY = "pools";
    protected static final String JSON_GUARANTEED_CARDS_KEY = "guaranteedCards";
    protected static final String JSON_GUARANTEED_ITEMS_KEY = "guaranteedItems";
    protected static final String STORED_CARD_PACK_DATA_KEY = "CardPackData";
    public static final String JSON_WEIGHT_IN_LOOT_POOL_KEY = "weightInLootPool";
    public static final String JSON_DROPPED_BY_MOBS_KEY = "droppedByMobs";
    public static final String JSON_PARENT_KEY = "parent";
    public final String nameSpace;
    protected String packId;
    protected String gameId;
    protected String setId;
    protected CardTooltipLine detailHeader;
    protected ArrayList<CardTooltipSection> hoverTooltipSections = new ArrayList<>();
    protected ArrayList<CardTooltipSection> detailTooltipSections = new ArrayList<>();
    protected ArrayList<CardPackPool> pools = new ArrayList<>();
    protected HashMap<ResourceLocation, Integer> guaranteedItems = new HashMap<>();
    protected HashMap<CardIdentifier, Integer> guaranteedCards = new HashMap<>();
    protected String packName;
    protected double weight = 1;
    protected boolean droppedByMobs = true;
    public boolean duplicationAllowed = true;

    public static final StreamCodec<FriendlyByteBuf, CardPack> SYNC_CODEC = new StreamCodec<FriendlyByteBuf, CardPack>() {
        @Override
        public void encode(FriendlyByteBuf buffer, CardPack value) {
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.nameSpace);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.packId);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.gameId);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.setId);

            ByteBufCodecs.optional(CardTooltipLine.SYNC_CODEC).encode(buffer, Optional.ofNullable(value.detailHeader));

            ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC).encode(buffer, value.hoverTooltipSections);
            ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC).encode(buffer, value.detailTooltipSections);
            ByteBufCodecs.collection(ArrayList::new, CardPackPool.SYNC_CODEC).encode(buffer, value.pools);

            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT).encode(buffer, value.guaranteedItems);
            ByteBufCodecs.map(HashMap::new, CardIdentifier.STREAM_CODEC, ByteBufCodecs.INT).encode(buffer, value.guaranteedCards);

            ByteBufCodecs.STRING_UTF8.encode(buffer, value.packName);
            ByteBufCodecs.DOUBLE.encode(buffer, value.weight);
            ByteBufCodecs.BOOL.encode(buffer, value.droppedByMobs);
            ByteBufCodecs.BOOL.encode(buffer, value.duplicationAllowed);
        }

        @Override
        public CardPack decode(FriendlyByteBuf buffer) {
            String nameSpace = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String packId = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String gameId = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String setId = ByteBufCodecs.STRING_UTF8.decode(buffer);

            CardTooltipLine detailHeader = ByteBufCodecs.optional(CardTooltipLine.SYNC_CODEC).decode(buffer).orElse(null);

            ArrayList<CardTooltipSection> hoverTooltipSections = ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC).decode(buffer);
            ArrayList<CardTooltipSection> detailTooltipSections = ByteBufCodecs.collection(ArrayList::new, CardTooltipSection.SYNC_CODEC).decode(buffer);
            ArrayList<CardPackPool> pools = ByteBufCodecs.collection(ArrayList::new, CardPackPool.SYNC_CODEC).decode(buffer);

            HashMap<ResourceLocation, Integer> guaranteedItems = ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT).decode(buffer);
            HashMap<CardIdentifier, Integer> guaranteedCards = ByteBufCodecs.map(HashMap::new, CardIdentifier.STREAM_CODEC, ByteBufCodecs.INT).decode(buffer);

            String packName = ByteBufCodecs.STRING_UTF8.decode(buffer);
            double weight = ByteBufCodecs.DOUBLE.decode(buffer);
            boolean droppedByMobs = ByteBufCodecs.BOOL.decode(buffer);
            boolean duplicationAllowed = ByteBufCodecs.BOOL.decode(buffer);

            return new CardPack(packId, gameId, setId, nameSpace, detailHeader, hoverTooltipSections, detailTooltipSections, pools,
                    guaranteedItems, guaranteedCards, packName, weight, droppedByMobs, duplicationAllowed);
        }
    };

    public CardPack(String packId, String gameId, String setId, String nameSpace, CardTooltipLine detailHeader, ArrayList<CardTooltipSection>
                            hoverTooltipSections, ArrayList<CardTooltipSection> detailTooltipSections, ArrayList<CardPackPool> pools,
                    HashMap<ResourceLocation, Integer> guaranteedItems, HashMap<CardIdentifier, Integer> guaranteedCards,
                    String packName, double weight, boolean droppedByMobs, boolean duplicationAllowed) {
        this.packId = packId;
        this.gameId = gameId;
        this.setId = setId;
        this.detailHeader = detailHeader;
        this.hoverTooltipSections = hoverTooltipSections;
        this.detailTooltipSections = detailTooltipSections;
        this.pools = pools;
        this.guaranteedItems = guaranteedItems;
        this.guaranteedCards = guaranteedCards;
        this.packName = packName;
        this.weight = weight;
        this.droppedByMobs = droppedByMobs;
        this.nameSpace = nameSpace;
        this.duplicationAllowed = duplicationAllowed;
    }

    public void relink(CardGame value) {
        setGame(value);
        pools.forEach(cardPackPool -> cardPackPool.setCardPack(this));
    }


    public static class CardPackData{

    }

    protected CardPack(String gameId, String packId, String nameSpace){
        this.gameId = gameId;
        this.packId = packId;
        this.nameSpace = nameSpace;
    }
    public CardPack(String gameId, String setId, String packId, String nameSpace) {
        this.packId = packId;
        this.gameId = gameId;
        this.setId = setId;
        this.packName = packId;
        this.nameSpace = nameSpace;
    }

    public CardPack(String packId, String gameId, String nameSpace, CardTooltipLine detailHeader, ArrayList<CardTooltipSection>
            hoverTooltipSections, ArrayList<CardTooltipSection> detailTooltipSections, ArrayList<CardPackPool> pools,
                    HashMap<ResourceLocation, Integer> guaranteedItems, HashMap<CardIdentifier, Integer> guaranteedCards,
                    String packName, double weight, boolean droppedByMobs, boolean duplicationAllowed) {
        this.packId = packId;
        this.gameId = gameId;
        this.detailHeader = detailHeader;
        this.hoverTooltipSections = hoverTooltipSections;
        this.detailTooltipSections = detailTooltipSections;
        this.pools = pools;
        this.guaranteedItems = guaranteedItems;
        this.guaranteedCards = guaranteedCards;
        this.packName = packName;
        this.weight = weight;
        this.droppedByMobs = droppedByMobs;
        this.nameSpace = nameSpace;
        this.duplicationAllowed = duplicationAllowed;
    }

    public static CardPack getCardPack(ItemStack stack) {
        var cardResourceLocation = stack.get(CARD_PACK_DATA_COMPONENT.get());
        return CardGameRegistry.getPackData(cardResourceLocation);
    }

    public static void addCardPackResourceLocation(ItemStack stack, CardIdentifier cardResourceLocation) {
//        ListTag nbtList = Card.getCardDataNBT(stack, STORED_CARD_PACK_DATA_KEY);
        stack.set(CARD_PACK_DATA_COMPONENT.get(), cardResourceLocation);
    }

    public static CardPack parse(JSONObject json, CardGame game, CardSet cardSet, String nameSpace) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_PACK_ID_KEY)) throw new MalformedJsonException("Card pack Json was empty");
        CardPack cardPack;

        if(json.containsKey(JSON_PARENT_KEY)) {
            try {
                cardPack = cardSet.getParentPack((String) json.get(JSON_PARENT_KEY)).copy((String) json.get(JSON_PACK_ID_KEY));
            } catch (Exception e) {
                throw new MalformedJsonException("Card pack parent was malformed: " + e.getMessage());
            }
        } else {
            try {
                cardPack = new CardPack(game.getGameId(), cardSet.getSetId(), (String) json.get(JSON_PACK_ID_KEY), nameSpace);
            } catch (Exception e) {
                throw new MalformedJsonException("Card pack id was malformed: " + e.getMessage());
            }
        }
        if(json.containsKey(JSON_NAME_HEADER_KEY)){
            try{
                cardPack.packName = (String) json.get(JSON_NAME_HEADER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card pack name value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey("canBeDuplicated")){
            try{
                cardPack.duplicationAllowed = (boolean) json.get("canBeDuplicated");
            } catch (Exception e){
                throw new MalformedJsonException("Card pack name value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CARD_HOVER_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_HOVER_TOOLTIP_KEY);
                for (var section : contents) {
                    cardPack.hoverTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CARD_DETAIL_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_DETAIL_TOOLTIP_KEY);
                for (var section : contents) {
                    cardPack.detailTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail tooltip value was malformed: "+e.getMessage());
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
                throw new MalformedJsonException("Card detail header value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_PACK_POOLS_KEY)){
            try{
                cardPack.detailHeader = new CardTooltipLine();
                JSONArray pools = (JSONArray) json.get(JSON_PACK_POOLS_KEY);
                for (var pool: pools) {
                    try{
                        cardPack.pools.add(CardPackPool.parse((JSONObject)pool, game, cardPack));
                    } catch(Exception e){
                        throw new MalformedJsonException("Card Pack pool entry was malformed: "+e.getMessage());
                    }
                }

            } catch (Exception e){
                throw new MalformedJsonException("Card pools value was malformed: "+e.getMessage());
            }
        }

        if(json.containsKey(JSON_GUARANTEED_CARDS_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_GUARANTEED_CARDS_KEY);
                for (var section : contents) {
                    var sectionAsObject = (JSONObject)section;
                    cardPack.guaranteedCards.put(new CardIdentifier((String) sectionAsObject.get(JSON_SELF_GAME_ID_KEY),
                                    (String) sectionAsObject.get(JSON_SELF_SET_ID_KEY),(String) sectionAsObject.get(JSON_SELF_CARD_ID_KEY),
                                    (sectionAsObject.containsKey("rarity") ? (String) sectionAsObject.get("rarity") : "")),
                            (int) (long)sectionAsObject.get("amount"));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pack guarnteed cards value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GUARANTEED_ITEMS_KEY)){
            try{
                var identifierArray = ((JSONArray) json.get(JSON_GUARANTEED_ITEMS_KEY));
                for (var identifier : identifierArray) {
                    var identifierAsObject = (JSONObject)identifier;
                    var identifierSplit = ((String)identifierAsObject.get("itemId")).split(":");
                    cardPack.guaranteedItems.put(ResourceLocation.fromNamespaceAndPath(identifierSplit[0], identifierSplit[1]),
                            (int) (long)identifierAsObject.get("amount"));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_WEIGHT_IN_LOOT_POOL_KEY)){
            try{
                cardPack.weight = (int) (long) json.get(JSON_WEIGHT_IN_LOOT_POOL_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card pack weight in loot pool was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_DROPPED_BY_MOBS_KEY)){
            try{
                cardPack.droppedByMobs = (boolean) json.get(JSON_DROPPED_BY_MOBS_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        return cardPack;
    }

    public CardPack copy(String packId) {
        return new CardPack(packId, this.gameId, this.setId, this.nameSpace, this.detailHeader, this.hoverTooltipSections, this.detailTooltipSections,
                this.pools, this.guaranteedItems, this.guaranteedCards, this.packName, this.weight, this.droppedByMobs, this.duplicationAllowed);
    }

    public static CardPack getRandomCardPack(boolean forMobDrops) {
        List<CardPack> gameCardPacks = CardGameRegistry.getCardGames().values().stream().map(game -> game.getCardPacks().values()).flatMap(Collection::stream).toList();
        List<CardPack> setCardPacks = CardGameRegistry.getCardGames().values().stream()
                .map(game -> game.cardSets.values()).flatMap(Collection::stream).map(cardSet -> cardSet.getCardPacks().values()).flatMap(Collection::stream).toList();
        List<CardPack> packs = new ArrayList<>();
        packs.addAll(gameCardPacks);
        packs.addAll(setCardPacks);
        var weightedRandom = new RandomCollection<CardPack>();
        for (CardPack pack : packs) {
            if(pack.droppedByMobs || !forMobDrops){
                weightedRandom.add(pack.weight,pack);
            }
        }
        var cardPack = weightedRandom.next();
        return cardPack;
    }

    public static void lootPoolCardPackInjection(LootContext context, Consumer<ItemStack> consumer, int i) {
        if(context.hasParam(LootContextParams.THIS_ENTITY)){
            var thisEntity = context.getParam(LootContextParams.THIS_ENTITY);
            var spawnGroup = thisEntity.getType().getCategory();
            if(spawnGroup == MobCategory.MONSTER || !spawnGroup.isFriendly()){
                for (int k = 0; k < i; k++) {
                    int roll = ThreadLocalRandom.current().nextInt(0, 4);
                    if(roll==1){
                        var itemStack = new ItemStack(StackTheCardsItems.CARD_PACK.get());
                        CardPack.addCardPackResourceLocation(itemStack, CardPack.getRandomCardPack(true).getCardIdentifier());
                        consumer.accept(itemStack);
                    }
                }
            }
        }
    }

    private CardIdentifier getCardIdentifier() {
        return new CardIdentifier(gameId, setId, packId, "");
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

    public Collection<? extends Component> getDetailToolTips() {
        return getTexts(detailTooltipSections);
    }

    public Collection<? extends Component> getTooltipsDescriptors() {
        return getTexts(hoverTooltipSections);
    }

    public ModelResourceLocation getModelResourceLocation() {
        if(packId!=null && !packId.equals("missing")) return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(nameSpace, "stc_cards/packs/"+packId), "");

        var cardGame = this.getCardGame();
        if(cardGame!=null) {
            var cardGamePackModel = cardGame.getCardPackModel();
            if(cardGamePackModel!=null) return cardGamePackModel;
        }

        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("stack_the_cards", "stc_cards/packs/fallback"), "");
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

    private CardSet getCardSet() {
        return CardGameRegistry.getCardGame(gameId).getCardSet(setId);
    }

    public String getTextureId() {
        return getCardSet().getSetResourceLocation() + "_" + packId;
    }

    public String getPackTextureLocation() {
        return gameId + "/" + getCardSet().getSetId() + "/" + packId;
    }

    public ModelResourceLocation getFallbackModel() {
        return new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath("stack_the_cards", "stc_cards/packs/fallback"), "");
    }

    public Component getPackNameLabel() {
        if(!StackTheCardsClient.cardLoreKeyPressed){
            return Component.literal(getPackName()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
        } else {
            if(detailHeader == null) return Component.literal(getPackName()).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE));
            return detailHeader.getTextComponent();
        }
    }

    private String getPackName() {
        return packName;
    }

    public PullResult pull() {
        var pullresult = new PullResult();
        for (var pool : pools) {
            var randomCollection = new RandomCollection<>();
            pool.cardsInPool.forEach((cardResourceLocation, integer) -> randomCollection.add(integer, cardResourceLocation));
            pool.raritiesInPool.forEach((rarity, integer) -> randomCollection.add(integer, rarity));
            pool.itemsInPool.forEach((items, integer) -> randomCollection.add(integer, items));
//            pool.tagsInPool.forEach((cardResourceLocation, integer) -> randomCollection.add(integer, cardResourceLocation));
            if(randomCollection.getTotal()<1) continue;
            if(pool.cardsInPool.values().isEmpty() && pool.itemsInPool.values().isEmpty() && pool.raritiesInPool.values().isEmpty()) continue;
            var amountOfPulls = ThreadLocalRandom.current().nextInt(pool.minimumAmountOfCardsFromPool, pool.maximumAmountOfCardsFromPool+1);
            for (int i = 0; i < amountOfPulls; i++) {
                pull(pullresult, randomCollection);
            }
        }
        for (var guaranteedCardEntry : guaranteedCards.entrySet()) {
            for (int i = 0; i < guaranteedCardEntry.getValue(); i++) {
                pullresult.pulledCards.add(guaranteedCardEntry.getKey());
            }
        }
        for (var guaranteedCardEntry : guaranteedItems.entrySet()) {
            for (int i = 0; i < guaranteedCardEntry.getValue(); i++) {
                pullresult.pulledItems.add(guaranteedCardEntry.getKey());
            }
        }
        return pullresult;
    }

    public CardIdentifier rollCardInRarity(CardRarity rarity) {
        var cardsInRarity = getCardGame().getCardSet(setId).getCards().values().stream()
                .filter(cardData -> cardData.cardRarityIds.contains(rarity.rarityId)).toList();
        var roll = ThreadLocalRandom.current().nextInt(0,cardsInRarity.size());
        var identifier = cardsInRarity.get(roll).getCardIdentifier();
        return new CardIdentifier(identifier.gameId, identifier.setId, identifier.cardId, rarity.rarityId);
    }

    public void pull(PullResult pullResult, RandomCollection<Object> collection) {
        var pulledObject = collection.next();
        if(pulledObject instanceof CardIdentifier cardResourceLocation){
            if(cardResourceLocation.rarityId.equalsIgnoreCase("missing"))
                cardResourceLocation.rarityId = CardGameRegistry.getCardData(cardResourceLocation).cardRarityIds.get(0);
            pullResult.pulledCards.add(cardResourceLocation);
        } else if(pulledObject instanceof CardRarity rarity){
            var card = rollCardInRarity(rarity);
            if(card == null){
                pull(pullResult, collection);
            } else {
                pullResult.pulledCards.add(card);
            }
        } else if(pulledObject instanceof ResourceLocation identifier){
            pullResult.pulledItems.add(identifier);
        }
    }

    public int getCountInGroup() {
        return getCardSet().getCards().size();
    }

    public String getEffectResourceLocation() {
        return getCardGame().getCardSet(this.getSetId()).getEffectResourceLocation();
    }

    public int getOrdering() {
        return getCardSet().getOrdering();
    }

    public CardIdentifier getResourceLocation() {
        return new CardIdentifier(gameId, setId, packId, "");
    }
}
