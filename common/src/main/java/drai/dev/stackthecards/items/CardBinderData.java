package drai.dev.stackthecards.items;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.components.*;
import net.minecraft.core.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.*;

import java.util.*;

public final class CardBinderData {
    private final int amountOfSlots;
    private final CardIdentifier restrictedTo;
    private final NonNullList<ItemStack> inventory;
    private final boolean appliesEffect;
    private final String effect;
    private final int cardBinderCount;

    public CardBinderData() {
        this(120, new CardIdentifier(), NonNullList.withSize(120, ItemStack.EMPTY), false, "", 0);
    }

    public CardBinderData(int amountOfSlots){
        this(amountOfSlots, new CardIdentifier(), NonNullList.withSize(amountOfSlots, ItemStack.EMPTY), false, "", 0);
    }

    public CardBinderData(int amountOfSlots, CardIdentifier restrictedTo, List<ItemStack> inventory, boolean appliesEffect, String effect, int cardBinderCount) {
        this.amountOfSlots = amountOfSlots;
        this.restrictedTo = restrictedTo;
        ItemStack[] array = inventory.toArray(new ItemStack[0]);
        this.inventory = NonNullList.of(ItemStack.EMPTY, array);
        this.appliesEffect = appliesEffect;
        this.effect = effect;
        this.cardBinderCount = cardBinderCount;
    }

    public static final Codec<CardBinderData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("amountOfSlots").forGetter(CardBinderData::getAmountOfSlots),
                    CardIdentifier.CODEC.fieldOf("restrictedTo").forGetter(CardBinderData::getRestrictedTo),
                    Codec.list(ItemStack.OPTIONAL_CODEC).fieldOf("inventory").forGetter(CardBinderData::getInventory),
                    Codec.BOOL.fieldOf("appliesEffect").forGetter(CardBinderData::isAppliesEffect),
                    Codec.STRING.fieldOf("effect").forGetter(CardBinderData::getEffect),
                    Codec.INT.fieldOf("cardBinderCount").forGetter(CardBinderData::getCardBinderCount)
            ).apply(instance, CardBinderData::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, CardBinderData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CardBinderData::getAmountOfSlots,
            CardIdentifier.STREAM_CODEC, CardBinderData::getRestrictedTo,
            ByteBufCodecs.collection(ArrayList::new, ItemStack.OPTIONAL_STREAM_CODEC), CardBinderData::getInventory,
            ByteBufCodecs.BOOL, CardBinderData::isAppliesEffect,
            ByteBufCodecs.STRING_UTF8, CardBinderData::getEffect,
            ByteBufCodecs.INT, CardBinderData::getCardBinderCount,
            CardBinderData::new);

    public static CardBinderData getOrCreate(ItemStack stack){
        var data = stack.get(StackTheCardsComponentTypes.CARD_BINDER_DATA_COMPONENT.get());
        if (data == null) data = new CardBinderData();
        return data;
    }

    public static void saveChanges(ItemStack itemStack, CardBinderData data) {
        itemStack.set(StackTheCardsComponentTypes.CARD_BINDER_DATA_COMPONENT.get(), data);
    }

    public int getAmountOfSlots() {
        return amountOfSlots;
    }

    public CardIdentifier getRestrictedTo() {
        return restrictedTo;
    }

    public NonNullList<ItemStack> getInventory() {
        NonNullList<ItemStack> copy = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.size(); i++) {
            copy.set(i, inventory.get(i));
        }
        return copy;
    }

    public boolean isAppliesEffect() {
        return appliesEffect;
    }

    public String getEffect() {
        return effect;
    }

    public int getCardBinderCount() {
        return cardBinderCount;
    }

    public boolean isRestricted() {
        return !restrictedTo.setId.equalsIgnoreCase("missing");
    }

    public CardBinderData setRestrictedTo(CardIdentifier cardResourceLocation) {
        return new CardBinderData(amountOfSlots, cardResourceLocation, inventory, appliesEffect, effect, cardBinderCount);
    }

    public CardBinderData setAppliesEffect(boolean appliesEffect) {
        return new CardBinderData(amountOfSlots, restrictedTo, inventory, appliesEffect, effect, cardBinderCount);
    }

    public CardBinderData setCardBinderCount(int cardBinderCount) {
        return new CardBinderData(amountOfSlots, restrictedTo, inventory, appliesEffect, effect, cardBinderCount);
    }

    public CardBinderData setEffect(String effect) {
        return new CardBinderData(amountOfSlots, restrictedTo, inventory, appliesEffect, effect, cardBinderCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CardBinderData that)) return false;
        return amountOfSlots == that.amountOfSlots &&
                appliesEffect == that.appliesEffect &&
                cardBinderCount == that.cardBinderCount &&
                Objects.equals(restrictedTo, that.restrictedTo) &&
                inventory.equals(that.inventory) &&
                Objects.equals(effect, that.effect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountOfSlots, restrictedTo, inventory, appliesEffect, effect, cardBinderCount);
    }

    @Override
    public String toString() {
        return "CardBinderData{" +
                "amountOfSlots=" + amountOfSlots +
                ", restrictedTo=" + restrictedTo +
                ", inventory=" + inventory +
                ", appliesEffect=" + appliesEffect +
                ", effect='" + effect + '\'' +
                ", cardBinderCount=" + cardBinderCount +
                '}';
    }

    public CardBinderData setInventory(NonNullList<ItemStack> inventory) {
        return new CardBinderData(amountOfSlots, restrictedTo, inventory, appliesEffect, effect, cardBinderCount);
    }
}
