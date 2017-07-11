package com.loraneo.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.json.Json;

/**
 *
 * @author rpofuk
 */
public class JSONLogFormatter extends Formatter {

    private long recordNumber = 0;

    private static final String RFC3339_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(final LogRecord record) {
        return createJson(record);
    }

    private String createJson(final LogRecord record) {
        try {
            return new StringBuilder(Json.createObjectBuilder()
                    .add("_Timestamp",
                            new SimpleDateFormat(RFC3339_DATE_FORMAT).format(record.getMillis()))
                    .add("_Level",
                            String.valueOf(record.getLevel()))
                    .add("_LoggerName",
                            getLoggerName(record))
                    .add("_ThreadID",
                            String.valueOf(record.getThreadID()))
                    .add("_ThreadName",
                            Thread.currentThread()
                                    .getName())
                    .add("_LevelValue",
                            String.valueOf(record.getLevel()
                                    .intValue()))
                    .add("ClassName",
                            getLogClassName(record))
                    .add("MethodName",
                            getLogMethodName(record))
                    .add("RecordNumber",
                            getRecordNumber(record))
                    .add("_LogMessage",
                            readMessage(record))
                    .add("_LogException",
                            getException(record))
                    .build()

                    .toString()).append(LINE_SEPARATOR)
                            .toString();

        } catch (final Exception ex) {
            new ErrorManager().error("Error in formatting Logrecord",
                    ex,
                    ErrorManager.FORMAT_FAILURE);
            return "";
        }
    }

    private String readMessage(final LogRecord record) {
        final String message = formatMessage(record);
        if (message == null) {
            return "";
        }
        return message;
    }

    private String getException(final LogRecord record) {
        if (record.getThrown() != null) {
            return Json.createObjectBuilder()
                    .add("_Exception",
                            getExceptionMessage(record))
                    .add("_StackTrace",
                            getStackTrace(record.getThrown()))
                    .build()
                    .toString();

        }
        return "";
    }

    private String getExceptionMessage(final LogRecord record) {
        if (record.getThrown()
                .getMessage() == null) {
            return "";
        }
        return record.getThrown()
                .getMessage();
    }

    private String getStackTrace(final Throwable thrown) {
        try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
            thrown.printStackTrace(printWriter);
            return stringWriter.toString();
        } catch (final IOException ex) {
            new ErrorManager().error("Error printing stack strace",
                    ex,
                    ErrorManager.FORMAT_FAILURE);
        }
        return "";
    }

    private String getRecordNumber(final LogRecord record) {
        if (recordNumber()) {
            recordNumber++;
            return String.valueOf(recordNumber);
        }
        return "";
    }

    private String getLogClassName(final LogRecord record) {
        if (logSource() || record.getLevel()
                .intValue() <= Level.FINE.intValue()) {
            if (null != record.getSourceClassName() && !record.getSourceClassName()
                    .isEmpty()) {
                return record.getSourceClassName();
            }

        }
        return "";
    }

    private String getLogMethodName(final LogRecord record) {
        if (logSource() || record.getLevel()
                .intValue() <= Level.FINE.intValue()) {
            if (null != record.getSourceMethodName() && !record.getSourceMethodName()
                    .isEmpty()) {
                return record.getSourceMethodName();
            }
        }
        return "";

    }

    private String getLoggerName(final LogRecord record) {
        return record.getLoggerName() != null ? record.getLoggerName()
                                              : "";
    }

    private boolean logSource() {
        return "true".equals(System.getProperty("com.sun.aas.logging.keyvalue.logsource"));
    }

    private boolean recordNumber() {
        return "true".equals(System.getProperty("com.sun.aas.logging.keyvalue.recordnumber"));
    }

}
