package drai.dev.stackthecards.registry;

import dev.architectury.registry.registries.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;

import java.util.function.*;

import static drai.dev.stackthecards.StackTheCards.*;

public class StackTheCardsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<Card> CARD = registerItem(() -> new Card(new Item.Properties()),"card");
    public static final RegistrySupplier<CardPackItem> CARD_PACK = registerItem(() -> new CardPackItem(new Item.Properties()), "card_pack");
    public static final RegistrySupplier<CardBinder> CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.BROWN, new Item.Properties().stacksTo(1)), "card_binder_brown");
    public static final RegistrySupplier<CardBinder> WHITE_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.WHITE, new Item.Properties().stacksTo(1)), "card_binder_white");
    public static final RegistrySupplier<CardBinder> ORANGE_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.ORANGE, new Item.Properties().stacksTo(1)), "card_binder_orange");
    public static final RegistrySupplier<CardBinder> MAGENTA_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.MAGENTA, new Item.Properties().stacksTo(1)), "card_binder_magenta");
    public static final RegistrySupplier<CardBinder> LIGHT_BLUE_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.LIGHT_BLUE, new Item.Properties().stacksTo(1)), "card_binder_light_blue");
    public static final RegistrySupplier<CardBinder> YELLOW_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.YELLOW, new Item.Properties().stacksTo(1)), "card_binder_yellow");
    public static final RegistrySupplier<CardBinder> LIME_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.LIME, new Item.Properties().stacksTo(1)), "card_binder_lime");
    public static final RegistrySupplier<CardBinder> PINK_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.PINK, new Item.Properties().stacksTo(1)), "card_binder_pink");
    public static final RegistrySupplier<CardBinder> GRAY_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.GRAY, new Item.Properties().stacksTo(1)), "card_binder_gray");
    public static final RegistrySupplier<CardBinder> LIGHT_GRAY_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.LIGHT_GRAY, new Item.Properties().stacksTo(1)), "card_binder_light_gray");
    public static final RegistrySupplier<CardBinder> CYAN_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.CYAN, new Item.Properties().stacksTo(1)), "card_binder_cyan");
    public static final RegistrySupplier<CardBinder> BLUE_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.BLUE, new Item.Properties().stacksTo(1)), "card_binder_blue");
    public static final RegistrySupplier<CardBinder> GREEN_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.GREEN, new Item.Properties().stacksTo(1)), "card_binder_green");
    public static final RegistrySupplier<CardBinder> RED_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.RED, new Item.Properties().stacksTo(1)), "card_binder_red");
    public static final RegistrySupplier<CardBinder> BLACK_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.BLACK, new Item.Properties().stacksTo(1)), "card_binder_black");
    public static final RegistrySupplier<CardBinder> PURPLE_CARD_BINDER = registerItem(() -> new CardBinder(DyeColor.PURPLE, new Item.Properties().stacksTo(1)), "card_binder_purple");;

    public static <T extends Item> RegistrySupplier<T> registerItem(Supplier<T> item, String name){
        
        return ITEMS.register(name, item);
    }

    public static void register(){
        ITEMS.register();
    }

    public static void touch() {

    }
}
