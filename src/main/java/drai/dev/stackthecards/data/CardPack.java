package drai.dev.stackthecards.data;

public class CardPack {
    private String packId;
    private String gameId;
    private CardSet setId;

    public String getPackId() {
        return packId;
    }

    public void setGame(CardGame cardGame) {
        this.gameId = cardGame.getGameId();
    }

    public void setSet(CardSet cardSet) {
        this.setId = cardSet;
    }
}
