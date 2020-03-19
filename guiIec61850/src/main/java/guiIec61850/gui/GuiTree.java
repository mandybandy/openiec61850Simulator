/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.GuiTree.java
 * @author Philipp Mandl
 */
package guiIec61850.gui;

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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.xml.sax.SAXException;
import guiIec61850.files.ModifyXmlFile;
import guiIec61850.files.NodeDescription;
import static guiIec61850.gui.Gui.ipTB;
import static guiIec61850.gui.Gui.portTB;
import static guiIec61850.network.Client.association;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.SOUTHEAST;
import static java.awt.GridBagConstraints.SOUTHWEST;
import static java.lang.Integer.parseInt;
import static java.lang.System.getProperty;
import static java.net.InetAddress.getByName;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.ToolTipManager.sharedInstance;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * creates a new client which is able to change all values manually
 */
public final class GuiTree extends JFrame implements ActionListener, TreeSelectionListener {

    private static ServerModel serverModel;

    /**
     * nodetree of GuiTree
     */
    public static JTree tree = new javax.swing.JTree(new DefaultMutableTreeNode("connecting..."));
    private final JPanel detailsPanel = new JPanel();
    private final GridBagLayout detailsLayout = new GridBagLayout();

    private static final Logger LOGGER_GUITREE = getLogger(Simulator.class);

    private final ModifyXmlFile xml;

    private DataTreeNode selectedNode;
    private final String ied;

    /**
     *controls GuiTree window
     */
    public boolean guiTreeEnabled;

    /**
     * gui change values
     *
     * @param xml ModifyXmlFile
     * @param ied string ied 
     * @throws java.net.UnknownHostException unknown host 
     * @throws com.beanit.openiec61850.ServiceError service error
     */
    public GuiTree(ModifyXmlFile xml, String ied) throws UnknownHostException, ServiceError, IOException {
        super("change values");
        this.guiTreeEnabled = true;
        this.xml = xml;
        this.ied = ied;
        //Info: setze Icon 
        ImageIcon img = new ImageIcon(getProperty("user.dir") + "\\files\\icons\\iconSelecter.png");
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

        detailsPanel.setLayout(detailsLayout);
        detailsPanel.setAlignmentY(TOP_ALIGNMENT);
        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsPanel.setMaximumSize(detailsScrollPane.getSize());
        detailsScrollPane.setMinimumSize(new Dimension(0, 0));
        detailsScrollPane.setVisible(true);
        GridBagConstraints detailsScrollPaneConstraint = new GridBagConstraints();
        detailsScrollPaneConstraint.fill = BOTH;
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
     * controls button events
     *
     * @param e button event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("reload".equalsIgnoreCase(e.getActionCommand())) {
            reload();
        } else if ("write".equalsIgnoreCase(e.getActionCommand())) {
            write();
        }
    }

    /**
     * rebuilds tree
     *
     * @param e TreeSelectionEvent
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
                gbc.fill = BOTH;
                gbc.gridx = 0;
                gbc.gridy = RELATIVE;
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
                gbc.fill = NONE;
                gbc.gridx = 0;
                gbc.gridy = RELATIVE;
                gbc.gridwidth = 2;
                gbc.gridheight = 1;
                gbc.weightx = 0;
                gbc.weighty = 0;
                gbc.anchor = SOUTHWEST;
                gbc.insets = new Insets(0, 5, 5, 0);
                detailsLayout.setConstraints(button, gbc);
                detailsPanel.add(button);
                if (selectedNode.getChildCount() == 0) {
                    button = new JButton("Write values");
                    button.addActionListener(this);
                    button.setActionCommand("write");
                    gbc = new GridBagConstraints();
                    gbc.fill = NONE;
                    gbc.gridx = 2;
                    gbc.gridy = RELATIVE;
                    gbc.gridwidth = 1;
                    gbc.gridheight = 1;
                    gbc.weightx = 0;
                    gbc.weighty = 0;
                    gbc.anchor = SOUTHEAST;
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
     * @throws java.net.UnknownHostException unknown host
     */
    private void connect() throws UnknownHostException {

        ClientSap clientSap = new ClientSap();

        InetAddress address = null;
        address = getByName(ipTB.getText());

        int remotePort = parseInt(portTB.getText());
        if (remotePort < 1 || remotePort > 0xFFFF) {
            throw new NumberFormatException("port must be in range [1, 65535]");
        }

        try {
            serverModel = association.retrieveModel();
            association.getAllDataValues();
        } catch (ServiceError e) {
            LOGGER_GUITREE.error("Service Error requesting model.", e);
            return;
        } catch (IOException e) {
            LOGGER_GUITREE.error("Fatal IOException requesting model.", e);
            return;
        }

        ServerModelParser parser = new ServerModelParser(serverModel);
        tree.setModel(new DefaultTreeModel(parser.getModelTree()));

        Thread describtionThread = new Thread() {
            public void run() {
                try {
                    NodeDescription adder = new NodeDescription((DefaultTreeModel) tree.getModel(), xml, ied);
                    LOGGER_GUITREE.info("got server descriptions");
                    tree.treeDidChange();
                } catch (IOException | ParserConfigurationException | SAXException e) {
                    LOGGER_GUITREE.error("error", e);
                }
            }
        };
        describtionThread.start();

        validate();

        LOGGER_GUITREE.debug(
                "values can now be changed by gui");
    }

    private void reload() {
        if (selectedNode.readable()) {
            try {
                selectedNode.reset(association);
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
            selectedNode.writeValues(association);
            showMessageDialog(this, "wrote data sucessfully", "sucess", INFORMATION_MESSAGE);
        } catch (ServiceError e) {
            LOGGER_GUITREE.error("ServiceError on write", e);
            return;
        } catch (IOException e) {
            LOGGER_GUITREE.error("IOException on write", e);
            return;
        } catch (NullPointerException e) {
            LOGGER_GUITREE.error("invalid value, could not write data");
            return;
        }
        validate();

        LOGGER_GUITREE.info("wrote sucessfully");
        reload();
    }

    /**
     * shows data details
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
     */
    private void addDetailsComponent(Component c, int x, int y, int width, int height, double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = HORIZONTAL;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.anchor = NORTH;
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
        this.guiTreeEnabled = true;
    }
    
    /**
     *sets boolean that guitree is opened
     * @param ena state
     */
    public void setEna(boolean ena){
        this.guiTreeEnabled=ena;
    }
}
