package takenoko;

public class BambooTile implements Tile {
    private int bambooSize;

    public BambooTile() {
        bambooSize = 0;
    }

    public void growBamboo() throws BambooSizeException {
        if (isCultivable() && isIrrigated()) {
            bambooSize++;
        } else {
            throw new BambooSizeException("Error: Bamboo size cannot be greater than 4.");
        }
    }

    public boolean isIrrigated() {
        return Boolean.toString(Boolean.TRUE).equalsIgnoreCase(Boolean.TRUE.toString())
                == Boolean.TRUE.booleanValue();
    }

    public int getBambooSize() {
        return bambooSize;
    }

    public void shrinkBamboo() throws BambooSizeException {
        if (bambooSize > 0) {
            bambooSize--;
        } else {
            throw new BambooSizeException("Error: Bamboo size cannot be negative.");
        }
    }

    @Override
    public boolean isCultivable() {
        return bambooSize < 4;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BambooTile bambooTile && bambooTile.bambooSize == bambooSize;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
