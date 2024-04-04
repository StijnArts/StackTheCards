package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class CardPackItem extends Item {
    private CardPack cardPack = new CardPack("pokemon_tcg", "base", "base");
    public CardPackItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        var cardPack = CardPack.getCardPack(itemStack);
        var pullResult = cardPack.pull();
        var cardsToDrop = pullResult.getPulledCards();
        var itemsToDrop = pullResult.getPulledItems();
        for (var card : cardsToDrop) {
            user.dropStack(Card.getAsItemStack(card), 0.5F);
        }
        for (var item : itemsToDrop) {
            user.dropStack(new ItemStack(Registries.ITEM.get(item)), 0.5F);
        }
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }
        return TypedActionResult.success(itemStack);
    }

    public void playSound(PlayerEntity user, SoundEvent sound, float volume, float pitch) {
        if (!user.isSilent()) {
            user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), sound, user.getSoundCategory(), volume, pitch);
        }
    }

}
