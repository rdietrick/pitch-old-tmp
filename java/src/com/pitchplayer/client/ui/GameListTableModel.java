package com.pitchplayer.client.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * @author robd
 *
 */
public class GameListTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = {
		"Game Type", "Status", "P1", "P2", "P3", "P4"
	};

	String[][] gameInfo;
	
	public GameListTableModel() {
	}
	
	public GameListTableModel(String[][] gameInfo) {
		this.gameInfo = gameInfo;
	}

	public void setGameList(String[][] gameInfo) {
		this.gameInfo = gameInfo;
		TableModelEvent event = new TableModelEvent(this);
		fireTableChanged(event);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (gameInfo == null) {
			return 0;
		}
		return gameInfo.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 0) {
			return (gameInfo[rowIndex][1].equals("s")?"singles":"doubles");
		}
		else if (columnIndex == 1) {
			return (gameInfo[rowIndex][2].equals("running")?"playing":gameInfo[rowIndex][2]);
		}
		else {
			String val = gameInfo[rowIndex][columnIndex+1];
			if (val != null) {
				return val;
			}
			else if (gameInfo[rowIndex][columnIndex+1] != null && 
					!gameInfo[rowIndex][2].equals("running")) {
				return "JOIN";
			}
			else {
				return null;
			}
		}
	}

	public String getColumnName(int c) {
		return COLUMN_NAMES[c];
	}
	
	public String getGameIdForJoin(int row) {
		if (gameInfo[row][2].equals("running")) {
			return null;
		}
		return gameInfo[row][0];
	}
	
}
