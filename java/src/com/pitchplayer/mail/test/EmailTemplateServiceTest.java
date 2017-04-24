package com.pitchplayer.mail.test;

import java.sql.SQLException;
import java.util.List;

import com.pitchplayer.db.DbException;
import com.pitchplayer.mail.EmailTemplate;
import com.pitchplayer.mail.EmailTemplateDao;
import com.pitchplayer.mail.EmailTemplateListOrder;
import com.pitchplayer.mail.EmailTemplateService;
import com.pitchplayer.test.AbstractSpringTest;

import junit.framework.TestCase;

public class EmailTemplateServiceTest extends AbstractSpringTest  {

	private EmailTemplateService emailTemplateService;
	private EmailTemplateDao emailTemplateDao;
	
	private EmailTemplate testTemplate;
	
	private EmailTemplate createTestTemplate() throws DbException {
		EmailTemplate template = new EmailTemplate();
		template.setTemplateName("test template");
		template.setFromAddress("robert@admob.com");
		template.setSubject("testing");
		template.setMessageBody("Hello.  This is just a test message");
		emailTemplateService.createNewEmailTemplate(template);
		return template;
	}
	
	public void onTearDown() throws Exception {
		if (testTemplate != null) {
			emailTemplateService.deleteEmailTemplate(testTemplate.getEmailTemplateKey());
			testTemplate = null;
		}
	}
	
	public void testShowInfo() throws SQLException {
		emailTemplateDao.debug();
	}
	
	public void testCreateNewEmailTemplate() throws DbException {
		testTemplate = createTestTemplate();
		assertTrue(testTemplate.getEmailTemplateKey() != null);
	}

	public void testDeleteEmailTemplate() throws DbException {
		testTemplate = createTestTemplate();
		emailTemplateService.deleteEmailTemplate(testTemplate.getEmailTemplateKey());
		testTemplate = emailTemplateService.getEmailTemplate(testTemplate.getEmailTemplateKey());
		assertTrue(testTemplate == null);
	}

	public void testGetEmailTemplate() throws DbException {
		testTemplate = createTestTemplate();
		EmailTemplate copy = emailTemplateService.getEmailTemplate(testTemplate.getEmailTemplateKey());
		assertTrue(testTemplate.getEmailTemplateKey() == copy.getEmailTemplateKey() && 
				testTemplate.getSubject().equals(copy.getSubject()));
	}

	public void testSearchTemplatesByName() throws DbException {
		testTemplate = createTestTemplate();
		List<EmailTemplate> results = emailTemplateService.searchTemplatesByName("test");
		for (EmailTemplate tmpl : results) {
			if (tmpl.getEmailTemplateKey() == testTemplate.getEmailTemplateKey()) {
				assertTrue(true);
				return;
			}
		}
		assertTrue(false);
	}

	public void testUpdateEmailTemplate() throws DbException {
		testTemplate = createTestTemplate();
		testTemplate.setSubject("new subject");
		emailTemplateService.updateEmailTemplate(testTemplate);
		EmailTemplate copy = emailTemplateService.getEmailTemplate(testTemplate.getEmailTemplateKey());
		assertTrue(testTemplate.getEmailTemplateKey() == copy.getEmailTemplateKey() && 
				testTemplate.getSubject().equals(copy.getSubject()));
	}

	public void testListEmailTemplates() throws DbException {
		testTemplate = createTestTemplate();
		List<EmailTemplate> results = emailTemplateService.listTemplates(null, 0, 0, EmailTemplateListOrder.DATE_CREATED_ASC);
		assertTrue(results.get(0).getEmailTemplateKey() == testTemplate.getEmailTemplateKey());
	}
	
	public EmailTemplateService getEmailTemplateService() {
		return emailTemplateService;
	}

	public void setEmailTemplateService(EmailTemplateService emailTemplateService) {
		this.emailTemplateService = emailTemplateService;
	}

	public void setEmailTemplateDao(EmailTemplateDao emailTemplateDao) {
		this.emailTemplateDao = emailTemplateDao;
	}

}
