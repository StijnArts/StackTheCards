package drai.dev.stackthecards.registry;

import drai.dev.stackthecards.items.*;
import net.fabricmc.fabric.api.item.v1.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;

public class Items {
    public static final Card CARD = registerItem(new Card(new FabricItemSettings()),"card");
    public static final CardPack CARD_PACK = registerItem(new CardPack(new FabricItemSettings()), "card_pack");

    public static <T extends Item> T registerItem(T item, String name){
        Registry.register(Registries.ITEM, new Identifier("stack_the_cards", name), item);
        return item;
    }

    public static void register(){
        System.out.println("Registered items for Stack the Cards");
    }
}
