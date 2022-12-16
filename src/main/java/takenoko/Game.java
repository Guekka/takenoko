package takenoko;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {
    private static final int DEFAULT_ACTION_CREDIT = 2;
    private final Board board;
    private final List<Player> players;

    private final Logger out;
    private int numTurn = 1;

    private int irrigationStickLeft = 20;

    public Game(List<Player> players, Logger out) {
        board = new Board();
        this.players = players;
        this.out = out;
    }

    public Player play() {
        this.out.log(Level.INFO, "Beginning of the game!");
        while (true) {
            this.out.log(Level.INFO, "Beginning of the tour number " + numTurn + "!");
            var winner = playTurn();
            numTurn++;
            if (winner.isPresent()) {
                this.out.log(Level.INFO, "Someone won!");
                this.out.log(Level.INFO, "End of the game.");
                return winner.get();
            }
        }
    }

    private Optional<Player> playTurn() {
        int numPlayer = 1;
        int numAction = 1;
        for (Player player : players) {
            this.out.log(Level.INFO, "Turn of player number " + numPlayer + " to play!");
            player.beginTurn(DEFAULT_ACTION_CREDIT);
            while (!player.wantsToEndTurn()) {
                this.out.log(
                        Level.INFO,
                        "Player number " + numPlayer + " do his action number " + numAction + ":");
                var action = player.chooseAction(board);
                playAction(action, player);
                numAction++;
            }
            numPlayer++;
            numAction = 1;
        }
        return Optional.of(players.get(0)); // TODO: determine winning condition
    }

    // S1301: we want pattern matching so switch is necessary
    // S1481: pattern matching requires variable name even if unused
    @SuppressWarnings({"java:S1301", "java:S1481"})
    private void playAction(Action action, Player player) {
        switch (action) {
            case Action.None ignored -> {
                // do nothing
            }
            case Action.PlaceTile placeTile -> {
                try {
                    board.placeTile(placeTile.coord(), placeTile.tile());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case Action.TakeIrrigationStick takeIrrigationStick -> {
                try {
                    takeIrrigationStick(player);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            case Action.PlaceIrrigationStick placeIrrigationStick -> {
                try {
                    board.placeIrrigation(
                            placeIrrigationStick.coord(), placeIrrigationStick.side());

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    // take an irrigation stick from the stack and put it in the player's inventory
    private void takeIrrigationStick(Player player) throws Exception {
        if (irrigationStickLeft == 0) {
            throw new Exception("No more irrigation stick left");
        }
        player.takeIrrigationStick();
        irrigationStickLeft--;
    }

    private void placeIrrigationStick(Player player, Coord coord, TileSides side) throws Exception {
        if (player.getInventory() <= 0) {
            throw new Exception("No more irrigation stick left in player's inventory");
        }
        board.placeIrrigation(coord, side);
        player.placeIrrigationStick();
    }
}
