import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Pomodoro {

    JFrame frmTimer;
    JLabel labelTimer;
    JLabel statusLabel;
    Timer timer;
    int second = 0;
    int minute = 0;
    int rounds;
    String ddSecond, ddMinute;
    DecimalFormat dFormat = new DecimalFormat("00");


    public Pomodoro() {

        frmTimer = new JFrame();
        frmTimer.setTitle("Timer");
        frmTimer.setBounds(100, 100, 450, 300);
        frmTimer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTimer.getContentPane().setLayout(new BorderLayout(0, 0));

        labelTimer = new JLabel("Enter Time");
        labelTimer.setHorizontalAlignment(JLabel.CENTER);
        labelTimer.setBackground(new Color(255, 222, 173));

        statusLabel = new JLabel("Waiting");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);


        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(152, 251, 152));
        frmTimer.getContentPane().add(controlPanel, BorderLayout.SOUTH);

        // User time input
        JPanel timePanel = new JPanel();
        frmTimer.getContentPane().add(timePanel, BorderLayout.NORTH);
        timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JSpinner minutes = new JSpinner();
        minutes.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(minutes);

        JTextPane txtpnMinutes = new JTextPane();
        txtpnMinutes.setText("minutes");
        txtpnMinutes.setEditable(false);
        timePanel.add(txtpnMinutes);

        JSpinner seconds = new JSpinner();
        seconds.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(seconds);

        JTextPane txtpnSeconds = new JTextPane();
        txtpnSeconds.setText("seconds");
        txtpnSeconds.setEditable(false);
        timePanel.add(txtpnSeconds);


        // Display time left and status
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(new Color(224, 255, 255));
        frmTimer.getContentPane().add(displayPanel, BorderLayout.CENTER);
        displayPanel.setLayout(new GridLayout(0, 1, 0, 0));
        displayPanel.add(labelTimer);
        displayPanel.add(statusLabel);




        // Set of control keys
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        JButton pauseButton = new JButton("Pause");
        JButton addTimeButton = new JButton("Add 15 seconds");
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
//        controlPanel.add(pauseButton);
//        controlPanel.add(addTimeButton);

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                second = (int) seconds.getValue();
                minute = (int) minutes.getValue();
                ddSecond = dFormat.format(second);
                ddMinute = dFormat.format(minute);
                labelTimer.setText(ddMinute + ":" + ddSecond);
                statusLabel.setText("Focus!");
                if (second ==0 && minute == 0) {

                } else {
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    workTimer();
                    timer.start();
                }
            }
        });
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                timer.stop();
                labelTimer.setText("Stopped");
                statusLabel.setText("Take a break!");

            }
        });
        frmTimer.setVisible(true);
    }
    public void workTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                second--;
                ddSecond = dFormat.format(second);
                ddMinute = dFormat.format(minute);
                labelTimer.setText(ddMinute + ":" + ddSecond);

                if (second == -1) {
                    second = 59;
                    minute--;
                    ddSecond = dFormat.format(second);
                    ddMinute = dFormat.format(minute);
                    labelTimer.setText(ddMinute + ":" + ddSecond);
                }
                if (minute == 0 && second == 0) {
                    timer.stop();
                    labelTimer.setText("Time over");
                    statusLabel.setText("Waiting");
                }
            }
        });
    }
    public void breakTimer() {
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                second--;
                ddSecond = dFormat.format(second);
                ddMinute = dFormat.format(minute);
                labelTimer.setText(ddMinute + ":" + ddSecond);

                if (second == -1) {
                    second = 59;
                    minute--;
                    ddSecond = dFormat.format(second);
                    ddMinute = dFormat.format(minute);
                    labelTimer.setText(ddMinute + ":" + ddSecond);
                }
                if (minute == 0 && second == 0) {
                    timer.stop();
                    labelTimer.setText("Break over");
                }
            }
        });
    }
}


