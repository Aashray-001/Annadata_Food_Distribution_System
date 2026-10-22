package model;

import java.sql.Timestamp;

public class EmergencyAid {
    private int id;
    private int recipientId;
    private String title;
    private String description;
    private String emergencyType;
    private String urgency;
    private int peopleAffected;
    private String location;
    private String requiredItems;
    private int requiredQuantity;
    private String contactPhone;
    private String status;
    private Timestamp createdAt;

    private String recipientName; // For joined queries

    public EmergencyAid() {}

    public EmergencyAid(int id, int recipientId, String title, String description, String emergencyType, String urgency, int peopleAffected, String location, String requiredItems, int requiredQuantity, String contactPhone, String status, Timestamp createdAt, String recipientName) {
        this.id = id;
        this.recipientId = recipientId;
        this.title = title;
        this.description = description;
        this.emergencyType = emergencyType;
        this.urgency = urgency;
        this.peopleAffected = peopleAffected;
        this.location = location;
        this.requiredItems = requiredItems;
        this.requiredQuantity = requiredQuantity;
        this.contactPhone = contactPhone;
        this.status = status;
        this.createdAt = createdAt;
        this.recipientName = recipientName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmergencyType() { return emergencyType; }
    public void setEmergencyType(String emergencyType) { this.emergencyType = emergencyType; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public int getPeopleAffected() { return peopleAffected; }
    public void setPeopleAffected(int peopleAffected) { this.peopleAffected = peopleAffected; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRequiredItems() { return requiredItems; }
    public void setRequiredItems(String requiredItems) { this.requiredItems = requiredItems; }

    public int getRequiredQuantity() { return requiredQuantity; }
    public void setRequiredQuantity(int requiredQuantity) { this.requiredQuantity = requiredQuantity; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    @Override
    public String toString() {
        return "EmergencyAid{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", urgency='" + urgency + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
