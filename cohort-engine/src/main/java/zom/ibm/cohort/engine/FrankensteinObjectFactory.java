package zom.ibm.cohort.engine;

// This project lives in the `zom.*` package to ensure it's full class name
// is lexiographically after `ObjectFactoryEx`.

import javax.xml.bind.annotation.XmlRegistry;

import org.cqframework.cql.elm.execution.And;
import org.cqframework.cql.elm.execution.Or;
import org.cqframework.cql.elm.execution.ShortOrEvaluator;

import com.ibm.cohort.engine.translation.ShortAndEvaluator;

@XmlRegistry
public class FrankensteinObjectFactory {

    public And createAnd() {
        return new ShortAndEvaluator();
    }

    public Or createOr() {
        return new ShortOrEvaluator();
    }
}
