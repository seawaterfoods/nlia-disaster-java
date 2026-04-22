package tw.org.nlia.disaster.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class NdReportMainTest {

    @Test
    void deriveNdStatus_allX_returnsX() {
        NdReportMain main = NdReportMain.builder()
                .nd1("X").nd2("X").nd3("X").nd4("X").nd5("X").build();
        assertEquals("X", main.deriveNdStatus());
    }

    @Test
    void deriveNdStatus_anyY_returnsY() {
        NdReportMain main = NdReportMain.builder()
                .nd1("X").nd2("N").nd3("Y").nd4("X").nd5("X").build();
        assertEquals("Y", main.deriveNdStatus());
    }

    @Test
    void deriveNdStatus_anyN_noY_returnsN() {
        NdReportMain main = NdReportMain.builder()
                .nd1("X").nd2("N").nd3("X").nd4("X").nd5("X").build();
        assertEquals("N", main.deriveNdStatus());
    }

    @Test
    void deriveNdStatus_yTakesPriorityOverN() {
        // PHP check_nd(): Y has highest priority
        NdReportMain main = NdReportMain.builder()
                .nd1("N").nd2("N").nd3("Y").nd4("N").nd5("N").build();
        assertEquals("Y", main.deriveNdStatus());
    }

    @Test
    void deriveNdStatus_allY_returnsY() {
        NdReportMain main = NdReportMain.builder()
                .nd1("Y").nd2("Y").nd3("Y").nd4("Y").nd5("Y").build();
        assertEquals("Y", main.deriveNdStatus());
    }

    @Test
    void deriveNdStatus_allN_returnsN() {
        NdReportMain main = NdReportMain.builder()
                .nd1("N").nd2("N").nd3("N").nd4("N").nd5("N").build();
        assertEquals("N", main.deriveNdStatus());
    }

    @ParameterizedTest
    @CsvSource({
        "X, X, X, X, X, X",
        "Y, X, X, X, X, Y",
        "N, X, X, X, X, N",
        "X, Y, X, X, X, Y",
        "N, N, N, N, N, N",
        "Y, Y, Y, Y, Y, Y",
        "N, N, Y, N, N, Y",
    })
    void deriveNdStatus_parametrized(String nd1, String nd2, String nd3, String nd4, String nd5, String expected) {
        NdReportMain main = NdReportMain.builder()
                .nd1(nd1).nd2(nd2).nd3(nd3).nd4(nd4).nd5(nd5).build();
        assertEquals(expected, main.deriveNdStatus());
    }
}
