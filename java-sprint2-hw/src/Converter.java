public class Converter {


    int convertToKm(int steps) {
        // длина 1 шага = 75 см
        return steps * 75 / 100000;
    }

    int convertStepsToKilocalories(int steps) {
        //  1 шаг = 50 кал; 1 ккал = 1000 кал.
        return steps * 50 / 1000;
    }
}
