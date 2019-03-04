import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;

abstract class MyLogger {
	abstract void log(String mark, String message);
}


class FileLogger extends MyLogger {
	private String fileFolder;
	private Logger fileLogger;

	public FileLogger(String folder, Logger, logger) {
		fileFolder = folder;
		fileLogger = logger;
	}

	public void log(String mark, String message) {
		String fileName = fileFolder + "/logFile.txt"
		File logFile = new File(fileName);
		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		FileHandler fh = new FileHandler(fileName);
		fileLogger.addHandler(fh);
		fileLogger.log(Level.INFO, mark + " " + message);
	}
}

class DatabaseLogger extends MyLogger {

	private Map dbParams;

	public DatabaseLogger(Map dbParamsMap) {
		dbParams = dbParamsMap;
	}
	
	public void log(String mark, String message) {
		Connection connection = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", dbParams.get("userName"));
		connectionProps.put("password", dbParams.get("password"));
		connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
				+ ":" + dbParams.get("portNumber") + "/", connectionProps);
		Statement stmt = connection.createStatement();
		int t = 0;
		if (mark.equals("message")) {
			t = 1;
		}

		else if (mark.equals("error")) {
			t = 2;
		}

		else if (mark.equals("warning")) {
			t = 3;
		}
		stmt.executeUpdate("insert into Log_Values('" + mark + " " + message + "', " + String.valueOf(t) + ")");
	}
}

class ConsoleLogger extends MyLogger {
	private Logger consoleLogger;

	public ConsoleLogger(Logger logger) {
		consoleLogger = logger;
	}

	public void log(String mark, String message) {
		ConsoleHandler ch = new ConsoleHandler();
		consoleLogger.addHandler(ch);
		consoleLogger.log(Level.INFO, mark + " " + message);
	}
}

public class JobLogger {
	private static boolean logMessage;
	private static boolean logWarning;
	private static boolean logError;
	private boolean initialized;
	private static Logger logger;
	private static LinkedList<MyLogger> jobLoggers;

	public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
			boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
		logger = Logger.getLogger("MyLog");  
		logError = logErrorParam;
		logMessage = logMessageParam;
		logWarning = logWarningParam;
		jobLoggers = new LinkedList<MyLogger>();
		if (logToFileParam) {
			jobLoggers.add(new FileLogger(dbParamsMap.get("logFileFolder") + "/logFile.txt", logger));
		}
		if (logToConsoleParam) {
			jobLoggers.add(new ConsoleLogger(logger));
		}
		if (logToDatabaseParam) {
			jobLoggers.add(new DatabaseLogger(dbParamsMap))
		}

	}

	private String generateMessage(String messageText) {
		return DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
	}

	public static void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
		messageText.trim();
		if (messageText == null || messageText.length() == 0) {
			return;
		}
		if (jobLoggers.size() == 0) {
			throw new Exception("Invalid configuration");
		}
		if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
			throw new Exception("Error or Warning or Message must be specified");
		}

		String logText = generateMessage(messageText);

		for(MyLogger currentLogger: jobLoggers) {
			if (error && logError) {
				currentLogger.log("error", logText);
			}
			if (warning && logWarning) {
				currentLogger.log("warning", logText);
			}
			if (message && logMessage) {
				currentLogger.log("message", logText);
			}
		}	
	}
}