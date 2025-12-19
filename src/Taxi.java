import java.util.concurrent.locks.ReentrantLock;

class Taxi implements Runnable {
    private final int id;
    private volatile Location currentLocation;
    private volatile boolean available;
    private volatile RideRequest currentRequest;
    private final ReentrantLock lock;
    private final Dispatcher dispatcher;

    public Taxi(int id, Location startLocation, Dispatcher dispatcher) {
        this.id = id;
        this.currentLocation = startLocation;
        this.available = true;
        this.currentRequest = null;
        this.lock = new ReentrantLock();
        this.dispatcher = dispatcher;
    }

    public int getId() { return id; }

    public Location getCurrentLocation() {
        lock.lock();
        try {
            return currentLocation;
        } finally {
            lock.unlock();
        }
    }

    public boolean isAvailable() {
        lock.lock();
        try {
            return available;
        } finally {
            lock.unlock();
        }
    }

    public RideRequest getCurrentRequest() {
        lock.lock();
        try {
            return currentRequest;
        } finally {
            lock.unlock();
        }
    }

    public double distanceTo(Location location) {
        lock.lock();
        try {
            return currentLocation.distanceTo(location);
        } finally {
            lock.unlock();
        }
    }

    public boolean assignRequest(RideRequest request) {
        lock.lock();
        try {
            if (available) {
                currentRequest = request;
                available = false;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void simulateMovement(Location target, String action, String passengerInfo) throws InterruptedException {
        lock.lock();
        try {
            double distance = currentLocation.distanceTo(target);
            int travelTime = (int) (distance * 1000);
            System.out.printf("[Taxi-%d] %s %s to %s (%.2f units, %d ms)\n",
                    id, passengerInfo, action, target, distance, travelTime);

            if (travelTime > 0) {
                Thread.sleep(travelTime);
                currentLocation = target;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (isAvailable()) {
                    Thread.sleep(100);
                }

                RideRequest request = getCurrentRequest();
                if (request == null) continue;

                System.out.printf("[Taxi-%d] Received order %s\n", id, request);

                try {
                    simulateMovement(request.getPickupLocation(), "driving", "To pick up");

                    System.out.printf("[Taxi-%d] Arrived at client, boarding\n", id);
                    Thread.sleep(1000);

                    simulateMovement(request.getDestination(), "driving", "With passenger");

                    System.out.printf("[Taxi-%d] Passenger delivered, disembarking\n", id);
                    Thread.sleep(500);

                    System.out.printf("[Taxi-%d] Ride #%d completed\n", id, request.getId());

                } finally {
                    lock.lock();
                    try {
                        available = true;
                        currentRequest = null;
                    } finally {
                        lock.unlock();
                    }

                    dispatcher.notifyRideComplete(this, request);
                }
            }
        } catch (InterruptedException e) {
            System.out.printf("[Taxi-%d] Shutting down\n", id);
            Thread.currentThread().interrupt();
        }
    }
}