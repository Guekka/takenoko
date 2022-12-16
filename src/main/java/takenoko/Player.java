package takenoko;

public interface Player {
    void beginTurn(int actionCredits);

    int availableActionCredits();

    Action chooseAction(Board board);

    boolean wantsToEndTurn();

    int getInventory();

    void takeIrrigationStick();

    void placeIrrigationStick();
}
