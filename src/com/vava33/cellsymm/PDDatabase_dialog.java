package com.vava33.cellsymm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.jutils.Cif_file;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.FilteredListModel;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

public abstract class PDDatabase_dialog {
    public static float minDspacingToSearch = 1.15f; //def 1.15
    private static float minDspacingLatGen = 1.05f; //def 1.05
    private static final int maxNsol = 50;

    protected String helpMSG = "\n"
            +"** General help **\n"
            +" - Click on a compound to see the peak positions on top of your pattern (if ShowPeaks is selected)\n"
            +" - Check apply name filter and type to find the desired compound\n"
            +" - Add/Edit compounds by clicking the respective buttons and filling the info. Alternatively you can edit manually the DB file\n"
            + "(which is a simple self-explanatory text file)\n"
            +"** Search by peaks **\n"
            +" - To search by peaks on the database first you must perform \"Processing->find peaks\" in the desired dataserie. Then: \n"
            +" - Click the button -search by peaks-\n"
            +" - List will be updated by the best matching compounds (with respective residuals)\n"
            +" - Click on the compounds to see the peak positions on top of your pattern and check their match\n"
            +"\n"
            +"Note:\n"
            + "The default DB is a small selection of compounds taken from different sources, mostly publications. Each entry contains the reference from\n"
            + "where it has been taken (with the respective authors) which can be seen when selecting the compound.\n"
            + "For any doubts/comments/complaints/suggestions, please contact the author\n"
            +"\n";
    
    protected JDialog DBdialog;
    private JButton btnLoadDB;
    private JCheckBox cbox_onTop;
    protected JCheckBox chckbxPDdata;
    private JPanel contentPanel;
    private JLabel lblHelp;
    protected JList<Object> listCompounds;
    private JPanel panel_left;
    private JScrollPane scrollPane;
    
    private DefaultListModel<Object> lm;
    private boolean showPDDataPeaks;
    public static JProgressBar pBarDB;
    public ProgressMonitor pm;
    private PDDatabase.openDBfileWorker openDBFwk;
    private PDDatabase.saveDBfileWorker saveDBFwk;
    protected PDDatabase.searchDBWorker searchDBwk;
    private JButton btnSearchByPeaks;
    private JCheckBox chckbxNameFilter;
    private JTextField txtNamefilter;
    
    private JButton btnResetSearch;
    private JButton btnSaveDb;
    private JLabel lblHeader;
    private JPanel panel;
    private JPanel panel_1;
    private JButton btnAddCompound;
    private static final String className = "DB";
    protected static VavaLogger log = CellSymm_global.getVavaLogger(className);

    protected JButton btnAddAsSerie;
    private JPanel panel_3;
    private JLabel lblName;
    private JLabel lblNamealt;
    private JLabel lblFormula;
    private JLabel lblCellParameters;
    private JLabel lblSpaceGroup;
    private JLabel lblReference;
    private JLabel lblComment;
    private JLabel label;
    private JTextArea textAreaDsp;
    private JScrollPane scrollPane_2;
    private JTextField txtName;
    private JTextField txtNamealt;
    private JTextField txtFormula;
    private JTextField txtCellParameters;
    private JTextField txtSpaceGroup;
    private JTextField txtReference;
    private JTextField txtComment;
    private JButton btnCalcRefl;
    private JButton btnApplyChanges;
    private JButton btnRemove;
    private JButton btnAddAsNew;
    private JButton btnImportCif;
    private JButton btnImportHkl;
    private JSplitPane splitPane_1;
    protected JCheckBox chckbxIntensity;
    private JCheckBox chckbxAddToQuicklist;
    private JCheckBox chckbxShowQL;
    
    private boolean warningSave = true;
    /**
     * Create the dialog.
     */
    public PDDatabase_dialog(JFrame parent) {
    	DBdialog = new JDialog(parent,"Compound DB",false);
    	this.contentPanel=new JPanel();
    	DBdialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                do_this_windowClosing(e);
            }
        });

        DBdialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        DBdialog.setSize(660,730);
        
        DBdialog.getContentPane().setLayout(new MigLayout("fill, insets 5", "[grow]", "[grow][37px]"));
        DBdialog.getContentPane().add(this.contentPanel, "cell 0 0,grow");
        contentPanel.setLayout(new MigLayout("fill, insets 0", "[grow]", "[598px,grow]"));
        {
            {
                this.panel_left = new JPanel();
                contentPanel.add(this.panel_left, "cell 0 0,grow");
                {
                    {
                        {
                            panel_left.setLayout(new MigLayout("fill, insets 0", "[grow]", "[25px][grow]"));
                            {
                                panel = new JPanel();
                                panel_left.add(panel, "cell 0 0,grow");
                                panel.setLayout(new MigLayout("fill, insets 0", "[][][][][grow][][]", "[]"));
                                this.btnLoadDB = new JButton("Load DB");
                                panel.add(btnLoadDB, "cell 0 0,growx,aligny center");
                                {
                                    btnSaveDb = new JButton("Save DB");
                                    panel.add(btnSaveDb, "cell 1 0,alignx center,aligny center");
                                    this.chckbxPDdata = new JCheckBox("Show Peaks");
                                    panel.add(chckbxPDdata, "cell 2 0,alignx left,aligny center");
                                    this.chckbxPDdata.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent arg0) {
                                            do_chckbxCalibrate_itemStateChanged(arg0);
                                        }
                                    });
                                    {
                                    	chckbxIntensity = new JCheckBox("Show Intensity");
                                    	chckbxIntensity.addItemListener(new ItemListener() {
                                    		public void itemStateChanged(ItemEvent e) {
                                    			do_chckbxIntensity_itemStateChanged(e);
                                    		}
                                    	});
                                    	panel.add(chckbxIntensity, "cell 3 0");
                                    }
                                    {
                                        this.lblHelp = new JLabel("?");
                                        panel.add(lblHelp, "cell 5 0,alignx right,aligny center");
                                        this.lblHelp.addMouseListener(new MouseAdapter() {
                                            @Override
                                            public void mouseEntered(MouseEvent e) {
                                                do_lbllist_mouseEntered(e);
                                            }

                                            @Override
                                            public void mouseExited(MouseEvent e) {
                                                do_lbllist_mouseExited(e);
                                            }

                                            @Override
                                            public void mouseReleased(MouseEvent e) {
                                                do_lbllist_mouseReleased(e);
                                            }
                                        });
                                        this.lblHelp.setFont(new Font("Tahoma", Font.BOLD, 14));
                                    }
                                    this.cbox_onTop = new JCheckBox("on top");
                                    panel.add(cbox_onTop, "cell 6 0,alignx right,aligny center");
                                    this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                                    this.cbox_onTop.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent arg0) {
                                            do_cbox_onTop_itemStateChanged(arg0);
                                        }
                                    });
                                    this.cbox_onTop.setActionCommand("on top");
                                    btnSaveDb.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent arg0) {
                                            do_btnSaveDb_actionPerformed(arg0);
                                        }
                                    });
                                }
                                this.btnLoadDB.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent arg0) {
                                        do_btnLoadDB_actionPerformed(arg0);
                                    }
                                });
                            }
                        }
                    }
                }
                {
                    splitPane_1 = new JSplitPane();
                    splitPane_1.setResizeWeight(0.5);
                    splitPane_1.setContinuousLayout(true);
                    panel_left.add(splitPane_1, "cell 0 1,grow");
                    {
                        panel_3 = new JPanel();
                        panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Selected Compound Info", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
                        splitPane_1.setRightComponent(panel_3);
                        panel_3.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][][][][][grow][]"));
                        {
                        }
                        btnImportHkl = new JButton("Import HKL");
                        btnImportHkl.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                do_btnImportHkl_actionPerformed(e);
                            }
                        });
                        {
                            btnImportCif = new JButton("Import CIF");
                            btnImportCif.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnImportCif_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnImportCif, "cell 1 0,alignx right");
                        }
                        panel_3.add(btnImportHkl, "cell 2 0,alignx right");
                        {
                            lblName = new JLabel("Name");
                            panel_3.add(lblName, "cell 0 1,alignx trailing");
                        }
                        {
                            txtName = new JTextField();
                            txtName.setText("Name");
                            panel_3.add(txtName, "cell 1 1 2 1,growx");
                            txtName.setColumns(10);
                        }
                        {
                            lblNamealt = new JLabel("Name (alt)");
                            panel_3.add(lblNamealt, "cell 0 2,alignx trailing");
                        }
                        {
                            txtNamealt = new JTextField();
                            txtNamealt.setText("NameAlt");
                            panel_3.add(txtNamealt, "cell 1 2 2 1,growx");
                            txtNamealt.setColumns(10);
                        }
                        {
                            lblFormula = new JLabel("Formula");
                            panel_3.add(lblFormula, "cell 0 3,alignx trailing");
                        }
                        {
                            txtFormula = new JTextField();
                            txtFormula.setText("Formula");
                            panel_3.add(txtFormula, "cell 1 3 2 1,growx");
                            txtFormula.setColumns(10);
                        }
                        {
                            lblCellParameters = new JLabel("Cell parameters");
                            panel_3.add(lblCellParameters, "cell 0 4,alignx trailing");
                        }
                        {
                            txtCellParameters = new JTextField();
                            txtCellParameters.setText("Cell Parameters");
                            panel_3.add(txtCellParameters, "cell 1 4 2 1,growx");
                            txtCellParameters.setColumns(10);
                        }
                        {
                            lblSpaceGroup = new JLabel("Space group");
                            panel_3.add(lblSpaceGroup, "cell 0 5,alignx trailing");
                        }
                        {
                            txtSpaceGroup = new JTextField();
                            txtSpaceGroup.setText("Space Group");
                            panel_3.add(txtSpaceGroup, "cell 1 5,growx");
                            txtSpaceGroup.setColumns(10);
                        }
                        {
                            btnCalcRefl = new JButton("Calc Refl.");
                            btnCalcRefl.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnCalcRefl_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnCalcRefl, "cell 2 5,growx");
                        }
                        {
                            lblReference = new JLabel("Reference");
                            panel_3.add(lblReference, "cell 0 6,alignx trailing");
                        }
                        {
                            txtReference = new JTextField();
                            txtReference.setText("Reference");
                            panel_3.add(txtReference, "cell 1 6 2 1,growx");
                            txtReference.setColumns(10);
                        }
                        {
                            lblComment = new JLabel("Comment");
                            panel_3.add(lblComment, "cell 0 7,alignx trailing");
                        }
                        {
                            txtComment = new JTextField();
                            txtComment.setText("Comment");
                            panel_3.add(txtComment, "cell 1 7 2 1,growx");
                            txtComment.setColumns(10);
                        }
                        {
                        	chckbxAddToQuicklist = new JCheckBox("Add to QuickList");
                        	chckbxAddToQuicklist.addActionListener(new ActionListener() {
                        		public void actionPerformed(ActionEvent e) {
                        			do_chckbxAddToQuicklist_actionPerformed(e);
                        		}
                        	});
                        	panel_3.add(chckbxAddToQuicklist, "cell 0 8 3 1,alignx right");
                        }
                        {
                            label = new JLabel("list of (one per line): h k l d-spacing intensity");
                            panel_3.add(label, "cell 0 9 3 1,alignx left");
                        }
                        {
                            scrollPane_2 = new JScrollPane();
                            panel_3.add(scrollPane_2, "cell 0 10 3 1,grow");
                            {
                                textAreaDsp = new JTextArea();
                                scrollPane_2.setViewportView(textAreaDsp);
                                textAreaDsp.setRows(3);
                            }
                        }
                        {
                            btnApplyChanges = new JButton("Apply Changes");
                            btnApplyChanges.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnApplyChanges_actionPerformed(e);
                                }
                            });
                            btnAddAsSerie = new JButton("Add as Serie");
                            btnAddAsSerie.addActionListener(new ActionListener() {
                            	public void actionPerformed(ActionEvent e) {
                            		do_btnAddAsSerie_actionPerformed(e);
                            	}
                            });
                            panel_3.add(btnAddAsSerie, "cell 0 11,alignx left");
                            panel_3.add(btnApplyChanges, "cell 1 11,alignx right");
                        }
                        {
                            btnAddAsNew = new JButton("Add as New");
                            btnAddAsNew.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnAddAsNew_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnAddAsNew, "cell 2 11");
                        }
                    }
                    {
                        panel_1 = new JPanel();
                        panel_1.setBorder(new TitledBorder(null, "Compound List", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                        splitPane_1.setLeftComponent(panel_1);
                        panel_1.setLayout(new MigLayout("fill", "[][][grow][]", "[][][][grow][]"));
                        {
                            chckbxNameFilter = new JCheckBox("Apply name filter:");
                            chckbxNameFilter.setSelected(true);
                            chckbxNameFilter.addItemListener(new ItemListener() {
                                public void itemStateChanged(ItemEvent e) {
                                    do_chckbxNameFilter_itemStateChanged(e);
                                }
                            });
                            panel_1.add(chckbxNameFilter, "cell 0 0 2 1,alignx left");
                        }
                        txtNamefilter = new JTextField();
                        panel_1.add(txtNamefilter, "cell 2 0 2 1,growx,aligny center");
                        
                        txtNamefilter.getDocument().addDocumentListener(new DocumentListener() {
                            public void changedUpdate(DocumentEvent e) {
                                filterList();
                              }
                              public void removeUpdate(DocumentEvent e) {
                                filterList();
                              }
                              public void insertUpdate(DocumentEvent e) {
                                filterList();
                              }
                            });
                        {
                        	chckbxShowQL = new JCheckBox("Show QuickList only");
                        	chckbxShowQL.addItemListener(new ItemListener() {
                        		public void itemStateChanged(ItemEvent e) {
                        			do_chckbxShowQL_itemStateChanged(e);
                        		}
                        	});
                        	panel_1.add(chckbxShowQL, "cell 0 1 4 1");
                        }
                        {
                            lblHeader = new JLabel("header");
                            panel_1.add(lblHeader, "cell 0 2 4 1,alignx left");
                        }
                        {
                            this.scrollPane = new JScrollPane();
                            panel_1.add(scrollPane, "cell 0 3 4 1,grow");
                            {
                                this.listCompounds = new JList<Object>();
                                listCompounds.setFont(new Font("Monospaced", Font.PLAIN, 15));
                                listCompounds.addListSelectionListener(new ListSelectionListener() {
                                    public void valueChanged(ListSelectionEvent arg0) {
                                        do_listCompounds_valueChanged(arg0);
                                    }
                                });
                                this.scrollPane.setViewportView(this.listCompounds);
                            }
                        }
                        {
                            btnAddCompound = new JButton("New");
                            panel_1.add(btnAddCompound, "cell 0 4,growx");
                            {
                                btnRemove = new JButton("Remove");
                                btnRemove.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        do_btnRemove_actionPerformed(e);
                                    }
                                });
                                panel_1.add(btnRemove, "cell 1 4,alignx left");
                            }
                            btnSearchByPeaks = new JButton("Search by peaks");
                            panel_1.add(btnSearchByPeaks, "cell 2 4,alignx right");
                            btnSearchByPeaks.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnSearchByPeaks_actionPerformed(e);
                                }
                            });
                            btnResetSearch = new JButton("reset list");
                            panel_1.add(btnResetSearch, "cell 3 4,alignx center");
                            btnResetSearch.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnResetSearch_actionPerformed(arg0);
                                }
                            });
                            btnAddCompound.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnAddCompound_actionPerformed(arg0);
                                }
                            });
                        }
                    }
                }
                {
                }
            }
        }
        {
            JPanel buttonPane = new JPanel();
            DBdialog.getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
            JButton okButton = new JButton("close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            {
                buttonPane.setLayout(new MigLayout("fill, insets 0", "[grow][]", "[]"));
            }
            {
                pBarDB = new JProgressBar();
                buttonPane.add(pBarDB, "cell 0 0,growx");
                pBarDB.setStringPainted(true);
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 1 0,alignx right,aligny center");
            DBdialog.getRootPane().setDefaultButton(okButton);
        }
        DBdialog.setAlwaysOnTop(cbox_onTop.isSelected());
        log.info("** PDDatabase **");
        DBdialog.pack();
        this.inicia();
    }

    private void inicia(){
        lm = new DefaultListModel<Object>();
        listCompounds.setModel(lm);
        this.readDB(true);
    }

    private void readDB(boolean readDefault) {
        pm = new ProgressMonitor(DBdialog,
                "Reading DB file...",
                "", 0, 100);
        pm.setProgress(0);
        pBarDB.setString("Reading DB");
        pBarDB.setStringPainted(true);
        
        //db per defecte:
        File DBFile = new File(PDDatabase.getDefaultDBpath());
        
        if (!readDefault) {
            //Load file
            FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("DB file (db,txt,dat)", "db", "txt", "dat")};
            DBFile = FileUtils.fchooserOpen(DBdialog,new File(this.getWorkDir()), filter, 0);
            if(DBFile==null){
            	log.warning("No data file selected");
                return;
            }
            this.setWorkDir(DBFile);
        }
        
        //Si hem arribat aquí creem el worker, hi afegim el listener
        openDBFwk = new PDDatabase.openDBfileWorker(DBFile);
        openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              if ("progress" == evt.getPropertyName() ) {
                  int progress = (Integer) evt.getNewValue();
                  pm.setProgress(progress);
                  pBarDB.setValue(progress);
                  pm.setNote(String.format("%d%%\n", progress));
                  if (pm.isCanceled() || openDBFwk.isDone()) {
                      Toolkit.getDefaultToolkit().beep();
                      if (pm.isCanceled()) {
                          openDBFwk.cancel(true);
                          log.info("reading of DB file "+openDBFwk.getReadedFile()+" stopped!");
                          log.info("Number of compounds = "+PDDatabase.getnCompounds());    
                      } else {
                    	  log.info("reading of DB file "+openDBFwk.getReadedFile()+" finished!");
                    	  log.info("Number of compounds = "+PDDatabase.getnCompounds());    
                      }
                      pm.close();
                      pBarDB.setValue(100);
                      pBarDB.setStringPainted(true);
                      updateListAllCompounds();
                      PDDatabase.setDBmodified(false);
                  }
              }
            }
        });
        
        //reset current Database
        PDDatabase.resetDB();
        //read database file, executing the swingworker task
        openDBFwk.execute();
        if (!new File(this.getDBFile()).getName().equalsIgnoreCase(DBFile.getName())){
            //ask if this new file should become the default one on config
            boolean defDB = FileUtils.YesNoDialog(DBdialog, "Set this DB file as the default for further sessions?");
            if (defDB) {
                this.setDBFile(DBFile.getAbsolutePath());
            }
        }
    }
    
    //boolean ask for default or not
    protected void do_btnLoadDB_actionPerformed(ActionEvent arg0) {
        this.readDB(false);
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
    	DBdialog.setAlwaysOnTop(cbox_onTop.isSelected());
    }
    
    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.showPDDataPeaks = chckbxPDdata.isSelected();
        checkboxShowCanvia();
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        lblHelp.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        lblHelp.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
        FileUtils.InfoDialog(DBdialog, helpMSG, "Database Help");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.checkSaveAndDispose();
    }

    public PDCompound getCurrentCompound() {
        if (listCompounds == null){return null;}
        if (listCompounds.getSelectedIndex() >= 0) {
            if (listCompounds.getSelectedValue() instanceof PDCompound){
                PDCompound comp = (PDCompound) listCompounds.getSelectedValue();
                return comp;
            }
            if (listCompounds.getSelectedValue() instanceof PDSearchResult){
                PDSearchResult sr = (PDSearchResult) listCompounds.getSelectedValue();
                return sr.getC();
            }
        }
        return null;
    }
    
    public List<PDCompound> getCurrentCompounds() {
        if (listCompounds == null){return null;}
        
        int nsel = listCompounds.getSelectedIndices().length;
        if (nsel > 0) {
        	List<PDCompound> comps = new ArrayList<PDCompound>();
            if (listCompounds.getSelectedValue() instanceof PDCompound){
            	for (Object pdc:listCompounds.getSelectedValuesList()) {
            		comps.add((PDCompound) pdc);
            	}
            }
            if (listCompounds.getSelectedValue() instanceof PDSearchResult){
            	for (Object sr:listCompounds.getSelectedValuesList()) {
            		comps.add(((PDSearchResult) sr).getC());
            	}
            }
            return comps;
        }
        return null;
    }
    

    public boolean isShowDataPeaks() {
        return showPDDataPeaks;
    }

    public void updateListAllCompounds() {
    	listCompounds.setModel(new DefaultListModel()); //TODO improve, so many calls? create custom model?
    	SwingWorker<Integer, Void> wk = new SwingWorker<Integer,Void>(){
    	
			@Override
			protected Integer doInBackground() throws Exception {
		        pBarDB.setString("Populating List");
		        pBarDB.setStringPainted(true);
		        lm.clear();
		        Iterator<PDCompound> itrcomp = PDDatabase.getDBCompList().iterator();
		        int n = 0;
		        int ncomp = PDDatabase.getDBCompList().size();
		        while (itrcomp.hasNext()){
		            lm.addElement(itrcomp.next());
		            log.debug("update list... "+n);
		            //progress
		            if (n%100 == 0){
		                pBarDB.setValue((int)(((float)n/(float)ncomp)/100.f));    
		            }
		            n++;
		        }
		        return 0;
		        
			}
			
		    @Override
		    public void done() {
		    	log.config("updateListAllCompounds done");
		    	listCompounds.setModel(lm);
		        lblHeader.setText(" Name  [Formula]  (alt. names)");
		        pBarDB.setValue(100);
		        pBarDB.setStringPainted(false);
		        listCompounds.setSelectedIndex(0);
		    }
    	};
    	
    	wk.run();
    	
    }

    
    public boolean isShowPDDataPeaks() {
        return showPDDataPeaks;
    }

    public void setShowPDDataPeaks(boolean showPDDataPk) {
        this.showPDDataPeaks = showPDDataPk;
    }
    
    
    public void updateInfo(PDCompound c){
        txtName.setText(c.getCompNames().get(0));
        txtNamealt.setText(c.getAltNames());
        txtFormula.setText(c.getFormula());
        txtCellParameters.setText(c.getCellParameters());
        txtSpaceGroup.setText(c.getCella().getSg().getName());
        txtReference.setText(c.getReference());
        txtComment.setText(c.getAllComments());
        textAreaDsp.setText(c.getHKLlines());
        chckbxAddToQuicklist.setSelected(c.isQuicklist());
    }
    
    public void prepareFields(){
        txtName.setText("");
        txtNamealt.setText("");
        txtFormula.setText("");
        txtCellParameters.setText("");
        txtSpaceGroup.setText("");
        txtReference.setText("");
        txtComment.setText("");
        chckbxAddToQuicklist.setSelected(false);
    }
    
    public void loadSearchPeaksResults(){
        
        if (lm==null){return;}
        if (PDDatabase.getDBSearchresults().size()==0){return;}
        
        lm.clear();
        
        List<PDSearchResult> res = PDDatabase.getDBSearchresults();
        
        //mirem si hi ha criteris complementaris pel residual
        Iterator<PDSearchResult> itrcomp = res.iterator();
        while (itrcomp.hasNext()){
            PDSearchResult c = itrcomp.next();
            float resid = c.getResidualPositions();
            resid = resid * ((Math.max((float)c.getC().getNrRefUpToDspacing(PDSearchResult.getMinDSPin())/(float)PDSearchResult.getnDSPin(),1))/2);
            c.setTotal_residual(resid);
        }            
        Collections.sort(res);
        itrcomp = res.iterator();
        int nsol = 0;
        while (itrcomp.hasNext()){
            if (nsol >= maxNsol) break;
            PDSearchResult c = itrcomp.next();
            lm.addElement(c);
            nsol = nsol + 1;
        }
        lblHeader.setText(" Residual  inputRefs/compoundRefs  CompoundName  [Formula]  (alt. names)");
        this.actualitzaPlot();
    }
    
    
    public abstract void searchPeaks();
    
    
    protected void do_btnSearchByPeaks_actionPerformed(ActionEvent e) {
        this.searchPeaks();
    }
    
    protected void filterList(){
        if (lm.isEmpty()){return;}
        if (chckbxNameFilter.isSelected()){
            //filter list
            FilteredListModel filteredListModel = new FilteredListModel(lm);
            listCompounds.setModel(filteredListModel);
            filteredListModel.setFilter(new FilteredListModel.Filter() {
                public boolean accept(Object element) {

                    PDCompound comp = null;
                    try{
                        comp = (PDCompound)element;    
                    }catch(Exception e){
                        comp = ((PDSearchResult)element).getC();
                    }
                    if (comp == null) return false;
                    
                    StringBuilder compinfo = new StringBuilder();
                    compinfo.append(comp.getCompNames()).append(" ");
                    compinfo.append(comp.getFormula()).append(" ");
                    compinfo.append(comp.getAltNames()).append(" ");
                    compinfo.append(comp.getCellParameters()).append(" ");
                    compinfo.append(comp.getCella().getSg().getName()).append(" ");
                    compinfo.append(comp.getAllComments()).append(" ");
                    
                    String s = compinfo.toString().trim();
                    
                    String[] sMult = txtNamefilter.getText().split("\\s+");
                    for (int i=0; i<sMult.length; i++){
                        if (!FileUtils.containsIgnoreCase(s, sMult[i])){
                            return false;
                        }
                    }
                    return true;
                    
                }
            });
            if (txtNamefilter.getText().trim().length() == 0){
                listCompounds.setModel(lm);
                log.info("Number of compounds = "+lm.getSize());    
            }else{
            	log.info("Number of (filtered) compounds = "+filteredListModel.getSize());    
            }
        }
        this.actualitzaPlot();
        
    }
    protected void do_btnResetSearch_actionPerformed(ActionEvent arg0) {
    	this.txtNamefilter.setText("");
    	this.chckbxShowQL.setSelected(false);
		this.inicia();
    	this.updateListAllCompounds();
    	this.actualitzaPlot();
    }
    protected void do_btnSaveDb_actionPerformed(ActionEvent arg0) {
        FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("DB files", "db", "DB")};
        File f = FileUtils.fchooserSaveAsk(DBdialog, new File(PDDatabase.getCurrentDB()),filter,null);
        if (f == null)return;
        this.setWorkDir(f);
        pm = new ProgressMonitor(DBdialog,
                "Saving DB file...",
                "", 0, 100);
        pm.setProgress(0);
        pBarDB.setString("Saving DB");
        pBarDB.setStringPainted(true);
        
        //Si hem arribat aquí creem el worker, hi afegim el listener
        saveDBFwk = new PDDatabase.saveDBfileWorker(f);
        saveDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pBarDB.setValue(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    if (pm.isCanceled() || saveDBFwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            saveDBFwk.cancel(true);
                            log.warning("Error saving file "+saveDBFwk.getDbFileString());
                        } else {
                        	log.info("DB saved to "+saveDBFwk.getDbFileString());
                        }
                        pm.close();
                        pBarDB.setValue(100);
                        pBarDB.setStringPainted(true);
                        PDDatabase.setDBmodified(false);
                    }
                }
            }
        });
        
        saveDBFwk.execute();
        if (!new File(this.getDBFile()).getName().equalsIgnoreCase(saveDBFwk.getDbFileString())){
            //ask if this new file should become the default one on config
            boolean defDB = FileUtils.YesNoDialog(DBdialog, "Set this DB file as the default for further sessions?");
            if (defDB) {
                this.setDBFile(f.getAbsolutePath());
            }
        }
    }


    public static float getMinDspacingToSearch() {
        return minDspacingToSearch;
    }

    public static void setMinDspacingToSearch(float minDspacingToSearch) {
        PDDatabase_dialog.minDspacingToSearch = minDspacingToSearch;
    }
    
    protected void do_btnApplyChanges_actionPerformed(ActionEvent e) {
        this.updateCompoundFromFields(this.getCurrentCompound());
    }
    
    
    protected void do_btnAddAsNew_actionPerformed(ActionEvent e) {
        PDCompound co = new PDCompound(txtName.getText().trim());
        PDDatabase.addCompoundDB(co);
        this.updateCompoundFromFields(co);
        lm.addElement(co);
        PDDatabase.setDBmodified(true);
        listCompounds.setSelectedIndex(listCompounds.getModel().getSize()-1);
        this.scrollPane.validate();
        JScrollBar vertical = this.scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    protected void do_btnAddCompound_actionPerformed(ActionEvent arg0) {
        PDCompound co = new PDCompound("NEW COMPOUND");
        PDDatabase.addCompoundDB(co);
        lm.addElement(co);
        PDDatabase.setDBmodified(true);
        listCompounds.setSelectedIndex(listCompounds.getModel().getSize()-1);
        this.scrollPane.validate();
        JScrollBar vertical = this.scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    protected void do_btnRemove_actionPerformed(ActionEvent e) {
        boolean remove = FileUtils.YesNoDialog(DBdialog, "Remove selected Compound?");
        if (remove) PDDatabase.getDBCompList().remove(this.getCurrentCompound());
        lm.removeElement(this.getCurrentCompound());
        PDDatabase.setDBmodified(true);
    }
    
    private boolean updateCompoundFromFields(PDCompound comp) {
        
        String cell = txtCellParameters.getText().trim();
        String[] cellp = cell.split("\\s+");
        float a,b,c,alfa,beta,gamma;
        try{
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        }catch(Exception e){
            JOptionPane.showMessageDialog(DBdialog, "Error parsing cell parameters, should be: a b c alpha beta gamma");
            return false;
        }
        if (txtName.getText().isEmpty()){
            JOptionPane.showMessageDialog(DBdialog, "Please give the compound name");
            return false;
        }
        
        String[] hkl_lines = textAreaDsp.getText().trim().split("\\n");
        List<HKLrefl> pdref = new ArrayList<HKLrefl>();
        //CHECK CONSISTENCY HKL
        for (int i=0;i<hkl_lines.length;i++){
            String[] line = hkl_lines[i].trim().split("\\s+");
            if (line.length<4){
                JOptionPane.showMessageDialog(DBdialog, "Error in hkl lines, should be at least: h k l dspacing");
                return false;
            }else{
                try{
                    int h = Integer.parseInt(line[0]);
                    int k = Integer.parseInt(line[1]);
                    int l = Integer.parseInt(line[2]);
                    float dsp = Float.parseFloat(line[3]);
                    float inten = 0.0f;
                    try {
                        inten = Float.parseFloat(line[4]);    
                    }catch(Exception e2) {
//                        log.debug("no intensity for reflection");
                    }
                    HKLrefl refl = new HKLrefl(h,k,l,dsp,inten,0);
                    pdref.add(refl);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(DBdialog, "Error in parsing hkl lines, e.g: 1 0 0 12.5 100.0");
                    return false;
                }
            }
        }
        
        //now we put the info into COMP
        comp.getCompNames().clear();
        comp.addCompoundName(txtName.getText().trim());
        comp.addCompoundName(txtNamealt.getText().trim());
        comp.setFormula(txtFormula.getText().trim());
        comp.getCella().setCellParameters(a, b, c, alfa, beta, gamma, true);
        comp.getCella().setSg(CellSymm_global.getSpaceGroupByName(txtSpaceGroup.getText().trim(), true));
        comp.setReference(txtReference.getText().trim());
        comp.getComment().clear();
        comp.addComent(txtComment.getText().trim());
        //dsp + intensities
        comp.getPeaks().clear();
        comp.setPeaks(pdref);
        comp.setQuicklist(chckbxAddToQuicklist.isSelected());

        if(warningSave) {
        	JOptionPane.showMessageDialog(DBdialog, "Do not forget to save the DB into a file \n(otherwise changes will be lost on close)");
        	warningSave=false;
        }
        return true;
    }
    
    protected void do_btnImportHkl_actionPerformed(ActionEvent e) {
        //lines like this:
        //0  -1  -1  26042. 547.139   1
        FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("HKL file", "hkl", "HKL")};
        File hklfile = FileUtils.fchooserOpen(DBdialog,new File(this.getWorkDir()), filter, 0);
        if(hklfile==null)return;
        this.setWorkDir(hklfile);
        
        String cell = txtCellParameters.getText().trim();
        String[] cellp = cell.split("\\s+");
        double a,b,c,alfa,beta,gamma;
        try{
            a = Double.parseDouble(cellp[0]);
            b = Double.parseDouble(cellp[1]);
            c = Double.parseDouble(cellp[2]);
            alfa = Double.parseDouble(cellp[3]);
            beta = Double.parseDouble(cellp[4]);
            gamma = Double.parseDouble(cellp[5]);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(DBdialog, "Cell parameters needed (a b c alpha beta gamma) to parse hkl file");
            return;
        }
        
        Scanner shkl;
        List<HKLrefl> refs = new ArrayList<HKLrefl>();
        Cell cel = new Cell(a,b,c,alfa,beta,gamma,true);
        try {
            shkl = new Scanner(hklfile);
            while (shkl.hasNextLine()){
                String line = shkl.nextLine();
                String[] values = line.split("\\s+");
                try{
                    int h = Integer.parseInt(values[0]);
                    int k = Integer.parseInt(values[1]);
                    int l = Integer.parseInt(values[2]);
                    float inten = Integer.parseInt(values[3]);
                    refs.add(new HKLrefl(h,k,l,cel.calcDspHKL(h, k, l),inten,0));
                }catch(Exception ex2){
                    log.warning("Error parsing h,k,l,intensity values");
                }
            }
            shkl.close();
        } catch (Exception ex) {
            log.warning("Error reading HKL file");
        }
        
        if (refs.size()==0){
            log.warning("No reflections found");
            return;
        }
        
        //calculem el factor de normalitzacio de les intensitats a 100
        Iterator<HKLrefl> itrr = refs.iterator();
        double maxInten = -1;
        while (itrr.hasNext()){
            HKLrefl p = itrr.next();
            double pinten = p.getYcalc();
            if(pinten>maxInten){
                maxInten = pinten;
            }
        }
        double factor = 100/maxInten;
        
        //Ara ja escribim al textarea
        textAreaDsp.setText("");
        itrr = refs.iterator();
        while (itrr.hasNext()){
            HKLrefl p = itrr.next();
            textAreaDsp.append(String.format("%4d %4d %4d %8.4f %8.2f\n", p.getH(),p.getK(),p.getL(),p.getDsp(),p.getYcalc()*factor));
        }
    }
    
    protected void do_btnImportCif_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("CIF file", "cif", "CIF")};
        File ciffile = FileUtils.fchooserOpen(DBdialog,new File(this.getWorkDir()), filter, 0);
        if(ciffile==null)return;
        this.setWorkDir(ciffile);
        Cif_file cf = new Cif_file(ciffile,true);
        
        //populate fields
        txtName.setText(cf.getNom());
        txtNamealt.setText("");
        txtFormula.setText("");
        txtCellParameters.setText(cf.getCellParametersAsString());
        if (cf.getSgString().trim().length()<=0) {
            //put the number
            txtSpaceGroup.setText(Integer.toString(cf.getSgNum()));
        }else {
            txtSpaceGroup.setText(cf.getSgString());    
        }
        txtReference.setText("");
        txtComment.setText("");
        //else calculem reflexions, utilitzem directament cf que ha estat corregit si era necessari
        Cell cel = new Cell(cf);
        cel.generateHKLsAsymetricUnitCrystalFamily(1/(minDspacingLatGen*minDspacingLatGen), true, true, true, true, true);
        cel.calcInten(true);
        cel.normIntensities(100);
        this.textAreaDsp.setText("");
        this.textAreaDsp.setText(cel.getListAsString_HKLMerged_dsp_Fc2());
    }
    
    protected void do_btnCalcRefl_actionPerformed(ActionEvent e) {
        SpaceGroup sg = CellSymm_global.getSpaceGroupByName(txtSpaceGroup.getText().trim(),true);
        String cell = txtCellParameters.getText().trim();
        String[] cellp = cell.split("\\s+");
        float a,b,c,alfa,beta,gamma;
        try{
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(DBdialog, "Cell parameters needed (a b c alpha beta gamma) to parse hkl file");
            return;
        }
        Cell cel = new Cell(a,b,c,alfa,beta,gamma,true,sg);
        cel.generateHKLsAsymetricUnitCrystalFamily(1, true, true, true, true, true);
        
        this.textAreaDsp.setText("");
        this.textAreaDsp.setText(cel.getListAsString_HKLMerged_dsp_Fc2());
    }
    
    
    protected void do_this_windowClosing(WindowEvent e) {
        checkSaveAndDispose();
    }
    
    private void checkSaveAndDispose() {
        if(PDDatabase.isDBmodified()){
            //prompt and save QL file if necessary
            int save = FileUtils.YesNoCancelDialog(DBdialog, "Database has changed, Do you want to save it?");
            if (save==1) {
                this.do_btnSaveDb_actionPerformed(null);
            }
            if (save==-1) {
                return; //not save nor exit
            }
        }
        this.dispose();
    }
    public void visible(boolean vis) {
    	DBdialog.setVisible(vis);
    	if(vis)this.chckbxPDdata.setSelected(true);
    }
    public void dispose() {
        this.chckbxPDdata.setSelected(false);
        DBdialog.dispose();
    }
	
    protected void do_chckbxNameFilter_itemStateChanged(ItemEvent e) {
        if (!chckbxNameFilter.isSelected()) {
            this.txtNamefilter.setText("");
            listCompounds.setModel(lm);
            this.actualitzaPlot();
        }
    }
    
	protected void do_chckbxShowQL_itemStateChanged(ItemEvent e) {
		//nomes mostrarem els que son QL
		if (lm.isEmpty()){return;}
		if (chckbxShowQL.isSelected()) {
            //filter list
            FilteredListModel filteredListModel = new FilteredListModel(lm);
            listCompounds.setModel(filteredListModel);
            filteredListModel.setFilter(new FilteredListModel.Filter() {
                public boolean accept(Object element) {
                    PDCompound comp = null;
                    try{
                        comp = (PDCompound)element;    
                    }catch(Exception e){
                        comp = ((PDSearchResult)element).getC();
                    }
                    if (comp == null) return false;
                    
                    if (comp.isQuicklist()) {
                    	return true;
                    }else {
                    	return false;
                    }
                }
            });
        	log.info("Number of (filtered) compounds = "+filteredListModel.getSize());
		}else {
            listCompounds.setModel(lm);
            log.info("Number of compounds = "+lm.getSize());    
		}
        this.actualitzaPlot();
		
	}

	protected void do_chckbxAddToQuicklist_actionPerformed(ActionEvent e) {
		PDDatabase.setDBmodified(true); //per avisar que cal posar aplicar
        this.btnApplyChanges.doClick();
		
	}
	
    //aquest s'ha d'implementar per fer el que faci falta quan es canvia la seleccio a la llista, etc... sempre que es fa una acció que impliqui actualitzar grafics
    /*
     * p.ex. al d1Dplot tenia:
     *  this.plotpanel.actualitzaPlot()
     *  
     * i al d2Dplot tenia:
     *  this.getIpanel().actualitzarVista();
     */
    protected abstract void actualitzaPlot();

    //que fer quan es canvia el valor de showPPDataPeaks
    protected abstract void checkboxShowCanvia();
    
    protected abstract String getWorkDir();
    protected abstract void setWorkDir(File f);
    protected abstract String getDBFile();
    protected abstract void setDBFile(String s);
    protected abstract void do_listCompounds_valueChanged(ListSelectionEvent arg0);
    protected abstract void do_btnAddAsSerie_actionPerformed(ActionEvent e);
    protected abstract void do_chckbxIntensity_itemStateChanged(ItemEvent e);
        
    
    



}
