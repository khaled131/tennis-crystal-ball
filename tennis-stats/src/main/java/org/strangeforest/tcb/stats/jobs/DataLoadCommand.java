package org.strangeforest.tcb.stats.jobs;

import java.io.*;
import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.*;

import org.slf4j.*;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

public abstract class DataLoadCommand {

	private static final String DATA_LOAD_COMMAND = "../data-load/bin/data-load";

	private static final Logger LOGGER = LoggerFactory.getLogger(DataLoadCommand.class);

	public static int dataLoad(String name, String... params) {
		try {
			List<String> command = new ArrayList<>();
			command.add(DATA_LOAD_COMMAND);
			command.addAll(asList(params));
			LOGGER.info("Executing {} [{}]", name, command.stream().collect(joining(" ")));
			Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				StringBuilder sb = new StringBuilder(200);
				sb.append(name).append(" output:");
				while (!process.waitFor(1L, TimeUnit.SECONDS))
					readOutput(reader, sb);
				int exitCode = process.waitFor();
				readOutput(reader, sb);
				LOGGER.info(sb.toString());
				if (exitCode == 0)
					LOGGER.info("{} finished.", name);
				else
					LOGGER.error("{} exited with code {}.", name, exitCode);
				return exitCode;
			}
		}
		catch (Exception ex) {
			LOGGER.error("Error executing {}.", name, ex);
			return -1;
		}
	}

	private static void readOutput(BufferedReader reader, StringBuilder sb) throws IOException {
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			sb.append('\n').append(line);
		}
	}
}
