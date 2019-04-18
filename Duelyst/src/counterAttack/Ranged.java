package counterAttack;

import requirment.Coordinate;

public class Ranged extends CounterAttack{

    public Boolean checkIfSquereIsWithinRange(Coordinate coordinate){
        return !(Math.abs(this.getCoordinate().getX() - coordinate.getX()) <= 1 &&
                Math.abs(this.getCoordinate().getY() - coordinate.getY()) <= 1)
    }

    public void callFunctionForDefend(Coordinate coordinate){
        if(checkIfSquereIsWithinRange(coordinate)){

        }
    }
    public void callFunctionForAttack(Coordinate coordinate){
        if(checkIfSquereIsWithinRangeOfCard(this.getCard().getRange(), coordinate) && checkIfSquereIsWithinRange(coordinate)){

        }
    }

    private boolean checkIfSquereIsWithinRangeOfCard(int range, Coordinate coordinate){
        return (Math.abs(this.getCoordinate().getX() - coordinate.getX()) +
                Math.abs(this.getCoordinate().getY() - coordinate.getY()) <= range)
    }

}
