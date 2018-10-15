/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package it.eng.idra.authentication;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import it.eng.idra.authentication.basic.LoggedUser;
import it.eng.idra.beans.User;
import it.eng.idra.beans.exception.InvalidPasswordException;
import it.eng.idra.management.FederationCore;
import it.eng.idra.management.PersistenceManager;
import it.eng.idra.utils.CommonUtil;
import it.eng.idra.utils.GsonUtil;

public class BasicAuthenticationManager extends AuthenticationManager {

	private static BasicAuthenticationManager instance;
	private static List<LoggedUser> loggedUsers = new ArrayList<LoggedUser>();
	private static Logger logger = FederationCore.getLogger();

	private BasicAuthenticationManager() {
	}

	public static BasicAuthenticationManager getInstance() {
		if (instance == null)
			instance = new BasicAuthenticationManager();
		return instance;
	}

	@Override
	public Object login(String username, String password, String code)
			throws SQLException, NullPointerException, NoSuchAlgorithmException {

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			User existingUser = manageBeansJpa.getUser(username);

			if (!existingUser.getPassword().equals(password)) {

				logger.error("Username or password invalid");
				throw new NullPointerException("Username or password invalid!");

			} else {

				String token = getToken(existingUser.getUsername(), null);
				LoggedUser u = new LoggedUser(existingUser.getUsername(), token);
				loggedUsers.add(u);
				logger.info("Login success");
				return token;

			}
		} finally {
			manageBeansJpa.jpaClose();
		}

	}

	@Override
	public Response logout(HttpServletRequest httpRequest) throws Exception {
		
		String input = IOUtils.toString(httpRequest.getInputStream(), Charset.defaultCharset());
		LoggedUser user = GsonUtil.json2Obj(input, GsonUtil.loggedUserType);

		
		int remove = -1;
		for (int i = 0; i < loggedUsers.size(); i++) {
			if (loggedUsers.get(i).getUsername().equals(user.getUsername())) {
				remove = i;
			}
		}
		if (remove >= 0) {
			loggedUsers.remove(remove);
		}

		logger.info("Logout success");
		return Response.status(Response.Status.OK).build();

	}

	@Override
	public String getToken(String username, String code) throws NoSuchAlgorithmException {
		Random random = new SecureRandom();
		String t = new BigInteger(130, random).toString(32);
		return CommonUtil.encodePassword(username + t);
	}

	@Override
	public Boolean validateToken(Object token) throws Exception {
		// Check if it was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid

		// int defaultValidationPeriod = Integer.parseInt(
		// FederationCore.getSettings().get("token_validation") );
		int defaultValidationPeriod = 3600000;

		if (loggedUsers.size() == 0) {
			return false;
		}

		for (int i = 0; i < loggedUsers.size(); i++) {
			LoggedUser tmp = loggedUsers.get(i);
			if (tmp.getToken().equals(token)) {
				Date n = new Date();
				if ((n.getTime() - tmp.getCreationDate().getTime()) > defaultValidationPeriod) {
					loggedUsers.remove(i);
					return false;
				} else {
					tmp.setCreationDate(n);
					break;
				}
			} else if (i == loggedUsers.size() - 1 && !tmp.getToken().equals(token)) {
				return false;
			}
		}
		return true;
	}

	public static List<LoggedUser> getLoggedUsers() {
		return loggedUsers;
	}

	public static boolean validatePassword(String username, String password) throws SQLException {

		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			User tmp = manageBeansJpa.getUser(username);
			if (!tmp.getPassword().equals(password)) {
				return false;
			}

			return true;
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	public static boolean updateUserPassword(String username, String newPassword)
			throws SQLException, NoSuchAlgorithmException, InvalidPasswordException {
		PersistenceManager manageBeansJpa = new PersistenceManager();
		try {
			User u = manageBeansJpa.getUser(username);
			manageBeansJpa.updateUserPassword(u, newPassword);
			return validatePassword(u.getUsername(), newPassword);
		} finally {
			manageBeansJpa.jpaClose();
		}
	}

	@Override
	public Class<BasicAuthenticationManager> getFilterClass() throws ClassNotFoundException {

		return BasicAuthenticationManager.class;

	}

}
