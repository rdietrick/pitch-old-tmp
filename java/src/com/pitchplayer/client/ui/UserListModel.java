package com.pitchplayer.client.ui;

import javax.swing.AbstractListModel;

public class UserListModel extends AbstractListModel {

	private String[] users;
	
	public UserListModel() {
		users = new String[0];
	}
	
	public UserListModel(String[] l) {
		this.users = l;
	}
	
	public int getSize() {
		return users.length;
	}

	public Object getElementAt(int index) {
		if (index < users.length) {
			return users[index];
		}
		else return null;
	}

	/**
	 * Update the list of logged in users.
	 * @param l
	 */
	public void setUserList(String[] l) {
		System.err.println("updating user list with list of size " + l.length);
		int maxVal = users.length;
		synchronized(users) {
			this.users = l;
			if (users.length > maxVal) {
				maxVal = users.length;
			}
		}
		fireContentsChanged(this, 0, maxVal-1);
	}
}
