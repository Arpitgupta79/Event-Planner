package controllers;

import model.Event;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;

public class EventController {

    // 1. Add Event with Category and Reminder Time
    public static boolean addEvent(String title, String date, String time, String location, String category, int reminderDays) {
        String query = "INSERT INTO event (title, event_date, event_time, location, category, reminder_days) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setString(1, title);
            stmt.setString(2, date);
            stmt.setString(3, time);
            stmt.setString(4, location);
            stmt.setString(5, category);
            stmt.setInt(6, reminderDays);
    
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error adding event: " + e.getMessage());
            return false;
        }
    }

    // 2. Fetch all events
    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(extractEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching events: " + e.getMessage());
        }

        return events;
    }

    // 3. Get Upcoming Events
    public static List<Event> getUpcomingEvents(int days) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE event_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) ORDER BY event_date, event_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(extractEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching upcoming events: " + e.getMessage());
        }

        return events;
    }

    // 4. Search by title, category or location
    public static List<Event> searchEvents(String keyword) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE title LIKE ? OR category LIKE ? OR location LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            stmt.setString(3, likeKeyword);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(extractEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error searching events: " + e.getMessage());
        }

        return events;
    }

    // 5. Update Event with new fields
    public static void updateEvent(int eventId, String title, String date, String time, String location, String category, int reminderDays) {
        String query = "UPDATE event SET title = ?, event_date = ?, event_time = ?, location = ?, category = ?, reminder_days = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, title);
            stmt.setString(2, date);
            stmt.setString(3, time);
            stmt.setString(4, location);
            stmt.setString(5, category);
            stmt.setInt(6, reminderDays);
            stmt.setInt(7, eventId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Event updated successfully!");
            } else {
                System.out.println("⚠️ No event found with that ID.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }

    // 6. Delete
    public static void deleteEventById(int eventId) {
        String query = "DELETE FROM event WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, eventId);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("✅ Event deleted successfully!");
            } else {
                System.out.println("⚠️ Event not found.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting event: " + e.getMessage());
        }
    }
    
    // 7. Get Event by ID
    public static Event getEventById(int id) {
        String query = "SELECT * FROM event WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
    
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return new Event(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getDate("event_date"),
                    rs.getString("event_time"),
                    rs.getString("location"),
                    rs.getString("category"),
                    rs.getInt("reminder_days")
                );
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching event by Id: " + e.getMessage());
        }
        return null;
    }

    // 8. Get Events by Category
    public static List<Event> getEventsByCategory(String category) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT * FROM event WHERE category = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(extractEvent(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching events by category: " + e.getMessage());
        }

        return events;
    }

    // Utility Method: Extract Event from ResultSet
    private static Event extractEvent(ResultSet rs) throws SQLException {
        return new Event(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getDate("event_date"),
            rs.getString("event_time"),
            rs.getString("location"),
            rs.getString("category"),
            rs.getInt("reminder_days")
        );
    }
}