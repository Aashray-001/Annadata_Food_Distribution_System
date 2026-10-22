package dao;

import db.DatabaseConnection;
import model.EmergencyAid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmergencyAidDAO {

    public boolean create(EmergencyAid e) {
        String sql = "INSERT INTO emergency_aids (recipient_id, title, description, emergency_type, urgency, people_affected, location, required_items, required_quantity, contact_phone, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, e.getRecipientId());
            pstmt.setString(2, e.getTitle());
            pstmt.setString(3, e.getDescription());
            pstmt.setString(4, e.getEmergencyType());
            pstmt.setString(5, e.getUrgency());
            pstmt.setInt(6, e.getPeopleAffected());
            pstmt.setString(7, e.getLocation());
            pstmt.setString(8, e.getRequiredItems());
            pstmt.setInt(9, e.getRequiredQuantity());
            pstmt.setString(10, e.getContactPhone());
            pstmt.setString(11, e.getStatus());
            pstmt.setTimestamp(12, e.getCreatedAt() != null ? e.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<EmergencyAid> findAll() {
        List<EmergencyAid> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name as recipient_name FROM emergency_aids e LEFT JOIN users u ON e.recipient_id = u.id ORDER BY FIELD(e.urgency, 'critical', 'high', 'medium'), e.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                EmergencyAid aid = extractEmergencyAidFromResultSet(rs);
                aid.setRecipientName(rs.getString("recipient_name"));
                list.add(aid);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<EmergencyAid> findActive() {
        List<EmergencyAid> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name as recipient_name FROM emergency_aids e LEFT JOIN users u ON e.recipient_id = u.id WHERE e.status IN ('open', 'responding', 'partially_fulfilled') ORDER BY FIELD(e.urgency, 'critical', 'high', 'medium'), e.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                EmergencyAid aid = extractEmergencyAidFromResultSet(rs);
                aid.setRecipientName(rs.getString("recipient_name"));
                list.add(aid);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<EmergencyAid> findByRecipient(int recipientId) {
        List<EmergencyAid> list = new ArrayList<>();
        String sql = "SELECT e.*, u.name as recipient_name FROM emergency_aids e LEFT JOIN users u ON e.recipient_id = u.id WHERE e.recipient_id = ? ORDER BY e.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, recipientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EmergencyAid aid = extractEmergencyAidFromResultSet(rs);
                    aid.setRecipientName(rs.getString("recipient_name"));
                    list.add(aid);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE emergency_aids SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private EmergencyAid extractEmergencyAidFromResultSet(ResultSet rs) throws SQLException {
        EmergencyAid e = new EmergencyAid();
        e.setId(rs.getInt("id"));
        e.setRecipientId(rs.getInt("recipient_id"));
        e.setTitle(rs.getString("title"));
        e.setDescription(rs.getString("description"));
        e.setEmergencyType(rs.getString("emergency_type"));
        e.setUrgency(rs.getString("urgency"));
        e.setPeopleAffected(rs.getInt("people_affected"));
        e.setLocation(rs.getString("location"));
        e.setRequiredItems(rs.getString("required_items"));
        e.setRequiredQuantity(rs.getInt("required_quantity"));
        e.setContactPhone(rs.getString("contact_phone"));
        e.setStatus(rs.getString("status"));
        e.setCreatedAt(rs.getTimestamp("created_at"));
        return e;
    }
}
