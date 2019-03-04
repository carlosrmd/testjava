import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

public class Tests {

	private Map dbParams = ...
	private String mark = "test";
	private String message = "this is a test message";

	//Return true if the specified arguments are stored in the Log_Value table in the database, false if not
	private Boolean checkLogValuesDatabase(String message, String t) { ... }

	//Retuns true if the given message is written somewhere in the given file, false if not
	private Boolean checkLogInFile(String fileName, String message) { ... }

	//Returns true if the given message was printed out in the console, false if not
	private Boolean checkLogInConsole(String message) { ... }


	//tests
	@Test(expected = Exception.class)
	public void testInvalidConfiguration() {
		JobLogger jb = new JobLogger(false, false, false, false, false, false, new Map());
		jb.LogMessage("42",true,true,true);
	}


	@Test(expected = Exception.class)
	public void testNoMarkSpecification() {
		JobLogger jb = new JobLogger(true, false, false, false, false, false, new Map());
		jb.LogMessage("42",false,false,false);
	}

	@Test
	public void testFileLoggerClass() {
		FileLogger fl = new FileLogger("./test", Logger.getLogger("MockLog"));
		fl.log(mark, message);
		assertTrue(checkLogInFile("./test/logFile.txt",mark + " " + message))
	}

	@Test
	public void testConsoleLoggerClass() {
		ConsoleLogger cl = new ConsoleLogger(Logger.getLogger("MockLog"));
		cl.log(mark, message);
		assertTrue(checkLogInConsole(mark + " " + message))
	}

	@Test
	public void testDatabaseLoggerClass() {
		DatabaseLogger cl = new DatabaseLogger(Logger.getLogger("MockLog"));
		cl.log(mark, message);
		assertTrue(checkLogInConsole(mark + " " + message))
	}

	@Test
	public void testJobLoggerAllLoggers() {
		JobLogger jb = new JobLogger(true, true, true, true, false, false, dbParams);
		jb.LogMessage("42",true,false,false);
		Assert.assertTrue(checkLogValuesDatabase("42", ""));
		Assert.assertTrue(checkLogInFile("./test/logFile", "42"));
		Assert.assertTrue(checkLogInConsole("42"));
	}

	@Test
	public void testJobLoggerDatabaseOnly() {
		JobLogger jb = new JobLogger(false, false, true, true, false, false, dbParams);
		jb.LogMessage("to database",true,false,false);
		Assert.assertTrue(checkLogValuesDatabase("to database", ""));
		Assert.assertFalse(checkLogInFile("./test/logFile", "to database"));
		Assert.assertFalse(checkLogInConsole("to database"));
	}

	@Test
	public void testJobLoggerFile() {
		JobLogger jb = new JobLogger(true, false, false, true, false, false, dbParams);
		jb.LogMessage("to file",true,false,false);
		Assert.assertFalse(checkLogValuesDatabase("to file", ""));
		Assert.assertTrue(checkLogInFile("./test/logFile", "to file"));
		Assert.assertFalse(checkLogInConsole("to file"));
	}

	@Test
	public void testJobLoggerConsole() {
		JobLogger jb = new JobLogger(false, true, false, true, false, false, dbParams);
		jb.LogMessage("to console",true,false,false);
		Assert.assertFalse(checkLogValuesDatabase("to console", ""));
		Assert.assertFalse(checkLogInFile("./test/logFile", "to console"));
		Assert.assertTrue(checkLogInConsole("to console"));
	}


}