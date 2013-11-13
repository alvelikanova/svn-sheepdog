package com.sample;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import com.sample.business.services.UserManagementService;

@Component("changePassword")
public class ChangePasswordHandler implements HttpRequestHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ChangePasswordHandler.class);

	@Autowired
	private UserManagementService businessService;

	@Override
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		LOG.info("Started changing password");
		
		// http://localhost:9003/maven-quickstart/change/password?login=jdoe&old_password=qwerty123&new_password=qwerty345
		PageParameters params = getPageParameters(req);


		businessService.updatePassword(params.getLogin(), params.getOld_password(), params.getNew_password());

		PrintWriter writer = resp.getWriter();
		writer.write("Password has been successfully changed");

	}

	private PageParameters getPageParameters(HttpServletRequest req) {
		PageParameters result = new PageParameters();

		String login = req.getParameter("login");
		String old_password = req.getParameter("old_password");
		String new_password = req.getParameter("new_password");

		result.setLogin(login);
		result.setOld_password(old_password);
		result.setNew_password(new_password);

		return result;
	}

	private class PageParameters {
		private String login;
		private String old_password;
		private String new_password;

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getOld_password() {
			return old_password;
		}

		public void setOld_password(String old_password) {
			this.old_password = old_password;
		}

		public String getNew_password() {
			return new_password;
		}

		public void setNew_password(String new_password) {
			this.new_password = new_password;
		}
	}
}
