package model;

import java.sql.Timestamp;

public class Request {
    private int id;
    private int donationId;
    private int recipientId;
    private String status;
    private String message;
    private Timestamp createdAt;

    // For joined queries
    private String donorName;
    private String donationTitle;
    private String recipientName;

    public Request() {}

    public Request(int id, int donationId, int recipientId, String status, String message, Timestamp createdAt, String donorName, String donationTitle, String recipientName) {
        this.id = id;
        this.donationId = donationId;
        this.recipientId = recipientId;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.donorName = donorName;
        this.donationTitle = donationTitle;
        this.recipientName = recipientName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDonationId() { return donationId; }
    public void setDonationId(int donationId) { this.donationId = donationId; }

    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getDonationTitle() { return donationTitle; }
    public void setDonationTitle(String donationTitle) { this.donationTitle = donationTitle; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", donationId=" + donationId +
                ", status='" + status + '\'' +
                '}';
    }
}
