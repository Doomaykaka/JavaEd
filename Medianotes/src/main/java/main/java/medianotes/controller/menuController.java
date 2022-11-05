package main.java.medianotes.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.http.*;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import main.java.medianotes.auth.Authentication;
import main.java.medianotes.model.Folder;
import main.java.medianotes.model.Note;
import main.java.medianotes.repository.FolderRepository;
import main.java.medianotes.repository.NoteRepository;
import main.java.medianotes.repository.impl.FolderRepositoryImpl;
import main.java.medianotes.repository.impl.NoteRepositoryImpl;

@RestController
public class menuController {
	// поля
	private static final NoteRepository noteRepository = new NoteRepositoryImpl(); // создаём объект интерфейса для
																					// хранения запсок
	private static final FolderRepository folderRepository = new FolderRepositoryImpl(); // создаём объект интерфейса
																							// для хранения запсок

	// методы

	// проверяем данные аккаунта пользователя
	private String[] CheckLogin(HttpServletRequest req) throws IOException {
		int status = -1;

		Cookie[] cookies = req.getCookies();
		String cookieName1 = "user";
		String cookieName2 = "password";
		String cookieName3 = "id";
		Cookie cookieLogin = null;
		Cookie cookiepassword = null;
		Cookie cookieid = null;
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (cookieName1.equals(c.getName())) {
					cookieLogin = c;
				}
				if (cookieName2.equals(c.getName())) {
					cookiepassword = c;
				}
				if (cookieName3.equals(c.getName())) {
					cookieid = c;
				}
			}
		}

		status = Authentication.findAccount(cookieLogin.getValue() + " " + cookiepassword.getValue());
		
		int user_id = Integer.parseInt(cookieid.getValue());
		
		String[] data = new String[2];
		
		data[0] = Integer.toString(status);
		data[1] = Integer.toString(user_id);

		return data;
	}

	// отображаем страницу с меню
	@RequestMapping(value = "/menu", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void CreateMenu(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>Menu</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}#list{border: 1em solid #1158c2; border-radius: 2em;background: #1158c2;display: inline-block;}#block{border: 1em solid #051373; border-radius: 1em;background: #051373;display: inline-block;}</style>");

			String[] data = CheckLogin(req);
			int user_id = Integer.parseInt(data[1]);
			int status = Integer.parseInt(data[0]);
			if (status != -1) {
				writer.println("<p>Hello user with id "+Integer.toString(user_id)+"</p>");
				writer.println("<p><a href=\"" + req.getContextPath() + "/\">LogOut</a></p>");
				writer.println("<div id=\"block\">");
				writer.println("<h2>Folders</h2>");
				if (folderRepository.getCurrent(user_id) != null) {
					writer.println("<p><a href=\"" + req.getContextPath() + "/createFolder?curr="
							+ folderRepository.getCurrent(user_id).getName() + "\">Create folder</a></p>");
				} else {
					writer.println(
							"<p><a href=\"" + req.getContextPath() + "/createFolder?curr=\">Create folder</a></p>");
				}
				writer.println("<p><a href=\"" + req.getContextPath() + "/viewFolders\">View folders</a></p>");
				writer.println("</div>");
				writer.println("<p></p>");
				writer.println("<div id=\"block\">");
				writer.println("<h2>Notes</h2>");
				if (folderRepository.getCurrent(user_id) != null) {
					writer.println("<p><a href=\"" + req.getContextPath() + "/createNote?curr="
							+ folderRepository.getCurrent(user_id).getName() + "\">Create note</a></p>");
				}
				writer.println("<p><a href=\"" + req.getContextPath() + "/viewNotes\">View notes</a></p>");
				writer.println("</div>");
				writer.println("<p></p>");
				writer.println("<div id=\"block\">");
				writer.println("<h2>List</h2>");

				writer.println("<div id=\"block\">");

				Set<Folder> folders = folderRepository.getFolders(user_id); // получаем список всех папок
				List<Note> notes = noteRepository.getAllNotes(user_id); // получаем список всех записок

				Folder curr = folderRepository.getCurrent(user_id);

				if (curr == null) {
					for (Folder folder : folders) { // выводим их содержимое
						if ((folder.getParentFolder() == null) && (folder != null)) {
							writer.println("<p></p><div id=\"list\"><p></p>"
									+"<p>DIR | " + folder.getName() + " \n<p> "
									+ "<form action=\"\" method=\"post\">"
									+ "<button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">"
									+ "GO</button><input type=\"hidden\" name=\"go\" value=\"" + folder.getName()
									+ "\"></form>");
							writer.println("<a href=\"" + req.getContextPath() + "/deleteFolder?name="
									+ folder.getName() + "\">REMOVE</a>");
							writer.println("<p></p></div>");
						}
					}

					for (Note note : notes) { // выводим записки
						if ((note.getParentFolder() == null) && (note != null)) {
							writer.println("<p></p><div id=\"list\"><p></p>"
									+ "<p>NOTE | " + note.getName() + " | " + note.getText() + " | "
									+ note.getAuthor() + " | " + note.getCreationDate().toString() + " | "
									+ note.getStatus() + " | \n<p>");
							writer.println("<a href=\"" + req.getContextPath() + "/editNote?name=" + note.getName()
									+ "\">EDIT</a>");
							writer.println("<a href=\"" + req.getContextPath() + "/statusNote?name=" + note.getName()
									+ "\">SET STATUS</a>");
							writer.println("<a href=\"" + req.getContextPath() + "/deleteNote?name=" + note.getName()
									+ "\">REMOVE</a>");
							writer.println("<p></p></div>");
						}
					}
				} else {
					writer.println(
							"<p>...\n<p> <form action=\"\" method=\"post\"><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">GO</button><input type=\"hidden\" name=\"go\" value=\"..\"></form>");

					for (Folder folder : folders) { // выводим их содержимое
						if ((folder.getParentFolder() != null) && (folder != null)) {
							if (folder.getParentFolder().getName().equals(curr.getName())) {
								writer.println("<p></p><div id=\"list\"><p></p>"
										+ "<p>DIR | " + folder.getName() + " \n<p> "
										+ "<form action=\"\" method=\"post\">"
										+ "<button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">"
										+ "GO</button><input type=\"hidden\" name=\"go\" value=\"" + folder.getName()
										+ "\"></form>");
								writer.println("<a href=\"" + req.getContextPath() + "/deleteFolder?name="
										+ folder.getName() + "\">REMOVE</a>");
								writer.println("<p></p></div>");
							}
						}
					}

					for (Note note : notes) { // выводим записки
						if ((note.getParentFolder() != null) && (note != null)) {
							if (note.getParentFolder().getName().equals(curr.getName())) {
								writer.println("<p></p><div id=\"list\"><p></p>"
										+ "<p>NOTE | " + note.getName() + " | " + note.getText() + " | "
										+ note.getAuthor() + " | " + note.getCreationDate().toString() + " | "
										+ note.getStatus() + " | \n<p>");
								writer.println("<a href=\"" + req.getContextPath() + "/editNote?name=" + note.getName()
										+ "\">EDIT</a>");
								writer.println("<a href=\"" + req.getContextPath() + "/statusNote?name="
										+ note.getName() + "\">SET STATUS</a>");
								writer.println("<a href=\"" + req.getContextPath() + "/deleteNote?name="
										+ note.getName() + "\">REMOVE</a>");
								writer.println("<p></p></div>");
							}
						}
					}
				}
				
				writer.println("</div>");
			} else {
				writer.println("<p>Bad cookie</p>");
				writer.println("<a href=\"" + req.getContextPath() + "/\">Go to start page</a>");
			}
			
			writer.println("</body></html>");
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendRedirect(req.getContextPath() + "/");
		} finally {
			writer.close();
		}
	}

	// обрабатываем страницу с меню
	@RequestMapping(value = "/menu", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	public void RedirectMenu(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			out.println("<html><head><title>Menu</title></head><body>");
			out.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");
			String[] data = CheckLogin(request);
			int user_id = Integer.parseInt(data[1]);
			int status = Integer.parseInt(data[0]);
			if (status != -1) {
				String fname = request.getParameter("go");
				folderRepository.setCurrent(fname, user_id);
				response.sendRedirect(request.getContextPath() + "/menu");
			} else {
				out.println("<p>Bad login</p>");
				out.println("<a href=\"" + request.getContextPath() + "/\">Go to start page</a>");
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
