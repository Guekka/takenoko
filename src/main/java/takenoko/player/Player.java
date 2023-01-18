package takenoko.player;

import takenoko.action.Action;
import takenoko.action.PossibleActionLister;
import takenoko.game.GameState;

public interface Player {
    void beginTurn(int actionCredits);

    int availableActionCredits();

    Action chooseAction(GameState gameState, PossibleActionLister actionLister)
            throws PlayerException;

    Inventory getInventory();

    void increaseScore(int delta);

    int getScore();
}
