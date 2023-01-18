package takenoko.game;

import java.util.ArrayList;
import java.util.List;
import takenoko.game.board.Board;
import takenoko.game.objective.BambooSizeObjective;
import takenoko.game.objective.HarvestingObjective;
import takenoko.game.objective.Objective;
import takenoko.game.objective.TilePatternObjective;
import takenoko.game.tile.TileDeck;
import takenoko.player.Player;

public record GameState(
        Board board,
        List<Objective> objectives,
        TileDeck tileDeck,
        GameInventory inventory,
        List<Player> players,
        int numTurn,
        boolean isOver) {

    public GameState copy() {
        Board boardCopy = new Board(this.board);
        List<Player> playersCopy = new ArrayList<>(this.players);
        List<Objective> objectivesCopy = new ArrayList<>();
        for (var objective : this.objectives) {
            if (objective instanceof BambooSizeObjective bambooSizeObjective) {
                objectivesCopy.add(new BambooSizeObjective(bambooSizeObjective));
            } else if (objective instanceof TilePatternObjective tilePatternObjective) {
                objectivesCopy.add(new TilePatternObjective(tilePatternObjective));
            } else if (objective instanceof HarvestingObjective harvestingObjective) {
                objectivesCopy.add(new HarvestingObjective(harvestingObjective));
            }
        }
        GameInventory inventoryCopy = new GameInventory(this.inventory);
        TileDeck tileDeckCopy = new TileDeck(this.tileDeck);

        return new GameState(
                boardCopy,
                objectivesCopy,
                tileDeckCopy,
                inventoryCopy,
                playersCopy,
                this.numTurn,
                isOver);
    }
}
