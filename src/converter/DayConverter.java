package converter;
public enum DayConverter {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    private static final DayConverter[] e = DayConverter.values();

    public static String convertDay(int day) {
        return e[day-1].toString();
    }

    public static String capsFormat(String day) {
        return day.substring(0, 1) + day.substring(1).toLowerCase();
    }
}