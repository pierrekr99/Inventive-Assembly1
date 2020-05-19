package Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import objekte.Auftrag;
import objekte.Komponente;
import objekte.*;

public class DB_Verbindung {

	Connection Verbindung = null;
	ResultSet rs;
	List<Auftrag> Auftragsliste = new ArrayList<>();
	List<Auftraggeber> Auftraggeberliste = new ArrayList<>();
	List<Disponent> Disponentliste = new ArrayList<>();
	
	
	public static void main (String[] args) { 
		
		DB_Verbindung test = new DB_Verbindung();
		test.verbinden();
		test.auftraggeber_einlesen();
		//test.Auftrag_einlesen();
		
	}
	

	public void verbinden() { // stellt Verbindung mit der Datenbank her
	
		String adresse = "jdbc:mysql://3.125.60.55/db2";
		String treiber = "com.mysql.cj.jdbc.Driver";
		String benutzername = "db2";
		String passwort = "!db2.winf?";

		try{
			Class.forName(treiber);
			Verbindung = DriverManager.getConnection(adresse, benutzername, passwort);
			System.out.println("Verbindung hergestellt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void trennen() {		//trennt die Verbindung mit der Datenbank

		try {
			Verbindung.close();
			System.out.println("Verbindung getrennt");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void Auftrag_einlesen() { 																 //alle Aufträge werden eingelesen
		try {
		Statement stmt = Verbindung.createStatement();
		rs = stmt.executeQuery("SELECT * FROM `auftrag`");
		
		while (rs.next()) {
			objekte.Auftrag a = new Auftrag(rs.getString("Auftragsnummer"), rs.getString("Erstellungsdatum"), rs.getString("Frist"), rs.getString("Status"), rs.getString("Zuständigkeit"), rs.getString("Auftraggeber"), null);																						
			Auftragsliste.add(a);
		}
		System.out.println("Aufträge einlesen:");
		
	}catch(Exception e) {
		e.printStackTrace();
	}
			
	}
	
	
	public void auftraggeber_einlesen() {  //Methode vum alle Auftraggeber aus der Tabelle "auftraggeber" von der Datenbank einlesen
		
		try {
			Statement stmt = Verbindung.createStatement();
			rs = stmt.executeQuery("SELECT * FROM `auftraggeber`");
			
			while (rs.next()) {
				objekte.Auftraggeber Auftraggeber = new Auftraggeber(rs.getString("Name"), rs.getString("Kundennr"));		//Ein Exemplar von Auftraggeber wird erstellt																
				Auftraggeberliste.add(Auftraggeber);
			}
			System.out.println("Auftraggeber einlesen:" + Auftraggeberliste);
	
		}catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
//	public void disponent_einlesen() {
//		
//		try {
//			Statement stmt = Verbindung.createStatement();
//			rs = stmt.executeQuery("SELECT * FROM `disponent`");
//			
//			while (rs.next()) {
//				objekte.Disponent Disponent = new Disponent(rs.getString(""));																		
//				Disponentliste.add(Disponent);
//			}
//			System.out.println("Auftraggeber einlesen:" + Auftraggeberliste);
//	
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		
//		
//	}
	public void komponente_einlesen() {
		
		
		
		
		
	}
	public void monteur_einlesen() {}
	

	
}

	
	


