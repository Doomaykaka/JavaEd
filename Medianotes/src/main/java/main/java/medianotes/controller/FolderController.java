package main.java.medianotes.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import main.java.medianotes.auth.Authentication;
import main.java.medianotes.model.Folder;
import main.java.medianotes.repository.FolderRepository;
import main.java.medianotes.repository.NoteRepository;
import main.java.medianotes.repository.impl.FolderRepositoryImpl;
import main.java.medianotes.repository.impl.NoteRepositoryImpl;

@RestController
public class FolderController {
	// поля

	private static final NoteRepository noteRepository = new NoteRepositoryImpl(); // создаём объект интерфейса для
																					// хранения запсок
	private static final FolderRepository folderRepository = new FolderRepositoryImpl(); // создаём объект интерфейса

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

	// отображаем все папки пользователя
	@RequestMapping(value = "/viewFolders", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void ViewFolders(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Set<Folder> folders = folderRepository.getFolders(user_id); // получаем список всех папок
		resp.setContentType("text/html");

		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>ViewFolders</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}#block{border: 1em solid #051373; border-radius: 1em;background: #051373;display: inline-block;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Folders</h2>");
				for (Folder folder : folders) { // выводим их содержимое
					writer.println("<p></p><div id=\"block\">");
					writer.println(
							"<p>Path: " + folderRepository.getPath(folder.getName(), folder.getParentFolder(), user_id)
									+ " \n</p>");
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

	// отображаем страницу создания папки
	@RequestMapping(value = "/createFolder", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void CreateFolderGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String parfol = req.getParameter("curr");
		resp.setContentType("text/html");

		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>CreateFolder</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Folder</h2>");

				writer.println("<form action=\"\" method=\"post\">");
				writer.println("<p>Folder name </p><input type=\"text\" name=\"fol\"></input>");
				writer.println(
						"<p></p><button class=\"form_auth_button\" type=\"submit\" name=\"form_auth_submit\">Create</button>");
				writer.println("<input type=\"hidden\" name=\"par\" value=\"" + parfol + "\">");
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

	// обрабатываем данные для создания папки
	@RequestMapping(value = "/createFolder", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	protected void CreateFolderPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String folname = request.getParameter("fol");
		String parentfn = request.getParameter("par");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		try {
			out.println("<html><head><title>CreateFolder</title></head><body>");
			out.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (user_id != -1) {
				boolean res2 = true; // ищем такую папку и в случае отсутствия продолжаем работу
				for (Folder fal : folderRepository.getFolders(user_id)) {
					if (fal.getName().equals(folname)) {
						res2 = false;
						break;
					}
				}

				if (!res2) {
					out.println("<p>This folder has already been created</p>"); // выводим информирующее сообщение
				}

				Folder par; // искомая папка

				folderRepository.setCurrent(parentfn, user_id);

				par = folderRepository.getCurrent(user_id);

				if (par == null) {
					folderRepository.createFolder(folname, null, user_id);
					out.println("<p>Folder created</p>"); // выводим информирующее сообщение
				} else {
					folderRepository.createFolder(folname, par, user_id);
					out.println("<p>Folder created</p>"); // выводим информирующее сообщение
				}

				out.println("<a href=\"" + request.getContextPath() + "/menu\">Go to menu</a>");
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

	// отображаем страницу удаления папки
	@RequestMapping(value = "/deleteFolder", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.GET)
	protected void DeleteFolderGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String folname = req.getParameter("name");
		resp.setContentType("text/html");
		PrintWriter writer = resp.getWriter();

		try {
			writer.println("<html><head><title>DeleteFolder</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (CheckLogin(req) != -1) {
				writer.println("<h2>Delete folder?</h2>");
				writer.println("<form action=\"\" method=\"post\">");
				writer.println("<p>Folder " + folname + "</p>");
				writer.println("<input type=\"hidden\" name=\"del\" value=\"" + folname + "\">");
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

	// обрабатываем данные для удаления папки
	@RequestMapping(value = "/deleteFolder", produces = MediaType.TEXT_HTML_VALUE, method = RequestMethod.POST)
	protected void DeleteFolderPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String folname = request.getParameter("del");
		PrintWriter writer = response.getWriter();

		try {
			writer.println("<html><head><title>DeleteFolder</title></head><body>");
			writer.println("<style> body { background: #4e5f8f; color: #ff713d;  }A{ color: white;}</style>");

			if (user_id != -1) {

				if (folderRepository.getCurrent(user_id) != null) {
					Folder par; // искомая папка
					par = folderRepository.findFolder(folname, folderRepository.getCurrent(user_id), user_id);
					if (par == null) {
						writer.println("<p>Folder not deleted</p>"); // выводим информирующее сообщение
					} else {
						folderRepository.removeFolder(folname, folderRepository.getCurrent(user_id).getName(), user_id,
								noteRepository);
						writer.println("<p>Folder deleted</p>"); // выводим информирующее сообщение
					}
				} else {
					for (Folder fal : folderRepository.getFolders(user_id)) {
						if (fal.getName().equals(folname)) {
							if (fal.getParentFolder() != null) {
								folderRepository.removeFolder(folname, fal.getParentFolder().getName(), user_id,
										noteRepository);
							} else {
								folderRepository.removeFolder(folname, null, user_id, noteRepository);
							}
							writer.println("<p>Folder deleted<p>"); // выводим информирующее сообщение
							break;
						}
					}
				}

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
