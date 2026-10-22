package dao;

import db.DatabaseConnection;
import model.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public boolean create(Request r) {
        String sql = "INSERT INTO requests (donation_id, recipient_id, status, message, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, r.getDonationId());
            pstmt.setInt(2, r.getRecipientId());
            pstmt.setString(3, r.getStatus());
            pstmt.setString(4, r.getMessage());
            pstmt.setTimestamp(5, r.getCreatedAt() != null ? r.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Request> findByDonation(int donationId) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT r.*, u.name as recipient_name FROM requests r JOIN users u ON r.recipient_id = u.id WHERE r.donation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, donationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Request req = extractRequestFromResultSet(rs);
                    // Add recipientName field temporarily by sub-classing or if it's available in the Model.
                    // Assuming Request model might have those fields or we simply set it.
                    // Note: If Request doesn't have these, you may need to extend Request or add fields.
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Request> findByRecipient(int recipientId) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT r.*, d.title as donation_title, u.name as donor_name FROM requests r " +
                     "JOIN donations d ON r.donation_id = d.id " +
                     "JOIN users u ON d.donor_id = u.id " +
                     "WHERE r.recipient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, recipientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Request req = extractRequestFromResultSet(rs);
                    req.setDonationTitle(rs.getString("donation_title"));
                    req.setDonorName(rs.getString("donor_name"));
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE requests SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) as total FROM requests";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Request extractRequestFromResultSet(ResultSet rs) throws SQLException {
        Request r = new Request();
        r.setId(rs.getInt("id"));
        r.setDonationId(rs.getInt("donation_id"));
        r.setRecipientId(rs.getInt("recipient_id"));
        r.setStatus(rs.getString("status"));
        r.setMessage(rs.getString("message"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
