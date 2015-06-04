package parsop;

import java.util.NoSuchElementException;
import java.util.Scanner;

import parsop.parser.ParseException;
import parsop.parser.Parser;

public class Main {

	public static void main(String[] args) throws ParseException {
		Parser parser = Parser.fromCommandLineArguments(args);
		Scanner stdin = new Scanner(System.in);
		System.out.println("Enter an expression to parse:");
		String line;
		try {
			while (!(line = stdin.nextLine()).equals(""))
				try{
					System.out.println(parser.parse(line));
				} catch (ParseException e) {/* Nothing */ }
		} catch (NoSuchElementException e) { }

		System.out.println("Goodbye!");
		stdin.close();
	}
}
