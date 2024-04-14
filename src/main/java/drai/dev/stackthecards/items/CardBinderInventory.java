package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;

import java.util.*;

import static drai.dev.stackthecards.items.CardBinder.CARD_BINDER_COUNT_KEY;

public class CardBinderInventory implements Inventory {
    public static final String CARD_BINDER_SIZE_KEY = "amount_of_slots";
    public static final String CARD_BINDER_RESTRICTION_KEY = "RestrictedTo";
    public static final String CARD_BINDER_DATA_KEY = "CardBinderData";
    public static final String CARD_BINDER_INVENTORY_KEY = "CardBinderInventory";
    public static final String BINDER_COLOR_KEY = "BinderColor";
    public static final String CARD_BINDER_SHOULD_APPLY_EFFECT_KEY = "AppliesEffect";
    public static final String CARD_BINDER_EFFECT_KEY = "Effect";
    public int size;
    private DefaultedList<ItemStack> inventory;

    public CardBinderInventory(PlayerEntity player){
        var itemStack = player.getMainHandStack();
        size = getInventorySize(itemStack);
        inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        /*ItemStack itemStack = Inventories.splitStack(inventory, slot, amount);
        var stack = this.inventory.get(slot);
        ItemStack itemStack = Objects.requireNonNullElse(stack, ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);*/
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot < inventory.size()){
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public void onOpen(PlayerEntity player) {
        var itemStack = player.getMainHandStack();

        size = getInventorySize(itemStack);
        var compound2 = itemStack.getOrCreateNbt().getCompound(CARD_BINDER_INVENTORY_KEY);
        var newInventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        Inventories.readNbt(compound2, newInventory);
        inventory = newInventory;
    }

    public static int getInventorySize(ItemStack itemStack){
        var binderSizeNbt = itemStack.getOrCreateNbt();
        if(binderSizeNbt== null || binderSizeNbt.getSize()==0) return 120;
        int maxSlots = binderSizeNbt.getInt(CARD_BINDER_SIZE_KEY);
        if(maxSlots == 0){
            maxSlots = 120;
        } /*else {
            maxSlots = maxSlots + (maxSlots % CardBinder.MAX_CARDS_PER_PAGE == 0 ? 0: CardBinder.MAX_CARDS_PER_PAGE - maxSlots % CardBinder.MAX_CARDS_PER_PAGE);
        }*/
        return maxSlots;
    }

    public String getColorAffix(PlayerEntity player){
        var itemStack = player.getMainHandStack();
        String color = ((CardBinder)itemStack.getItem()).getColor().asString();
        if(color == null || color.isEmpty()){
            return "_brown";
        } else {
            try{
                return "_"+color;
            } catch (Exception e){
                return "";
            }
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        var itemStack = player.getMainHandStack();
        var nbt = new NbtCompound();
        Inventories.writeNbt(nbt, inventory, true);
        itemStack.removeSubNbt(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateNbt().put(CARD_BINDER_INVENTORY_KEY, nbt);
        var appliesEffect = 0;
        var shouldApplyEffect = true;
        String effect = "";
        List<CardData> collectionCards;
        var identifier = getCardIdentifier(itemStack);
        if(identifier.setId.isEmpty()){
            var game = CardGameRegistry.getCardGame(identifier.gameId);
            collectionCards = game.cards.values().stream().toList();
            if(game.getEffectIdentifier() != null) effect = game.getEffectIdentifier();
            shouldApplyEffect = game.appliesEffect;
        } else {
            var set = CardGameRegistry.getCardGame(identifier.gameId).getCardSet(identifier.setId);
            collectionCards = set.getCards().values().stream().toList();
            if(set.getEffectIdentifier() != null) effect = set.getEffectIdentifier();
            shouldApplyEffect = set.appliesEffect;
        }
        var distinctCards = inventory.stream().filter(stack -> !stack.isEmpty()).map(Card::getCardData).distinct().toList();
        var cardsInInventory = distinctCards.stream().filter(collectionCards::contains).toList();
        if(cardsInInventory.size() == collectionCards.size() && shouldApplyEffect) appliesEffect = 1;
        itemStack.getOrCreateNbt().put(CARD_BINDER_SHOULD_APPLY_EFFECT_KEY, NbtInt.of(appliesEffect));
        itemStack.getOrCreateNbt().put(CARD_BINDER_COUNT_KEY, NbtInt.of(distinctCards.size()));
        itemStack.getOrCreateNbt().put(CARD_BINDER_EFFECT_KEY, NbtString.of(effect));
    }

    @Override
    public void markDirty() {

    }

    public CardIdentifier getCardIdentifier(ItemStack stack){
        return CardIdentifier.getCardIdentifier(stack.getOrCreateNbt().getCompound(CARD_BINDER_RESTRICTION_KEY));
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    public DefaultedList<ItemStack> getInventory() {
        return inventory;
    }

    public ItemStack removeStack(int inventoryIndex, int amount, PlayerEntity player) {
        var stack = this.removeStack(inventoryIndex, amount);
        var itemStack = player.getMainHandStack();
        var nbt = new NbtCompound();
        Inventories.writeNbt(nbt, inventory, true);
        itemStack.removeSubNbt(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateNbt().put(CARD_BINDER_INVENTORY_KEY, nbt);
        return stack;
    }

    public void setStack(int inventoryIndex, ItemStack stack, PlayerEntity player) {
        this.setStack(inventoryIndex, stack);
        var itemStack = player.getMainHandStack();
        var nbt = new NbtCompound();
        Inventories.writeNbt(nbt, inventory, true);
        itemStack.removeSubNbt(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateNbt().put(CARD_BINDER_INVENTORY_KEY, nbt);
    }

    public boolean isBound(PlayerEntity player) {
        var itemStack = player.getMainHandStack();
        var nbt = itemStack.getOrCreateNbt();
        return nbt.contains(CARD_BINDER_RESTRICTION_KEY);
    }

    public CardIdentifier getBoundIdentifier(PlayerEntity player){
        var itemStack = player.getMainHandStack();
        var nbt = itemStack.getOrCreateNbt();
        return CardIdentifier.getCardIdentifier(nbt.getCompound(CARD_BINDER_RESTRICTION_KEY));
    }
}
