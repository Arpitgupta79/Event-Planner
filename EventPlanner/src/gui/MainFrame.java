package gui;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Smart Event Planner and Scheduler");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation Panel
        JPanel navPanel = new JPanel(new GridLayout(1, 2));
        JButton btnCalendar = new JButton("Calendar");
        JButton btnReminders = new JButton("Reminders");
        navPanel.add(btnCalendar);
        navPanel.add(btnReminders);

        // Main Content Panel with CardLayout
        JPanel mainPanel = new JPanel(new CardLayout());
        
        CalendarPanel calendarPanel = new CalendarPanel(); 
        ReminderPanel reminderPanel = new ReminderPanel(); // Placeholder for reminders

        mainPanel.add(calendarPanel, "Calendar");
        mainPanel.add(reminderPanel, "Reminders");

        // Button Actions to Switch Panels
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        btnCalendar.addActionListener(e -> cl.show(mainPanel, "Calendar"));
        btnReminders.addActionListener(e -> cl.show(mainPanel, "Reminders"));

        add(navPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}