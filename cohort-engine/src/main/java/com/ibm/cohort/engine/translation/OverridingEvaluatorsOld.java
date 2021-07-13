package com.ibm.cohort.engine.translation;

import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.reflections.Reflections;

public class OverridingEvaluatorsOld {

	private static Set<Class<?>> evaluatorsToOverride =
			new Reflections("com.ibm.cohort.engine.translation")
					.getTypesAnnotatedWith(OverridingEvaluator.class);

//	private static Map<Class, Class> superClassLookup;
//	static {
//		superClassLookup = evaluatorsToOverride.stream()
//				.collect(Collectors.toMap(a -> a.getClass(), Function::identity));
//	}

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

//	public void overrideClasses(List<ClassDescriptor> descriptors) {
//		descriptors.stream().filter(this::shouldOverride).forEach(this::overrideDescriptor);
//	}

//	public void overrideDescriptor(ClassDescriptor descriptor) {
//
//
//		Class overridingClass = lookup.get;
//		descriptor.setJavaClass(overridingClass);
//		descriptor.setInstantiationPolicy(usingDefaultConstructor);
//	}

//	private boolean shouldOverride(ClassDescriptor descriptor) {
//		descriptor.getJavaClass()
//		return true;
//	}

//	private static Map<String, InstantiationPolicy> createInstantiationMapping(Set<Class<?>> classes) {
//		return classes.stream()
//				.collect(Collectors.toMap(Class::getName, OverridingEvaluators::createInstantiationPolicy));
//	}
//
//	private static InstantiationPolicy createInstantiationPolicy(Class<?> clazz) {
//		InstantiationPolicy policy = new InstantiationPolicy();
//		policy.useDefaultConstructorInstantiationPolicy();
////		policy.setDescriptor(discriptor);
//
//		return policy;
//	}

}
