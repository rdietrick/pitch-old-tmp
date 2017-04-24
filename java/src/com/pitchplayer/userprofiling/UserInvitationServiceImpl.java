package com.pitchplayer.userprofiling;

import java.util.Date;
import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import com.pitchplayer.mail.EmailTemplate;
import com.pitchplayer.mail.EmailTemplateManager;
import com.pitchplayer.userprofiling.dao.UserInvitationDao;
import com.pitchplayer.userprofiling.om.UserInvitation;

public class UserInvitationServiceImpl implements UserInvitationService {

	UserInvitationDao userInvitationDao;
	private JavaMailSender mailSender;
	private String invitationTemplateName;
	private EmailTemplateManager emailTemplateManager;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public UserInvitation getByInvitationCode(String code) {
		return userInvitationDao.getByInvitationCode(code);
	}

	public void update(UserInvitation invitation) {
		userInvitationDao.update(invitation);
	}

	public UserInvitationDao getUserInvitationDao() {
		return userInvitationDao;
	}

	public void setUserInvitationDao(UserInvitationDao userInvitationDao) {
		this.userInvitationDao = userInvitationDao;
	}

	public void sendUserInvitation(final UserInvitation invitation) throws MessagingException {
		invitation.setStatus(STATUS_SENT);
		invitation.setSentDate(new Date());
		
		HashMap rootMap = new HashMap();
		rootMap.put("user", invitation.getUser());
		rootMap.put("invitation", invitation);
		EmailTemplate emailTemplate;
		try {
			emailTemplate = emailTemplateManager.getEmailMessage(invitationTemplateName, rootMap);
		} catch (Exception e) {
			log.error("Error retrieving template", e);
			throw new MessagingException();
		}
		if (emailTemplate == null) {
			log.error("could not load template with name " + invitationTemplateName);
		}
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(invitation.getInviteeEmail());
		helper.setText(emailTemplate.getMessageBody(), true);
		helper.setFrom(emailTemplate.getFromAddress());
		helper.setSubject(emailTemplate.getSubject());
		mailSender.send(message);

		update(invitation);	
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setInvitationTemplateName(String invitationTemplateName) {
		this.invitationTemplateName = invitationTemplateName;
	}

	public void setEmailTemplateManager(EmailTemplateManager emailTemplateManager) {
		this.emailTemplateManager = emailTemplateManager;
	}

}
