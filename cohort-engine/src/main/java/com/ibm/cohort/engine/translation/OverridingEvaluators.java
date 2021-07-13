package com.ibm.cohort.engine.translation;

import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.reflections.Reflections;

public class OverridingEvaluators {

	private static Set<Class<?>> evaluatorsToOverride =
			new Reflections("com.ibm.cohort.engine.translation")
					.getTypesAnnotatedWith(OverridingEvaluator.class);

	private static InstantiationPolicy usingDefaultConstructor = new InstantiationPolicy();
	static {
		usingDefaultConstructor.useDefaultConstructorInstantiationPolicy();
	}

	public void override(Map<Class, ClassDescriptor> descriptors) {
		for (Class<?> evaluator : evaluatorsToOverride) {
			Class<?> superclass = evaluator.getSuperclass();

			ClassDescriptor descriptor = descriptors.get(superclass);
			if (descriptor != null) {
				descriptor.setJavaClass(evaluator);
				descriptor.setInstantiationPolicy(usingDefaultConstructor);
			}
		}
	}

}
