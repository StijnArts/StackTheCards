package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import io.netty.buffer.*;
import net.minecraft.nbt.*;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.data.CardIdentifier.*;
import static drai.dev.stackthecards.data.components.StackTheCardsComponentTypes.CARD_CONNECTION_COMPONENT;

public class CardConnection {
    private static final String JSON_CONNECTION_ID_KEY = "connectionId";
    private static final String JSON_LAYOUT_KEY = "layout";
    private final String connectionId;
    public boolean isSingle = false;
    private String cardGameId = "";
    private List<List<CardConnectionEntry>> layout = new ArrayList<>();
    private List<List<CardConnectionEntry>> layoutByColumn = new ArrayList<>();

    public CardConnection(String connectionId, String cardGame, List<List<CardConnectionEntry>> layout) {
        this.connectionId = connectionId;
        this.cardGameId = cardGame;
        this.layout = layout;
        this.layoutByColumn = transposeLayout(layout);
    }

    public CardConnection(String connectionId, String cardGameId) {
        this.connectionId = connectionId;
        this.cardGameId = cardGameId;
    }

    public static CardConnection parse(JSONObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_CONNECTION_ID_KEY)) throw new MalformedJsonException("Card connection json was empty");
        CardConnection cardConnection;
        try{
            cardConnection = new CardConnection((String) json.get(JSON_CONNECTION_ID_KEY), game.getGameId());
        } catch (Exception e){
            throw new MalformedJsonException("Card connection id was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_LAYOUT_KEY)){
            try{
                var layoutJson = (JSONArray) json.get(JSON_LAYOUT_KEY);
                var layout = new ArrayList<List<CardConnectionEntry>>();
                int i = 0;
                int j = 0;
                for (var rowJson : layoutJson) {
                    JSONArray row = (JSONArray) ((JSONObject) rowJson).get("row");
                    var rowList = new ArrayList<CardConnectionEntry>();
                    for (var entry  : row) {
                        rowList.add(CardConnectionEntry.parse((JSONObject)entry));
                        j++;
                    }
                    layout.add(rowList);
                    i++;
                }
                cardConnection.setLayout(layout);
                cardConnection.isSingle = i == 1 && j ==1;
            } catch (Exception e){
                throw new MalformedJsonException("Card connection layout was malformed: "+e.getMessage());
            }
        }
        return cardConnection;
    }

    public static int getMaxColumnSize(List<List<CardConnectionEntry>> column) {
        if (column.isEmpty()) {
            return 0; // Return null if the input list is empty
        }

        int maxSize = 0; // Initialize with the size of the first list

        // Iterate through the remaining lists
        for (List<?> list : column) {
            int size = list.size();
            if (size > maxSize) {
                maxSize = size; // Update the maximum size
            }
        }

        return maxSize;
    }

    public static boolean checkSingleCardConnection(ItemStack other) {
        var otherCardResourceLocation = Card.getCardIdentifier(other);
        var singleConnections = Card.getCardData(other).getCardGame().getSingleConnections();
        for (CardConnection connection : singleConnections) {
            if(connection.matches(List.of(otherCardResourceLocation))) {
//                CardConnection.breakConnections(self);
                for (var card : List.of(otherCardResourceLocation)) {
                    CardConnection.addToConnection(other, card, connection);
                }
                return true;
            }
        }
        return false;
    }

    private List<List<CardConnectionEntry>> transposeLayout(List<List<CardConnectionEntry>> layout) {
        // Determine the number of rows and columns
        int numRows = layout.size();
        int numCols = layout.isEmpty() ? 0 : getMaxRowSize(layout);

        // Create a new list to hold the transposed data
        List<List<CardConnectionEntry>> transposedList = new ArrayList<>(numCols);
        for (int col = 0; col < numCols; col++) {
            // Create a new column
            List<CardConnectionEntry> transposedColumn = new ArrayList<>(numRows);
            for (int row = 0; row < numRows; row++) {
                List<CardConnectionEntry> currentRow = layout.get(row);
                // If the current row has fewer elements than the current column index, pad it with nulls
                if (col >= currentRow.size()) {
                    transposedColumn.add(null);
                } else {
                    // Otherwise, add the element at the corresponding index
                    transposedColumn.add(currentRow.get(col));
                }
            }
            // Add the transposed column to the transposed list
            transposedList.add(transposedColumn);
        }
        return transposedList;
    }

    private int getMaxRowSize(List<List<CardConnectionEntry>> layout) {
        int maxRowSize = 0;
        for (List<CardConnectionEntry> row : layout) {
            maxRowSize = Math.max(maxRowSize, row.size());
        }
        return maxRowSize;
    }

    public static boolean checkCardConnection(ItemStack self, ItemStack other){
        var currentConnection = CardConnection.getConnection(self);
        //should return identifier of *all* connected cards, including self
        var connectedCards = CardConnection.getConnectedCards(self);
        var selfCardResourceLocation = Card.getCardIdentifier(self);
        var otherCardResourceLocation = Card.getCardIdentifier(other);
//        if(selfCardResourceLocation.isEqual(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier()) || otherCardResourceLocation.isEqual(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier()))
        if(selfCardResourceLocation.isEqual(otherCardResourceLocation)) return true;
        //if its already in the connection, don't add it
        if(connectedCards.stream().anyMatch(cardResourceLocation -> cardResourceLocation.isEqual(otherCardResourceLocation)) || selfCardResourceLocation.isEqual(otherCardResourceLocation)) return true;
        //if card canCraftInDimensions in the current connection, add it to the current one
        if(currentConnection!=null && currentConnection.accepts(otherCardResourceLocation)) {
            CardConnection.addToConnection(self, otherCardResourceLocation, currentConnection);
            return true;
        }

        //Card is not in the current connection and doesnt fit in the currentConnection,
        //so see if there is one that does fit all of the currently connected cards
        var cardsToConnect = new ArrayList<>(connectedCards);
        cardsToConnect.add(otherCardResourceLocation);
        var connections = Card.getCardData(self).getCardGame().getConnections(selfCardResourceLocation);
        for (CardConnection connection : connections ) {
            if(connection.matches(cardsToConnect) && connection.contains(cardsToConnect)) {
                CardConnection.breakConnections(self);
                for (var card : cardsToConnect) {
                    CardConnection.addToConnection(self, card, connection);
                }
                return true;
            }
        }
        for (CardConnection connection : connections) {
            if(connection.matches(cardsToConnect)) {
                CardConnection.breakConnections(self);
                for (var card : cardsToConnect) {
                    CardConnection.addToConnection(self, card, connection);
                }
                return true;
            }
        }

        return false;
    }

    public boolean contains(List<CardIdentifier> cardsToConnect) {
            for (var identifier : getCardIdentifiers()) {
                if(identifier==null) continue;
                if(cardsToConnect.stream().noneMatch(cardResourceLocation -> cardResourceLocation.isEqual(identifier))) return false;
            }
            return true;
    }

    public boolean matches(List<CardIdentifier> cardsToConnect) {
        for (var identifier : cardsToConnect) {
            if(getCardIdentifiers().stream().noneMatch(cardResourceLocation -> cardResourceLocation == null || cardResourceLocation.isEqual(identifier))) return false;
        }
        return true;
    }

    public static List<CardIdentifier> getConnectedCards(ItemStack self) {
        var connectedCards = new ArrayList<>(Card.getOrCreateCardRecord(self).getConnectedCards().stream().map(CardConnectionEntry.CardConnectionEntryData::getSelf).toList());
        var selfId = CardIdentifier.getCardIdentifier(self);
        if(connectedCards.stream().noneMatch(cardResourceLocation -> cardResourceLocation.isEqual(selfId))) connectedCards.add(selfId);
        return connectedCards;
    }

    public static boolean hasConnectedCards(ItemStack stack) {
        return !Card.getOrCreateCardRecord(stack).getConnectedCards().isEmpty();
    }

    public static int getSizeIgnoringEmpty(List<CardConnectionEntry> row) {
        return (int) row.stream().filter(element-> element != CardConnectionEntry.EMPTY && element !=null).count();
    }

    public static List<CardConnectionEntry> getColumn(CardConnection connection, int j) {
        return connection.getLayoutByColumn().get(j);
    }

    public List<List<CardConnectionEntry>> getLayoutByColumn() {
        return layoutByColumn;
    }

    public boolean accepts(CardIdentifier cardResourceLocation) {
        return getConnectionEntry(cardResourceLocation) != null;
    }

    public static CardConnection getConnection(ItemStack self) {
        /*var nbt = Card.getCardDataNBT(self, Card.STORED_CARD_CONNECTION_KEY);
        var connectionNbt = nbt.getCompound(0);
        if(connectionNbt.contains(CONNECTION_ID)) {
            var connectionId = connectionNbt.getString(CONNECTION_ID);
            var game = CardGameRegistry.getCardGame(connectionNbt.getString(GAME_ID_KEY));
            if(game.cardConnections.containsKey(connectionId)){
//                System.out.println("card had a connection: "+connectionId);
                return game.cardConnections.get(connectionId);
            }
        }
//        System.out.println("card had no connections");*/
        var connectionData = self.get(CARD_CONNECTION_COMPONENT.get());
        if(connectionData == null) return null;

        return CardGameRegistry.getCardGame(connectionData.gameId).cardConnections.get(connectionData.connectionId);
    }

    public static void addToConnection(ItemStack self, CardIdentifier otherCardResourceLocation, CardConnection connection) {
        var cardRecord = Card.getOrCreateCardRecord(self);
        var connectionEntry = connection.getConnectionEntry(otherCardResourceLocation);
        assert connectionEntry != null;
        var connectedCards = cardRecord.getConnectedCards();
        var connectionDataComponent = self.get(CARD_CONNECTION_COMPONENT.get());
        if(connectionDataComponent==null){
            self.set(CARD_CONNECTION_COMPONENT.get(), CardConnection.createComponent(connection));
        }
        connectedCards.add(CardConnectionEntry.createConnectionData(connectionEntry));
        Card.saveChanges(self, cardRecord);
//        self.getOrCreateTag().put(Card.STORED_CARD_CONNECTION_KEY, connectedCards);
//        System.out.println("added card to connection: "+ connection.connectionId);
    }

    public static CardIdentifier removeConnection(ItemStack stack) {
        var record = Card.getOrCreateCardRecord(stack);

        var connectedCardEntries = record.getConnectedCards();
        var poppedCard = connectedCardEntries.getLast();
        connectedCardEntries.removeLast();
        Card.saveChanges(stack, record);
//        stack.getOrCreateTag().put(Card.STORED_CARD_CONNECTION_KEY, nbtList);
        if(connectedCardEntries.size() < 2){
            CardConnection.breakConnections(stack);
        }
        poppedCard.self.fixMissingRarity();
        return poppedCard.self;
    }

    private CardConnectionEntry getConnectionEntry(CardIdentifier cardResourceLocation) {
        for (var row : layout) {
            for (var column: row) {
                if(column.self.isEqual(cardResourceLocation)) return column;
            }
        }
        return null;
    }

    public static void breakConnections(ItemStack stack) {
        var record = Card.getOrCreateCardRecord(stack);
        record.clearConnectedCards();
        Card.saveChanges(stack, record);
//        ListTag nbtList = Card.getCardDataNBT(stack, Card.STORED_CARD_CONNECTION_KEY);
//        nbtList.clear();
//        stack.remove(Card.STORED_CARD_CONNECTION_KEY);
    }

    public List<CardIdentifier> getCardIdentifiers() {
        var identifiers = layout.stream().flatMap(List::stream)
                .map(cardConnectionEntry -> cardConnectionEntry.self).collect(Collectors.toList());
        return identifiers;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public CardGame getCardGame() {
        return CardGameRegistry.getCardGame(cardGameId);
    }

    public List<List<CardConnectionEntry>> getLayout() {
        return layout;
    }

    public void setGame(CardGame cardGame) {
        this.cardGameId = cardGame.getGameId();
    }

    public void setLayout(List<List<CardConnectionEntry>> layout) {
        this.layout = layout;
        this.layoutByColumn = transposeLayout(layout);
    }


    private static CardConnectionData createComponent(CardConnection connection) {
//        CompoundTag nbtCompound = new CompoundTag();
//        nbtCompound.putString(CardConnection.CONNECTION_ID, String.valueOf(connection.connectionId));
//        nbtCompound.putString(GAME_ID_KEY, String.valueOf(connection.cardGameId));
        return new CardConnectionData(connection.connectionId, connection.cardGameId);
    }

    public static final Codec<CardConnection.CardConnectionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("connectionId").forGetter(CardConnection.CardConnectionData::getConnectionId),
                    Codec.STRING.fieldOf("gameId").forGetter(CardConnection.CardConnectionData::getGameId)
            ).apply(instance, CardConnection.CardConnectionData::new)
    );
    public static final StreamCodec<ByteBuf, CardConnection.CardConnectionData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CardConnection.CardConnectionData::getConnectionId,
            ByteBufCodecs.STRING_UTF8, CardConnection.CardConnectionData::getGameId,
            CardConnection.CardConnectionData::new);

    public static class CardConnectionData {
        public String connectionId = "";
        public String gameId = "";
        public CardConnectionData() {}
        public CardConnectionData(String connectionId, String gameId) {
            this.connectionId = connectionId;
            this.gameId = gameId;
        }

        public String getConnectionId() {
            return connectionId;
        }

        public String getGameId() {
            return gameId;
        }
    }
}
