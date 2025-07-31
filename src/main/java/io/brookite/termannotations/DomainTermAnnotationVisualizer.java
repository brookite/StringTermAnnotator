package io.brookite.termannotations;

import java.util.Locale;

public interface DomainTermAnnotationVisualizer {
    String apply(String original, DomainTermAnnotation anno, Locale lang);
}
