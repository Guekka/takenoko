package takenoko.main;

import java.util.List;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import takenoko.game.Game;
import takenoko.game.WeatherDice;
import takenoko.game.tile.TileDeck;
import takenoko.player.Player;
import takenoko.player.PlayerType;
import takenoko.player.bot.EasyBot;
import takenoko.utils.LogFormatter;

public class Main {

    public static void main(String... args) {
        demo();
        simulate();
    }

    public static void demo() {
        List<Player> players = List.of(new EasyBot(new Random()), new EasyBot(new Random()));
        var tileDeck = new TileDeck(new Random());
        var logger = Logger.getGlobal();
        ConsoleHandler handler = new ConsoleHandler();
        logger.addHandler(handler);
        LogFormatter formatter = new LogFormatter();
        logger.setUseParentHandlers(false);
        handler.setFormatter(formatter);
        var game = new Game(players, logger, tileDeck, new WeatherDice(new Random()), new Random());
        var winner = game.play();

        if (winner.isPresent()) {
            logger.info("There is a winner: " + winner.get());
        } else {
            logger.info("No winner");
        }
    }

    public static void simulate() {

        var logger = Logger.getGlobal();
        logger.setLevel(Level.OFF);

        Simulator simulator =
                new Simulator(500, List.of(PlayerType.RANDOM, PlayerType.RANDOM), logger);
        System.out.println(simulator.simulate());
    }
}