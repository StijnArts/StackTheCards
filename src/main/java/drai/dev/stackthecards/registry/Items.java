package drai.dev.stackthecards.registry;

import drai.dev.stackthecards.items.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;

public class Items {
    public static final Card CARD = registerItem(new Card(new FabricItemSettings()),"card");
    public static final CardPackItem CARD_PACK = registerItem(new CardPackItem(new FabricItemSettings()), "card_pack");
    public static final CardBinder CARD_BINDER = registerItem(new CardBinder(DyeColor.BROWN, new FabricItemSettings().maxCount(1)), "card_binder_brown");
    public static final CardBinder WHITE_CARD_BINDER = registerItem(new CardBinder(DyeColor.WHITE, new FabricItemSettings().maxCount(1)), "card_binder_white");
    public static final CardBinder ORANGE_CARD_BINDER = registerItem(new CardBinder(DyeColor.ORANGE, new FabricItemSettings().maxCount(1)), "card_binder_orange");
    public static final CardBinder MAGENTA_CARD_BINDER = registerItem(new CardBinder(DyeColor.MAGENTA, new FabricItemSettings().maxCount(1)), "card_binder_magenta");
    public static final CardBinder LIGHT_BLUE_CARD_BINDER = registerItem(new CardBinder(DyeColor.LIGHT_BLUE, new FabricItemSettings().maxCount(1)), "card_binder_light_blue");
    public static final CardBinder YELLOW_CARD_BINDER = registerItem(new CardBinder(DyeColor.YELLOW, new FabricItemSettings().maxCount(1)), "card_binder_yellow");
    public static final CardBinder LIME_CARD_BINDER = registerItem(new CardBinder(DyeColor.LIME, new FabricItemSettings().maxCount(1)), "card_binder_lime");
    public static final CardBinder PINK_CARD_BINDER = registerItem(new CardBinder(DyeColor.PINK, new FabricItemSettings().maxCount(1)), "card_binder_pink");
    public static final CardBinder GRAY_CARD_BINDER = registerItem(new CardBinder(DyeColor.GRAY, new FabricItemSettings().maxCount(1)), "card_binder_gray");
    public static final CardBinder LIGHT_GRAY_CARD_BINDER = registerItem(new CardBinder(DyeColor.LIGHT_GRAY, new FabricItemSettings().maxCount(1)), "card_binder_light_gray");
    public static final CardBinder CYAN_CARD_BINDER = registerItem(new CardBinder(DyeColor.CYAN, new FabricItemSettings().maxCount(1)), "card_binder_cyan");
    public static final CardBinder BLUE_CARD_BINDER = registerItem(new CardBinder(DyeColor.BLUE, new FabricItemSettings().maxCount(1)), "card_binder_blue");
    public static final CardBinder GREEN_CARD_BINDER = registerItem(new CardBinder(DyeColor.GREEN, new FabricItemSettings().maxCount(1)), "card_binder_green");
    public static final CardBinder RED_CARD_BINDER = registerItem(new CardBinder(DyeColor.RED, new FabricItemSettings().maxCount(1)), "card_binder_red");
    public static final CardBinder BLACK_CARD_BINDER = registerItem(new CardBinder(DyeColor.BLACK, new FabricItemSettings().maxCount(1)), "card_binder_black");
    public static final CardBinder PURPLE_CARD_BINDER = registerItem(new CardBinder(DyeColor.PURPLE, new FabricItemSettings().maxCount(1)), "card_binder_purple");;

    public static <T extends Item> T registerItem(T item, String name){
        Registry.register(Registries.ITEM, new Identifier("stack_the_cards", name), item);
        return item;
    }

    public static void register(){
        System.out.println("Registered items for Stack the Cards");
    }
}
