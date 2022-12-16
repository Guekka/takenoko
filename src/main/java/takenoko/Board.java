package takenoko;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
    public static final Coord POND_COORD = new Coord(0, 0);
    private final Map<Coord, Tile> tiles;

    public Board() {
        tiles = new HashMap<>();
        tiles.put(POND_COORD, new PondTile());
    }

    public void placeTile(Coord c, Tile t) throws Exception {
        if (!c.isAdjacentTo(POND_COORD)) {
            throw new BoardException("Error: non-adjacent tile.");
        }
        if (tiles.containsKey(c)) {
            throw new BoardException(
                    "Error: There is already a tile present at theses coordinates.");
        }
        tiles.put(c, t);

        for (TileSide side : TileSide.values()) {
            Coord adjacentCoord = c.adjacentCoordSide(side);
            if (tiles.containsKey(adjacentCoord)) {
                if (tiles.get(adjacentCoord).isSideIrrigated(side.oppositeSide())) {
                    t.irrigateSide(side);
                }
            }
        }
    }

    public void placeIrrigation(Coord coord, TileSide side) {
        try {
            tiles.get(coord).irrigateSide(side);
            tiles.get(coord.adjacentCoordSide(side)).irrigateSide(side.oppositeSide());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Tile getTile(Coord c) throws BoardException {
        if (!tiles.containsKey(c)) {
            throw new BoardException(
                    "Error: the tile with these coordinates is not present on the board.");
        }
        return tiles.get(c);
    }

    public Set<Coord> getAvailableCoords() {
        return tiles.keySet().stream()
                .flatMap(c -> Stream.of(c.adjacentCoords()))
                .filter(c -> !tiles.containsKey(c))
                .collect(Collectors.toSet());
    }
}
