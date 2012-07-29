package org.dashmud.cli;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class DashMudServer {
	private static final int PORT = 4444;
	private static final int MAX_CONNECTIONS = 0;

	private static AtomicInteger numConnections = new AtomicInteger(0);
	private static ObjectContainer dbContainer;
	private static ServerSocket listener;
	
	public static void main(final String[] args) {
		try {
			String databaseFile = 
				args[0];
			
			listener = 
				new ServerSocket(PORT);
			
			dbContainer = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), databaseFile);
			
			while ((numConnections.get() < MAX_CONNECTIONS) || (MAX_CONNECTIONS == 0)) {
				Socket socket = 
					listener.accept();

				numConnections.incrementAndGet();
				
				CommandLineInterfaceHandler cli = 
					new CommandLineInterfaceHandler(socket, dbContainer.ext().openSession());
				
				Thread t = 
					new Thread(cli);
				
				t.start();
			}
			
		} catch (IOException ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
			
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			dbContainer.close();
		}
	}
	
	public static int getNumConnections() {
		return numConnections.get();
	}

	public static class CommandLineInterfaceHandler implements Runnable {
		private static final String BANNER =
			"________                .__        _____   ____ ___________\r\n" +
			"\\______ \\ _____    _____|  |__    /     \\ |    |   \\______ \\\r\n" +
			" |    |  \\\\__  \\  /  ___/  |  \\  /  \\ /  \\|    |   /|    |  \\\r\n" +
			" |    `   \\/ __ \\_\\___ \\|   Y  \\/    Y    \\    |  / |    `   \\\r\n" +
			"/_______  (____  /____  >___|  /\\____|__  /______/ /_______  /\r\n" +
			"        \\/     \\/     \\/     \\/         \\/                 \\/\r\n";
	
		private final Socket server;
		private final ObjectContainer db;
		
		public CommandLineInterfaceHandler(
			final Socket server,
			final ObjectContainer db
		) {
			this.server = server;
			this.db = db;
		}
	
		public void run() {
			try {
				BufferedInputStream in = 
					new BufferedInputStream(server.getInputStream());
				
				PrintStream out = 
					new PrintStream(server.getOutputStream());
	
				Terminal t =
					new Terminal(in, out);
				
				t.push(Terminal.Color.LIGHT_GREEN);
					t.println(BANNER);
				t.pop();
				
				String username =
					t.prompt("username: ", false);
				
				String password =
					t.prompt("password: ", true);
				
				t.clearHistory();
				
				t.println();
				
				CommandBundle b =
					new CommandBundle();
				
				b.register("say", SayCommand.BUILDER);
				b.register("tell", TellCommand.BUILDER);
				b.register("whisper", TellCommand.BUILDER);
				b.register("emote", EmoteCommand.BUILDER);
				b.register("quit", QuitCommand.BUILDER);
				b.register("exit", QuitCommand.BUILDER);
				
				while (true) {
					t.prompt(username + "> ", b);
				}	
				
			} catch (IOException ioe) {
				System.out.println("IOException on socket listen: " + ioe);
				ioe.printStackTrace();
				
			} finally {
				try {
					server.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				numConnections.decrementAndGet();
			}
		}
	}
}
