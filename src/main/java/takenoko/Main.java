package takenoko;

import java.util.List;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;
import takenoko.game.Game;
import takenoko.game.tile.TileDeck;
import takenoko.player.Player;
import takenoko.player.bot.EasyBot;
import takenoko.utils.LogFormatter;

public class Main {
    public static void main(String... args) {
        List<Player> players = List.of(new EasyBot(new Random()), new EasyBot(new Random()));
        var tileDeck = new TileDeck(new Random());
        var logger = Logger.getGlobal();
        ConsoleHandler handler = new ConsoleHandler();
        logger.addHandler(handler);
        LogFormatter formatter = new LogFormatter();
        logger.setUseParentHandlers(false);
        handler.setFormatter(formatter);
        var game = new Game(players, logger, tileDeck, new Random());
        var winner = game.play();

        if (winner.isPresent()) {
            logger.info("There is a winner: " + winner.get());
        } else {
            logger.info("No winner");
        }
    }
}
