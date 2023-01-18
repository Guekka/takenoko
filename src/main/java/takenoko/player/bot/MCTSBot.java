package takenoko.player.bot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import takenoko.action.Action;
import takenoko.action.ActionApplier;
import takenoko.action.PossibleActionLister;
import takenoko.game.GameState;
import takenoko.game.tile.TileDeck;
import takenoko.player.PlayerBase;
import takenoko.utils.Utils;

public class MCTSBot extends PlayerBase<MCTSBot> implements PlayerBase.PlayerBaseInterface {
    private final Random randomSource;
    private final int maxIterations;
    private final int maxDepth;
    private static final int DEFAULT_DEPTH = 5;
    private static final int DEFAULT_MAX_ITERATIONS = 1000;

    public MCTSBot(Random randomSource) {
        this(randomSource, DEFAULT_MAX_ITERATIONS, DEFAULT_DEPTH);
    }

    public MCTSBot(Random randomSource, int maxIterations, int maxDepth) {
        this.randomSource = randomSource;
        this.maxIterations = maxIterations;
        this.maxDepth = maxDepth;
    }

    public MCTSBot(Random randomSource, int maxIterations) {
        this(randomSource, maxIterations, DEFAULT_DEPTH);
    }

    public MCTSBot(MCTSBot other) {
        this(other.randomSource, other.maxIterations, other.maxDepth);
    }

    public Action chooseActionImpl(GameState gameState, PossibleActionLister actionLister) {
        // If an objective is achieved, unveil it
        for (var obj : getInventory().getObjectives())
            if (obj.wasAchievedAfterLastCheck()) return new Action.UnveilObjective(obj);

        // if we do not have enough action credits, end the turn
        if (availableActionCredits() == 0) return Action.END_TURN;

        Node root = new Node(gameState, actionLister, this);
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
        private final GameState gameState;
        private final Node parent;
        private int visits;
        private int wins;
        private final List<Node> children;
        private final PossibleActionLister actionLister;
        private final MCTSBot player;
        private static int count = 0;
        private Logger out;

        public Node(GameState gameState, PossibleActionLister actionLister, MCTSBot player) {
            this(null, gameState, actionLister, null, player);
        }

        public Node(
                Action action,
                GameState gameState,
                PossibleActionLister actionLister,
                Node parent,
                MCTSBot player) {
            count++;
            this.action = action;
            this.gameState = gameState.copy();
            this.parent = parent;
            this.player = new MCTSBot(player);
            this.visits = 0;
            this.wins = 0;
            this.children = new ArrayList<>();
            this.actionLister = new PossibleActionLister(actionLister);
            this.out = Logger.getLogger("takenoko.player.bot.MCTSBot");
            // Mute the logger
            // this.out.setLevel(Level.OFF);
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }

        public boolean isTerminal() {
            return gameState.isOver() || gameState.numTurn() > maxDepth;
        }

        public Node getBestChild() {
            return children.stream()
                    .max(Comparator.comparingDouble(Node::getUCB1Value))
                    .orElseThrow();
        }

        public double getUCB1Value() {
            return wins / (double) visits + Math.sqrt(2 * Math.log(parent.visits) / visits);
        }

        public Node expand() {
            if (isLeaf()) {
                generateChildren();
            }
            return Utils.randomPick(children, randomSource).orElseThrow();
        }

        public void generateChildren() {
            for (var a : actionLister.getPossibleActions(TileDeck.DEFAULT_DRAW_TILE_PREDICATE)) {
                GameState newGameState = gameState.copy();
                var applier =
                        new ActionApplier(
                                gameState.board(),
                                out,
                                gameState.inventory(),
                                gameState.tileDeck());
                applier.apply(a, player);
                children.add(new Node(a, newGameState, actionLister, this, player));
            }
        }

        public int simulate() {
            while (!gameState.isOver() && gameState.numTurn() < maxDepth) {
                List<Action> actions =
                        actionLister.getPossibleActions(TileDeck.DEFAULT_DRAW_TILE_PREDICATE);
                Action action = Utils.randomPick(actions, randomSource).orElse(Action.END_TURN);
                var applier =
                        new ActionApplier(
                                gameState.board(),
                                out,
                                gameState.inventory(),
                                gameState.tileDeck());
                applier.apply(action, player);
            }
            // check if the bot wins
            return player.getScore();
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
