package takenoko.player.bot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import takenoko.action.Action;
import takenoko.action.ActionApplier;
import takenoko.action.PossibleActionLister;
import takenoko.game.Game;
import takenoko.game.GameState;
import takenoko.game.tile.TileDeck;
import takenoko.player.PlayerBase;
import takenoko.utils.Utils;

public class HardBot extends PlayerBase<HardBot> implements PlayerBase.PlayerBaseInterface {
    private final Random randomSource;
    private final int maxIterations;
    private GameState gameState;

    public HardBot(Random randomSource, int maxIterations) {
        this.randomSource = randomSource;
        this.maxIterations = maxIterations;
    }

    public HardBot(HardBot other) {
        this(other.randomSource, other.maxIterations);
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
        private final HardBot player;

        public Node(GameState gameState, PossibleActionLister actionLister, HardBot player) {
            this(null, gameState, actionLister, null, player);
        }

        public Node(
                Action action,
                GameState gameState,
                PossibleActionLister actionLister,
                Node parent,
                HardBot player) {
            this.action = action;
            this.gameState = gameState;
            this.parent = parent;
            this.player = new HardBot(player);
            this.visits = 0;
            this.wins = 0;
            this.children = new ArrayList<>();
            this.actionLister = actionLister;
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }

        public boolean isTerminal() {
            return gameState.isOver(); // TODO : ADAPT IF SHORTER GAMES ARE IMPLEMENTED (DEPTH)
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
                Logger out = Logger.getGlobal();
                out.setLevel(Level.OFF);
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
            GameState newGameState = gameState.copy();
            Game game = new Game(newGameState);
            game.play();

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
