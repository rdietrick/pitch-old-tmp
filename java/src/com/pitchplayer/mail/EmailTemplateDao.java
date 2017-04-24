package com.pitchplayer.mail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pitchplayer.db.DbException;

public class EmailTemplateDao extends JdbcDaoSupport {
	
	private Logger log = Logger.getLogger(this.getClass().getName());
	
	public void debug() throws SQLException {
		log.debug("data source = " + this.getDataSource().toString());
		log.debug("data source class = " + getDataSource().getClass());
		log.debug("catalog = " + getDataSource().getConnection().getCatalog());
	}
	
	// email_template DDL here for reference:
//	create TABLE email_template (
//			email_template_key			int NOT NULL auto_increment,
//			template_name				varchar(128) not null,
//			from_address				varchar(128) not null,
//			subject						varchar(128),
//			body						text,
//			PRIMARY KEY (email_template_key)
//		)  ENGINE=InnoDB DEFAULT CHARSET=utf8;
	
	public void save(EmailTemplate emailTemplate) throws DbException {
		if (emailTemplate.getEmailTemplateKey() == null) {
			saveNew(emailTemplate);
		}
		else {
			update(emailTemplate);
		}
	}

	private static final String UPDATE_SQL = 
		"update email_template set template_name = ?, from_address = ?, subject = ?, body = ?, date_updated = ? "
		+ "where email_template_key = ?";

	private void update(final EmailTemplate emailTemplate) throws DbException {
		try {
			this.getJdbcTemplate().update(UPDATE_SQL, new PreparedStatementSetter() {

				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, emailTemplate.getTemplateName());
					ps.setString(2, emailTemplate.getFromAddress());
					ps.setString(3, emailTemplate.getSubject());
					ps.setString(4, emailTemplate.getMessageBody());
					ps.setTimestamp(5, new Timestamp(emailTemplate.getDateUpdated().getTime()));
					ps.setInt(6, emailTemplate.getEmailTemplateKey());
				}

			});
		} catch (DataAccessException dae) {
			throw new DbException("Error updating EmailTemplate", dae);
		}
	}

	private static final String INSERT_SQL = 
		"insert into email_template (template_name, from_address, subject, body, date_created, date_updated) values (?,?,?,?,?,?)";
	
	private void saveNew(final EmailTemplate emailTemplate) throws DbException {
		KeyHolder kh = new GeneratedKeyHolder();
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {

				public PreparedStatement createPreparedStatement(Connection conn)
				throws SQLException {
					PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
					ps.setString(1, emailTemplate.getTemplateName());
					ps.setString(2, emailTemplate.getFromAddress());
					ps.setString(3, emailTemplate.getSubject());
					ps.setString(4, emailTemplate.getMessageBody());
					if (emailTemplate.getDateCreated() != null) {
						ps.setTimestamp(5, new Timestamp(emailTemplate.getDateCreated().getTime()));
					}
					else {
						ps.setTimestamp(5, new Timestamp(new Date().getTime()));
					}
					ps.setTimestamp(6, new Timestamp(new Date().getTime()));
					return ps;
				}

			}, kh);
		} catch (DataAccessException dae) {
			throw new DbException("Error saving EmailTemplate", dae);
		}
		emailTemplate.setEmailTemplateKey(((Long) kh.getKey()).intValue());
	}

	private static final String DELETE_SQL = 
		"delete from email_template where email_template_key = ?";

	public void delete(final Integer emailTemplateId) throws DbException {
		int rowCount = 0;
		try {
			rowCount = getJdbcTemplate().update(DELETE_SQL, new PreparedStatementSetter() {

				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setInt(1, emailTemplateId);
				}

			});
		} catch (DataAccessException dae) {
			throw new DbException("Error deleting EmailTemplate", dae);
		}
		if (rowCount < 1) {
			throw new DbException("Could not delete EmailTemplte with email_template_key = " + emailTemplateId);
		}
		else if (rowCount > 1) {
			throw new DbException("More than one EmailTemplate deleted with email_template_key = " + emailTemplateId);
		}
	}

	private static final String FIND_BY_ID_SQL = 
		"select * from email_template where email_template_key = ?";
	
	@SuppressWarnings("unchecked")
	public EmailTemplate findById(final Integer emailTemplateKey) throws DbException {
		List<EmailTemplate> results = null; 
		try {
			results = getJdbcTemplate().query(FIND_BY_ID_SQL, new PreparedStatementSetter() {

				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setInt(1, emailTemplateKey);
				}

			}, new EmailTemplateRowMapper());
		} catch (DataAccessException dae) {
			throw new DbException("Error querying for EmailTemplate", dae);
		}
		if (results != null && results.size() > 0) {
			return (EmailTemplate)results.get(0);
		}
		else {
			return null;
		}
	}
	
	private static final String FIND_BY_NAME_SQL = 
		"select * from email_template where template_name = ?";
	
	public EmailTemplate findByName(final String emailTemplateName) throws DbException {
		List<EmailTemplate> results = null; 
		try {
			results = getJdbcTemplate().query(FIND_BY_NAME_SQL, new PreparedStatementSetter() {

				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, emailTemplateName);
				}

			}, new EmailTemplateRowMapper());
		} catch (DataAccessException dae) {
			throw new DbException("Error querying for EmailTemplate", dae);
		}
		if (results != null && results.size() > 0) {
			return (EmailTemplate)results.get(0);
		}
		else {
			return null;
		}
	}


	private static final String SEARCH_BY_NAME_SQL =
		"select * from email_template where template_name like ?";
		
	public List<EmailTemplate> searchByName(final String name) throws DbException {
		try {
		return getJdbcTemplate().query(SEARCH_BY_NAME_SQL, new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, "%" + name + "%");
			}}, 
			new EmailTemplateRowMapper());
		} catch (DataAccessException dae) {
			throw new DbException("Error searching EmailTemplates", dae);
		}
	}
	
	
	
	
	private static class EmailTemplateRowMapper implements RowMapper {

		public Object mapRow(ResultSet rs, int index) throws SQLException {
			EmailTemplate tmpl = new EmailTemplate();
			tmpl.setEmailTemplateKey(rs.getInt("email_template_key"));
			tmpl.setTemplateName(rs.getString("template_name"));
			tmpl.setFromAddress(rs.getString("from_address"));
			tmpl.setSubject(rs.getString("subject"));
			tmpl.setMessageBody(rs.getString("body"));
			tmpl.setDateCreated(rs.getTimestamp("date_created"));
			tmpl.setDateUpdated(rs.getTimestamp("date_updated"));
			return tmpl;
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<EmailTemplate> list(final Date newSince, final int limit, final int offset, final EmailTemplateListOrder order) throws DbException {
		StringBuilder query = new StringBuilder();
		query.append("select * from email_template ");
		if (newSince != null) {
			query.append(" where date_created >= ? ");
		}
		switch (order) {
		case DATE_CREATED_ASC:
			query.append("order by date_created asc ");
			break;
		case DATE_CREATED_DESC:
			query.append("order by date_created desc ");
			break;
		case DATE_UPDATED_ASC:
			query.append("order by date_updated asc ");
			break;
		case DATE_UPDATED_DESC:
			query.append("order by date_updated desc ");
			break;
		case NAME:
			query.append("order by template_name asc ");
			break;
		}
		if (limit > 0) {
			query.append(" limit ? ");
		}
		if (offset > 0) {
			query.append(" offset ? ");
		}
		try {
		return getJdbcTemplate().query(query.toString(), new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				int i=1;
				if (newSince != null) {
					ps.setTimestamp(i++, new Timestamp(newSince.getTime()));
				}
				if (limit > 0) {
					ps.setInt(i++, limit);
				}
				if (offset > 0) {
					ps.setInt(i++, offset);
				}
			}
			
		}, new EmailTemplateRowMapper());
		} catch (DataAccessException dae) {
			throw new DbException("Error searching for EmailTemplates", dae);
		}
	}


}
