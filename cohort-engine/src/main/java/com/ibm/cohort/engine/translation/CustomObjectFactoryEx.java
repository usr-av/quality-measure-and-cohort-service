package com.ibm.cohort.engine.translation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.cqframework.cql.elm.execution.And;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.Or;
import org.cqframework.cql.elm.execution.ShortOrEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.ObjectFactoryEx;


// todo: [daniel.kim] does not work
@XmlRegistry
@XmlSeeAlso(ObjectFactoryEx.class)
public class CustomObjectFactoryEx extends ObjectFactoryEx {
	@Override
	public And createAnd() {
		return new ShortAndEvaluator();
	}

	@Override
	public Or createOr() {
		return new ShortOrEvaluator();
	}

//	@Override
//	public ExpressionDef createExpressionDef() {
//		return super.createExpressionDef();
//	}

	// The JAXB implementations does not recursively search the superclass for annotations
	@Override
	@XmlElementDecl(namespace = "urn:hl7-org:elm:r1", name = "library")
	public JAXBElement<Library> createLibrary(Library value) {
		return super.createLibrary(value);
	}
}
