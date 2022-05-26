import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    JLabel roundCounter;
    JButton startButton;
    JButton stopButton;
    Timer timer;
    int second = 0;
    int minute = 0;
    int rounds;
    int roundsStart;
    int minuteForCycle = 0;
    int secondForCycle = 0;
    String ddSecond, ddMinute;
    DecimalFormat dFormat = new DecimalFormat("00");
    String strFilePath = "time.txt";

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

        roundCounter = new JLabel("Rounds");
        roundCounter.setHorizontalAlignment(SwingConstants.CENTER);


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
        timePanel.add(seconds); // for testing

        JTextPane txtpnSeconds = new JTextPane();
        txtpnSeconds.setText("seconds");
        txtpnSeconds.setEditable(false);
        timePanel.add(txtpnSeconds);

        JSpinner pomodoroCycles = new JSpinner();
        pomodoroCycles.setToolTipText("Enter number of cycles");
        timePanel.add(pomodoroCycles);


        JTextPane pomodoroPointer = new JTextPane();
        pomodoroPointer.setText("<- Enter number of cycles");
        timePanel.add(pomodoroPointer);

        // Display time left and status
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(new Color(224, 255, 255));
        frmTimer.getContentPane().add(displayPanel, BorderLayout.CENTER);
        displayPanel.setLayout(new GridLayout(0, 1, 0, 0));

        displayPanel.add(labelTimer);
        displayPanel.add(roundCounter);
        displayPanel.add(statusLabel);


        // Set of control keys
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        JButton pauseButton = new JButton("Pause");
        JButton addTimeButton = new JButton("Add 15 seconds");
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
//        controlPanel.add(pauseButton);
//        controlPanel.add(addTimeButton);
//

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                second = (int) seconds.getValue();
                minute = (int) minutes.getValue();
                minuteForCycle = minute;
                secondForCycle = second;
                rounds = (int) pomodoroCycles.getValue();
                roundsStart = rounds;
                intoFile(minute, second);
                ddSecond = dFormat.format(second);
                ddMinute = dFormat.format(minute);
                labelTimer.setText(ddMinute + ":" + ddSecond);
                statusLabel.setText("Focus!");
                if (second == 0 && minute == 0) {
                    labelTimer.setText("Enter Time");
                    statusLabel.setText("Waiting");
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
                timer.stop();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                labelTimer.setText("Stopped");
                statusLabel.setText("Waiting");
            }
        });
        frmTimer.setVisible(true);
    }

    public void workTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roundCounter.setText("Rounds " + rounds + " out of " + roundsStart);
                if (rounds == 0) {
                    timer.stop();
                } else {
                    intoFile(minute, second);
                    second--;
                    intoFile(minute, second);
                    ddSecond = dFormat.format(second);
                    ddMinute = dFormat.format(minute);
                    labelTimer.setText(ddMinute + ":" + ddSecond);
                    if (second == -1) {
                        intoFile(minute - 1, 59);
                        second = 59;
                        minute--;
                        ddSecond = dFormat.format(second);
                        ddMinute = dFormat.format(minute);
                        labelTimer.setText(ddMinute + ":" + ddSecond);
                    }
                    if (minute == 0 && second == 0) {
                        if (rounds == 0) {
                            timer.stop();
                            startButton.setEnabled(true);
                            stopButton.setEnabled(false);
                            labelTimer.setText("Time over");
                            statusLabel.setText("Waiting");
                        } else minute = minuteForCycle;
                        second = secondForCycle;
                        breakTimer();
                    }
                }
            }
        });
    }

    public void intoFile(int minute, int second) {
        try {
            File file = new File(strFilePath);
            PrintWriter writer = new PrintWriter(file);
            writer.print(String.format("%02d:%02d", minute, second));
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void breakTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                intoFile(minute, second);
                second--;
                intoFile(minute, second);
                ddSecond = dFormat.format(second);
                ddMinute = dFormat.format(minute);
                labelTimer.setText(ddMinute + ":" + ddSecond);
                if (second == -1) {
                    intoFile(minute - 1, 59);
                    second = 59;
                    minute--;
                    ddSecond = dFormat.format(second);
                    ddMinute = dFormat.format(minute);
                    labelTimer.setText(ddMinute + ":" + ddSecond);
                    statusLabel.setText("Break!");
                }
                if (minute == 0 && second == 0) {
                    rounds--;
                    workTimer();
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    labelTimer.setText("Time over");
                    statusLabel.setText("Waiting");

                }
            }
        });
    }
    public void roundChecker() {

    }

}


