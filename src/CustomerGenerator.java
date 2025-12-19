import java.util.Random;
import java.util.concurrent.BlockingQueue;

class CustomerGenerator implements Runnable {
    private final BlockingQueue<RideRequest> requestQueue;
    private final Random random;
    private volatile boolean running;
    private final int totalOrders;
    private final double minDistance;

    public CustomerGenerator(BlockingQueue<RideRequest> requestQueue) {
        this(requestQueue, 10, 10.0);
    }

    public CustomerGenerator(BlockingQueue<RideRequest> requestQueue, int totalOrders, double minDistance) {
        this.requestQueue = requestQueue;
        this.random = new Random();
        this.running = true;
        this.totalOrders = totalOrders;
        this.minDistance = minDistance;

        if (minDistance > 100) {
            System.err.println("[Generator] WARNING: Minimum distance exceeds map bounds (100x100)");
        }
    }

    public void stop() {
        running = false;
    }

    private Location generateRandomLocation() {
        double x = random.nextDouble() * 100;
        double y = random.nextDouble() * 100;
        return new Location(x, y);
    }

    @Override
    public void run() {
        try {
            int requestCount = 0;
            int attempts = 0;
            final int MAX_ATTEMPTS = 1000;

            while (running && requestCount < totalOrders) {
                Thread.sleep(random.nextInt(1500) + 500);

                Location pickup = generateRandomLocation();
                Location destination = generateRandomLocation();
                attempts = 0;

                while (pickup.distanceTo(destination) < minDistance && attempts < MAX_ATTEMPTS) {
                    destination = generateRandomLocation();
                    attempts++;

                    if (attempts >= MAX_ATTEMPTS) {
                        System.err.printf("[Generator] WARNING: Could not generate valid destination after %d attempts\n", MAX_ATTEMPTS);
                        break;
                    }
                }

                RideRequest request = new RideRequest(pickup, destination);
                requestQueue.put(request);
                requestCount++;

                System.out.printf("[Generator] Created order #%d (%d/%d) distance: %.2f\n",
                        request.getId(), requestCount, totalOrders, pickup.distanceTo(destination));
            }

            System.out.printf("[Generator] Finished creating %d orders\n", totalOrders);

            if (attempts >= MAX_ATTEMPTS) {
                System.err.println("[Generator] WARNING: Some orders may have invalid distances");
            }

        } catch (InterruptedException e) {
            System.out.println("[Generator] Interrupted");
            Thread.currentThread().interrupt();
        }
    }
}