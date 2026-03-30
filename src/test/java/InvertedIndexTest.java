import chapter13.invertedIndex.InvertedIndex;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InvertedIndexTest {

    private InvertedIndex index;

    @BeforeEach
    void setUp() {
        index = new InvertedIndex();
    }

    @Test
    void documentsWithTerm_findsDocBySingleWord() {
        index.add("a", "hello world");
        assertEquals(Set.of("a"), index.documentsWithTerm("hello"));
        assertEquals(Set.of("a"), index.documentsWithTerm("world"));
    }

    @Test
    void documentsWithTerm_caseInsensitive() {
        index.add("1", "Hello HELLO");
        assertEquals(Set.of("1"), index.documentsWithTerm("hello"));
    }

    @Test
    void documentsWithTerm_sameTermInTwoDocs() {
        index.add("x", "cat");
        index.add("y", "the cat sat");
        assertEquals(Set.of("x", "y"), index.documentsWithTerm("cat"));
    }

    @Test
    void documentsWithTerm_cyrillic() {
        index.add("ru", "Инвертированный индекс ускоряет поиск");
        assertEquals(Set.of("ru"), index.documentsWithTerm("инвертированный"));
        assertEquals(Set.of("ru"), index.documentsWithTerm("поиск"));
    }

    @Test
    void documentsWithTerm_unknownTerm_empty() {
        index.add("a", "only this");
        assertTrue(index.documentsWithTerm("missing").isEmpty());
    }

    @Test
    void documentsWithAllTerms_requiresBothTerms() {
        index.add("1", "alpha beta");
        index.add("2", "alpha gamma");
        index.add("3", "beta gamma");
        assertEquals(Set.of("1"), index.documentsWithAllTerms("alpha", "beta"));
    }

    @Test
    void documentsWithAllTerms_noMatch_empty() {
        index.add("1", "foo bar");
        assertTrue(index.documentsWithAllTerms("foo", "baz").isEmpty());
    }

    @Test
    void documentsWithAllTerms_noTerms_empty() {
        index.add("1", "x");
        assertTrue(index.documentsWithAllTerms().isEmpty());
    }

    @Test
    void documentsWithAnyTerm_union() {
        index.add("a", "apple");
        index.add("b", "banana");
        index.add("c", "cherry apple");
        assertEquals(Set.of("a", "b", "c"), index.documentsWithAnyTerm("apple", "banana"));
    }

    @Test
    void add_rejectsNullDocumentId() {
        assertThrows(NullPointerException.class, () -> index.add(null, "text"));
    }

    @Test
    void add_rejectsNullText() {
        assertThrows(NullPointerException.class, () -> index.add("id", null));
    }

    @Test
    void buildParallel_matchesSequentialAdds() {
        InvertedIndex sequential = new InvertedIndex();
        sequential.add("a", "hello world");
        sequential.add("b", "hello cat");
        sequential.add("c", "dog");

        InvertedIndex parallel = InvertedIndex.buildParallel(List.of(
                Map.entry("a", "hello world"),
                Map.entry("b", "hello cat"),
                Map.entry("c", "dog")));

        assertEquals(sequential.documentsWithTerm("hello"), parallel.documentsWithTerm("hello"));
        assertEquals(sequential.documentsWithTerm("cat"), parallel.documentsWithTerm("cat"));
        assertEquals(sequential.documentsWithAllTerms("hello", "cat"), parallel.documentsWithAllTerms("hello", "cat"));
    }

    @Test
    void buildParallel_listOverload_matchesSequential() {
        InvertedIndex sequential = new InvertedIndex();
        sequential.add("1", "alpha beta");
        sequential.add("2", "alpha gamma");

        InvertedIndex parallel = InvertedIndex.buildParallel(
                List.of("1", "2"),
                List.of("alpha beta", "alpha gamma"));

        assertEquals(sequential.documentsWithAllTerms("alpha", "beta"), parallel.documentsWithAllTerms("alpha", "beta"));
    }
}
