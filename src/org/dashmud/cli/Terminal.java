package org.dashmud.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;


public class Terminal {
	public static class Cursor {
		public int x;
		public int y;
		
		public Cursor( 
			final int x,
			final int y
		) {
			this.x = x;
			this.y = y;
		}
	}

	// FIXME: color should be context and should be (attr, fg, bg) instead
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

	public Terminal(
		final InputStream in,
		final OutputStream out
	) {
		this.in = in;
		this.out = new PrintStream(out);
	}
	
	public Terminal(
		final Socket socket	
	) throws 
		IOException 
	{
		this(socket.getInputStream(), socket.getOutputStream());
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
	
	public int read() throws IOException {
		return in.read();
	}

	public void useAlternateScreenBuffer() {
		print("\033[?47h");
	}

	public void maximizeWindow() {
		print("\033[9;1t");
	}

	public void hideScrollbar() {
		print("\033[?30l");
	}

	public Cursor getTermSize() {
		print("\033[18t");
		
		Cursor size =
			new Cursor(0, 0);
		
		StringBuilder b =
			new StringBuilder();
		
		//byte c = in.read();
		
		throw new UnsupportedOperationException();
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
}
