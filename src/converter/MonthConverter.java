package converter;
public enum MonthConverter {
    JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER;

    private static final MonthConverter[] e = MonthConverter.values();

    public static String convertMonth(int month) {
        return e[month].toString();
    }

    public static String capsFormat(String month) {
        return month.substring(0, 1) + month.substring(1).toLowerCase();
    }
}
