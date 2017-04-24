/**
 * 
 */
package com.pitchplayer.client.ui;


import java.util.List;
import java.util.Iterator;


import javax.swing.table.DefaultTableModel;

/**
 * @author robd
 *
 */
public class ScoringTableModel extends DefaultTableModel {

	String[] players = {" ", " ", " ", " "};
	
	public ScoringTableModel() {
		addRow((String[])null);
	}
	
	
	public void setPlayers(List names) {
		int i = 0;
		if (getRowCount() == 0) {
			addRow((Object[])null);
		}
		for (Iterator it = names.iterator();it.hasNext();) {
			players[i] = (String)it.next();
			setValueAt("11", 0, i++);
		}
		fireTableStructureChanged();
	}
	
	public void reinit() {
		players = new String[] {" ", " ", " ", " "};
		for (int i=getRowCount()-1;getRowCount() > 0;i--) {
			removeRow(i);
		}
		fireTableStructureChanged();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 4;
	}
	
	public String getColumnName(int c) {
		return players[c];
	}
	
	/**
	 * Update the score model to reflect the new scores.
	 * Just adds a new row to the table containing the specified values.
	 * @param scores
	 */
	public void updateScores(String[] scores) {
		int lastRow = getRowCount()-1;
		for (int i=0;i<4;i++) {
			String val = (String) getValueAt(lastRow, i);
			if (val != null) {
				int spaceIndex = val.indexOf(" ");
				if (spaceIndex > 0) {
					setValueAt(val.substring(0, spaceIndex), lastRow, i);
				}
			}
		}
		addRow(scores);
	}

}
