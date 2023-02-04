package takenoko.game.objective;

import takenoko.action.Action;
import takenoko.game.board.Board;
import takenoko.game.board.VisibleInventory;

public interface Objective {
    class Status {
        public int completed;
        public int totalToComplete;

        public Status(int completed, int totalToComplete) {
            this.completed = completed;
            this.totalToComplete = totalToComplete;
        }

        public Status(Status other) {
            this.completed = other.completed;
            this.totalToComplete = other.totalToComplete;
        }

        public boolean achieved() {
            return completed >= totalToComplete;
        }

        public float progressFraction() {
            return (float) completed / totalToComplete;
        }
    }

    boolean computeAchieved(Board board, Action lastAction, VisibleInventory visibleInventory);

    Status status();

    int getScore();
}
