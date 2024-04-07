package drai.dev.stackthecards.items;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;

public class CardBinderInventory implements Inventory {
    public static final String CARD_BINDER_SIZE_KEY = "amount_of_slots";
    public static final String CARD_BINDER_RESTRICTION_KEY = "RestrictedTo";
    public static final String CARD_BINDER_DATA_KEY = "CardBinderData";
    public static final String CARD_BINDER_INVENTORY_KEY = "CardBinderInventory";
    public static final String BINDER_COLOR_KEY = "BinderColor";
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
