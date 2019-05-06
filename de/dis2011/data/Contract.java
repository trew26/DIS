package de.dis2011.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Contract {
    private int id = -1;
    private int person;
    private int duration;
    private int additional_cost;
    private int appartment;
    private int installment;
    private int rate;
    private int house;

    private String first_name;
    private String last_name;
    private String address;
    private String place;
    private String startdate;
    private String contractdate;

    private int getId() {return id;}
    private int getPerson() {return person;}
    private int getDuration() {return duration;}
    private int getAdditionalCost() {return additional_cost;}
    private int getAppartment() {return appartment;}
    private int getInstallment() {return installment;}
    private int getRate() {return rate;}
    private int getHouse() {return house;}
    private String getFirst_name() {return first_name;}
    private String getLast_name() {return last_name;}
    private String getAddress() {return address;}
    private String getPlace() {return place;}
    private String getStartdate() {return startdate;}
    private String getContractdate() {return contractdate;}

    public void setId(int id) {this.id = id; }
    public void setPerson(int person) {
        this.person = person;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public void setAdditionalCost(int additional_cost) {
        this.additional_cost = additional_cost;
    }
    public void setAppartment(int appartment) {
        this.appartment = appartment;
    }
    public void setInstallment(int installment) {
        this.installment = installment;
    }
    public void setRate(int rate) {
        this.rate = rate;
    }
    public void setHouse(int house) {
        this.house = house;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }
    public void setContractdate(String contract) {
        this.contractdate = contract;
    }

    public void savePerson() {
        // Hole Verbindung
        Connection con = DB2ConnectionManager.getInstance().getConnection();

        try {
            // FC<ge neues Element hinzu, wenn das Objekt noch keine ID hat.
            if (getId() == -1) {
                // Achtung, hier wird noch ein Parameter mitgegeben,
                // damit spC$ter generierte IDs zurC<ckgeliefert werden!
                String insertSQL = "INSERT INTO person(first_name, last_name, address) VALUES (?, ?, ?)";

                PreparedStatement pstmt = con.prepareStatement(insertSQL,
                        Statement.RETURN_GENERATED_KEYS);

                // Setze Anfrageparameter und fC<hre Anfrage aus
                pstmt.setString(1, getFirst_name());
                pstmt.setString(2, getLast_name());
                pstmt.setString(3, getAddress());
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
                String updateSQL = "UPDATE person SET first_name = ?, last_name = ?, address = ? WHERE id = ?";
                PreparedStatement pstmt = con.prepareStatement(updateSQL);

                // Setze Anfrage Parameter
                pstmt.setString(1, getFirst_name());
                pstmt.setString(2, getLast_name());
                pstmt.setString(3, getAddress());
                pstmt.setInt(4, getId());
                pstmt.executeUpdate();

                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveContract() {
        // Hole Verbindung
        Connection con = DB2ConnectionManager.getInstance().getConnection();

        try {
            // FC<ge neues Element hinzu, wenn das Objekt noch keine ID hat.
            if (getId() == -1) {
                // Achtung, hier wird noch ein Parameter mitgegeben,
                // damit spC$ter generierte IDs zurC<ckgeliefert werden!
                String insertSQL = "INSERT INTO contract(date, place, fk_person_id) VALUES (?, ?, ?)";

                PreparedStatement pstmt = con.prepareStatement(insertSQL,
                        Statement.RETURN_GENERATED_KEYS);

                // Setze Anfrageparameter und fC<hre Anfrage aus
                pstmt.setString(1, getContractdate());
                pstmt.setString(2, getPlace());
                pstmt.setInt(3, getPerson());
                String date = getContractdate();
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
                String updateSQL = "UPDATE contract SET date = ?, place = ?, fk_person_id = ? WHERE id = ?";
                PreparedStatement pstmt = con.prepareStatement(updateSQL,
                        Statement.RETURN_GENERATED_KEYS);

                // Setze Anfrage Parameter
                pstmt.setString(1, getContractdate());
                pstmt.setString(2, getPlace());
                pstmt.setInt(3, getPerson());
                pstmt.setInt(4, getId());

                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveTenancy(){
        // Hole Verbindung
        Connection con = DB2ConnectionManager.getInstance().getConnection();

        try {            // Teste ob Estate mit id existiert
            String test_SQL = "SELECT * FROM tenancies WHERE fk_apartment_id = ?";
            PreparedStatement test_pstmt = con.prepareStatement(test_SQL);
            test_pstmt.setInt(1, getAppartment());
            ResultSet test_rs = test_pstmt.executeQuery();
            if (test_rs.next()){
                System.out.println("Appartment mit der ID " + getAppartment() + " hat bereits einen Vertrag.");
                test_pstmt.close();
                test_rs.close();
                return;
            }else {


                saveContract();

                String insertSQL = "INSERT INTO tenancies(START_DATE, DURATION, ADDITIONAL_COSTS, FK_CONTRACT_NUMBER, FK_APARTMENT_ID) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement insert_pstmt = con.prepareStatement(insertSQL);

                // Setze Anfrageparameter und fC<hre Anfrage aus
                insert_pstmt.setString(1, getStartdate());
                insert_pstmt.setInt(2, getDuration());
                insert_pstmt.setInt(3, getAdditionalCost());
                insert_pstmt.setInt(4, getId());
                insert_pstmt.setInt(5, getAppartment());

                insert_pstmt.executeUpdate();
                insert_pstmt.close();
            }
                } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void savePurchase(){
        // Hole Verbindung
        Connection con = DB2ConnectionManager.getInstance().getConnection();

        try {
            // Teste ob Estate mit id existiert
            String test_SQL = "SELECT * FROM purchase WHERE FK_HOUSE_ID = ?";
            PreparedStatement test_pstmt = con.prepareStatement(test_SQL);
            test_pstmt.setInt(1, getHouse());
            ResultSet test_rs = test_pstmt.executeQuery();
            if (test_rs.next()){
                System.out.println("Haus mit der ID " + getHouse() + " hat bereits einen Vertrag.");
                test_pstmt.close();
                test_rs.close();
                return;}
            else{

                saveContract();

            String insertSQL = "INSERT INTO purchase(FK_HOUSE_ID, FK_CONTRACT_NUMBER, INSTALLMENTS, RATE) VALUES (?, ?, ?, ?)";

            PreparedStatement insert_pstmt = con.prepareStatement(insertSQL);

            // Setze Anfrageparameter und fC<hre Anfrage aus
            insert_pstmt.setInt(1, getHouse());
            insert_pstmt.setInt(2, getId());
            insert_pstmt.setInt(3, getInstallment());
            insert_pstmt.setInt(4, getRate());

            insert_pstmt.executeUpdate();
            insert_pstmt.close();

        }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void showContracts(){
        // Hole Verbindung
        Connection con = DB2ConnectionManager.getInstance().getConnection();
        try {
            // Teste ob Estate mit id existiert
            String test_SQL = "SELECT * FROM contract";
            PreparedStatement test_pstmt = con.prepareStatement(test_SQL);
            ResultSet rs = test_pstmt.executeQuery();
            while(rs.next()) {
                System.out.println("Number: " + rs.getString("number") +
                        ", Date: " + rs.getString("Date") +
                        ", Ort: " + rs.getString("place") +
                        ", Person " + rs.getString("fk_person_id"));
            }

            test_pstmt.close();
            rs.close();
            System.out.println();

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
