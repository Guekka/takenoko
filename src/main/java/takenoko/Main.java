package takenoko;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import takenoko.game.Game;
import takenoko.game.tile.TileDeck;
import takenoko.player.Player;
import takenoko.player.bot.RandomBot;
import takenoko.player.bot.RuleBasedBot;
import takenoko.player.bot.strategies.Strategies;

public class Main {
    public static void main(String... args) {
        List<Player> players =
                List.of(
                        new RandomBot(new Random()),
                        new RuleBasedBot(new Random(), Strategies.PLOT_RUSH));
        var tileDeck = new TileDeck(new Random());
        var logger = Logger.getGlobal();
        var game = new Game(players, logger, tileDeck, new Random());
        var winner = game.play();

        if (winner.isPresent()) {
            logger.info("There is a winner: " + winner.get());
        } else {
            logger.info("No winner");
        }
    }
}
