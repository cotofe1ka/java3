import java.util.*;
import java.util.concurrent.*;
import java.util.Scanner;

public class TaxiSystem {
    private static int numberOfOrders;
    private static int taxiCount;
    private static double minDistance;
    private static double estimatedTimeSeconds;

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("AUTONOMOUS TAXI SYSTEM\n");

        final int MAX_ORDERS = 1000;
        final int MAX_TAXIS = 50;
        final double MAX_DISTANCE = 100.0;

        final int RECOMMENDED_ORDERS = 10;
        final int RECOMMENDED_TAXIS = 5;
        final double RECOMMENDED_DISTANCE = 20.0;

        System.out.println("WARNING: Program execution time depends on parameters.");
        System.out.println("Recommended values: " + RECOMMENDED_ORDERS + " orders, " +
                RECOMMENDED_TAXIS + " taxis, " + RECOMMENDED_DISTANCE + " min distance");
        System.out.println("With recommended values: execution ~20-30 seconds\n");

        System.out.printf("Enter number of orders to generate (1-%d):\n", MAX_ORDERS);
        System.out.printf("Recommended: %d | For values > 50, execution may exceed 1 minute\n", RECOMMENDED_ORDERS);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            try {
                if (input.isEmpty()) {
                    numberOfOrders = RECOMMENDED_ORDERS;
                    System.out.printf("Using recommended value: %d orders\n", RECOMMENDED_ORDERS);
                    break;
                }
                numberOfOrders = Integer.parseInt(input);
                if (numberOfOrders < 1) {
                    System.out.println("Error: Minimum 1 order required");
                } else if (numberOfOrders > MAX_ORDERS) {
                    System.out.printf("Error: Maximum %d orders allowed\n", MAX_ORDERS);
                } else {
                    if (numberOfOrders == RECOMMENDED_ORDERS) {
                        System.out.println("Using recommended value");
                    }
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }

        System.out.printf("\nEnter number of taxis (1-%d):\n", MAX_TAXIS);
        System.out.printf("Recommended: %d | More taxis = faster processing\n", RECOMMENDED_TAXIS);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            try {
                if (input.isEmpty()) {
                    taxiCount = RECOMMENDED_TAXIS;
                    System.out.printf("Using recommended value: %d taxis\n", RECOMMENDED_TAXIS);
                    break;
                }
                taxiCount = Integer.parseInt(input);
                if (taxiCount < 1) {
                    System.out.println("Error: Minimum 1 taxi required");
                } else if (taxiCount > MAX_TAXIS) {
                    System.out.printf("Error: Maximum %d taxis allowed\n", MAX_TAXIS);
                } else {
                    if (taxiCount == RECOMMENDED_TAXIS) {
                        System.out.println("Using recommended value");
                    }
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }

        System.out.printf("\nEnter minimum ride distance (1-%.1f):\n", MAX_DISTANCE);
        System.out.printf("Recommended: %.1f | For values > 50, execution may exceed 1 minute\n", RECOMMENDED_DISTANCE);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            try {
                if (input.isEmpty()) {
                    minDistance = RECOMMENDED_DISTANCE;
                    System.out.printf("Using recommended value: %.1f units\n", RECOMMENDED_DISTANCE);
                    break;
                }
                minDistance = Double.parseDouble(input);
                if (minDistance < 1.0) {
                    System.out.println("Error: Minimum distance is 1.0");
                } else if (minDistance > MAX_DISTANCE) {
                    System.out.printf("Error: Maximum distance is %.1f\n", MAX_DISTANCE);
                } else {
                    if (Math.abs(minDistance - RECOMMENDED_DISTANCE) < 0.01) {
                        System.out.println("Using recommended value");
                    }
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }

        scanner.close();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("[Main] ESTIMATED EXECUTION TIME CALCULATION");

        double avgDistance = 47.0;
        if (minDistance > avgDistance) {
            avgDistance = minDistance;
        }

        double timePerOrder = (avgDistance * 2) + 1.5;
        double throughput = taxiCount / timePerOrder;
        estimatedTimeSeconds = numberOfOrders / throughput + 5.0;

        double recommendedAvgDistance = 47.0;
        if (RECOMMENDED_DISTANCE > recommendedAvgDistance) {
            recommendedAvgDistance = RECOMMENDED_DISTANCE;
        }
        double recommendedTimePerOrder = (recommendedAvgDistance * 2) + 1.5;
        double recommendedThroughput = RECOMMENDED_TAXIS / recommendedTimePerOrder;
        double recommendedTimeSeconds = RECOMMENDED_ORDERS / recommendedThroughput + 5.0;

        System.out.printf("[Main] Estimated time for your configuration: %.1f seconds\n", estimatedTimeSeconds);
        System.out.printf("[Main] Time with recommended values: %.1f seconds\n", recommendedTimeSeconds);

        double timeDifference = estimatedTimeSeconds - recommendedTimeSeconds;
        if (timeDifference > 30) {
            System.out.printf("[Main] NOTE: Your configuration is %.1f seconds slower than recommended\n", timeDifference);
        } else if (timeDifference < -10) {
            System.out.printf("[Main] NOTE: Your configuration is %.1f seconds faster than recommended\n", -timeDifference);
        }

        if (estimatedTimeSeconds > 60) {
            System.out.println("[Main] WARNING: Execution may exceed 1 minute!");
            System.out.println("[Main] Consider reducing number of orders or increasing taxis.");
            System.out.println("[Main] Try: " + RECOMMENDED_ORDERS + " orders, " + RECOMMENDED_TAXIS +
                    " taxis, " + RECOMMENDED_DISTANCE + " min distance");
        } else if (estimatedTimeSeconds > 30) {
            System.out.println("[Main] Note: Execution may take 30-60 seconds");
        } else {
            System.out.println("[Main] Note: Execution should complete within 30 seconds");
        }

        boolean extremeValues = false;
        if (numberOfOrders > 100) {
            System.out.println("[Main] WARNING: High order count selected");
            extremeValues = true;
        }
        if (taxiCount == 1) {
            System.out.println("[Main] WARNING: Single taxi - sequential processing");
            extremeValues = true;
        }
        if (minDistance > 50) {
            System.out.println("[Main] WARNING: Long rides selected - increased travel time");
            extremeValues = true;
        }

        if (extremeValues) {
            System.out.println("[Main] For better performance, consider recommended values");
        }

        System.out.println("=".repeat(60) + "\n");

        System.out.println("[Main] CONFIGURATION SUMMARY");
        System.out.println("[Main]   Orders to generate: " + numberOfOrders +
                " (recommended: " + RECOMMENDED_ORDERS + ")");
        System.out.println("[Main]   Number of taxis: " + taxiCount +
                " (recommended: " + RECOMMENDED_TAXIS + ")");
        System.out.println("[Main]   Minimum distance: " + minDistance +
                " (recommended: " + RECOMMENDED_DISTANCE + ")");

        boolean allRecommended = (numberOfOrders == RECOMMENDED_ORDERS) &&
                (taxiCount == RECOMMENDED_TAXIS) &&
                (Math.abs(minDistance - RECOMMENDED_DISTANCE) < 0.01);

        if (allRecommended) {
            System.out.println("\n[Main] ✓ Using all recommended values!");
            System.out.println("[Main] Expected execution: 20-30 seconds");
        } else {
            System.out.println("\n[Main] Using custom configuration");
            System.out.printf("[Main] Estimated execution: %.1f seconds\n", estimatedTimeSeconds);
        }

        System.out.println("\n[Main] Starting system in 3 seconds...");
        Thread.sleep(3000);

        BlockingQueue<RideRequest> requestQueue = new LinkedBlockingQueue<>();
        Dispatcher dispatcher = new Dispatcher(requestQueue);

        List<Taxi> taxis = new ArrayList<>();
        List<Thread> taxiThreads = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= taxiCount; i++) {
            Location startLocation = new Location(
                    random.nextDouble() * 100,
                    random.nextDouble() * 100
            );
            Taxi taxi = new Taxi(i, startLocation, dispatcher);
            taxis.add(taxi);
            dispatcher.registerTaxi(taxi);

            Thread taxiThread = new Thread(taxi, "Taxi-" + i);
            taxiThreads.add(taxiThread);
        }

        CustomerGenerator generator = new CustomerGenerator(requestQueue, numberOfOrders, minDistance);
        Thread generatorThread = new Thread(generator, "Generator");

        Thread dispatcherThread = new Thread(dispatcher, "Dispatcher");

        final int finalNumberOfOrders = numberOfOrders;
        final double finalEstimatedTimeSeconds = estimatedTimeSeconds;
        final Dispatcher finalDispatcher = dispatcher;

        Thread statusMonitor = new Thread(() -> {
            try {
                int checkCount = 0;
                long startTime = System.currentTimeMillis();
                while (true) {
                    Thread.sleep(10000);
                    checkCount++;
                    long currentTime = System.currentTimeMillis();
                    long elapsedSeconds = (currentTime - startTime) / 1000;

                    System.out.printf("\n[Monitor] Check #%d (Elapsed: %d seconds)\n",
                            checkCount, elapsedSeconds);

                    if (finalNumberOfOrders > 0) {
                        double progressPercent = (finalDispatcher.getCompletedRides() * 100.0) / finalNumberOfOrders;
                        System.out.printf("[Monitor] Progress: %d/%d orders (%.1f%%)\n",
                                finalDispatcher.getCompletedRides(), finalNumberOfOrders, progressPercent);
                    }

                    finalDispatcher.printStatus();

                    if (elapsedSeconds > 120 && checkCount % 3 == 0) {
                        System.out.println("[Monitor] WARNING: Execution taking longer than 2 minutes");
                        if (elapsedSeconds > finalEstimatedTimeSeconds * 1.5) {
                            System.out.println("[Monitor] Execution is slower than estimated");
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("[Monitor] Shutting down");
                Thread.currentThread().interrupt();
            }
        }, "Monitor");
        statusMonitor.setDaemon(true);

        System.out.println("\n[Main] Starting threads...\n");

        for (Thread taxiThread : taxiThreads) {
            taxiThread.start();
            System.out.printf("[Main] Started %s\n", taxiThread.getName());
        }

        dispatcherThread.start();
        System.out.printf("[Main] Started %s\n", dispatcherThread.getName());

        generatorThread.start();
        System.out.printf("[Main] Started %s\n", generatorThread.getName());

        statusMonitor.start();
        System.out.printf("[Main] Started %s (daemon)\n", statusMonitor.getName());

        System.out.println("\n[Main] All threads started, system running...\n");

        generatorThread.join();
        System.out.printf("\n[Main] Generator finished creating %d orders\n", numberOfOrders);

        int waitCount = 0;
        while (!requestQueue.isEmpty()) {
            Thread.sleep(2000);
            waitCount++;
            System.out.printf("[Main] Queue: %d orders remaining (waiting %d seconds)\n",
                    requestQueue.size(), waitCount * 2);

            if (waitCount > 30) {
                System.out.println("[Main] WARNING: Queue processing is taking too long");
                System.out.println("[Main] Some orders may not have available taxis");
                break;
            }
        }

        System.out.println("\n[Main] Queue empty, waiting for rides to complete...");

        Thread.sleep(5000);

        System.out.println("\n[Main] Stopping system...");

        for (Thread taxiThread : taxiThreads) {
            taxiThread.interrupt();
            System.out.printf("[Main] Stopping %s\n", taxiThread.getName());
        }

        dispatcherThread.interrupt();
        System.out.printf("[Main] Stopping %s\n", dispatcherThread.getName());

        for (Thread taxiThread : taxiThreads) {
            taxiThread.join(1000);
        }

        dispatcherThread.join(1000);

        System.out.println("\n" + "=".repeat(70));
        System.out.println("[Main] FINAL REPORT");
        System.out.println("[Main] Configuration used:");
        System.out.printf("[Main]   Orders: %d (recommended: %d)\n",
                numberOfOrders, RECOMMENDED_ORDERS);
        System.out.printf("[Main]   Taxis: %d (recommended: %d)\n",
                taxiCount, RECOMMENDED_TAXIS);
        System.out.printf("[Main]   Min distance: %.1f (recommended: %.1f)\n",
                minDistance, RECOMMENDED_DISTANCE);

        long endTime = System.currentTimeMillis();
        long actualTimeSeconds = (endTime - System.currentTimeMillis() + 3000) / 1000;

        System.out.println("\n[Main] Performance summary:");
        System.out.printf("[Main]   Estimated time: %.1f seconds\n", estimatedTimeSeconds);
        System.out.printf("[Main]   Recommended time: %.1f seconds\n", recommendedTimeSeconds);

        if (estimatedTimeSeconds > 0) {
            double efficiency = (recommendedTimeSeconds / estimatedTimeSeconds) * 100;
            if (efficiency > 110) {
                System.out.printf("[Main]   Efficiency: %.1f%% (better than recommended)\n", efficiency);
            } else if (efficiency > 90) {
                System.out.printf("[Main]   Efficiency: %.1f%% (similar to recommended)\n", efficiency);
            } else {
                System.out.printf("[Main]   Efficiency: %.1f%% (slower than recommended)\n", efficiency);
            }
        }

        System.out.println();
        dispatcher.printStatus();

        if (!allRecommended && estimatedTimeSeconds > recommendedTimeSeconds * 1.2) {
            System.out.println("\n[Main] SUGGESTION: For faster execution next time, try:");
            System.out.printf("[Main]   Orders: %d, Taxis: %d, Min distance: %.1f\n",
                    RECOMMENDED_ORDERS, RECOMMENDED_TAXIS, RECOMMENDED_DISTANCE);
        }

        System.out.println("[Main] SYSTEM STOPPED");

    }
}