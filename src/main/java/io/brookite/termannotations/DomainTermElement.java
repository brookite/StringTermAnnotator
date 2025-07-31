package io.brookite.termannotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.brookite.termannotations.utils.DetectionMethodDeserializer;
import io.brookite.termannotations.utils.ManyLocalizedObjectDeserializer;
import io.brookite.termannotations.utils.SingleLocalizedObjectDeserializer;
import lombok.Getter;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Getter
public class DomainTermElement {
    @JsonDeserialize(using = ManyLocalizedObjectDeserializer.class)
    private Map<String, List<String>> pattern = new HashMap<>();

    @JsonProperty("detection_method")
    @JsonDeserialize(using = DetectionMethodDeserializer.class)
    private DomainTermDetectionMethod detectionMethod = DomainTermDictionary.DEFAULT_DETECTION_METHOD;

    @JsonProperty("detection_settings")
    private DetectionMethodSettings detectionSettings = DetectionMethodSettings.defaults();

    @JsonDeserialize(using = SingleLocalizedObjectDeserializer.class)
    private Map<String, String> explanations = new HashMap<>();

    public List<String> getPossiblePatterns(Locale lang) {
        if (pattern.size() == 1) {
            return pattern.values().iterator().next();
        }
        return pattern.get(lang.toString());
    }

    public boolean isMalformed() {
        return pattern.isEmpty() || explanations.isEmpty();
    }

    public boolean isMalformed(Locale lang) {
        return !pattern.containsKey(lang.toString()) || !pattern.containsKey(lang.toString());
    }

    public String getExplanation(Locale lang) {
        if (explanations.size() == 1) {
            return explanations.values().iterator().next();
        }
        return explanations.get(lang.toString());
    }
}
