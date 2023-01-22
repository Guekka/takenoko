package takenoko.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import takenoko.action.Action;
import takenoko.game.tile.TileDeck;
import takenoko.player.InventoryException;
import takenoko.player.Player;
import takenoko.player.PlayerException;
import takenoko.player.PrivateInventory;
import takenoko.player.bot.EasyBot;
import utils.TestLogHandler;

class GameTest {
    TileDeck tileDeck;
    TestLogHandler logHandler;
    Logger logger;

    @BeforeEach
    public void setUp() throws InventoryException {
        tileDeck = new TileDeck(new Random(0));

        logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logHandler = new TestLogHandler();
        logger.addHandler(logHandler);
    }

    void assertNoSevereLog() {
        assertEquals(
                Collections.emptyList(),
                logHandler.getRecords().stream()
                        .filter(r -> r.getLevel().equals(java.util.logging.Level.SEVERE))
                        // so that we can see the messages
                        .map(LogRecord::getMessage)
                        .toList());
    }

    @Test
    void testGetWinner() throws PlayerException {
        var p1 = mock(Player.class);
        when(p1.getPrivateInventory()).thenReturn(new PrivateInventory());
        when(p1.getScore()).thenReturn(1);
        when(p1.chooseAction(any(), any())).thenReturn(Action.END_TURN);

        var p2 = mock(Player.class);
        when(p2.getPrivateInventory()).thenReturn(new PrivateInventory());
        when(p2.getScore()).thenReturn(2);
        when(p2.chooseAction(any(), any())).thenReturn(Action.END_TURN);

        var players = List.of(p1, p2);
        var game = new Game(players, logger, tileDeck);

        assertEquals(Optional.of(p2), game.play());
        assertNoSevereLog();
    }

    @Test
    void randomGame() {
        // We just want to check that the game is not crashing
        // So we run some games

        for (int i = 0; i < 10; i++) {
            List<Player> players = List.of(new EasyBot(new Random()), new EasyBot(new Random()));
            var game = new Game(players, logger, tileDeck);
            game.play();
            assertNoSevereLog();
        }
    }
}
