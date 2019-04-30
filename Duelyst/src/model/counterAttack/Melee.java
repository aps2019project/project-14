package model.counterAttack;

import model.requirment.Coordinate;

public class Melee extends CounterAttack {

    public void callFunctionForDefend(Coordinate coordinate) {
        if (checkIfSquareIsWithinRange(coordinate)) {
            //todo
        }
    }

    public void callFunctionForAttack(Coordinate coordinate) {
        if (checkIfSquareIsWithinRange(coordinate)) {
            //todo
        }
    }

    public Boolean checkIfSquareIsWithinRange(Coordinate coordinate) {
        return (Math.abs(this.getCoordinate().getX() - coordinate.getX()) <= 1 &&
                Math.abs(this.getCoordinate().getY() - coordinate.getY()) <= 1);
    }
}
