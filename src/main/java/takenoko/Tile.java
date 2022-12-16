package takenoko;

public interface Tile {
    boolean isCultivable();

    boolean isSideIrrigated(TileSides side);
}
