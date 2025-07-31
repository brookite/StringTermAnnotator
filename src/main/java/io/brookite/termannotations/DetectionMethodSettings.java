package io.brookite.termannotations;

import lombok.Builder;
import lombok.Getter;

@Builder
public class DetectionMethodSettings {

    public static DetectionMethodSettings defaults() {
        return DetectionMethodSettings.builder()
                .fuzzyThreshold(0.85f)
                .caseIgnored(true)
                .build();
    }

    @Getter
    float fuzzyThreshold;
    @Getter
    boolean caseIgnored;

}
