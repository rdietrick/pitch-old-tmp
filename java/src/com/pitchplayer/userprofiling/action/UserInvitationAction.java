package com.pitchplayer.userprofiling.action;

import com.opensymphony.xwork2.Validateable;
import com.pitchplayer.action.BaseAction;
import com.pitchplayer.userprofiling.UserInvitationService;
import com.pitchplayer.userprofiling.om.User;
import com.pitchplayer.userprofiling.om.UserInvitation;
import com.pitchplayer.userprofiling.util.UserInvitationUtil;

public class UserInvitationAction extends BaseAction implements Validateable {

	UserInvitationService userInvitationService;
	private String invitees;

	private String status = null;
	private String ic = null;
	private UserInvitation userInvitation;
	
	public UserInvitationAction() {
		super();
	}
	
	public UserInvitationService getUserInvitationService() {
		return userInvitationService;
	}

	public void setUserInvitationService(UserInvitationService userInvitationService) {
		this.userInvitationService = userInvitationService;
	}
	

	public String handleViewInvitation() {
		log.debug("handleViewInvitation() called");
		if (ic == null) {
			log.error("handleViewInvitation called without invication code ('ic') parameter.");
			return ERROR;
		}
		try {
			userInvitation = userInvitationService.getByInvitationCode(ic);
		} catch (Exception e) {
			log.error("Error retrieving invitation code", e);
			return ERROR;
		}
		if (userInvitation != null) {
			log.debug("invitation found");
		}
		return SUCCESS;
	}
	
	public String handleSendInvitation() {
		log.debug("handleSendInvitation invoked; invitees = " + invitees);
		String[] addresses;
		try {
			addresses = UserInvitationUtil.parseEmailAddresses(invitees);
		} catch (IllegalArgumentException iae) {
			this.addFieldError("invitees", iae.getMessage());
			return ERROR;
		}
		log.debug("parsed " + addresses.length + " addresses");
		User sender = this.getSessionUser();
		StringBuffer badAddresses = new StringBuffer();
		for (String address : addresses) {
			UserInvitation invitation = new UserInvitation();
			invitation.setUser(sender);
			invitation.setInvitationCode(UserInvitationUtil.generateInvitationCode(address));
			invitation.setInviteeEmail(address);
			try {
				userInvitationService.sendUserInvitation(invitation);
			} catch (Exception e) {
				log.debug("messaging exception", e);
				badAddresses.append(address).append(" ");
				this.addActionError("Message could not be sent to " + address);
			}
		}
		if (this.hasErrors()) {
			invitees = badAddresses.toString();
			return ERROR;
		}
		else {
			invitees = null;
			status = "SUCCESS";
		}
		return SUCCESS;
	}

	public String getInvitees() {
		return invitees;
	}

	public void setInvitees(String invitees) {
		this.invitees = invitees;
	}

	public String getStatus() {
		return status;
	}

	public String getIc() {
		return ic;
	}

	public void setIc(String ic) {
		this.ic = ic;
	}

	public UserInvitation getUserInvitation() {
		return userInvitation;
	}
	
		
}
