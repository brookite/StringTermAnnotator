package io.brookite.termannotations;

import java.util.Locale;

public record DomainTermAnnotation(DomainTermElement term, Locale language,
                                   int pos, int length, String pattern) {
    public String explanation() {
        return term.getExplanation(language);
    }
}
