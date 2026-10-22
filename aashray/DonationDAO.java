package dao;

import db.DatabaseConnection;
import model.Donation;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonationDAO {

    public boolean create(Donation d) {
        String sql = "INSERT INTO donations (donor_id, title, description, category, quantity, unit, expiry_date, pickup_address, source_type, source_name, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, d.getDonorId());
            pstmt.setString(2, d.getTitle());
            pstmt.setString(3, d.getDescription());
            pstmt.setString(4, d.getCategory());
            pstmt.setInt(5, d.getQuantity());
            pstmt.setString(6, d.getUnit());
            pstmt.setTimestamp(7, d.getExpiryDate() != null ? Timestamp.valueOf(d.getExpiryDate()) : null);
            pstmt.setString(8, d.getPickupAddress());
            pstmt.setString(9, d.getSourceType());
            pstmt.setString(10, d.getSourceName());
            pstmt.setString(11, d.getStatus());
            pstmt.setTimestamp(12, d.getCreatedAt() != null ? d.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Donation d) {
        String sql = "UPDATE donations SET title=?, description=?, category=?, quantity=?, unit=?, expiry_date=?, pickup_address=?, source_type=?, source_name=?, status=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, d.getTitle());
            pstmt.setString(2, d.getDescription());
            pstmt.setString(3, d.getCategory());
            pstmt.setInt(4, d.getQuantity());
            pstmt.setString(5, d.getUnit());
            pstmt.setTimestamp(6, d.getExpiryDate() != null ? Timestamp.valueOf(d.getExpiryDate()) : null);
            pstmt.setString(7, d.getPickupAddress());
            pstmt.setString(8, d.getSourceType());
            pstmt.setString(9, d.getSourceName());
            pstmt.setString(10, d.getStatus());
            pstmt.setInt(11, d.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Donation findById(int id) {
        String sql = "SELECT d.*, u.name as donor_name FROM donations d LEFT JOIN users u ON d.donor_id = u.id WHERE d.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDonationFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Donation> findAvailable() {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.*, u.name as donor_name FROM donations d LEFT JOIN users u ON d.donor_id = u.id WHERE d.status = 'available' AND d.expiry_date > NOW() ORDER BY d.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(extractDonationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Donation> findByDonor(int donorId) {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.*, u.name as donor_name FROM donations d LEFT JOIN users u ON d.donor_id = u.id WHERE d.donor_id = ? ORDER BY d.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, donorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractDonationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE donations SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> countBySourceType() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT source_type, COUNT(*) as count FROM donations GROUP BY source_type";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                counts.put(rs.getString("source_type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    public List<Map<String, Object>> countMonthlyBySource() {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT MONTH(created_at) as month, YEAR(created_at) as year, source_type, COUNT(*) as count FROM donations GROUP BY year, month, source_type ORDER BY year, month";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("month", rs.getInt("month"));
                row.put("year", rs.getInt("year"));
                row.put("source_type", rs.getString("source_type"));
                row.put("count", rs.getInt("count"));
                results.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<Donation> findExpiring() {
        List<Donation> list = new ArrayList<>();
        String sql = "SELECT d.*, u.name as donor_name FROM donations d LEFT JOIN users u ON d.donor_id = u.id WHERE d.status = 'available' AND d.expiry_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 24 HOUR)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(extractDonationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) as total FROM donations";
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

    private Donation extractDonationFromResultSet(ResultSet rs) throws SQLException {
        Donation d = new Donation();
        d.setId(rs.getInt("id"));
        d.setDonorId(rs.getInt("donor_id"));
        d.setTitle(rs.getString("title"));
        d.setDescription(rs.getString("description"));
        d.setCategory(rs.getString("category"));
        d.setQuantity(rs.getInt("quantity"));
        d.setUnit(rs.getString("unit"));
        Timestamp expiry = rs.getTimestamp("expiry_date");
        if (expiry != null) {
            d.setExpiryDate(expiry.toLocalDateTime());
        }
        d.setPickupAddress(rs.getString("pickup_address"));
        d.setSourceType(rs.getString("source_type"));
        d.setSourceName(rs.getString("source_name"));
        d.setStatus(rs.getString("status"));
        d.setCreatedAt(rs.getTimestamp("created_at"));
        return d;
    }
}
