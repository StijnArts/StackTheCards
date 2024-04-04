package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.data.CardIdentifier.GAME_ID_KEY;

public class CardConnection {
    public static final String CONNECTION_ID = "connection_id";
    private static final String JSON_CONNECTION_ID_KEY = "connectionId";
    private static final String JSON_LAYOUT_KEY = "layout";
    private final String connectionId;
    private String cardGameId;
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
                for (var rowJson : layoutJson) {
                    JSONArray row = (JSONArray) ((JSONObject) rowJson).get("row");
                    var rowList = new ArrayList<CardConnectionEntry>();
                    for (var entry  : row) {
                        rowList.add(CardConnectionEntry.parse((JSONObject)entry));
                    }
                    layout.add(rowList);
                }
                cardConnection.setLayout(layout);
            } catch (Exception e){
                throw new MalformedJsonException("Card connection layout was malformed: "+e.getMessage());
            }
        }
        return cardConnection;
    }

    public static CardIdentifier removeConnection(ItemStack stack) {
        NbtList nbtList = Card.getCardDataNBT(stack, Card.STORED_CARD_CONNECTION_KEY);
        var poppedCard = nbtList.getCompound(nbtList.size()-1);
        nbtList.remove(nbtList.size()-1);
        stack.getOrCreateNbt().put(Card.STORED_CARD_CONNECTION_KEY, nbtList);
        System.out.println("removed bottom card from connection: current size ="+nbtList.size());
        if(nbtList.size()<3){
            CardConnection.breakConnections(stack);
        }
        return CardIdentifier.getCardIdentifier(poppedCard);
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
        var selfCardIdentifier = Card.getCardIdentifier(self);
        var otherCardIdentifier = Card.getCardIdentifier(other);
//        if(selfCardIdentifier.isEqual(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier()) || otherCardIdentifier.isEqual(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier()))
        if(selfCardIdentifier.isEqual(otherCardIdentifier)) return true;
        //if its already in the connection, don't add it
        if(connectedCards.stream().anyMatch(cardIdentifier -> cardIdentifier.isEqual(otherCardIdentifier)) || selfCardIdentifier.isEqual(otherCardIdentifier)) return true;
        //if card fits in the current connection, add it to the current one
        if(currentConnection!=null && currentConnection.accepts(otherCardIdentifier)) {
            CardConnection.addToConnection(self, otherCardIdentifier, currentConnection);
            return true;
        }

        //Card is not in the current connection and doesnt fit in the currentConnection,
        //so see if there is one that does fit all of the currently connected cards
        var cardsToConnect = new ArrayList<>(connectedCards);
        cardsToConnect.add(otherCardIdentifier);
        var connections = Card.getCardData(self).getCardGame().getConnections(selfCardIdentifier);
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
                if(cardsToConnect.stream().noneMatch(cardIdentifier -> cardIdentifier.isEqual(identifier))) return false;
            }
            return true;
    }

    public boolean matches(List<CardIdentifier> cardsToConnect) {
        for (var identifier : cardsToConnect) {
            if(getCardIdentifiers().stream().noneMatch(cardIdentifier -> cardIdentifier == null || cardIdentifier.isEqual(identifier))) return false;
        }
        return true;
    }

    public static List<CardIdentifier> getConnectedCards(ItemStack self) {
        var nbt = Card.getCardDataNBT(self, Card.STORED_CARD_CONNECTION_KEY);
        var connectedCards = new ArrayList<CardIdentifier>();
        for (int i = 1; i < nbt.size(); i++) {
            var identifier = CardIdentifier.getCardIdentifier(nbt.getCompound(i));
            connectedCards.add(identifier);
//            System.out.println("found a connectedCard!");
        }
        var selfId = Card.getCardIdentifier(self);
        if(!connectedCards.stream().anyMatch(cardIdentifier -> cardIdentifier.isEqual(selfId))) connectedCards.add(selfId);
        return connectedCards;
    }

    public static boolean hasConnectedCards(ItemStack stack) {
        return Card.getCardDataNBT(stack, Card.STORED_CARD_CONNECTION_KEY).size() > 0;
    }

    public static List<CardConnectionEntry> getCardConnections(ItemStack stack) {
        var nbt = Card.getCardDataNBT(stack, Card.STORED_CARD_CONNECTION_KEY);
        var connectedCards = new ArrayList<CardConnectionEntry>();
        var connection = getConnection(stack);

        for (int i = 1; i < nbt.size(); i++) {
            var identifier = connection.getConnectionEntry(CardIdentifier.getCardIdentifier(nbt.getCompound(i)));
            connectedCards.add(identifier);
        }
        return connectedCards;
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

    public boolean accepts(CardIdentifier cardIdentifier) {
        return getConnectionEntry(cardIdentifier) != null;
    }

    public static CardConnection getConnection(ItemStack self) {
        var nbt = Card.getCardDataNBT(self, Card.STORED_CARD_CONNECTION_KEY);
        var connectionNbt = nbt.getCompound(0);
        if(connectionNbt.contains(CONNECTION_ID)) {
            var connectionId = connectionNbt.getString(CONNECTION_ID);
            var game = CardGameRegistry.getCardGame(connectionNbt.getString(GAME_ID_KEY));
            if(game.cardConnections.containsKey(connectionId)){
//                System.out.println("card had a connection: "+connectionId);
                return game.cardConnections.get(connectionId);
            }
        }
//        System.out.println("card had no connections");
        return null;
    }

    public static void addToConnection(ItemStack self, CardIdentifier otherCardIdentifier, CardConnection connection) {
        var connectionEntry = connection.getConnectionEntry(otherCardIdentifier);
        assert connectionEntry != null;
        NbtList nbtList = Card.getCardDataNBT(self, Card.STORED_CARD_CONNECTION_KEY);
        if(!nbtList.getCompound(0).contains(CONNECTION_ID)){
            nbtList.add(CardConnection.createNbt(connection));
        }
        nbtList.add(CardConnection.createNbt(connectionEntry));
        self.getOrCreateNbt().put(Card.STORED_CARD_CONNECTION_KEY, nbtList);
//        System.out.println("added card to connection: "+ connection.connectionId);
    }

    private static NbtElement createNbt(CardConnectionEntry connectionEntry) {
        NbtCompound nbtCompound = (NbtCompound) CardIdentifier.createNbt(connectionEntry.self);
        nbtCompound.putString(CardConnectionEntry.CONNECTION_X_MODIFIER, String.valueOf(connectionEntry.xModifier));
        nbtCompound.putString(CardConnectionEntry.CONNECTION_Y_MODIFIER, String.valueOf(connectionEntry.yModifier));
        nbtCompound.putString(CardConnectionEntry.CONNECTION_DIRECTION, String.valueOf(connectionEntry.connectingDirection));
        nbtCompound.putString(CardConnectionEntry.CONNECTION_ROTATION, String.valueOf(connectionEntry.rotation));
        return nbtCompound;
    }

    private static NbtElement createNbt(CardConnection connection) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putString(CardConnection.CONNECTION_ID, String.valueOf(connection.connectionId));
        nbtCompound.putString(GAME_ID_KEY, String.valueOf(connection.cardGameId));
        return nbtCompound;
    }

    private CardConnectionEntry getConnectionEntry(CardIdentifier cardIdentifier) {
        for (var row : layout) {
            for (var column: row) {
                if(column.self.isEqual(cardIdentifier)) return column;
            }
        }
        return null;
    }

    public static void breakConnections(ItemStack stack) {
        NbtList nbtList = Card.getCardDataNBT(stack, Card.STORED_CARD_CONNECTION_KEY);
        nbtList.clear();
        stack.removeSubNbt(Card.STORED_CARD_CONNECTION_KEY);

    }

    public List<CardIdentifier> getCardIdentifiers() {
        var identifiers = layout.stream().flatMap(List::stream).map(cardConnectionEntry -> cardConnectionEntry.self).collect(Collectors.toList());
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
}
