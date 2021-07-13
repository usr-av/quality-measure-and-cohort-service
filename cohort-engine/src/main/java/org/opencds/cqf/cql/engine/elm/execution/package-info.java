@XmlJavaTypeAdapters({
		@XmlJavaTypeAdapter(type = And.class,
				value = ShortAndAdapter.class),
		@XmlJavaTypeAdapter(type = Or.class,
				value = ShortOrAdapter.class),

})

package org.opencds.cqf.cql.engine.elm.execution;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import org.cqframework.cql.elm.execution.And;
import org.cqframework.cql.elm.execution.Or;

import com.ibm.cohort.engine.translation.ShortAndAdapter;
import com.ibm.cohort.engine.translation.ShortOrAdapter;