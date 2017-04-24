package com.pitchplayer.mail;

import java.util.Date;
import java.util.List;

import org.springframework.mail.SimpleMailMessage;

import com.pitchplayer.db.DbException;

public interface EmailTemplateService {

	public void createNewEmailTemplate(EmailTemplate emailTemplate) throws DbException;
	
	public void updateEmailTemplate(EmailTemplate emailTemplate) throws DbException;
	
	public EmailTemplate getEmailTemplate(Integer emailTemplateKey) throws DbException;
	
	public EmailTemplate getEmailTemplate(String emailTemplateName) throws DbException;
	
	public List<EmailTemplate> searchTemplatesByName(String name) throws DbException;
	
	public void deleteEmailTemplate(Integer emailTemplateId) throws DbException;
	
	/**
	 * List all EmailTemplates.
	 * 
	 * @param newSince if non-null, only templates created since this date will be returned
	 * @param limit max number of results to return
	 * @param offset row number to start (used in conjunction with limit for paging)
	 * @param order determines whether results are ordered in descending (<0) or ascending (>=0) order according to date_created
	 * @return a List<EmailTemplate> 
	 * @throws DbException
	 */
	public List<EmailTemplate> listTemplates(Date newSince, int limit, int offset, EmailTemplateListOrder order) throws DbException;

	/**
	 * Retrieve an EmailTemplate by name and load a SimpleMailMessage from it.
	 * @param valString the name of the EmailTemplate.
	 * @return
	 * @throws DbException 
	 */
	public EmailTemplate getMailMessage(String valString) throws DbException;

	

}
