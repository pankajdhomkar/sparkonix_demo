package com.sparkonix.entity.dialect;

import java.sql.Types;

import javax.inject.Inject;

import org.hibernate.dialect.PostgreSQL9Dialect;

public class JsonPostgreSQLDialect extends PostgreSQL9Dialect {
	@Inject
	public JsonPostgreSQLDialect() {
		super();
		this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
	}
}
