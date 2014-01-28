package org.sunspotworld.heatsensors.views;

import java.awt.BorderLayout;
import javax.swing.*;
import org.sunspotworld.heatsensors.HeatSensorsHostNetwork;
import org.sunspotworld.heatsensors.utils.ApplicationSetup;

public class TemperatureMonitor extends JFrame {

    private static final long serialVersionUID = 1L;
    private HeatSensorsHostNetwork app;
    private ApplicationSetup setup;
    private JPanel jContentPane = null;
    private JPanel northContentPane = null;
    private JPanel southContentPane = null;
    private JPanel contentLog = null;
    private DataPanel contentGraph = null;
    private JMenuBar menuBarMonitor = null;
    private JMenu menuStart = null;
    private JMenu menuLog = null;
    private JMenuItem itemStart = null;
    private JMenuItem itemSaveLog = null;
    private JMenuItem itemExit = null;
    private JTabbedPane tabbedMonitor = null;
    private JToolBar toolBarInfo = null;
    private JLabel labelBarInfo = null;
    private JMenuItem itemStop = null;
    private JTextArea textLog = null;
    private JScrollPane scrollPane = null;
    private JMenu menuSetup = null;
    private JMenuItem itemParameters = null;

   /**
    * This is the default constructor
    */
    public TemperatureMonitor(HeatSensorsHostNetwork app, DataPanel graph) {
        super();
        this.app = app;
        this.contentGraph = graph;
        app.setMonitor(this);
        initialize();
    }
    
    /**
    * Alternate constructor with setup data
    */
    public TemperatureMonitor(HeatSensorsHostNetwork app, DataPanel graph, ApplicationSetup setup) {
        super();
        this.app = app;
        this.contentGraph = graph;
        this.setup = setup;
        app.setMonitor(this);
        initialize();
    }

   /**
    * This method initializes this
    * 
    * @return void
    */
    private void initialize() {
        this.setSize(657, 317);
        this.setJMenuBar(getMenuBarMonitor());
        this.setContentPane(getJContentPane());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Temperature Monitor");
    }

   /**
    * This method initializes jContentPane
    * 
    * @return javax.swing.JPanel
    */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getNorthContentPane(), BorderLayout.NORTH);
            jContentPane.add(getSouthContentPane(), BorderLayout.SOUTH);
            jContentPane.add(getTabbedMonitor(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

   /* This method initializes northContentPane
    * 
    * @return javax.swing.JPanel
    */
    private JPanel getNorthContentPane() {
        if (northContentPane == null) {
            northContentPane = new JPanel();
            northContentPane.setLayout(new BorderLayout());
            northContentPane.add(getTabbedMonitor(), BorderLayout.CENTER);
        }
        return northContentPane;
    }

   /* This method initializes southContentPane
    * 
    * @return javax.swing.JPanel
    */
    private JPanel getSouthContentPane() {
        if (southContentPane == null) {
            southContentPane = new JPanel();
            southContentPane.setLayout(new BorderLayout());
            southContentPane.add(getToolBarInfo(), BorderLayout.CENTER);
        }
        return southContentPane;
    }

   /**
    * This method initializes contentLog
    * 
    * @return javax.swing.JPanel
    */
    private JPanel getContentLog() {
        if (contentLog == null) {
            contentLog = new JPanel();
            contentLog.setLayout(new BorderLayout());
            contentLog.add(getScrollPane(), BorderLayout.CENTER);
        }
        return contentLog;
    }

   /**
    * This method initializes contentGraph
    * 
    * @return javax.swing.JPanel
    */
    private JPanel getContentGraph() {
        return contentGraph;
    }

   /**
    * This method initializes menuBarMonitor	
    * 	
    * @return javax.swing.JMenuBar	
    */
    private JMenuBar getMenuBarMonitor() {
        if (menuBarMonitor == null) {
            menuBarMonitor = new JMenuBar();
            menuBarMonitor.add(getMenuStart());
            menuBarMonitor.add(getMenuLog());
            menuBarMonitor.add(getMenuSetup());
        }
        return menuBarMonitor;
    }

   /**
    * This method initializes menuStart	
    * 	
    * @return javax.swing.JMenu	
    */
    private JMenu getMenuStart() {
        if (menuStart == null) {
            menuStart = new JMenu();
            menuStart.setText("Start");
            menuStart.add(getItemStart());
            menuStart.add(getItemStop());
            menuStart.add(getItemExit());
        }
        return menuStart;
    }

   /**
    * This method initializes menuLog	
    * 	
    * @return javax.swing.JMenu	
    */
    private JMenu getMenuLog() {
        if (menuLog == null) {
            menuLog = new JMenu();
            menuLog.setText("Log");
            menuLog.add(getItemSaveLog());
        }
        return menuLog;
    }

   /**
    * This method initializes itemStart	
    * 	
    * @return javax.swing.JMenuItem	
    */
    private JMenuItem getItemStart() {
        if (itemStart == null) {
            itemStart = new JMenuItem();
            itemStart.setText("Start Monitoring");
            itemStart.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    SwingWorker worker = new SwingWorker(){
                        @Override
                        protected Object doInBackground() throws Exception {
                                app.startMonitoring();
                                return null;
                        }
                        @Override
                        protected void done(){}
                    };
                    worker.execute();
                }
            });
        }
        return itemStart;
    }

   /**
    * This method initializes itemSaveLog	
    * 	
    * @return javax.swing.JMenuItem	
    */
    private JMenuItem getItemSaveLog() {
        if (itemSaveLog == null) {
            itemSaveLog = new JMenuItem();
            itemSaveLog.setText("Save Log");
        }
        return itemSaveLog;
    }

   /**
    * This method initializes itemExit	
    * 	
    * @return javax.swing.JMenuItem	
    */
    private JMenuItem getItemExit() {
        if (itemExit == null) {
            itemExit = new JMenuItem();
            itemExit.setText("Exit");
        }
        itemExit.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.exit(0);
            }
        });
        return itemExit;
    }

   /**
    * This method initializes tabbedMonitor	
    * 	
    * @return javax.swing.JTabbedPane	
    */
    private JTabbedPane getTabbedMonitor() {
        if (tabbedMonitor == null) {
            tabbedMonitor = new JTabbedPane();
            tabbedMonitor.addTab("Log", getContentLog());
            tabbedMonitor.addTab("Graph", getContentGraph());
        }
        return tabbedMonitor;
    }

   /**
    * This method initializes toolBarInfo	
    * 	
    * @return javax.swing.JToolBar	
    */
    private JToolBar getToolBarInfo() {
        if (toolBarInfo == null) {
            labelBarInfo = new JLabel();
            labelBarInfo.setText("Info here");
            toolBarInfo = new JToolBar();
            toolBarInfo.add(labelBarInfo);
        }
        return toolBarInfo;
    }

   /**
    * This method initializes itemStop	
    * 	
    * @return javax.swing.JMenuItem	
    */
    private JMenuItem getItemStop() {
        if (itemStop == null) {
            itemStop = new JMenuItem();
            itemStop.setText("Stop Monitoring");
        }
        return itemStop;
    }

   /**
    * This method initializes textArea	
    * 	
    * @return javax.swing.JTextArea	
    */
    private JTextArea getTextLog() {
        if (textLog == null) {
            textLog = new JTextArea();
        }
        return textLog;
    }

   /**
    * This method initializes jScrollPane	
    * 	
    * @return javax.swing.JScrollPane	
    */
    private JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView(getTextLog());
        }
        return scrollPane;
    }

   /**
    * Logs info in log tab	
    */
    public void logInfo(String info){
        System.out.println(info);
        if(this.textLog.getText().equals(""))
            info = this.textLog.getText() + info;
        else
            info = this.textLog.getText() + "\n" + info;
        this.textLog.setText(info);
    }

    public DataPanel getDataPanel(){
        return (DataPanel)this.contentGraph;
    }

    /**
    * This method initializes menuSetup	
    * 	
    * @return javax.swing.JMenu	
    */
    private JMenu getMenuSetup() {
        if (menuSetup == null) {
            menuSetup = new JMenu();
            menuSetup.setText("Setup");
            menuSetup.add(getItemParameters());
        }
        return menuSetup;
    }

   /**
    * This method initializes itemParameters	
    * 	
    * @return javax.swing.JMenuItem	
    */
    private JMenuItem getItemParameters() {
        if (itemParameters == null) {
            itemParameters = new JMenuItem();
            itemParameters.setText("Parameters");
            itemParameters.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Parameters param;
                    if(setup != null){
                        param = new Parameters(setup);
                    } else {
                        param = new Parameters();
                    }
                    param.setVisible(true);
                }
            });
        }
        return itemParameters;
    }

}
