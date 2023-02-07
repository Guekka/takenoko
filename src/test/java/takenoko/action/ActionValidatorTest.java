package takenoko.action;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import takenoko.game.GameInventory;
import takenoko.game.WeatherDice;
import takenoko.game.board.Board;
import takenoko.game.board.BoardException;
import takenoko.game.board.MovablePiece;
import takenoko.game.board.VisibleInventory;
import takenoko.game.objective.Objective;
import takenoko.game.tile.*;
import takenoko.player.PrivateInventory;
import takenoko.utils.Coord;

class ActionValidatorTest {
    private Board board;
    private GameInventory gameInventory;
    private ActionValidator validator;

    @BeforeEach
    void setUp() {
        board = new Board();
        PrivateInventory privateInventory = new PrivateInventory();
        VisibleInventory visibleInventory = new VisibleInventory();
        visibleInventory.incrementIrrigation();
        gameInventory =
                new GameInventory(
                        20,
                        new TileDeck(new Random(0)),
                        new Random(0),
                        new WeatherDice(new Random(0)));
        validator =
                new ActionValidator(
                        board,
                        gameInventory,
                        privateInventory,
                        visibleInventory,
                        WeatherDice.Face.SUN);
    }

    @Test
    void testNone() {
        assertTrue(validator.isValid(Action.NONE));
    }

    @ParameterizedTest
    @MethodSource("placeTileProvider")
    void testPlaceTile(Coord coord, BambooTile tile, boolean expectedResult) {
        var action = new Action.PlaceTile(coord, TileDeck.DEFAULT_DRAW_PREDICATE);
        assertEquals(expectedResult, validator.isValid(action));
    }

    private static Stream<Arguments> placeTileProvider() {
        return Stream.of(
                Arguments.of(new Coord(0, 1), new BambooTile(Color.GREEN), true),
                Arguments.of(new Coord(0, 0), new BambooTile(Color.GREEN), false),
                Arguments.of(new Coord(2, 2), new BambooTile(Color.GREEN), false));
    }

    @ParameterizedTest
    @MethodSource("placeIrrigationProvider")
    void testPlaceIrrigation(Coord coord, TileSide side, boolean expectedResult)
            throws IrrigationException, BoardException {
        board.placeTile(new Coord(0, 1), new BambooTile(Color.GREEN));
        var action = new Action.PlaceIrrigationStick(coord, side);
        assertEquals(expectedResult, validator.isValid(action));
    }

    private static Stream<Arguments> placeIrrigationProvider() {
        return Stream.of(
                Arguments.of(new Coord(0, 1), TileSide.UP_LEFT, true, true),
                Arguments.of(new Coord(0, 0), TileSide.UP_LEFT, false, false),
                Arguments.of(new Coord(2, 2), TileSide.UP_LEFT, false, false));
    }

    @Test
    void testPlaceIrrigationWhenNotEnough() throws IrrigationException, BoardException {
        board.placeTile(new Coord(0, 1), new BambooTile(Color.GREEN));
        var action = new Action.PlaceIrrigationStick(new Coord(0, 1), TileSide.UP_LEFT);
        validator =
                new ActionValidator(
                        board,
                        gameInventory,
                        new PrivateInventory(),
                        new VisibleInventory(),
                        WeatherDice.Face.SUN);
        assertFalse(validator.isValid(action));
    }

    @Test
    void testTakeIrrigation() {
        var action = new Action.TakeIrrigationStick();
        assertTrue(validator.isValid(action));
    }

    @Test
    void testTakeIrrigationWhenNotEnough() {
        var validator =
                new ActionValidator(
                        board,
                        new GameInventory(
                                0,
                                new TileDeck(new Random(0)),
                                new Random(0),
                                new WeatherDice(new Random(0))),
                        new PrivateInventory(),
                        new VisibleInventory(),
                        WeatherDice.Face.SUN);
        var action = new Action.TakeIrrigationStick();
        assertFalse(validator.isValid(action));
    }

    @ParameterizedTest
    @MethodSource("unveilObjectiveProvider")
    void testUnveilObjective(Objective obj, boolean expectedResult) {
        when(obj.isAchieved()).thenReturn(expectedResult);
        var action = new Action.UnveilObjective(obj);
        assertEquals(expectedResult, validator.isValid(action));
    }

    private static Stream<Arguments> unveilObjectiveProvider() {
        var objMock1 = mock(Objective.class);
        var objMock2 = mock(Objective.class);
        return Stream.of(Arguments.of(objMock1, true), Arguments.of(objMock2, false));
    }

    private static Stream<Arguments> moveGardenerProvider() {
        return Stream.of(
                Arguments.of(new Coord(0, 1), true),
                Arguments.of(new Coord(0, 0), false), // already there
                Arguments.of(new Coord(1, 1), false),
                Arguments.of(new Coord(99, 99), false));
    }

    @ParameterizedTest
    @MethodSource("moveGardenerProvider")
    void testMoveGardener(Coord coord, boolean expectedResult)
            throws IrrigationException, BoardException {
        board.placeTile(new Coord(0, 1), new BambooTile(Color.GREEN));
        board.placeTile(new Coord(1, 0), new BambooTile(Color.GREEN));
        board.placeTile(new Coord(1, 1), new BambooTile(Color.GREEN));
        var action = new Action.MovePiece(MovablePiece.GARDENER, coord);
        assertEquals(expectedResult, validator.isValid(action));
    }

    private static Stream<Arguments> movePandaProvider() {
        return Stream.of(
                Arguments.of(new Coord(0, 1), true),
                Arguments.of(new Coord(0, 0), false), // already there
                Arguments.of(new Coord(2, 2), false),
                Arguments.of(new Coord(99, 99), false));
    }

    @ParameterizedTest
    @MethodSource("movePandaProvider")
    void testMovePanda(Coord coord, boolean expectedResult)
            throws IrrigationException, BoardException {
        board.placeTile(new Coord(0, 1), new BambooTile(Color.GREEN));
        board.placeTile(new Coord(1, 0), new BambooTile(Color.GREEN));
        board.placeTile(new Coord(1, 1), new BambooTile(Color.GREEN));
        var action = new Action.MovePiece(MovablePiece.PANDA, coord);
        assertEquals(expectedResult, validator.isValid(action));
    }

    @Test
    void testTwiceAction() {
        var action = new Action.PlaceTile(new Coord(0, 1), TileDeck.DEFAULT_DRAW_PREDICATE);
        assertTrue(validator.isValid(action));
        validator =
                new ActionValidator(
                        board,
                        gameInventory,
                        new PrivateInventory(),
                        new VisibleInventory(),
                        WeatherDice.Face.SUN,
                        new ArrayList<>(List.of(action)));
        var action2 = new Action.PlaceTile(new Coord(1, 0), TileDeck.DEFAULT_DRAW_PREDICATE);
        assertFalse(validator.isValid(action2));
    }

    @Test
    void testTwiceActionWithWind() {
        var action = new Action.PlaceTile(new Coord(0, 1), TileDeck.DEFAULT_DRAW_PREDICATE);
        assertTrue(validator.isValid(action));
        validator =
                new ActionValidator(
                        board,
                        gameInventory,
                        new PrivateInventory(),
                        new VisibleInventory(),
                        WeatherDice.Face.WIND,
                        new ArrayList<>(List.of(action)));
        var action2 = new Action.PlaceTile(new Coord(1, 0), TileDeck.DEFAULT_DRAW_PREDICATE);
        assertTrue(validator.isValid(action2));
    }
}
