package com.loraneo.logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SimpleTest {

    private Logger log;
    private ByteArrayOutputStream loggerContent;
    private StreamHandler streamHandler;

    @Before
    public void prepare() throws SecurityException, IOException {
        log = Logger.getLogger(SimpleTest.class.getName());
        Stream.of(log.getHandlers())
                .forEach(log::removeHandler);
        loggerContent = new ByteArrayOutputStream();
        final PrintStream prStr = new PrintStream(loggerContent);
        streamHandler = new StreamHandler(prStr,
                new JSONLogFormatter());

        log.addHandler(streamHandler);
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        final String greetings = "g1";
        final String name = "t2";

        log.entering(SimpleTest.class.getName(),
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
        log.throwing(SimpleTest.class.getName(),
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
        log.exiting(SimpleTest.class.getName(),
                "hello",
                rc);

        streamHandler.flush();
        loggerContent.flush();

        loggerContent.writeTo(System.out);
        Assert.assertEquals(neutralize(readTestData()),
                neutralize(loggerContent.toString()));

    }

    private String readTestData() throws IOException, URISyntaxException {
        return new String(Files.readAllBytes(Paths.get(getClass().getResource("/simple_test.out")
                .toURI())));
    }

    private String neutralize(final String from) {
        return from.replaceAll(
                "([0-9]+)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])[Tt]([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]|60)(\\.[0-9]+)?(([Zz])|([\\+|\\-]([01][0-9]|2[0-3]):?[0-5][0-9]))?",
                "xxxxx");
    }

}
