-- create the email template table
create TABLE email_template (
	email_template_key			int NOT NULL auto_increment,
	template_name				varchar(128) not null,
	from_address				varchar(128) not null,
	subject						varchar(128),
	body						text,
	date_created				datetime not null,
	date_updated				datetime,
	PRIMARY KEY (email_template_key)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX email_template_uix_1 on email_template (template_name);