package takenoko;

public enum TileSides {
    UP,
    UP_RIGHT,
    DOWN_RIGHT,
    DOWN,
    DOWN_LEFT,
    UP_LEFT;

    public TileSides rightSide() {
        return values()[(ordinal() + 1) % values().length];
    }

    public TileSides leftSide() {
        return values()[(ordinal() + 1) % values().length];
    }
}
