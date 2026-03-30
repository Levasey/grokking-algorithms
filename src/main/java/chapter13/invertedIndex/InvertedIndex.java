package chapter13.invertedIndex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Инвертированный индекс: сопоставляет термин (слово) множеству идентификаторов документов, где он встречается.
 * Используется в полнотекстовом поиске: по запросу быстро находятся релевантные документы без полного сканирования корпуса.
 */
public final class InvertedIndex {

    private static final Pattern WORD = Pattern.compile("[\\p{IsAlphabetic}\\p{IsDigit}]+");

    private final Map<String, Set<String>> termToDocuments = new HashMap<>();

    /**
     * Добавляет документ в индекс: текст разбивается на термы (последовательности букв и цифр) в нижнем регистре.
     *
     * @param documentId устойчивый идентификатор документа
     * @param text       полный текст документа
     */
    public void add(String documentId, String text) {
        Objects.requireNonNull(documentId, "documentId");
        Objects.requireNonNull(text, "text");
        String lower = text.toLowerCase(Locale.ROOT);
        Matcher m = WORD.matcher(lower);
        while (m.find()) {
            String term = m.group();
            termToDocuments.computeIfAbsent(term, k -> new HashSet<>()).add(documentId);
        }
    }

    /**
     * Документы, содержащие данный термин (сравнение без учёта регистра для латиницы и др.).
     */
    public Set<String> documentsWithTerm(String term) {
        Objects.requireNonNull(term, "term");
        Set<String> docs = termToDocuments.get(term.toLowerCase(Locale.ROOT));
        if (docs == null) {
            return Set.of();
        }
        return Set.copyOf(docs);
    }

    /**
     * Документы, в которых встречаются все перечисленные термы (логическое И).
     */
    public Set<String> documentsWithAllTerms(String... terms) {
        Objects.requireNonNull(terms, "terms");
        if (terms.length == 0) {
            return Set.of();
        }
        Set<String> result = new HashSet<>(documentsWithTerm(terms[0]));
        for (int i = 1; i < terms.length; i++) {
            result.retainAll(documentsWithTerm(terms[i]));
            if (result.isEmpty()) {
                break;
            }
        }
        return Set.copyOf(result);
    }

    /**
     * Документы, в которых встречается хотя бы один из терминов (логическое ИЛИ).
     */
    public Set<String> documentsWithAnyTerm(String... terms) {
        Objects.requireNonNull(terms, "terms");
        Set<String> result = new HashSet<>();
        for (String t : terms) {
            result.addAll(documentsWithTerm(t));
        }
        return Set.copyOf(result);
    }
}
