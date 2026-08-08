package drai.dev.stackthecards.items;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.components.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.*;

import java.util.*;

public class Card extends Item {

    public Card(Item.Properties settings) {
        super(settings);
    }

    public static CardData getCardData(ItemStack stack) {
        var cardResourceLocation = CardIdentifier.getCardIdentifier(stack);
        return CardGameRegistry.getCardData(cardResourceLocation);
    }

    public static CardIdentifier getCardIdentifier(ItemStack stack) {
        return CardIdentifier.getCardIdentifier(stack);
    }

    public static CardRecord getOrCreateCardRecord(ItemStack stack) {
        var cardRecord = stack.get(StackTheCardsComponentTypes.CARD_DATA_COMPONENT.get());
        if(cardRecord == null) cardRecord = new CardRecord();
        return cardRecord;
    }

    public static void saveChanges(ItemStack stack, CardRecord record) {
        stack.set(StackTheCardsComponentTypes.CARD_DATA_COMPONENT.get(), record);
    }

    public static void toggleCardFlipped(ItemStack stack){
        var record = getOrCreateCardRecord(stack);
        record = record.withFlipped(!record.isFlipped);
        saveChanges(stack, record);
    }

    public static void attachCard(ItemStack topCard, CardIdentifier newCard){
        var record = getOrCreateCardRecord(topCard);
        record.getAttachedCards().add(newCard);
        saveChanges(topCard, record);
//        System.out.println("added card to stack");
    }

    public static List<CardIdentifier> getAttachedCards(ItemStack stack) {
//        var cardNBTData = getCardDataNBT(stack, STORED_ATTACHED_CARDS_KEY);
        return getOrCreateCardRecord(stack).getAttachedCards();
    }

    public static ItemStack getAsItemStack(CardIdentifier cardResourceLocation) {
        var itemStack = new ItemStack(StackTheCardsItems.CARD.get());
        cardResourceLocation.setCardIdentifier(itemStack);
//        addCardResourceLocation(itemStack, cardResourceLocation);
        return itemStack;
    }

    /*public static void addCardResourceLocation(ItemStack stack, CardIdentifier cardResourceLocation) {
        ListTag nbtList = Card.getCardDataNBT(stack, STORED_CARD_DATA_KEY);
        boolean cardHasId = false;
        for(int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            CardIdentifier cardResourceLocation2 = CardIdentifier.getCardIdentifier(nbtCompound);
            if (CardIdentifier.isValid(cardResourceLocation2)) continue;
            cardHasId = true;
            break;
        }
        if(cardResourceLocation.rarityId == null || cardResourceLocation.rarityId.isEmpty()) cardResourceLocation.rarityId = CardGameRegistry.getCardData(cardResourceLocation).rarity;
        if (!cardHasId) {
            nbtList.add(cardResourceLocation);
        }
        stack.getOrCreateTag().put(STORED_CARD_DATA_KEY, nbtList);
    }*/

    public static CardIdentifier popLastCardFromStack(ItemStack holder) {
        var record = getOrCreateCardRecord(holder);
        var attachedCards = record.getAttachedCards();
        var poppedCard = attachedCards.getLast();
        attachedCards.removeLast();
        saveChanges(holder, record);
//        System.out.println("removed bottom card from stack: current size ="+nbtList.size());
        return poppedCard;
    }

    public static CardIdentifier popTopCardFromStack(ItemStack holder) {
        var record = getOrCreateCardRecord(holder);
        var attachedCards = record.getAttachedCards();
        var poppedCard = attachedCards.getFirst();
        attachedCards.removeFirst();
        saveChanges(holder, record);
//        System.out.println("removed bottom card from stack: current size ="+nbtList.size());
        return poppedCard;
    }

    public static void attachCards(ItemStack card, List<CardIdentifier> attachedCards) {
        for (int i = 0; i < attachedCards.size(); i++) {
            attachCard(card, attachedCards.get(i));
        }
    }

    public static void removeAttachedCards(ItemStack stack) {
        var record = getOrCreateCardRecord(stack);
        saveChanges(stack, record.clearAttachedCards());
    }

    public static boolean getIsFlipped(ItemStack stack) {
        return getOrCreateCardRecord(stack).isFlipped;
    }


    public static final Codec<CardRecord> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("isFlipped").forGetter(CardRecord::isFlipped),
                    Codec.list(CardIdentifier.CODEC).fieldOf("attachedCards").forGetter(CardRecord::getAttachedCards),
                    Codec.list(CardConnectionEntry.CODEC).fieldOf("connectedCards").forGetter(CardRecord::getConnectedCards)
            ).apply(instance, CardRecord::new)
    );
    public static final StreamCodec<FriendlyByteBuf, CardRecord> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, CardRecord::isFlipped,
            ByteBufCodecs.collection(ArrayList::new, CardIdentifier.STREAM_CODEC), CardRecord::getAttachedCards,
            ByteBufCodecs.collection(ArrayList::new, CardConnectionEntry.STREAM_CODEC), CardRecord::getConnectedCards,
            CardRecord::new);

    public static void resetFlipped(ItemStack stack) {
        var data = getOrCreateCardRecord(stack);
        data = data.withFlipped(false);
        saveChanges(stack, data);
    }

    public static class CardRecord {
        private final boolean isFlipped;
        private final List<CardIdentifier> attachedCards;
        private final List<CardConnectionEntry.CardConnectionEntryData> connectedCards;
        public CardRecord(){
            isFlipped = false;
            attachedCards = new ArrayList<>();
            connectedCards = new ArrayList<>();
        }

        public CardRecord(boolean isFlipped, List<CardIdentifier> attachedCards, List<CardConnectionEntry.CardConnectionEntryData> connectedCards) {
            this.isFlipped = isFlipped;
            this.attachedCards = new ArrayList<>(List.copyOf(attachedCards));
            this.connectedCards = new ArrayList<>(List.copyOf(connectedCards));
        }

        public boolean isFlipped() {
            return isFlipped;
        }

        // "With"-style methods to create updated copies
        public CardRecord withFlipped(boolean flipped) {
            return new CardRecord(flipped, this.attachedCards, this.connectedCards);
        }

        public List<CardIdentifier> getAttachedCards() {
            return attachedCards;
        }

        public CardRecord clearAttachedCards() {
            return new CardRecord(this.isFlipped, List.of(), this.connectedCards);
        }

        public List<CardConnectionEntry.CardConnectionEntryData> getConnectedCards() {
            return connectedCards;
        }

        public CardRecord clearConnectedCards() {
            return new CardRecord(this.isFlipped, this.attachedCards, List.of());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CardRecord that)) return false;
            return isFlipped == that.isFlipped &&
                    Objects.equals(attachedCards, that.attachedCards) &&
                    Objects.equals(connectedCards, that.connectedCards);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isFlipped, attachedCards, connectedCards);
        }
    }

}
