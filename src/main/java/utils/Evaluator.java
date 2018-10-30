package utils;

import enviroment.Stand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Evaluator {
    private static final Logger LOG = LoggerFactory.getLogger(Evaluator.class);
    private static final Map<Object, Object> STASH = new HashMap<>();
    private static final String VAR_PATTERN = "\\#\\{(?<var>[^{^}]*)\\}";
    private static final String NOW_PATTERN = "\\#\\{\\s*now\\s*\\(:?(?<format>.*?)\\)\\s*((?<sign>[\\+\\-])\\s*(?<amount>\\d+)\\s*(?<points>[ydMmsS]))?\\s*\\}";
    private static final String DATE_PATTERN = "\\#\\{\\s*date\\s*\\((.*)\\)\\s*(([\\+\\-])\\s*(\\d+)\\s*([ydMmsS]))*\\}";
    private static final String RANDOM_NUMBER_PATTERN = "\\#\\{\\s*random\\s*\\{(?<random>.*?)\\}\\}";
    private static final Pattern VAR_PATTERN_COMPILED = Pattern.compile(VAR_PATTERN);
    private static final Pattern NOW_PATTERN_COMPILED = Pattern.compile(NOW_PATTERN);
    private static final Pattern DATE_PATTERN_COMPILED = Pattern.compile(DATE_PATTERN);
    private static final Pattern RANDOM_NUMBER_PATTERN_COMPILED = Pattern.compile(RANDOM_NUMBER_PATTERN);
    private static DateFormatSymbols monthNominativeFormatSymbols = new DateFormatSymbols() {

        @Override
        public String[] getMonths() {
            return new String[]{"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        }

    };
    private static DateFormatSymbols monthGenitiveFormatSymbols = new DateFormatSymbols() {

        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }

    };

    private Evaluator() {

    }

    public static void setVariable(String name, Object value) {
        STASH.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getVariable(String name) {
        return (T) Optional.ofNullable(evalVariable(name)).get();
    }

    public static Map<String, String> getVariables(Map<String, String> map) {
        HashMap<String, String> resultMap = new HashMap<>(map);
        resultMap.forEach((key, value) -> {
            resultMap.compute(key, (key1, oldValue) -> getVariable(oldValue));
        });
        return resultMap;
    }

    public static void clearVariables() {
        STASH.clear();
    }

    public static boolean removeVariable(String name) {
        return STASH.entrySet().removeIf(entry -> entry.getKey().equals(name));
    }

    private static <T> T evalVariable(String param) {
        if (param.trim().matches(".*" + VAR_PATTERN + ".*")) {
            Matcher varMatcher = VAR_PATTERN_COMPILED.matcher(param);
            StringBuffer varSB = new StringBuffer();
            while (varMatcher.find()) {
                String name = varMatcher.group("var");
                Object var = STASH.get(name);
                if (var == null) {
                    var = System.getProperty(name);
                    if (var != null) STASH.put(name, var);
                }
                String value = String.valueOf(var);
                varMatcher.appendReplacement(varSB, Matcher.quoteReplacement(value));
            }
            varMatcher.appendTail(varSB);
            return evalVariable(varSB.toString());
        } else if (Stand.getCurrentStand().getAccounts().containsKey(param)) {
            return evalVariable(Stand.getCurrentStand().getAccounts().getString(param));
        } else if (param.trim().matches(".*" + NOW_PATTERN + ".*")) {
            Matcher nowMatcher = NOW_PATTERN_COMPILED.matcher(param);
            StringBuffer nowSB = new StringBuffer();
            while (nowMatcher.find()) {
                String value = String.valueOf(evalNow(nowMatcher.group(0)));
                nowMatcher.appendReplacement(nowSB, value);
            }
            nowMatcher.appendTail(nowSB);
            return evalVariable(nowSB.toString());
        } else if (param.trim().matches(".*" + DATE_PATTERN + ".*")) {
            Matcher dateMatcher = DATE_PATTERN_COMPILED.matcher(param);
            StringBuffer dateSB = new StringBuffer();
            while (dateMatcher.find()) {
                String value = "";
                try {
                    value = String.valueOf(evalDate(dateMatcher.group(0)));
                    dateMatcher.appendReplacement(dateSB, value);
                } catch (ParseException error) {
                    throw new IllegalStateException(error);
                }
            }
            dateMatcher.appendTail(dateSB);
            return evalVariable(dateSB.toString());
        } else if (param.trim().matches(".*" + RANDOM_NUMBER_PATTERN + ".*")) {
            Matcher randomMatcher = RANDOM_NUMBER_PATTERN_COMPILED.matcher(param);
            StringBuffer randomSB = new StringBuffer();
            String value = "";
            while (randomMatcher.find()) {
                value = String.valueOf(evalRandomNumber(randomMatcher.group(1)));
            }
            randomMatcher.appendTail(randomSB);
            return evalVariable(value);
        }
        return (T) param;
    }


    private static String evalNow(String command) {
        Matcher match = NOW_PATTERN_COMPILED.matcher(command);
        if (!match.find())
            return null;

        Calendar cl = Calendar.getInstance();

        String format = match.group("format");
        if (format.isEmpty())
            format = "dd.MM.yyyy";

        String sign = match.group("sign");
        String amount = match.group("amount");
        String points = match.group("points");

        if (sign != null) {
            int fld = -1;
            switch (points) {
                case "y":
                    fld = Calendar.YEAR;
                    break;
                case "M":
                    fld = Calendar.MONTH;
                    break;
                case "d":
                    fld = Calendar.DATE;
                    break;
                case "h":
                    fld = Calendar.HOUR;
                    break;
                case "H":
                    fld = Calendar.HOUR_OF_DAY;
                    break;
                case "m":
                    fld = Calendar.MINUTE;
                    break;
                case "s":
                    fld = Calendar.SECOND;
                    break;
                case "S":
                    fld = Calendar.MILLISECOND;
                    break;
            }
            cl.add(fld, (sign.equals("-") ? -1 : 1) * Integer.valueOf(amount));
        }
        DateFormat formatter;
        if (format.contains("llll"))
            formatter = new SimpleDateFormat(format.replace("llll", "MMMM"), monthGenitiveFormatSymbols);
        else
            formatter = new SimpleDateFormat(format, monthNominativeFormatSymbols);

        return formatter.format(cl.getTime());
    }

    private static String evalDate(String command) throws ParseException {
        Matcher match = DATE_PATTERN_COMPILED.matcher(command);
        if (!match.find())
            return null;

        Calendar cl = Calendar.getInstance();

        String args = match.group(1);
        if (args.isEmpty())
            return command;

        String[] parts = args.split("\\s*\\,\\s*");

        String format = parts.length >= 2 ? parts[1] : "dd.MM.yyyy";
        String value = evalVariable(parts[0]);
        value = value.trim().replaceAll("^\\\"", "").replaceAll("\\\"$", "");

        DateFormat formatter = new SimpleDateFormat(format);
        cl.setTime(formatter.parse(value));

        String sign = match.group(3);
        String amount = match.group(4);
        String points = match.group(5);

        if (sign != null) {
            int fld = -1;
            switch (points) {
                case "y":
                    fld = Calendar.YEAR;
                    break;
                case "M":
                    fld = Calendar.MONTH;
                    break;
                case "d":
                    fld = Calendar.DATE;
                    break;
                case "h":
                    fld = Calendar.HOUR;
                    break;
                case "m":
                    fld = Calendar.MINUTE;
                    break;
                case "s":
                    fld = Calendar.SECOND;
                    break;
                case "S":
                    fld = Calendar.MILLISECOND;
                    break;
                default:
                    throw new InvalidParameterException("Не найденг маппинг для символа " + points);
            }
            cl.add(fld, ("-".equals(sign) ? -1 : 1) * Integer.valueOf(amount));
        }

        return formatter.format(cl.getTime());
    }

    private static String evalRandomNumber(String command) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String[] values = command.split(",");
        int limit;
        String prefix = "";
        if (values.length > 1) {
            prefix = values[0];
            limit = Integer.valueOf(values[1]);
        } else {
            limit = Integer.valueOf(values[0]);
        }
        long min = 1111111111111111111L;
        long range = Long.MAX_VALUE - min + 1;
        StringBuilder result = new StringBuilder(prefix);
        while (result.length() < limit) {
            result.append(random.nextLong(range) + min);
        }
        return result.substring(0, limit);
    }
}
