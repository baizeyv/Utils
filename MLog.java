package com.aurora.melody;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MLog {

    /* whether to save log to file */
    private static boolean LOG_FILE_SWITCHER = false;

    private static int MAX_WIDTH = 140;

    private final static int FORMAT_WIDTH = 37;

    private static LogLevel logLevel = LogLevel.INFO;

    private static FileHandler fh;

    static {
        try {
            if (LOG_FILE_SWITCHER)
                fh = new FileHandler("MelodyLog.log");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过\033特殊转义字符实现输出格式控制
     * @param content, 待格式化的内容
     * @param fontColor,       字体颜色：30黑 31红 32绿 33黄 34蓝 35紫 36深绿 37白
     * @param fontType,        字体格式：0重置 1加粗 2减弱 3斜体 4下划线 5慢速闪烁 6快速闪烁
     * @param backgroundColor, 字背景颜色：40黑 41红 42绿 43黄 44蓝 45紫 46深绿 47白
     * @return .
     */
    private static String FormatOutputString(String content, int fontColor, int fontType, int backgroundColor) {
        return String.format("\033[%d;%d;%dm%s\033[0m", fontColor, fontType, backgroundColor, content);
    }

    private static String FormatOutputWidth(int width, String content) {
        return String.format("%-" + width + "s", content);
    }

    private static String GetPrefix(LogLevel level) {
        int fontColor = 30;
        if (level == LogLevel.INFO) {
            fontColor = 37;
        } else if (level == LogLevel.DEBUG) {
            fontColor = 34;
        } else if (level == LogLevel.SUCCESS) {
            fontColor = 32;
        } else if (level == LogLevel.WARNING) {
            fontColor = 33;
        } else if (level == LogLevel.ERROR) {
            fontColor = 31;
        }
        return FormatOutputWidth(25, FormatOutputString("[" + level + "]", fontColor, 3, 99)) + " " + GetDateTime() + " -> ";
    }

    private static String GetDateTime() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        return FormatOutputString("<" + currentDate.getYear() + "-" + handleNumber(currentDate.getMonthValue()) + "-" + handleNumber(currentDate.getDayOfMonth()) + " " +
                handleNumber(currentTime.getHour()) + ":" + handleNumber(currentTime.getMinute()) + ":" + handleNumber(currentTime.getSecond()) + ">", 35, 4, 99);
    }

    private static String handleNumber(int value) {
        if (value < 10)
            return "0" + value;
        else
            return "" + value;
    }

    private static String swapLine(String content) {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String word : content.split("\\s+")) {
            if (count + word.length() > MAX_WIDTH) {
                sb.append("\n");
                for (int i = 0; i < FORMAT_WIDTH; i ++) {
                    sb.append(" ");
                }
                count = 0;
            }
            sb.append(word).append(" ");
            count += word.length() + 1;
        }
        return sb.toString();
    }

    public static void info(String content) {
        saveLog(LogLevel.INFO, content);
        content = swapLine(content);
        System.out.println(
                GetPrefix(LogLevel.INFO) +
                FormatOutputString(content, 37, 2, 99)
        );
    }

    public static void debug(String content) {
        saveLog(LogLevel.DEBUG, content);
        content = swapLine(content);
        System.out.println(
                GetPrefix(LogLevel.DEBUG) +
                FormatOutputString(content, 34, 2, 99)
        );
    }

    public static void success(String content) {
        saveLog(LogLevel.SUCCESS, content);
        content = swapLine(content);
        System.out.println(
                GetPrefix(LogLevel.SUCCESS) +
                        FormatOutputString(content, 32, 2, 99)
        );
    }

    public static void warning(String content) {
        saveLog(LogLevel.WARNING, content);
        content = swapLine(content);
        System.out.println(
                GetPrefix(LogLevel.WARNING) +
                        FormatOutputString(content, 33, 2, 99)
        );
    }

    public static void error(String content) {
        saveLog(LogLevel.ERROR, content);
        content = swapLine(content);
        System.out.println(
                GetPrefix(LogLevel.ERROR) +
                        FormatOutputString(content, 31, 2, 99)
        );
    }

    public static void line() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n✂");
        for (int i = 0; i < MAX_WIDTH + FORMAT_WIDTH - 1; i ++) {
            sb.append("-");
        }
        sb.append("\n");
        String line = sb.toString();
        System.out.println(
                FormatOutputString(line, 36, 1, 99)
        );
    }

    public static void print(String content) {
        switch (logLevel) {
            case INFO:
                info(content);
                break;
            case DEBUG:
                debug(content);
                break;
            case WARNING:
                warning(content);
                break;
            case ERROR:
                error(content);
                break;
            case SUCCESS:
                success(content);
                break;
        }
    }

    public static void SetLogLevel(int level) {
        if (level < 0)
            level = 0;
        if (level > 4)
            level = 4;
        switch (level) {
            case 0:
                logLevel = LogLevel.INFO;
                break;
            case 1:
                logLevel = LogLevel.DEBUG;
                break;
            case 2:
                logLevel = LogLevel.WARNING;
                break;
            case 3:
                logLevel = LogLevel.ERROR;
                break;
            case 4:
                logLevel = LogLevel.SUCCESS;
                break;
        }
    }

    public static void saveLog(LogLevel level, String content) {
        if (!LOG_FILE_SWITCHER)
            return;
        Logger logger = Logger.getLogger(MLog.class.getName());
        try {
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            if (level == LogLevel.WARNING || level == LogLevel.ERROR)
                logger.warning(content);
             else
                logger.info(content);
        } catch (Exception e) {
            logger.warning("[WARNING]: " + e.getMessage());
        }
    }

    public static void SetSwitcher(boolean switcher) {
        LOG_FILE_SWITCHER = switcher;
    }

    public static void SetMaxWidth(int max) {
        if (max < 40)
            max = 140;
        MAX_WIDTH = max;
    }

    enum LogLevel {
        INFO, // 0
        DEBUG, // 1
        WARNING, // 2
        ERROR, // 3
        SUCCESS // 4
    }

}
