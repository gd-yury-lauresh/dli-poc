package com.github.yarnd.dli.provider.filestub;

import com.github.yarnd.dli.core.LogItem;
import com.github.yarnd.dli.provider.LogItemProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Emulates event arrival from the source
 */
public class StubFileProvider implements LogItemProvider{

    static Logger logger = LoggerFactory.getLogger(StubFileProvider.class);

    private static final String STUB_FILE = "stub.log";

    private static final int MIN_PACKAGE_SIZE = 0;

    private static final int MAX_PACKAGE_SIZE = 5;

    private static final int MIN_DELAY = 10;

    private static final int MAX_DELAY = 1000;

    public static final String LOG_ENTRY_DELIMITERS = "|";

    public static final int LOG_ENTRY_HEADER_FIELDS = 2;

    public static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("dd MM yyyy HH:mm:ss");;

    private Random random = new Random();

    private BufferedReader fileReader;

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING", justification = "Default used")
    public StubFileProvider() throws IOException {
        fileReader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(STUB_FILE)));
    }

    @Override
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "SWL_SLEEP_WITH_LOCK_HELD", justification = "Yes, we will sleep with lock, it's emulation")
    public synchronized Collection<LogItem> retrieveLogItems() {

        // if file was ended - emulate source end
        if (fileReader == null) {
            return null;
        }

        // Emulate delay
        try {
            int sleepTime = getRandom(MIN_DELAY, MAX_DELAY);
            logger.debug("Stub provider, sleep for {} ms", sleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            // do nothing
        }

        // Emulate package arrival
        int packageSize = getRandom(MIN_PACKAGE_SIZE, MAX_PACKAGE_SIZE);
        logger.debug("Stub provider, package size is {} items", packageSize);

        Collection<LogItem> logItems = new ArrayList<>(packageSize);

        // Read package from file
        for (int i = 0; i < packageSize; i++) {
            String logLine;
            try {
                logLine = fileReader.readLine();
            } catch (IOException e) {
                logger.error("Failed while reading stub file, emulating stream end", e);
                fileReader = null;
                break;
            }

            if (logLine == null) {
                logger.info("Stub file ended, emulating stream end");
                fileReader = null;
                break;
            }

            LogItem logItem = parseLogLine(logLine);
            if (logItem != null) {
                logItems.add(logItem);
            }
        }

        return logItems;
    }

    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "STCAL_STATIC_SIMPLE_DATE_FORMAT_INSTANCE", justification = "Synchronized")
    private synchronized LogItem parseLogLine(String logLine) {

        String[] logEntries = StringUtils.split(logLine,LOG_ENTRY_DELIMITERS,LOG_ENTRY_HEADER_FIELDS +1);

        if (logEntries.length != LOG_ENTRY_HEADER_FIELDS + 1) {
            logger.error("Log entry malformed, headers not found in '{}'", logLine);
            return null;
        }

        try {
            return new LogItem(
                    LOG_DATE_FORMAT.parse(logEntries[0]),
                    logEntries[1],
                    logEntries[2]
            );
        } catch (ParseException e) {
            logger.error("Date {} parse failed, skipping entry", logEntries[0]);
            return null;
        }
    }

    private int getRandom(int minValue, int maxValue) {
        return minValue+random.nextInt(maxValue-minValue);
    }
}
