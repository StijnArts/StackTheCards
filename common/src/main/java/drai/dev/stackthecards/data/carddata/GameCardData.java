package drai.dev.stackthecards.data.carddata;

import drai.dev.stackthecards.data.*;

public class GameCardData extends CardData{

    public GameCardData(String gameId, String cardId, String nameSpace) {
        super(cardId, nameSpace);
        this.gameId = gameId;
    }

    @Override
    public String getTextureId() {
        return gameId + "_" + cardId;
    }

    @Override
    public CardIdentifier getCardIdentifier() {
        return new CardIdentifier(gameId, null, getCardGame().getGameId(), "");
    }

    @Override
    public String getCardTextureLocation() {
        return gameId + "/" + cardId;
    }

    @Override
    public int getCountInGroup() {
        return getCardGame().cards.size();
    }
}
