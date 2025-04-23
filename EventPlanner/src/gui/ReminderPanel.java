package gui;

import controllers.EventController;
import model.Event;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderPanel extends JPanel {
    private DefaultListModel<String> thisWeekModel;
    private DefaultListModel<String> upcomingModel;
    private JList<String> thisWeekList;
    private JList<String> upcomingList;

    private java.util.Timer reminderTimer;

    public ReminderPanel() {
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("🔔 Event Reminders", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Grid for two columns
        JPanel gridPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Category 1: This Week
        thisWeekModel = new DefaultListModel<>();
        thisWeekList = new JList<>(thisWeekModel);
        JPanel thisWeekPanel = createCategoryPanel("🗓 Events This Week", thisWeekList);
        gridPanel.add(thisWeekPanel);

        // Category 2: Upcoming Events
        upcomingModel = new DefaultListModel<>();
        upcomingList = new JList<>(upcomingModel);
        JPanel upcomingPanel = createCategoryPanel("📅 Upcoming Events", upcomingList);
        gridPanel.add(upcomingPanel);

        add(gridPanel, BorderLayout.CENTER);

        // Refresh Button
        JButton btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.addActionListener(e -> fetchGroupedReminders());
        add(btnRefresh, BorderLayout.SOUTH);

        fetchGroupedReminders();
        startReminderCheck();
        startEventCleanup();
    }

    private JPanel createCategoryPanel(String title, JList<String> list) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(title, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        return panel;
    }

    private void fetchGroupedReminders() {
        List<Event> allEvents = EventController.getUpcomingEvents(30); // 30-day horizon
        thisWeekModel.clear();
        upcomingModel.clear();


        Date today = new Date();
        long millisIn7Days = 7 * 24 * 60 * 60 * 1000L;
        Date weekLater = new Date(today.getTime() + millisIn7Days);

        List<Event> thisWeekEvents = allEvents.stream()
                .filter(e -> e.getDate().before(weekLater))
                .collect(Collectors.toList());

        List<Event> upcomingEvents = allEvents.stream()
                .filter(e -> e.getDate().after(weekLater))
                .collect(Collectors.toList());

        if (thisWeekEvents.isEmpty()) {
            thisWeekModel.addElement("⚠️ No events this week.");
        } else {
            for (Event event : thisWeekEvents) {
                String display = "📌 " + event.getTitle() + " ⏰ " + event.getTime() + " - " + event.getLocation();
                thisWeekModel.addElement(display);
            }
        }

        if (upcomingEvents.isEmpty()) {
            upcomingModel.addElement("⚠️ No upcoming events.");
        } else {
            for (Event event : upcomingEvents) {
                String display = "🔜 " + event.getTitle() + " ⏰ " + event.getTime() + " - " + event.getLocation();
                upcomingModel.addElement(display);
            }
        }

        revalidate();
        repaint();
    }

    private void startReminderCheck() {
        reminderTimer = new java.util.Timer(true);
        reminderTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    fetchGroupedReminders();
                    checkReminders();
                });
            }
        }, 0, 60000); // every 1 min
    }

    private void checkReminders() {
        List<Event> todayEvents = EventController.getUpcomingEvents(1);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());

        for (Event event : todayEvents) {
            String eventTime = event.getTime().substring(0, 5);
            if (eventTime.equals(currentTime)) {
                showReminderPopup(event);
            }
        }
    }

    private void showReminderPopup(Event event) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                this,
                "Event: " + event.getTitle() + "\nTime: " + event.getTime() + "\nLocation: " + event.getLocation(),
                "⏰ Event Reminder!",
                JOptionPane.INFORMATION_MESSAGE
        ));
    }

    private void startEventCleanup() {
        java.util.Timer cleanupTimer = new java.util.Timer(true);
        cleanupTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                deleteExpiredEvents();
            }
        }, 0, 60000);
    }

    private void deleteExpiredEvents() {
        List<Event> events = EventController.getAllEvents();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateTime = sdf.format(new Date());

        for (Event event : events) {
            String eventDateTime = sdf.format(event.getDate()) + " " + event.getTime();
            if (eventDateTime.compareTo(currentDateTime) < 0) {
                EventController.deleteEventById(event.getEventId());
                System.out.println("🗑️ Deleted expired event: " + event.getTitle());
            }
        }

        SwingUtilities.invokeLater(this::fetchGroupedReminders);
    }

    public void refreshReminders() {
        fetchGroupedReminders();
    }
}