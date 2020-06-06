package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import Datenbank.datenbankVerbindung;
import gui.DisponentFenster.JButtonEditor;
import gui.DisponentFenster.JButtonRenderer;
import objekte.Auftrag;
import objekte.Mitarbeiter;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AuftraegeListeFenster extends JFrame {

	static datenbankVerbindung db = main.Main.getdb();

	private JPanel contentPane;
	private JTable tabelle;
	private int zeilenTabelle = 0;
	int summeAuftraege = 0;
	private int zeile = 0;
	Mitarbeiter monteure;

	/**
	 * Launch the application.
	 */
	/*
	 * public static void main(String[] args) { EventQueue.invokeLater(new
	 * Runnable() { public void run() { try { AuftraegeListeFenster frame = new
	 * AuftraegeListeFenster(db.getMonteurListe().get(1)); frame.setVisible(true); }
	 * catch (Exception e) { e.printStackTrace(); } } }); }
	 */
	/**
	 * Create the frame.
	 */
	public AuftraegeListeFenster(Mitarbeiter monteur) {
		monteure = monteur;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1200, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		tabelle = new JTable();
		scrollPane.setViewportView(tabelle);
		tabelle.setCellSelectionEnabled(true);// Einzelne Zellen k�nnen ausgew�hlt werden
		tabelle.setFont(new Font("Tahoma", Font.PLAIN, 18));// Schriftart und -gr��e in der Tabelle
		tabelle.getTableHeader().setFont(new Font("Tahoma", Font.PLAIN, 22));
		tabelleAktualisieren(monteur); // Erstellen/aktualisieren der Auftragstabelle -> mehr Details in der Methode

		tabelle.setAutoCreateRowSorter(true);// durch Anklicken der Kopfzeile (in der jeweiligen Spalte) werden die
												// Auftr�ge nach diesem Attribut
												// in der nat�rlichen Ordnung und umgekehrt sortiert
		/*
		 * tabelle.addMouseListener(new MouseAdapter() {// MouseListener f�r das Fenster
		 * public void mouseClicked(MouseEvent e) { if (e.MOUSE_PRESSED == 501) {// Wenn
		 * die Maus Gedr�ckt wird (Beim Dr�cken die Maus bewegen z�hlt nicht // dazu)
		 * JTable target = (JTable) e.getSource(); int row = target.getSelectedRow();//
		 * wo wurde geklickt int column = target.getSelectedColumn(); // do some action
		 * if appropriate column if (column == 0) {// wenn in DetailsSpalte //
		 * detailsFenster();//Detailsfenster wird ge�ffnet DetailsFenster frame = new
		 * DetailsFenster(row); // reihe des Auftrags wird �bergeben um details aufrufen
		 * zu k�nnen frame.setVisible(true); } } } });
		 */
		monteureTblFormat();

		this.setTitle(monteur.getName() + ", " + monteur.getVorname());

	}

	private void monteureTblFormat() {
		tabelle.getColumnModel().getColumn(0).setPreferredWidth(150);
		tabelle.getColumnModel().getColumn(0).setMinWidth(150);
		tabelle.getColumnModel().getColumn(0).setMaxWidth(150);

		tabelle.getColumnModel().getColumn(1).setPreferredWidth(100);
		tabelle.getColumnModel().getColumn(1).setMinWidth(200);
		tabelle.getColumnModel().getColumn(1).setMaxWidth(250);

		tabelle.getColumnModel().getColumn(2).setPreferredWidth(100);
		tabelle.getColumnModel().getColumn(2).setMinWidth(150);
		tabelle.getColumnModel().getColumn(2).setMaxWidth(250);

		tabelle.getColumnModel().getColumn(3).setPreferredWidth(100);
		tabelle.getColumnModel().getColumn(3).setMinWidth(200);
		tabelle.getColumnModel().getColumn(3).setMaxWidth(200);

		tabelle.getColumnModel().getColumn(2).setPreferredWidth(100);
		tabelle.getColumnModel().getColumn(2).setMinWidth(200);
		tabelle.getColumnModel().getColumn(2).setMaxWidth(200);

		tabelle.getColumnModel().getColumn(3).setPreferredWidth(100);
		tabelle.getColumnModel().getColumn(3).setMinWidth(200);
		tabelle.getColumnModel().getColumn(3).setMaxWidth(200);

		tabelle.setRowHeight(50);
	}

	private Object[][] tabelle(Mitarbeiter monteur) {
		int zeile = 0;
		for (int i = 0; i < db.getMonteurListe().size(); i++) {
			if (richtigerMonteur(i)) {
				zeilenTabelle = summeAuftraege(monteur);
			}
		}
		Object[][] auftraege = new Object[zeilenTabelle][6];
		for (int i = 0; i < db.getAuftragsListe().size(); i++) {
			if (richtigerAuftrag(i)) {
				auftraege[zeile][0] = "Details";
				auftraege[zeile][1] = db.getAuftragsListe().get(i).getAuftragsNummer();
				auftraege[zeile][2] = db.getAuftragsListe().get(i).getStatus();
				auftraege[zeile][3] = db.getAuftragsListe().get(i).getErstellungsdatum();
				auftraege[zeile][4] = db.getAuftragsListe().get(i).getFrist();
				auftraege[zeile][5] = db.getAuftragsListe().get(i).getAuftraggeber().getKundenNummer();
				zeile++;
			}
		}
		zeile = 0;
		zeilenTabelle = 0;
		return auftraege;
	}

	private void tabelleAktualisieren(Mitarbeiter monteur) {

		tabelle.setModel(new DefaultTableModel(tabelle(monteur), // Ben�tigter Inhalt: (String[][],String[])
				// Sonst wird hier ein eigenes Modell Eingef�gt
				new String[] { "", "AuftragsNummer", "Status", "Erstellungsdatum", "Frist", "Auftragsgeber" }) {
			boolean[] columnEditables = new boolean[] { // welche spalten lassen sich �ndern
					true, false, false, false, false, false };

			public boolean isCellEditable(int row, int column) {// kontrollmethode ob spalten sich �ndern lassen
				return columnEditables[column];
			}
		});
		tabelle.getColumn(tabelle.getColumnName(0)).setCellRenderer(new JButtonRenderer());
		tabelle.getColumn(tabelle.getColumnName(0)).setCellEditor(new JButtonEditor());
	}

	private boolean richtigerAuftrag(int i) {
		return monteure.getMitarbeiterNummer()
				.equals(db.getAuftragsListe().get(i).getZustaendig().getMitarbeiterNummer());
	}

	private boolean richtigerMonteur(int i) {
		return monteure.getMitarbeiterNummer()
				.equals(db.getAuftragsListe().get(i).getZustaendig().getMitarbeiterNummer());
	}

	private int summeAuftraege(Mitarbeiter monteur) {// z�hlt die zugeh�rigen Auftr�ge des Monteurs
		int summe;
		for (int j = 0; j < db.getAuftragsListe().size(); j++) {
			if (db.getAuftragsListe().get(j).getZustaendig().getMitarbeiterNummer()
					.equals(monteur.getMitarbeiterNummer())) {
				/*
				 * Hier wird die MitarbeiterNummer des Zust�ndigen Mitarbeiter in einem Auftrag
				 * mit der Mitarbeiter einse Mitarbeiters aus Der Datenbank Verglichen und wenn
				 * diese �bereinstimmen wird Hochgez�hlt.
				 */
				summeAuftraege = summeAuftraege + 1;
			}
		}
		summe = summeAuftraege;
		summeAuftraege = 0;
		return summe;
	}

	/**
	 * Buttons in der Tabelle
	 */

	class JButtonRenderer implements TableCellRenderer {

		JButton button = new JButton();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			table.setShowGrid(true);
			table.setGridColor(Color.LIGHT_GRAY);
			button.setText("Details anzeigen");
			return button;
		}
	}

	class JButtonEditor extends AbstractCellEditor implements TableCellEditor {
		JButton button;
		String txt;

		public JButtonEditor() {
			super();
			button = new JButton();
			button.setOpaque(true);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Auftrag auftrag = welcherAuftrag(tabelle.getEditingRow());
					DetailsFenster frame = new DetailsFenster(auftrag);
					frame.setVisible(true);
					tabelleAktualisieren(monteure);
				}
			});
		}

		private Auftrag welcherAuftrag(int editingRow) {
			for (Auftrag auftrag : db.getAuftragsListe()) {

				if (tabelle.getValueAt(editingRow, 1).equals(auftrag.getAuftragsNummer())) {
					return auftrag;
				}
			}
			return null;
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			txt = (value == null) ? "" : value.toString();
			button.setText(txt);
			return button;
		}
	}
}