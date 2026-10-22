package model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Donation {
    private int id;
    private int donorId;
    private String title;
    private String description;
    private String category;
    private int quantity;
    private String unit;
    private LocalDateTime expiryDate;
    private String pickupAddress;
    private String sourceType;
    private String sourceName;
    private String status;
    private Timestamp createdAt;
    
    private String donorName; // For joined queries

    public Donation() {}

    public Donation(int id, int donorId, String title, String description, String category, int quantity, String unit, LocalDateTime expiryDate, String pickupAddress, String sourceType, String sourceName, String status, Timestamp createdAt, String donorName) {
        this.id = id;
        this.donorId = donorId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.pickupAddress = pickupAddress;
        this.sourceType = sourceType;
        this.sourceName = sourceName;
        this.status = status;
        this.createdAt = createdAt;
        this.donorName = donorName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDonorId() { return donorId; }
    public void setDonorId(int donorId) { this.donorId = donorId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    @Override
    public String toString() {
        return "Donation{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", quantity=" + quantity + " " + unit +
                ", status='" + status + '\'' +
                '}';
    }
}
