package com.ibm.cohort.engine.translation;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;

public class ShortCircuitCustomizer implements DescriptorCustomizer {

	@Override
	public void customize(ClassDescriptor descriptor) throws Exception {
		descriptor.setInstantiationPolicy(new DefaultConstructorInstantiationPolicy(descriptor));
	}

	private static class DefaultConstructorInstantiationPolicy extends InstantiationPolicy {

		public DefaultConstructorInstantiationPolicy(ClassDescriptor descriptor) {
			InstantiationPolicy defaultInstantiationPolicy = descriptor.getInstantiationPolicy();
			this.factoryClassName = defaultInstantiationPolicy.getFactoryClassName();
			this.factoryClass = defaultInstantiationPolicy.getFactoryClass();
			this.methodName = defaultInstantiationPolicy.getMethodName();
		}

		@Override
		public void initialize(AbstractSession session) throws DescriptorException {
			super.initialize(session);
		}

		@Override
		public Object buildNewInstance() throws DescriptorException {
			return buildNewInstanceUsingDefaultConstructor();
		}

	}

}