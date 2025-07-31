package io.brookite.termannotations;

import java.util.Locale;

public interface DomainTermDetectionMethod {
    DomainTermAnnotation[] findTerms(String source, DomainTermDictionary dictionary,
                                     DomainTermElement element, Locale language);
}
