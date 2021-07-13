package com.ibm.cohort.engine.translation;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.cqframework.cql.elm.execution.And;

@OverridingEvaluator
public class ShortAndAdapter extends XmlAdapter<ShortAndEvaluator, And> {

	public ShortAndAdapter() {
	}

	@Override
	public And unmarshal(ShortAndEvaluator and) throws Exception {
		return and;
	}

	@Override
	public ShortAndEvaluator marshal(And and) throws Exception {
		if (and.getClass() == ShortAndEvaluator.class) {
			return (ShortAndEvaluator) and;
		}

		return new ShortAndEvaluator();
	}
}
