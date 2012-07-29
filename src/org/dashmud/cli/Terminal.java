package org.dashmud.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Terminal {
	public static enum Color {
		BLACK        (0, 30),
		BLUE         (0, 34),
		GREEN        (0, 32),
		CYAN         (0, 36),
		RED          (0, 31),
		PURPLE       (0, 35),
		BROWN        (0, 33),
		GRAY         (0, 37),
		DARK_GRAY    (1, 30),
		LIGHT_BLUE   (1, 34),
		LIGHT_GREEN  (1, 32),
		LIGHT_CYAN   (1, 36),
		LIGHT_RED    (1, 31),
		LIGHT_PURPLE (1, 35),
		YELLOW       (1, 33),
		WHITE        (1, 37);
		
		private int attr;
		private int color;

		private Color(
			final int attr, 
			final int color
		) {
			this.attr = attr;
			this.color = color;
		}
		
		@Override
		public String toString() {
			return "\033[" + attr + ";" + color + "m";
		}
	}
	
	private final InputStream in;
	private final PrintStream out;
	
	private final LinkedList<Color> contextStack;
	
	private StringBuilder b;
	private int cursor;
	
	private final LinkedList<String> history;
	private ListIterator<String> historyIterator;
	
	public Terminal(
		final InputStream in,
		final PrintStream out
	) {
		this.in = in;
		this.out = out;
		this.contextStack = new LinkedList<Color>();
		this.history = new LinkedList<String>();
	}

	public void clearHistory() {
		history.clear();
	}
	
	public void print(final String s) {
		out.print(s);
	}

	public void println() {
		out.println();
	}

	public void println(final String s) {
		out.println(s);
	}

	public void println(final char c) {
		out.println(c);
	}

	public void print(final char c) {
		out.print(c);
	}
	
	public void push(final Color color) {
		out.print(color);
		contextStack.push(color);
	}
	
	public void pop() {
		contextStack.pop();
		
		if (contextStack.isEmpty()) {
			out.print(Color.GRAY);
		} else {
			out.print(contextStack.peek());
		}
	}

	public String prompt(
		final String promptString, 
		final boolean hideTyping
	) throws 
		IOException 
	{
		GetStringCommand cmd =
			(GetStringCommand) prompt(promptString, GetStringCommand.BUILDER(), hideTyping);
		
		return cmd.value();
	}
	
	public Command prompt(
		final String promptString, 
		final CommandBuilder commandBuilder
	) throws 
		IOException 
	{
		return prompt(promptString, commandBuilder, false);
	}
	
	public Command prompt(
		final String promptString, 
		final CommandBuilder commandBuilder,
		final boolean hideTyping
	) throws 
		IOException 
	{
		print(promptString);

		byte c = 0;
		
		b = new StringBuilder();
		cursor = 0;
		historyIterator = history.listIterator();
		
		inputLoop : while (true) {
			c = (byte) in.read();
			
			switch (c) {
				// FUNKY CODES
				case (byte) 0xff:
					c = (byte) in.read();
					c = (byte) in.read();
					break;
					
				// ESCAPE
				case 0x1b:
					handleEscape(promptString, hideTyping);
					break;

					
				// CARRIAGE RETURN
				case '\r':
					break;
					
					
				// ENTER
				case '\n': 
					println();
					history.push(b.toString());
					
					while (history.size() > 500) {
						history.pop();
					}
					
					Command cmd = 
						commandBuilder.parse(b.toString());
					
					if (cmd == null) {
						println("Huh?");
						println();
					} else {
						cmd.run();
						return cmd;
					}
					
					break inputLoop;
					
					
				// BACKSPACE and DELETE
				case 0x08: 
				case 0x7f: 
					if (b.length() > 0) {
						if (cursor == b.length()) {
							print((char) c);
							cursor = Math.max(0, cursor - 1);
							b.deleteCharAt(b.length() - 1);
							
						} else {
							cursor = Math.max(0, cursor - 1);
							b.deleteCharAt(cursor);
							
							refreshCurrentPrompt(promptString, hideTyping);
						}
					}
					break;

					
				// TAB
				case '\t':
					if (!hideTyping) {
						List<String> completions = 
							commandBuilder.getCompletions(b.toString());
	
						if (completions.size() == 0) {
							// do nothing
							
						} else if (completions.size() == 1) {
							b = new StringBuilder(completions.get(0) + " ");
							cursor = b.length();
							refreshCurrentPrompt(promptString, hideTyping);
							
						} else {
							println();
							printCompletions(completions);
							println();
							
							print(promptString);
							print(b.toString());
						}
					}
					break;
					
					
				// input characters
				default:
					if (cursor == b.length()) {
						if (hideTyping) {
							print('*');
						} else {
							print((char) c);
						}
						
						b.append((char) c);
						cursor++;
						
					} else {
						b.insert(cursor, (char) c);
						cursor++;
						
						refreshCurrentPrompt(promptString, hideTyping);
					}
					
					break;
			}
		}
		
		return null;
	}

	private void printCompletions(
		final List<String> completions
	) {
		for (String item : completions) {
			println("  " + item);
		}
	}

	protected void refreshCurrentPrompt(
		final String promptString, 
		final boolean hideTyping
	) {
		print('\r'); // carriage return only, maybe should be HOME?
		print(promptString);
		
		if (hideTyping) {
			print(b.toString().replaceAll(".", "*"));
		} else {
			print(b.toString());
		}
		
		print(" ");
		clearRestOfLine();
		cursorLeft(b.length() - cursor + 1);
	}
	
	public void clearRestOfLine() {
		print("\033[K");
	}

	public void cursorLeft() {
		print("\033[D");
	}
	
	public void cursorRight() {
		print("\033[C");
	}
	
	public void cursorDown() {
		print("\033[B");
	}
	
	public void cursorUp() {
		print("\033[A");
	}

	public void cursorLeft(int n) {
		print("\033[" + n + "D");
	}
	
	public void cursorRight(int n) {
		print("\033[" + n + "C");
	}
	
	public void cursorDown(int n) {
		print("\033[" + n + "B");
	}
	
	public void cursorUp(int n) {
		print("\033[" + n + "A");
	}
	
	private void handleEscape(
		final String promptString, // this argument needs to be factored out somehow
		final boolean hideTyping
	) throws 
		IOException 
	{
		byte c = (byte) in.read();
		
		// CSI
		if (c == '[') {
			c = (byte) in.read();
			switch (c) {
				case 'A': // TODO: History UP
					if (historyIterator.hasNext()) {
						b = new StringBuilder(historyIterator.next());
						cursor = b.length();
						refreshCurrentPrompt(promptString, hideTyping);
					}
					break;
					
				case 'B': // TODO: History DOWN 
					if (historyIterator.hasPrevious()) {
						b = new StringBuilder(historyIterator.previous());
						cursor = b.length();
						refreshCurrentPrompt(promptString, hideTyping);
					}
					break;
				
				case 'C': 
					if (cursor < b.length()) {
						cursor++;
						cursorRight();
					}
					break;
					
				case 'D': 
					if (cursor > 0) {
						cursor--;
						cursorLeft();
					}
					break;
			}
		} else {
			return;
		}
	}
}
