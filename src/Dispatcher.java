import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class Dispatcher implements Runnable {
    private final List<Taxi> taxis;
    private final BlockingQueue<RideRequest> requestQueue;
    private final AtomicInteger completedRides;
    private final ReentrantLock dispatchLock;

    public Dispatcher(BlockingQueue<RideRequest> requestQueue) {
        this.taxis = new CopyOnWriteArrayList<>();
        this.requestQueue = requestQueue;
        this.completedRides = new AtomicInteger(0);
        this.dispatchLock = new ReentrantLock();
    }

    public int getCompletedRides() {
        return completedRides.get();
    }

    public void registerTaxi(Taxi taxi) {
        taxis.add(taxi);
        System.out.printf("[Dispatcher] Registered Taxi-%d at %s\n",
                taxi.getId(), taxi.getCurrentLocation());
    }

    private Taxi findNearestAvailableTaxi(Location location) {
        Taxi nearestTaxi = null;
        double minDistance = Double.MAX_VALUE;

        for (Taxi taxi : taxis) {
            if (taxi.isAvailable()) {
                double distance = taxi.distanceTo(location);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestTaxi = taxi;
                }
            }
        }

        return nearestTaxi;
    }

    @Override
    public void run() {
        try {
            while (true) {
                RideRequest request = requestQueue.take();

                System.out.printf("[Dispatcher] New order in queue: %s\n", request);

                boolean assigned = false;
                int attempts = 0;
                final int MAX_ATTEMPTS = 3;

                while (!assigned && attempts < MAX_ATTEMPTS) {
                    dispatchLock.lock();
                    try {
                        Taxi taxi = findNearestAvailableTaxi(request.getPickupLocation());

                        if (taxi != null && taxi.assignRequest(request)) {
                            System.out.printf("[Dispatcher] Order #%d assigned to Taxi-%d\n",
                                    request.getId(), taxi.getId());
                            assigned = true;
                        }
                    } finally {
                        dispatchLock.unlock();
                    }

                    if (!assigned) {
                        attempts++;
                        System.out.printf("[Dispatcher] No available taxis for order #%d (attempt %d)\n",
                                request.getId(), attempts);
                        Thread.sleep(1000);
                    }
                }

                if (!assigned) {
                    System.out.printf("[Dispatcher] Failed to find taxi for order #%d\n",
                            request.getId());
                }
            }
        } catch (InterruptedException e) {
            System.out.println("[Dispatcher] Shutting down");
            Thread.currentThread().interrupt();
        }
    }

    public void notifyRideComplete(Taxi taxi, RideRequest request) {
        completedRides.incrementAndGet();
        System.out.printf("[Dispatcher] Ride #%d completed. Total: %d\n",
                request.getId(), completedRides.get());
    }

    public void printStatus() {
        System.out.println("\n[Status] SYSTEM INFORMATION");
        System.out.printf("[Status] Taxis: %d | Completed rides: %d | Queue: %d\n",
                taxis.size(), completedRides.get(), requestQueue.size());

        int available = 0;
        for (Taxi taxi : taxis) {
            if (taxi.isAvailable()) {
                available++;
                System.out.printf("[Status] Taxi-%d: available at %s\n",
                        taxi.getId(), taxi.getCurrentLocation());
            } else {
                System.out.printf("[Status] Taxi-%d: busy with order\n", taxi.getId());
            }
        }
        System.out.printf("[Status] Available taxis: %d\n", available);
        System.out.println();
    }
}