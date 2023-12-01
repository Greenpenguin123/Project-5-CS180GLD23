import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.*;

public class logUtility implements ILog {
    private static final Lock lock = new ReentrantLock();

    private final Logger logger;

    public logUtility(String logFileName) {
        this.logger = Logger.getLogger(logFileName);
        init(logFileName);
    }

    private void init(String logFileName) {
        try {
            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String user, String action, String message) {
        lock.lock();
        try {
            LogRecord record = new LogRecord(Level.INFO, String.format("User: %s, Action: %s, Message: %s", user, action, message));
            logger.log(record);
        } finally {
            lock.unlock();
        }
    }    

    private static class CustomFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return record.getMessage() + System.lineSeparator();
        }
    }
}

