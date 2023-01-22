package takenoko.objective;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import takenoko.action.Action;
import takenoko.game.board.Board;
import takenoko.game.board.BoardException;
import takenoko.game.objective.BambooSizeObjective;
import takenoko.game.objective.PowerUpNecessity;
import takenoko.game.tile.*;
import takenoko.utils.Coord;

class BambooSizeObjectiveTest {

    BambooSizeObjective b1, b2, b3, b4, b5, b6;
    Board board;

    // The board always apply this action first
    static final Action.PlaceTile INITIAL_ACTION =
            new Action.PlaceTile(new Coord(0, 0), TileDeck.DEFAULT_DRAW_PREDICATE);

    Action.PlaceTile placeBambooTile(Board board, Coord c, Color co) {
        try {
            board.placeTile(c, new BambooTile(co));
        } catch (BoardException | IrrigationException e) {
            fail(e);
        }
        return new Action.PlaceTile(c, TileDeck.DEFAULT_DRAW_PREDICATE);
    }

    @BeforeEach
    void setUp() throws BambooSizeException {
        b1 = new BambooSizeObjective(2, 1, Color.PINK);
        b2 = new BambooSizeObjective(3, 3, Color.YELLOW);
        b3 = new BambooSizeObjective(1, 4, Color.GREEN);
        b4 =
                new BambooSizeObjective(
                        1, 2, Color.PINK, 1, PowerUpNecessity.MANDATORY, PowerUp.FERTILIZER);
        b5 = new BambooSizeObjective(1, 2, Color.PINK, 1, PowerUpNecessity.FORBIDDEN, PowerUp.NONE);
        b6 = new BambooSizeObjective(1, 2, Color.PINK);
        board = new Board();
    }

    @Test
    void testBambooSizeObjectiveException() {
        assertThrows(
                BambooSizeException.class,
                () -> {
                    BambooSizeObjective b5 = new BambooSizeObjective(1, 5, Color.PINK);
                });
        assertThrows(
                BambooSizeException.class,
                () -> {
                    BambooSizeObjective b6 = new BambooSizeObjective(0, 3, Color.PINK);
                });

        assertThrows(
                BambooSizeException.class,
                () -> {
                    BambooSizeObjective b7 = new BambooSizeObjective(1, 0, Color.GREEN);
                });
    }

    @Test
    void testIsAchieved() throws BambooSizeException, BambooIrrigationException, BoardException {
        // Initial verification
        assertFalse(b1.computeAchieved(board, INITIAL_ACTION, null));
        assertFalse(b2.computeAchieved(board, INITIAL_ACTION, null));
        assertFalse(b3.computeAchieved(board, INITIAL_ACTION, null));

        // Verification of the 1st objective
        var secondAction = placeBambooTile(board, new Coord(0, 1), Color.PINK);
        var thirdAction = placeBambooTile(board, new Coord(1, 0), Color.PINK);

        var bt1 = board.getTile(new Coord(0, 1));
        var bt2 = board.getTile(new Coord(1, 0));

        assertTrue(bt1 instanceof BambooTile);
        assertTrue(bt2 instanceof BambooTile);
        BambooTile bt1_1 = (BambooTile) bt1;
        BambooTile bt2_1 = (BambooTile) bt2;

        // First bamboo grow on the 1st tile
        bt1_1.growBamboo();

        assertFalse(b1.computeAchieved(board, thirdAction, null));

        // First bamboo grow on the 2nd tile
        bt2_1.growBamboo();

        assertTrue(b1.computeAchieved(board, thirdAction, null));
        assertTrue(b1.isAchieved());

        // Verification of the 2nd objective
        var fourthAction = placeBambooTile(board, new Coord(1, 1), Color.YELLOW);
        var fifthAction = placeBambooTile(board, new Coord(2, 0), Color.YELLOW);
        var sixthAction = placeBambooTile(board, new Coord(0, 2), Color.YELLOW);

        var bt3 = board.getTile(new Coord(1, 1));
        var bt4 = board.getTile(new Coord(2, 0));
        var bt5 = board.getTile(new Coord(0, 2));

        assertTrue(bt3 instanceof BambooTile);
        assertTrue(bt4 instanceof BambooTile);
        assertTrue(bt5 instanceof BambooTile);
        BambooTile bt3_1 = (BambooTile) bt3;
        BambooTile bt4_1 = (BambooTile) bt4;
        BambooTile bt5_1 = (BambooTile) bt5;

        // We have to irrigate our tile
        bt3_1.irrigateSide(TileSide.UP);
        bt4_1.irrigateSide(TileSide.UP);
        bt5_1.irrigateSide(TileSide.UP);

        // We apply growBamboo() three times in the third tile because we need it for the 2nd
        // objectif
        bt3_1.growBamboo();
        bt3_1.growBamboo();
        bt3_1.growBamboo();

        // We apply growBamboo() three times in the fourth tile because we need it for the 2nd
        // objectif
        bt4_1.growBamboo();
        bt4_1.growBamboo();
        bt4_1.growBamboo();

        // We apply growBamboo() three times in the fifth tile because we need it for the 2nd
        // objectif
        bt5_1.growBamboo();
        bt5_1.growBamboo();
        bt5_1.growBamboo();

        assertTrue(b2.computeAchieved(board, sixthAction, null));
        assertTrue(b2.isAchieved());

        // Verification of the 3rd objective
        var seventhAction = placeBambooTile(board, new Coord(2, 1), Color.GREEN);

        var bt6 = board.getTile(new Coord(2, 1));

        assertTrue(bt6 instanceof BambooTile);
        BambooTile bt6_1 = (BambooTile) bt6;

        // We have to irrigate our tile
        bt6_1.irrigateSide(TileSide.UP);

        // We apply growBamboo() four times in the sixth tile because we need it for the 3rd
        // objectif
        bt6_1.growBamboo();
        bt6_1.growBamboo();
        bt6_1.growBamboo();
        bt6_1.growBamboo();

        assertTrue(b3.computeAchieved(board, seventhAction, null));
        assertTrue(b3.isAchieved());
    }

    @Test
    void testIsAchievedWithPowerUpConstraints()
            throws BoardException, BambooSizeException, BambooIrrigationException,
                    PowerUpException {
        var secondAction = placeBambooTile(board, new Coord(0, 1), Color.PINK);

        var bt1 = board.getTile(new Coord(0, 1));

        assertTrue(bt1 instanceof BambooTile);
        BambooTile bt1_1 = (BambooTile) bt1;

        // First bamboo grow on the 1st tile
        bt1_1.growBamboo();
        // Second bamboo grow on the 1st tile
        bt1_1.growBamboo();

        // The objective b4 is not achieved because a fertilizer power-up is mandatory.
        assertFalse(b4.computeAchieved(board, secondAction, null));
        // But the b5 does, because no power-up are on the tile.
        assertTrue(b5.computeAchieved(board, secondAction, null));
        // And the b6 will be finished all the time, regardless of power-up changes, because it
        // hasn't constraint on power-ups.
        assertTrue(b6.computeAchieved(board, secondAction, null));

        bt1_1.setPowerUp(PowerUp.FERTILIZER);
        assertTrue(b6.computeAchieved(board, secondAction, null));
        // Now the objective b4 is achieved.
        assertTrue(b4.computeAchieved(board, secondAction, null));
        // But the b5 is not anymore.
        assertFalse(b5.computeAchieved(board, secondAction, null));
    }
}
