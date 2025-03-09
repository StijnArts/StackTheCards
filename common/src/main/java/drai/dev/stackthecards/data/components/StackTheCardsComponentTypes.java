package drai.dev.stackthecards.data.components;

import dev.architectury.registry.registries.*;
import drai.dev.stackthecards.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.*;

import java.util.function.*;

public class StackTheCardsComponentTypes {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(StackTheCards.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<Card.CardRecord>> CARD_DATA_COMPONENT = register("card_data_component",
            builder -> builder.persistent(Card.CODEC).networkSynchronized(Card.STREAM_CODEC));

    public static final RegistrySupplier<DataComponentType<CardIdentifier>> CARD_IDENTIFIER_COMPONENT = register("card_identifier_component",
            builder -> builder.persistent(CardIdentifier.CODEC)
                    .networkSynchronized(CardIdentifier.STREAM_CODEC));/*

    public static final RegistrySupplier<DataComponentType<CardConnectionEntry.CardConnectionEntryData>> CARD_CONNECTION_ENTRY_COMPONENT = register("card_connection_data_component",
            builder -> builder.persistent(CardConnectionEntry.CODEC)
                    .networkSynchronized(CardConnectionEntry.STREAM_CODEC));*/

    public static final RegistrySupplier<DataComponentType<CardConnection.CardConnectionData>> CARD_CONNECTION_COMPONENT = register("card_connection_component",
            builder -> builder.persistent(CardConnection.CODEC)
                    .networkSynchronized(CardConnection.STREAM_CODEC));

    public static final RegistrySupplier<DataComponentType<CardIdentifier>> CARD_PACK_DATA_COMPONENT = register("card_pack_component",
            builder -> builder.persistent(CardIdentifier.CODEC)
                    .networkSynchronized(CardIdentifier.STREAM_CODEC));

    public static final RegistrySupplier<DataComponentType<CardBinderData>> CARD_BINDER_DATA_COMPONENT = register("card_binder_component",
            builder -> builder.persistent(CardBinderData.CODEC)
                    .networkSynchronized(CardBinderData.STREAM_CODEC));


    private static <T>RegistrySupplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register() {
        DATA_COMPONENT_TYPES.register();
    }

    public static void touch() {

    }
}

