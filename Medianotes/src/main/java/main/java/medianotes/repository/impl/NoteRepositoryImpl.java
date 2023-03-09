package main.java.medianotes.repository.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import main.java.medianotes.model.Folder;
import main.java.medianotes.model.Note;
import main.java.medianotes.repository.FolderRepository;
import main.java.medianotes.repository.NoteRepository;

//класс хранилища записок
public class NoteRepositoryImpl implements NoteRepository, Serializable {
	// поля класса

	private static final long serialVersionUID = 1L;
	private static Connection db = null; // объект подключения
	private static boolean connection_status = false; // логическая переменная хранящая статус подключения к бд

	// интерфейсы

	// функциональный интерфейс
	interface SortInt {
		// абстрактный метод
		List<Note> getSorted();
	}

	// методы класса

	// метод работы со статическими членами (запускается при старте приложения)
	static {
		String db_type = "postgresql";
		String db_address = "localhost";
		String db_port = "5432";
		String db_name = "Seventh";
		String db_user = "postgres";
		String db_password = "FireStarter11";

		// подключаемся к бд
		try {
			DriverManager.registerDriver((Driver) Class.forName("org.postgresql.Driver").newInstance());

			StringBuilder url = new StringBuilder();

			url.append("jdbc:" + db_type + "://").append(db_address + ":").append(db_port + "/").append(db_name + "?")
					.append("user=" + db_user + "&").append("password=" + db_password);

			db = DriverManager.getConnection(url.toString());
			if (db != null) {
				connection_status = true;
			}
		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// контруктор класса
	public NoteRepositoryImpl() {
	}

	// сохранение записки
	public Note save(Note note,int user_id_in) {
		// добавляем данные о записках
		if (connection_status) {
			try {
				FolderRepository folderRepository = new FolderRepositoryImpl(); //создаём объект интерфейса для хранения запсок
				
				String query = "";
				
				String name = note.getName().toUpperCase();
				String par_fol = "";
				if(note.getParentFolder()==null) {
					for(Folder fal : folderRepository.getFolders(user_id_in)) {
						par_fol=fal.getName();
					}
				}else {
					par_fol=note.getParentFolder().getName().toUpperCase();
				}
				String text = note.getText();
				String author = note.getAuthor();
				String cr_date = note.getCreationDate().toString();
				query = "SELECT * FROM \"Notes\" WHERE id=?";
				PreparedStatement st1 = db.prepareStatement(query);
				st1.setInt(1, 0);
				System.out.println("name - " + name + " pf - " + par_fol + " text - " + text + " author - " + author
						+ " cr date - " + cr_date);
				ResultSet rs1;
				boolean getId = true;
				int count = 0;
				while(getId) {
					rs1 = st1.executeQuery();
					st1.setInt(1, count);
					if (!rs1.next()){
						getId=false;
					}else {
						count=count+1;
					}
				}
				PreparedStatement st = db.prepareStatement("INSERT INTO \"Notes\" (id,parent_folder,name,text,author,creation_date,author_id)"
						+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
				st.setInt(1,count);
				st.setString(2, par_fol);
				st.setString(3, name);
				st.setString(4, text);
				st.setString(5, author);
				st.setString(6, cr_date);
				st.setInt(7, user_id_in);
				
				st.executeUpdate();			
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}
		}

		return note;
	};
	
	// сохранение записки
	public Note edit(Note note,int user_id_in) {
		// изменяем данные о записках
		if (connection_status) {
			try {
				FolderRepository folderRepository = new FolderRepositoryImpl(); //создаём объект интерфейса для хранения запсок
					
				String name = note.getName().toUpperCase();
				String par_fol = "";
				if(note.getParentFolder()==null) {
					for(Folder fal : folderRepository.getFolders(user_id_in)) {
						par_fol=fal.getName();
					}
				}else {
					par_fol=note.getParentFolder().getName().toUpperCase();
				}
				String text = note.getText();
				String author = note.getAuthor();
				String cr_date = note.getCreationDate().toString();
				String nt_status = note.getStatus();
				
				String query = "UPDATE \"Notes\" SET parent_folder=?,name=?,text=?,author=?, creation_date=?,status=? WHERE name=?";
				
				PreparedStatement st = db.prepareStatement(query);
				st.setString(1, par_fol);
				st.setString(2, name);
				st.setString(3, text);
				st.setString(4, author);
				st.setString(5, cr_date);
				st.setString(6, nt_status);
				st.setString(7, name);
				
				st.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}
		}

		return note;
	};

	// метод получения списка со всеми записками
	@Override
	public List<Note> getAllNotes(int user_id_in) {
		final List<Note> NOTES = new LinkedList<>();
		// запрашиваем данные о записках
		if (connection_status) {
			try {
				String query = "SELECT * FROM \"Notes\" WHERE author_id=?";
				
				PreparedStatement st = db.prepareStatement(query);
				st.setInt(1, user_id_in);
				ResultSet rs = st.executeQuery();
				ResultSet vl = rs;
				String nm;
				String pf;
				String txt;
				String author;
				String date;
				String status;
				while (vl.next()) {
					nm = vl.getString("name");
					pf = vl.getString("parent_folder");
					txt = vl.getString("text");
					author = vl.getString("author");
					date = vl.getString("creation_date");
					status = vl.getString("status");
					NOTES.add(new Note(nm, txt, author, FindFolder(pf,user_id_in),user_id_in,status,Instant.parse(date)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// сортировка списка записок
		SortInt si; // создаём объект функцианального интерфейса
		si = () -> NOTES.stream().sorted(Comparator.comparing(note -> note.getCreationDate())).collect(Collectors.toList()); // сортируем лямбда-выражением
		List <Note> sorted = si.getSorted();
		si = () -> sorted.stream().sorted().collect(Collectors.toList()); // сортируем лямбда-выражением
		Collections.sort(sorted);
		Collections.reverse(sorted);
		return sorted;
	}

	// метод удаления записки
	@Override
	public void remove(String name,int user_id_in) {
		// удаляем данные о записке
		if (connection_status) {
			try {
				String query = "DELETE FROM \"Notes\" WHERE name=? AND author_id=?";
				
				PreparedStatement st = db.prepareStatement(query);
				st.setString(1, name);
				st.setInt(2, user_id_in);
				
				st.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}

		}
	}

	// поиск папки
	public Folder FindFolder(String name,int user_id_in) {
		// запрашиваем данные о репозиториях
		if (connection_status) {
			try {
				String query = "SELECT * FROM \"Folders\" WHERE author_id=?";
				
				PreparedStatement st = db.prepareStatement(query);
				st.setInt(1, user_id_in);
				ResultSet rs = st.executeQuery();
				ResultSet vl = rs;
				String nm;
				String pf;
				while (vl.next()) {
					nm = vl.getString("name");
					pf = vl.getString("parent_folder");
					if (nm.equals(name)) {
						if (pf != null)
							return (new Folder(nm, FindFolder(pf, user_id_in), user_id_in));
						else
							return (new Folder(nm, null, user_id_in));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}

		}
		return null;
	};
}
