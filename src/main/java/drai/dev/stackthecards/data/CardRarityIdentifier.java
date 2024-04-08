package drai.dev.stackthecards.data;

public class CardRarityIdentifier extends CardIdentifier {
    public String rarityId;

    public CardRarityIdentifier(String gameId, String setId, String cardId, String rarityId){
        this.gameId = gameId;
        this.setId = setId;
        this.cardId = cardId;
        this.rarityId = rarityId;
    }
}
