/*
 * (C) Copyright IBM Corp. 2020, 2021
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.cohort.cache.invalidator;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import com.ibm.cohort.fhir.client.config.FhirClientBuilder;
import com.ibm.cohort.fhir.client.config.FhirClientBuilderFactory;
import com.ibm.cohort.fhir.client.config.FhirServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class CohortCacheInvalidatorPgm {

	private static final Logger LOG = LoggerFactory.getLogger(CohortCacheInvalidatorPgm.class);

	private static final class Arguments {
		@Parameter(names = "--cos-config-file", description = "COS Configuration File", required = true)
		private File cosConfigFile;

		@Parameter(names = "--fhir-config-file", description = "FHIR Configuration File", required = true)
		private File fhirConfigurationFile;
	}

	public static void main(String[] args) throws Exception {
		Arguments arguments = new Arguments();
		JCommander jc = JCommander.newBuilder()
				.programName("cohort-cache-invalidator")
				.addObject(arguments)
				.build();
		jc.parse(args);

		ObjectMapper mapper = new ObjectMapper();
		TempCosConfig cosConfig = mapper.readValue(arguments.cosConfigFile, TempCosConfig.class);
		FhirServerConfig fhirConfig = mapper.readValue(arguments.fhirConfigurationFile, FhirServerConfig.class);

		CohortCacheInvalidatorPgm pgm = new CohortCacheInvalidatorPgm(cosConfig, fhirConfig);
		pgm.run();
	}

	private final TempCosConfig cosConfig;
	private final FhirServerConfig fhirConfig;

	public CohortCacheInvalidatorPgm(TempCosConfig cosConfig, FhirServerConfig fhirConfig) {
		this.cosConfig = cosConfig;
		this.fhirConfig = fhirConfig;
	}

	public void run() {
		System.out.println(cosConfig);
		System.out.println(fhirConfig);

		AmazonS3 cosClient = createCosClient(cosConfig);
		IGenericClient fhirClient = createFhirClient(fhirConfig);

		// TODO: Do we want one instance of the invalidator to process all tenants at once?
		// Or do we want an invalidator instance per tentant?
		// Both have their problems...

		// TODO: Get bucket(s) to use (i.e. tenant(s) to process)

		// does invalidator-timestamp exist?
			// read and set to current-timestamp local var
		// else
			// find reasonable deafult!!

		// main loop
			// calculate next time interval
			// perform history request from the fhir server

			// for each resource
				// get patient id and timestamp
				// store patient timestamp mapping in a map somewhere and ensure latest timestamp is kept

			// for each patient timestamp mapping
				// write latest-timestamp for patient

			// set current-timestamp local var
			// write new invalidator-timestamp to cos using end of interval
			// sleep
	}

	private IGenericClient createFhirClient(FhirServerConfig fhirConfig) {
		FhirClientBuilder fhirClientBuilder = FhirClientBuilderFactory.newInstance().newFhirClientBuilder();
		return fhirClientBuilder.createFhirClient(fhirConfig);
	}

	private AmazonS3 createCosClient(TempCosConfig cosConfig) {
		AWSCredentials credentials = new BasicIBMOAuthCredentials(cosConfig.getApiKey(), cosConfig.getServiceInstanceId());
		ClientConfiguration clientConfig = new ClientConfiguration()
				.withRequestTimeout(50_000)
				.withTcpKeepAlive(true);

		return AmazonS3ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(cosConfig.getEndpoint(), cosConfig.getLocation()))
				.withPathStyleAccessEnabled(true)
				.withClientConfiguration(clientConfig)
				.build();
	}
}
