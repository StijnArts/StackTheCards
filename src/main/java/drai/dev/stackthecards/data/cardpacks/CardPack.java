package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.loot.context.*;
import net.minecraft.nbt.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.json.simple.*;

import java.util.*;
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
    private static final String JSON_WEIGHT_IN_LOOT_POOL_KEY = "weightInLootPool";
    private static final String JSON_DROPPED_BY_MOBS_KEY = "droppedByMobs";
    protected String packId;
    protected String gameId;
    protected String setId;
    protected CardTooltipLine detailHeader;
    protected final List<CardTooltipSection> hoverTooltipSections = new ArrayList<>();
    protected final List<CardTooltipSection> detailTooltipSections = new ArrayList<>();
    private final List<CardPackPool> pools = new ArrayList<>();
    private final Map<Identifier, Integer> guaranteedItems = new HashMap<>();
    private final Map<CardIdentifier, Integer> guaranteedCards = new HashMap<>();
    protected String packName;
    private double weight = 1;
    private boolean droppedByMobs = true;

    protected CardPack(String gameId, String packId){
        this.gameId = gameId;
        this.packId = packId;
    }
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

    public static void addCardPackIdentifier(ItemStack stack, CardIdentifier cardIdentifier) {
        NbtList nbtList = Card.getCardDataNBT(stack, STORED_CARD_PACK_DATA_KEY);
        boolean cardHasId = false;
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            CardIdentifier cardIdentifier2 = CardIdentifier.getCardIdentifier(nbtCompound);
            if (CardIdentifier.isValid(cardIdentifier2)) continue;
            cardHasId = true;
            break;
        }
        if (!cardHasId) {
            nbtList.add(CardIdentifier.createNbt(cardIdentifier));
        }
        stack.getOrCreateNbt().put(STORED_CARD_PACK_DATA_KEY, nbtList);
    }

    public static CardPack parse(JSONObject json, CardGame game, CardSet cardSet) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_PACK_ID_KEY)) throw new MalformedJsonException("Card pack Json was empty");
        CardPack cardPack;
        try{
            cardPack = new CardPack(game.getGameId(), cardSet.getSetId(), (String) json.get(JSON_PACK_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card pack id was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_NAME_HEADER_KEY)){
            try{
                cardPack.packName = (String) json.get(JSON_NAME_HEADER_KEY);
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
                        cardPack.pools.add(CardPackPool.parse((JSONObject)pool, game));
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
                    cardPack.guaranteedItems.put(new Identifier(identifierSplit[0], identifierSplit[1]),
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
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
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
        if(context.hasParameter(LootContextParameters.THIS_ENTITY)){
            var thisEntity = context.get(LootContextParameters.THIS_ENTITY);
            var spawnGroup = thisEntity.getType().getSpawnGroup();
            if(spawnGroup == SpawnGroup.CREATURE || !spawnGroup.isPeaceful()){
                for (int k = 0; k < i; k++) {
                    int roll = ThreadLocalRandom.current().nextInt(0, 4);
                    if(roll==1){
                        var itemStack = new ItemStack(Items.CARD_PACK);
                        CardPack.addCardPackIdentifier(itemStack, CardPack.getRandomCardPack(true).getCardIdentifier());
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
        return getCardSet().getSetIdentifier() + "_" + packId;
    }

    public String getPackTextureLocation() {
        return gameId + "/" + getCardSet().getSetId() + "/" + packId;
    }

    public Identifier getFallbackModel() {
        return new Identifier("stack_the_cards", "stc_cards/packs/fallback");
    }

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

    public PullResult pull() {
        var pullresult = new PullResult();
        for (var pool : pools) {
            var randomCollection = new RandomCollection<Object>();
            pool.cardsInPool.forEach((cardIdentifier, integer) -> randomCollection.add(integer, cardIdentifier));
            pool.raritiesInPool.forEach((rarity, integer) -> randomCollection.add(integer, rarity));
            pool.itemsInPool.forEach((items, integer) -> randomCollection.add(integer, items));
//            pool.tagsInPool.forEach((cardIdentifier, integer) -> randomCollection.add(integer, cardIdentifier));
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

    private CardIdentifier getCardsFromRarity(CardRarity rarity) {
        var cardsInRarity = getCardGame().getCardSet(setId).getCards().values().stream()
                .filter(cardData -> cardData.cardRarityIds.contains(rarity.rarityId)).toList();
        if(cardsInRarity.size()<1) return null;

        var roll = ThreadLocalRandom.current().nextInt(0,cardsInRarity.size());
        var identifier = cardsInRarity.get(roll).getCardIdentifier();
        return new CardIdentifier(identifier.gameId, identifier.setId, identifier.cardId, rarity.rarityId);
    }

    public void pull(PullResult pullResult, RandomCollection collection) {
        var pulledObject = collection.next();
        if(pulledObject instanceof CardIdentifier cardIdentifier){
            if(cardIdentifier.rarityId == null || cardIdentifier.rarityId.isEmpty())
                cardIdentifier.rarityId = CardGameRegistry.getCardData(cardIdentifier).cardRarityIds.get(0);
            pullResult.pulledCards.add(cardIdentifier);
        } else if(pulledObject instanceof CardRarity rarity){
            var card = getCardsFromRarity(rarity);
            if(card == null){
                pull(pullResult, collection);
            } else {
                pullResult.pulledCards.add(card);
            }
        } else if(pulledObject instanceof Identifier identifier){
            pullResult.pulledItems.add(identifier);
        }
    }

    public int getCountInGroup() {
        return getCardSet().getCards().size();
    }

    public String getEffectIdentifier() {
        return getCardGame().getCardSet(this.getSetId()).getEffectIdentifier();
    }
}
