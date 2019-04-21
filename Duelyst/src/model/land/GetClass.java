package model.land;

import model.card.Buff;
import model.card.Hero;
import model.card.Minion;
import model.card.Spell;
import model.counterAttack.Hybrid;
import model.counterAttack.Melee;
import model.counterAttack.Ranged;

class GetClass {
    private static final GetClass GET_CLASS = new GetClass();
    private GetClass() {

    }

    public GetClass getInstance() {
        return GET_CLASS;
    }

    public Class getClass(Object object) {
        if (object instanceof Buff)
            return Buff.class;
        if (object instanceof Hero)
            return Hero.class;
        if (object instanceof Minion)
            return Minion.class;
        if (object instanceof Spell)
            return Spell.class;
        if (object instanceof Hybrid)
            return Hybrid.class;
        if (object instanceof Melee)
            return Melee.class;
        if (object instanceof Ranged)
            return Ranged.class;
        return null;
    }
}