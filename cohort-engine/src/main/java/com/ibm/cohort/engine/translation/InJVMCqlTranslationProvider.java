/*
 * (C) Copyright IBM Corp. 2020, 2020
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ibm.cohort.engine.translation;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslator.Options;
import org.cqframework.cql.cql2elm.CqlTranslatorException;
import org.cqframework.cql.cql2elm.FhirLibrarySourceProvider;
import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.cqframework.cql.cql2elm.ModelInfoLoader;
import org.cqframework.cql.cql2elm.ModelInfoProvider;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumService;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.opencds.cqf.cql.engine.elm.execution.ObjectFactoryEx;
import org.opencds.cqf.cql.engine.execution.CqlLibraryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cohort.engine.LibraryFormat;
import zom.ibm.cohort.engine.FrankensteinObjectFactory;

/**
 * Uses the CqlTranslator inprocess to convert CQL to ELM. 
 */
public class InJVMCqlTranslationProvider extends BaseCqlTranslationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(InJVMCqlTranslationProvider.class);
	private ModelManager modelManager;
	private LibraryManager libraryManager;

	public InJVMCqlTranslationProvider() {
		this.modelManager = new ModelManager();
		this.libraryManager = new LibraryManager(modelManager);
		libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
	}

	public InJVMCqlTranslationProvider(LibraryManager libraryManager, ModelManager modelManager) {
		this.modelManager = modelManager;
		this.libraryManager = libraryManager;
	}
	
	public InJVMCqlTranslationProvider(LibrarySourceProvider provider) {
		this();
		addLibrarySourceProvider(provider);
	}

	public InJVMCqlTranslationProvider addLibrarySourceProvider(LibrarySourceProvider provider) {
		libraryManager.getLibrarySourceLoader().registerProvider(provider);
		return this;
	}

	@Override
	public Library translate(InputStream cql, List<Options> options, LibraryFormat targetFormat) throws Exception {
		Library result;

		UcumService ucumService = null;
		LibraryBuilder.SignatureLevel signatureLevel = LibraryBuilder.SignatureLevel.None;

		List<Options> optionsList = new ArrayList<>();
		if (options != null) {
			optionsList.addAll(options);
		}

		CqlTranslator translator = CqlTranslator.fromStream(cql, modelManager, libraryManager, ucumService,
				CqlTranslatorException.ErrorSeverity.Info, signatureLevel,
				optionsList.toArray(new Options[optionsList.size()]));

		LOG.debug("Translated CQL contains {} errors", translator.getErrors().size());
		if (!translator.getErrors().isEmpty()) {
			throw new Exception("CQL translation contained errors: " + translator.getErrors().stream().map(Throwable::toString).collect(Collectors.joining("\n")));
		}

		LOG.debug("Translated CQL contains {} exceptions", translator.getExceptions().size());
		if (!translator.getExceptions().isEmpty()) {
			throw new Exception("CQL translation contained exceptions: " + translator.getExceptions().stream().map(Throwable::toString).collect(Collectors.joining("\n")));
		}

		switch (targetFormat) {
		case XML:
			result = gnarlyConvert(translator.toXml());
//			result = CqlLibraryReader.read(new StringReader(translator.toXml()));
			break;
// This is only a theoretical nice-to-have and fails deserialization, so disabling support for now.
//		case JSON:
//			result = JsonCqlLibraryReader.read(new StringReader(translator.toJxson()));
//			break;
		default:
			throw new IllegalArgumentException(
					String.format("The CQL Engine does not support format %s", targetFormat.name()));
		}

		return result;
	}

	private Library gnarlyConvert(String elmString) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ObjectFactoryEx.class, FrankensteinObjectFactory.class);
		Unmarshaller u = context.createUnmarshaller();
		Object result = u.unmarshal(new StringReader(elmString));
		return ((JAXBElement<Library>)result).getValue();
	}

	
	@Override
	public void registerModelInfo(ModelInfo modelInfo) {
		// Force mapping  to FHIR 4.0.1. Consider supporting different versions in the future.
		// Possibly add support for auto-loading model info files.
		modelInfo.setTargetVersion("4.0.1");
		modelInfo.setTargetUrl("http://hl7.org/fhir");
		org.hl7.elm.r1.VersionedIdentifier modelId = (new org.hl7.elm.r1.VersionedIdentifier()).withId(modelInfo.getName()).withVersion(modelInfo.getVersion());
		ModelInfoProvider modelProvider = () -> modelInfo;
		ModelInfoLoader.registerModelInfoProvider(modelId, modelProvider);
	}

	@Override
	public ModelInfo convertToModelInfo(InputStream modelInfoInputStream) {
		return JAXB.unmarshal(modelInfoInputStream, ModelInfo.class);
	}

	@Override
	public ModelInfo convertToModelInfo(Reader modelInfoReader) {
		return JAXB.unmarshal(modelInfoReader, ModelInfo.class);
	}

	@Override
	public ModelInfo convertToModelInfo(File modelInfoFile) {
		return JAXB.unmarshal(modelInfoFile, ModelInfo.class);
	}
}
