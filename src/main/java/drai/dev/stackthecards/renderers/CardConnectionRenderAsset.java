package drai.dev.stackthecards.renderers;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.item.*;

import java.util.*;

public class CardConnectionRenderAsset {
    public double maxOffsetX;
    public double maxOffsetY;
    public double minOffsetX;
    public double minOffsetY;
    private final List<CardConnectionAssetEntry> cards = new ArrayList<>();

    public CardConnectionRenderAsset(CardConnection connection, List<CardIdentifier> containedCards, boolean isFlipped) {
        var layout = connection.getLayout();
        maxOffsetX = 0;
        maxOffsetY = 0;
        minOffsetX = 0;
        minOffsetY = 0;

        for (int i = 0; i < layout.size(); i++) {
            var row = layout.get(i);
            var rowSize = CardConnection.getSizeIgnoringEmpty(row);
            var evenRowSize = rowSize % 2;
            var xOffsetForRow = evenRowSize == 1 ? 0 : 64 - 128 / (evenRowSize + 1);
            for (int j = 0; j < rowSize; j++) {
                var columnSize = CardConnection.getMaxColumnSize(connection.getLayoutByColumn());
                var evenColumnSize = columnSize % 2;
                var yOffsetForAllCards = evenColumnSize == 1 ? 0 : 64 - 128 / (evenColumnSize + 1);
                var connectionSlot = row.get(j);
                if (connectionSlot != CardConnectionEntry.EMPTY) {
                    //amount of shifting that had to be done per card on x axis
                    var column = CardConnection.getColumn(connection, j);
                    double xOffset = 0;
                    double yOffset = 0;
                    var cardTexture = CardRenderer.getCardTexture(CardGameRegistry.getCardData(connectionSlot.self), isFlipped);
                    var unit = cardTexture.getUnit();
                    var middleRow = rowSize - (rowSize/2) - evenRowSize;
                    var middleColumn = columnSize - (columnSize/2) - evenColumnSize;
                    var distanceFromMiddleX = (middleRow-(rowSize-1-j));
                    var distanceFromMiddleY = (middleColumn-(columnSize-1-i));
                    //align card in block
                    if(connectionSlot.rotation == CardRotation.RIGHT || connectionSlot.rotation == CardRotation.LEFT){
                        //always apply this unless uneven and the middle one
                        if((columnSize % 2 == 1 && distanceFromMiddleY!=0) || columnSize % 2 == 0)
                            yOffset+= cardTexture.getXOffset()*unit * connectionSlot.connectingDirection.yMod;
                        if((rowSize % 2 == 1 && distanceFromMiddleX!=0) || rowSize % 2 == 0)
                            xOffset+= cardTexture.getYOffset()*unit * connectionSlot.connectingDirection.xMod;
                    } else {
                        if(columnSize % 2 == 1 && distanceFromMiddleY!=0)
                            yOffset+= cardTexture.getYOffset()*unit * connectionSlot.connectingDirection.yMod;
                        if(rowSize % 2 == 1 && distanceFromMiddleX!=0)
                            xOffset+= cardTexture.getXOffset()*unit * connectionSlot.connectingDirection.xMod;
                    }

                    //move cards to their own block
                    double yBlockOffset = distanceFromMiddleY * 128;
                    yOffset += yBlockOffset;
                    double xBlockOffset = distanceFromMiddleX * 128;
                    xOffset += xBlockOffset;

                    //move card by amount of previous cards
                    if(distanceFromMiddleX!=0){
                        double xNeighbourOffset = getOffsetFromNeighbours(row, j, middleRow, distanceFromMiddleX,
                                rowSize % 2 == 0, connectionSlot,true);
                        xOffset -= xNeighbourOffset;
                    }
                    if(distanceFromMiddleY!=0){
                        double yNeighbourOffset = getOffsetFromNeighbours(column, i, middleColumn, distanceFromMiddleY,
                                columnSize % 2 == 0, connectionSlot,false);
                        yOffset -= yNeighbourOffset;
                    }
                    xOffset+=xOffsetForRow + connectionSlot.xModifier;
                    yOffset+=yOffsetForAllCards + connectionSlot.yModifier;
                    if(containedCards.stream().anyMatch(cardIdentifier -> cardIdentifier.isEqual(connectionSlot.self))){
                        cards.add(new CardConnectionAssetEntry(xOffset, yOffset, connectionSlot.layer, connectionSlot, cardTexture));
                    }

                    if(connectionSlot.rotation == CardRotation.RIGHT || connectionSlot.rotation == CardRotation.LEFT){
                        yOffset+= cardTexture.getXOffset()*unit * connectionSlot.connectingDirection.yMod;
                        xOffset+= cardTexture.getYOffset()*unit * connectionSlot.connectingDirection.xMod;
                    } else {
                        yOffset+= cardTexture.getYOffset()*unit * connectionSlot.connectingDirection.yMod;
                        xOffset+= cardTexture.getXOffset()*unit * connectionSlot.connectingDirection.xMod;
                    }

                    if(xOffset > maxOffsetX) maxOffsetX = xOffset;
                    if(xOffset * connectionSlot.connectingDirection.xMod< minOffsetX) minOffsetX = xOffset;
                    if(yOffset > maxOffsetY) maxOffsetY = yOffset;
                    if(yOffset * connectionSlot.connectingDirection.yMod< minOffsetY) minOffsetY = yOffset;
                }
            }
        }
    }

    public List<CardConnectionAssetEntry> getCards() {
        return cards;
    }

    private static double getOffsetFromNeighbours(List<CardConnectionEntry> row, int j, int middle, int distanceToMiddle, boolean isEven,
                                                  CardConnectionEntry connectionSlot, boolean isX) {
        //up
        distanceToMiddle -= (isEven ? 1 : 0)/* * (isX ? -1 : 1)*/;
        distanceToMiddle *= -1;
        var offset = 0;
        var upIterations = distanceToMiddle+(isEven ? 1 : 0);
        for (int k = 0; k < upIterations*-1; k++) {
            var index = middle+k+(isEven ? 1 : 0);
            offset -= getOffset(row, connectionSlot, isX, index) * (middle-index==0 && !isEven ? 1 : 2);
        }
        //down
        var downIterations = distanceToMiddle;
        for (int k = 0; k > downIterations*-1; k--) {
            var index = middle-k;
            offset -= getOffset(row, connectionSlot, isX, index) * (middle-index==0 && !isEven ? 1 : 2);
        }
        return offset;
    }

    private static double getOffset(List<CardConnectionEntry> row, CardConnectionEntry connectionSlot, boolean isX, int index) {
        var neighbourColumn = row.get(index);
        if(neighbourColumn == null) return getOffset(row, connectionSlot, isX, index-1);
        var neighbourCardTexture = CardRenderer.getCardTexture(CardGameRegistry.getCardData(neighbourColumn.self), false);
        var unit = neighbourCardTexture.getUnit();
        if(neighbourColumn.rotation == CardRotation.RIGHT || neighbourColumn.rotation == CardRotation.LEFT){
            //always apply this unless uneven and the middle one
            if(isX)
                return neighbourCardTexture.getYOffset()* connectionSlot.connectingDirection.xMod * unit;
            else {
                return  neighbourCardTexture.getXOffset()* connectionSlot.connectingDirection.yMod * unit;
            }
        } else {
            if(isX)
                return neighbourCardTexture.getXOffset()* connectionSlot.connectingDirection.xMod * unit;
            else
                return neighbourCardTexture.getYOffset()* connectionSlot.connectingDirection.yMod * unit;
        }
    }

    public class CardConnectionAssetEntry {
        public double xOffset;
        public double yOffset;
        public int layer;
        public CardConnectionEntry connectionEntry;
        public CardTexture cardTexture;

        public CardConnectionAssetEntry(double xOffset, double yOffset, int layer, CardConnectionEntry connectionEntry, CardTexture cardTexture) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.layer = layer;
            this.connectionEntry = connectionEntry;
            this.cardTexture = cardTexture;
        }
    }
}
