package org.dashmud.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * xterm reference: http://invisible-island.net/xterm/ctlseqs/ctlseqs.html
 */
public class Shell {
	final Terminal terminal;
	
	private final LinkedList<Terminal.Color> contextStack;
	
	private StringBuilder b;
	private int promptCursor;
	
	private final Terminal.Cursor cursor;
	private Terminal.Cursor termSize;
	
	private final LinkedList<String> history;
	private ListIterator<String> historyIterator;
	
	public Shell(
		final Terminal terminal
	) {
		this.terminal = terminal;
		this.contextStack = new LinkedList<Terminal.Color>();
		this.history = new LinkedList<String>();
		this.cursor = new Terminal.Cursor(0, 0);
		
		terminal.useAlternateScreenBuffer(); 
		terminal.hideScrollbar();
		terminal.maximizeWindow();
		//termSize = terminal.getTermSize();
	}


	public void clearHistory() {
		history.clear();
	}
	
	public void push(final Terminal.Color color) {
		terminal.print(color.toString());
		contextStack.push(color);
	}
	
	public void pop() {
		contextStack.pop();
		
		if (contextStack.isEmpty()) {
			terminal.print(Terminal.Color.GRAY.toString());
		} else {
			terminal.print(contextStack.peek().toString());
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

	public boolean promptBoolean(
		final String promptString
	) throws 
		IOException 
	{
		GetBooleanCommand cmd =
			(GetBooleanCommand) prompt(promptString, GetBooleanCommand.BUILDER(), false);
		
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
		promptCursor = 0;
		historyIterator = history.listIterator();
		
		inputLoop : while (true) {
			c = (byte) terminal.read();
			
			switch (c) {
				// FUNKY CODES
				case (byte) 0xff:
					c = (byte) terminal.read();
					c = (byte) terminal.read();
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
						return cmd;
					}
					
					break inputLoop;
					
					
				// BACKSPACE and DELETE
				case 0x08: 
				case 0x7f: 
					if (b.length() > 0) {
						if (promptCursor == b.length()) {
							print((char) c);
							promptCursor = Math.max(0, promptCursor - 1);
							b.deleteCharAt(b.length() - 1);
							
						} else {
							promptCursor = Math.max(0, promptCursor - 1);
							b.deleteCharAt(promptCursor);
							
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
							promptCursor = b.length();
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
					if (promptCursor == b.length()) {
						if (hideTyping) {
							print('*');
						} else {
							print((char) c);
						}
						
						b.append((char) c);
						promptCursor++;
						
					} else {
						b.insert(promptCursor, (char) c);
						promptCursor++;
						
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
		terminal.clearRestOfLine();
		terminal.cursorLeft(b.length() - promptCursor + 1);
	}
	
	private void handleEscape(
		final String promptString, // this argument needs to be factored out somehow
		final boolean hideTyping
	) throws 
		IOException 
	{
		byte c = (byte) terminal.read();
		
		// CSI
		if (c == '[') {
			c = (byte) terminal.read();
			switch (c) {
				case 'A': 
					if (historyIterator.hasNext()) {
						b = new StringBuilder(historyIterator.next());
						promptCursor = b.length();
						refreshCurrentPrompt(promptString, hideTyping);
					}
					break;
					
				case 'B':  
					if (historyIterator.hasPrevious()) {
						b = new StringBuilder(historyIterator.previous());
						promptCursor = b.length();
						refreshCurrentPrompt(promptString, hideTyping);
					}
					break;
				
				case 'C': 
					if (promptCursor < b.length()) {
						promptCursor++;
						terminal.cursorRight();
					}
					break;
					
				case 'D': 
					if (promptCursor > 0) {
						promptCursor--;
						terminal.cursorLeft();
					}
					break;
			}
		} else {
			return;
		}
	}
	
	/**
	 * http://ramblingsrobert.wordpress.com/2011/04/13/java-word-wrap-algorithm/
	 */
	public static String wrap(
		String in, 
		final int len
	) {
		in = in.trim();
		
		if (in.length() < len) {
			return in;
		}
		
		if (in.substring(0, len).contains("\n")) {
			return 
				in.substring(0, in.indexOf("\n")).trim() + "\n\n" + 
				wrap(in.substring(in.indexOf("\n") + 1), len);
		}
		
		int place = 
			Math.max(
				Math.max(
					in.lastIndexOf(" ",len), 
					in.lastIndexOf("\t",len)), 
				in.lastIndexOf("-",len));
		
		return 
			in.substring(0,place).trim() + "\n" + 
			wrap(in.substring(place),len);
	}
	


	public void print(final String s) {
		terminal.print(s);
	}

	public void println() {
		terminal.println();
	}

	public void println(final String s) {
		terminal.println(s);
	}

	public void println(final char c) {
		terminal.println(c);
	}

	public void print(final char c) {
		terminal.print(c);
	}
}
