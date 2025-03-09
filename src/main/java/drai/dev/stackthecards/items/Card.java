package drai.dev.stackthecards.items;

import com.mojang.datafixers.types.templates.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.nbt.*;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.*;

import java.util.*;
import java.util.List;

public class Card extends Item {
    public static final String STORED_CARD_DATA_KEY = "CardData";
    public static final String STORED_CARD_CONNECTION_KEY = "CardConnection";
    public static final String STORED_ATTACHED_CARDS_KEY = "AttachedCards";
    public static final String STORED_CARD_FLIPPED_STATE = "CardFlipped";
    public static final String FLIPPED_KEY = "flipped";
    public CardIdentifier cardResourceLocation;
//    EnchantedBookItem

    public Card(Item.Properties settings) {
        super(settings);
    }

    public static CardData getCardData(ItemStack stack) {
        var cardNBTData = getCardDataNBT(stack, STORED_CARD_DATA_KEY);
        var cardResourceLocation = CardIdentifier.getCardIdentifier(cardNBTData);
        return CardGameRegistry.getCardData(cardResourceLocation);
    }

    public static CardIdentifier getCardIdentifier(ItemStack stack) {
        return CardIdentifier.getCardIdentifier(getCardDataNBT(stack, STORED_CARD_DATA_KEY));
    }

    public static void addCardResourceLocation(ItemStack stack, CardIdentifier cardResourceLocation) {
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
            nbtList.add(CardIdentifier.createNbt(cardResourceLocation));
        }
        stack.getOrCreateTag().put(STORED_CARD_DATA_KEY, nbtList);
    }

    public static void toggleCardFlipped(ItemStack stack){
        ListTag nbtList = Card.getCardDataNBT(stack, STORED_CARD_FLIPPED_STATE);
        boolean isFlipped = nbtList.getCompound(0).getBoolean(FLIPPED_KEY);
        nbtList.clear();
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putBoolean(FLIPPED_KEY, !isFlipped);
        nbtList.add(nbtCompound);
        stack.getOrCreateTag().put(STORED_CARD_FLIPPED_STATE, nbtList);
    }

    public static void attachCard(ItemStack topCard, CardIdentifier newCard){
        ListTag nbtList = Card.getCardDataNBT(topCard, STORED_ATTACHED_CARDS_KEY);
        nbtList.add(CardIdentifier.createNbt(newCard));
        topCard.getOrCreateTag().put(STORED_ATTACHED_CARDS_KEY, nbtList);
//        System.out.println("added card to stack");
    }

    public static ListTag getCardDataNBT(ItemStack stack, String key) {
        CompoundTag nbtCompound = stack.getTag();
        if (nbtCompound != null) {
            var list = nbtCompound.getList(key, Tag.TAG_COMPOUND);
            return list;
        }
        return new ListTag();
    }

    public static List<CardIdentifier> getAttachedCards(ItemStack stack) {
        var cardNBTData = getCardDataNBT(stack, STORED_ATTACHED_CARDS_KEY);
        return CardIdentifier.getCardIdentifiers(cardNBTData);
    }

    public static ItemStack getAsItemStack(CardIdentifier cardResourceLocation) {
        var itemStack = new ItemStack(Items.CARD);
        addCardResourceLocation(itemStack, cardResourceLocation);
        return itemStack;
    }

    public static CardIdentifier popCardFromStack(ItemStack holder) {
        ListTag nbtList = Card.getCardDataNBT(holder, STORED_ATTACHED_CARDS_KEY);
        var poppedCard = nbtList.getCompound(nbtList.size()-1);
        nbtList.remove(nbtList.size()-1);
        holder.getOrCreateTag().put(STORED_ATTACHED_CARDS_KEY, nbtList);
//        System.out.println("removed bottom card from stack: current size ="+nbtList.size());
        return CardIdentifier.getCardIdentifier(poppedCard);
    }

    public static CardIdentifier getTopCardFromStack(ItemStack holder) {
        ListTag nbtList = Card.getCardDataNBT(holder, STORED_ATTACHED_CARDS_KEY);
        var poppedCard = nbtList.getCompound(0);
        nbtList.remove(0);
        holder.getOrCreateTag().put(STORED_ATTACHED_CARDS_KEY, nbtList);
//        System.out.println("removed top card from stack");
        return CardIdentifier.getCardIdentifier(poppedCard);
    }

    public static void attachCards(ItemStack card, List<CardIdentifier> attachedCards) {
        for (int i = 0; i < attachedCards.size(); i++) {
            attachCard(card, attachedCards.get(i));
        }
    }

    public static void removeAttachedCards(ItemStack stack) {
        ListTag nbtList = Card.getCardDataNBT(stack, STORED_ATTACHED_CARDS_KEY);
        nbtList.clear();
        stack.removeTagKey(STORED_ATTACHED_CARDS_KEY);
        stack.removeTagKey(STORED_CARD_FLIPPED_STATE);
    }

    public static boolean getIsFlipped(ItemStack stack) {
        ListTag nbtList = Card.getCardDataNBT(stack, STORED_CARD_FLIPPED_STATE);
        return nbtList.getCompound(0).getBoolean(FLIPPED_KEY);
    }


}
