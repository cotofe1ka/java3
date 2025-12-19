import java.util.concurrent.atomic.AtomicInteger;

class RideRequest {
    private static final AtomicInteger idCounter = new AtomicInteger(1);
    private final int id;
    private final Location pickupLocation;
    private final Location destination;
    private final long timestamp;

    public RideRequest(Location pickupLocation, Location destination) {
        this.id = idCounter.getAndIncrement();
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.timestamp = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public Location getPickupLocation() { return pickupLocation; }
    public Location getDestination() { return destination; }
    public long getTimestamp() { return timestamp; }

    public double getRideDistance() {
        return pickupLocation.distanceTo(destination);
    }

    @Override
    public String toString() {
        return String.format("#%d: %s -> %s (%.2f km)",
                id, pickupLocation, destination, getRideDistance());
    }
}