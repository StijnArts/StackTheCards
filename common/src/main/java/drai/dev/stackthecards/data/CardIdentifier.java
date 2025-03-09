package drai.dev.stackthecards.data;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import drai.dev.stackthecards.registry.*;
import io.netty.buffer.*;
import net.minecraft.nbt.*;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static drai.dev.stackthecards.data.components.StackTheCardsComponentTypes.CARD_IDENTIFIER_COMPONENT;

public class CardIdentifier {

    public static final Codec<CardIdentifier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("gameId").forGetter(CardIdentifier::getGameId),
                    Codec.STRING.fieldOf("setId").forGetter(CardIdentifier::getSetId),
                    Codec.STRING.fieldOf("cardId").forGetter(CardIdentifier::getCardId),
                    Codec.STRING.fieldOf("rarityId").forGetter(CardIdentifier::getRarityId)
            ).apply(instance, CardIdentifier::new)
    );
    public static final StreamCodec<ByteBuf,CardIdentifier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CardIdentifier::getGameId,
            ByteBufCodecs.STRING_UTF8, CardIdentifier::getSetId,
            ByteBufCodecs.STRING_UTF8, CardIdentifier::getCardId,
            ByteBufCodecs.STRING_UTF8, CardIdentifier::getRarityId,
            CardIdentifier::new);

    public static final String CARD_ID_KEY = "card_id";
    public static final String SET_ID_KEY = "set_id";
    public static final String GAME_ID_KEY = "game_id";
    private static final String RARITY_ID_KEY = "rarity_id";
    public String rarityId  = "missing";
    public String cardId = "missing";
    public String gameId = "missing";
    public String setId = "missing";

    public CardIdentifier(String gameId, String setId, String cardId, @Nullable String rarityId) {
        this.cardId = cardId;
        this.gameId = gameId;
        this.setId = setId;
        if(rarityId != null) {
            this.rarityId = rarityId;
        }
    }

    public CardIdentifier() {
        this.cardId = "missing";
        this.gameId = "missing";
        this.setId = "missing";
    }

   /* public static List<CardIdentifier> getCardIdentifiers(ListTag list){
        var CardIdentifiers = new ArrayList<CardIdentifier>();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbtCompound = list.getCompound(i);
            CardIdentifier identifier = tryParse(nbtCompound.getString(GAME_ID_KEY), nbtCompound.getString(SET_ID_KEY), nbtCompound.getString(CARD_ID_KEY), nbtCompound.getString(RARITY_ID_KEY));
            if(identifier != null){
                CardIdentifiers.add(identifier);
            }
        }
        return CardIdentifiers;
    }*/

    @Nullable
    public static CardIdentifier getCardIdentifier(ItemStack stack) {
        //        var identifier = tryParse(nbtCompound.getString(GAME_ID_KEY), nbtCompound.getString(SET_ID_KEY), nbtCompound.getString(CARD_ID_KEY), nbtCompound.getString(RARITY_ID_KEY));
//        if(identifier == null) return new CardIdentifier();
        return stack.get(CARD_IDENTIFIER_COMPONENT.get());
    }

    public void setCardIdentifier(ItemStack stack) {
        //        var identifier = tryParse(nbtCompound.getString(GAME_ID_KEY), nbtCompound.getString(SET_ID_KEY), nbtCompound.getString(CARD_ID_KEY), nbtCompound.getString(RARITY_ID_KEY));
//        if(identifier == null) return new CardIdentifier();
        stack.set(CARD_IDENTIFIER_COMPONENT.get(), this);
    }

    /*@Nullable
    private static CardIdentifier tryParse(String gameId, String setId, String cardId, String rarityId) {
        if(gameId.isEmpty() || gameId.isBlank() || cardId.isEmpty() || cardId.isBlank()) return null;
        return new CardIdentifier(gameId, setId, cardId, rarityId);
    }

    public static boolean isValid(CardIdentifier CardIdentifier) {
        return CardIdentifier.cardId.equalsIgnoreCase("missing") ||
                CardIdentifier.gameId.equalsIgnoreCase("missing");
    }*/

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

    public String getRarityId() {
        return rarityId;
    }

    public void setRarityId(String rarityId) {
        this.rarityId = rarityId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
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

    public void fixMissingRarity() {
        if(this.rarityId.equalsIgnoreCase("missing") || this.rarityId.isEmpty())
            this.rarityId = CardGameRegistry.getCardData(this).cardRarityIds.get(0);
    }
}
