/*
 * (C) Copyright IBM Corp. 2021, 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.ibm.cohort.engine;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.ibm.cohort.cql.data.CqlDataProvider;
import com.ibm.cohort.cql.data.DefaultCqlDataProvider;
import com.ibm.cohort.cql.evaluation.ContextNames;
import com.ibm.cohort.cql.evaluation.CqlEvaluator;
import com.ibm.cohort.cql.library.ClasspathCqlLibraryProvider;
import com.ibm.cohort.cql.library.CqlLibraryDescriptor;
import com.ibm.cohort.cql.library.CqlLibraryProvider;
import com.ibm.cohort.cql.library.Format;
import com.ibm.cohort.cql.terminology.CqlTerminologyProvider;
import com.ibm.cohort.cql.terminology.DefaultCqlTerminologyProvider;
import com.ibm.cohort.cql.translation.CqlToElmTranslator;
import com.ibm.cohort.cql.translation.TranslatingCqlLibraryProvider;
import com.ibm.cohort.engine.r4.cache.R4FhirModelResolverFactory;
import com.ibm.cohort.engine.retrieve.R4RestFhirRetrieveProvider;
import com.ibm.cohort.engine.terminology.R4RestFhirTerminologyProvider;
import com.ibm.cohort.fhir.client.config.FhirClientBuilder;
import com.ibm.cohort.fhir.client.config.FhirClientBuilderFactory;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.r4.model.Patient;

import com.ibm.cohort.fhir.client.config.FhirServerConfig;
import com.ibm.cohort.fhir.client.config.IBMFhirServerConfig;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.engine.model.ModelResolver;
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider;

public class BasePatientTest extends BaseFhirTest {
	protected CqlEvaluator setupTestFor(Patient patient, String firstPackage, String... packages) {
		IBMFhirServerConfig fhirConfig = new IBMFhirServerConfig();
		fhirConfig.setEndpoint("http://localhost:" + HTTP_PORT);
		fhirConfig.setUser("fhiruser");
		fhirConfig.setPassword("change-password");
		fhirConfig.setTenantId("default");

		return setupTestFor(patient, fhirConfig, firstPackage, packages);
	}

	protected CqlEvaluator setupTestFor(Patient patient, FhirServerConfig fhirConfig, String firstPackage, String... packages) {

		mockFhirResourceRetrieval("/metadata?_format=json", getCapabilityStatement());
		mockFhirResourceRetrieval(patient);

		CqlEvaluator evaluator = null;
		if (firstPackage != null) {
			FhirClientBuilderFactory factory = FhirClientBuilderFactory.newInstance();
			FhirClientBuilder fhirClientBuilder = factory.newFhirClientBuilder();

			CqlLibraryProvider classpathCqlLibraryProvider = new ClasspathCqlLibraryProvider(firstPackage, packages);
			CqlToElmTranslator translator = new CqlToElmTranslator();
			CqlLibraryProvider libraryProvider = new TranslatingCqlLibraryProvider(classpathCqlLibraryProvider, translator);

			IGenericClient testClient = fhirClientBuilder.createFhirClient(fhirConfig);

			TerminologyProvider terminologyProvider = new R4RestFhirTerminologyProvider(testClient);
			CqlTerminologyProvider termProvider = new DefaultCqlTerminologyProvider(terminologyProvider);

			ModelResolver modelResolver = R4FhirModelResolverFactory.createCachingResolver();
			SearchParameterResolver searchParameterResolver = new SearchParameterResolver(testClient.getFhirContext());
			R4RestFhirRetrieveProvider retrieveProvider = new R4RestFhirRetrieveProvider(searchParameterResolver, testClient);
			retrieveProvider.setExpandValueSets(true);
			retrieveProvider.setTerminologyProvider(terminologyProvider);
			CqlDataProvider dataProvider = new DefaultCqlDataProvider(modelResolver, retrieveProvider);

			evaluator = new CqlEvaluator()
					.setLibraryProvider(libraryProvider)
					.setDataProvider(dataProvider)
					.setTerminologyProvider(termProvider)
					.setCacheContexts(false);
		}

		return evaluator;
	}

	protected CqlLibraryDescriptor newDescriptor(String id, String version, Format format) {
		return new CqlLibraryDescriptor()
				.setLibraryId(id)
				.setVersion(version)
				.setFormat(format);
	}

	protected Pair<String, String> newPatientContext(String patientId) {
		return new ImmutablePair<>(ContextNames.PATIENT, patientId);
	}
}
