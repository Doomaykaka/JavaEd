package main.java.medianotes.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;

import javax.servlet.ServletException;
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
public class NoteController {
	// поля

	private static final NoteRepository noteRepository = new NoteRepositoryImpl(); // создаём объект интерфейса для
																					// хранения записок
	private static final FolderRepository folderRepository = new FolderRepositoryImpl(); // создаём объект интерфейса
																							// для хранения папок
	private static int user_id; // поле с id вошедшего пользователя

	// методы

	// проверяем данные аккаунта пользователя
	private int CheckLogin(HttpServletRequest req) throws IOException {
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

		Authentication auth = new Authentication();
		status = auth.findAccount(cookieLogin.getValue() + " " + cookiepassword.getValue());

		user_id = Integer.parseInt(cookieid.getValue());

		return status;
	}

	// отображаем все записки пользователя
	@RequestMapping(value = "/viewNotes", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void ViewNotes(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Note> notes = noteRepository.getAllNotes(user_id); // получаем список всех записок
		resp.setContentType("text/html");

		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>ViewNotes</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}#block{border: 1em solid #051373; border-radius: 1em;background: #051373;display: inline-block;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Notes</h2>");
				for (Note note : notes) { // выводим их содержимое
					writer.println("<p></p><div id=\"block\">");
					writer.printf("Path: %S , Name: %s , Text: %s , Author: %s , CreationDate: %s , Status: %s \n",
							note.getParentFolder().getName(), note.getName(), note.getText(), note.getAuthor(),
							note.getCreationDate().toString(), note.getStatus());
					writer.println("</div><p></p>");
				}
				writer.println("<a href=\"" + req.getContextPath() + "/menu\">Go to menu</a>");
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

	// отображаем страницу изменения статуса записки
	@RequestMapping(value = "/statusNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void StatusNoteSetGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter("name");
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>StatusNote</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Note status set</h2>");
				writer.println("<form action=\"\" method=\"post\">");
				writer.println("<p>Note status </p>");
				writer.println(
						"<p> Completed </p><input type=\"radio\" name=\"nstatus\" id=\"stattype\" value=\"Completed\"></input>");
				writer.println(
						"<p> Not completed </p><input type=\"radio\" name=\"nstatus\" id=\"stattype2\" value=\"notCompleted\"></input>");
				writer.println("<input type=\"hidden\" name=\"nname\" value=\"" + name + "\"></input>");
				writer.println(
						"<p></p><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">Set</button>");
				writer.println("</form>");
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

	// обрабатываем данные для изменения статуса записки
	@RequestMapping(value = "/statusNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	protected void StatusNoteSetPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String notename = request.getParameter("nname");
		String notestatus = request.getParameter("nstatus");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			out.println("<html><head><title>StatusNote</title></head><body>");
			out.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (user_id != -1) {
				for (Note note : noteRepository.getAllNotes(user_id)) { // ищем записку
					if (note.getName().equals(notename)) {
						note.setStatus(notestatus);
						noteRepository.edit(note, user_id);
					}
				}

				out.println("Note created "); // выводим информирующее сообщение
				out.println("<a href=\"" + request.getContextPath() + "/menu\">Go to menu</a>");
			} else {
				out.println("<p>Bad cookie</p>");
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

	// отображаем страницу создания записки
	@RequestMapping(value = "/createNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void CreateNoteGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String parfol = req.getParameter("curr");
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>CreateNote</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Note</h2>");

				writer.println("<form action=\"\" method=\"post\">");
				writer.println("<p>Note name </p><input type=\"text\" name=\"nname\"></input>");
				writer.println("<p>Note author </p><input type=\"text\" name=\"nauthor\"></input>");
				writer.println("<input type=\"hidden\" name=\"nfolder\" value=\"" + parfol + "\"></input>");
				writer.println("<p>Note text </p><input type=\"text\" name=\"ntext\"></input>");
				writer.println(
						"<p></p><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">Create</button>");
				writer.println("</form>");
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

	// обрабатываем данные для создания записки
	@RequestMapping(value = "/createNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	protected void CreateNotePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String notename = request.getParameter("nname");
		String noteauthor = request.getParameter("nauthor");
		String notefolder = request.getParameter("nfolder");
		String notetext = request.getParameter("ntext");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			out.println("<html><head><title>CreateNote</title></head><body>");
			out.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (user_id != -1) {
				Folder falderRes = new Folder("", null, user_id);

				boolean res = false; // ищем такую папку и в случае удачи копируем объект
				for (Folder fal : folderRepository.getFolders(user_id)) {
					if (fal.getName().equals(notefolder)) {
						res = true;
						falderRes = fal;
						break;
					}
				}
				if (!res) { // если не нашли , то прекращаем выполнение
					out.println("<p>Folder not exist</p>");
				}

				Note newNote = new Note(notename, notetext, noteauthor, falderRes, user_id, "notCompleted",
						Instant.now()); // создаём записку с полученными полями
				noteRepository.save(newNote, user_id);

				out.println("<p>Note created </p>"); // выводим информирующее сообщение
				out.println("<a href=\"" + request.getContextPath() + "/menu\">Go to menu</a>");
			} else {
				out.println("<p>Bad cookie</p>");
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

	// отображаем страницу изменения записки
	@RequestMapping(value = "/editNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void EditNoteGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter("name");
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>EditNote</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Note</h2>");

				writer.println("<form action=\"\" method=\"post\">");
				writer.println("<p>Note name is " + name + "</p><input type=\"hidden\" name=\"nname\" value=\"" + name
						+ "\"></input>");
				writer.println("<p>Note author </p><input type=\"text\" name=\"nauthor\"></input>");
				writer.println("<p>Note folder </p><input type=\"text\" name=\"nfolder\"></input>");
				writer.println("<p>Note text </p><input type=\"text\" name=\"ntext\"></input>");
				writer.println(
						"<p></p><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">Edit</button>");
				writer.println("</form>");
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

	// обрабатываем данные для изменения записки
	@RequestMapping(value = "/editNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	protected void EditNotePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String notename = request.getParameter("nname");
		String noteauthor = request.getParameter("nauthor");
		String notefolder = request.getParameter("nfolder");
		String notetext = request.getParameter("ntext");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			out.println("<html><head><title>EditNote</title></head><body>");
			out.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (user_id != -1) {
				Folder falderRes = new Folder("", null, user_id);

				boolean res = false; // ищем такую папку и в случае удачи копируем объект
				for (Folder fal : folderRepository.getFolders(user_id)) {
					if (fal.getName().equals(notefolder)) {
						res = true;
						falderRes = fal;
						break;
					}
				}
				if (!res) { // если не нашли , то прекращаем выполнение
					out.println("<p>Folder not exist</p>");
				}

				for (Note note : noteRepository.getAllNotes(user_id)) { // ищем записку
					if (note.getName().equals(notename)) {
						note.setAuthor(noteauthor);
						note.setParentFolder(falderRes);
						note.setText(notetext);
						noteRepository.edit(note, user_id);
					}
				}

				out.println("<p>Note edited </p>"); // выводим информирующее сообщение
				out.println("<a href=\"" + request.getContextPath() + "/menu\">Go to menu</a>");
			} else {
				out.println("<p>Bad cookie</p>");
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

	// отображаем страницу удаления записки
	@RequestMapping(value = "/deleteNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void DeleteNoteGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter("name");
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>DeleteNote</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Delete note?</h2>");
				writer.println("<form action=\"\" method=\"post\">");
				writer.println("<input type=\"hidden\" name=\"nname\" value=\"" + name + "\"></input>");
				writer.println(
						"<p></p><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">Delete</button>");
				writer.println("</form>");
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

	// обрабатываем данные для удаления записки
	@RequestMapping(value = "/deleteNote", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	protected void DeleteNotePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String name = request.getParameter("nname");
		PrintWriter writer = response.getWriter();

		try {
			writer.println("<html><head><title>DeleteNote</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (user_id != -1) {
				noteRepository.remove(name, user_id); // удаляем записку по полученному имени

				writer.println("<p>Note deleted</p>"); // выводим информирующее сообщение

				writer.println("<a href=\"" + request.getContextPath() + "/menu\">Go to menu</a>");
			}
			
			writer.println("</body></html>");
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/");
		} finally {
			writer.close();
		}
	}
}
