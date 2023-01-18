package takenoko.player.bot;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import takenoko.game.Game;
import takenoko.game.objective.Objective;
import takenoko.game.objective.TilePatternObjective;
import takenoko.game.tile.Color;
import takenoko.game.tile.TileDeck;
import takenoko.player.Player;
import takenoko.player.PlayerException;

public class HardBotTest {

    @Test
    void testChooseActions() throws PlayerException {
        List<Player> players = List.of(new EasyBot(new Random()), new HardBot(new Random(), 20));
        List<Objective> objectives =
                List.of(
                        new TilePatternObjective(Color.GREEN, TilePatternObjective.TRIANGLE_3),
                        new TilePatternObjective(Color.GREEN, TilePatternObjective.DIAMOND_4),
                        new TilePatternObjective(Color.GREEN, TilePatternObjective.LINE_3),
                        new TilePatternObjective(Color.GREEN, TilePatternObjective.LINE_2));
        var tileDeck = new TileDeck(new Random());
        var logger = Logger.getGlobal();
        var game = new Game(players, objectives, logger, tileDeck);
        var winner = game.play();

        assertTrue(winner.isPresent());
    }
}
