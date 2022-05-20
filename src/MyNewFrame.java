import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MyNewFrame {

    private JFrame frmTimer;
    private ScheduledFuture future;


    public MyNewFrame() throws FileNotFoundException {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        String strFilePath = "time.txt";


        frmTimer = new JFrame();
        frmTimer.setTitle("Timer");
        frmTimer.setBounds(100, 100, 450, 300);
        frmTimer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTimer.getContentPane().setLayout(new BorderLayout(0, 0));
        // Set of control keys
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(152, 251, 152));
        frmTimer.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        // User time input
        JPanel timePanel = new JPanel();
        frmTimer.getContentPane().add(timePanel, BorderLayout.NORTH);
        timePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JSpinner hours = new JSpinner();
        hours.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
        timePanel.add(hours);
        JSpinner minutes = new JSpinner();
        minutes.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(minutes);
        JSpinner seconds = new JSpinner();
        seconds.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        timePanel.add(seconds);
        // Display time left and status
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground(new Color(224, 255, 255));
        frmTimer.getContentPane().add(displayPanel, BorderLayout.CENTER);
        JLabel labelTimer = new JLabel("Enter Time");
        labelTimer.setLabelFor(displayPanel);
        labelTimer.setHorizontalAlignment(SwingConstants.CENTER);
        labelTimer.setBackground(new Color(255, 222, 173));
        displayPanel.add(labelTimer);

        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");
        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        JButton addTimeButton = new JButton("Add 15 seconds");
        controlPanel.add(addTimeButton);

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                future = service.scheduleWithFixedDelay(new Runnable() {
                    int totalSeconds = (int) seconds.getValue() + ((int) minutes.getValue()*60) + ((int) hours.getValue()*3600);
                    int j = totalSeconds;

                    @Override
                    public void run() {
                        int sec = totalSeconds % 60;
                        int min = (totalSeconds/60)%60;
                        int hours = (totalSeconds/60)/60;
                        labelTimer.setText(String.format("Time left: %02d : %02d : %02d ", hours, min, sec));
                        try {
                            File file = new File(strFilePath);
                            PrintWriter writer = new PrintWriter(file);
                            writer.print(String.format("%02d:%02d:%02d", hours, min, sec));
                            writer.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        totalSeconds--;
                        if (totalSeconds < 0) {
                            File file = new File(strFilePath);
                            PrintWriter writer = null;
                            try {
                                writer = new PrintWriter(file);
                            } catch (FileNotFoundException ex) {
                                ex.printStackTrace();
                            }
                            writer.print(String.format("%02d:%02d:%02d", hours, min, sec));
                            writer.close();
                            future.cancel(true);
                            labelTimer.setText("Time Over");
                            totalSeconds=j;
                            startButton.setEnabled(true);
                        }
                    }
                },0,1, TimeUnit.SECONDS);
            }
        });

        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                future.cancel(true);
                labelTimer.setText("Stopped");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });






        frmTimer.setVisible(true);


    }
}

