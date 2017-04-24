package com.pitchplayer.mail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import freemarker.cache.TemplateLoader;

public class EmailTemplateLoader implements TemplateLoader {
	
	private EmailTemplate emailTemplate;
	
	public EmailTemplateLoader(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
		// don't need to do anything
	}

	public Object findTemplateSource(String name) throws IOException {
		return emailTemplate;
	}

	public long getLastModified(Object templateSource) {
		return emailTemplate.getDateUpdated().getTime();
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return new StringReader(emailTemplate.getMessageBody());
	}


}
