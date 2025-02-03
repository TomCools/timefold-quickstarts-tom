package org.acme.maintenancescheduling.domain;

import java.util.Objects;

public class Equipment {
    private String id;
    private int capacity;

    public Equipment() {
    }

    public Equipment(String id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return capacity == equipment.capacity && Objects.equals(id, equipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, capacity);
    }
}
