package takenoko.game.board;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import takenoko.game.tile.*;
import takenoko.player.Player;
import takenoko.player.bot.EasyBot;
import takenoko.utils.Coord;

class BoardTest {

    Player p1, p2;
    Board tileboard;

    @BeforeEach
    void setUp() {
        p1 = new EasyBot(new Random());
        p2 = new EasyBot(new Random());
        tileboard = new Board(List.of(p1, p2));
    }

    @Test
    void placeTileTest() throws Exception {
        Coord c2 = new Coord(0, 1);
        Tile t = new BambooTile(Color.GREEN);
        tileboard.placeTile(c2, t);
        assertEquals(tileboard.getTile(c2), t);
    }

    @Test
    void placeTileAdjacentToTwo() throws IrrigationException, BoardException {
        tileboard.placeTile(new Coord(0, 1), new BambooTile(Color.GREEN));
        tileboard.placeTile(new Coord(1, 0), new BambooTile(Color.GREEN));
        tileboard.placeTile(new Coord(1, 1), new BambooTile(Color.GREEN));
        assertEquals(tileboard.getTile(new Coord(1, 1)), new BambooTile(Color.GREEN));
    }

    @Test
    void cannotPlaceTileTest() throws IrrigationException, BoardException {
        tileboard.placeTile(new Coord(0, 1), new BambooTile(Color.GREEN));

        // Must be adjacent to the pond or TWO tiles
        var c = new Coord(0, 2);
        var t = new BambooTile(Color.GREEN);

        assertThrows(Exception.class, () -> tileboard.placeTile(c, t));
    }

    @Test
    void contains() throws Exception {
        Coord c = new Coord(0, 1);
        assertFalse(tileboard.contains(c));
        tileboard.placeTile(c, new BambooTile(Color.GREEN));
        assertTrue(tileboard.contains(c));
    }

    @Test
    void placeIrrigationTest() throws Exception {
        Coord c = new Coord(0, 1);
        Tile t = new BambooTile(Color.GREEN);
        tileboard.placeTile(c, t);
        assertTrue(tileboard.getTile(c).isSideIrrigated(TileSide.UP));
        assertThrows(IrrigationException.class, () -> tileboard.placeIrrigation(c, TileSide.UP));
        tileboard.placeIrrigation(c, TileSide.UP_LEFT);
        assertTrue(tileboard.getTile(c).isSideIrrigated(TileSide.UP_LEFT));
    }

    @Test
    void canNotPlaceIrrigationTest() {
        Coord c = new Coord(1, 2);
        assertThrows(BoardException.class, () -> tileboard.placeIrrigation(c, TileSide.UP));
    }

    @Test
    void moveTest() throws Exception {
        Coord c1 = new Coord(0, 1);
        Coord c2 = new Coord(0, 2);
        BambooTile t1 = new BambooTile(Color.GREEN);
        tileboard.placeTile(c1, t1);
        t1.growBamboo();
        tileboard.placeTile(new Coord(1, 0), new BambooTile(Color.GREEN));
        // Gardener
        tileboard.move(MovablePiece.GARDENER, c1, p1);
        assertEquals(tileboard.getPieceCoord(MovablePiece.GARDENER), c1);
        assertThrows(BoardException.class, () -> tileboard.move(MovablePiece.GARDENER, c2, p1));
        Coord c3 = new Coord(1, 1);
        Tile t2 = new BambooTile(Color.GREEN);
        tileboard.placeTile(c3, t2);
        tileboard.placeIrrigation(c3, TileSide.UP_LEFT);
        tileboard.move(MovablePiece.GARDENER, c3, p1);
        // Panda
        assertEquals(0, p1.getVisibleInventory().getBamboo(Color.GREEN));
        tileboard.move(MovablePiece.PANDA, c1, p1);
        assertEquals(tileboard.getPieceCoord(MovablePiece.PANDA), c1);
        assertEquals(1, p1.getVisibleInventory().getBamboo(Color.GREEN));
        assertThrows(BoardException.class, () -> tileboard.move(MovablePiece.PANDA, c2, p1));
        assertEquals(1, p1.getVisibleInventory().getBamboo(Color.GREEN));
        Coord c4 = new Coord(1, 2);
        Tile t3 = new BambooTile(Color.GREEN);
        tileboard.placeTile(c2, new BambooTile(Color.GREEN));
        tileboard.placeTile(c4, t3);
        assertThrows(BoardException.class, () -> tileboard.move(MovablePiece.PANDA, c4, p1));
    }
}
