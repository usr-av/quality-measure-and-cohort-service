package aaa.ibm.cohort.engine;

// This project lives in the `zom.*` package to ensure it's full class name
// is lexiographically after `ObjectFactoryEx`.

import javax.xml.bind.annotation.XmlRegistry;

import org.cqframework.cql.elm.execution.Or;
import org.cqframework.cql.elm.execution.ShortOrEvaluator;

@XmlRegistry
public class AAAFrankensteinObjectFactory {

//    public And createAnd() {
//        return new ShortAndEvaluator();
//    }

//	@XmlJavaTypeAdapter(ShortOrAdapter.class)
	public Or createOr() {
        return new ShortOrEvaluator();
    }
}
