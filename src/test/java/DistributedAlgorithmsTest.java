import chapter13.distributed.BullyLeaderElection;
import chapter13.distributed.BullyLeaderElection.ElectionStep;
import chapter13.distributed.DistributedWordCount;
import chapter13.distributed.LamportClock;
import chapter13.distributed.VectorClock;
import chapter13.distributed.VectorClock.CausalRelation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class DistributedAlgorithmsTest {

    @Test
    public void lamport_receive_advancesPastMax() {
        LamportClock p = new LamportClock();
        p.tick();
        p.tick();
        assertEquals(3L, p.onSend());
        LamportClock q = new LamportClock();
        assertEquals(1L, q.tick());
        assertEquals(4L, q.onReceive(3L));
    }

    @Test
    public void lamport_receive_rejectsNegativeRemote() {
        LamportClock q = new LamportClock();
        assertThrows(IllegalArgumentException.class, () -> q.onReceive(-1L));
    }

    @Test
    public void vectorClock_detectsConcurrent() {
        VectorClock a = new VectorClock(3, 0);
        VectorClock b = new VectorClock(3, 1);
        a.tick();
        b.tick();
        assertEquals(CausalRelation.CONCURRENT, VectorClock.relation(a.snapshot(), b.snapshot()));
    }

    @Test
    public void vectorClock_happensBefore_acrossMessage() {
        VectorClock p0 = new VectorClock(2, 0);
        VectorClock p1 = new VectorClock(2, 1);
        p0.tick();
        int[] m = p0.onSend();
        p1.onReceive(m);
        assertEquals(CausalRelation.BEFORE, VectorClock.relation(p0.snapshot(), p1.snapshot()));
        assertEquals(CausalRelation.AFTER, VectorClock.relation(p1.snapshot(), p0.snapshot()));
    }

    @Test
    public void bully_leader_isMaxAliveId() {
        TreeSet<Integer> alive = new TreeSet<>(List.of(2, 7, 5));
        assertEquals(7, BullyLeaderElection.leader(alive).getAsInt());
        assertEquals(7, BullyLeaderElection.run(2, alive, null).getAsInt());
    }

    @Test
    public void bully_trace_fromSmallest() {
        List<ElectionStep> trace = new ArrayList<>();
        TreeSet<Integer> alive = new TreeSet<>(List.of(3, 9, 7));
        OptionalInt r = BullyLeaderElection.run(3, alive, trace);
        assertEquals(9, r.getAsInt());
        assertTrue(trace.stream().anyMatch(s -> s.kind().equals("coordinator") && s.from() == 9));
    }

    @Test
    public void distributedWordCount_matchesSequential() {
        List<String> lines = List.of("a b", "b c", "a");
        List<DistributedWordCount.ReduceShard> r1 = DistributedWordCount.count(lines, 1);
        List<DistributedWordCount.ReduceShard> r3 = DistributedWordCount.count(lines, 3);
        assertEquals(r1, r3);
        assertEquals(2, r1.stream().filter(s -> s.word().equals("a")).findFirst().orElseThrow().total());
        assertEquals(2, r1.stream().filter(s -> s.word().equals("b")).findFirst().orElseThrow().total());
    }

    @Test
    public void distributedWordCount_invalidNodeCount() {
        assertThrows(IllegalArgumentException.class,
                () -> DistributedWordCount.count(List.of("x"), 0));
    }
}
