package com.ibm.cohort.engine.translation;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.cqframework.cql.elm.execution.Or;
import org.cqframework.cql.elm.execution.ShortOrEvaluator;


public class ShortOrAdapter extends XmlAdapter<Or, ShortOrEvaluator> {

	@Override
	public ShortOrEvaluator unmarshal(Or or) throws Exception {
		ShortOrEvaluator shortOrEvaluator = new ShortOrEvaluator();
		shortOrEvaluator.setLocalId(or.getLocalId());
		shortOrEvaluator.setLocator(or.getLocator());
		shortOrEvaluator.setResultTypeName(or.getResultTypeName());
		shortOrEvaluator.setResultTypeSpecifier(or.getResultTypeSpecifier());
//		shortOrEvaluator.withSignature()


		return shortOrEvaluator;
	}

	@Override
	public Or marshal(ShortOrEvaluator or) throws Exception {
		Or Or = new Or();
		Or.setLocalId(or.getLocalId());
		Or.setLocator(or.getLocator());
		Or.setResultTypeName(or.getResultTypeName());
		Or.setResultTypeSpecifier(or.getResultTypeSpecifier());

//		Or.withAnnotation();

		return Or;
	}
}
