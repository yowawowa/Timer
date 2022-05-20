import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.concurrent.*;

public class MyFrame extends JFrame {

    private JFrame frmTimer;
    private ScheduledFuture future;

    MyFrame() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        frmTimer = new JFrame();
        frmTimer.setTitle("Timer");
        frmTimer.setBounds(100, 100, 450, 300);
        frmTimer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmTimer.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(152, 251, 152));
        frmTimer.getContentPane().add(controlPanel, BorderLayout.SOUTH);

        JLabel labelTimer = new JLabel("Enter Time");
        labelTimer.setHorizontalAlignment(SwingConstants.CENTER);
        labelTimer.setBackground(new Color(224, 255, 255));
        frmTimer.add(labelTimer);


        // Panel to enter time
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

        frmTimer.add(timePanel);

        JButton startButton = new JButton("Start");
        startButton.setEnabled(true);
        JButton stopButton = new JButton("Stop");
        stopButton.setEnabled(true);

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                future = service.scheduleAtFixedRate(new Runnable() {
                    int i = (int) seconds.getValue();
                    int j = i;
                    @Override
                    public void run() {
                        labelTimer.setText("Time left " + i);
                        i--;
                        if (i < 0) {
                            future.cancel(true);
                            labelTimer.setText("Time Over");
                            i=j;
                            startButton.setEnabled(true);
                        }
                    }
                },0,1, TimeUnit.SECONDS);
            }
        });
        controlPanel.add(startButton);

        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                future.cancel(true);
                labelTimer.setText("stopped");
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });
        controlPanel.add(stopButton);



        frmTimer.setVisible(true);
    }
}
