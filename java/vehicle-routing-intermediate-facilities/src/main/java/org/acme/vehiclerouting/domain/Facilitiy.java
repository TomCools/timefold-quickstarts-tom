package org.acme.vehiclerouting.domain;

public record Facilitiy(String name, Location location) implements LocationAware {
    @Override
    public Location getLocation() {
        return location;
    }
}
