package drai.dev.stackthecards.data.cardData;

import drai.dev.stackthecards.data.*;

public class GameCardData extends CardData{
    public GameCardData(String gameId, String cardId) {
        super(cardId);
        this.gameId = gameId;
    }

    @Override
    public String getTextureId() {
        return gameId + "_" + cardId;
    }

    @Override
    public CardIdentifier getCardIdentifier() {
        return new CardIdentifier(gameId, null, getCardGame().getGameId());
    }

    @Override
    public String getCardTextureLocation() {
        return gameId + "/" + cardId;
    }
}
