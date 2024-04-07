package drai.dev.stackthecards.items;

import drai.dev.stackthecards.client.*;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.collection.*;

public class CardBinderInventory implements Inventory {
    public static final String SLOT_AMOUNT_KEY = "amount_of_slots";
    public static final String CARD_BINDER_DATA_KEY = "CardBinderData";
    private static final String CARD_BINDER_INVENTORY_KEY = "CardBinderInventory";
    public int size;
    private DefaultedList<ItemStack> inventory;

    public CardBinderInventory(PlayerEntity player){
        var itemStack = player.getMainHandStack();
        size = getInventorySize(itemStack);
        inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return CardBinder.MAX_CARDS_PER_PAGE;
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
        this.inventory.set(slot, stack);
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

    public int getInventorySize(ItemStack itemStack){
        int maxSlots = Card.getCardDataNBT(itemStack, CARD_BINDER_DATA_KEY).getCompound(0).getInt(SLOT_AMOUNT_KEY);
        if(maxSlots == 0){
            maxSlots = 120;
        } else {
            maxSlots = maxSlots + (maxSlots % CardBinder.MAX_CARDS_PER_PAGE == 0 ? 0: CardBinder.MAX_CARDS_PER_PAGE);
        }
        return maxSlots;
    }

    @Override
    public void onClose(PlayerEntity player) {
        var itemStack = player.getMainHandStack();
        var nbt = new NbtCompound();
        Inventories.writeNbt(nbt, inventory, true);
        itemStack.removeSubNbt(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateNbt().put(CARD_BINDER_INVENTORY_KEY, nbt);
    }

    @Override
    public void markDirty() {

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
}
