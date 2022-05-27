import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;


public class Pomodoro {

    JFrame frmTimer;
    JLabel labelTimer;
    JLabel statusLabel;
    JLabel roundCounter;
    JButton startButton;
    JButton stopButton;
    JSpinner workSeconds;
    JSpinner workMinutes;
    JSpinner breakSeconds;
    JSpinner breakMinutes;
    Timer timer;
    int second = 0;
    int minute = 0;
    int rounds;
    int roundsPrev;
    int roundsStart;
    int roundsCount = 1;
    int minuteForCycle = 0;
    int secondForCycle = 0;
    int breakTime = 0;
    String ddSecond, ddMinute;

    String cycleStatus;
    String roundStatus;

    String soundName;
    AudioInputStream audioInputStream;
    Clip clip;

    DecimalFormat dFormat = new DecimalFormat("00");
    String strFilePath = "time.txt";
    private JCheckBox chckbxNewCheckBox;
    private Button soundSelect;

    public Pomodoro() {
        frmTimer = new JFrame();
        frmTimer.setFont(new Font("Century", Font.PLAIN, 18));
        frmTimer.setTitle("Timer");
        frmTimer.setBounds(100, 100, 600, 300);
        frmTimer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTimer.getContentPane().setLayout(new BorderLayout(0, 0));

        labelTimer = new JLabel("Enter Time");
        labelTimer.setFont(new Font("Neucha", Font.PLAIN, 20));
        labelTimer.setHorizontalAlignment(JLabel.CENTER);
        labelTimer.setBackground(new Color(255, 222, 173));

        statusLabel = new JLabel("Waiting");
        statusLabel.setFont(new Font("Neucha", Font.PLAIN, 20));
        statusLabel.setHorizontalAlignment(JLabel.CENTER);

        roundCounter = new JLabel("Rounds");
        roundCounter.setFont(new Font("Neucha", Font.PLAIN, 20));
        roundCounter.setHorizontalAlignment(SwingConstants.CENTER);
        roundCounter.setHorizontalAlignment(SwingConstants.CENTER);
        roundCounter.setVerticalAlignment(SwingConstants.CENTER);


        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(152, 251, 152));
        frmTimer.getContentPane().add(controlPanel, BorderLayout.SOUTH);

        // User time input
        JPanel timePanel = new JPanel();
        timePanel.setBackground(Color.PINK);
        timePanel.setForeground(Color.BLACK);
        frmTimer.getContentPane().add(timePanel, BorderLayout.NORTH);

        workSeconds = new JSpinner();
        workSeconds.setModel(new SpinnerNumberModel(5, 0, 59, 1));
//        timePanel.add(workSeconds); // for testing

        JTextPane txtpnSeconds = new JTextPane();
        txtpnSeconds.setText("seconds");
        txtpnSeconds.setEditable(false);

        breakSeconds = new JSpinner();
        breakSeconds.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        workMinutes = new JSpinner();
        workMinutes.setFont(new Font("Neucha", Font.PLAIN, 18));
        workMinutes.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(workMinutes);

        JTextPane txtpnMinutes = new JTextPane();
        txtpnMinutes.setFont(new Font("Neucha", Font.PLAIN, 18));
        txtpnMinutes.setText("minutes");
        txtpnMinutes.setEditable(false);
        timePanel.add(txtpnMinutes);
        //        timePanel.add(txtpnSeconds); // for testing

        JSpinner pomodoroCycles = new JSpinner();
        pomodoroCycles.setFont(new Font("Neucha", Font.PLAIN, 18));
        pomodoroCycles.setToolTipText("Enter number of cycles");
        pomodoroCycles.setModel(new SpinnerNumberModel(5, 0, null, 1));
        timePanel.add(pomodoroCycles);

        JTextPane pomodoroPointer = new JTextPane();
        pomodoroPointer.setFont(new Font("Neucha", Font.PLAIN, 18));
        pomodoroPointer.setText("<- Enter number of cycles");
        timePanel.add(pomodoroPointer);

        breakMinutes = new JSpinner();
        breakMinutes.setFont(new Font("Neucha", Font.PLAIN, 18));
        breakMinutes.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(breakMinutes);
        //        timePanel.add(breakSeconds);

        JTextPane breakPointer = new JTextPane();
        breakPointer.setFont(new Font("Neucha", Font.PLAIN, 18));
        breakPointer.setText("<- Enter break time");
        timePanel.add(breakPointer);


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
        startButton.setFont(new Font("Impact", Font.PLAIN, 18));
        stopButton = new JButton("Stop");
        stopButton.setFont(new Font("Impact", Font.PLAIN, 18));
        stopButton.setEnabled(false);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        chckbxNewCheckBox = new JCheckBox("Sound On End");
        controlPanel.add(chckbxNewCheckBox);

        soundSelect = new Button("...");
        soundSelect.setActionCommand("command");
        controlPanel.add(soundSelect);

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                second = (int) workSeconds.getValue();
                minute = (int) workMinutes.getValue();
                breakTime = (int) breakMinutes.getValue();
                secondForCycle = second;
                minuteForCycle = minute;
                if ((int) pomodoroCycles.getValue() == 0) {
                    rounds = 1;
                } else {
                    rounds = (int) pomodoroCycles.getValue();
                    roundsPrev = rounds;
                }
                roundsStart = rounds;
                intoFile(minute, second);
                ddSecond = dFormat.format(second);
                ddMinute = dFormat.format(minute);
                labelTimer.setText(ddMinute + ":" + ddSecond);
                statusLabel.setText("Focus!");
                cycleStatus = "Study";
                roundStatus = roundsCount + "/" + roundsStart;
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
                fullStop();
            }
        });
        frmTimer.setVisible(true);
    }

    public void workTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roundCounter.setText("<html>Rounds <br>" + roundsCount + " out of " + roundsStart + "</html>");
                if (rounds == 0) {
                    fullStop();
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
                            fullStop();
                        } else if (rounds == roundsPrev) {
                            minute = breakTime;
                            second = 1;
                            statusLabel.setText("BREAK!");
                            cycleStatus = "Break";
                            rounds--;
                        } else {
                            minute = minuteForCycle;
                            second = secondForCycle + 1;
                            statusLabel.setText("Focus");
                            cycleStatus = "Study";
                            roundsPrev--;
                            roundsCount++;
                            roundStatus = roundsCount + "/" + roundsStart;

                        }
                    }
                }
            }
        });
    }

    public void intoFile(int minute, int second) {
        try {
            File file = new File(strFilePath);
            PrintWriter writer = new PrintWriter(file);
            writer.print(String.format(roundStatus + "\n%02d:%02d " + cycleStatus, minute, second));
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void fullStop() {
        timer.stop();
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        labelTimer.setText("Time over");
        statusLabel.setText("Waiting");
        roundCounter.setText("Rounds");
        try {
            File file = new File(strFilePath);
            PrintWriter writer = new PrintWriter(file);
            writer.print("no timer");
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}






