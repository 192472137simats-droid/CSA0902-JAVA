/**
 * Q6 Part A: Hostel Wallet Transfers - Deadlock Demonstration and Prevention
 */
public class WalletDeadlock {

    static class Account {
        final int id;
        double balance;

        Account(int id, double balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    // Safe transfer using global lock ordering by Account ID
    public static void transferSafe(Account from, Account to, double amount) {
        Account firstLock = from.id < to.id ? from : to;
        Account secondLock = from.id < to.id ? to : from;

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (from.balance >= amount) {
                    from.balance -= amount;
                    to.balance += amount;
                    System.out.printf("Successfully transferred Rs %.2f from Acc-%d to Acc-%d%n",
                            amount, from.id, to.id);
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Demonstrating Safe Lock Ordering in Wallet Transfers ---");
        Account accA = new Account(101, 5000);
        Account accB = new Account(102, 5000);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 50; i++) transferSafe(accA, accB, 10);
        }, "TransferThread-AtoB");

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 50; i++) transferSafe(accB, accA, 10);
        }, "TransferThread-BtoA");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.printf("Final Balances -> Acc-101: Rs %.2f | Acc-102: Rs %.2f (Deadlock completely prevented!)%n",
                accA.balance, accB.balance);
    }
}
