package takenoko.player;

import takenoko.action.Action;
import takenoko.action.PossibleActionLister;
import takenoko.game.GameState;

@SuppressWarnings("java:S119") // Why couldn't I name my template SELF?
public abstract class PlayerBase<SELF extends PlayerBase<SELF> & PlayerBase.PlayerBaseInterface>
        implements Player {
    private final SELF self;
    private int actionCredits = 0;
    private int score = 0;
    private final Inventory inventory;

    @SuppressWarnings("unchecked")
    protected PlayerBase() {
        // SAFETY: This is safe because we're an abstract class using CRTP
        self = (SELF) this;
        inventory = new Inventory();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void beginTurn(int actionCredits) {
        this.actionCredits = actionCredits;
    }

    public int availableActionCredits() {
        return actionCredits;
    }

    @Override
    public Action chooseAction(GameState gameState, PossibleActionLister actionLister)
            throws PlayerException {
        var action = self.chooseActionImpl(gameState, actionLister);

        actionCredits -= action.hasCost() ? 1 : 0;
        if (actionCredits < 0) {
            throw new IllegalStateException("Not enough action credits");
        }
        return action;
    }

    @Override
    public void increaseScore(int delta) {
        score += delta;
    }

    @Override
    public int getScore() {
        return score;
    }

    public interface PlayerBaseInterface {
        Action chooseActionImpl(GameState gameState, PossibleActionLister actionLister)
                throws PlayerException;
    }
}
