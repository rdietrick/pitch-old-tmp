package com.pitchplayer.mail;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import com.pitchplayer.db.DbException;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * This class should be used to retrieve email message templates for content generation.
 * Delegates to Freemarker for template resolution.
 * @author robd
 *
 */
public class EmailTemplateManager {

	private FreeMarkerConfigurationFactory cfgFactory;
	private EmailTemplateService emailTemplateService;
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	private String process(Template tmpl, Object rootMap) throws TemplateException, IOException {
		StringWriter out = new StringWriter();
		tmpl.process(rootMap, out);
		return out.toString();
	}
	
	public EmailTemplate getEmailMessage(String templateName, Object rootMap) 
	throws IOException, TemplateException, DbException {
		EmailTemplate emailTemplate = emailTemplateService.getEmailTemplate(templateName);
		if (emailTemplate == null) {
			log.debug("could not load template " + templateName);
		}
		Template freemarkerTmpl = getFreemarkerTemplate(emailTemplate);
		emailTemplate.setMessageBody(process(freemarkerTmpl, rootMap));
		return emailTemplate;
	}
	
	Configuration lastConfig;
	
	private Template getFreemarkerTemplate(EmailTemplate emailTemplate) throws IOException, TemplateException {
		Template tmpl = null;
		Configuration cfg = cfgFactory.createConfiguration();
		
		if (cfg != null) {
			cfg.setTemplateLoader(new MultiTemplateLoader(new TemplateLoader[] {new EmailTemplateLoader(emailTemplate)}));
			tmpl = cfg.getTemplate(emailTemplate.getTemplateName());
		}
		return tmpl;
	}
	
	

	public void setEmailTemplateService(EmailTemplateService emailTemplateService) {
		this.emailTemplateService = emailTemplateService;
	}

	public void setCfgFactory(FreeMarkerConfigurationFactory cfgFactory) {
		this.cfgFactory = cfgFactory;
	}
	
}
