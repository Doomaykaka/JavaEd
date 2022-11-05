package main.java.medianotes.repository.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
				Statement st1 = db.createStatement();
				System.out.println("name - " + name + " pf - " + par_fol + " text - " + text + " author - " + author
						+ " cr date - " + cr_date);
				ResultSet rs1;
				boolean getId = true;
				int count = 0;
				while(getId) {
					rs1 = st1.executeQuery("SELECT * FROM \"Notes\" WHERE id="+count);
					if (!rs1.next()){
						getId=false;
					}else {
						count=count+1;
					}
				}
				Statement st = db.createStatement();
				st.executeUpdate("INSERT INTO \"Notes\" (id,parent_folder,name,text,author,creation_date,author_id)"
								+ "VALUES (" + Integer.toString(count) + ", '" + par_fol + "', '" + name + "', '"
								+ text + "', '" + author + "', '" + cr_date + "',"+Integer.toString(user_id_in)+")");
				
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
				
				Statement st = db.createStatement();
				st.executeUpdate("UPDATE \"Notes\" SET parent_folder='" + par_fol + "',name='"+name+"'"
								+ ",text='" + text + "',author='" + author + "', creation_date='" + cr_date + "',status='"+nt_status+"'"
								+ " WHERE name='"+name+"'");
				
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
				Statement st = db.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM \"Notes\" WHERE author_id="+user_id_in);
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
				Statement st = db.createStatement();
				st.executeUpdate("DELETE FROM \"Notes\" WHERE name='" + name + "' AND author_id="+Integer.toString(user_id_in));
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
				Statement st = db.createStatement();
				ResultSet rs = st.executeQuery("SELECT * FROM \"Folders\" WHERE author_id="+Integer.toString(user_id_in));
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
