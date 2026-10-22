package dao;

import db.DatabaseConnection;
import model.DistributionPlace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistributionPlaceDAO {

    public boolean create(DistributionPlace p) {
        String sql = "INSERT INTO distribution_places (name, category, address, contact_person, contact_phone, operating_hours, capacity, notes, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, p.getName());
            pstmt.setString(2, p.getCategory());
            pstmt.setString(3, p.getAddress());
            pstmt.setString(4, p.getContactPerson());
            pstmt.setString(5, p.getContactPhone());
            pstmt.setString(6, p.getOperatingHours());
            pstmt.setInt(7, p.getCapacity());
            pstmt.setString(8, p.getNotes());
            pstmt.setBoolean(9, p.isActive());
            pstmt.setTimestamp(10, p.getCreatedAt() != null ? p.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(DistributionPlace p) {
        String sql = "UPDATE distribution_places SET name=?, category=?, address=?, contact_person=?, contact_phone=?, operating_hours=?, capacity=?, notes=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, p.getName());
            pstmt.setString(2, p.getCategory());
            pstmt.setString(3, p.getAddress());
            pstmt.setString(4, p.getContactPerson());
            pstmt.setString(5, p.getContactPhone());
            pstmt.setString(6, p.getOperatingHours());
            pstmt.setInt(7, p.getCapacity());
            pstmt.setString(8, p.getNotes());
            pstmt.setBoolean(9, p.isActive());
            pstmt.setInt(10, p.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM distribution_places WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DistributionPlace> findAll() {
        List<DistributionPlace> list = new ArrayList<>();
        String sql = "SELECT * FROM distribution_places";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(extractDistributionPlaceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DistributionPlace> findByCategory(String category) {
        List<DistributionPlace> list = new ArrayList<>();
        String sql = "SELECT * FROM distribution_places WHERE category = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, category);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(extractDistributionPlaceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DistributionPlace> findActive() {
        List<DistributionPlace> list = new ArrayList<>();
        String sql = "SELECT * FROM distribution_places WHERE is_active = TRUE";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(extractDistributionPlaceFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public DistributionPlace findById(int id) {
        String sql = "SELECT * FROM distribution_places WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractDistributionPlaceFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private DistributionPlace extractDistributionPlaceFromResultSet(ResultSet rs) throws SQLException {
        DistributionPlace p = new DistributionPlace();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategory(rs.getString("category"));
        p.setAddress(rs.getString("address"));
        p.setContactPerson(rs.getString("contact_person"));
        p.setContactPhone(rs.getString("contact_phone"));
        p.setOperatingHours(rs.getString("operating_hours"));
        p.setCapacity(rs.getInt("capacity"));
        p.setNotes(rs.getString("notes"));
        p.setActive(rs.getBoolean("is_active"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
