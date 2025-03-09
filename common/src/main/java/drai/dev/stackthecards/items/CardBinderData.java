package drai.dev.stackthecards.items;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.components.*;
import io.netty.buffer.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;

import java.util.*;

public class CardBinderData {

//    public static class

    public int amountOfSlots = 120;
    public CardIdentifier restrictedTo = new CardIdentifier();
    public NonNullList<ItemStack> inventory = NonNullList.withSize(amountOfSlots, ItemStack.EMPTY);
    public boolean appliesEffect = false;
    public String effect = "";
    public int cardBinderCount = 0;

    public CardBinderData(){}
    public CardBinderData(int amountOfSlots){
        this.amountOfSlots = amountOfSlots;
        inventory = NonNullList.withSize(amountOfSlots, ItemStack.EMPTY);
    }

    public CardBinderData(int amountOfSlots, CardIdentifier restrictedTo, List<ItemStack> inventory, boolean appliesEffect, String effect, int cardBinderCount) {
        this.amountOfSlots = amountOfSlots;
        this.restrictedTo = restrictedTo;
        this.inventory = NonNullList.of(ItemStack.EMPTY, inventory.toArray(new ItemStack[0]));
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
        return inventory;
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
}
