package org.dashmud.cli;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.dashmud.cli.QuitCommand.QuitError;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.Db4oRecoverableException;
import com.db4o.query.Predicate;

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
				Terminal terminal = new Terminal(server);
				Shell shell = new Shell(terminal);

				printBanner(shell);

				User user = getUser(shell);

				shell.println();
				
				CommandBundle b =
					new CommandBundle();
				
				b.register("say", SayCommand.BUILDER(user));
				b.register("move", MoveCommand.BUILDER(user)); 
				b.register("go", MoveCommand.BUILDER(user));
				b.register("tell", TellCommand.BUILDER(user));
				b.register("whisper", TellCommand.BUILDER(user));
				b.register("emote", EmoteCommand.BUILDER(user));
				b.register("quit", QuitCommand.BUILDER(user));
				b.register("exit", QuitCommand.BUILDER(user));
				
				while (true) {
					Command cmd = 
						shell.prompt(user.getName() + "> ", b);
					
					if (cmd != null) {
						cmd.run(shell);
					}
				}	
				
			} catch (Exception e) {
				System.out.println("An error occured: " + e);
				e.printStackTrace();
				
			} catch (QuitError e) {	
				// seeya!
				
			} finally {
				try {
					server.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				numConnections.decrementAndGet();
			}
		}

		protected void printBanner(Shell t) {
			t.push(Terminal.Color.LIGHT_GREEN);
				t.println(BANNER);
			t.pop();
		}

		protected User getUser(
			final Shell t
		) throws 
			IOException, 
			Exception,
			InterruptedException 
		{
			User user = null;
			
			while (user == null) {
				final String username =
					t.prompt("username: ", false);

				t.clearHistory();

				
				ObjectSet<User> users = db.query(new Predicate<User>() {
					@Override
					public boolean match(final User u) {
						return u.getName().equals(username);
					}
				});
				
				if (users.size() == 1) {
					user = users.get(0);
					
					boolean invalidPassword = true;
					
					while (invalidPassword) {
						String password =
							t.prompt("password: ", true);
						
						t.clearHistory();
						
						invalidPassword =
							!Password.check(password, user.getHash());
						
						Thread.sleep(1000);
					}
					
				} else {
					t.println();
					
					if (t.promptBoolean("User \"" + username + "\" does not exist, would you like to create it? ")) {
						boolean invalidPassword = true;
						String password = null;
						
						while (invalidPassword) {
							String firstPassword =
								t.prompt("new password: ", true);
							
							String secondPassword =
								t.prompt("re-enter password: ", true);
							
							t.println();
							
							invalidPassword = false;
							
							if (!firstPassword.equals(secondPassword)) {
								t.println("Passwords don't match.");
								invalidPassword = true;
							}
							
							if (firstPassword.length() < 8) {
								t.println("Passwords should be at least 8 characters long.");
								invalidPassword = true;
							}
							
							password = firstPassword;
						}
						
						user = new User(username, Password.getSaltedHash(password));
						db.store(user);
						db.commit();
					}
				}
			}
			
			return user;
		}
	}
}
