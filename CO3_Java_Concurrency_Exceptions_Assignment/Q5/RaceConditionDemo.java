/**
 * Q5: Race Condition Analysis and Comparative Synchronization Strategies
 */
public class RaceConditionDemo {

    static class UnsafeSeatBooking {
        private final boolean[] booked;
        private int bookingsMade = 0;

        UnsafeSeatBooking(int seats) {
            this.booked = new boolean[seats];
        }

        // Unsynchronized check-then-act bug
        void book(int seat) {
            if (!booked[seat]) {
                try {
                    // Small delay to reliably expose race condition
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}
                booked[seat] = true;
                bookingsMade++;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Demonstrating Unsynchronized Race Condition (10 Runs) ---");
        for (int run = 1; run <= 5; run++) {
            UnsafeSeatBooking unsafeScreen = new UnsafeSeatBooking(10);
            Thread[] users = new Thread[100];
            for (int i = 0; i < users.length; i++) {
                final int seat = i % 10;
                users[i] = new Thread(() -> unsafeScreen.book(seat));
                users[i].start();
            }
            for (Thread t : users) t.join();
            System.out.printf("Run %d: Total seats sold = %d (EXCEEDS 10! Double bookings detected)%n",
                    run, unsafeScreen.bookingsMade);
        }
    }
}
