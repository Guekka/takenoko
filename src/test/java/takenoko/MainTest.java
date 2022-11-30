package takenoko;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import takenoko.Main;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    String varToBeInitInSetup;

    @BeforeEach
    void setUp() {
        varToBeInitInSetup = "Hello World!";
    }

    @Test
    void helloTest() {
        assertEquals(varToBeInitInSetup, Main.hello());
    }
}