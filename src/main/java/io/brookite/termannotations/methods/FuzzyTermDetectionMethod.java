package io.brookite.termannotations.methods;


import io.brookite.termannotations.DomainTermAnnotation;
import io.brookite.termannotations.DomainTermDetectionMethod;
import io.brookite.termannotations.DomainTermDictionary;
import io.brookite.termannotations.DomainTermElement;
import io.brookite.termannotations.fuzzy.WeightedSimilarity;
import io.brookite.termannotations.utils.TagTolerantStringTokenizer;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static io.brookite.termannotations.utils.StringUtils.stripTags;
import static io.brookite.termannotations.utils.TagTolerantStringTokenizer.findWordPosition;

public class FuzzyTermDetectionMethod implements DomainTermDetectionMethod {

    @AllArgsConstructor
    public static class MatchResult {
        String text; int score; int pos; int length;
    }

    public static List<MatchResult> fuzzyNgramSearch(String text, String pattern, int baseMaxScore) {
        String[] words = TagTolerantStringTokenizer.tokenize(text);
        int n = TagTolerantStringTokenizer.tokenize(pattern).length;

        List<MatchResult> matches = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder ngramBuilder = new StringBuilder();
            for (int j = 0; j < n; j++) {
                ngramBuilder.append(words[i + j]);
                if (j != n - 1) ngramBuilder.append(" ");
            }
            String ngram = ngramBuilder.toString();
            var posKey = Pair.of(i, i + n);

            int score = (int)(WeightedSimilarity.computeWRatio(stripTags(ngram), pattern) * 100);

            if (score >= baseMaxScore) {
                int offset = findWordPosition(text, posKey.getKey())[0];
                int length = findWordPosition(text, posKey.getValue())[0] - offset - 1;
                if (offset != -1) {
                    matches.add(new MatchResult(ngram, score,
                            offset, length));
                }
            }
        }
        return matches;
    }

    @Override
    public DomainTermAnnotation[] findTerms(String source, DomainTermDictionary dictionary,
                                            DomainTermElement element, Locale language) {
        var settings = dictionary.getDetectionSettings(element);
        float threshold = settings.getFuzzyThreshold();
        boolean ignoreCase = settings.isCaseIgnored();

        if (threshold < 1) {
            threshold *= 100;
        }

        List<DomainTermAnnotation> annotations = new ArrayList<>();
        for (String pattern : element.getPossiblePatterns(language)) {

            List<MatchResult> matches_found = fuzzyNgramSearch(ignoreCase ? source.toLowerCase() : source,
                    ignoreCase ? pattern.toLowerCase() : pattern,
                    (int) threshold);

            // Убрать излишнюю, т.е. не присутствующую в паттерне, пунктуацию (1+ символов с конца)
            for (var match : matches_found) {
                for (int last_pos = match.pos + match.length - 1; last_pos >= 0; --last_pos) {
                    char last_char = source.charAt(last_pos);
                    var c_type = Character.getType(last_char);
                    if (c_type == Character.END_PUNCTUATION || c_type == Character.DASH_PUNCTUATION || c_type == Character.OTHER_PUNCTUATION) {
                        if (pattern.indexOf(last_char) == -1) {
                            // Символ не содержится в паттерне.
                            // Вырезать 1 символ в конце, т.к. он является неожиданной пунктуацией:
                            match.length -= 1;
                            continue;
                        }
                    }
                    break;  // No changes, stop iteration.
                }
            }

            annotations.addAll(matches_found.stream().sorted(Comparator.comparingInt(o -> -o.score))
                    .map(match -> new DomainTermAnnotation(
                            element, language,
                            match.pos, match.length, pattern
                    )).toList());
        }
        return annotations.toArray(DomainTermAnnotation[]::new);
    }
}
