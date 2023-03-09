package main.java.medianotes.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

//класс отвечающий за аутентификацию
public class Authentication {
	// поля класса

	private static String[] vLOGINandPASSWORDandID = {}; // поле с массивом данных аккаунтов

	// методы класса

	// метод класса отвечающий за подключение к бд
	public static Connection getConnection() {
		Connection db = null; // объект подключения
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

		} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return db;
	}

	// метод класса отвечающий за запрос данных аккаунтов из бд
	public static void readAuthentificationData() {
		boolean connection_status = false; // логическая переменная хранящая статус подключения к бд
		Connection db = getConnection(); // объект подключения

		if (db != null) {
			connection_status = true;
		}
		// запрашиваем данные аккаунтов
		if (connection_status) {
			try {
				String query = "SELECT * FROM \"Auths\"";
				
				PreparedStatement st = db.prepareStatement(query);
				ResultSet rs = st.executeQuery();
				ResultSet vl = rs;
				List<String> allRows = new ArrayList<String>();
				String acc;
				String id;
				while (vl.next()) {
					acc = vl.getString("login_password");
					id = vl.getString("id");
					allRows.add(acc + " " + id);
				}
				vLOGINandPASSWORDandID = allRows.toArray(new String[0]);
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
	}

	// метод класса отвечающий за поиск введённых данных в базе
	public static int findAccount(String accdat) {
		int finded_id = -1; // переменная хранящая id результата поиска объекта
		String LOGINandPASSWORD = ""; // переменная хранящая логин и пароль аккаунта
		
		readAuthentificationData(); // читаем данные аккаунтов

		for (int i = 0; i < vLOGINandPASSWORDandID.length; i++) { // сравниваем данные аккаунтов с введёнными
			String[] words = vLOGINandPASSWORDandID[i].split(" ");
			LOGINandPASSWORD = words[0] + " " + words[1];
			if (LOGINandPASSWORD.equals(accdat)) {
				finded_id = Integer.parseInt(words[2]);
			}
		}

		return finded_id; // возвращаем результат поиска
	}

	// метод класса отвечающий за регистрацию аккаунта в бд
	/**
	 * @param accdat
	 */
	public static void registerAccount(String accdat) {
		boolean connection_status = false; // логическая переменная хранящая статус подключения к бд
		Connection db = getConnection(); // объект подключения

		if (db != null) {
			connection_status = true;
		}
		// отправляем данные аккаунта
		if (connection_status) {
			try {
				String query = "SELECT count(*) FROM \"Auths\"";
				
				PreparedStatement st1 = db.prepareStatement(query);
				ResultSet rs1 = st1.executeQuery();
				if (rs1.next()) {
					int count = rs1.getInt(1);
					query = "INSERT INTO \"Auths\" (id,login_password)VALUES (?,?)";
					PreparedStatement st2 = db.prepareStatement(query);
					st2.setInt(1, count);
					st2.setString(2,accdat);
					st2.executeUpdate();
				}
				
				db.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception a) {
				a.printStackTrace();
			}		
		}
	}
	
	// геттер базы аккаунтов
	public static String[] getAccounts() {
		readAuthentificationData(); // читаем данные аккаунтов
		return vLOGINandPASSWORDandID;
	}
}
