package com.loraneo.logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TestClass {
    private static final Logger log = Logger.getLogger(TestClass.class.getName());

    public static void main(final String[] args) throws SecurityException, IOException {

        final LogManager logManager = LogManager.getLogManager();
        try (final InputStream is = logManager.getClass()
                .getResourceAsStream("/logging.properties")) {
            logManager.readConfiguration(is);
        }

        final TestClass sut = new TestClass();
        sut.hello("Hello",
                "Kyle");

    }

    public String hello(final String greetings,
                        final String name)
            throws SecurityException, IOException {

        // entering will be logged as FINER
        log.entering(TestClass.class.getName(),
                "hello",
                new Object[] {greetings,
                              name });

        // lambdas
        log.finest(() -> "finest: " + LocalDateTime.now());
        log.finer(() -> "finer: " + LocalDateTime.now());
        log.fine(() -> "fine: " + LocalDateTime.now());
        log.info(() -> "info: " + LocalDateTime.now());
        log.warning(() -> "warning: " + LocalDateTime.now());
        log.severe(() -> "severe: " + LocalDateTime.now());

        // exception logging

        // throwing will be logged as FINER
        log.throwing(TestClass.class.getName(),
                "hello",
                new Exception("test"));

        // exception + message logging with lambda
        log.log(Level.FINEST,
                new Exception("test"),
                () -> String.format("arg=%s",
                        name));

        // exception + parameter logging with LogRecord
        final LogRecord record = new LogRecord(Level.FINEST,
                "arg={0}");
        record.setThrown(new Exception("test"));
        record.setLoggerName(log.getName()); // logger name will be null unless this
        record.setParameters(new Object[] {name });
        log.log(record);

        log.log(Level.SEVERE,
                "Test",
                new RuntimeException("TEst2"));
        final String rc = greetings + ", " + name;

        // exiting will be logged as FINER
        log.exiting(TestClass.class.getName(),
                "hello",
                rc);
        return rc;
    }
}
