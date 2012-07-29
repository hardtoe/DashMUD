package org.supermud.cli;

import junit.framework.Assert;

import org.dashmud.cli.Password;
import org.junit.Test;

public class PasswordTest {
	@Test
	public void simpleTest() throws Exception {
		String hash = Password.getSaltedHash("password");
		
		Assert.assertTrue(Password.check("password", hash));
	}
}
