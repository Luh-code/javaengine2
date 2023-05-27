package org.app.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.function.Function;
import java.util.function.Supplier;

public class Logger {
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_WHITE = "\u001B[37m";

	private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

	private static boolean logToFile = false;
	private static String filePath;
	private static PrintWriter writer;

	public static void activateLoggingToFile(String path) {
		try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		filePath = path + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
		int num = 0;
		Function<Integer, String> addEnding = (Integer n) ->
				String.format("%s - %d.log", filePath, n);
		File f;
		do
		{
			f = new File(addEnding.apply(num));
			try {
				if (f.createNewFile()) break;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			++num;
		} while (true);
		filePath = f.getPath();
		try {
			writer = new PrintWriter(new FileWriter(filePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		logToFile = true;
	}

	public static void deactivateLoggingToFile() {
		logToFile = false;
		writer.close();
	}
	
	private static void printLine(String text, String lvl, String col) {
		lvl += "]";
		String templateString = String.format("%s[%7s %s%s\n", "%s", lvl, text, "%s");
		System.out.printf(templateString, col, ANSI_RESET);

		if (!logToFile) return;
		writer.printf("%12s  "+templateString, new SimpleDateFormat("HH-mm-ss:SSS").format(new java.util.Date()), "", "");
		writer.flush();
	}

	public static void logDebug(String text) {
		printLine(text, "Debug", ANSI_CYAN);
	}

	public static void logInfo(String text) {
		printLine(text, "Info", ANSI_RESET);
	}

	public static void logWarn(String text) {
		printLine(text, "Warn", ANSI_YELLOW);
	}

	public static void logError(String text) {
		printLine(text, "Error", ANSI_RED);
	}

	public static void logCrit(String text) {
		printLine(text, "!Crit", ANSI_RED_BACKGROUND + ANSI_BLACK);
	}
}
