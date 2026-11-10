import java.util.*;

/**
 * Q2: When Is ArrayList an Appropriate Engineering Choice?
 * Problem Context: Music Playlist Editor
 * 
 * Compares ArrayList, LinkedList, and ArrayDeque across operations:
 * - Add at end
 * - Insert at front
 * - Insert in middle
 * - Remove from front
 * - Remove from middle
 * - Get k-th element
 * - Full iteration
 */
public class PlaylistBenchmark {

    static class Song {
        final int id;
        final String title;

        Song(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    private static void benchmarkOperations(int size, int sampleOps) {
        System.out.println("==========================================================================================");
        System.out.println("Playlist Size: " + size + " songs | Benchmark Sample Ops: " + sampleOps);
        System.out.println("==========================================================================================");

        // 1. Add at end
        long tStart = System.nanoTime();
        List<Song> arrayList = new ArrayList<>();
        for (int i = 0; i < size; i++) arrayList.add(new Song(i, "Song_" + i));
        double alAddEnd = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        List<Song> linkedList = new LinkedList<>();
        for (int i = 0; i < size; i++) linkedList.add(new Song(i, "Song_" + i));
        double llAddEnd = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        Deque<Song> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < size; i++) arrayDeque.addLast(new Song(i, "Song_" + i));
        double adAddEnd = (System.nanoTime() - tStart) / 1_000_000.0;

        // 2. Insert at front (sampleOps)
        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) arrayList.add(0, new Song(-i, "Front_" + i));
        double alInsertFront = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) linkedList.add(0, new Song(-i, "Front_" + i));
        double llInsertFront = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) arrayDeque.addFirst(new Song(-i, "Front_" + i));
        double adInsertFront = (System.nanoTime() - tStart) / 1_000_000.0;

        // 3. Insert in middle (sampleOps)
        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) arrayList.add(arrayList.size() / 2, new Song(-i, "Mid_" + i));
        double alInsertMid = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) linkedList.add(linkedList.size() / 2, new Song(-i, "Mid_" + i));
        double llInsertMid = (System.nanoTime() - tStart) / 1_000_000.0;

        // 4. Remove at front (sampleOps)
        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) arrayList.remove(0);
        double alRemoveFront = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) linkedList.remove(0);
        double llRemoveFront = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) arrayDeque.removeFirst();
        double adRemoveFront = (System.nanoTime() - tStart) / 1_000_000.0;

        // 5. Remove in middle (sampleOps)
        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) arrayList.remove(arrayList.size() / 2);
        double alRemoveMid = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        for (int i = 0; i < sampleOps; i++) linkedList.remove(linkedList.size() / 2);
        double llRemoveMid = (System.nanoTime() - tStart) / 1_000_000.0;

        // 6. Access k-th element (sampleOps random lookups)
        Random rnd = new Random(42);
        int[] indices = new int[sampleOps];
        for (int i = 0; i < sampleOps; i++) indices[i] = rnd.nextInt(arrayList.size());

        tStart = System.nanoTime();
        long alDummy = 0;
        for (int idx : indices) alDummy += arrayList.get(idx).id;
        double alGetK = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        long llDummy = 0;
        for (int idx : indices) llDummy += linkedList.get(idx).id;
        double llGetK = (System.nanoTime() - tStart) / 1_000_000.0;

        // 7. Iterate through whole list
        tStart = System.nanoTime();
        long sumAL = 0;
        for (Song s : arrayList) sumAL += s.id;
        double alIterate = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        long sumLL = 0;
        for (Song s : linkedList) sumLL += s.id;
        double llIterate = (System.nanoTime() - tStart) / 1_000_000.0;

        tStart = System.nanoTime();
        long sumAD = 0;
        for (Song s : arrayDeque) sumAD += s.id;
        double adIterate = (System.nanoTime() - tStart) / 1_000_000.0;

        // Print report
        System.out.printf("%-24s | %-16s | %-16s | %-16s%n", "Operation", "ArrayList (ms)", "LinkedList (ms)", "ArrayDeque (ms)");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-24s | %16.3f | %16.3f | %16.3f%n", "Add at End (" + size + ")", alAddEnd, llAddEnd, adAddEnd);
        System.out.printf("%-24s | %16.3f | %16.3f | %16.3f%n", "Insert Front (" + sampleOps + ")", alInsertFront, llInsertFront, adInsertFront);
        System.out.printf("%-24s | %16.3f | %16.3f | %16s%n", "Insert Middle (" + sampleOps + ")", alInsertMid, llInsertMid, "N/A (No Index)");
        System.out.printf("%-24s | %16.3f | %16.3f | %16.3f%n", "Remove Front (" + sampleOps + ")", alRemoveFront, llRemoveFront, adRemoveFront);
        System.out.printf("%-24s | %16.3f | %16.3f | %16s%n", "Remove Middle (" + sampleOps + ")", alRemoveMid, llRemoveMid, "N/A (No Index)");
        System.out.printf("%-24s | %16.3f | %16.3f | %16s%n", "Get k-th Element (" + sampleOps + ")", alGetK, llGetK, "N/A (No Index)");
        System.out.printf("%-24s | %16.3f | %16.3f | %16.3f%n", "Full Iteration", alIterate, llIterate, adIterate);
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("Q2: Music Playlist Editor Data Structure Benchmark");
        System.out.println("Comparing ArrayList vs LinkedList vs ArrayDeque\n");

        benchmarkOperations(1000, 500);
        benchmarkOperations(10000, 1000);
        benchmarkOperations(50000, 2000);
    }
}
