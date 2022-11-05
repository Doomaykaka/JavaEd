package main.java.medianotes.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.*;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import main.java.medianotes.auth.Authentication;

@RestController
public class mainController {
	// методы

	// отображаем страницу для входа и регистрации
	@RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	public String CreateMain(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		String cookieName = "password";
		if (cookies != null) {
			for (javax.servlet.http.Cookie c : cookies) {
				if (cookieName.equals(c.getName())) {
					c.setValue("");
					response.addCookie(c);
				}
			}
		}
		String html;
		html = "<html><head><title>StartPage</title></head><body>";
		html += "<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}#block{border: 1em solid #051373; border-radius: 1em;background: #051373;display: inline-block;}</style>";
		html += "<h2>Medianotes app</h2>";
		html += "<div id=\"block\">";
		html += "<h2>Input login and password:</h2>";
		html += "<form action=\"\" method=\"post\">";
		html += "<p>Login </p><input type=\"text\" name=\"log\"></input>";
		html += "<p>Password </p><input type=\"text\" name=\"pas\"></input>";
		html += "<p></p><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">Login</button>";
		html += "</form>";
		html += "</div>";
		html += "<p></p>";
		html += "<div id=\"block\">";
		html += "<h2>Register:</h2>";
		html += "<form action=\"\" method=\"post\">";
		html += "<p>Login </p><input type=\"text\" name=\"Rlog\"></input>";
		html += "<p>Password </p><input type=\"text\" name=\"Rpas\"></input>";
		html += "<p>Repeat the password </p><input type=\"text\" name=\"Rpas2\"></input>";
		html += "<p></p><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">Register</button>";
		html += "</form>";
		html += "</div>";
		html += "</body></html>";
		return html;
	}

	// обрабатываем данные для входа или регистрации
	@RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	public void ProcessingMain(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uname = request.getParameter("log");
		String pword = request.getParameter("pas");
		String regname = request.getParameter("Rlog");
		String regpword = request.getParameter("Rpas");
		String regpwordrep = request.getParameter("Rpas2");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		Authentication auth = new Authentication();

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			out.println("<html><head><title>StartPage</title></head><body>");
			out.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if ((uname != null) && (pword != null)) {
				int login_id = auth.findAccount(uname + " " + pword);

				if (login_id != -1) {
					out.println("<p>Hello " + uname + "</p>");
					response.addCookie(new Cookie("user", uname));
					response.addCookie(new Cookie("password", pword));
					response.addCookie(new Cookie("id", Integer.toString(login_id)));
					out.println("<a href=\"" + request.getContextPath() + "/menu\">Go to menu</a>");
				} else {
					out.println("<p>Bad login</p>");
					out.println("<a href=\"" + request.getContextPath() + "/\">Go to start page</a>");
				}
			} else {
				if ((regname != null) && (regpword != null) && (regpwordrep != null)) {
					if (regpword.equals(regpwordrep)) { // проверяем правильность пароля
						auth.readAuthentificationData();
						String[] vLOGINandPASSWORDandID = auth.getAccounts();
						boolean loginUsingLever;
						loginUsingLever = false;

						for (String acc : vLOGINandPASSWORDandID) {
							String[] data = acc.split(" ");
							if (data[0].equals(regname)) {
								loginUsingLever = true;
							}
						}

						if (loginUsingLever) {
							out.println("<p>The login is already in use</p>"); // выбрасываем исключение
																				// сигнализирующее о неверных данных
																				// аккаунта
							out.println("<a href=\"" + request.getContextPath() + "/\">Go to start page</a>");
						} else {
							auth.registerAccount(regname + " " + regpword); // регистрируем аккаунт
							out.println("<a href=\"" + request.getContextPath() + "/menu\">Go to menu</a>");
						}
					} else {
						out.println("<p>Bad password</p>");
						out.println("<a href=\"" + request.getContextPath() + "/\">Go to start page</a>");
					}
				} else {
					out.println("<p>Bad register</p>");
					out.println("<a href=\"" + request.getContextPath() + "/\">Go to start page</a>");
				}
			}
			
			out.println("</body></html>");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/");
		} finally {
			out.close();
		}
	}
}
