package io.brookite.termannotations.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.brookite.termannotations.DomainTermDetectionMethod;
import io.brookite.termannotations.methods.FuzzyTermDetectionMethod;
import io.brookite.termannotations.methods.RegexTermDetectionMethod;

import java.io.IOException;

public class DetectionMethodDeserializer extends JsonDeserializer<DomainTermDetectionMethod> {
    @Override
    public DomainTermDetectionMethod deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        switch (value.toLowerCase()) {
            case "fuzzy": return new FuzzyTermDetectionMethod();
            case "regex": return new RegexTermDetectionMethod();
            default: throw new IllegalArgumentException("Unknown detection method: " + value);
        }
    }
}
