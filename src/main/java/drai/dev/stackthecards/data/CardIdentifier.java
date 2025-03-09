package drai.dev.stackthecards.data;

import net.minecraft.nbt.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CardIdentifier {
    public static final String CARD_ID_KEY = "card_id";
    public static final String SET_ID_KEY = "set_id";
    public static final String GAME_ID_KEY = "game_id";
    private static final String RARITY_ID_KEY = "rarity_id";
    public String rarityId;
    public String cardId;
    public String gameId;
    public String setId;

    public CardIdentifier(String gameId, String setId, String cardId, @Nullable String rarityId) {
        this.cardId = cardId;
        this.gameId = gameId;
        this.setId = setId;
        this.rarityId = rarityId;
    }

    public CardIdentifier() {
        this.cardId = "missing";
        this.gameId = "missing";
        this.setId = "missing";
    }

    public static Tag createNbt(CardIdentifier CardIdentifier) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putString(CARD_ID_KEY, String.valueOf(CardIdentifier.cardId));
        nbtCompound.putString(SET_ID_KEY, String.valueOf(CardIdentifier.setId));
        nbtCompound.putString(GAME_ID_KEY, String.valueOf(CardIdentifier.gameId));
        nbtCompound.putString(RARITY_ID_KEY, String.valueOf(CardIdentifier.rarityId));
        return nbtCompound;
    }

    public static CardIdentifier getCardIdentifier(ListTag nbtList) {
        var gameId = "";
        var setId = "";
        var cardId = "";
        var rarityId = "";
        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            if(nbtCompound.contains(GAME_ID_KEY)) gameId = nbtCompound.getString(GAME_ID_KEY);
            if(nbtCompound.contains(SET_ID_KEY)) setId = nbtCompound.getString(SET_ID_KEY);
            if(nbtCompound.contains(CARD_ID_KEY)) cardId = nbtCompound.getString(CARD_ID_KEY);
            if(nbtCompound.contains(RARITY_ID_KEY)) rarityId = nbtCompound.getString(RARITY_ID_KEY);
        }
        CardIdentifier identifier = tryParse(gameId, setId, cardId, rarityId);
        if(identifier != null){
            return identifier;
        }
        return new CardIdentifier();
    }

    public static List<CardIdentifier> getCardIdentifiers(ListTag list){
        var CardIdentifiers = new ArrayList<CardIdentifier>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbtCompound = list.getCompound(i);
            CardIdentifier identifier = tryParse(nbtCompound.getString(GAME_ID_KEY), nbtCompound.getString(SET_ID_KEY), nbtCompound.getString(CARD_ID_KEY), nbtCompound.getString(RARITY_ID_KEY));
            if(identifier != null){
                CardIdentifiers.add(identifier);
            }
        }
        return CardIdentifiers;
    }

    public static CardIdentifier getCardIdentifier(CompoundTag nbtCompound) {
        var identifier = tryParse(nbtCompound.getString(GAME_ID_KEY), nbtCompound.getString(SET_ID_KEY), nbtCompound.getString(CARD_ID_KEY), nbtCompound.getString(RARITY_ID_KEY));
        if(identifier == null) return new CardIdentifier();
        return identifier;
    }

    private static CardIdentifier tryParse(String gameId, String setId, String cardId, String rarityId) {
        if(gameId.isEmpty() || gameId.isBlank() || cardId.isEmpty() || cardId.isBlank()) return null;
        return new CardIdentifier(gameId, setId, cardId, rarityId);
    }

    public static boolean isValid(CardIdentifier CardIdentifier) {
        return CardIdentifier.cardId.equalsIgnoreCase("missing") ||
                CardIdentifier.gameId.equalsIgnoreCase("missing");
    }

    public boolean isEqual(CardIdentifier other){
        return isSameItem(this, other);
    }

    public static boolean isSameItem(CardIdentifier identifier1, CardIdentifier identifier2){
        if(identifier1 == null || identifier2 == null) return false;
        var sameGame = identifier1.gameId.equalsIgnoreCase(identifier2.gameId);
        var sameSet = identifier1.setId.equalsIgnoreCase(identifier2.setId);
        var sameCard = identifier1.cardId.equalsIgnoreCase(identifier2.cardId);
        return sameGame && sameSet && sameCard;
    }

    public List<String> ToList() {
        var list = new ArrayList<String>();
        list.add(gameId);
        list.add(setId);
        list.add(cardId);
        return list;
    }

    public String forPrint() {
        return gameId+":"+setId+":"+cardId;
    }
}
