package takenoko.objective;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import takenoko.*;

public class BambooSizeObjectiveTest {

    BambooSizeObjective b1, b3, b4;
    Board board;

    // The board always apply this action first
    static final Action.PlaceTile INITIAL_ACTION =
            new Action.PlaceTile(new Coord(0, 0), new PondTile());

    Action.PlaceTile placeBambooTile(Board board, Coord c) {
        try {
            board.placeTile(c, new BambooTile());
        } catch (BoardException e) {
            fail(e);
        } catch (IrrigationException e) {
            fail(e);
        }
        return new Action.PlaceTile(c, new BambooTile());
    }

    @BeforeEach
    void setUp() throws BambooSizeException {
        b1 = new BambooSizeObjective(1);
        b3 = new BambooSizeObjective(3);
        b4 = new BambooSizeObjective(4);
        board = new Board();
    }

    @Test
    void testBambooSizeObjectiveException() {
        assertThrows(
                BambooSizeException.class,
                () -> {
                    BambooSizeObjective b5 = new BambooSizeObjective(5);
                });
        assertThrows(
                BambooSizeException.class,
                () -> {
                    BambooSizeObjective b0 = new BambooSizeObjective(0);
                });
    }

    @Test
    void testIsAchieved() throws BambooSizeException {
        // Initial verification
        assertFalse(b1.isAchieved(board, INITIAL_ACTION));
        assertFalse(b3.isAchieved(board, INITIAL_ACTION));
        assertFalse(b4.isAchieved(board, INITIAL_ACTION));

        var secondAction = placeBambooTile(board, new Coord(0, 1));

        Tile bt1 = null;
        try {
            bt1 = board.getTile(new Coord(0, 1));
        } catch (BoardException e) {
            throw new RuntimeException(e);
        }

        assertTrue(bt1 instanceof BambooTile);
        BambooTile bt2 = (BambooTile) bt1;

        // First bamboo grow
        bt2.growBamboo();
        // Now b1 is achieved
        assertTrue(b1.isAchieved(board, secondAction));
        assertTrue(b1.wasAchievedAfterLastCheck());

        // Second bamboo grow
        bt2.growBamboo();
        // b1 is no longer achieved
        assertFalse(b1.isAchieved(board, secondAction));
        assertFalse(b1.wasAchievedAfterLastCheck());

        // Third bamboo grow
        bt2.growBamboo();
        // b3 is achieved
        assertTrue(b3.isAchieved(board, secondAction));
        assertTrue(b3.wasAchievedAfterLastCheck());

        // Fourth bamboo grow
        bt2.growBamboo();
        // b3 is no longer achieved, but b4 is
        assertFalse(b3.isAchieved(board, secondAction));
        assertTrue(b4.isAchieved(board, secondAction));
        assertFalse(b3.wasAchievedAfterLastCheck());
        assertTrue(b4.wasAchievedAfterLastCheck());
    }
}