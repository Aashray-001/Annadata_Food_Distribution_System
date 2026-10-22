package model;

import java.sql.Timestamp;

public class DistributionPlace {
    private int id;
    private String name;
    private String category;
    private String address;
    private String contactPerson;
    private String contactPhone;
    private String operatingHours;
    private int capacity;
    private String notes;
    private boolean active;
    private Timestamp createdAt;

    public DistributionPlace() {}

    public DistributionPlace(int id, String name, String category, String address, String contactPerson, String contactPhone, String operatingHours, int capacity, String notes, boolean active, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.address = address;
        this.contactPerson = contactPerson;
        this.contactPhone = contactPhone;
        this.operatingHours = operatingHours;
        this.capacity = capacity;
        this.notes = notes;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "DistributionPlace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
