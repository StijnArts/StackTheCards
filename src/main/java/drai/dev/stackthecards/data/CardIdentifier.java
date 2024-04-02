package drai.dev.stackthecards.data;

import net.minecraft.nbt.*;
import net.minecraft.server.command.*;

import java.util.*;

public class CardIdentifier {
    public static final String CARD_ID_KEY = "card_id";
    public static final String SET_ID_KEY = "set_id";
    public static final String GAME_ID_KEY = "game_id";
    public String cardId;
    public String gameId;
    public String setId;

    public CardIdentifier(String gameId, String setId, String cardId) {
        this.cardId = cardId;
        this.gameId = gameId;
        this.setId = setId;
    }

    public CardIdentifier() {
        this.cardId = "missing";
        this.gameId = "missing";
        this.setId = "missing";
    }

    public static NbtElement createNbt(CardIdentifier cardIdentifier) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString(CARD_ID_KEY, String.valueOf(cardIdentifier.cardId));
        nbtCompound.putString(SET_ID_KEY, String.valueOf(cardIdentifier.setId));
        nbtCompound.putString(GAME_ID_KEY, String.valueOf(cardIdentifier.gameId));
        return nbtCompound;
    }

    public static CardIdentifier getCardIdentifier(NbtList nbtList) {
        var gameId = "";
        var setId = "";
        var cardId = "";
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            if(nbtCompound.contains(GAME_ID_KEY)) gameId = nbtCompound.getString(GAME_ID_KEY);
            if(nbtCompound.contains(SET_ID_KEY)) setId = nbtCompound.getString(SET_ID_KEY);
            if(nbtCompound.contains(CARD_ID_KEY)) cardId = nbtCompound.getString(CARD_ID_KEY);
        }
        CardIdentifier identifier = tryParse(gameId, setId, cardId);
        if(identifier != null){
            return identifier;
        }
        return new CardIdentifier();
    }

    public static List<CardIdentifier> getCardIdentifiers(NbtList list){
        var cardIdentifiers = new ArrayList<CardIdentifier>();
        for (int i = 0; i < list.size(); i++) {
            NbtCompound nbtCompound = list.getCompound(i);
            CardIdentifier identifier = tryParse(nbtCompound.getString(GAME_ID_KEY), nbtCompound.getString(SET_ID_KEY), nbtCompound.getString(CARD_ID_KEY));
            if(identifier != null){
                cardIdentifiers.add(identifier);
            }
        }
        return cardIdentifiers;
    }

    public static CardIdentifier getCardIdentifier(NbtCompound nbtCompound) {
        var identifier = tryParse(nbtCompound.getString(GAME_ID_KEY), nbtCompound.getString(SET_ID_KEY), nbtCompound.getString(CARD_ID_KEY));
        if(identifier == null) return new CardIdentifier();
        return identifier;
    }

    private static CardIdentifier tryParse(String gameId, String setId, String cardId) {
        if(gameId.isEmpty() || gameId.isBlank() || setId.isEmpty() || setId.isBlank() || cardId.isEmpty() || cardId.isBlank()) return null;
        return new CardIdentifier(gameId, setId, cardId);
    }

    public static boolean isValid(CardIdentifier cardIdentifier) {
        return cardIdentifier.cardId.equalsIgnoreCase("missing") ||
                cardIdentifier.setId.equalsIgnoreCase("missing") || //TODO check if set could be missing if a card is just in a game
                cardIdentifier.gameId.equalsIgnoreCase("missing");
    }

    public boolean isEqual(CardIdentifier other){
        return areEqual(this, other);
    }

    public static boolean areEqual(CardIdentifier identifier1, CardIdentifier identifier2){
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
