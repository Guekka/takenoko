package takenoko;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {

    Board tileboard;

    @BeforeEach
    void setUp() {
        tileboard = new Board();
    }

    @Test
    void placeTileTest() throws Exception {
        Coord c = new Coord(1, 2);
        Coord c2 = new Coord(0, 1);
        Tile t = new BambooTile();
        assertThrows(BoardException.class, () -> tileboard.placeTile(c, t));
        tileboard.placeTile(c2, t);
        assertEquals(tileboard.getTile(c2), t);
    }
}
