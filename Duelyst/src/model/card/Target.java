package model.card;

import model.battle.Player;
import model.land.LandOfGame;
import model.land.Square;

import java.util.ArrayList;

public class Target {
    private Player player;
    private LandOfGame land;
    private ArrayList<Square> targets = new ArrayList<>();
    private String counterAttackType; //ranged hybrid melee
    private String cardType; //Minion Hero Spell (Buff?)
    //    private String number; // 0 <=
    private boolean one;
    private boolean row;
    private boolean column;
    private boolean all;
    private boolean random;
    //todo by haniyeh
    private static final int DEFAULT = -1;
    private int distance = DEFAULT;
    //default
    private int attackPower;
    private boolean self;
    private boolean Enemy;
    private boolean Ally;


    public Target() {

    }

    public Target(Player player, LandOfGame land) {
        this.player = player;
        this.land = land;
    }

    public boolean checkIfAttackedCardIsValid(Object attacked) {
        //check beshe ba sharayet target mikhoone ya na todo kamel nistaa
        if (!(attacked instanceof Card))
            return false;
        if (attacked instanceof Spell)
            return true;
        String counterAttackName = null;
        if (attacked instanceof Minion) {
            if (!cardType.equals("Minion")) {
                return false;
            }
            counterAttackName = ((Card)attacked).getCounterAttackName();
        }
        if (attacked instanceof Hero) {
            if (!cardType.equals("Hero")) {
                return false;
            }
            counterAttackName = ((Card)attacked).getCounterAttackName();
        }
        if (counterAttackName != null) {
            return counterAttackName.equals(counterAttackType);

        }
        return false;
    }

    public ArrayList<Square> getTarget(Square square /*squari e ke seda mikone */) {
        //todo
        return null;
    }

    public ArrayList<Square> getTargets() {
        return targets;
    }

//    public void setTargets(Card attacker, Card attacked) {
//        if (attacked == null) {
//
//        } else {
//
//        }
//    }

    public void setTargets(ArrayList<Square> targets) {
        this.targets = targets;
    }

    public boolean isColumn() {
        return column;
    }

    public boolean isAll() {
        return all;
    }

    public boolean isOne() {
        return one;
    }

    public boolean isRow() {
        return row;
    }

    public boolean isRandom() {
        return random;
    }

    public boolean isSelf() {
        return self;
    }

    //todo isAlly and isEnemy
    public boolean isAlly() {
        return Ally;
    }

    public boolean isEnemy() {
        return Enemy;
    }

    public String getCardType() {
        return cardType;
    }

    public int getDistance() {
        return distance;
    }

    public boolean checkNotItSelf(int y, int x, Square position) {
        return y == position.getYCoordinate() && x == position.getXCoordinate();
    }
}
