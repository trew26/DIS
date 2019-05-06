package de.dis2011.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Makler-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE makler(id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1, NO CACHE) PRIMARY KEY,
 * name varchar(255),
 * address varchar(255),
 * login varchar(40) UNIQUE,
 * password varchar(40));
 */
public class Makler {
	private int id = -1;
	private String name;
	private String address;
	private String login;
	private String password;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Lädt einen Makler aus der Datenbank
	 * @param id ID des zu ladenden Maklers
	 * @return Makler-Instanz
	 */
	public static Makler load(int id) {
		try {
			// Hole Verbindung
			Connection con = DB2ConnectionManager.getInstance().getConnection();

			// Erzeuge Anfrage
			String selectSQL = "SELECT * FROM makler WHERE id = ?";
			PreparedStatement pstmt = con.prepareStatement(selectSQL);
			pstmt.setInt(1, id);

			// Führe Anfrage aus
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Makler ts = new Makler();
				ts.setId(id);
				ts.setName(rs.getString("name"));
				ts.setAddress(rs.getString("address"));
				ts.setLogin(rs.getString("login"));
				ts.setPassword(rs.getString("password"));

				// testing this
				System.out.println(rs.getString(1));
				System.out.println(rs.getString("name"));

				rs.close();
				pstmt.close();
				return ts;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void delete(int id) {
		try {
			// Hole Verbindung
			Connection con = DB2ConnectionManager.getInstance().getConnection();

			// Teste ob Makler mit id existiert
            String test_SQL = "SELECT * FROM makler WHERE id = ?";
            PreparedStatement test_pstmt = con.prepareStatement(test_SQL);
            test_pstmt.setInt(1, id);
            ResultSet test_rs = test_pstmt.executeQuery();
            if (!test_rs.next()){
                System.out.println("Kein Makler mit der ID " + id + " vorhanden.");
                return;
            }
            else {
                // Tatsächlies löschen
                // Erzeuge Querry
                String selectSQL = "DELETE FROM makler WHERE id = ?";
                PreparedStatement pstmt = con.prepareStatement(selectSQL);
                pstmt.setInt(1, id);

                // Führe Querry aus
                pstmt.executeUpdate();

                System.out.println("Makler mit ID " + id + " wurde gelöscht.");

            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Speichert den Makler in der Datenbank. Ist noch keine ID vergeben
	 * worden, wird die generierte Id von DB2 geholt und dem Model übergeben.
	 */
	public void save() {
		// Hole Verbindung
		Connection con = DB2ConnectionManager.getInstance().getConnection();

		try {
			// FC<ge neues Element hinzu, wenn das Objekt noch keine ID hat.
			if (getId() == -1) {
				// Achtung, hier wird noch ein Parameter mitgegeben,
				// damit spC$ter generierte IDs zurC<ckgeliefert werden!
				String insertSQL = "INSERT INTO makler(name, address, login, password) VALUES (?, ?, ?, ?)";

				PreparedStatement pstmt = con.prepareStatement(insertSQL,
						Statement.RETURN_GENERATED_KEYS);

				// Setze Anfrageparameter und fC<hre Anfrage aus
				pstmt.setString(1, getName());
				pstmt.setString(2, getAddress());
				pstmt.setString(3, getLogin());
				pstmt.setString(4, getPassword());
				pstmt.executeUpdate();

				// Hole die Id des engefC<gten Datensatzes
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					setId(rs.getInt(1));
				}

				rs.close();
				pstmt.close();
			} else {
				// Falls schon eine ID vorhanden ist, mache ein Update...
				String updateSQL = "UPDATE makler SET name = ?, address = ?, login = ?, password = ? WHERE id = ?";
				PreparedStatement pstmt = con.prepareStatement(updateSQL);

				// Setze Anfrage Parameter
				pstmt.setString(1, getName());
				pstmt.setString(2, getAddress());
				pstmt.setString(3, getLogin());
				pstmt.setString(4, getPassword());
				pstmt.setInt(5, getId());
				pstmt.executeUpdate();

				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void showAll() {
        // Hole Verbindung
        Connection con = DB2ConnectionManager.getInstance().getConnection();
        try {
            String select_sql = "SELECT * FROM makler";
            PreparedStatement pstmt = con.prepareStatement(select_sql);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Es existieren folgende Makler:");

            while (rs.next()) {
                System.out.println("Name: " + rs.getString("name") +
						", ID: " + rs.getString("id") +
						", Login: " + rs.getString("login"));
            }

            System.out.println();
        }
        catch (SQLException e) {
        e.printStackTrace();
        }

    }

    public static Boolean check_login(String login, String password) {
		// Hole Verbindung
		Connection con = DB2ConnectionManager.getInstance().getConnection();
		Boolean verified_login = false;

		try {
			// Get the ID for the login
			String get_id_sql = "Select id FROM makler where login=?";
			PreparedStatement id_pstmt = con.prepareStatement(get_id_sql);

			id_pstmt.setString(1, login);
			ResultSet id_rs = id_pstmt.executeQuery();

			if(!id_rs.next()){
				System.out.println("Es existiert kein Makler mit dem Login " + login + "!");
				verified_login = false;
				return verified_login;
			}
			else {
				int makler_id = id_rs.getInt("id");
				String select_sql = "SELECT login, password FROM makler WHERE id=?";
				PreparedStatement login_pstmt = con.prepareStatement(select_sql);

				login_pstmt.setInt(1, makler_id);
				ResultSet login_rs = login_pstmt.executeQuery();

				if(login_rs.next()){
					String db_login = login_rs.getString("login");
					String db_password = login_rs.getString("password");

					if (db_login.equals(login) && db_password.equals(password)) {
						verified_login = true;
					}
					else {
						verified_login = false;
					}
				}
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return verified_login;
	}
}
