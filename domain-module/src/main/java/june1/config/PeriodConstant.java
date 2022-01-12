package june1.config;

public class PeriodConstant {

    public static String SECOND = "* * * * * ?";
    public static String SECOND_30 = "0/30 * * * * ?";
    public static String MINUTE = "0 * * * * ?";
    public static String MINUTE_3 = "0 0/3 * * * ?";
    public static String HOUR = "0 0 * * * ?";
    public static String DAY = "0 0 0 * * ?";
    public static String DAY_15 = "0 0 0 1,16 * ?";
    public static String MONTH = "0 0 0 1 * ?";
    public static String MONTH_3 = "0 0 0 1 1,4,7,10 ?";
    public static String MONTH_6 = "0 0 0 1 1,7 ?";
    public static String YEAR = "0 0 0 1 1 ?";
}
