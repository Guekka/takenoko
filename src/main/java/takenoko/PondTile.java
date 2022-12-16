package takenoko;

public class PondTile implements Tile {

    public boolean isSideIrrigated(TileSide side) {
        return true;
    }

    public void irrigateSide(TileSide side) throws Exception {
        throw new Exception("Cannot irrigate a pond");
    }

    @Override
    public boolean isCultivable() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PondTile;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
