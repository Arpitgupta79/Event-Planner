package gui;

import controllers.EventController;
import model.Event;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JCalendar;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.sql.Date;

public class CalendarPanel extends JPanel {
    private JCalendar calendar;
    private JTable eventTable;
    private DefaultTableModel tableModel;
    private JButton btnAddEvent, btnEditEvent, btnDeleteEvent;
    private JLabel clockLabel;
    private JTextField searchField;

    // 🔽 Predefined Categories
    private static final String[] EVENT_CATEGORIES = {
        "Work", "Personal", "Birthday", "Holiday", "Meeting", "Reminder", "Other"
    };

    public CalendarPanel() {
        setLayout(new BorderLayout());

        // 🔼 Top Panel (Search + Calendar + Clock)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // 🔍 Search Panel (inside topPanel)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton btnSearch = new JButton("🔍 Search");
        btnSearch.addActionListener(e -> searchEvents());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(btnSearch);
        topPanel.add(searchPanel, BorderLayout.NORTH);

        // 📅 Calendar
        calendar = new JCalendar();
        calendar.addPropertyChangeListener("calendar", evt -> loadEvents());
        topPanel.add(calendar, BorderLayout.CENTER);

        // 🕒 Clock
        JPanel clockPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        clockPanel.add(clockLabel);
        topPanel.add(clockPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // 🧾 Event Table (center)
        String[] columns = {"Id", "Title", "Date", "Time", "Location", "Category", "Reminder (Days Before)"};
        tableModel = new DefaultTableModel(columns, 0);
        eventTable = new JTable(tableModel);
        eventTable.removeColumn(eventTable.getColumnModel().getColumn(0));
        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        // ➕ Button Panel (south)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAddEvent = new JButton("Add Event");
        btnEditEvent = new JButton("Edit Event");
        btnDeleteEvent = new JButton("Delete Event");

        btnAddEvent.addActionListener(e -> addEvent());
        btnEditEvent.addActionListener(e -> editEvent());
        btnDeleteEvent.addActionListener(e -> deleteEvent());

        buttonPanel.add(btnAddEvent);
        buttonPanel.add(btnEditEvent);
        buttonPanel.add(btnDeleteEvent);
        add(buttonPanel, BorderLayout.SOUTH);

        loadEvents();
        startClock();
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            String currentTime = new SimpleDateFormat("hh:mm:ss a").format(new java.util.Date());
            clockLabel.setText("🕒 " + currentTime);
        });
        timer.start();
    }

    private void loadEvents() {
        tableModel.setRowCount(0);
        List<Event> events = EventController.getAllEvents();
        for (Event event : events) {
            tableModel.addRow(new Object[]{
                event.getEventId(),
                event.getTitle(),
                event.getDate(),
                event.getTime(),
                event.getLocation(),
                event.getCategory(),
                event.getReminderDays()
            });
        }
    }

    private void addEvent() {
        String title = JOptionPane.showInputDialog(this, "Enter Event Title:");
        if (title == null) return;

        String date = JOptionPane.showInputDialog(this, "Enter Event Date (YYYY-MM-DD):");
        if (date == null) return;

        String time = JOptionPane.showInputDialog(this, "Enter Event Time (HH:MM:SS):");
        if (time == null) return;

        String location = JOptionPane.showInputDialog(this, "Enter Event Location:");
        if (location == null) return;

        // 🔽 Category dropdown
        JComboBox<String> categoryBox = new JComboBox<>(EVENT_CATEGORIES);
        int catResult = JOptionPane.showConfirmDialog(this, categoryBox, "Select Event Category", JOptionPane.OK_CANCEL_OPTION);
        if (catResult != JOptionPane.OK_OPTION) return;
        String category = (String) categoryBox.getSelectedItem();

        String reminder = JOptionPane.showInputDialog(this, "Remind how many days before the event?");
        int reminderDays = (reminder == null || reminder.isEmpty()) ? 0 : Integer.parseInt(reminder);

        boolean success = EventController.addEvent(title, date, time, location, category, reminderDays);
        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Event added successfully!");
            loadEvents();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Failed to add event.");
        }
    }

    private void editEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select an event to edit.");
            return;
        }

        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        Date date = (Date) tableModel.getValueAt(selectedRow, 2);
        String time = (String) tableModel.getValueAt(selectedRow, 3);
        String location = (String) tableModel.getValueAt(selectedRow, 4);
        String category = (String) tableModel.getValueAt(selectedRow, 5);
        int reminderDays = (int) tableModel.getValueAt(selectedRow, 6);

        String newTitle = JOptionPane.showInputDialog(this, "Edit Title:", title);
        if (newTitle == null) return;

        String newDate = JOptionPane.showInputDialog(this, "Edit Date (YYYY-MM-DD):", date.toString());
        if (newDate == null) return;

        String newTime = JOptionPane.showInputDialog(this, "Edit Time:", time);
        if (newTime == null) return;

        String newLocation = JOptionPane.showInputDialog(this, "Edit Location:", location);
        if (newLocation == null) return;

        // 🔽 Category dropdown with pre-selected value
        JComboBox<String> categoryBox = new JComboBox<>(EVENT_CATEGORIES);
        categoryBox.setSelectedItem(category);
        int catResult = JOptionPane.showConfirmDialog(this, categoryBox, "Edit Category", JOptionPane.OK_CANCEL_OPTION);
        if (catResult != JOptionPane.OK_OPTION) return;
        String newCategory = (String) categoryBox.getSelectedItem();

        String newReminder = JOptionPane.showInputDialog(this, "Edit Reminder Days:", reminderDays);
        if (newReminder == null) return;

        EventController.updateEvent(eventId, newTitle, newDate, newTime, newLocation, newCategory, Integer.parseInt(newReminder));
        JOptionPane.showMessageDialog(this, "✅ Event updated successfully!");
        loadEvents();
    }

    private void deleteEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ Please select an event to delete.");
            return;
        }

        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete event: \"" + title + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            EventController.deleteEventById(eventId);
            JOptionPane.showMessageDialog(this, "✅ Event deleted successfully!");
            loadEvents();
        }
    }

    private void searchEvents() {
        String keyword = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        List<Event> events = EventController.getAllEvents();

        for (Event event : events) {
            if (event.getTitle().toLowerCase().contains(keyword) || event.getCategory().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[]{
                    event.getEventId(),
                    event.getTitle(),
                    event.getDate(),
                    event.getTime(),
                    event.getLocation(),
                    event.getCategory(),
                    event.getReminderDays()
                });
            }
        }
    }
}