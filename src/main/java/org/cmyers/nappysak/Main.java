package org.cmyers.nappysak;

import java.io.File;
import java.io.IOException;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.common.collect.ImmutableSet;

public class Main {

	// Cribbed from http://args4j.kohsuke.org/sample.html
	public static class Options {
		private Boolean help;

		public Options() {
			// set defaults
			help = false;
		}
		public Boolean getHelp() {
			return help;
		}

		@Option(name="--help", usage="Print this help",help=true)
		public void setHelp(Boolean help) {
			this.help = help;
		}
	}
	
	public static void main(String args[]) throws IOException {
		Options options = new Options();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			parser.printUsage(System.err);
		}
		if (options.getHelp()) {
			parser.printUsage(System.out);
			return;
		}
	}
}
