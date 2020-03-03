package serverguiiec61850.gui;

import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.clientgui.DataObjectTreeCellRenderer;
import com.beanit.openiec61850.clientgui.DataObjectTreeNode;
import com.beanit.openiec61850.clientgui.ServerModelParser;
import com.beanit.openiec61850.clientgui.SettingsFrame;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.UnknownHostException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Erstellt eine GUI zum auswählen der FCs und Reference
 *
 * @author Philipp Mandl
 */
public final class RefSelect extends JFrame implements TreeSelectionListener {

    private static DataObjectTreeNode selectedNode;
    private static String fc = "";
    private static String reference = "";

    private final JTree tree = new javax.swing.JTree(new DefaultMutableTreeNode("No server connected"));
    private final JPanel detailsPanel = new JPanel();
    private final GridBagLayout detailsLayout = new GridBagLayout();

    private final SettingsFrame settingsFrame = new SettingsFrame();

    /**
     * erstellt Fenster, wo die Referenz und die FC gewählt werden kann ähnlich
     * wie GuiTree
     *
     * @throws java.net.UnknownHostException
     */
    public RefSelect(ServerModel serverModel) throws UnknownHostException {
        super("select datanode");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exit();
            }
        });


        //ToDo: icon Pfad ändern für -jar
        ImageIcon img = new ImageIcon(System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\nodeicon.png");
        this.setIconImage(img.getImage());

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        GridBagConstraints topPanelConstraint = new GridBagConstraints();
        topPanelConstraint.fill = GridBagConstraints.HORIZONTAL;
        topPanelConstraint.gridwidth = GridBagConstraints.REMAINDER;
        topPanelConstraint.gridx = 0;
        topPanelConstraint.gridy = 0;
        topPanelConstraint.insets = new Insets(5, 5, 5, 5);
        topPanelConstraint.anchor = GridBagConstraints.NORTH;
        gbl.setConstraints(topPanel, topPanelConstraint);
        add(topPanel);

        ToolTipManager.sharedInstance().registerComponent(tree);

        tree.setCellRenderer(new DataObjectTreeCellRenderer());
        tree.setMinimumSize(new Dimension(100, 0));
        tree.addTreeSelectionListener(this);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setMinimumSize(new Dimension(100, 0));
        treeScrollPane.setVisible(true);

        GridBagConstraints treeScrollPaneConstraint = new GridBagConstraints();
        treeScrollPaneConstraint.fill = GridBagConstraints.BOTH;
        treeScrollPaneConstraint.gridx = 0;
        treeScrollPaneConstraint.gridy = 1;
        treeScrollPaneConstraint.weightx = 0.2;
        treeScrollPaneConstraint.weighty = 1;
        treeScrollPaneConstraint.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(treeScrollPane, treeScrollPaneConstraint);
        add(treeScrollPane);
        JButton confirm = new JButton("confirm");
        add(confirm);
        confirm.addActionListener(this::confirmBTNpressed);

        // gleich wie GuiTree
        setSize(700, 500);
        setMinimumSize(new Dimension(420, 0));
        setVisible(true);

        ServerModelParser parser = new ServerModelParser(serverModel);
        tree.setModel(new DefaultTreeModel(parser.getModelTree()));

    }

    /**
     *
     * @param e
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        detailsPanel.removeAll();
        detailsPanel.repaint();
        if (e.getNewLeadSelectionPath() != null) {
            selectedNode = (DataObjectTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
            validate();
        }
    }

    /**
     *
     * @param evt
     */
    public void confirmBTNpressed(java.awt.event.ActionEvent evt) {
        String[] fcs = {"ST", "MX", "SP", "SV", "CF", "DC", "SG", "SE", "SR", "OR", "BL", "EX", "CO", "US", "MS", "RP", "BR", "LG", "ALL", "NONE"};
        List<String> fcList = Arrays.asList(fcs);
        //if (selectedNode.writable()) {

        for (int fcCount = 0; fcCount < fcList.size(); fcCount++) {
            fc = RefSelect.selectedNode.getNode().getBasicDataAttributes().get(0).getFc().toString();
            reference = selectedNode.getNode().getReference().toString();
            if (reference != null) {
                if (fc.equals(fcList.get(fcCount))) {
                    validate();
                    if (("MX".equals(fc)) || ("ST".equals(fc))) {
                        JOptionPane.showMessageDialog(this, "you want to access a ST,MX constraint", "write error", JOptionPane.WARNING_MESSAGE);
                    }

                    Gui.referenceTB.setText(reference);
                    Gui.createDatasetRefTB.setText(reference);
                    Gui.createDatasetFcCB.setSelectedItem(fc);
                    Gui.simulateRampReferenceTB.setText(reference);
                    Gui.simulateRampFcCB.setSelectedItem(fc);
                    Gui.simulatePulsReferenceTB.setText(reference);
                    Gui.simulatePulsFcCB.setSelectedItem(fc);
                    exit();
                }
            } else {
                JOptionPane.showMessageDialog(this, "no reference selected", "no reference", JOptionPane.WARNING_MESSAGE);
            }
        }

//        } else {
//            JOptionPane.showMessageDialog(this, "ST and MX are not writeable", "write error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    /**
     * schließt Fenster
     *
     */
    public void exit() {
        setVisible(false);
        dispose();
    }
}
