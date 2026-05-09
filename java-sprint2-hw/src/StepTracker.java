import java.util.Scanner;

class StepTracker {

    MonthData[] monthToData = new MonthData[12];
    Scanner scanner;
    int goalByStepsPerDay = 10000;
    Converter converter = new Converter();
    MonthData monthData = new MonthData();

    StepTracker(Scanner scan) {
        scanner = scan;

        for (int i = 0; i < monthToData.length; i++) {
            monthToData[i] = new MonthData();
        }
    }

    void addNewNumberStepsPerDay() {
        System.out.println("Введите номер месяца"); // ввод и проверка номера месяца
        int month = scanner.nextInt();
        if (month < 1 || month > 12) {
            System.out.println("Номер месяца должен быть от 1 до 12, а вы ввели: " + month);
            return;
        }

        System.out.println("Введите день от 1 до 30 (включительно)"); // ввод и проверка дня
        int day = scanner.nextInt();
        if (day < 1 || day > 30) {
            System.out.println("Количества дней должно быть от 1 до 30, а вы ввели: " + day);
            return;
        }

        System.out.println("Введите количество шагов"); // ввод и проверка количества шагов
        int steps = scanner.nextInt();
        if (steps <= 0) {
            System.out.println("Количество шагов должно быть положительным числом, а вы ввели: " + steps);
            return;
        }

        MonthData monthData = monthToData[month - 1];  // получение соответствующего объекта MonthData из массива
        monthData.days[day - 1] = steps; // сохраняем количество шагов в соответствующий день определенного месяца
    }

    void changeStepGoal() {
        System.out.println("Введите новую цель по шагам: ");
        int goal = scanner.nextInt();

        if (goal <= 0) {
            System.out.println("Количество шагов за день не должно быть меньше 0");
        } else {
            goalByStepsPerDay = goal;
        }
    }

    double averageNumberOfSteps() {
        double averSteps = monthData.sumStepsFromMonth() / 30;
        return averSteps;
    }

    void printStatistic() {
        System.out.println("Введите номер месяца: 1 - Январь, 2 - Февраль, 3 - Март, 4 - Апрель, 5 - Май," +
                "6 - Июнь, 7 - Июль, 8 - Август, 9 - Сентябрь, 10 - Октябрь, 11 - Ноябрь, 12 - Декабрь.");
        int numberMonth = scanner.nextInt();

        if (numberMonth < 1 || numberMonth > 12) {
            System.out.println("Номер месяца должен быть от 1 до 12, а Вы ввели: " + numberMonth);
            return;
        } else {
            monthData = monthToData[numberMonth - 1];
        }

        monthData.printDaysAndStepsFromMonth();  //Количество пройденных шагов по дням
        System.out.println("Общее количество шагов в месяц: " + monthData.sumStepsFromMonth());
        System.out.println("Максимальное пройденное количество шагов за месяц: " + monthData.maxSteps());
        System.out.println("Среднее количество шагов в месяц: " + averageNumberOfSteps());
        System.out.println("Пройденная дистанция (в километрах): "
                + converter.convertToKm(monthData.sumStepsFromMonth()));
        System.out.println("Количество сожжённых килокалорий: "
                + converter.convertStepsToKilocalories(monthData.sumStepsFromMonth()));
        System.out.println("Лучшая серия: " + monthData.bestSeries(goalByStepsPerDay));
        System.out.println();
    }
}

