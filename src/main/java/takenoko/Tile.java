package takenoko;

public interface Tile {
    boolean isCultivable();

    boolean isSideIrrigated(TileSides side);

    void irrigateSide(TileSides side) throws Exception;
}
