package chapter13.localitySensitive;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MinHashLshIndexTest {

    @Test
    void minHash_rejectsEmptyFeatures() {
        MinHash mh = new MinHash(10, 123L);
        assertThrows(IllegalArgumentException.class, () -> mh.signature(Set.of()));
    }

    @Test
    void lshIndex_rejectsInvalidConfig() {
        assertThrows(IllegalArgumentException.class, () -> new MinHashLshIndex(0, 1, 1L));
        assertThrows(IllegalArgumentException.class, () -> new MinHashLshIndex(10, 3, 1L));
    }

    @Test
    void candidateNeighbors_includesSelf() {
        MinHashLshIndex idx = new MinHashLshIndex(40, 10, 99L);
        Set<Long> features = Set.of(10L, 20L, 30L);
        idx.add("x", features);
        assertTrue(idx.candidateNeighbors("x").contains("x"));
    }

    @Test
    void identicalDocuments_estimatedJaccardOne() {
        MinHashLshIndex idx = new MinHashLshIndex(64, 8, 7L);
        Set<Long> f = Set.of(1L, 2L, 3L, 4L, 5L);
        idx.add("a", f);
        idx.add("b", f);
        assertEquals(1.0, idx.estimatedJaccard("a", "b"), 1e-9);
    }

    @Test
    void nearDuplicate_highJaccard_yieldsSharedBucket() {
        Set<Long> base = new HashSet<>();
        for (long i = 0; i < 80; i++) {
            base.add(i);
        }
        Set<Long> near = new HashSet<>(base);
        near.remove(79L);
        near.add(10_000L);

        MinHashLshIndex idx = new MinHashLshIndex(128, 16, 42L);
        idx.add("base", base);
        idx.add("near", near);

        Set<String> fromBase = idx.candidateNeighbors("base");
        Set<String> fromNear = idx.candidateNeighbors("near");
        assertTrue(fromBase.contains("near"), "expected LSH to link near-duplicate sets");
        assertTrue(fromNear.contains("base"));
        assertTrue(idx.estimatedJaccard("base", "near") > 0.5);
    }

    @Test
    void queryCandidates_matchesIndexedSignature() {
        MinHashLshIndex idx = new MinHashLshIndex(32, 8, 11L);
        Set<Long> q = Set.of(5L, 6L, 7L);
        idx.add("doc", q);
        Set<String> cand = idx.queryCandidates(q);
        assertTrue(cand.contains("doc"));
    }

    @Test
    void unknownDocument_throws() {
        MinHashLshIndex idx = new MinHashLshIndex(20, 5, 0L);
        idx.add("only", Set.of(1L));
        assertThrows(IllegalArgumentException.class, () -> idx.candidateNeighbors("missing"));
    }
}
