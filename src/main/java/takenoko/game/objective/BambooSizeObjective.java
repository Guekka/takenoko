package takenoko.game.objective;

import takenoko.action.Action;
import takenoko.game.board.Board;
import takenoko.game.board.BoardException;
import takenoko.game.tile.BambooSizeException;
import takenoko.game.tile.BambooTile;
import takenoko.game.tile.Color;
import takenoko.game.tile.Tile;
import takenoko.player.Inventory;

public class BambooSizeObjective implements Objective {

    private final int numberOfBamboos;
    private final int sizeObjective;
    private final Color color;
    private boolean achieved = false;

    public BambooSizeObjective(int nbOfBamboos, int size, Color c) throws BambooSizeException {

        if (nbOfBamboos < 1 || nbOfBamboos > 4) {
            throw new BambooSizeException("Error : unreachable number of bamboos.");
        }
        if (size < 1 || size > 4) {
            throw new BambooSizeException("Error : unreachable bamboo size.");
        }

        this.numberOfBamboos = nbOfBamboos;
        this.sizeObjective = size;
        this.color = c;
    }

    public BambooSizeObjective(BambooSizeObjective other) {
        sizeObjective = other.sizeObjective;
        achieved = other.achieved;
    }

    @Override
    public boolean isAchieved(Board board, Action lastAction, Inventory ignored) {
        int nbOfBamboos = numberOfBamboos;
        achieved = false;
        for (var coord : board.getPlacedCoords()) {
            Tile tile = null;
            try {
                tile = board.getTile(coord);
            } catch (BoardException e) {
                throw new IllegalStateException();
            }
            if (tile instanceof BambooTile bambooTile
                    && bambooTile.getBambooSize() == sizeObjective
                    && bambooTile.getColor() == color) {
                nbOfBamboos--;
            }
        }
        if (nbOfBamboos <= 0) {
            achieved = true;
        }
        return achieved;
    }

    @Override
    public boolean wasAchievedAfterLastCheck() {
        return achieved;
    }

    @Override
    public int getScore() {
        return 1;
    }
}
