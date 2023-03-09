package main.java.medianotes.repository.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import main.java.medianotes.model.Folder;
import main.java.medianotes.model.Note;
import main.java.medianotes.repository.FolderRepository;
import main.java.medianotes.repository.NoteRepository;

//класс хранилища папок
public class FolderRepositoryImpl implements FolderRepository, Serializable {
	// поля класса

	private static final long serialVersionUID = 1L;
	private String path; // строка для хранения пути
	private static Connection db = null; // объект подключения
	private static boolean connection_status = false; // логическая переменная хранящая статус подключения к бд
	private String currentDir; // строка для текущей папки
	Set <String> mp;
	
	// интерфейсы

	// функциональный интерфейс
	interface SortInt {
		// абстрактный метод
		Set<Folder> getSorted();
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
	public FolderRepositoryImpl() {
		currentDir="";
	}

	// геттер репозитория (получение репозитория)
	public Set<Folder> getFolders(int user_id_in) {
		final Set<Folder> folders = new HashSet<>(); // множество
		final Set<Folder> foldersN = new HashSet<>(); // множество
		mp=new HashSet<>(); // множество
		PreparedStatement st = null;
		int count=0;
		// запрашиваем данные о репозиториях
		if (connection_status) {
			try {
				String query = "SELECT * FROM \"Folders\" WHERE author_id=?";
				
				st = db.prepareStatement(query);
				st.setInt(1, user_id_in);
				ResultSet vl = st.executeQuery();
				while (vl.next()) {
					final String pf = vl.getString("parent_folder");
					String nm;
					nm = vl.getString("name");
					if((pf==null)&&(mp.contains(nm)==false)) {	
						foldersN.add(new Folder(nm, null, user_id_in));
						mp.add(nm);
					}
				}
				vl.close();
				
				query = "SELECT count(*) FROM \"Folders\" WHERE author_id=?";
				st = db.prepareStatement(query);
				st.setInt(1, user_id_in);
				ResultSet rs2 = st.executeQuery();
				while (rs2.next()) {
					count = rs2.getInt(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		folders.addAll(addFoldersRec(st,foldersN,user_id_in,count));
		
		// сортировка списка папок
		SortInt si; // создаём объект функцианального интерфейса
		si = () -> folders.stream().sorted(Comparator.comparing(folder -> folder.getName()))
				.collect(Collectors.toSet()); // сортируем список папок при помощи лямбда-выражениия

		return si.getSorted();
	}
	
	//  метод рекурсивного добавления папок 
	public Set<Folder> addFoldersRec(PreparedStatement st,Set<Folder> foldersPrevios,int user_id_in,int size) {
		Set<Folder> folders = new HashSet<>(); // множество	
		
		try {
			String query = "SELECT * FROM \"Folders\" WHERE author_id=?";
			
			st = db.prepareStatement(query);
			st.setInt(1, user_id_in);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				final String pf = rs.getString("parent_folder");
				String nm;
				if(pf!=null) {
					nm = rs.getString("name");
					for(Folder fol:foldersPrevios) {
						if((fol.getName().equals(pf))&&(mp.contains(nm)==false)) {
							folders.add(new Folder(nm, fol, user_id_in));
							mp.add(nm);
						}	
					}
				}
			}
			rs.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Set<Folder> foldersThis = folders;
		
		folders.addAll(foldersPrevios);

		if(folders.toArray().length<size) {
			foldersThis=addFoldersRec(st,foldersThis,user_id_in,size);
		}else {
			foldersThis=folders;
		}
		
		return foldersThis;
	}

	// создание папки
	public void createFolder(String name, Folder parentFolder, int user_id_in) {
		// добавляем данные о репозиториях
		if (connection_status) {
			try {
				String query = "SELECT * FROM \"Folders\" WHERE id=?";
				
				int count = 0;
				name = name.toUpperCase();
				PreparedStatement st1 = db.prepareStatement(query);
				st1.setInt(1, count);
				ResultSet rs1;
				boolean getId = true;
				while(getId) {
					rs1 = st1.executeQuery();
					if (!rs1.next()){
						getId=false;
					}else {
						count=count+1;
						st1.setInt(1, count);
					}
				}
				
				
				if(parentFolder==null){
					query = "INSERT INTO \"Folders\" (id,name,parent_folder,author_id)" + "VALUES (?, ?, null,?)";
					
					PreparedStatement st = db.prepareStatement(query);
					st.setInt(1,count);
					st.setString(2,name);
					st.setInt(3, user_id_in);
					st.executeUpdate();
				}else {
					query = "INSERT INTO \"Folders\" (id,name,parent_folder,author_id)" + "VALUES (?, ?, ?,?)";
					
					PreparedStatement st = db.prepareStatement(query);
					st.setInt(1,count);
					st.setString(2,name);
					st.setString(3, parentFolder.getName());
					st.setInt(4, user_id_in);
					st.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}

		}
	}

	// удаление папки
	public void removeFolder(String name, String parentFolderName, int user_id_in, NoteRepository noteRepository) {
		Set<Folder> folders = getFolders(user_id_in);
		// удаляем данные о репозиториях
		if (connection_status) {
			try {
				String query = "";
				PreparedStatement st;
				for(Note n : noteRepository.getAllNotes(user_id_in)) {
					if(n.getParentFolder().getName().equals(name)) {
						noteRepository.remove(n.getName(), user_id_in);
					}
				}
				for(Folder f : folders) {
					if(f.getParentFolder()!=null) {
						if(f.getParentFolder().getName().equals(name)) {
							removeFolder(f.getName(),f.getParentFolder().getName(),user_id_in,noteRepository);
						}
					}
				}
				if(parentFolderName!=null) {
					query = "DELETE FROM \"Folders\" WHERE name=? AND parent_folder=? AND author_id=?";
					
					st = db.prepareStatement(query);
					st.setString(1, name);
					st.setString(2, parentFolderName);
					st.setInt(3, user_id_in);
					
					st.executeUpdate();
				}else {
					query = "DELETE FROM \"Folders\" WHERE name=? AND parent_folder ISNULL AND author_id=?";
					
					st = db.prepareStatement(query);
					st.setString(1, name);
					st.setInt(2, user_id_in);
					
					st.executeUpdate();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}

		}
	}

	// получение пути к папке
	public String getPath(String name, Folder parentFolder, int user_id_in) {
		path = "";
		Folder fol = findFolder(name,parentFolder,user_id_in);
		if (fol == null) {
			System.out.println("Folder not found"); // выводим информирующее сообщение
			return "";
		} else {
			PathGenRec(fol);
		}
		return path;
	}

	// рекурсивное построение пути
	private void PathGenRec(Folder fol) {
		if (fol != null) {
			path = fol.getName() + "/" + path;
			PathGenRec(fol.getParentFolder());
		}
	}

	// поиск папки
	public Folder findFolder(String name ,Folder pfolder, int user_id_in) {
		// запрашиваем данные о репозиториях
		if (connection_status) {
			try {
				String query = "";
				
				PreparedStatement st;
				ResultSet rs;
				if(pfolder==null) {
					query = "SELECT * FROM \"Folders\" WHERE author_id=?";
					
					st = db.prepareStatement(query);
					st.setInt(1, user_id_in);
					
					rs = st.executeQuery();
				}else {
					query = "SELECT * FROM \"Folders\" WHERE parent_folder=? AND author_id=?";
					
					st = db.prepareStatement(query);
					st.setString(1, pfolder.getName());
					st.setInt(2, user_id_in);
					
					rs = st.executeQuery();
				}
				
				ResultSet vl = rs;
				String nm;
				String pf;
				
				while (vl.next()) {
					nm = vl.getString("name");
					pf = vl.getString("parent_folder");
					if (nm.equals(name)) {
						if (pf != null)
							return (new Folder(nm, pfolder, user_id_in));
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
	
	//получение папки из пути
	public Folder pathToFolder(String path,int user_id_in) {
		String[] folders = path.split("/");
		Folder result;
		result=findFolder(folders[0],null,user_id_in);
		for(int i=1;i<folders.length;i++) {
			String folder = folders[i];
			result=findFolder(folder,result,user_id_in);
		}
		return result;
	}
	
	// получение текущей папки
	public Folder getCurrent(int user_id_in) {
		return pathToFolder(currentDir,user_id_in);
	}
	
	// установка текущей папки
	public void setCurrent(String currentDir_in,int user_id_in) {
		final Set<Folder> FOLDERS; // множество	
		
		if(getCurrent(user_id_in)!=null) {
			if(currentDir_in.equals("..")) {
				if(getCurrent(user_id_in).getParentFolder()==null) {
					currentDir="";
				}else {
					currentDir=getPath(getCurrent(user_id_in).getParentFolder().getName(),getCurrent(user_id_in).getParentFolder().getParentFolder(),user_id_in);
				}
			}else {
				currentDir=getPath(currentDir_in,getCurrent(user_id_in),user_id_in);
			}
		}else {
			FOLDERS=getFolders(user_id_in);
			for(Folder fol:FOLDERS) {
				if((fol.getName().equals(currentDir_in))&&(fol.getParentFolder()==null)) {
					currentDir=getPath(fol.getName(),null,user_id_in);
				}
			}
		}
	}
}
