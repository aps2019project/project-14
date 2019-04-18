package card;


import java.util.ArrayList;

public abstract class Card {
    private String name;
    private CardId cardId;
    private ArrayList<Integer> turnsOfPickingUp = new ArrayList<>();


    public void setCardIdFromClassCardId() {
    }

    public void addNewNameOfCardToCard(String cardName) {
    }

    public void decreaseNumberOfSameCard(String cardName) {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCardId(CardId cardId) {
        this.cardId = cardId;
    }

    public CardId getCardId() {
        return cardId;
    }

    public void addToTurnsOfPickingUp(int turn) {
        turnsOfPickingUp.add(turn);
    }

    public void addToTurnOfpickingUp(int number) {
        turnsOfPickingUp.add(number);
    }

    public boolean equals(String cardId) {
        if(this.cardId.getCardIdAsString().equals(cardId))
            return true;
        return false;
    }
    public void removeCounterAttack(){//TODO

    }
}
