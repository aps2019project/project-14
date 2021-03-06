package model.battle;

import model.account.Account;
import model.card.*;
import model.item.ActivationTimeOfItem;
import model.item.Collectible;
import model.item.Flag;
import model.land.LandOfGame;
import model.land.Square;
import model.requirment.Coordinate;
import view.Graphic.BattleScene;
import view.enums.ErrorType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public abstract class Player implements Serializable {
    protected String type;
    private Deck mainDeck;
    private Hand hand;
    private Player opponent;
    private ArrayList<Card> cardsOnLand = new ArrayList<>();
    //private Card flagSaver;
    private int turnForSavingFlag = 0;
    private ArrayList<Flag> ownFlags = new ArrayList<>();
    private Account account;
    private Match match;
    private int turnsPlayed = 0;
    private int manaOfThisTurn = 2 /*don't change this except in initPerTurn*/, mana = 2;
    private GraveYard graveYard = new GraveYard(this);
    private ArrayList<Buff> buffsOnThisPlayer = new ArrayList<>();
    private String avatarPath = null;


    //Collectible item to hand ast :D

    //    public abstract void move(Card card, Square newPosition);
//    public abstract void attack(Card card, Square target);
//    public abstract void useSpecialPower(Card card);

    public abstract void endGame(MatchInfo matchInfo, int reward);

    public Player() {
        Random random = new Random();
        avatarPath = "pics/battle_categorized/profile/" + random.nextInt(17) + ".png";
    }

    public ErrorType putCardOnLand(Card playerCard, Coordinate coordinate, LandOfGame land, boolean server) {

        //false:
        if (getMana() < playerCard.getMp()) {
            return ErrorType.HAVE_NOT_ENOUGH_MANA;
        }

        playerCard.setPosition(getHero().getPosition());
        if (!(playerCard instanceof Spell) && !playerCard.canInsertToCoordination(this.getHero().getPosition().getCoordinate(), coordinate)) {
            return ErrorType.INVALID_TARGET;
        }

        Square square = land.passSquareInThisCoordinate(coordinate);
        if (square == null) {
            return ErrorType.INVALID_SQUARE;
        }

        if (playerCard instanceof Spell && !(playerCard.checkTarget(square) ||
                playerCard.getChange().getTargetType().equals("square"))) {
            return ErrorType.INVALID_TARGET;
        }


        //true:
        mana -= playerCard.getMp();
        if (!server)
            match.getBattleScene().getBattleHeader().makeHeaderEachTurn(getNumberOfPlayer(), this);
        hand.removeUsedCardsFromHand(playerCard);

        //spell:
        if (playerCard instanceof Spell) {
            playerCard.setTarget(land.passSquareInThisCoordinate(coordinate));
            playerCard.getChange().affect(playerCard.getPlayer(), playerCard.getTarget().getTargets());
            graveYard.addCardToGraveYard(playerCard, land.passSquareInThisCoordinate(coordinate));
            return null;
        }

        playerCard.setPosition(null);

        //if square has Collectible item:
        if (square.getObject() instanceof Collectible &&
                ((Collectible) square.getObject()).getTarget().checkTheOneWhoCollects(playerCard)) {
            Collectible collectible = (Collectible) square.getObject();
            getHand().addToCollectibleItem(collectible);
            collectible.setTheOneWhoCollects(playerCard);
            if (!server) {
                BattleScene.getSingleInstance().getCollectibleView(collectible).setOpacity(0);
                BattleScene.getSingleInstance().showAlert(collectible.getName() + ": " + collectible.getDescription());
            }
        }

        //cellEffect:
        for (Buff buff : square.getBuffs()) {
            addBuffToPlayer(buff);
        }
        if (!server)
            square.clearBuffs();

        //flags:
        if (square.getFlags().size() > 0) {
            for (Flag flag : square.getFlags()) {
                flag.setOwnerCard(playerCard);
                playerCard.getPlayer().addToOwnFlags(flag);
                if (!server)
                    BattleScene.getSingleInstance().getFlagView(flag).setOpacity(0);
            }
            square.clearFlags();
        }


        //item:
        if (mainDeck.getItem() != null && mainDeck.getItem().getActivationTimeOfItem() == ActivationTimeOfItem.ON_PUT &&
                mainDeck.getItem().getTarget().checkTheOneWhoDoesTheThing(playerCard)) {
            mainDeck.getItem().setTarget(this);
            mainDeck.getItem().getChange().affect(this, mainDeck.getItem().getTarget().getTargets());
        }

        //put card on land:
        if (playerCard instanceof Minion) {
            if (((Minion) playerCard).getActivationTimeOfSpecialPower() ==
                    ActivationTimeOfSpecialPower.ON_SPAWN) {
                playerCard.useSpecialPower(square);
                playerCard.setTarget(land.passSquareInThisCoordinate(coordinate));
                playerCard.getChange().affect(this, playerCard.getTargetClass().getTargets());
            }
        }

        playerCard.setPosition(square);
        getCardsOnLand().add(playerCard);
        Square[][] squares = land.getSquares();
        squares[coordinate.getX()][coordinate.getY()].setObject(playerCard);
        playerCard.setCanMove(false, 0);

        return null;
    }

    public int getMana() {
        return mana;
    }

    public Hero getHero() {
        return mainDeck.getHero();
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public void addBuffToPlayer(Buff buff) {
        buffsOnThisPlayer.add(buff);
    }

    public void addToOwnFlags(Flag flag) {
        ownFlags.add(flag);
    }

    private int getNumberOfPlayer() {
        if (this.equals(match.getPlayers()[0]))
            return 0;
        else
            return 1;
    }

    public ArrayList<Card> getCardsOnLand() {
        return cardsOnLand;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void useCollectibleItemOnLand(Coordinate coordinate, String collectibleItemId) {
        ArrayList<Collectible> collectibleItems = getHand().getCollectibleItems();
        Collectible selected = null;
        for (Collectible collectible : collectibleItems)
            if (collectible.getName().equals(collectibleItemId))
                selected = collectible;
        if (selected != null) {
            Square square = match.getLand().passSquareInThisCoordinate(coordinate);
            Object preObject = square.getObject();
            square.setObject(selected);
            selected.setTarget(this);
            square.setObject(preObject);
            selected.getChange().affect(this, selected.getTarget().getTargets());
            hand.getCollectibleItems().remove(selected);
        }
    }

    public void addItemToCollectibles(Collectible collectible) {
        hand.getCollectibleItems().add(collectible);
    }

    public Card passCardInGame(String cardId) {
        Card card = hand.passCardInHand(cardId);
        if (card != null) {
            return card;
        }
        ArrayList<Card> cards = new ArrayList<>(cardsOnLand);
        for (Card outPutCard : cards) {
            if (outPutCard.equalCard(cardId) && outPutCard.getPlayer().equals(this))
                card = outPutCard;
        }
        return card;
    }


    private ArrayList<Buff> processBuffsPerTurn(ArrayList<Buff> buffs, Object object) {
        ArrayList<Buff> buffsToBeRemoved = new ArrayList<>();
        if (buffs != null) {
            for (Buff buff : buffs) {
                if (buff != null && !buff.isContinuous()) {
                    int forHowManyTurn = buff.getForHowManyTurn();
                    forHowManyTurn--;
                    buff.setForHowManyTurn(forHowManyTurn);
                    if (forHowManyTurn > 0) {
                        if (object instanceof Card)
                            buff.affect((Card) object);
                        else
                            buff.affect((Player) object);

                    } else {
                        if (buff.isHaveUnAffect() && object instanceof Card)
                            buff.unAffect((Card) object);
                        buffsToBeRemoved.add(buff);
                    }
                }
            }
            for (Buff buff : buffsToBeRemoved)
                buffs.remove(buff);
        }
        return buffs;
    }

    void initPerTurn(int numberOfPlayer, boolean server) {
        hand.checkTheHandAndAddToIt();
        if (!server) {
            match.getBattleScene().getBattleFooter().changeFooterEachTurn();
            match.getBattleScene().getBattleHeader().makeHeaderEachTurn(numberOfPlayer, this);
        }
        for (Card card : cardsOnLand) {
            card.changeTurnOfCanNotAttack(-1);
            card.changeTurnOfCanNotCounterAttack(-1);
            card.changeTurnOfCanNotMove(-1);
            if (card.getTurnOfCanNotAttack() <= 0)
                card.setCanAttack(true, 1);
            if (card.getTurnOfCanNotCounterAttack() <= 0)
                card.setCanCounterAttack(true, 1);
            if (card.getTurnOfCanNotMove() <= 0)
                card.setCanMove(true, 1);

            card.setBuffsOnThisCard(processBuffsPerTurn(card.getBuffsOnThisCard(), card));

            if (card instanceof Minion && ((Minion) card).getHaveSpecialPower() &&
                    ((Minion) card).getActivationTimeOfSpecialPower() ==
                            ActivationTimeOfSpecialPower.PASSIVE)
                card.useSpecialPower(card.getPosition());
        }


        buffsOnThisPlayer = processBuffsPerTurn(buffsOnThisPlayer, this);

        turnsPlayed++;
        if (manaOfThisTurn < 9) {
            manaOfThisTurn++;
            mana = manaOfThisTurn;
            if (!server)
                match.getBattleScene().getBattleHeader().makeHeaderEachTurn(numberOfPlayer, this);
        }
        mainDeck.getHero().addToTurnNotUsedSpecialPower(1);

        if (mainDeck.getItem() != null && mainDeck.getItem().getActivationTimeOfItem() == ActivationTimeOfItem.EACH_ROUND) {
            mainDeck.getItem().setTarget(this);
            mainDeck.getItem().getChange().affect(this, mainDeck.getItem().getTarget().getTargets());
        }

        if (ownFlags.size() > 0)
            turnForSavingFlag++;
    }

    void addToCardsOfLand(Card card) {
        cardsOnLand.add(card);
    }

    public void addToTurnForSavingFlag() {
        turnForSavingFlag++;
    }

    /*public void setFlagSaver(Card card) {
        flagSaver = card;
    }*/

    void setHand() {
        hand = new Hand(mainDeck);
        hand.setCards();
    }

    public void removeCard(Card card) {
        cardsOnLand.remove(card);
    }

    public void playTurnForComputer() {
        //write nothing here for access to this function in computer
    }

    public void manaChange(int number) {
        mana += number;
    }

    public String getAvatarPath() {
        if (avatarPath == null) {
            Random random = new Random();
            int number = random.nextInt(21);
            avatarPath = "pics/battle_categorized/generals/general_portrait_" + number + ".png";
        }
        return avatarPath;
    }

    public String getUserName() {
        if (this instanceof ComputerPlayer) {
            return "computer";
        }
        return account.getUserName();
    }

    public int getTurnForSavingFlag() {
        return turnForSavingFlag;
    }

    void setTurnForSavingFlag(int turnForSavingFlag) {
        this.turnForSavingFlag = turnForSavingFlag;
    }

    public ArrayList<Flag> getOwnFlags() {
        return ownFlags;
    }

    public int getNumberOfFlagsSaved() {
        return ownFlags.size();
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public GraveYard getGraveYard() {
        return graveYard;
    }

    public void setGraveYard(GraveYard graveYard) {
        this.graveYard = graveYard;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Deck getMainDeck() {
        return mainDeck;
    }

    void setMainDeck(Deck mainDeck) {
        this.mainDeck = mainDeck;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public int getTurnsPlayed() {
        return turnsPlayed;
    }

    public void setTurnsPlayed(int turnsPlayed) {
        this.turnsPlayed = turnsPlayed;
    }

    public int getManaOfThisTurn() {
        return manaOfThisTurn;
    }
}
