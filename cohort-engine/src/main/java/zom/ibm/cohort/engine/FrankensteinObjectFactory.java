package zom.ibm.cohort.engine;

// This project lives in the `zom.*` package to ensure it's full class name
// is lexiographically after `ObjectFactoryEx`.

import com.ibm.cohort.engine.translation.ShortAndEvaluator;
import com.ibm.cohort.engine.translation.ShortOrEvaluator;
import org.cqframework.cql.elm.execution.And;
import org.cqframework.cql.elm.execution.Or;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class FrankensteinObjectFactory {

    public And createAnd() {
        return new ShortAndEvaluator();
    }

    public Or createOr() {
        return new ShortOrEvaluator();
    }
}
