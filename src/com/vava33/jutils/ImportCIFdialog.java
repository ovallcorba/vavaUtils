package com.vava33.jutils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.vava33.jutils.Cif_file;
import com.vava33.jutils.FileUtils;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ImportCIFdialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtA;
    private JTextField txtB;
    private JTextField txtC;
    private JTextField txtAlpha;
    private JTextField txtBeta;
    private JTextField txtGamma;
    private JTextField txtName;
    private JTextField txtSgsymbol;
    private JTextField txtSgnum;
    private JTable table;
    private JScrollPane scrollPane;

    private boolean okclosed;

    /**
     * Create the dialog.
     */
    public ImportCIFdialog(Cif_file cf) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(ImportCIFdialog.class.getResource("/img/ico_vava.png")));
        setTitle("Import CIF");
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        setBounds(100, 100, 626, 498);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow][grow][grow][grow][grow][grow][grow]", "[][][][][grow]"));
        {
            JLabel lblName = new JLabel("Name");
            contentPanel.add(lblName, "cell 0 0,alignx trailing");
        }
        {
            txtName = new JTextField();
            txtName.setText("name");
            contentPanel.add(txtName, "cell 1 0 6 1,growx");
            txtName.setColumns(10);
        }
        {
            JLabel lblCellParameters = new JLabel("Cell Parameters");
            contentPanel.add(lblCellParameters, "cell 0 1,alignx trailing");
        }
        {
            txtA = new JTextField();
            txtA.setText("a");
            contentPanel.add(txtA, "cell 1 1,growx");
            txtA.setColumns(10);
        }
        {
            txtB = new JTextField();
            txtB.setText("b");
            contentPanel.add(txtB, "cell 2 1,growx");
            txtB.setColumns(10);
        }
        {
            txtC = new JTextField();
            txtC.setText("c");
            contentPanel.add(txtC, "cell 3 1,growx");
            txtC.setColumns(10);
        }
        {
            txtAlpha = new JTextField();
            txtAlpha.setText("alpha");
            contentPanel.add(txtAlpha, "cell 4 1,growx");
            txtAlpha.setColumns(10);
        }
        {
            txtBeta = new JTextField();
            txtBeta.setText("beta");
            contentPanel.add(txtBeta, "cell 5 1,growx");
            txtBeta.setColumns(10);
        }
        {
            txtGamma = new JTextField();
            txtGamma.setText("gamma");
            contentPanel.add(txtGamma, "cell 6 1,growx");
            txtGamma.setColumns(10);
        }
        {
            JLabel lblSgHmSymbol = new JLabel("SG H-M symbol");
            contentPanel.add(lblSgHmSymbol, "cell 0 2,alignx trailing");
        }
        {
            txtSgsymbol = new JTextField();
            txtSgsymbol.setText("SGsymbol");
            contentPanel.add(txtSgsymbol, "cell 1 2 3 1,growx");
            txtSgsymbol.setColumns(10);
        }
        {
            JLabel lblSgItNum = new JLabel("SG IT num");
            contentPanel.add(lblSgItNum, "cell 4 2,alignx trailing");
        }
        {
            txtSgnum = new JTextField();
            txtSgnum.setText("SGnum");
            contentPanel.add(txtSgnum, "cell 5 2 2 1,growx");
            txtSgnum.setColumns(10);
        }
        {
            JLabel lblAtoms = new JLabel("Atoms");
            contentPanel.add(lblAtoms, "cell 0 3");
        }
        {
            scrollPane = new JScrollPane();
            contentPanel.add(scrollPane, "cell 0 4 7 1,grow");
            {
                table = new JTable();
                scrollPane.setViewportView(table);
                table.setModel(new DefaultTableModel(
                    new Object[][] {
                        {null, null, null, null, null, null, null},
                    },
                    new String[] {
                        "Label", "Atom Type", "x/a", "y/b", "z/c", "Occ", "U_iso"
                    }
                ) {
                    private static final long serialVersionUID = 1L;
                    Class<?>[] columnTypes = new Class[] {
                        String.class, String.class, Float.class, Float.class, Float.class, Float.class, Float.class
                    };
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_cancelButton_actionPerformed(e);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        
        fillValues(cf);
    }

    private void fillValues(Cif_file cf) {
        txtName.setText(cf.getNom());
        txtA.setText(String.valueOf(FileUtils.dfX_5.format(cf.getA())));
        txtB.setText(String.valueOf(FileUtils.dfX_5.format(cf.getB())));
        txtC.setText(String.valueOf(FileUtils.dfX_5.format(cf.getC())));
        txtAlpha.setText(String.valueOf(FileUtils.dfX_4.format(cf.getAl())));
        txtBeta.setText(String.valueOf(FileUtils.dfX_4.format(cf.getBe())));
        txtGamma.setText(String.valueOf(FileUtils.dfX_4.format(cf.getGa())));
        txtSgsymbol.setText(cf.getSgString());
        txtSgnum.setText(Integer.toString(cf.getSgNum()));
        //taula d'atoms
        DefaultTableModel tm = (DefaultTableModel) table.getModel();
        tm.setRowCount(0);
        for (int i=0;i<cf.getNAtoms();i++) {
            Object[] fila = new Object[7];
            fila[0]=cf.getAtomLabel(i);
            fila[1]=cf.getAtomType(i);
            fila[2]=cf.getAtomXcrys(i);
            fila[3]=cf.getAtomYcrys(i);
            fila[4]=cf.getAtomZcrys(i);
            fila[5]=cf.getAtomOcc(i);
            fila[6]=cf.getAtomDisp(i);
            tm.addRow(fila);
        }
        
    }
    
    public int getAtomTableNRows() {
        return table.getModel().getRowCount();
    }
    
    //TODO: S'hauria de posar millor el tema columnes... fer-ho no depenent de numero aqui
    public String getAtomLabel(int row) {
        try {
        return (String) table.getModel().getValueAt(row, 0);
        }catch(Exception ex) {
            FileUtils.InfoDialog(this, "error parsing atom information", "import error");
        }
        return null;
    }
    public String getAtomTipus(int row) {
        try {
            return (String) table.getModel().getValueAt(row, 1);    
        }catch(Exception ex) {
            FileUtils.InfoDialog(this, "error parsing atom information", "import error");
        }
        return null;

    }
    public float getAtomXcryst(int row) {
        try {
            return (Float) table.getModel().getValueAt(row, 2); 
        }catch(Exception ex) {
            FileUtils.InfoDialog(this, "error parsing atom information", "import error");
        }
        return -1;
    }
    public float getAtomYcryst(int row) {
        try {
            return (Float) table.getModel().getValueAt(row, 3); 
        }catch(Exception ex) {
            FileUtils.InfoDialog(this, "error parsing atom information", "import error");
        }
        return -1;
    }
    public float getAtomZcryst(int row) {
        try {
            return (Float) table.getModel().getValueAt(row, 4); 
        }catch(Exception ex) {
            FileUtils.InfoDialog(this, "error parsing atom information", "import error");
        }
        return -1;
    }
    public float getAtomOccup(int row) {
        try {
            return (Float) table.getModel().getValueAt(row, 5); 
        }catch(Exception ex) {
            FileUtils.InfoDialog(this, "error parsing atom information", "import error");
        }
        return -1;
    }
    public float getAtomDisp(int row) {
        try {
            return (Float) table.getModel().getValueAt(row, 6); 
        }catch(Exception ex) {
            FileUtils.InfoDialog(this, "error parsing atom information", "import error");
        }
        return -1;
    }
    
    public float getA() {
        try {
            return Float.parseFloat(txtA.getText());
        }catch(Exception ex){
            FileUtils.InfoDialog(this, "error parsing cell parameter A", "import error");
        }
        return -1;
    }
    
    public float getB() {
        try {
            return Float.parseFloat(txtB.getText());
        }catch(Exception ex){
            FileUtils.InfoDialog(this, "error parsing cell parameter B", "import error");
        }
        return -1;
    }
    
    public float getC() {
        try {
            return Float.parseFloat(txtC.getText());
        }catch(Exception ex){
            FileUtils.InfoDialog(this, "error parsing cell parameter C", "import error");
        }
        return -1;
    }
    
    public float getAlfa() {
        try {
            return Float.parseFloat(txtAlpha.getText());
        }catch(Exception ex){
            FileUtils.InfoDialog(this, "error parsing Alpha", "import error");
        }
        return -1;
    }
    
    public float getBeta() {
        try {
            return Float.parseFloat(txtBeta.getText());
        }catch(Exception ex){
            FileUtils.InfoDialog(this, "error parsing Beta", "import error");
        }
        return -1;
    }
    
    public float getGamma() {
        try {
            return Float.parseFloat(txtGamma.getText());
        }catch(Exception ex){
            FileUtils.InfoDialog(this, "error parsing Gamma", "import error");
        }
        return -1;
    }
    
    public String getName() {
        return txtName.getText().trim();
    }
    
    public String getSGsymbol() {
        return txtSgsymbol.getText().trim();
    }
    
    public int getSGnum() {
        try {
            return Integer.parseInt(txtSgnum.getText());
        }catch(Exception ex){
            FileUtils.InfoDialog(this, "error parsing SG number", "import error");
        }
        return -1;    
    }
    
    public boolean getOkclosed() {
        return okclosed;
    }
    
    protected void do_okButton_actionPerformed(ActionEvent e) {
        okclosed=true;
        this.dispose();
    }
    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        okclosed=false;
        this.dispose();
    }
}
