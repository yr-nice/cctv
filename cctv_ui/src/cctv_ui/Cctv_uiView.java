/*
 * Cctv_uiView.java
 */
package cctv_ui;

import com.mu.cctv.Main;
import com.mu.cctv.MediaUtil;
import com.mu.cctv.TaskGenerator;
import com.mu.cctv.cfg.CfgMgr;
import com.mu.cctv.db.dao.DownloadTaskDAO;
import com.mu.cctv.db.dao.EpisodeDAO;
import com.mu.cctv.db.dao.ProgramDAO;
import com.mu.cctv.global.Global;
import com.mu.cctv.program.Comparator.CompareEpiId;
import com.mu.cctv.program.Comparator.CompareEpiIndex;
import com.mu.cctv.program.Comparator.CompareEpiStatus;
import com.mu.cctv.program.Comparator.CompareEpiTitle;
import com.mu.cctv.program.Episode;
import com.mu.cctv.program.EpisodeStatus;
import com.mu.cctv.program.ProgramInfo;
import com.mu.cctv.program.ProgramStatus;
import com.mu.cctv.program.ProgramUtil;
import com.mu.cctv.program.impl.BaseEpisode;
import com.mu.cctv.program.impl.BaseProgramInfo;
import com.mu.cctv.program.impl.SpaceEpisode;
import com.mu.cctv.program.impl.VodEpisode;
import com.mu.cctv.web.CCTVDownloadTask;
import com.mu.util.UniqueTimeGenerator;
import com.mu.util.net.downloader.TaskStatus;
import hello.mu.util.MuLog;
import java.awt.Component;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 * The application's main frame.
 */
public class Cctv_uiView extends FrameView
{

    public Cctv_uiView(SingleFrameApplication app)
    {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++)
        {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {

            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName))
                {
                    if (!busyIconTimer.isRunning())
                    {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                }
                else if ("done".equals(propertyName))
                {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                }
                else if ("message".equals(propertyName))
                {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                }
                else if ("progress".equals(propertyName))
                {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        Main.init();
        initUI();
    }

    @Action
    public void showAboutBox()
    {
        if (aboutBox == null)
        {
            JFrame mainFrame = Cctv_uiApp.getApplication().getMainFrame();
            aboutBox = new Cctv_uiAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        Cctv_uiApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        tabProgram = new javax.swing.JTabbedPane();
        spProgram = new javax.swing.JScrollPane();
        tblProgram = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProgramPrepare = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProgramDownloading = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProgramDownloaded = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProgramRemoved = new javax.swing.JTable();
        tabEpi = new javax.swing.JTabbedPane();
        spEpi = new javax.swing.JScrollPane();
        tblEpi = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblEpiPrepare = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblEpiDownloading = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblEpiDownloaded = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblEpiRemoved = new javax.swing.JTable();
        tabTask = new javax.swing.JTabbedPane();
        spTask = new javax.swing.JScrollPane();
        tblTask = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        tblTaskDownloading = new javax.swing.JTable();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblTaskDownloaded = new javax.swing.JTable();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblTaskRemoved = new javax.swing.JTable();
        jScrollPane9 = new javax.swing.JScrollPane();
        taStatus = new javax.swing.JTextArea();
        btnStartDownloading = new javax.swing.JButton();
        btnStopDownloading = new javax.swing.JButton();
        btnEpiUp = new javax.swing.JButton();
        btnEpiDown = new javax.swing.JButton();
        btnEpiSave = new javax.swing.JButton();
        btnTaskUp = new javax.swing.JButton();
        btnTaskDown = new javax.swing.JButton();
        btnTaskSave = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        ppmProg = new javax.swing.JPopupMenu();
        pmiAddProg = new javax.swing.JMenuItem();
        pmiRetrieveProgInfo = new javax.swing.JMenuItem();
        pmiProgramToTask = new javax.swing.JMenuItem();
        ppmProgStatus = new javax.swing.JMenu();
        ppmProgRemoveFromDB = new javax.swing.JMenuItem();
        pmiCombin = new javax.swing.JMenuItem();
        pmiOpenFolder = new javax.swing.JMenuItem();
        ppmEpi = new javax.swing.JPopupMenu();
        pmiAddNewEpi = new javax.swing.JMenuItem();
        pmiRetrieveInfo = new javax.swing.JMenuItem();
        pmiEpiAddTask = new javax.swing.JMenuItem();
        ppmEpiStatus = new javax.swing.JMenu();
        pmmEpiSortBy = new javax.swing.JMenu();
        pmiEpiSortByTitle = new javax.swing.JMenuItem();
        pmiEpiSortByStatus = new javax.swing.JMenuItem();
        pmiEpiSortById = new javax.swing.JMenuItem();
        pmiEpiSortByIndex = new javax.swing.JMenuItem();
        pmiEpiRemoveFromDB = new javax.swing.JMenuItem();
        pmiMoveTop = new javax.swing.JMenuItem();
        pmiMoveBottom = new javax.swing.JMenuItem();
        pmiCombineEpi = new javax.swing.JMenuItem();
        ppmTask = new javax.swing.JPopupMenu();
        pmiStartAll = new javax.swing.JMenuItem();
        pmiStopAll = new javax.swing.JMenuItem();
        pmiStartSelected = new javax.swing.JMenuItem();
        pmiStopSelected = new javax.swing.JMenuItem();
        ppmTaskStatus = new javax.swing.JMenu();
        pmiAddDownloader = new javax.swing.JMenuItem();
        pmiReduceDownloader = new javax.swing.JMenuItem();
        pmiTaskRemoveFromDB = new javax.swing.JMenuItem();
        pmiMoveTaskToTop = new javax.swing.JMenuItem();
        pmiMoveTaskToBottom = new javax.swing.JMenuItem();
        pmiRedownload = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        tabProgram.setName("tabProgram"); // NOI18N
        tabProgram.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabProgramStateChanged(evt);
            }
        });

        spProgram.setName("spProgram"); // NOI18N

        tblProgram.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Status", "Type", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProgram.setName("tblProgram"); // NOI18N
        tblProgram.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblProgramMouseReleased(evt);
            }
        });
        spProgram.setViewportView(tblProgram);
        tblProgram.getColumnModel().getColumn(3).setMinWidth(0);
        tblProgram.getColumnModel().getColumn(3).setPreferredWidth(0);
        tblProgram.getColumnModel().getColumn(3).setMaxWidth(0);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(cctv_ui.Cctv_uiApp.class).getContext().getResourceMap(Cctv_uiView.class);
        tabProgram.addTab(resourceMap.getString("spProgram.TabConstraints.tabTitle"), spProgram); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblProgramPrepare.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblProgramPrepare.setName("tblProgramPrepare"); // NOI18N
        tblProgramPrepare.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblProgramMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblProgramPrepare);

        tabProgram.addTab(resourceMap.getString("jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblProgramDownloading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblProgramDownloading.setName("tblProgramDownloading"); // NOI18N
        tblProgramDownloading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblProgramMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(tblProgramDownloading);

        tabProgram.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tblProgramDownloaded.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblProgramDownloaded.setName("tblProgramDownloaded"); // NOI18N
        tblProgramDownloaded.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblProgramMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(tblProgramDownloaded);

        tabProgram.addTab(resourceMap.getString("jScrollPane3.TabConstraints.tabTitle"), jScrollPane3); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tblProgramRemoved.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblProgramRemoved.setName("tblProgramRemoved"); // NOI18N
        tblProgramRemoved.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblProgramMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(tblProgramRemoved);

        tabProgram.addTab(resourceMap.getString("jScrollPane4.TabConstraints.tabTitle"), jScrollPane4); // NOI18N

        tabEpi.setName("tabEpi"); // NOI18N
        tabEpi.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabEpiStateChanged(evt);
            }
        });

        spEpi.setName("spEpi"); // NOI18N

        tblEpi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title", "ID", "Status", "Type", "Object"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblEpi.setName("tblEpi"); // NOI18N
        tblEpi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblEpiMouseReleased(evt);
            }
        });
        tblEpi.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                tblEpiMouseDragged(evt);
            }
        });
        spEpi.setViewportView(tblEpi);
        tblEpi.getColumnModel().getColumn(4).setMinWidth(0);
        tblEpi.getColumnModel().getColumn(4).setPreferredWidth(0);
        tblEpi.getColumnModel().getColumn(4).setMaxWidth(0);
        tblEpi.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblEpi.columnModel.title4")); // NOI18N

        tabEpi.addTab(resourceMap.getString("spEpi.TabConstraints.tabTitle"), spEpi); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        tblEpiPrepare.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblEpiPrepare.setName("tblEpiPrepare"); // NOI18N
        tblEpiPrepare.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblEpiMouseReleased(evt);
            }
        });
        jScrollPane5.setViewportView(tblEpiPrepare);

        tabEpi.addTab(resourceMap.getString("jScrollPane5.TabConstraints.tabTitle"), jScrollPane5); // NOI18N

        jScrollPane6.setName("jScrollPane6"); // NOI18N

        tblEpiDownloading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblEpiDownloading.setName("tblEpiDownloading"); // NOI18N
        tblEpiDownloading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblEpiMouseReleased(evt);
            }
        });
        jScrollPane6.setViewportView(tblEpiDownloading);

        tabEpi.addTab(resourceMap.getString("jScrollPane6.TabConstraints.tabTitle"), jScrollPane6); // NOI18N

        jScrollPane7.setName("jScrollPane7"); // NOI18N

        tblEpiDownloaded.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblEpiDownloaded.setName("tblEpiDownloaded"); // NOI18N
        tblEpiDownloaded.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblEpiMouseReleased(evt);
            }
        });
        jScrollPane7.setViewportView(tblEpiDownloaded);

        tabEpi.addTab(resourceMap.getString("jScrollPane7.TabConstraints.tabTitle"), jScrollPane7); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        tblEpiRemoved.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblEpiRemoved.setName("tblEpiRemoved"); // NOI18N
        tblEpiRemoved.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblEpiMouseReleased(evt);
            }
        });
        jScrollPane8.setViewportView(tblEpiRemoved);

        tabEpi.addTab(resourceMap.getString("jScrollPane8.TabConstraints.tabTitle"), jScrollPane8); // NOI18N

        tabTask.setName("tabTask"); // NOI18N
        tabTask.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabTaskStateChanged(evt);
            }
        });

        spTask.setName("spTask"); // NOI18N

        tblTask.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Size", "%", "Dest", "URL", "Status", "Cur Spd", "Aver Spd", "Object"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTask.setName("tblTask"); // NOI18N
        tblTask.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblTaskMouseReleased(evt);
            }
        });
        spTask.setViewportView(tblTask);
        tblTask.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblTask.columnModel.title0")); // NOI18N
        tblTask.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblTask.columnModel.title1")); // NOI18N
        tblTask.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblTask.columnModel.title2")); // NOI18N
        tblTask.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblTask.columnModel.title3")); // NOI18N
        tblTask.getColumnModel().getColumn(4).setHeaderValue(resourceMap.getString("tblTask.columnModel.title4")); // NOI18N
        tblTask.getColumnModel().getColumn(5).setHeaderValue(resourceMap.getString("tblTask.columnModel.title5")); // NOI18N
        tblTask.getColumnModel().getColumn(6).setHeaderValue(resourceMap.getString("tblTask.columnModel.title6")); // NOI18N
        tblTask.getColumnModel().getColumn(7).setHeaderValue(resourceMap.getString("tblTask.columnModel.title7")); // NOI18N
        tblTask.getColumnModel().getColumn(8).setMinWidth(0);
        tblTask.getColumnModel().getColumn(8).setPreferredWidth(0);
        tblTask.getColumnModel().getColumn(8).setMaxWidth(0);
        tblTask.getColumnModel().getColumn(8).setHeaderValue(resourceMap.getString("tblTask.columnModel.title8")); // NOI18N

        tabTask.addTab(resourceMap.getString("spTask.TabConstraints.tabTitle"), spTask); // NOI18N

        jScrollPane10.setName("jScrollPane10"); // NOI18N

        tblTaskDownloading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblTaskDownloading.setName("tblTaskDownloading"); // NOI18N
        tblTaskDownloading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblTaskDownloadingMouseReleased(evt);
            }
        });
        jScrollPane10.setViewportView(tblTaskDownloading);

        tabTask.addTab(resourceMap.getString("jScrollPane10.TabConstraints.tabTitle"), jScrollPane10); // NOI18N

        jScrollPane11.setName("jScrollPane11"); // NOI18N

        tblTaskDownloaded.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblTaskDownloaded.setName("tblTaskDownloaded"); // NOI18N
        tblTaskDownloaded.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblTaskDownloadedMouseReleased(evt);
            }
        });
        jScrollPane11.setViewportView(tblTaskDownloaded);

        tabTask.addTab(resourceMap.getString("jScrollPane11.TabConstraints.tabTitle"), jScrollPane11); // NOI18N

        jScrollPane12.setName("jScrollPane12"); // NOI18N

        tblTaskRemoved.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblTaskRemoved.setName("tblTaskRemoved"); // NOI18N
        tblTaskRemoved.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tblTaskRemovedMouseReleased(evt);
            }
        });
        jScrollPane12.setViewportView(tblTaskRemoved);

        tabTask.addTab(resourceMap.getString("jScrollPane12.TabConstraints.tabTitle"), jScrollPane12); // NOI18N

        jScrollPane9.setName("jScrollPane9"); // NOI18N

        taStatus.setColumns(20);
        taStatus.setEditable(false);
        taStatus.setRows(5);
        taStatus.setName("taStatus"); // NOI18N
        jScrollPane9.setViewportView(taStatus);

        btnStartDownloading.setText(resourceMap.getString("btnStartDownloading.text")); // NOI18N
        btnStartDownloading.setName("btnStartDownloading"); // NOI18N
        btnStartDownloading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartDownloadingActionPerformed(evt);
            }
        });

        btnStopDownloading.setText(resourceMap.getString("btnStopDownloading.text")); // NOI18N
        btnStopDownloading.setName("btnStopDownloading"); // NOI18N
        btnStopDownloading.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopDownloadingActionPerformed(evt);
            }
        });

        btnEpiUp.setText(resourceMap.getString("btnEpiUp.text")); // NOI18N
        btnEpiUp.setName("btnEpiUp"); // NOI18N
        btnEpiUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEpiUpActionPerformed(evt);
            }
        });

        btnEpiDown.setText(resourceMap.getString("btnEpiDown.text")); // NOI18N
        btnEpiDown.setName("btnEpiDown"); // NOI18N
        btnEpiDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEpiDownActionPerformed(evt);
            }
        });

        btnEpiSave.setText(resourceMap.getString("btnEpiSave.text")); // NOI18N
        btnEpiSave.setName("btnEpiSave"); // NOI18N
        btnEpiSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEpiSaveActionPerformed(evt);
            }
        });

        btnTaskUp.setText(resourceMap.getString("btnTaskUp.text")); // NOI18N
        btnTaskUp.setName("btnTaskUp"); // NOI18N
        btnTaskUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaskUpActionPerformed(evt);
            }
        });

        btnTaskDown.setText(resourceMap.getString("btnTaskDown.text")); // NOI18N
        btnTaskDown.setName("btnTaskDown"); // NOI18N
        btnTaskDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaskDownActionPerformed(evt);
            }
        });

        btnTaskSave.setText(resourceMap.getString("btnTaskSave.text")); // NOI18N
        btnTaskSave.setName("btnTaskSave"); // NOI18N
        btnTaskSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaskSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 1006, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tabProgram, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
                            .addComponent(tabEpi, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
                            .addComponent(tabTask, javax.swing.GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEpiUp)
                            .addComponent(btnEpiDown)
                            .addComponent(btnEpiSave)
                            .addComponent(btnTaskUp)
                            .addComponent(btnTaskDown)
                            .addComponent(btnTaskSave)
                            .addComponent(btnStartDownloading)
                            .addComponent(btnStopDownloading))
                        .addGap(104, 104, 104))))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(tabProgram, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tabEpi, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tabTask, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEpiUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEpiDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEpiSave)
                        .addGap(132, 132, 132)
                        .addComponent(btnTaskUp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTaskDown)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTaskSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStartDownloading)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStopDownloading)
                        .addGap(125, 125, 125)))
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(cctv_ui.Cctv_uiApp.class).getContext().getActionMap(Cctv_uiView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        helpMenu.add(jMenuItem2);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 1094, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 924, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        ppmProg.setName("ppmProg"); // NOI18N

        pmiAddProg.setText(resourceMap.getString("pmiAddProg.text")); // NOI18N
        pmiAddProg.setName("pmiAddProg"); // NOI18N
        pmiAddProg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiAddProgActionPerformed(evt);
            }
        });
        ppmProg.add(pmiAddProg);

        pmiRetrieveProgInfo.setText(resourceMap.getString("pmiRetrieveProgInfo.text")); // NOI18N
        pmiRetrieveProgInfo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pmiRetrieveProgInfo.setName("pmiRetrieveProgInfo"); // NOI18N
        pmiRetrieveProgInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiRetrieveProgInfoActionPerformed(evt);
            }
        });
        ppmProg.add(pmiRetrieveProgInfo);

        pmiProgramToTask.setText(resourceMap.getString("pmiProgramToTask.text")); // NOI18N
        pmiProgramToTask.setName("pmiProgramToTask"); // NOI18N
        pmiProgramToTask.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pmiProgramToTaskMouseClicked(evt);
            }
        });
        ppmProg.add(pmiProgramToTask);

        ppmProgStatus.setText(resourceMap.getString("ppmProgStatus.text")); // NOI18N
        ppmProgStatus.setName("ppmProgStatus"); // NOI18N
        ppmProg.add(ppmProgStatus);

        ppmProgRemoveFromDB.setText(resourceMap.getString("ppmProgRemoveFromDB.text")); // NOI18N
        ppmProgRemoveFromDB.setActionCommand(resourceMap.getString("ppmProgRemoveFromDB.actionCommand")); // NOI18N
        ppmProgRemoveFromDB.setName("ppmProgRemoveFromDB"); // NOI18N
        ppmProgRemoveFromDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppmProgRemoveFromDBActionPerformed(evt);
            }
        });
        ppmProg.add(ppmProgRemoveFromDB);

        pmiCombin.setText(resourceMap.getString("pmiCombin.text")); // NOI18N
        pmiCombin.setName("pmiCombin"); // NOI18N
        pmiCombin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiCombinActionPerformed(evt);
            }
        });
        ppmProg.add(pmiCombin);

        pmiOpenFolder.setText(resourceMap.getString("pmiOpenFolder.text")); // NOI18N
        pmiOpenFolder.setName("pmiOpenFolder"); // NOI18N
        pmiOpenFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiOpenFolderActionPerformed(evt);
            }
        });
        ppmProg.add(pmiOpenFolder);

        ppmEpi.setName("ppmEpi"); // NOI18N

        pmiAddNewEpi.setText(resourceMap.getString("pmiAddNewEpi.text")); // NOI18N
        pmiAddNewEpi.setName("pmiAddNewEpi"); // NOI18N
        pmiAddNewEpi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiAddNewEpiActionPerformed(evt);
            }
        });
        ppmEpi.add(pmiAddNewEpi);

        pmiRetrieveInfo.setText(resourceMap.getString("pmiRetrieveInfo.text")); // NOI18N
        pmiRetrieveInfo.setName("pmiRetrieveInfo"); // NOI18N
        pmiRetrieveInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiRetrieveInfoActionPerformed(evt);
            }
        });
        ppmEpi.add(pmiRetrieveInfo);

        pmiEpiAddTask.setText(resourceMap.getString("pmiEpiAddTask.text")); // NOI18N
        pmiEpiAddTask.setName("pmiEpiAddTask"); // NOI18N
        pmiEpiAddTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEpiAddTaskActionPerformed(evt);
            }
        });
        ppmEpi.add(pmiEpiAddTask);

        ppmEpiStatus.setText(resourceMap.getString("ppmEpiStatus.text")); // NOI18N
        ppmEpiStatus.setName("ppmEpiStatus"); // NOI18N
        ppmEpi.add(ppmEpiStatus);

        pmmEpiSortBy.setText(resourceMap.getString("pmmEpiSortBy.text")); // NOI18N
        pmmEpiSortBy.setActionCommand(resourceMap.getString("pmmEpiSortBy.actionCommand")); // NOI18N
        pmmEpiSortBy.setName("pmmEpiSortBy"); // NOI18N

        pmiEpiSortByTitle.setText(resourceMap.getString("pmiEpiSortByTitle.text")); // NOI18N
        pmiEpiSortByTitle.setActionCommand(resourceMap.getString("pmiEpiSortByTitle.actionCommand")); // NOI18N
        pmiEpiSortByTitle.setName("pmiEpiSortByTitle"); // NOI18N
        pmiEpiSortByTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEpiSortByTitleActionPerformed(evt);
            }
        });
        pmmEpiSortBy.add(pmiEpiSortByTitle);

        pmiEpiSortByStatus.setText(resourceMap.getString("pmiEpiSortByStatus.text")); // NOI18N
        pmiEpiSortByStatus.setActionCommand(resourceMap.getString("pmiEpiSortByStatus.actionCommand")); // NOI18N
        pmiEpiSortByStatus.setName("pmiEpiSortByStatus"); // NOI18N
        pmiEpiSortByStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEpiSortByStatusActionPerformed(evt);
            }
        });
        pmmEpiSortBy.add(pmiEpiSortByStatus);

        pmiEpiSortById.setText(resourceMap.getString("pmiEpiSortById.text")); // NOI18N
        pmiEpiSortById.setActionCommand(resourceMap.getString("pmiEpiSortById.actionCommand")); // NOI18N
        pmiEpiSortById.setName("pmiEpiSortById"); // NOI18N
        pmiEpiSortById.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEpiSortByIdActionPerformed(evt);
            }
        });
        pmmEpiSortBy.add(pmiEpiSortById);

        pmiEpiSortByIndex.setText(resourceMap.getString("pmiEpiSortByIndex.text")); // NOI18N
        pmiEpiSortByIndex.setActionCommand(resourceMap.getString("pmiEpiSortByIndex.actionCommand")); // NOI18N
        pmiEpiSortByIndex.setName("pmiEpiSortByIndex"); // NOI18N
        pmiEpiSortByIndex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEpiSortByIndexActionPerformed(evt);
            }
        });
        pmmEpiSortBy.add(pmiEpiSortByIndex);

        ppmEpi.add(pmmEpiSortBy);

        pmiEpiRemoveFromDB.setText(resourceMap.getString("pmiEpiRemoveFromDB.text")); // NOI18N
        pmiEpiRemoveFromDB.setActionCommand(resourceMap.getString("pmiEpiRemoveFromDB.actionCommand")); // NOI18N
        pmiEpiRemoveFromDB.setName("pmiEpiRemoveFromDB"); // NOI18N
        pmiEpiRemoveFromDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEpiRemoveFromDBActionPerformed(evt);
            }
        });
        ppmEpi.add(pmiEpiRemoveFromDB);

        pmiMoveTop.setText(resourceMap.getString("pmiMoveTop.text")); // NOI18N
        pmiMoveTop.setName("pmiMoveTop"); // NOI18N
        pmiMoveTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiMoveTopActionPerformed(evt);
            }
        });
        ppmEpi.add(pmiMoveTop);

        pmiMoveBottom.setText(resourceMap.getString("pmiMoveBottom.text")); // NOI18N
        pmiMoveBottom.setName("pmiMoveBottom"); // NOI18N
        pmiMoveBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiMoveBottomActionPerformed(evt);
            }
        });
        ppmEpi.add(pmiMoveBottom);

        pmiCombineEpi.setText(resourceMap.getString("pmiCombineEpi.text")); // NOI18N
        pmiCombineEpi.setName("pmiCombineEpi"); // NOI18N
        pmiCombineEpi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiCombineEpiActionPerformed(evt);
            }
        });
        ppmEpi.add(pmiCombineEpi);

        ppmTask.setName("ppmTask"); // NOI18N

        pmiStartAll.setText(resourceMap.getString("pmiStartAll.text")); // NOI18N
        pmiStartAll.setName("pmiStartAll"); // NOI18N
        pmiStartAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiStartAllActionPerformed(evt);
            }
        });
        ppmTask.add(pmiStartAll);

        pmiStopAll.setText(resourceMap.getString("pmiStopAll.text")); // NOI18N
        pmiStopAll.setName("pmiStopAll"); // NOI18N
        pmiStopAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiStopAllActionPerformed(evt);
            }
        });
        ppmTask.add(pmiStopAll);

        pmiStartSelected.setText(resourceMap.getString("pmiStartSelected.text")); // NOI18N
        pmiStartSelected.setName("pmiStartSelected"); // NOI18N
        pmiStartSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiStartSelectedActionPerformed(evt);
            }
        });
        ppmTask.add(pmiStartSelected);

        pmiStopSelected.setText(resourceMap.getString("pmiStopSelected.text")); // NOI18N
        pmiStopSelected.setName("pmiStopSelected"); // NOI18N
        pmiStopSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiStopSelectedActionPerformed(evt);
            }
        });
        ppmTask.add(pmiStopSelected);

        ppmTaskStatus.setText(resourceMap.getString("ppmTaskStatus.text")); // NOI18N
        ppmTaskStatus.setName("ppmTaskStatus"); // NOI18N
        ppmTask.add(ppmTaskStatus);

        pmiAddDownloader.setText(resourceMap.getString("pmiAddDownloader.text")); // NOI18N
        pmiAddDownloader.setName("pmiAddDownloader"); // NOI18N
        pmiAddDownloader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiAddDownloaderActionPerformed(evt);
            }
        });
        ppmTask.add(pmiAddDownloader);

        pmiReduceDownloader.setText(resourceMap.getString("pmiReduceDownloader.text")); // NOI18N
        pmiReduceDownloader.setName("pmiReduceDownloader"); // NOI18N
        pmiReduceDownloader.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiReduceDownloaderActionPerformed(evt);
            }
        });
        ppmTask.add(pmiReduceDownloader);

        pmiTaskRemoveFromDB.setText(resourceMap.getString("pmiTaskRemoveFromDB.text")); // NOI18N
        pmiTaskRemoveFromDB.setActionCommand(resourceMap.getString("pmiTaskRemoveFromDB.actionCommand")); // NOI18N
        pmiTaskRemoveFromDB.setName("pmiTaskRemoveFromDB"); // NOI18N
        pmiTaskRemoveFromDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiTaskRemoveFromDBActionPerformed(evt);
            }
        });
        ppmTask.add(pmiTaskRemoveFromDB);

        pmiMoveTaskToTop.setText(resourceMap.getString("pmiMoveTaskToTop.text")); // NOI18N
        pmiMoveTaskToTop.setName("pmiMoveTaskToTop"); // NOI18N
        pmiMoveTaskToTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiMoveTaskToTopActionPerformed(evt);
            }
        });
        ppmTask.add(pmiMoveTaskToTop);

        pmiMoveTaskToBottom.setText(resourceMap.getString("pmiMoveTaskToBottom.text")); // NOI18N
        pmiMoveTaskToBottom.setName("pmiMoveTaskToBottom"); // NOI18N
        pmiMoveTaskToBottom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiMoveTaskToBottomActionPerformed(evt);
            }
        });
        ppmTask.add(pmiMoveTaskToBottom);

        pmiRedownload.setText(resourceMap.getString("pmiRedownload.text")); // NOI18N
        pmiRedownload.setName("pmiRedownload"); // NOI18N
        pmiRedownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiRedownloadActionPerformed(evt);
            }
        });
        ppmTask.add(pmiRedownload);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void tabProgramStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_tabProgramStateChanged
    {//GEN-HEADEREND:event_tabProgramStateChanged
        if(this.progList == null)
            return;
        loadTblProg(progFilter());
        //MuLog.log(pane.getSelectedComponent().getName());
    }//GEN-LAST:event_tabProgramStateChanged

    private void tabEpiStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_tabEpiStateChanged
    {//GEN-HEADEREND:event_tabEpiStateChanged
        if(this.epiList == null)
            return;

        loadTblEpi(epiFilter());
    }//GEN-LAST:event_tabEpiStateChanged

    private void tblProgramMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tblProgramMouseReleased
    {//GEN-HEADEREND:event_tblProgramMouseReleased
        if(evt.isPopupTrigger())
        {
            //ppmProg.setVisible(true);
            ppmProg.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tblProgramMouseReleased

    private void pmiProgramToTaskMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_pmiProgramToTaskMouseClicked
    {//GEN-HEADEREND:event_pmiProgramToTaskMouseClicked
        ArrayList<ProgramInfo> arr = getSelectedProg();
        if(arr.size()>0)
        {
            ProgramInfo p = arr.get(0);
            MuLog.log(p.getProgramName());
            String dest = CfgMgr.getDownloadDestFolder() + File.separator + p.getProgramName();
            TaskGenerator.programToTask(p, dest, true);
        }
        ppmProg.setVisible(false);
    }//GEN-LAST:event_pmiProgramToTaskMouseClicked

    private void btnStartDownloadingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnStartDownloadingActionPerformed
    {//GEN-HEADEREND:event_btnStartDownloadingActionPerformed
        ArrayList<CCTVDownloadTask> list = getAllTasks();
        Main.startDownload(list);
    }//GEN-LAST:event_btnStartDownloadingActionPerformed

    private void btnStopDownloadingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnStopDownloadingActionPerformed
    {//GEN-HEADEREND:event_btnStopDownloadingActionPerformed
        Main.stopDownload();
    }//GEN-LAST:event_btnStopDownloadingActionPerformed

    private void tabTaskStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_tabTaskStateChanged
    {//GEN-HEADEREND:event_tabTaskStateChanged
        if(taskList == null) return;
        loadTblTask(taskFilter());
    }//GEN-LAST:event_tabTaskStateChanged

    private void tblEpiMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tblEpiMouseReleased
    {//GEN-HEADEREND:event_tblEpiMouseReleased
        if(evt.isPopupTrigger())
        {
            ppmEpi.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tblEpiMouseReleased

    private void pmiRetrieveProgInfoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiRetrieveProgInfoActionPerformed
    {//GEN-HEADEREND:event_pmiRetrieveProgInfoActionPerformed
        //MuLog.log("Action");
        ArrayList<ProgramInfo> arr = getSelectedProg();
        if(arr.size()>0)
        {
            ProgramInfo p = arr.get(0);
            MuLog.log(p.getProgramName());
            TaskGenerator.addProgram(p.getProgramName(), p.getProgramURL());
            //this.epiList = EpisodeDAO.getWithFilter(null, null, null, null, null);
            initUI();
        }
        ppmProg.setVisible(false);
        //loadTblEpi(epiFilter());
    }//GEN-LAST:event_pmiRetrieveProgInfoActionPerformed

    private void tblEpiMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tblEpiMouseDragged
    {//GEN-HEADEREND:event_tblEpiMouseDragged
        MuLog.log("Dragged");
    }//GEN-LAST:event_tblEpiMouseDragged

    private void btnEpiUpActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnEpiUpActionPerformed
    {//GEN-HEADEREND:event_btnEpiUpActionPerformed
        moveUp(tabEpi);
        /*JTable table = getSelectedTable(tabEpi);
        int[] arr = table.getSelectedRows();
        ListSelectionModel selectionModel = table.getSelectionModel();
        for(int i : arr)
        {
            if(i>0)
            {
                tmEpisode.moveRow(i, i, i-1);
                selectionModel.removeIndexInterval(i, i);
                selectionModel.addSelectionInterval(i-1, i-1);
            }
        }*/

    }//GEN-LAST:event_btnEpiUpActionPerformed

    private void btnEpiDownActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnEpiDownActionPerformed
    {//GEN-HEADEREND:event_btnEpiDownActionPerformed

        moveDown(tabEpi);
        /*
        JTable table = getSelectedTable(tabEpi);
        int[] arr = table.getSelectedRows();
        ListSelectionModel selectionModel = table.getSelectionModel();
        for(int a=arr.length-1; a>=0; a--)
        {
            int i = arr[a];
            MuLog.log(i+"");
            if(i<table.getRowCount()-1)
            {
                tmEpisode.moveRow(i, i, i+1);
            }
        }
        selectionModel.removeIndexInterval(0, table.getRowCount());
        for(int i: arr)
            if(i<table.getRowCount()-1)
                selectionModel.addSelectionInterval(i+1, i+1);

        */
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEpiDownActionPerformed

    private void btnEpiSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnEpiSaveActionPerformed
    {//GEN-HEADEREND:event_btnEpiSaveActionPerformed
        ArrayList<Episode> all = getAllEpisode();
        ArrayList<Long> indexArr  = new ArrayList<Long>();
        for(Episode e:all)
        {
            long index = e.getIndex();
            if(index == 0)
                indexArr.add(UniqueTimeGenerator.currentTimeMillis());
            else
                indexArr.add(index);
        }
        Collections.sort(indexArr);
        int i=0;
        for(Episode e:all)
        {
            e.setIndex(indexArr.get(i++).longValue());
            EpisodeDAO.update(e);
        }
        Collections.sort(epiList, new CompareEpiIndex());
    }//GEN-LAST:event_btnEpiSaveActionPerformed

    private void btnTaskUpActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnTaskUpActionPerformed
    {//GEN-HEADEREND:event_btnTaskUpActionPerformed
        moveUp(tabTask);
    }//GEN-LAST:event_btnTaskUpActionPerformed

    private void btnTaskDownActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnTaskDownActionPerformed
    {//GEN-HEADEREND:event_btnTaskDownActionPerformed
        moveDown(tabTask);
    }//GEN-LAST:event_btnTaskDownActionPerformed

    private void btnTaskSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnTaskSaveActionPerformed
    {//GEN-HEADEREND:event_btnTaskSaveActionPerformed
        ArrayList<CCTVDownloadTask> all = getAllTasks();
        ArrayList<Long> indexArr  = new ArrayList<Long>();
        for(CCTVDownloadTask e:all)
        {
            long index = e.getIndex();
            if(index == 0)
                indexArr.add(UniqueTimeGenerator.currentTimeMillis());
            else
                indexArr.add(index);
        }
        Collections.sort(indexArr);
        int i=0;
        for(CCTVDownloadTask e:all)
        {
            e.setIndex(indexArr.get(i++).longValue());
            DownloadTaskDAO.update(e);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTaskSaveActionPerformed

    private void pmiRetrieveInfoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiRetrieveInfoActionPerformed
    {//GEN-HEADEREND:event_pmiRetrieveInfoActionPerformed
        ArrayList<Episode> arr = getSelectedEpi();
        for(Episode e : arr)
        {
            e.initVideoUrl();
        }
    }//GEN-LAST:event_pmiRetrieveInfoActionPerformed

    private void pmiEpiAddTaskActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiEpiAddTaskActionPerformed
    {//GEN-HEADEREND:event_pmiEpiAddTaskActionPerformed
        ArrayList<Episode> arr = getSelectedEpi();
        String dest = CfgMgr.getDownloadDestFolder() + File.separator ;
        for(Episode e : arr)
        {
            String folder = dest;
            for(ProgramInfo p : progList)
                if(p.getProgramId().equals(e.getProgramId()))
                {
                    folder += p.getProgramName();
                    break;
                }
            try{
            TaskGenerator.episodeToTask(e, folder, true);
            }catch(Exception x){
                MuLog.log(x);
                TaskGenerator.episodeToTask(e, folder, false);
            }
        }
        initUI();
    }//GEN-LAST:event_pmiEpiAddTaskActionPerformed

    private void pmiAddProgActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiAddProgActionPerformed
    {//GEN-HEADEREND:event_pmiAddProgActionPerformed

        if (dlgNewProg == null)
        {
            JFrame mainFrame = Cctv_uiApp.getApplication().getMainFrame();
            dlgNewProg = new Cctv_uiNewProgDlg(mainFrame);
            dlgNewProg.setLocationRelativeTo(mainFrame);
        }
        Cctv_uiApp.getApplication().show(dlgNewProg);
        initUI();

    }//GEN-LAST:event_pmiAddProgActionPerformed

    private void pmiAddNewEpiActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiAddNewEpiActionPerformed
    {//GEN-HEADEREND:event_pmiAddNewEpiActionPerformed
        if (dlgNewEpi == null)
        {
            JFrame mainFrame = Cctv_uiApp.getApplication().getMainFrame();
            dlgNewEpi = new Cctv_uiNewEpiDlg(mainFrame);
            dlgNewEpi.setLocationRelativeTo(mainFrame);
        }
        Cctv_uiApp.getApplication().show(dlgNewEpi);
        initUI();
        // TODO add your handling code here:
    }//GEN-LAST:event_pmiAddNewEpiActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem1ActionPerformed
    {//GEN-HEADEREND:event_jMenuItem1ActionPerformed
        pmiAddProgActionPerformed(evt);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void tblTaskMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tblTaskMouseReleased
    {//GEN-HEADEREND:event_tblTaskMouseReleased
        if(evt.isPopupTrigger())
        {
            ppmTask.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_tblTaskMouseReleased

    private void tblTaskDownloadingMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tblTaskDownloadingMouseReleased
    {//GEN-HEADEREND:event_tblTaskDownloadingMouseReleased
        tblTaskMouseReleased(evt);
    }//GEN-LAST:event_tblTaskDownloadingMouseReleased

    private void tblTaskDownloadedMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tblTaskDownloadedMouseReleased
    {//GEN-HEADEREND:event_tblTaskDownloadedMouseReleased
        tblTaskMouseReleased(evt);
    }//GEN-LAST:event_tblTaskDownloadedMouseReleased

    private void tblTaskRemovedMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_tblTaskRemovedMouseReleased
    {//GEN-HEADEREND:event_tblTaskRemovedMouseReleased
        tblTaskMouseReleased(evt);
    }//GEN-LAST:event_tblTaskRemovedMouseReleased

    private void pmiStartAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiStartAllActionPerformed
    {//GEN-HEADEREND:event_pmiStartAllActionPerformed
        ArrayList<CCTVDownloadTask> list = getAllTasks();
        Main.startDownload(list);
    }//GEN-LAST:event_pmiStartAllActionPerformed

    private void pmiStopAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiStopAllActionPerformed
    {//GEN-HEADEREND:event_pmiStopAllActionPerformed
        Main.stopDownload();
    }//GEN-LAST:event_pmiStopAllActionPerformed

    private void pmiStartSelectedActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiStartSelectedActionPerformed
    {//GEN-HEADEREND:event_pmiStartSelectedActionPerformed
        ArrayList<CCTVDownloadTask> list = getSelectedTasks();
        Main.startDownload(list);
    }//GEN-LAST:event_pmiStartSelectedActionPerformed

    private void pmiStopSelectedActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiStopSelectedActionPerformed
    {//GEN-HEADEREND:event_pmiStopSelectedActionPerformed
        ArrayList<CCTVDownloadTask> list = getSelectedTasks();
        Main.stopDownload(list);
    }//GEN-LAST:event_pmiStopSelectedActionPerformed

    private void pmiAddDownloaderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiAddDownloaderActionPerformed
    {//GEN-HEADEREND:event_pmiAddDownloaderActionPerformed
        Main.addDownloader();
    }//GEN-LAST:event_pmiAddDownloaderActionPerformed

    private void pmiReduceDownloaderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiReduceDownloaderActionPerformed
    {//GEN-HEADEREND:event_pmiReduceDownloaderActionPerformed
        Main.reduceDownloader();
    }//GEN-LAST:event_pmiReduceDownloaderActionPerformed

    private void pmiEpiSortByTitleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiEpiSortByTitleActionPerformed
    {//GEN-HEADEREND:event_pmiEpiSortByTitleActionPerformed
        sortEpiBy(new CompareEpiTitle());
        btnEpiSaveActionPerformed(null);
    }//GEN-LAST:event_pmiEpiSortByTitleActionPerformed

    private void pmiEpiSortByStatusActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiEpiSortByStatusActionPerformed
    {//GEN-HEADEREND:event_pmiEpiSortByStatusActionPerformed
        sortEpiBy(new CompareEpiStatus());
        btnEpiSaveActionPerformed(null);

    }//GEN-LAST:event_pmiEpiSortByStatusActionPerformed

    private void pmiEpiSortByIdActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiEpiSortByIdActionPerformed
    {//GEN-HEADEREND:event_pmiEpiSortByIdActionPerformed
        sortEpiBy(new CompareEpiId());
        btnEpiSaveActionPerformed(null);

    }//GEN-LAST:event_pmiEpiSortByIdActionPerformed

    private void pmiEpiSortByIndexActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiEpiSortByIndexActionPerformed
    {//GEN-HEADEREND:event_pmiEpiSortByIndexActionPerformed
        sortEpiBy(new CompareEpiIndex());
        btnEpiSaveActionPerformed(null);

    }//GEN-LAST:event_pmiEpiSortByIndexActionPerformed

    private void ppmProgRemoveFromDBActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ppmProgRemoveFromDBActionPerformed
    {//GEN-HEADEREND:event_ppmProgRemoveFromDBActionPerformed
        ArrayList<ProgramInfo> arr = getSelectedProg();
        for(ProgramInfo p : arr)
            ProgramDAO.delete(p.getProgramId());
        initUI();
    }//GEN-LAST:event_ppmProgRemoveFromDBActionPerformed

    private void pmiEpiRemoveFromDBActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiEpiRemoveFromDBActionPerformed
    {//GEN-HEADEREND:event_pmiEpiRemoveFromDBActionPerformed
        ArrayList<Episode> arr = getSelectedEpi();
        for(Episode p : arr)
            EpisodeDAO.delete(p.getId());
        initUI();
    }//GEN-LAST:event_pmiEpiRemoveFromDBActionPerformed

    private void pmiTaskRemoveFromDBActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiTaskRemoveFromDBActionPerformed
    {//GEN-HEADEREND:event_pmiTaskRemoveFromDBActionPerformed
        pmiStopSelectedActionPerformed(null);
        ArrayList<CCTVDownloadTask> arr = getSelectedTasks();
        for(CCTVDownloadTask p : arr)
        {
            DownloadTaskDAO.delete(p.getUrl());
            taskList.remove(p);
        }

        initUI();
    }//GEN-LAST:event_pmiTaskRemoveFromDBActionPerformed

    private void pmiMoveTopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiMoveTopActionPerformed
    {//GEN-HEADEREND:event_pmiMoveTopActionPerformed
        moveToTop(tabEpi);
    }//GEN-LAST:event_pmiMoveTopActionPerformed

    private void pmiMoveBottomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiMoveBottomActionPerformed
    {//GEN-HEADEREND:event_pmiMoveBottomActionPerformed
        moveToBottom(tabEpi);
    }//GEN-LAST:event_pmiMoveBottomActionPerformed

    private void pmiMoveTaskToTopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiMoveTaskToTopActionPerformed
    {//GEN-HEADEREND:event_pmiMoveTaskToTopActionPerformed
        moveToTop(tabTask);
    }//GEN-LAST:event_pmiMoveTaskToTopActionPerformed

    private void pmiMoveTaskToBottomActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiMoveTaskToBottomActionPerformed
    {//GEN-HEADEREND:event_pmiMoveTaskToBottomActionPerformed
        moveToBottom(tabTask);
    }//GEN-LAST:event_pmiMoveTaskToBottomActionPerformed

    private void pmiCombinActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiCombinActionPerformed
    {//GEN-HEADEREND:event_pmiCombinActionPerformed
        ArrayList<ProgramInfo> arr = getSelectedProg();
        /*if(arr.size()>0)
        {
            ProgramInfo p = arr.get(0);
            String folder = Global.baseDir + File.separator + "download" + File.separator + p.getProgramName();
            File dir = new File(folder);
            if(!dir.exists())
                dir.mkdirs();
            MediaUtil.joinMp4MediaFolder(dir.getAbsolutePath());
        }*/
        for(ProgramInfo p : arr)
            Main.combineProgram(p);

    }//GEN-LAST:event_pmiCombinActionPerformed

    private void pmiOpenFolderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiOpenFolderActionPerformed
    {//GEN-HEADEREND:event_pmiOpenFolderActionPerformed
        ArrayList<ProgramInfo> arr = getSelectedProg();
        if(arr.size()>0)
        {
            ProgramInfo p = arr.get(0);
            String folder = Global.baseDir + File.separator + "download" + File.separator + p.getProgramName();
            File dir = new File(folder);
            if(!dir.exists())
                dir.mkdirs();
            try{
            Runtime.getRuntime().exec(String.format("explorer \"%s\"", folder));
            }catch(Exception e){ MuLog.log(e);}
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_pmiOpenFolderActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItem2ActionPerformed
    {//GEN-HEADEREND:event_jMenuItem2ActionPerformed
        initUI();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void pmiCombineEpiActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiCombineEpiActionPerformed
    {//GEN-HEADEREND:event_pmiCombineEpiActionPerformed
        ArrayList<Episode> arr = getSelectedEpi();
        for(Episode e : arr)
        {
            Main.combineEpisode(e);
            /*
            String progName = "";
            ProgramInfo p = findProg(e.getProgramId());
            if(p!=null)
                progName = File.separator + p.getProgramName();
            String path = Global.getDownloadFolder() + progName + File.separator + e.getTitle()+".mp4";
            MediaUtil.joinMp4MediaFile(path);*/
        }
    }//GEN-LAST:event_pmiCombineEpiActionPerformed

    private void pmiRedownloadActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pmiRedownloadActionPerformed
    {//GEN-HEADEREND:event_pmiRedownloadActionPerformed
        Main.redownload(this.getSelectedTasks());
    }//GEN-LAST:event_pmiRedownloadActionPerformed

    private ProgramInfo findProg(String id)
    {
        for(ProgramInfo p: progList)
            if(p.getProgramId().equals(id))
                return p;
        return null;
    }

    private void moveToBottom(JTabbedPane pane)
    {
        JTable table = getSelectedTable(pane);
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int[] arr = table.getSelectedRows();
        ListSelectionModel selectionModel = table.getSelectionModel();
        int b=1;
        for(int a=arr.length-1; a>=0; a--)
        {
            int i = arr[a];
            MuLog.log(i+"");
            model.moveRow(i, i, table.getRowCount()-b);
            b++;
        }
        if(arr.length>0)
        {
            selectionModel.removeIndexInterval(0, table.getRowCount());
            selectionModel.addSelectionInterval(table.getRowCount()-b+1, table.getRowCount()-1);
        }
    }

    private void moveToTop(JTabbedPane pane)
    {
        JTable table = getSelectedTable(pane);
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int[] arr = table.getSelectedRows();
        ListSelectionModel selectionModel = table.getSelectionModel();
        int a=0;
        for(a=0; a<arr.length; a++)
        {
            int i = arr[a];
            MuLog.log(i+"");
            model.moveRow(i, i, a);
        }
        if(arr.length>0)
        {
            selectionModel.removeIndexInterval(0, table.getRowCount());
            selectionModel.addSelectionInterval(0, a-1);
        }
    }

    private void moveDown(JTabbedPane pane)
    {
        JTable table = getSelectedTable(pane);
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int[] arr = table.getSelectedRows();
        ListSelectionModel selectionModel = table.getSelectionModel();
        for(int a=arr.length-1; a>=0; a--)
        {
            int i = arr[a];
            MuLog.log(i+"");
            if(i<table.getRowCount()-1)
            {
                model.moveRow(i, i, i+1);
            }
        }
        selectionModel.removeIndexInterval(0, table.getRowCount());
        for(int i: arr)
            if(i<table.getRowCount()-1)
                selectionModel.addSelectionInterval(i+1, i+1);
    }

    private void moveUp(JTabbedPane pane)
    {
        JTable table = getSelectedTable(pane);
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int[] arr = table.getSelectedRows();
        ListSelectionModel selectionModel = table.getSelectionModel();
        for(int i : arr)
        {
            if(i>0)
            {
                model.moveRow(i, i, i-1);
                selectionModel.removeIndexInterval(i, i);
                selectionModel.addSelectionInterval(i-1, i-1);
            }
        }
        
    }
    private void sortEpiBy(Comparator c)
    {
        JTable table = getSelectedTable(tabEpi);
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        int[] arr = table.getSelectedRows();
        int i=0;
        ArrayList<Episode> list = getSelectedEpi();
        Collections.sort(list, c);
        for(Episode e : list)
        {
            int curInd = findEpiRowIndex(e);
            model.moveRow(curInd, curInd, arr[i++]);
        }
    }

    private ArrayList<ProgramInfo> progFilter()
    {
        ArrayList<ProgramInfo> arr = new ArrayList<ProgramInfo>();
        JTabbedPane pane = tabProgram;
        int i = pane.getSelectedIndex();
        switch(i)
        {
            case 0:
                arr.addAll( this.progList);
                break;
            case 1:
                for(ProgramInfo p : this.progList)
                    if(p.getStatus().ordinal() < ProgramStatus.Added_To_Tasklist.ordinal())
                        arr.add(p);
                break;
            case 2:
                for(ProgramInfo p : this.progList)
                    if(p.getStatus().ordinal() >= ProgramStatus.Added_To_Tasklist.ordinal() &&
                       p.getStatus() != ProgramStatus.Removed && p.getStatus() != ProgramStatus.Finished)
                        arr.add(p);
                break;
            case 3:
                for(ProgramInfo p : this.progList)
                    if( p.getStatus() == ProgramStatus.Finished )
                        arr.add(p);
                break;
            case 4:
                for(ProgramInfo p : this.progList)
                    if( p.getStatus() == ProgramStatus.Removed )
                        arr.add(p);
                break;
        }
        return arr;
    }
    private ArrayList<CCTVDownloadTask> getAllTasks()
    {
        ArrayList<CCTVDownloadTask> re = new ArrayList<CCTVDownloadTask>();
        int j = tmTask.getColumnCount()-1;
        for(int i=0; i<tmTask.getRowCount(); i++)
            re.add((CCTVDownloadTask)tmTask.getValueAt(i, j));
        return re;
    }
    private ArrayList<Episode> getAllEpisode()
    {
        ArrayList<Episode> re = new ArrayList<Episode>();
        int j = tmEpisode.getColumnCount()-1;
        for(int i=0; i<tmEpisode.getRowCount(); i++)
            re.add((Episode)tmEpisode.getValueAt(i, j));
        return re;
    }

    private JTable getSelectedTable(JTabbedPane pane)
    {
        JScrollPane jsp = (JScrollPane)pane.getSelectedComponent();
        JTable table = null;
        try{
        for(Component c : jsp.getComponents())
        {
            MuLog.log(c.getClass().toString());
            if(c.getClass().equals(Class.forName("javax.swing.JViewport")))
            {
                table = (JTable)(((JViewport)c).getView());
                break;
            }
        }
        }catch(Exception e){MuLog.log(e);};
        return table;
    }

    private ArrayList<CCTVDownloadTask> getSelectedTasks()
    {
        ArrayList<CCTVDownloadTask> re = new ArrayList<CCTVDownloadTask> ();
        JTable table = getSelectedTable(tabTask);
        int[] arr = table.getSelectedRows();
        for(int i:arr)
        {
            re.add((CCTVDownloadTask)tmTask.getValueAt(i, tmTask.getColumnCount()-1));
        }
        return re;
    }

    private ArrayList<ProgramInfo> getSelectedProg()
    {
        ArrayList<ProgramInfo> re = new ArrayList<ProgramInfo>();
        JTable table = getSelectedTable(tabProgram);
        int[] arr = table.getSelectedRows();
        for(int i:arr)
        {
            re.add((ProgramInfo)tmProgram.getValueAt(i, tmProgram.getColumnCount()-1));
        }
        return re;
    }

    private ArrayList<Episode> getSelectedEpi()
    {
        ArrayList<Episode> re = new ArrayList<Episode> ();
        JTable table = getSelectedTable(tabEpi);
        int[] arr = table.getSelectedRows();
        for(int i:arr)
        {
            re.add((Episode)tmEpisode.getValueAt(i, tmEpisode.getColumnCount()-1));
        }
        return re;
    }

    private void initUI()
    {
        populateData();


        tmProgram = (DefaultTableModel)tblProgram.getModel();
        tblProgramPrepare.setModel(tmProgram);
        tblProgramDownloading.setModel(tmProgram);
        tblProgramDownloaded.setModel(tmProgram);
        tblProgramRemoved.setModel(tmProgram);
        
        tmEpisode = (DefaultTableModel)tblEpi.getModel();
        tblEpiDownloaded.setModel(tmEpisode);
        tblEpiDownloading.setModel(tmEpisode);
        tblEpiPrepare.setModel(tmEpisode);
        tblEpiRemoved.setModel(tmEpisode);

        tmTask = (DefaultTableModel)tblTask.getModel();
        tblTaskDownloading.setModel(tmTask);
        tblTaskDownloaded.setModel(tmTask);
        tblTaskRemoved.setModel(tmTask);

        tblProgram.getSelectionModel().addListSelectionListener(new ProgListSelectionHandler());
        tblProgramPrepare.getSelectionModel().addListSelectionListener(new ProgListSelectionHandler());
        tblProgramDownloading.getSelectionModel().addListSelectionListener(new ProgListSelectionHandler());
        tblProgramDownloaded.getSelectionModel().addListSelectionListener(new ProgListSelectionHandler());
        tblProgramRemoved.getSelectionModel().addListSelectionListener(new ProgListSelectionHandler());
        tblEpi.getSelectionModel().addListSelectionListener(new EpiListSelectionHandler());
        tblEpiPrepare.getSelectionModel().addListSelectionListener(new EpiListSelectionHandler());
        tblEpiDownloading.getSelectionModel().addListSelectionListener(new EpiListSelectionHandler());
        tblEpiDownloaded.getSelectionModel().addListSelectionListener(new EpiListSelectionHandler());
        tblEpiRemoved.getSelectionModel().addListSelectionListener(new EpiListSelectionHandler());
        tblTask.getSelectionModel().addListSelectionListener(new TaskListSelectionHandler());
        tblTaskDownloaded.getSelectionModel().addListSelectionListener(new TaskListSelectionHandler());
        tblTaskDownloading.getSelectionModel().addListSelectionListener(new TaskListSelectionHandler());
        tblTaskRemoved.getSelectionModel().addListSelectionListener(new TaskListSelectionHandler());

        loadTblProg(progList);
        loadTblEpi(epiList);
        loadTblTask(taskList);

        
        //MuLog.log("ppmProgStatus count="+ppmProgStatus.getItemCount());
        if(ppmProgStatus.getItemCount()==0)
        {
            for(ProgramStatus i : ProgramStatus.values())
            {
                JMenuItem m = new JMenuItem(i.toString());
                m.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            setProgStatus(e);
                        }
                    });
                ppmProgStatus.add(m);
            }
        }

        if(ppmEpiStatus.getItemCount()==0)
        {
            for(EpisodeStatus i : EpisodeStatus.values())
            {
                JMenuItem m = new JMenuItem(i.toString());
                m.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            setEpiStatus(e);
                        }
                    });
                ppmEpiStatus.add(m);
            }
        }

        if(ppmTaskStatus.getItemCount()==0)
        {
            for(TaskStatus i : TaskStatus.values())
            {
                JMenuItem m = new JMenuItem(i.toString());
                m.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            setTaskStatus(e);
                        }
                    });
                ppmTaskStatus.add(m);
            }
        }

        tabProgramStateChanged(null);
        tabEpiStateChanged(null);
        tabTaskStateChanged(null);
    }

    private void setProgStatus(ActionEvent e)
    {
        MuLog.log(e.getActionCommand());
        String action = e.getActionCommand();
        ArrayList<ProgramInfo> arr = getSelectedProg();
        for(ProgramInfo x : arr)
        {
            x.setStatus(ProgramStatus.valueOf(action));
            ProgramDAO.update(x);
        }
        loadTblProg(progFilter());

    }


    private void setEpiStatus(ActionEvent e)
    {
        MuLog.log(e.getActionCommand());
        String action = e.getActionCommand();
        ArrayList<Episode> arr = getSelectedEpi();
        for(Episode x : arr)
        {
            x.setStatus(EpisodeStatus.valueOf(action));
            EpisodeDAO.update(x);
        }
        loadTblEpi(epiFilter());
    }

    private void setTaskStatus(ActionEvent e)
    {
        MuLog.log(e.getActionCommand());
        String action = e.getActionCommand();
        ArrayList<CCTVDownloadTask> arr = getSelectedTasks();
        for(CCTVDownloadTask x : arr)
        {
            x.setStatusCode(TaskStatus.valueOf(action));
            DownloadTaskDAO.update(x);
        }
        //loadTblEpi(epiFilter());
    }


    private void populateData()
    {
        progList = ProgramDAO.getWithFilter(null, null, null, null, null, null, false);
        epiList = EpisodeDAO.getWithFilter(null, null, null, null, null);
        if(taskList == null)
            taskList = DownloadTaskDAO.getWithFilter(null, null, null, null);
        else
        {
            ArrayList<CCTVDownloadTask> arr = DownloadTaskDAO.getWithFilter(null, null, null, null);
            HashMap<String, CCTVDownloadTask> tmpMap = new HashMap<String, CCTVDownloadTask> ();
            for(CCTVDownloadTask t : taskList)
                tmpMap.put(t.getUrl(), t);
            for(CCTVDownloadTask t : arr)
                if(!tmpMap.containsKey(t.getUrl()))
                    taskList.add(t);
        }

        for(ProgramInfo p : progList)
        {
            String pId = p.getProgramId();
            ArrayList<Episode> epiArr = p.getEpisodes();
            for(Episode e : epiList)
                if(e.getProgramId().equals(pId))
                    epiArr.add(e);
        }

        ProgramInfo others = new BaseProgramInfo();
        others.setProgramName("Others");
        others.setProgramId("0");
        for(Episode e : epiList)
        {
            if(e.getProgramId()==null || e.getProgramId().isEmpty() || e.getProgramId().equals("0"))
            {
                e.setProgramId("0");
                others.getEpisodes().add(e);
            }
        }
        if(others.getEpisodes().size()>0)
            progList.add(others);


        for(CCTVDownloadTask t : taskList)
            t.addObserver(this.taskUpdater);
        for(Episode e : epiList)
            ((BaseEpisode)e).addObserver(taskUpdater);

    }

    private void reloadTblProg()
    {
        loadTblProg(progList);
    }
    private void loadTblProg(ArrayList<ProgramInfo> arr)
    {

        Object[][] data = new Object[arr.size()][tblProgram.getColumnCount()];
        int i=0;
        for(ProgramInfo p : arr)
        {
            data[i][0] = p.getProgramName();
            data[i][1] = p.getStatus().toString();
            data[i][2] = ProgramUtil.getType(p);
            data[i][3] = p;
            i++;
        }
        tmProgram.setDataVector(data, pgmCol);

        hideProgObjCol();
    }

    private void reloadTblEpi()
    {
        //ArrayList<Episode> arr = EpisodeDAO.getWithFilter(null, null, null, null, null);
        loadTblEpi(epiList);
    }
    private void loadTblEpi(ArrayList<Episode> arr)
    {
        Object[][] data = new Object[arr.size()][tblEpi.getColumnCount()];
        int i=0;
        for(Episode p : arr)
        {
            data[i][0] = p.getTitle();
            data[i][1] = p.getId();
            data[i][2] = p.getStatus().toString();
            data[i][3] = ProgramUtil.getType(p);
            data[i][4] = p;
            i++;
        }
        tmEpisode.setDataVector(data, epiCol);
        //hideObjCol(tblEpi);
        hideEpiObjCol();
    }

    private void loadTblTask(ArrayList<CCTVDownloadTask> arr)
    {
        Object[][] data = new Object[arr.size()][tblTask.getColumnCount()];
        int i=0;
        for(CCTVDownloadTask p : arr)
        {
            data[i][0] = p.getName();
            data[i][1] = p.getSize();
            data[i][2] = p.getSize()>0 ? p.getDownloaded()*100/p.getSize() : 0;
            data[i][3] = p.getDest();
            data[i][4] = p.getUrl();
            data[i][5] = p.getStatusCode();
            data[i][6] = "0";
            data[i][7] = "0";
            data[i][8] = p;

            i++;
        }
        tmTask.setDataVector(data, taskCol);
        hideTaskObjCol();
    }

    private void hideProgObjCol()
    {
        hideObjCol(tblProgram);
        hideObjCol(tblProgramDownloaded);
        hideObjCol(tblProgramDownloading);
        hideObjCol(tblProgramPrepare);
        hideObjCol(tblProgramRemoved);
    }
    private void hideEpiObjCol()
    {
        hideObjCol(tblEpi);
        hideObjCol(tblEpiDownloaded);
        hideObjCol(tblEpiDownloading);
        hideObjCol(tblEpiPrepare);
        hideObjCol(tblEpiRemoved);
    }
    private void hideTaskObjCol()
    {
        hideObjCol(tblTask);
        hideObjCol(tblTaskDownloaded);
        hideObjCol(tblTaskDownloading);
        hideObjCol(tblTaskRemoved);
    }
    private void hideObjCol(JTable t)
    {
        t.getColumn("Object").setMinWidth(0);
        t.getColumn("Object").setMaxWidth(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEpiDown;
    private javax.swing.JButton btnEpiSave;
    private javax.swing.JButton btnEpiUp;
    private javax.swing.JButton btnStartDownloading;
    private javax.swing.JButton btnStopDownloading;
    private javax.swing.JButton btnTaskDown;
    private javax.swing.JButton btnTaskSave;
    private javax.swing.JButton btnTaskUp;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem pmiAddDownloader;
    private javax.swing.JMenuItem pmiAddNewEpi;
    private javax.swing.JMenuItem pmiAddProg;
    private javax.swing.JMenuItem pmiCombin;
    private javax.swing.JMenuItem pmiCombineEpi;
    private javax.swing.JMenuItem pmiEpiAddTask;
    private javax.swing.JMenuItem pmiEpiRemoveFromDB;
    private javax.swing.JMenuItem pmiEpiSortById;
    private javax.swing.JMenuItem pmiEpiSortByIndex;
    private javax.swing.JMenuItem pmiEpiSortByStatus;
    private javax.swing.JMenuItem pmiEpiSortByTitle;
    private javax.swing.JMenuItem pmiMoveBottom;
    private javax.swing.JMenuItem pmiMoveTaskToBottom;
    private javax.swing.JMenuItem pmiMoveTaskToTop;
    private javax.swing.JMenuItem pmiMoveTop;
    private javax.swing.JMenuItem pmiOpenFolder;
    private javax.swing.JMenuItem pmiProgramToTask;
    private javax.swing.JMenuItem pmiRedownload;
    private javax.swing.JMenuItem pmiReduceDownloader;
    private javax.swing.JMenuItem pmiRetrieveInfo;
    private javax.swing.JMenuItem pmiRetrieveProgInfo;
    private javax.swing.JMenuItem pmiStartAll;
    private javax.swing.JMenuItem pmiStartSelected;
    private javax.swing.JMenuItem pmiStopAll;
    private javax.swing.JMenuItem pmiStopSelected;
    private javax.swing.JMenuItem pmiTaskRemoveFromDB;
    private javax.swing.JMenu pmmEpiSortBy;
    private javax.swing.JPopupMenu ppmEpi;
    private javax.swing.JMenu ppmEpiStatus;
    private javax.swing.JPopupMenu ppmProg;
    private javax.swing.JMenuItem ppmProgRemoveFromDB;
    private javax.swing.JMenu ppmProgStatus;
    private javax.swing.JPopupMenu ppmTask;
    private javax.swing.JMenu ppmTaskStatus;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane spEpi;
    private javax.swing.JScrollPane spProgram;
    private javax.swing.JScrollPane spTask;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextArea taStatus;
    private javax.swing.JTabbedPane tabEpi;
    private javax.swing.JTabbedPane tabProgram;
    private javax.swing.JTabbedPane tabTask;
    private javax.swing.JTable tblEpi;
    private javax.swing.JTable tblEpiDownloaded;
    private javax.swing.JTable tblEpiDownloading;
    private javax.swing.JTable tblEpiPrepare;
    private javax.swing.JTable tblEpiRemoved;
    private javax.swing.JTable tblProgram;
    private javax.swing.JTable tblProgramDownloaded;
    private javax.swing.JTable tblProgramDownloading;
    private javax.swing.JTable tblProgramPrepare;
    private javax.swing.JTable tblProgramRemoved;
    private javax.swing.JTable tblTask;
    private javax.swing.JTable tblTaskDownloaded;
    private javax.swing.JTable tblTaskDownloading;
    private javax.swing.JTable tblTaskRemoved;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JDialog dlgNewProg;
    private JDialog dlgNewEpi;
    private DefaultTableModel tmProgram;
    private DefaultTableModel tmEpisode;
    private DefaultTableModel tmTask;
    private String[] pgmCol = new String[] {"Name", "Status", "Type", "Object"};
    private String[] epiCol = new String[] {"Title", "ID", "Status", "Type", "Object"};
    private String[] taskCol = new String [] {"Name", "Size", "%", "Dest", "URL", "Status", "Cur Spd", "Aver Spd", "Object"};
    private TableUpdater taskUpdater = new TableUpdater();

    ArrayList<ProgramInfo> progList;
    ArrayList<Episode> epiList;
    ArrayList<CCTVDownloadTask> taskList;
    /*private HashMap<String, ProgramStatus> progStaMap = new HashMap<String, ProgramStatus> ();
    private HashMap<String, EpisodeStatus> epiStaMap = new HashMap<String, EpisodeStatus> ();
    private HashMap<String, TaskStatus> taskStaMap = new HashMap<String, TaskStatus> ();
*/
    private ArrayList<Episode> epiFilter()
    {
        ArrayList<Episode> arr = new ArrayList<Episode>();
        JTable table = getSelectedTable(tabProgram);
    
        //filter by program selection
        int[] arri = table.getSelectedRows();
        ArrayList<Episode> list = new ArrayList<Episode>();
        for(int i:arri)
        {
            MuLog.log("Selected row:" + i);
            ProgramInfo p = (ProgramInfo)table.getValueAt(i, 3);
            list.addAll(p.getEpisodes());
        }
        /*if(list.size()==0)
            list.addAll(epiList);*/
        Collections.sort(list, new CompareEpiIndex());
        
        MuLog.log("List size:"+list.size());

        //filter by epitab
        int i = tabEpi.getSelectedIndex();
        MuLog.log("Selected tab:" + i);
        switch(i)
        {
            case 0:
                arr.addAll( list);
                break;
            case 1:
                for(Episode p : list)
                    if(p.getStatus().ordinal() < EpisodeStatus.Added_To_Tasklist.ordinal())
                        arr.add(p);
                break;
            case 2:
                for(Episode p : list)
                    if(p.getStatus().ordinal() >= EpisodeStatus.Added_To_Tasklist.ordinal() &&
                       p.getStatus() != EpisodeStatus.Removed && p.getStatus() != EpisodeStatus.Finished)
                        arr.add(p);
                break;
            case 3:
                for(Episode p : list)
                    if( p.getStatus() == EpisodeStatus.Finished )
                        arr.add(p);
                break;
            case 4:
                for(Episode p : list)
                    if( p.getStatus() == EpisodeStatus.Removed )
                        arr.add(p);
                break;
        }
        MuLog.log("arr size:"+arr.size());

        return arr;
    }

    private ArrayList<CCTVDownloadTask> taskFilter()
    {
        ArrayList<CCTVDownloadTask> arr = new ArrayList<CCTVDownloadTask>();
        if(taskList==null)
            return arr;
        ArrayList<CCTVDownloadTask> list = taskList;
        int i = tabTask.getSelectedIndex();
        MuLog.log("Selected tab:" + i);
        switch(i)
        {
            case 0:
                arr.addAll( list);
                break;
            case 1:
                for(CCTVDownloadTask p : list)
                    if(p.getStatusCode() != TaskStatus.Removed && p.getStatusCode() != TaskStatus.Finished)
                        arr.add(p);
                break;
            case 2:
                for(CCTVDownloadTask p : list)
                    if(p.getStatusCode() == TaskStatus.Finished)
                        arr.add(p);
                break;
            case 3:
                for(CCTVDownloadTask p : list)
                    if( p.getStatusCode() != TaskStatus.Removed)
                        arr.add(p);
                break;
        }
        return arr;
    }

    private int findTaskRowIndex(CCTVDownloadTask t)
    {
         ArrayList<CCTVDownloadTask> arr = getAllTasks();
         int i=0;
         boolean found = false;
         for(i=0; i<arr.size(); i++)
             if(arr.get(i).equals(t))
             {
                 found = true;
                 break;
             }
         if(found)
            return i;
         else
             return -1;
    }
    private int findEpiRowIndex(Episode t)
    {
         ArrayList<Episode> arr = getAllEpisode();
         int i=-1;
         for(i=0; i<arr.size(); i++)
             if(arr.get(i).equals(t))
                 break;
         return i;
    }

    class ProgListSelectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            ArrayList<ProgramInfo> arr = getSelectedProg();
            if(arr.size()>0)
            {
                taStatus.setText(arr.get(0).toString());
                loadTblEpi(epiFilter());
            }
        }
    }

    class EpiListSelectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            ArrayList<Episode> arr = getSelectedEpi();
            if(arr.size()>0)
                taStatus.setText(arr.get(0).toString());
        }
    }
    class TaskListSelectionHandler implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent e)
        {
            ArrayList<CCTVDownloadTask> arr = getSelectedTasks();
            if(arr.size()>0)
                taStatus.setText(arr.get(0).toString());
        }
    }

    class TableUpdater implements Observer
    {
        public void update(Observable source, Object arg)
        {
            //MuLog.log("Informed");
            if(source.getClass().equals(CCTVDownloadTask.class))
            {
                int i = findTaskRowIndex((CCTVDownloadTask)source);
                //MuLog.log("Row no:"+i);
                if(i!=-1)
                {
                    CCTVDownloadTask p = (CCTVDownloadTask) source;
                    tmTask.setValueAt(p.getName(), i, 0);
                    tmTask.setValueAt(p.getSize(), i, 1);
                    tmTask.setValueAt(p.getSize()>0 ? p.getDownloaded()*100/p.getSize() : 0, i, 2);
                    tmTask.setValueAt(p.getDest(), i, 3);
                    tmTask.setValueAt(p.getUrl(), i, 4);
                    tmTask.setValueAt(p.getStatusCode(), i, 5);
                    tmTask.setValueAt(p.getCurSpeed(), i, 6);
                    tmTask.setValueAt(p.getAverSpeed(), i, 7);
                    tmTask.setValueAt(p, i, 8);
                }
            }
            else if(source.getClass().equals(VodEpisode.class) || source.getClass().equals(SpaceEpisode.class))
            {
                int i = findEpiRowIndex((Episode)source);
                if(i!=-1)
                {
                    Episode p = (Episode) source;
                    tmEpisode.setValueAt(p.getTitle(), i, 0);
                    tmEpisode.setValueAt(p.getId(), i, 1);
                    tmEpisode.setValueAt(p.getStatus(), i, 2);
                    tmEpisode.setValueAt(ProgramUtil.getType(p), i, 3);
                    tmEpisode.setValueAt(p, i, 4);
                }
            }

        }
    }


}
