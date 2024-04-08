package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.*;

import java.util.*;

public class Card extends Item {
    public static final String STORED_CARD_DATA_KEY = "CardData";
    public static final String STORED_CARD_CONNECTION_KEY = "CardConnection";
    public static final String STORED_ATTACHED_CARDS_KEY = "AttachedCards";
    public static final String STORED_CARD_FLIPPED_STATE = "CardFlipped";
    public static final String FLIPPED_KEY = "flipped";
    public CardIdentifier cardIdentifier;
//    EnchantedBookItem

    public Card(Settings settings) {
        super(settings);
    }

    public static CardData getCardData(ItemStack stack) {
        var cardNBTData = getCardDataNBT(stack, STORED_CARD_DATA_KEY);
        var cardIdentifier = CardIdentifier.getCardIdentifier(cardNBTData);
        return CardGameRegistry.getCardData(cardIdentifier);
    }

    public static CardIdentifier getCardIdentifier(ItemStack stack) {
        return CardIdentifier.getCardIdentifier(getCardDataNBT(stack, STORED_CARD_DATA_KEY));
    }

    public static void addCardIdentifier(ItemStack stack, CardIdentifier cardIdentifier) {
        NbtList nbtList = Card.getCardDataNBT(stack, STORED_CARD_DATA_KEY);
        boolean cardHasId = false;
        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            CardIdentifier cardIdentifier2 = CardIdentifier.getCardIdentifier(nbtCompound);
            if (CardIdentifier.isValid(cardIdentifier2)) continue;
            cardHasId = true;
            break;
        }
        if(cardIdentifier.rarityId == null || cardIdentifier.rarityId.isEmpty()) cardIdentifier.rarityId = CardGameRegistry.getCardData(cardIdentifier).rarity;
        if (!cardHasId) {
            nbtList.add(CardIdentifier.createNbt(cardIdentifier));
        }
        stack.getOrCreateNbt().put(STORED_CARD_DATA_KEY, nbtList);
    }

    public static void toggleCardFlipped(ItemStack stack){
        NbtList nbtList = Card.getCardDataNBT(stack, STORED_CARD_FLIPPED_STATE);
        boolean isFlipped = nbtList.getCompound(0).getBoolean(FLIPPED_KEY);
        nbtList.clear();
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putBoolean(FLIPPED_KEY, !isFlipped);
        nbtList.add(nbtCompound);
        stack.getOrCreateNbt().put(STORED_CARD_FLIPPED_STATE, nbtList);
        //TODO flip connectedCards
    }

    public static void attachCard(ItemStack topCard, CardIdentifier newCard){
        NbtList nbtList = Card.getCardDataNBT(topCard, STORED_ATTACHED_CARDS_KEY);
        nbtList.add(CardIdentifier.createNbt(newCard));
        topCard.getOrCreateNbt().put(STORED_ATTACHED_CARDS_KEY, nbtList);
//        System.out.println("added card to stack");
    }

    public static NbtList getCardDataNBT(ItemStack stack, String key) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null) {
            return nbtCompound.getList(key, NbtElement.COMPOUND_TYPE);
        }
        return new NbtList();
    }

    public static List<CardIdentifier> getAttachedCards(ItemStack stack) {
        return CardIdentifier.getCardIdentifiers(getCardDataNBT(stack, STORED_ATTACHED_CARDS_KEY));
    }

    public static ItemStack getAsItemStack(CardIdentifier cardIdentifier) {
        var itemStack = new ItemStack(Items.CARD);
        addCardIdentifier(itemStack, cardIdentifier);
        return itemStack;
    }

    public static CardIdentifier popCardFromStack(ItemStack holder) {
        NbtList nbtList = Card.getCardDataNBT(holder, STORED_ATTACHED_CARDS_KEY);
        var poppedCard = nbtList.getCompound(nbtList.size()-1);
        nbtList.remove(nbtList.size()-1);
        holder.getOrCreateNbt().put(STORED_ATTACHED_CARDS_KEY, nbtList);
//        System.out.println("removed bottom card from stack: current size ="+nbtList.size());
        return CardIdentifier.getCardIdentifier(poppedCard);
    }

    public static CardIdentifier getTopCardFromStack(ItemStack holder) {
        NbtList nbtList = Card.getCardDataNBT(holder, STORED_ATTACHED_CARDS_KEY);
        var poppedCard = nbtList.getCompound(0);
        nbtList.remove(0);
        holder.getOrCreateNbt().put(STORED_ATTACHED_CARDS_KEY, nbtList);
//        System.out.println("removed top card from stack");
        return CardIdentifier.getCardIdentifier(poppedCard);
    }

    public static void attachCards(ItemStack card, List<CardIdentifier> attachedCards) {
        for (int i = 0; i < attachedCards.size(); i++) {
            attachCard(card, attachedCards.get(i));
        }
    }

    public static void removeAttachedCards(ItemStack stack) {
        NbtList nbtList = Card.getCardDataNBT(stack, STORED_ATTACHED_CARDS_KEY);
        nbtList.clear();
        stack.removeSubNbt(STORED_ATTACHED_CARDS_KEY);
        stack.removeSubNbt(STORED_CARD_FLIPPED_STATE);
    }

    public static boolean getIsFlipped(ItemStack stack) {
        NbtList nbtList = Card.getCardDataNBT(stack, STORED_CARD_FLIPPED_STATE);
        return nbtList.getCompound(0).getBoolean(FLIPPED_KEY);
    }


}
