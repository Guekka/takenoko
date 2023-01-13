package takenoko.player.bot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import takenoko.action.Action;
import takenoko.action.ActionValidator;
import takenoko.game.Game;
import takenoko.game.board.Board;
import takenoko.player.PlayerBase;
import takenoko.player.PlayerException;
import takenoko.utils.Utils;

public class HardBot extends PlayerBase<HardBot> implements PlayerBase.PlayerBaseInterface {
    private final Random randomSource;
    private final int maxIterations;

    public HardBot(Random randomSource, int maxIterations) {
        this.randomSource = randomSource;
        this.maxIterations = maxIterations;
    }

    public Action chooseActionImpl(Game game, ActionValidator validator) {
        // If an objective is achieved, unveil it
        for (var obj : getInventory().getObjectives())
            if (obj.wasAchievedAfterLastCheck()) return new Action.UnveilObjective(obj);

        // if we do not have enough action credits, end the turn
        if (availableActionCredits() == 0) return Action.END_TURN;

        Node root = new Node(game, validator);
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

    @Override
    public Action chooseActionImpl(Board board, ActionValidator validator) throws PlayerException {
        return null;
    }

    private class Node {
        private final Action action;
        private final Game game;
        private final ActionValidator validator;
        private final Node parent;
        private int visits;
        private int wins;
        private List<Node> children;

        public Node(Game game, ActionValidator validator) {
            this(null, game, validator, null);
        }

        public Node(Action action, Game game, ActionValidator validator, Node parent) {
            this.action = action;
            this.game = game;
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
            // check if the game is over
            return game.isOver();
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
            List<Action> possibleActions = null; // TODO : APPLY ACTION TO THE GAME
            for (Action action : possibleActions) {
                Game newGame = new Game(game);
                // TODO : APPLY ACTION TO THE GAME
                Node child = new Node(action, newGame, validator, this);
                children.add(child);
            }
            return children;
        }

        public int simulate() {
            Game simGame = new Game(game);
            while (!simGame.isOver()) {
                List<Action> possibleActions = null; // TODO : APPLY ACTION TO THE GAME
                Action simAction = Utils.randomPick(possibleActions, randomSource).orElseThrow();
                // TODO : APPLY ACTION TO THE GAME
            }

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
