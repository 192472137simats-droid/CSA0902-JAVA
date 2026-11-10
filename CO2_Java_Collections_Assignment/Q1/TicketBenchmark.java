import java.util.Arrays;

/**
 * Q1: Building Text Under Load: String, StringBuilder or StringBuffer?
 * Problem Context: Railway e-Ticket Generation
 * 
 * Compares String (+ operator), StringBuilder, and StringBuffer under 3 contrasting workloads.
 */
public class TicketBenchmark {

    // Dummy ticket field templates
    private static final String[] FIELD_NAMES = {
        "PNR", "TrainNo", "TrainName", "Origin", "Destination",
        "JourneyDate", "Class", "Quota", "Coach", "Berth",
        "BerthType", "Passenger1", "Age1", "Gender1", "Status1",
        "Passenger2", "Age2", "Gender2", "Status2", "Fare",
        "BaseFare", "ReservationFee", "SuperfastCharge", "GST", "TotalFare",
        "PaymentMode", "TransactionId", "BookingTime", "GateNo", "Platform",
        "BoardingStation", "ScheduledDeparture", "ScheduledArrival", "DistanceKm", "FoodChoice",
        "InsuranceOpted", "InsurancePolicyNo", "IdProofType", "IdProofNo", "ContactMobile",
        "ContactEmail", "IRCTCRef", "QRData", "BarcodeData", "TermsAccepted",
        "CateringService", "RefundPolicyCode", "SecurityChecksum", "AgentId", "FooterNote"
    };

    public static String buildWithPlus(int fieldCount) {
        String ticket = "";
        for (int i = 0; i < fieldCount; i++) {
            ticket = ticket + FIELD_NAMES[i % FIELD_NAMES.length] + ": VAL_" + i + " | ";
        }
        return ticket;
    }

    public static String buildWithStringBuilder(int fieldCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldCount; i++) {
            sb.append(FIELD_NAMES[i % FIELD_NAMES.length]).append(": VAL_").append(i).append(" | ");
        }
        return sb.toString();
    }

    public static String buildWithStringBuffer(int fieldCount) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fieldCount; i++) {
            sb.append(FIELD_NAMES[i % FIELD_NAMES.length]).append(": VAL_").append(i).append(" | ");
        }
        return sb.toString();
    }

    private static void runWorkload(String name, int ticketCount, int fieldsPerTicket, int runs) {
        System.out.println("======================================================================");
        System.out.println("Workload: " + name + " (" + ticketCount + " tickets, " + fieldsPerTicket + " fields/ticket)");
        System.out.println("======================================================================");

        // 1. String (+)
        // Warmup
        buildWithPlus(Math.min(fieldsPerTicket, 10));
        long totalTimePlus = 0;
        long memBeforePlus = getUsedMemory();
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < ticketCount; t++) {
                String s = buildWithPlus(fieldsPerTicket);
            }
            totalTimePlus += (System.nanoTime() - start);
        }
        long memAfterPlus = getUsedMemory();
        double avgMsPlus = (totalTimePlus / (double) runs) / 1_000_000.0;
        long estimatedPlusObjects = (long) ticketCount * fieldsPerTicket * 2; // intermediate objects

        // 2. StringBuilder
        buildWithStringBuilder(fieldsPerTicket);
        long totalTimeBuilder = 0;
        long memBeforeBuilder = getUsedMemory();
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < ticketCount; t++) {
                String s = buildWithStringBuilder(fieldsPerTicket);
            }
            totalTimeBuilder += (System.nanoTime() - start);
        }
        long memAfterBuilder = getUsedMemory();
        double avgMsBuilder = (totalTimeBuilder / (double) runs) / 1_000_000.0;
        long estimatedBuilderObjects = ticketCount; // only final String

        // 3. StringBuffer
        buildWithStringBuffer(fieldsPerTicket);
        long totalTimeBuffer = 0;
        long memBeforeBuffer = getUsedMemory();
        for (int r = 0; r < runs; r++) {
            long start = System.nanoTime();
            for (int t = 0; t < ticketCount; t++) {
                String s = buildWithStringBuffer(fieldsPerTicket);
            }
            totalTimeBuffer += (System.nanoTime() - start);
        }
        long memAfterBuffer = getUsedMemory();
        double avgMsBuffer = (totalTimeBuffer / (double) runs) / 1_000_000.0;
        long estimatedBufferObjects = ticketCount;

        System.out.printf("%-16s | %-15s | %-24s | %-18s%n", "Approach", "Avg Time (ms)", "Est. Intermediate Objs", "Memory Delta (KB)");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-16s | %15.3f | %24d | %18.2f%n", "String (+)", avgMsPlus, estimatedPlusObjects, (memAfterPlus - memBeforePlus) / 1024.0);
        System.out.printf("%-16s | %15.3f | %24d | %18.2f%n", "StringBuilder", avgMsBuilder, estimatedBuilderObjects, (memAfterBuilder - memBeforeBuilder) / 1024.0);
        System.out.printf("%-16s | %15.3f | %24d | %18.2f%n", "StringBuffer", avgMsBuffer, estimatedBufferObjects, (memAfterBuffer - memBeforeBuffer) / 1024.0);
        System.out.println();
    }

    private static long getUsedMemory() {
        System.gc();
        try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static void main(String[] args) {
        System.out.println("Q1: Railway e-Ticket Generation Benchmark");
        System.out.println("Comparing String (+), StringBuilder, and StringBuffer\n");

        // Workload 1: 1 short ticket with 5 fields
        runWorkload("Workload 1 (1 short ticket)", 1, 5, 5);

        // Workload 2: 10,000 tickets with 50 fields each
        runWorkload("Workload 2 (10,000 heavy tickets)", 10000, 50, 3);

        // Workload 3: 50,000 tickets with 20 fields each
        runWorkload("Workload 3 (50,000 burst tickets)", 50000, 20, 3);

        System.out.println("Verification Sample Ticket Output (StringBuilder):");
        System.out.println(buildWithStringBuilder(8));
    }
}
