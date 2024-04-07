package drai.dev.stackthecards.items;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.screen.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.tag.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

import java.util.*;

public class CardBinder extends Item {

    public static final int MAX_CARDS_PER_PAGE = 8;

    public CardBinder(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient){
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (id, inventory, playerEntity) -> StackTheCards.CARD_BINDER_SCREEN_HANDLER.create(id, inventory), Text.of("Card Binder")));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
