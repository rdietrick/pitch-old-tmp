package com.pitchplayer.mail;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.pitchplayer.db.DbException;

public class EmailTemplateServiceImpl implements EmailTemplateService {

	private EmailTemplateDao emailTemplateDao;
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	@Transactional
	public void createNewEmailTemplate(EmailTemplate emailTemplate)
			throws DbException {
		emailTemplate.setDateCreated(new Date());
		emailTemplateDao.save(emailTemplate);
	}

	@Transactional
	public void deleteEmailTemplate(Integer emailTemplateId) throws DbException {
		emailTemplateDao.delete(emailTemplateId);
	}

	@Transactional (readOnly = true)
	public EmailTemplate getEmailTemplate(Integer emailTemplateKey)
			throws DbException {
		return emailTemplateDao.findById(emailTemplateKey);
	}
	
	public EmailTemplate getEmailTemplate(String emailTemplateName)
	throws DbException {
		return emailTemplateDao.findByName(emailTemplateName);
	}


	@Transactional (readOnly = true)
	public List<EmailTemplate> searchTemplatesByName(String name)
			throws DbException {
		return emailTemplateDao.searchByName(name);
	}

	@Transactional
	public void updateEmailTemplate(EmailTemplate emailTemplate)
			throws DbException {
		emailTemplate.setDateUpdated(new Date());
		emailTemplateDao.save(emailTemplate);
	}

	@Transactional (readOnly = true)
	public List<EmailTemplate> listTemplates(Date newSince, int limit,
			int offset, EmailTemplateListOrder order) throws DbException {
		return emailTemplateDao.list(newSince, limit, offset, order);
	}

	@Transactional (readOnly = true)
	public EmailTemplate getMailMessage(String valString) throws DbException {
		List<EmailTemplate> res = searchTemplatesByName(valString);
		if (res == null || res.size() == 0) {
			return null;
		}
		else {
			return res.get(0);
		}
	}

	
	public EmailTemplateDao getEmailTemplateDao() {
		return emailTemplateDao;
	}

	public void setEmailTemplateDao(EmailTemplateDao emailTemplateDao) {
		this.emailTemplateDao = emailTemplateDao;
	}



}
