package com.pitchplayer.mail.action;

import java.util.List;

import org.apache.log4j.Logger;

import com.pitchplayer.db.DbException;
import com.pitchplayer.mail.EmailTemplate;
import com.pitchplayer.mail.EmailTemplateListOrder;
import com.pitchplayer.mail.EmailTemplateService;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class EmailTemplateCrudAction extends ActionSupport implements Preparable {

	private EmailTemplateService emailTemplateService;
	private Integer key;
	private EmailTemplate emailTemplate;
	private String emailTemplateName = null;
	private Integer offset = 0;
	private Integer limit = 20;
	private List<EmailTemplate> searchResults = null;
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public void prepare() throws Exception {
		log.debug("prepare called with key = " + key);
		if (key != null) {
			try {
				emailTemplate = emailTemplateService.getEmailTemplate(key);
				if (emailTemplate != null) {
					log.debug("found template");
				}
				else {
					log.debug("template not found");
				}
			} catch (DbException dbe) {
				log.error("Error retrieving email template", dbe);
				throw dbe;
			}
		}
		else if (emailTemplateName != null) {
			try {
				log.debug("searching for template " + emailTemplateName);
				emailTemplate = emailTemplateService.getEmailTemplate(emailTemplateName);
				if (emailTemplate != null) {
					log.debug("found tempalte");
				}
			} catch (DbException dbe) {
				log.error("Error retrieving email template", dbe);
				throw dbe;
			}
		}
	}
	
	public String create() {
		log.debug("emailTemplate.emailTemplateKey = " + emailTemplate.getEmailTemplateKey());
		try {
			emailTemplateService.createNewEmailTemplate(emailTemplate);
		} catch (DbException dbe) {
			log.error("Error saving template", dbe);
			addActionError("Error saving template: " + dbe.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String update() {
		if (emailTemplate == null) {
			addActionError("No EmailTemplate found with key = " + key);
			return ERROR;
		}
		try {
			emailTemplateService.updateEmailTemplate(emailTemplate);
		} catch (DbException dbe) {
			log.error("Error saving template", dbe);
			addActionError("Error updating template: " + dbe.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String view() {
		if (emailTemplate == null) {
			log.error("No email template with key " + key);
			addActionError("No EmailTemplate found");
			return ERROR;
		}
		log.debug("found template " + emailTemplate.getEmailTemplateKey());
		return SUCCESS;
	}
	
	public String list() {
		if (emailTemplateService == null) {
			log.error("emailTemplateService is null");
		}
		try {
			searchResults = emailTemplateService.listTemplates(null, limit, offset, EmailTemplateListOrder.DATE_UPDATED_DESC);
		} catch (DbException dbe) {
			log.error("Error searching for email templates", dbe);
			addActionError("Unexpected error: " + dbe.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String delete() {
		if (key == null) {
			addActionError("No key specified for delete operation");
			return ERROR;
		}
		try {
			emailTemplateService.deleteEmailTemplate(key);
		} catch (DbException dbe) {
			log.error("Error deleting email template", dbe);
			addActionError("Error deleting template: " + dbe.getMessage());
			return SUCCESS;
		}
		return SUCCESS;
	}

	public void setEmailTemplateService(EmailTemplateService emailTemplateService) {
		this.emailTemplateService = emailTemplateService;
	}
	
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public List<EmailTemplate> getSearchResults() {
		return searchResults;
	}

	public String getEmailTemplateName() {
		return emailTemplateName;
	}

	public void setEmailTemplateName(String emailTemplateName) {
		this.emailTemplateName = emailTemplateName;
	}

}
