package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.cardpacks.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class CardPackItem extends Item {
    public static final Identifier PACK_RIP_IDENTIFIER = new Identifier("stack_the_cards", "pack_rip");
    public static SoundEvent PACK_RIP = SoundEvent.of(PACK_RIP_IDENTIFIER);
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
        if(!world.isClient){
            world.playSound(null, user.getBlockPos(), PACK_RIP, SoundCategory.PLAYERS, 0.8f, 1.3f);
        }
        for (var card : cardsToDrop) {
            user.dropStack(Card.getAsItemStack(card), 1F);
        }
        for (var item : itemsToDrop) {
            user.dropStack(new ItemStack(Registries.ITEM.get(item)), 1F);
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
