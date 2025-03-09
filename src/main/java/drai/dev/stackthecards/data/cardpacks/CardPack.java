package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import com.mojang.datafixers.types.templates.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.*;
import org.json.simple.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.*;

import static drai.dev.stackthecards.data.CardConnectionEntry.*;
import static drai.dev.stackthecards.data.carddata.CardData.*;
import static drai.dev.stackthecards.items.Card.getCardDataNBT;

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
    protected List<CardTooltipSection> hoverTooltipSections = new ArrayList<>();
    protected List<CardTooltipSection> detailTooltipSections = new ArrayList<>();
    protected List<CardPackPool> pools = new ArrayList<>();
    protected Map<ResourceLocation, Integer> guaranteedItems = new HashMap<>();
    protected Map<CardIdentifier, Integer> guaranteedCards = new HashMap<>();
    protected String packName;
    protected double weight = 1;
    protected boolean droppedByMobs = true;
    public boolean duplicationAllowed = true;

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

    public CardPack(String packId, String gameId, String setId, String nameSpace, CardTooltipLine detailHeader, List<CardTooltipSection>
            hoverTooltipSections, List<CardTooltipSection> detailTooltipSections, List<CardPackPool> pools,
                    Map<ResourceLocation, Integer> guaranteedItems, Map<CardIdentifier, Integer> guaranteedCards,
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

    public CardPack(String packId, String gameId, String nameSpace, CardTooltipLine detailHeader, List<CardTooltipSection>
            hoverTooltipSections, List<CardTooltipSection> detailTooltipSections, List<CardPackPool> pools,
                    Map<ResourceLocation, Integer> guaranteedItems, Map<CardIdentifier, Integer> guaranteedCards,
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
        var cardNBTData = getCardDataNBT(stack, STORED_CARD_PACK_DATA_KEY);
        var cardResourceLocation = CardIdentifier.getCardIdentifier(cardNBTData);
        return CardGameRegistry.getPackData(cardResourceLocation);
    }

    public static void addCardPackResourceLocation(ItemStack stack, CardIdentifier cardResourceLocation) {
        ListTag nbtList = Card.getCardDataNBT(stack, STORED_CARD_PACK_DATA_KEY);
        boolean cardHasId = false;
        for(int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            CardIdentifier cardResourceLocation2 = CardIdentifier.getCardIdentifier(nbtCompound);
            if (CardIdentifier.isValid(cardResourceLocation2)) continue;
            cardHasId = true;
            break;
        }
        if (!cardHasId) {
            nbtList.add(CardIdentifier.createNbt(cardResourceLocation));
        }
        stack.getOrCreateTag().put(STORED_CARD_PACK_DATA_KEY, nbtList);
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
                    cardPack.guaranteedItems.put(new ResourceLocation(identifierSplit[0], identifierSplit[1]),
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
                        var itemStack = new ItemStack(Items.CARD_PACK);
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

    public ResourceLocation getModelResourceLocation() {
        if(packId!=null && !packId.equals("missing")) return new ResourceLocation(nameSpace, "stc_cards/packs/"+packId);

        var cardGame = this.getCardGame();
        if(cardGame!=null) {
            var cardGamePackModel = cardGame.getCardPackModel();
            if(cardGamePackModel !=null) return cardGamePackModel;
        }

        return new ResourceLocation("stack_the_cards", "stc_cards/packs/fallback");
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

    public ResourceLocation getFallbackModel() {
        return new ResourceLocation("stack_the_cards", "stc_cards/packs/fallback");
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
            if(cardResourceLocation.rarityId == null || cardResourceLocation.rarityId.isEmpty())
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
