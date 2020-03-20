/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.RefSelect.java
 * @author Philipp Mandl
 */
package guiIec61850.gui;

import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.clientgui.DataObjectTreeCellRenderer;
import com.beanit.openiec61850.clientgui.DataObjectTreeNode;
import com.beanit.openiec61850.clientgui.ServerModelParser;
import guiIec61850.files.NodeDescription;
import static guiIec61850.gui.Gui.referenceTB;
import static guiIec61850.gui.Gui.simulatePulsFcCB;
import static guiIec61850.gui.Gui.simulatePulsReferenceTB;
import static guiIec61850.gui.Gui.simulateRampFcCB;
import static guiIec61850.gui.Gui.simulateRampReferenceTB;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.REMAINDER;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.UnknownHostException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import java.util.List;
import static javax.swing.BoxLayout.X_AXIS;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import static javax.swing.ToolTipManager.sharedInstance;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * creates a gui for selecting reference and fc
 */
public final class RefSelect extends JFrame implements TreeSelectionListener {

    private static DataObjectTreeNode selectedNode;
    private static String fc = "";
    private static String reference = "";

    private final JTree tree = new javax.swing.JTree(new DefaultMutableTreeNode("connecting..."));
    private final JPanel detailsPanel = new JPanel();

    private final boolean noFc;

    /**
     * similar to guitree, but only for selecting fc and reference
     *
     * @param serverModel serverModel
     * @param noFc fcavavaible
     * @throws java.net.UnknownHostException unknown host
     */
    public RefSelect(ServerModel serverModel, boolean noFc) throws UnknownHostException {
        super("select datanode");
        this.noFc = noFc;
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exit();
            }
        });

        ImageIcon img = new ImageIcon(getProperty("user.dir") + "\\files\\icons\\nodeicon.png");
        this.setIconImage(img.getImage());

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, X_AXIS));

        GridBagConstraints topPanelConstraint = new GridBagConstraints();
        topPanelConstraint.fill = HORIZONTAL;
        topPanelConstraint.gridwidth = REMAINDER;
        topPanelConstraint.gridx = 0;
        topPanelConstraint.gridy = 0;
        topPanelConstraint.insets = new Insets(5, 5, 5, 5);
        topPanelConstraint.anchor = NORTH;
        gbl.setConstraints(topPanel, topPanelConstraint);
        add(topPanel);

        sharedInstance().registerComponent(tree);

        tree.setCellRenderer(new DataObjectTreeCellRenderer());
        tree.setMinimumSize(new Dimension(100, 0));
        tree.addTreeSelectionListener(this);
        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setMinimumSize(new Dimension(100, 0));
        treeScrollPane.setVisible(true);

        GridBagConstraints treeScrollPaneConstraint = new GridBagConstraints();
        treeScrollPaneConstraint.fill = BOTH;
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

        Thread describtionThread = new Thread() {
            public void run() {
                try {
                    NodeDescription adder = new NodeDescription((DefaultTreeModel) tree.getModel(), null, null);
                }
                catch (IOException | ParserConfigurationException | SAXException e) {
                }
            }
        };
        describtionThread.start();

    }

    /**
     * runs by a tree event
     *
     * @param e TreeSelectionEvent
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

    private void confirmBTNpressed(java.awt.event.ActionEvent evt) {
        String[] fcs = {"ST", "MX", "SP", "SV", "CF", "DC", "SG", "SE", "SR", "OR", "BL", "EX", "CO", "US", "MS", "RP", "BR", "LG", "ALL", "NONE"};
        List<String> fcList = asList(fcs);

        for (int fcCount = 0; fcCount < fcList.size(); fcCount++) {
            try {
                fc = selectedNode.getNode().getBasicDataAttributes().get(0).getFc().toString();
            }
            catch (Exception e) {
                fc = null;
            }
            try {
                reference = selectedNode.getNode().getReference().toString();
            }
            catch (Exception e) {
                reference = null;
            }
            if (reference != null) {
                if (fc.equals(fcList.get(fcCount))) {
                    validate();
                    if (selectedNode.getNode().getChildren() == null) {
                        if (("MX".equals(fc)) || ("ST".equals(fc))) {
                            showMessageDialog(this, "you want to access a ST,MX constraint", "access warning", WARNING_MESSAGE);
                        }
                    }
                    referenceTB.setText(reference);
                    simulateRampReferenceTB.setText(reference);
                    simulateRampFcCB.setSelectedItem(fc);
                    simulatePulsReferenceTB.setText(reference);
                    simulatePulsFcCB.setSelectedItem(fc);
                    exit();
                } else if (noFc) {
                    referenceTB.setText(reference);
                    simulateRampReferenceTB.setText(reference);
                    simulatePulsReferenceTB.setText(reference);
                    exit();
                }

            } else {
                showMessageDialog(this, "no reference selected", "no reference", WARNING_MESSAGE);
                return;
            }
        }

    }

    /**
     * close select window
     */
    public void exit() {
        setVisible(false);
        dispose();
    }
}
