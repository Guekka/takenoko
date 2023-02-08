package takenoko.player.bot;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import takenoko.action.Action;
import takenoko.action.ActionValidator;
import takenoko.action.PossibleActionLister;
import takenoko.game.GameInventory;
import takenoko.game.WeatherDice;
import takenoko.game.board.Board;
import takenoko.game.board.BoardException;
import takenoko.game.board.MovablePiece;
import takenoko.game.tile.*;
import takenoko.utils.Coord;

class SaboteurBotTest {
    private Board board;
    private SaboteurBot bot;
    private PossibleActionLister actionLister;
    private ActionValidator validator;

    @BeforeEach
    void setUp() {
        board = new Board();
        var random = new Random(0);
        bot = new SaboteurBot(random, "Saboteur");

        var gameInventory =
                new GameInventory(20, new TileDeck(random), random, new WeatherDice(random));
        validator =
                new ActionValidator(
                        board,
                        gameInventory,
                        bot.getPrivateInventory(),
                        bot.getVisibleInventory(),
                        WeatherDice.Face.SUN);
        actionLister = new PossibleActionLister(board, validator, bot.getPrivateInventory());

        bot.beginTurn(2);
    }

    @Test
    void oftenRetrieveBamboo()
            throws BambooSizeException, BambooIrrigationException, IrrigationException,
                    BoardException {
        var tile = new BambooTile(Color.GREEN);
        board.placeTile(new Coord(0, 1), tile);
        tile.growBamboo();

        var action = bot.chooseAction(board, actionLister);

        assertTrue(
                action instanceof Action.MovePiece movePiece
                        && movePiece.piece() == MovablePiece.PANDA
                        && movePiece.to().equals(new Coord(0, 1)));
    }

    @Test
    void randomValidActionWhenNoBamboo() throws IrrigationException, BoardException {
        var tile = new BambooTile(Color.GREEN);
        board.placeTile(new Coord(0, 1), tile);

        var action = bot.chooseAction(board, actionLister);
        assertTrue(validator.isValid(action));
    }

    @Test
    void chooseRandomWeather() {
        var allowedWeathers = List.of(WeatherDice.Face.SUN, WeatherDice.Face.RAIN);
        var weather = bot.chooseWeather(allowedWeathers);
        assertTrue(allowedWeathers.contains(weather));
    }

    @Test
    void throwsWhenNoPossibleWeather() {
        var allowedWeathers = new ArrayList<WeatherDice.Face>();
        assertThrows(IllegalStateException.class, () -> bot.chooseWeather(allowedWeathers));
    }

    @Test
    void endTurnWhenNoPossibleAction() {
        var lister = mock(PossibleActionLister.class);
        when(lister.getPossibleActions()).thenReturn(new ArrayList<>());
        assertEquals(Action.END_TURN, bot.chooseAction(board, lister));
    }
}
