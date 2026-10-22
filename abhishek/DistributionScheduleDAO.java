package dao;

import db.DatabaseConnection;
import model.DistributionSchedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DistributionScheduleDAO {

    public boolean create(DistributionSchedule s) {
        String sql = "INSERT INTO distribution_schedule (place_id, day_of_week, time_slot, food_type, quantity_target, unit, assigned_volunteer, volunteer_phone, donation_id, status, notes, recurring, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, s.getPlaceId());
            pstmt.setString(2, s.getDayOfWeek());
            pstmt.setString(3, s.getTimeSlot());
            pstmt.setString(4, s.getFoodType());
            pstmt.setInt(5, s.getQuantityTarget());
            pstmt.setString(6, s.getUnit());
            pstmt.setString(7, s.getAssignedVolunteer());
            pstmt.setString(8, s.getVolunteerPhone());
            if (s.getDonationId() != null) {
                pstmt.setInt(9, s.getDonationId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            pstmt.setString(10, s.getStatus());
            pstmt.setString(11, s.getNotes());
            pstmt.setBoolean(12, s.isRecurring());
            pstmt.setTimestamp(13, s.getCreatedAt() != null ? s.getCreatedAt() : new Timestamp(System.currentTimeMillis()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(DistributionSchedule s) {
        String sql = "UPDATE distribution_schedule SET place_id=?, day_of_week=?, time_slot=?, food_type=?, quantity_target=?, unit=?, assigned_volunteer=?, volunteer_phone=?, donation_id=?, status=?, notes=?, recurring=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, s.getPlaceId());
            pstmt.setString(2, s.getDayOfWeek());
            pstmt.setString(3, s.getTimeSlot());
            pstmt.setString(4, s.getFoodType());
            pstmt.setInt(5, s.getQuantityTarget());
            pstmt.setString(6, s.getUnit());
            pstmt.setString(7, s.getAssignedVolunteer());
            pstmt.setString(8, s.getVolunteerPhone());
            if (s.getDonationId() != null) {
                pstmt.setInt(9, s.getDonationId());
            } else {
                pstmt.setNull(9, Types.INTEGER);
            }
            pstmt.setString(10, s.getStatus());
            pstmt.setString(11, s.getNotes());
            pstmt.setBoolean(12, s.isRecurring());
            pstmt.setInt(13, s.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM distribution_schedule WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DistributionSchedule> findByDay(String dayOfWeek) {
        List<DistributionSchedule> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name as place_name, p.category as place_category FROM distribution_schedule s JOIN distribution_places p ON s.place_id = p.id WHERE s.day_of_week = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, dayOfWeek);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DistributionSchedule s = extractDistributionScheduleFromResultSet(rs);
                    s.setPlaceName(rs.getString("place_name"));
                    s.setPlaceCategory(rs.getString("place_category"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DistributionSchedule> findByPlace(int placeId) {
        List<DistributionSchedule> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name as place_name, p.category as place_category FROM distribution_schedule s JOIN distribution_places p ON s.place_id = p.id WHERE s.place_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, placeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    DistributionSchedule s = extractDistributionScheduleFromResultSet(rs);
                    s.setPlaceName(rs.getString("place_name"));
                    s.setPlaceCategory(rs.getString("place_category"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DistributionSchedule> findToday() {
        List<DistributionSchedule> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name as place_name, p.category as place_category FROM distribution_schedule s JOIN distribution_places p ON s.place_id = p.id WHERE LOWER(s.day_of_week) = LOWER(DAYNAME(CURDATE()))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                DistributionSchedule s = extractDistributionScheduleFromResultSet(rs);
                s.setPlaceName(rs.getString("place_name"));
                s.setPlaceCategory(rs.getString("place_category"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DistributionSchedule> findAll() {
        List<DistributionSchedule> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name as place_name, p.category as place_category FROM distribution_schedule s JOIN distribution_places p ON s.place_id = p.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                DistributionSchedule s = extractDistributionScheduleFromResultSet(rs);
                s.setPlaceName(rs.getString("place_name"));
                s.setPlaceCategory(rs.getString("place_category"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE distribution_schedule SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void linkDonation(int scheduleId, int donationId) {
        String sql = "UPDATE distribution_schedule SET donation_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, donationId);
            pstmt.setInt(2, scheduleId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DistributionSchedule extractDistributionScheduleFromResultSet(ResultSet rs) throws SQLException {
        DistributionSchedule s = new DistributionSchedule();
        s.setId(rs.getInt("id"));
        s.setPlaceId(rs.getInt("place_id"));
        s.setDayOfWeek(rs.getString("day_of_week"));
        s.setTimeSlot(rs.getString("time_slot"));
        s.setFoodType(rs.getString("food_type"));
        s.setQuantityTarget(rs.getInt("quantity_target"));
        s.setUnit(rs.getString("unit"));
        s.setAssignedVolunteer(rs.getString("assigned_volunteer"));
        s.setVolunteerPhone(rs.getString("volunteer_phone"));
        
        int donationId = rs.getInt("donation_id");
        if (!rs.wasNull()) {
            s.setDonationId(donationId);
        } else {
            s.setDonationId(null);
        }
        
        s.setStatus(rs.getString("status"));
        s.setNotes(rs.getString("notes"));
        s.setRecurring(rs.getBoolean("recurring"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
