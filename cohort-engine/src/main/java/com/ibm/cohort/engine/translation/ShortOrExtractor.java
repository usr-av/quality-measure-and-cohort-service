package com.ibm.cohort.engine.translation;

import org.cqframework.cql.elm.execution.Or;
import org.cqframework.cql.elm.execution.ShortOrEvaluator;
import org.eclipse.persistence.descriptors.ClassExtractor;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.Session;

public class ShortOrExtractor extends ClassExtractor {
	@Override
	public Class<? extends Or> extractClassFromRow(Record databaseRow, Session session) {
		return ShortOrEvaluator.class;
	}
}
