package model;

import java.sql.Timestamp;

public class DistributionSchedule {
    private int id;
    private int placeId;
    private String dayOfWeek;
    private String timeSlot;
    private String foodType;
    private int quantityTarget;
    private String unit;
    private String assignedVolunteer;
    private String volunteerPhone;
    private Integer donationId; // nullable
    private String status;
    private String notes;
    private boolean recurring;
    private Timestamp createdAt;

    // For joined queries
    private String placeName;
    private String placeCategory;

    public DistributionSchedule() {}

    public DistributionSchedule(int id, int placeId, String dayOfWeek, String timeSlot, String foodType, int quantityTarget, String unit, String assignedVolunteer, String volunteerPhone, Integer donationId, String status, String notes, boolean recurring, Timestamp createdAt, String placeName, String placeCategory) {
        this.id = id;
        this.placeId = placeId;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.foodType = foodType;
        this.quantityTarget = quantityTarget;
        this.unit = unit;
        this.assignedVolunteer = assignedVolunteer;
        this.volunteerPhone = volunteerPhone;
        this.donationId = donationId;
        this.status = status;
        this.notes = notes;
        this.recurring = recurring;
        this.createdAt = createdAt;
        this.placeName = placeName;
        this.placeCategory = placeCategory;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlaceId() { return placeId; }
    public void setPlaceId(int placeId) { this.placeId = placeId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public int getQuantityTarget() { return quantityTarget; }
    public void setQuantityTarget(int quantityTarget) { this.quantityTarget = quantityTarget; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getAssignedVolunteer() { return assignedVolunteer; }
    public void setAssignedVolunteer(String assignedVolunteer) { this.assignedVolunteer = assignedVolunteer; }

    public String getVolunteerPhone() { return volunteerPhone; }
    public void setVolunteerPhone(String volunteerPhone) { this.volunteerPhone = volunteerPhone; }

    public Integer getDonationId() { return donationId; }
    public void setDonationId(Integer donationId) { this.donationId = donationId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }

    public String getPlaceCategory() { return placeCategory; }
    public void setPlaceCategory(String placeCategory) { this.placeCategory = placeCategory; }

    @Override
    public String toString() {
        return "DistributionSchedule{" +
                "id=" + id +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", foodType='" + foodType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
