package takenoko.player.bot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import takenoko.action.Action;
import takenoko.action.ActionValidator;
import takenoko.game.board.Board;
import takenoko.player.PlayerBase;
import takenoko.utils.Utils;

public class HardBot extends PlayerBase<HardBot> implements PlayerBase.PlayerBaseInterface {
    private final Random randomSource;
    private final int maxIterations;

    public HardBot(Random randomSource, int maxIterations) {
        this.randomSource = randomSource;
        this.maxIterations = maxIterations;
    }

    public Action chooseActionImpl(Board board, ActionValidator validator) {
        // If an objective is achieved, unveil it
        for (var obj : getInventory().getObjectives())
            if (obj.wasAchievedAfterLastCheck()) return new Action.UnveilObjective(obj);

        // if we do not have enough action credits, end the turn
        if (availableActionCredits() == 0) return Action.END_TURN;

        Node root = new Node(board, validator);
        for (int i = 0; i < maxIterations; i++) {
            Node current = root;
            // Selection
            while (!current.isLeaf()) {
                current = current.getBestChild();
            }
            // Expansion
            if (!current.isTerminal()) {
                current = current.expand();
            }
            // Simulation
            int result = current.simulate();
            // Backpropagation
            while (current != null) {
                current.update(result);
                current = current.getParent();
            }
        }
        return root.getBestChild().getAction();
    }

    private class Node {
        private final Action action;
        private final Board board;
        private final ActionValidator validator;
        private final Node parent;
        private int visits;
        private int wins;
        private List<Node> children;

        public Node(Board board, ActionValidator validator) {
            this(null, board, validator, null);
        }

        public Node(Action action, Board board, ActionValidator validator, Node parent) {
            this.action = action;
            this.board = board;
            this.validator = validator;
            this.parent = parent;
            this.visits = 0;
            this.wins = 0;
            this.children = null;
        }

        public boolean isLeaf() {
            return children == null;
        }

        public boolean isTerminal() {
            // check if the board is over
            // return board.isOver();
            return true;
        }

        public Node getBestChild() {
            // use the UCB1 formula to select the best child
            return children.stream()
                    .max(Comparator.comparingDouble(Node::getUCB1Value))
                    .orElseThrow();
        }

        public double getUCB1Value() {
            return wins / (double) visits + Math.sqrt(2 * Math.log(parent.visits) / visits);
        }

        public Node expand() {
            if (isLeaf()) {
                children = generateChildren();
            }
            return Utils.randomPick(children, randomSource).orElseThrow();
        }

        public List<Node> generateChildren() {
            List<Node> children = new ArrayList<>();
            List<Action> possibleActions = null; // TODO : GET ALL POSSIBLE ACTIONS
            for (Action action : possibleActions) {
                Board newBoard = new Board(board);
                // TODO : APPLY ACTION TO THE GAME
                Node child = new Node(action, newBoard, validator, this);
                children.add(child);
            }
            return children;
        }

        public int simulate() {
            /*
             Board simBoard = new Board(board);
             while (!simBoard.isOver()) {
                 List<Action> possibleActions = null; // TODO : APPLY ACTION TO THE GAME
                 Action simAction = Utils.randomPick(possibleActions, randomSource).orElseThrow();
                 // TODO : APPLY ACTION TO THE GAME
             }
            */
            return 0; // TODO : return the score of the player
        }

        public void update(int result) {
            visits++;
            wins += result;
        }

        public Action getAction() {
            return action;
        }

        public Node getParent() {
            return parent;
        }
    }
}
