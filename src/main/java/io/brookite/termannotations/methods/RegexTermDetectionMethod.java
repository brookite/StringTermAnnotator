package io.brookite.termannotations.methods;

import io.brookite.termannotations.DomainTermAnnotation;
import io.brookite.termannotations.DomainTermDetectionMethod;
import io.brookite.termannotations.DomainTermDictionary;
import io.brookite.termannotations.DomainTermElement;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTermDetectionMethod implements DomainTermDetectionMethod {

    public record MatchResult(String text, int pos, int length) {}

    public static List<MatchResult> regexSearch(String text, String regexPattern, boolean caseInsensitive) {
        Pattern pattern = Pattern.compile(regexPattern, caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
        Matcher matcher = pattern.matcher(text);

        List<MatchResult> matches = new ArrayList<>();

        while (matcher.find()) {
            String matchText = matcher.group();
            int start = matcher.start();
            int length = matcher.end() - matcher.start();
            matches.add(new MatchResult(matchText, start, length));
        }

        return matches;
    }

    @Override
    public DomainTermAnnotation[] findTerms(String source, DomainTermDictionary dictionary,
                                            DomainTermElement element, Locale language) {
        List<DomainTermAnnotation> annotations = new ArrayList<>();
        for (String pattern : element.getPossiblePatterns(language)) {
            annotations.addAll(regexSearch(source, pattern, dictionary.getDetectionSettings(element).isCaseIgnored())
                    .stream()
                    .map(match -> new DomainTermAnnotation(
                            element, language,
                            match.pos,
                            match.length, pattern
                    )).toList());
        }
        return annotations.toArray(DomainTermAnnotation[]::new);
    }
}