package drai.dev.stackthecards.items;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import static drai.dev.stackthecards.items.Card.getCardDataNBT;
import static drai.dev.stackthecards.items.CardBinderInventory.BINDER_COLOR_KEY;
import static net.minecraft.util.DyeColor.WHITE;

public class CardBinder extends Item {

    public static final int MAX_CARDS_PER_PAGE = 8;
    private final DyeColor color;

    public CardBinder(DyeColor color, Settings settings) {
        super(settings);
        this.color = color;
    }

    public static ItemStack getItemStack(DyeColor color) {
        return new ItemStack(CardBinder.get(color));
    }

    private static ItemConvertible get(DyeColor color) {
        if (color == null) {
            return Items.CARD_BINDER;
        }
        switch (color) {
            case WHITE: {
                return Items.WHITE_CARD_BINDER;
            }
            case ORANGE: {
                return Items.ORANGE_CARD_BINDER;
            }
            case MAGENTA: {
                return Items.MAGENTA_CARD_BINDER;
            }
            case LIGHT_BLUE: {
                return Items.LIGHT_BLUE_CARD_BINDER;
            }
            case YELLOW: {
                return Items.YELLOW_CARD_BINDER;
            }
            case LIME: {
                return Items.LIME_CARD_BINDER;
            }
            case PINK: {
                return Items.PINK_CARD_BINDER;
            }
            case GRAY: {
                return Items.GRAY_CARD_BINDER;
            }
            case LIGHT_GRAY: {
                return Items.LIGHT_GRAY_CARD_BINDER;
            }
            case CYAN: {
                return Items.CYAN_CARD_BINDER;
            }
            default: {
                return Items.CARD_BINDER;
            }
            case BLUE: {
                return Items.BLUE_CARD_BINDER;
            }
            case PURPLE: {
                return Items.PURPLE_CARD_BINDER;
            }
            case GREEN: {
                return Items.GREEN_CARD_BINDER;
            }
            case RED: {
                return Items.RED_CARD_BINDER;
            }
            case BLACK:
        }
        return Items.BLACK_CARD_BINDER;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!world.isClient){
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (id, inventory, playerEntity) -> StackTheCards.CARD_BINDER_SCREEN_HANDLER.create(id, inventory), Text.of("Card Binder")));
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public Identifier getModelIdentifier() {
        return new Identifier("stack_the_cards", "item/card_binder_"+color.asString());
    }

    public DyeColor getColor() {
        return color;
    }
}
