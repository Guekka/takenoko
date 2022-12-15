package takenoko;

public class PondTile implements Tile {
    private Boolean[] irrigatedSides;

    public void BambooTile() {
        Boolean[] irrigatedSides = {true, true, true, true, true, true};
    }

    public boolean isSideIrrigated(int side) {
        return true;
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
