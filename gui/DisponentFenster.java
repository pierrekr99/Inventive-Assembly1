package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import Datenbank.datenbankVerbindung;
import objekte.Auftrag;
import objekte.Mitarbeiter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class DisponentFenster extends JFrame {

	static datenbankVerbindung db = main.Main.getdb();
	// erm�glicht auf den Inhalt der DB, die in der Main geladen wurde

	private JPanel contentPane;
	private JTextField txtSuche;
	private JTable auftraegeTbl;
	private JTable archivTbl;
	private JTable monteureTbl;
	private JScrollPane auftraegeSp;
	private JScrollPane archivSp;
	public int indexWochentag = 0;

	Object[][] auftraege;
	int zeilen = 0;
	private Object[][] archiv;
	private int zeilenArchiv = 0;
	int zeilenMonteure = 0;

	int summeAuftraege = 0;
	String details = "Details anzeigen";
	String monteur;

	JComboBox monteureCombobox = new JComboBox();
	JComboBox DatumCBox;
	TableColumn monteureColumn;
	private ArrayList<Instant> auftragsDaten = new ArrayList<Instant>();
	private ArrayList<Auftrag> archivListe = new ArrayList<Auftrag>();

	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { DisponentFenster frame = new
	 * DisponentFenster(); frame.setExtendedState(JFrame.MAXIMIZED_BOTH);// Fenster
	 * frame.setVisible(true); } catch (Exception e) { e.printStackTrace(); } } });
	 * }
	 */

	/**
	 * Create the frame.
	 */
	public DisponentFenster() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1047, 515);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		txtSuche = new JTextField();
		txtSuche.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txtSuche.setText("Suche");
		txtSuche.setColumns(10);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				if (tabbedPane.getSelectedComponent() == auftraegeSp) {
					DatumCBox.setVisible(false);
				} else {
					DatumCBox.setVisible(true);
				}

			}
		});

		JButton logoutKnopf = new JButton("Logout");// Logout schlie�t das fenster und �ffnet das LoginFenster
		logoutKnopf.setFont(new Font("Tahoma", Font.PLAIN, 16));
		logoutKnopf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginFenster login = new LoginFenster();
				login.setVisible(true);
				login.setLocationRelativeTo(null);
				dispose();
			}
		});

		JButton dbAktualisierenKnopf = new JButton("Aktualisieren");

		dbAktualisierenKnopf.setFont(new Font("Tahoma", Font.PLAIN, 16));
		dbAktualisierenKnopf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				statusAktualisieren();
				/*
				 * Jeder Status wird bei Knopfdruck �berpr�ft (alle Verf�gbarkeiten der Teile
				 * werden �berpr�ft) und ggf. �berschrieben
				 */

				monteureInArrayEinlesen();
				/*
				 * die aktuelle Tabelle wird in db.getAuftragsListe() eingelesen, dieser wird
				 * ggf. ein neuer Monteur zugewiesen (stimmt dann wieder mit der Tabelle ein)
				 */

				db.einlesen();

				auftraegeAktualisieren();
				/*
				 * Tabelle wird graphisch aktualisiert, Mitarbeiternummer wird bei Austausch des
				 * Monteurs automatisch mit�berschrieben, auch der Status wird �berpr�ft
				 */

				monteureAktualisieren();
				/*
				 * Tabelle wird graphisch aktualisiert, die Summe der Auftr�ge eines Monteurs
				 * passt sich an die neuen Zahlen an
				 */

				sortierenAuftraege(); // sortieren nach Auftragsnummer

			}
		});

		datumBefuellen();
		// Bef�llt die datumComboBox

		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(txtSuche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, 396, Short.MAX_VALUE)
										.addComponent(DatumCBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addGap(18).addComponent(dbAktualisierenKnopf).addGap(18)
										.addComponent(logoutKnopf))
								.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 1001, Short.MAX_VALUE))
						.addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
				.createSequentialGroup().addContainerGap()
				.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtSuche, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(logoutKnopf).addComponent(dbAktualisierenKnopf).addComponent(DatumCBox,
										GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE))
						.addComponent(DatumCBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE).addGap(6)));

		/**
		 * Auftraege Reiter.==================================================
		 */

		auftraegeSp = new JScrollPane();
		tabbedPane.addTab("Auftr�ge", null, auftraegeSp, null);

		auftraegeTbl = new JTable();
		auftraegeSp.setViewportView(auftraegeTbl);
		auftraegeTbl.setCellSelectionEnabled(true);
		// Einzelne Zellen k�nnen ausgew�hlt werden

		auftraegeTbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
		// Schriftart und -gr��e in der Tabelle

		auftraegeTbl.getTableHeader().setFont(new Font("Tahoma", Font.PLAIN, 22));
		// Schriftart und -gr��e in der Kopfzeile der Tabelle

		auftraegeAktualisieren();
		// Erstellen/aktualisieren der Auftragstabelle -> mehr Details in der Methode

		sortierenAuftraege();

		/*
		 * durch Anklicken der Kopfzeile (in der jeweiligen Spalte) werden die Auftr�ge
		 * nach diesem Attribut in der nat�rlichen Ordnung und umgekehrt sortiert
		 */



		monteureCombobox();
		// Bef�llt die monteureCombobox

		/**
		 * Archiv Reiter.==================================================
		 */

		archivSp = new JScrollPane();
		tabbedPane.addTab("Archiv", null, archivSp, null);

		archivTbl = new JTable();

		archivSp.setViewportView(archivTbl);
		archivTbl.setCellSelectionEnabled(true);
		// Einzelne Zellen k�nnen ausgew�hlt werden

		archivTbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
		// Schriftart und -gr��e in der Tabelle

		archivTbl.getTableHeader().setFont(new Font("Tahoma", Font.PLAIN, 22));
		// Schriftart und -gr��e in der Kopfzeile der Tabelle

		archivAktualisieren();
		// Erstellen/aktualisieren der Auftragstabelle -> mehr Details in der Methode

		sortierenArchiv();
		/*
		 * durch Anklicken der Kopfzeile (in der jeweiligen Spalte) werden die Auftr�ge
		 * nach diesem Attribut in der nat�rlichen Ordnung und umgekehrt sortiert
		 */

		


		/**
		 * Monteure Reiter.==================================================
		 */
		JScrollPane monteureSp = new JScrollPane();
		tabbedPane.addTab("Monteure", null, monteureSp, null);

		monteureTbl = new JTable();
		monteureSp.setViewportView(monteureTbl);
		monteureTbl.setCellSelectionEnabled(true);
		// Einzelne Zellen k�nnen ausgew�hlt werden

		monteureTbl.setFont(new Font("Tahoma", Font.PLAIN, 18));
		// Schriftart und -gr��e in der Tabelle

		monteureTbl.getTableHeader().setFont(new Font("Tahoma", Font.PLAIN, 22));
		// Schriftart und -gr��e in der Kopfzeile der Tabelle

		monteureAktualisieren();
		// Erstellen/aktualisieren der Monteurtabelle -> mehr Details in der Methode

		monteureTblFormat();
		// Monteure Tabelle wird formatiert

		auftraegeTblFormat();
		// Auftr�ge Tabelle wird formatiert

		monteureTbl.setAutoCreateRowSorter(true);
		/*
		 * durch Anklicken der Kopfzeile (in der jeweiligen Spalte) werden die Monteure
		 * nach diesem Attribut in der nat�rlichen Ordnung und umgekehrt sortiert
		 */

		contentPane.setLayout(gl_contentPane);
		// Group-Layout im contentPane wird festgelegt

	}

	/**
	 * GUI-Hilfsmethoden.==================================================
	 */


	private void sortierenAuftraege() {
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) auftraegeTbl.getModel());
		auftraegeTbl.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>(); //
		
		int columnIndexForAuftragsNummer = 1;
		sortKeys.add(new RowSorter.SortKey(columnIndexForAuftragsNummer, SortOrder.ASCENDING));
		 
		int columnIndexForStatus = 2;
		sortKeys.add(new RowSorter.SortKey(columnIndexForStatus, SortOrder.ASCENDING));

		int columnIndexToSortDatum = 4;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSortDatum, SortOrder.ASCENDING)); // beschreibt die
																						// Sortierreihenfolge in einer
																						// Spalte �ber ColumnIndex

		int columnIndexToSortDatum1 = 3;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSortDatum1, SortOrder.ASCENDING)); // beschreibt die
																						// Sortierreihenfolge in einer
																						// Spalte �ber ColumnIndex
		int columnIndexForMonteur = 5;
		sortKeys.add(new RowSorter.SortKey(columnIndexForMonteur, SortOrder.ASCENDING));
		 
		int columnIndexForMitarbeiterNummer = 6;
		sortKeys.add(new RowSorter.SortKey(columnIndexForMitarbeiterNummer, SortOrder.ASCENDING));
		
		int columnIndexForKundenNummer = 7;
		sortKeys.add(new RowSorter.SortKey(columnIndexForKundenNummer, SortOrder.ASCENDING));
		 

		sorter.setComparator(columnIndexToSortDatum, ((String datum1, String datum2) -> { // Erzeugen eines Comparators,der
																						// ausgew�hlte Spalte sortiert
			String[] datumGetrennt1 = datum1.split("\\."); // Datum-String wird in 3 Teile geteilt
			String[] datumGetrennt2 = datum2.split("\\.");
			if (datumGetrennt1.length != datumGetrennt2.length) // Daten werden miteinander verglichen, ob sie die Selbe
																// L�nge besitzen
				throw new ClassCastException();
			String datumZusammengesetzt1 = datumGetrennt1[2] + datumGetrennt1[1] + datumGetrennt1[0]; // Datum wird
																										// zusammengesetzt
			String datumZusammengesetzt2 = datumGetrennt2[2] + datumGetrennt2[1] + datumGetrennt2[0];

			return datumZusammengesetzt1.compareTo(datumZusammengesetzt2); // Ordnen der Daten �ber CompareTo-Methode

		}));

		sorter.setComparator(columnIndexToSortDatum1, ((String datum1, String datum2) -> {
			String[] datumGetrennt1 = datum1.split("\\.");
			String[] datumGetrennt2 = datum2.split("\\.");
			if (datumGetrennt1.length != datumGetrennt2.length)
				throw new ClassCastException();
			String datumZusammengesetzt1 = datumGetrennt1[2] + datumGetrennt1[1] + datumGetrennt1[0];
			String datumZusammengesetzt2 = datumGetrennt2[2] + datumGetrennt2[1] + datumGetrennt2[0];

			return datumZusammengesetzt1.compareTo(datumZusammengesetzt2);

		}));

		sorter.setSortKeys(sortKeys);
		sorter.sort();
	}

	private void sortierenArchiv() {
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) archivTbl.getModel());
		archivTbl.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> sortKeysArchiv = new ArrayList<>(); //

		int columnIndexForAuftragsNummer = 1;
		sortKeysArchiv.add(new RowSorter.SortKey(columnIndexForAuftragsNummer, SortOrder.ASCENDING));
		 
		int columnIndexForStatus = 2;
		sortKeysArchiv.add(new RowSorter.SortKey(columnIndexForStatus, SortOrder.ASCENDING));

		int columnIndexToSortArchiv = 4;
		sortKeysArchiv.add(new RowSorter.SortKey(columnIndexToSortArchiv, SortOrder.ASCENDING)); // beschreibt die
																						// Sortierreihenfolge in einer
																						// Spalte �ber ColumnIndex

		int columnIndexToSortArchiv1 = 3;
		sortKeysArchiv.add(new RowSorter.SortKey(columnIndexToSortArchiv1, SortOrder.ASCENDING)); // beschreibt die
																						// Sortierreihenfolge in einer
																						// Spalte �ber ColumnIndex
		
		int columnIndexForMonteur = 5;
		sortKeysArchiv.add(new RowSorter.SortKey(columnIndexForMonteur, SortOrder.ASCENDING));
		 
		int columnIndexForMitarbeiterNummer = 6;
		sortKeysArchiv.add(new RowSorter.SortKey(columnIndexForMitarbeiterNummer, SortOrder.ASCENDING));
		
		int columnIndexForKundenNummer = 7;
		sortKeysArchiv.add(new RowSorter.SortKey(columnIndexForKundenNummer, SortOrder.ASCENDING));

		sorter.setComparator(columnIndexToSortArchiv, ((String datum1, String datum2) -> { // Erzeugen eines Comparators,der
																						// ausgew�hlte Spalte sortiert
			String[] datumGetrennt1 = datum1.split("\\."); // Datum-String wird in 3 Teile geteilt
			String[] datumGetrennt2 = datum2.split("\\.");
			if (datumGetrennt1.length != datumGetrennt2.length) // Daten werden miteinander verglichen, ob sie die Selbe
																// L�nge besitzen
				throw new ClassCastException();
			String datumZusammengesetzt1 = datumGetrennt1[2] + datumGetrennt1[1] + datumGetrennt1[0]; // Datum wird
																										// zusammengesetzt
			String datumZusammengesetzt2 = datumGetrennt2[2] + datumGetrennt2[1] + datumGetrennt2[0];

			return datumZusammengesetzt1.compareTo(datumZusammengesetzt2); // Ordnen der Daten �ber CompareTo-Methode

		}));

		sorter.setComparator(columnIndexToSortArchiv1, ((String datum1, String datum2) -> {
			String[] datumGetrennt1 = datum1.split("\\.");
			String[] datumGetrennt2 = datum2.split("\\.");
			if (datumGetrennt1.length != datumGetrennt2.length)
				throw new ClassCastException();
			String datumZusammengesetzt1 = datumGetrennt1[2] + datumGetrennt1[1] + datumGetrennt1[0];
			String datumZusammengesetzt2 = datumGetrennt2[2] + datumGetrennt2[1] + datumGetrennt2[0];

			return datumZusammengesetzt1.compareTo(datumZusammengesetzt2);

		}));

		sorter.setSortKeys(sortKeysArchiv);
		sorter.sort();
	}

	private void auftraegeAktualisieren() {

		// DefaultTableModel(Tabelle,Kopfzeile){z.B. was ist editierbar?}
		auftraegeTbl.setModel(new DefaultTableModel(auftraege(), new String[] { "", "AuftragsNummer", "Status",
				"Erstellungsdatum", "Frist", "MonteurName", "MonteurNummer", "Auftragsgeber" }) {

			boolean[] columnEditables = new boolean[] { true, false, false, false, false, true, false, false };
			// welche spalten lassen sich �ndern
		});

		auftraegeTbl.getColumn(auftraegeTbl.getColumnName(0)).setCellRenderer(new JButtonRenderer("auftraegeTbl"));
		// ButtonRenderer wird in Spalte 0 ausgef�hrt

		auftraegeTbl.getColumn(auftraegeTbl.getColumnName(0)).setCellEditor(new JButtonEditor());
		// ButtonEditorwird in Spalte 0 ausgef�hrt

		auftraegeTblFormat();
		// Tabelle wird formatiert

		monteureCombobox();
		// monteureCombobox wird konfiguriert (muss bei jeder Aktualisierung geschehen)
	}

	private void archivAktualisieren() {

		// DefaultTableModel(Tabelle,Kopfzeile){z.B. was ist editierbar?}
		archivTbl.setModel(new DefaultTableModel(archiv(), new String[] { "", "AuftragsNummer", "Status",
				"Erstellungsdatum", "Frist", "MonteurName", "MonteurNummer", "Auftragsgeber" }) {

			boolean[] columnEditables = new boolean[] { true, false, false, false, false, true, false, false };
			// welche spalten lassen sich �ndern
		});

//		archivTbl.getColumn(archivTbl.getColumnName(0)).setCellRenderer(new JButtonRenderer("archivTbl"));
		// ButtonRenderer wird in Spalte 0 ausgef�hrt

//		archivTbl.getColumn(archivTbl.getColumnName(0)).setCellEditor(new JButtonEditor());
		// ButtonEditorwird in Spalte 0 ausgef�hrt

		archivTblFormat();
		// Tabelle wird formatiert
	}

	private void monteureAktualisieren() {

		// DefaultTableModel(Tabelle,Kopfzeile){z.B. was ist editierbar?}
		monteureTbl.setModel(new DefaultTableModel(monteure(),
				new String[] { "Name", "MitarbeiterNummer", "Verf�gbarkeit", "Auftraege" }) {

			boolean[] columnEditables = new boolean[] { false, false, false, true };
			// welche spalten lassen sich �ndern
		});

		monteureTbl.getColumn(monteureTbl.getColumnName(3)).setCellRenderer(new JButtonRenderer("monteureTbl"));
		// ButtonRenderer wird in Spalte 3 ausgef�hrt

		monteureTbl.getColumn(monteureTbl.getColumnName(3)).setCellEditor(new JButtonEditor());
		// ButtonEditorwird in Spalte 3 ausgef�hrt

		monteureTblFormat();
		// Tabelle wird formatiert
	}

	public Object[][] auftraege() {
		// Erstellt Inhalt zur bef�llung der auftraegeTabelle

		zeilen = db.getAuftragsListe().size();
		// gr��e der Tabelle wird ermittelt

		auftraege = new Object[zeilen][8];
		// dieses Array wird die Tabelle bef�llen

		for (int i = 0; i < db.getAuftragsListe().size(); i++) {
			auftraege[i][0] = details;
			auftraege[i][1] = db.getAuftragsListe().get(i).getAuftragsNummer();
			// AuftragsNummer

			auftraege[i][2] = db.getAuftragsListe().get(i).getStatus();
			// Status

			auftraege[i][3] = db.getAuftragsListe().get(i).getErstellungsdatum();
			// Erstellungsdatum

			auftraege[i][4] = db.getAuftragsListe().get(i).getFrist();
			// Frist

			auftraege[i][5] = "";
			// MitarbeiterName (Name Vorname) wenn kein Monteur zugwiesen ist

			auftraege[i][6] = "";
			// MitarbeiterNummer wenn kein Monteur zugwiesen ist

			auftraege[i][7] = db.getAuftragsListe().get(i).getAuftraggeber().getKundenNummer();
			// Auftraggeber

			if (db.getAuftragsListe().get(i).getZustaendig() != null
					&& db.getAuftragsListe().get(i).getZustaendig() != null
					&& !db.getAuftragsListe().get(i).getZustaendig().getMitarbeiterNummer().equals("0000")) {
				// ist ein Monteur zust�ndug?

				auftraege[i][5] = db.getAuftragsListe().get(i).getZustaendig().getName() + ", "
						+ db.getAuftragsListe().get(i).getZustaendig().getVorname();
				// MitarbeiterName (Name, Vorname)

				auftraege[i][6] = db.getAuftragsListe().get(i).getZustaendig().getMitarbeiterNummer();
				// MitarbeiterNummer

			} else if (db.getAuftragsListe().get(i).getZustaendig().getMitarbeiterNummer().equals("0000")) {
				auftraege[i][5] = db.getAuftragsListe().get(i).getZustaendig().getName() + " "
						+ db.getAuftragsListe().get(i).getZustaendig().getVorname();
				// "nicht zugewiesen"
			}
			auftraege[i][6] = db.getAuftragsListe().get(i).getZustaendig().getMitarbeiterNummer();
			// MitarbeiterNummer

		}
		return auftraege;
	}

	public Object[][] archiv() {
		// Erstellt Inhalt zur bef�llung der auftraegeTabelle

		try {
			Date date = java.util.Calendar.getInstance().getTime();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");

			String dateString = dateFormatter.format(date);
			System.out.println("ds" + dateString);
			Instant grenze = new SimpleDateFormat("dd.MM.yyyy").parse(dateString).toInstant();
			System.out.println("grenze" + grenze);

			for (Auftrag auftrag : db.getAuftragsListe()) {
				Instant auftragsfrist = new SimpleDateFormat("dd.MM.yyyy").parse(auftrag.getFrist()).toInstant();

				if (auftragsfrist.isBefore(grenze) && auftrag.getStatus().equals("im Lager")) {
					archivListe.add(auftrag);
				}

			}

			zeilenArchiv = archivListe.size();
			// gr��e der Tabelle wird ermittelt

		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Auftrag auftrag34 : archivListe) {
			System.out.println(auftrag34);
		}

		archiv = new Object[zeilenArchiv][8];
		// dieses Array wird die Tabelle bef�llen

		for (int i = 0; i < archivListe.size(); i++) {
			archiv[i][0] = details;
			archiv[i][1] = archivListe.get(i).getAuftragsNummer();
			// AuftragsNummer

			archiv[i][2] = archivListe.get(i).getStatus();
			// Status

			archiv[i][3] = archivListe.get(i).getErstellungsdatum();
			// Erstellungsdatum

			archiv[i][4] = archivListe.get(i).getFrist();
			// Frist

			archiv[i][5] = "";
			// MitarbeiterName (Name Vorname) wenn kein Monteur zugwiesen ist

			archiv[i][6] = "";
			// MitarbeiterNummer wenn kein Monteur zugwiesen ist

			archiv[i][7] = archivListe.get(i).getAuftraggeber().getKundenNummer();
			// Auftraggeber

			if (archivListe.get(i).getZustaendig() != null && archivListe.get(i).getZustaendig() != null
					&& !archivListe.get(i).getZustaendig().getMitarbeiterNummer().equals("0000")) {
				// ist ein Monteur zust�ndug?

				archiv[i][5] = archivListe.get(i).getZustaendig().getName() + ", "
						+ archivListe.get(i).getZustaendig().getVorname();
				// MitarbeiterName (Name, Vorname)

				archiv[i][6] = archivListe.get(i).getZustaendig().getMitarbeiterNummer();
				// MitarbeiterNummer
			}
		}
		return archiv;
	}

	private Object[][] monteure() {
		// Erstellt Inhalt zur bef�llung der monteureTabelle

		zeilenMonteure = db.getMonteurListe().size() - 1;
		// gr��e der Tabelle wird ermittelt

		Object[][] monteure = new Object[zeilenMonteure][4];
		// dieses Array wird die Tabelle bef�llen

		for (int i = 0; i < db.getMonteurListe().size(); i++) {
			if (!db.getMonteurListe().get(i).getMitarbeiterNummer().equals("0000")) {
				monteure[i][0] = db.getMonteurListe().get(i).getName() + " " + db.getMonteurListe().get(i).getVorname();
				// MitarbeiterName (Name Vorname)

				monteure[i][1] = db.getMonteurListe().get(i).getMitarbeiterNummer();
				// MitarbeiterNummer

				if (indexWochentag <= 4) {
					// f�r Montag bis Freitag

					monteure[i][2] = db.getMonteurListe().get(i).getAnwesenheit().get(indexWochentag);
					// hier wird nur noch die Anwesenheit am jeweiligen Tag eingetragen

				} else {
					// Samstag und Sonntag wird die komplette Liste angezeigt
					monteure[i][2] = db.getMonteurListe().get(i).getAnwesenheit();
				}

				monteure[i][3] = "Auftr�ge anzeigen [" + summeAuftraege(db.getMonteurListe().get(i)) + "]";
				// Summe der Auftr�ge
			}
		}
		return monteure;
	}

	private void auftraegeTblFormat() {
		// Details
		auftraegeTbl.getColumnModel().getColumn(0).setPreferredWidth(150);
		auftraegeTbl.getColumnModel().getColumn(0).setMinWidth(150);
		auftraegeTbl.getColumnModel().getColumn(0).setMaxWidth(150);

		// Auftragsnummer
		auftraegeTbl.getColumnModel().getColumn(1).setPreferredWidth(100);
		auftraegeTbl.getColumnModel().getColumn(1).setMinWidth(100);
		auftraegeTbl.getColumnModel().getColumn(1).setMaxWidth(250);

		// Status
		auftraegeTbl.getColumnModel().getColumn(2).setPreferredWidth(100);
		auftraegeTbl.getColumnModel().getColumn(2).setMinWidth(100);
		auftraegeTbl.getColumnModel().getColumn(2).setMaxWidth(200);

		// Erstellungsdatum
		auftraegeTbl.getColumnModel().getColumn(3).setPreferredWidth(100);
		auftraegeTbl.getColumnModel().getColumn(3).setMinWidth(100);
		auftraegeTbl.getColumnModel().getColumn(3).setMaxWidth(250);

		// Frist
		auftraegeTbl.getColumnModel().getColumn(4).setPreferredWidth(100);
		auftraegeTbl.getColumnModel().getColumn(4).setMinWidth(100);
		auftraegeTbl.getColumnModel().getColumn(4).setMaxWidth(250);

		// MonteurName
		auftraegeTbl.getColumnModel().getColumn(5).setPreferredWidth(100);
		auftraegeTbl.getColumnModel().getColumn(5).setMinWidth(100);
		auftraegeTbl.getColumnModel().getColumn(5).setMaxWidth(500);

		// MonteurNummer
		auftraegeTbl.getColumnModel().getColumn(6).setPreferredWidth(100);
		auftraegeTbl.getColumnModel().getColumn(6).setMinWidth(100);
		auftraegeTbl.getColumnModel().getColumn(6).setMaxWidth(200);

		// Aufraggeber
		auftraegeTbl.getColumnModel().getColumn(7).setPreferredWidth(100);
		auftraegeTbl.getColumnModel().getColumn(7).setMinWidth(100);
		auftraegeTbl.getColumnModel().getColumn(7).setMaxWidth(200);

		// Zeilenh�he
		auftraegeTbl.setRowHeight(50);

		// Nur lesen nicht schreiben
		auftraegeTbl.getTableHeader().setReorderingAllowed(false);
	}

	private void archivTblFormat() {

		// Zeilenh�he
		archivTbl.setRowHeight(50);

		// Nur lesen nicht schreiben
		archivTbl.getTableHeader().setReorderingAllowed(false);
	}

	private void monteureTblFormat() {
		// Name
		monteureTbl.getColumnModel().getColumn(0).setPreferredWidth(150);
		monteureTbl.getColumnModel().getColumn(0).setMinWidth(100);
		monteureTbl.getColumnModel().getColumn(0).setMaxWidth(500);

		// MitarbeiterNummer
		monteureTbl.getColumnModel().getColumn(1).setPreferredWidth(100);
		monteureTbl.getColumnModel().getColumn(1).setMinWidth(100);
		monteureTbl.getColumnModel().getColumn(1).setMaxWidth(400);

		// Verf�gbarkeit
		monteureTbl.getColumnModel().getColumn(2).setPreferredWidth(100);
		monteureTbl.getColumnModel().getColumn(2).setMinWidth(100);
		monteureTbl.getColumnModel().getColumn(2).setMaxWidth(500);

		// AuftragsNummer
		monteureTbl.getColumnModel().getColumn(3).setPreferredWidth(100);
		monteureTbl.getColumnModel().getColumn(3).setMinWidth(100);
		monteureTbl.getColumnModel().getColumn(3).setMaxWidth(500);

		// Zeilenh�he
		monteureTbl.setRowHeight(50);

		// Nur lesen nicht schreiben
		monteureTbl.getTableHeader().setReorderingAllowed(false);
	}

	private void monteureCombobox() {
		// F�gt Optionen zur Statusver�nderung hinzu

		monteureCombobox.setFont(new Font("Tahoma", Font.PLAIN, 18));
		// Schriftart und Gr��e

		monteureColumn = auftraegeTbl.getColumnModel().getColumn(5);
		// eine bestimmte Spalte f�r Combobox ausw�hlen

		monteureColumn.setCellEditor(new DefaultCellEditor(monteureCombobox));
		// in die Spalte die Combobox einbinden

		monteureCombobox.addActionListener(null);
		// zugewiesenen Monteur auslesen und in Datenbank zuweisung �ndern

		monteureCombobox.removeAllItems();
		// monteureCombobox wird geleert vor Bef�llung

		for (int i = 0; i < db.getMonteurListe().size(); i++) {

			if (db.getMonteurListe().get(i).getMitarbeiterNummer().equals("0000")) {
				monteureCombobox.addItem(
						db.getMonteurListe().get(i).getName() + " " + db.getMonteurListe().get(i).getVorname());
				// wenn kein Monteur dem Auftrag zugewiesen ist, wird die Combobox an dieser
				// Stelle mit "nicht (Nachname) zugewiesen (Vorname)" bef�llt

			} else {
				monteureCombobox.addItem(
						db.getMonteurListe().get(i).getName() + ", " + db.getMonteurListe().get(i).getVorname());
				// Name, Vorname
			}
		}
		Collections.sort(db.getMonteurListe(), new Comparator<Mitarbeiter>() {
			// sortiert die monteureCombobox

			@Override
			public int compare(Mitarbeiter o1, Mitarbeiter o2) {

				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	/**
	 * Buttons in der Tabelle.==================================================
	 */

	class JButtonRenderer implements TableCellRenderer {
		JButton button = new JButton();
		String tabelle;
		// in welcher Tabelle ist der Button

		public JButtonRenderer(String string) {
			this.tabelle = string;
		}

		// Wie soll der Button ausehen und was soll drin stehen
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			table.setShowGrid(true);
			table.setGridColor(Color.LIGHT_GRAY);
			button.setText(details);
			if (tabelle.equals("monteureTbl"))
				// wird ein knopf in der Monteur Tabelle gedr�ckt?

				button.setText("Auftr�ge anzeigen [" + summeAuftraege(welcherMonteur(row)) + "]");
			// Text im Button wird festgelegt und anzahl der Aufr�ge f�r die der Monteur
			// zust�ndig ist wird gez�hlt

			return button;
		}
	}

	class JButtonEditor extends AbstractCellEditor implements TableCellEditor {
		JButton button;

		public JButtonEditor() {
			super();
			button = new JButton();
			button.setOpaque(true);
			// ist der Button undurchsichtig oder nicht

			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// was passiert wenn man den Button anklickt

					if (button.getText().equals(details)) {
						// wird der deteilsButton gedr�ckt?

						Auftrag auftrag = welcherAuftrag(auftraegeTbl.getEditingRow());
						// welcher Auftrag wird in der Zeile des geklickten Buttons angezeigt

						if (auftrag != null) {
							// es existiert ein Auftrag in der Zeile

							DetailsFenster frame = new DetailsFenster(auftrag);
							frame.setVisible(true);
							// DetailsFenster wird ge�ffnet und der angezeigte Auftrag wird ihm mitgegeben

							auftraegeAktualisieren();
							// Tabelle wird neu geladen, damit der Button wieder erscheint
						}

					} else {
						Mitarbeiter monteur = welcherMonteur(monteureTbl.getEditingRow());
						if (summeAuftraege(monteur).equals("0")) {
							// ist der der Monteur f�r 0 Auftr�ge zust�ndig?

							JOptionPane nichtZugewiesen = new JOptionPane();
							nichtZugewiesen.showMessageDialog(null, "keine Auftr�ge zugewiesen");
							// Monteur ist f�r keinen Auftrag zust�ndig -> Warnung

							return;
							// Button wird vorzeitig beendet
						}

						// Monteur existiert und ist f�r mindestens einen Auftrag zust�ndig

						AuftraegeListeFenster frame = new AuftraegeListeFenster(monteur);
						frame.setVisible(true);
						// AuftraegeListeFenster und der Monteur

						monteureAktualisieren();
						// Tabelle wird neu geladen, damit der Button wieder erscheint
					}
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			// Button wird nach klick wieder beschriftet

			button.setText(details);
			if (table == monteureTbl) {
				// die �bergebene Tabelle ist die MonteurTabelle

				button.setText("Auftr�ge anzeigen [" + summeAuftraege(welcherMonteur(row)) + "]");
				// Text im Button wird festgelegt und anzahl der Aufr�ge f�r die der Monteur
				// zust�ndig ist wird gez�hlt
			}
			return button;
		}
	}

	/**
	 * funktionale Hilfsmethoden.==================================================
	 */

	private String summeAuftraege(Mitarbeiter monteur) {
		// z�hlt die Auftr�ge f�r die der Monteur Zust�ndig ist

		String summe;
		for (int j = 0; j < db.getAuftragsListe().size(); j++) {
			if (monteur != null
					&& db.getAuftragsListe().get(j).getZustaendig().getMitarbeiterNummer()
							.equals(monteur.getMitarbeiterNummer())
					&& !db.getAuftragsListe().get(j).getStatus().equals("im Lager")) {
				// Zust�ndiger Monteur = Monteur in der MonteurListe?
				// Auftr�ge, welche bereits abgeschlossen/ im Lager sind, z�hlen nicht mehr in
				// die Auftragssumme des einzelnen Monteurs

				summeAuftraege = summeAuftraege + 1;
			}
		}
		summe = "" + summeAuftraege;
		summeAuftraege = 0;
		// SummeAuftraege wird zur�ckgesetzt

		return summe;
	}

	private Auftrag welcherAuftrag(int editingRow) {
		for (Auftrag auftrag : db.getAuftragsListe()) {

			if (auftraegeTbl.getValueAt(editingRow, 1).equals(auftrag.getAuftragsNummer())) {
				return auftrag;
			}
		}
		return null;
	}

	private Mitarbeiter welcherMonteur(int editingRow) {
		for (Mitarbeiter monteur : db.getMonteurListe()) {

			if (monteureTbl.getValueAt(editingRow, 1).equals(monteur.getMitarbeiterNummer())) {
				return monteur;
			}
		}
		return null;
	}

	private void monteureInArrayEinlesen() {

		for (int i = 0; i < zeilen; i++) {// jede Tabellenzeile wird mit dieser for-Schleife durchlaufen

			for (Auftrag auftrag : db.getAuftragsListe()) {

				if (auftraegeTbl.getValueAt(i, 1).equals(auftrag.getAuftragsNummer())) {
					// vergleicht Auftragsnummer aus Tabellenzeile mit Auftragsnummer in der
					// db.getAuftragsliste();

					String ausgewaehlterMonteur = auftraegeTbl.getValueAt(i, 5).toString();
					// der in der Combobox ausgew�hlte Monteur wird in einen String umgewandelt und
					// die Combobox somit eliminiert

					String[] namentrennung;
					// Trennung in Vor- [1] und Nachname [0] des Monteurs

					if (auftraegeTbl.getValueAt(i, 5).equals("nicht zugewiesen")) {
						namentrennung = ausgewaehlterMonteur.split(" ");
						// falls der Auftrag "nicht" zugewiesen ist, muss ein anderes Splitverfahren
						// verwendet werden

					} else {
						namentrennung = ausgewaehlterMonteur.split(", ");
						// Trennung in Vor- [1] und Nachname [0] des Monteurs

					}

					if (!namentrennung[0].equals(auftrag.getZustaendig().getName())) {
						// vergleicht den zust�ndigen Monteur (anhand des Nachnamens) aus dem Auftrag
						// aus der Tabellenzeile mit dem
						// gleichen Auftrag aus der db.getAuftragsliste();

						for (Mitarbeiter monteur : db.getMonteurListe()) {

							if (monteur.getName().equals(namentrennung[0])) {
								// sollte der Monteur nicht �bereinstimmen (er wurde also vom Diponenten
								// ge�ndert), dann wird die db.getMonteurliste() mit einer for-each Schleife
								// durchlaufen und der Nachnam des Monteurs (aus der Tabelle) mit den Nachnamen
								// aus der Monteurliste verglichen

								auftrag.setZustaendig(monteur);
								// wenn in der Monteurliste der passende Monteur gefunden wurde (d.h. er stimmt
								// mit dem Monteur aus der Tabelle �berein), dann wird der jeweilige Auftrag
								// �berschrieben und bekommt den neuen Monteur zugewiesen (dies geschieht mit
								// dem Setter)

								db.setZustaendig(auftrag, monteur); // auftrag bekommt neuen Monteur zugewie�en in der
																	// Datenbank

							}
							int verfuegbareKomponenten = (int) auftrag.getKomponenten().stream()
									.filter((k) -> k.isVerfuegbarkeit()).count();
							// der gerade ge�nderte Auftrag wird nun auch nochmal auf seinen Status
							// �berpr�ft. Hierf�r werden die verf�gbaren Komponenten gez�hlt (mittels eines
							// Streams)

							if (verfuegbareKomponenten == 5) {

								auftrag.setStatus("disponiert");
								// wenn die Anzahl der verf�gbaren Komponenten genau 5 betr�gt, sind alle
								// relevanten Bauteile vorhanden und der Status wird ggf. in der
								// db.getAuftragsliste() auf "disponiert" gesetzt (falls er noch auf "Teile
								// fehlen" gesetzt ist)
								// Anmerkung: Diese Methode ist auch nochmal als eigene Methode vorzufinden,
								// allerdings hat der Disponent hier die M�glichkeit, einen Auftrag, welcher
								// "aus Versehen" im Lager gelandet ist, wieder einem Monteur zuweisen und der
								// Auftragsstatus wird dann wieder ge�ndert.

								db.setStatus(auftrag, "disponiert"); // Ver�ndert den Status in der Datenbank

							} else if (verfuegbareKomponenten != 5) {
								auftrag.setStatus("Teile fehlen");
								// wenn die Anzahl der Teile kleiner als 5 ist, hei�t das im Umkehrschluss, dass
								// mind. ein Teil nicht verf�gbar ist und somit wird der Auftragsstatus auf
								// "Teile fehlen" gesetzt.

								db.setStatus(auftrag, "Teile fehlen"); // Ver�ndert den Status in der Datenbank

							}
							if (auftrag.getZustaendig().getMitarbeiterNummer().equals("0000")) {
								auftrag.setStatus("nicht zugewiesen");
								// wenn kein Monteur einem Auftrag zugewiesen ist, wird der Status auf nicht
								// zugewiesen gestellt. Anmerkung: Diese Methode ist auch nochmal als eigene
								// Methode vorzufinden,
								// allerdings hat der Disponent hier die M�glichkeit, einen Auftrag, welcher
								// "aus Versehen" im Lager gelandet ist, wieder einem Monteur zuweisen und der
								// Auftragsstatus wird dann wieder ge�ndert.

								db.setStatus(auftrag, "nicht zugewiesen"); // Ver�ndert den Status in der Datenbank
							}

						}
					}
					;
				}

			}
		}
	}

	private void statusAktualisieren() {
		for (Auftrag auftrag : db.getAuftragsListe()) {

			int verfuegbareKomponenten = (int) auftrag.getKomponenten().stream().filter((k) -> k.isVerfuegbarkeit())
					.count();
			// der gerade ge�nderte Auftrag wird nun auch nochmal auf seinen Status
			// �berpr�ft. Hierf�r werden die verf�gbaren Komponenten gez�hlt (mittels eines
			// Streams)

			if (verfuegbareKomponenten == 5 && !auftrag.getStatus().equals("im Lager")
					&& !auftrag.getZustaendig().getMitarbeiterNummer().equals("0000")) {
				auftrag.setStatus("disponiert");
				// wenn die Anzahl der verf�gbaren Komponenten genau 5 betr�gt (und der Auftrag
				// nicht "im Lager" ist), sind alle
				// relevanten Bauteile vorhanden und der Status wird ggf. in der
				// db.getAuftragsliste() auf "disponiert" gesetzt (falls er noch auf "Teile
				// fehlen" gesetzt ist)

				db.setStatus(auftrag, "disponiert"); // Ver�ndert den Status in der Datenbank

			} else if (verfuegbareKomponenten != 5 && !auftrag.getStatus().equals("im Lager")
					&& !auftrag.getZustaendig().getMitarbeiterNummer().equals("0000")) {
				auftrag.setStatus("Teile fehlen");
				// wenn die Anzahl der Teile kleiner als 5 ist (und der Auftrag nicht "im Lager"
				// ist, hei�t das im Umkehrschluss, dass mind. ein Teil nicht verf�gbar ist und
				// somit wird der Auftragsstatus auf "Teile fehlen" gesetzt.

				db.setStatus(auftrag, "Teile fehlen"); // Ver�ndert den Status in der Datenbank

			} else if (auftrag.getZustaendig().getMitarbeiterNummer().equals("0000")) {
				auftrag.setStatus("nicht zugewiesen");
				// wenn kein Monteur einem Auftrag zugewiesen ist, wird der Status auf nicht
				// zugewiesen gestellt

				db.setStatus(auftrag, "nicht zugewiesen"); // Ver�ndert den Status in der Datenbank
			}
		}
	}

	private void datumBefuellen() {
		// ComboBox um das Datum auw�hlen zu k�nnen

		DateFormat f = new SimpleDateFormat("EEEE, dd.MM.yyyy");
		// EEEE steht f�r den Wochentag

		Calendar c = Calendar.getInstance();

		Date datum = new Date();

		String tag1 = datumAlsStringBekommen(datum);

		c.setTime(datum);
		c.add(Calendar.DATE, 1);
		datum = c.getTime();

		String tag2 = f.format(datum);

		c.setTime(datum);
		c.add(Calendar.DATE, 1);
		datum = c.getTime();

		String tag3 = f.format(datum);

		c.setTime(datum);
		c.add(Calendar.DATE, 1);
		datum = c.getTime();

		String tag4 = f.format(datum);

		c.setTime(datum);
		c.add(Calendar.DATE, 1);
		datum = c.getTime();

		String tag5 = f.format(datum);

		String[] Datum = { tag1, tag2, tag3, tag4, tag5 };

		DatumCBox = new JComboBox(Datum);
		DatumCBox.setFont(new Font("Tahoma", Font.PLAIN, 18));
		DatumCBox.setSelectedIndex(0);

		DatumCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String ausgewaeltesDatum = (String) DatumCBox.getSelectedItem();
				// liest Datum als String aus

				String[] ausgewaelterWochentag = ausgewaeltesDatum.split(",");
				// wochentag und datum wird getrennt

				String s = ausgewaelterWochentag[0];
				// nur der wochentag wird in s gespeichert

				switch (s) {
				/*
				 * index f�r wochentag. wird ben�tigt um verf�gbarkeit der monteure aufrufen zu
				 * k�nnen
				 */

				case "Montag":
					indexWochentag = 0;
					break;
				case "Dienstag":
					indexWochentag = 1;
					break;
				case "Mittwoch":
					indexWochentag = 2;
					break;
				case "Donnerstag":
					indexWochentag = 3;
					break;
				case "Freitag":
					indexWochentag = 4;
					break;
				case "Samstag":
					indexWochentag = 5;
					break;
				case "Sonntag":
					indexWochentag = 6;
					break;
				default:
					indexWochentag = 6;
					break;
				}

				monteureAktualisieren();

			}
		});

	}

	private String datumAlsStringBekommen(Date date) {
		// gibt heutiges Datum zur�ck

		DateFormat f = new SimpleDateFormat("EEEE, dd.MM.yyyy");
		// EEEE steht f�r den Wochentag
		return f.format(date);
	}

}
