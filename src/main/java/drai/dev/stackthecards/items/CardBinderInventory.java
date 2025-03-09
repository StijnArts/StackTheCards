package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;

import java.util.*;

import static drai.dev.stackthecards.items.CardBinder.CARD_BINDER_COUNT_KEY;

public class CardBinderInventory implements Container {
    public static final String CARD_BINDER_SIZE_KEY = "amount_of_slots";
    public static final String CARD_BINDER_RESTRICTION_KEY = "RestrictedTo";
    public static final String CARD_BINDER_DATA_KEY = "CardBinderData";
    public static final String CARD_BINDER_INVENTORY_KEY = "CardBinderInventory";
    public static final String BINDER_COLOR_KEY = "BinderColor";
    public static final String CARD_BINDER_SHOULD_APPLY_EFFECT_KEY = "AppliesEffect";
    public static final String CARD_BINDER_EFFECT_KEY = "Effect";
    public int size;
    private NonNullList<ItemStack> inventory;

    public CardBinderInventory(Player player){
        var itemStack = player.getMainHandItem();
        size = getInventorySize(itemStack);
        inventory = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        /*ItemStack itemStack = Inventories.splitStack(inventory, slot, amount);
        var stack = this.inventory.get(slot);
        ItemStack itemStack = Objects.requireNonNullElse(stack, ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);*/
        return ContainerHelper.removeItem(inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.removeItem(slot, 1);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if(slot < inventory.size()){
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public void startOpen(Player player) {
        var itemStack = player.getMainHandItem();

        size = getInventorySize(itemStack);
        var compound2 = itemStack.getOrCreateTag().getCompound(CARD_BINDER_INVENTORY_KEY);
        var newInventory = NonNullList.withSize(size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound2, newInventory);
        inventory = newInventory;
    }

    public static int getInventorySize(ItemStack itemStack){
        var binderSizeNbt = itemStack.getOrCreateTag();
        if(binderSizeNbt== null || binderSizeNbt.size()==0) return 120;
        int maxSlots = binderSizeNbt.getInt(CARD_BINDER_SIZE_KEY);
        if(maxSlots == 0){
            maxSlots = 120;
        } /*else {
            maxSlots = maxSlots + (maxSlots % CardBinder.MAX_CARDS_PER_PAGE == 0 ? 0: CardBinder.MAX_CARDS_PER_PAGE - maxSlots % CardBinder.MAX_CARDS_PER_PAGE);
        }*/
        return maxSlots;
    }

    public String getColorAffix(Player player){
        var itemStack = player.getMainHandItem();
        String color = ((CardBinder)itemStack.getItem()).getColor().toString();
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
    public void stopOpen(Player player) {
        var itemStack = player.getMainHandItem();
        var nbt = new CompoundTag();
        ContainerHelper.saveAllItems(nbt, inventory, true);
        itemStack.removeTagKey(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateTag().put(CARD_BINDER_INVENTORY_KEY, nbt);
        var appliesEffect = 0;
        var shouldApplyEffect = true;
        String effect = "";
        List<CardData> collectionCards;
        var identifier = getCardIdentifier(itemStack);
        if(identifier.setId.isEmpty()){
            var game = CardGameRegistry.getCardGame(identifier.gameId);
            collectionCards = game.cards.values().stream().toList();
            if(game.getEffectResourceLocation() != null) effect = game.getEffectResourceLocation();
            shouldApplyEffect = game.appliesEffect;
        } else {
            var set = CardGameRegistry.getCardGame(identifier.gameId).getCardSet(identifier.setId);
            collectionCards = set.getCards().values().stream().toList();
            if(set.getEffectResourceLocation() != null) effect = set.getEffectResourceLocation();
            shouldApplyEffect = set.appliesEffect;
        }
        var distinctCards = inventory.stream().filter(stack -> !stack.isEmpty()).map(Card::getCardData).distinct().toList();
        var cardsInInventory = distinctCards.stream().filter(collectionCards::contains).toList();
        if(cardsInInventory.size() == collectionCards.size() && shouldApplyEffect) appliesEffect = 1;
        itemStack.getOrCreateTag().put(CARD_BINDER_SHOULD_APPLY_EFFECT_KEY, IntTag.valueOf(appliesEffect));
        itemStack.getOrCreateTag().put(CARD_BINDER_COUNT_KEY, IntTag.valueOf(distinctCards.size()));
        itemStack.getOrCreateTag().put(CARD_BINDER_EFFECT_KEY, StringTag.valueOf(effect));
    }

    @Override
    public void setChanged() {

    }

    public CardIdentifier getCardIdentifier(ItemStack stack){
        return CardIdentifier.getCardIdentifier(stack.getOrCreateTag().getCompound(CARD_BINDER_RESTRICTION_KEY));
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    public ItemStack removeStack(int inventoryIndex, int amount, Player player) {
        var stack = this.removeItem(inventoryIndex, amount);
        var itemStack = player.getMainHandItem();
        var nbt = new CompoundTag();
        ContainerHelper.saveAllItems(nbt, inventory, true);
        itemStack.removeTagKey(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateTag().put(CARD_BINDER_INVENTORY_KEY, nbt);
        return stack;
    }

    public void setStack(int inventoryIndex, ItemStack stack, Player player) {
        this.setItem(inventoryIndex, stack);
        var itemStack = player.getMainHandItem();
        var nbt = new CompoundTag();
        ContainerHelper.saveAllItems(nbt, inventory, true);
        itemStack.removeTagKey(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateTag().put(CARD_BINDER_INVENTORY_KEY, nbt);
    }

    public boolean isBound(Player player) {
        var itemStack = player.getMainHandItem();
        var nbt = itemStack.getOrCreateTag();
        return nbt.contains(CARD_BINDER_RESTRICTION_KEY);
    }

    public CardIdentifier getBoundResourceLocation(Player player){
        var itemStack = player.getMainHandItem();
        var nbt = itemStack.getOrCreateTag();
        return CardIdentifier.getCardIdentifier(nbt.getCompound(CARD_BINDER_RESTRICTION_KEY));
    }
}
