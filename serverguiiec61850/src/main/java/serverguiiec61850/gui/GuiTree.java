/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.GuiTree.java
 * @author Philipp Mandl
 */
package serverguiiec61850.gui;

import com.beanit.openiec61850.ClientSap;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServiceError;
import com.beanit.openiec61850.clientgui.BasicDataBind;
import com.beanit.openiec61850.clientgui.DataObjectTreeCellRenderer;
import com.beanit.openiec61850.clientgui.DataObjectTreeNode;
import com.beanit.openiec61850.clientgui.DataTreeNode;
import com.beanit.openiec61850.clientgui.ServerModelParser;
import com.beanit.openiec61850.clientgui.util.Counter;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serverguiiec61850.network.Client;
import serverguiiec61850.files.ModifyXmlFile;

/**
 * creates a new client which is able to change all values manually
 */
public final class GuiTree extends JFrame implements ActionListener, TreeSelectionListener {

    private static ServerModel serverModel;
    private final JTree tree = new javax.swing.JTree(new DefaultMutableTreeNode("No server connected"));
    private final JPanel detailsPanel = new JPanel();
    private final GridBagLayout detailsLayout = new GridBagLayout();

    private static final Logger LOGGER_GUITREE = LoggerFactory.getLogger(Simulator.class);

    private DataTreeNode selectedNode;
    private ModifyXmlFile xml;

    /**
     * Gui change values
     *
     * @param xml
     * @throws java.net.UnknownHostException
     * @throws com.beanit.openiec61850.ServiceError
     */
    public GuiTree(ModifyXmlFile xml) throws UnknownHostException, ServiceError, IOException {
        super("change values");
        this.xml = xml;
        //Info: setze Icon 
        ImageIcon img = new ImageIcon(System.getProperty("user.dir") + "\\files\\iconSelecter.png");
        this.setIconImage(img.getImage());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exit();
            }
        });
//Info: erstelle GUI mittels raster(muss statisch sein, denn sonst kann nichts wieder erbaut werden)...
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

        detailsPanel.setLayout(detailsLayout);
        detailsPanel.setAlignmentY(TOP_ALIGNMENT);
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsPanel.setMaximumSize(detailsScrollPane.getSize());
        detailsScrollPane.setMinimumSize(new Dimension(0, 0));
        detailsScrollPane.setPreferredSize(new Dimension(200, 0));
        detailsScrollPane.setVisible(true);
        GridBagConstraints detailsScrollPaneConstraint = new GridBagConstraints();
        detailsScrollPaneConstraint.fill = GridBagConstraints.BOTH;
        detailsScrollPaneConstraint.gridx = 1;
        detailsScrollPaneConstraint.gridy = 1;
        detailsScrollPaneConstraint.weightx = 0.8;
        detailsScrollPaneConstraint.weighty = 1;
        detailsScrollPaneConstraint.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(detailsScrollPane, detailsScrollPaneConstraint);
        add(detailsScrollPane);

        // Info: Fix size
        setSize(700, 500);
        setMinimumSize(new Dimension(420, 400));
        setVisible(true);

        connect();

    }

    /**
     * controls buttonevents
     *
     * @param arg0
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        if ("reload".equalsIgnoreCase(arg0.getActionCommand())) {
            reload();
        } else if ("write".equalsIgnoreCase(arg0.getActionCommand())) {
            write();
        }
    }

    /**
     * rebuilds tree
     *
     * @param e
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        detailsPanel.removeAll();
        detailsPanel.repaint();
        if (e.getNewLeadSelectionPath() != null) {
            selectedNode = (DataTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
            if (selectedNode.readable()) {
                showDataDetails(selectedNode, new Counter());

                JPanel filler = new JPanel();
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridx = 0;
                gbc.gridy = GridBagConstraints.RELATIVE;
                gbc.gridwidth = 3;
                gbc.gridheight = 1;
                gbc.weightx = 0;
                gbc.weighty = 1;
                detailsLayout.setConstraints(filler, gbc);
                detailsPanel.add(filler);

                JButton button = new JButton("Reload values");
                button.addActionListener(this);
                button.setActionCommand("reload");
                gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.NONE;
                gbc.gridx = 0;
                gbc.gridy = GridBagConstraints.RELATIVE;
                gbc.gridwidth = 2;
                gbc.gridheight = 1;
                gbc.weightx = 0;
                gbc.weighty = 0;
                gbc.anchor = GridBagConstraints.SOUTHWEST;
                gbc.insets = new Insets(0, 5, 5, 0);
                detailsLayout.setConstraints(button, gbc);
                detailsPanel.add(button);
                if (selectedNode.getChildCount() == 0) {
                    button = new JButton("Write values");
                    button.addActionListener(this);
                    button.setActionCommand("write");
                    gbc = new GridBagConstraints();
                    gbc.fill = GridBagConstraints.NONE;
                    gbc.gridx = 2;
                    gbc.gridy = GridBagConstraints.RELATIVE;
                    gbc.gridwidth = 1;
                    gbc.gridheight = 1;
                    gbc.weightx = 0;
                    gbc.weighty = 0;
                    gbc.anchor = GridBagConstraints.SOUTHEAST;
                    gbc.insets = new Insets(0, 0, 5, 5);
                    detailsLayout.setConstraints(button, gbc);
                    detailsPanel.add(button);
                }
            }
        }

        validate();
    }

    /**
     * connects new client
     *
     * @throws java.net.UnknownHostException
     */
    private void connect() throws UnknownHostException {

        ClientSap clientSap = new ClientSap();

        InetAddress address = null;
        address = InetAddress.getByName(Gui.ipTB.getText());

        int remotePort = Integer.parseInt(Gui.portTB.getText());
        if (remotePort < 1 || remotePort > 0xFFFF) {
            throw new NumberFormatException("port must be in range [1, 65535]");
        }

        try {
            serverModel = Client.association.retrieveModel();
            Client.association.getAllDataValues();
        } catch (ServiceError e) {
            LOGGER_GUITREE.error("Service Error requesting model.", e);
            return;
        } catch (IOException e) {
            LOGGER_GUITREE.error("Fatal IOException requesting model.", e);
            return;
        }

        ServerModelParser parser = new ServerModelParser(serverModel);
        tree.setModel(new DefaultTreeModel(parser.getModelTree()));

        //tree.getComponent(i).setName(tree.getComponent(i).getName()+xml.getDesc(tree.getComponent(i).getName()));
        try {
            DataObjectTreeNode root = ((DataObjectTreeNode) ((DefaultTreeModel) tree.getModel()).getRoot());
//            if (!root.isLeaf()) {
//                for (int i = 0; i < root.getChildCount(); i++) {
//                    DataObjectTreeNode node1 = (DataObjectTreeNode) root.getChildAt(i);
//                    if (!node1.isLeaf()) {
//                        for (int u = 0; u < node1.getChildCount(); u++) {
//                            DataObjectTreeNode node2 = (DataObjectTreeNode) node1.getChildAt(u);
//                            if (!node2.isLeaf()) {
//                                for (int z = 0; z < node2.getChildCount(); z++) {
//                                    DataObjectTreeNode node3 = (DataObjectTreeNode) node2.getChildAt(z);
//                                    if (!node3.isLeaf()) {
//                                        for (int t = 0; t < node3.getChildCount(); t++) {
//                                            DataObjectTreeNode node4 = (DataObjectTreeNode) node3.getChildAt(t);
//                                            if (!node4.isLeaf()) {
//                                                for (int r = 0; r < node4.getChildCount(); r++) {
//                                                    DataObjectTreeNode node5 = (DataObjectTreeNode) node4.getChildAt(r);
//                                                    if (!node5.isLeaf()) {
//                                                        for (int e = 0; e < node5.getChildCount(); e++) {
//                                                            DataObjectTreeNode node6 = (DataObjectTreeNode) node5.getChildAt(e);
//                                                            if (!node6.isLeaf()) {
//                                                                for (int w = 0; w < node6.getChildCount(); w++) {
//                                                                    DataObjectTreeNode node7 = (DataObjectTreeNode) node6.getChildAt(w);
//                                                                    if (!node7.isLeaf()) {
//                                                                        for (int a = 0; a < node7.getChildCount(); a++) {
//                                                                            DataObjectTreeNode node8 = (DataObjectTreeNode) node7.getChildAt(a);
//                                                                            if (!node8.isLeaf()) {
//
//                                                                            } else {
//                                                                                String nodedesc = xml.getDesc((String) node8.getUserObject());
//                                                                                if ((!"".equals(nodedesc))) {
//                                                                                    Object obj = (String) node8.getUserObject() + nodedesc;
//                                                                                    node8.setUserObject(obj);
//                                                                                }
//                                                                            }
//                                                                        }
//                                                                    } else {
//                                                                        String nodedesc = xml.getDesc((String) node7.getUserObject());
//                                                                        if ((!"".equals(nodedesc))) {
//                                                                            Object obj = (String) node7.getUserObject() + nodedesc;
//                                                                            node7.setUserObject(obj);
//                                                                        }
//                                                                    }
//                                                                }
//                                                            } else {
//                                                                String nodedesc = xml.getDesc((String) node6.getUserObject());
//                                                                if ((!"".equals(nodedesc))) {
//                                                                    Object obj = (String) node6.getUserObject() + nodedesc;
//                                                                    node6.setUserObject(obj);
//                                                                }
//                                                            }
//                                                        }
//                                                    } else {
//                                                        String nodedesc = xml.getDesc((String) node5.getUserObject());
//                                                        if ((!"".equals(nodedesc))) {
//                                                            Object obj = (String) node5.getUserObject() + nodedesc;
//                                                            node5.setUserObject(obj);
//                                                        }
//
//                                                    }
//                                                }
//                                            } else {
//                                                String nodedesc = xml.getDesc((String) node4.getUserObject());
//                                                if ((!"".equals(nodedesc))) {
//                                                    Object obj = (String) node4.getUserObject() + nodedesc;
//                                                    node4.setUserObject(obj);
//                                                }
//                                            }
//                                        }
//
//                                    } else {
//                                        String nodedesc = xml.getDesc((String) node3.getUserObject());
//                                        if ((!"".equals(nodedesc))) {
//                                            Object obj = (String) node3.getUserObject() + nodedesc;
//                                            node3.setUserObject(obj);
//                                        }
//                                    }
//                                }
//                            } else {
//                                String nodedesc = xml.getDesc((String) node2.getUserObject());
//                                if ((!"".equals(nodedesc))) {
//                                    Object obj = (String) node2.getUserObject() + nodedesc;
//                                    node2.setUserObject(obj);
//                                }
//                            }
//                        }
//                    } else {
//                        String nodedesc = xml.getDesc((String) node1.getUserObject());
//                        if ((!"".equals(nodedesc))) {
//                            Object obj = (String) node1.getUserObject() + nodedesc;
//                            node1.setUserObject(obj);
//                        }
//                    }
//                }
//            }

            ArrayList<String> nodes = new ArrayList<>();

            for (int i = 0; i < root.getChildCount(); i++) {
                DataObjectTreeNode node1 = (DataObjectTreeNode) root.getChildAt(i);
                for (int u = 0; u < node1.getChildCount(); u++) {
                    DataObjectTreeNode node2 = (DataObjectTreeNode) node1.getChildAt(u);
                    for (int z = 0; z < node2.getChildCount(); z++) {
                        DataObjectTreeNode node3 = (DataObjectTreeNode) node2.getChildAt(z);
                        for (int t = 0; t < node3.getChildCount(); t++) {
                            DataObjectTreeNode node4 = (DataObjectTreeNode) node3.getChildAt(t);
                            for (int r = 0; r < node4.getChildCount(); r++) {
                                DataObjectTreeNode node5 = (DataObjectTreeNode) node4.getChildAt(r);
                                for (int e = 0; e < node5.getChildCount(); e++) {
                                    DataObjectTreeNode node6 = (DataObjectTreeNode) node5.getChildAt(e);
                                    for (int w = 0; w < node6.getChildCount(); w++) {
                                        DataObjectTreeNode node7 = (DataObjectTreeNode) node6.getChildAt(w);
                                        if (!node7.isLeaf()) {
                                            for (int a = 0; a < node7.getChildCount(); a++) {
                                                DataObjectTreeNode node8 = (DataObjectTreeNode) node7.getChildAt(a);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < nodes.gsize(); i++) {
                DataTreeNode node = (DataTreeNode)root;
                node=node.
                String nodedesc = xml.getDesc((String) node.getUserObject());

                if ((!"".equals(nodedesc))) {
                    Object obj = (String) nodes.get(i) + nodedesc;
                    node.setUserObject(obj);
                }
            }

        } catch (Exception e) {
            LOGGER_GUITREE.error("error", e);
        }

        tree.setModel(tree.getModel());

        validate();

        LOGGER_GUITREE.debug(
                "values can now be changed by gui");

    }

    private void reload() {
        if (selectedNode.readable()) {
            try {
                selectedNode.reset(Client.association);
            } catch (ServiceError e) {
                LOGGER_GUITREE.error("ServiceError on reading", e);
                return;
            } catch (IOException e) {
                LOGGER_GUITREE.error("IOException on reading", e);
                return;
            }
            validate();
        }
    }

    private void write() {
        try {
            selectedNode.writeValues(Client.association);
            JOptionPane.showMessageDialog(this, "wrote data sucessfully", "sucess", JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceError e) {
            LOGGER_GUITREE.error("ServiceError on write", e);
            return;
        } catch (IOException e) {
            LOGGER_GUITREE.error("IOException on write", e);
            return;
        } catch (NullPointerException e) {
            LOGGER_GUITREE.error("invalid value, could not write data", e);
        }
        validate();

        LOGGER_GUITREE.info("wrote sucessfully");
        reload();
    }

    /**
     * shows data details
     *
     */
    private void showDataDetails(DataTreeNode node, Counter y) {
        if (node.getData() != null) {
            BasicDataBind<?> data = node.getData();
            JLabel nameLabel = data.getNameLabel();
            nameLabel.setText(nameLabel.getText() + ": ");
            addDetailsComponent(nameLabel, 0, y.getValue(), 1, 1, 0, 0);
            addDetailsComponent(data.getValueField(), 1, y.getValue(), 2, 1, 1, 0);
            y.increment();
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                y.increment();
                DataObjectTreeNode childNode = (DataObjectTreeNode) node.getChildAt(i);
                showDataDetails(childNode, childNode.toString(), y);
            }
        }
    }

    /**
     * shows details of node
     *
     */
    private void showDataDetails(DataTreeNode node, String pre, Counter y) {
        if (node.getData() != null) {
            BasicDataBind<?> data = node.getData();
            JLabel nameLabel = data.getNameLabel();
            //String desc = xml.getDesc(node.toString());
            nameLabel.setText(pre + ": ");
            addDetailsComponent(nameLabel, 0, y.getValue(), 1, 1, 0, 0);
            addDetailsComponent(data.getValueField(), 1, y.getValue(), 2, 1, 1, 0);
            y.increment();
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                y.increment();
                DataObjectTreeNode childNode = (DataObjectTreeNode) node.getChildAt(i);
                showDataDetails(childNode, pre + "." + childNode.toString(), y);
                detailsPanel.add(new JSeparator());
                addDetailsComponent(new JSeparator(), 0, y.getValue(), 3, 1, 1, 0);
            }
        }
    }

    /**
     * adds details
     *
     */
    private void addDetailsComponent(Component c, int x, int y, int width, int height, double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(3, 3, 3, 3);
        detailsLayout.setConstraints(c, gbc);
        detailsPanel.add(c);
    }

    /**
     * exit guitree
     */
    private void exit() {
        setVisible(false);
        dispose();
    }
}
