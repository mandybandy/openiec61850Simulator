package serverguiiec61850.gui;

import com.beanit.openiec61850.ClientSap;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServiceError;
import com.beanit.openiec61850.clientgui.BasicDataBind;
import com.beanit.openiec61850.clientgui.DataObjectTreeCellRenderer;
import com.beanit.openiec61850.clientgui.DataObjectTreeNode;
import com.beanit.openiec61850.clientgui.DataTreeNode;
import com.beanit.openiec61850.clientgui.ServerModelParser;
import com.beanit.openiec61850.clientgui.SettingsFrame;
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
import javax.swing.ImageIcon;
import serverguiiec61850.network.Client;

/**
 * erstellt eine Gui, mit der es möglich ist Werte anzusehen und zu schreiben
 * hier funktionieren Datasets, GetDataValue und SetDataValue
 *
 * @author Philipp Mandl
 */
public final class GuiTree extends JFrame implements ActionListener, TreeSelectionListener {

    //private Server server;
    private final ServerModel serverModel;
    private final JTree tree = new javax.swing.JTree(new DefaultMutableTreeNode("No server connected"));
    private final JPanel detailsPanel = new JPanel();
    private final GridBagLayout detailsLayout = new GridBagLayout();

    private final SettingsFrame settingsFrame = new SettingsFrame();

    private DataTreeNode selectedNode;

    /**
     * Gui für manuelle Werteänderung
     *
     * @param serverModel
     * @throws java.net.UnknownHostException
     */
    public GuiTree(ServerModel serverModel) throws UnknownHostException {
        super("change values");
        this.serverModel = serverModel;
        //Info: setze Icon //ToDo: Pfad ändern für -jar
        ImageIcon img = new ImageIcon(System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\iconSelecter.png");
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
     * Buttonkontrolle
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
     * wiederaufbau für Tree
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

                //Info: If gilt nicht für ST,MX,EX,Op(Op ist speziell zu betrachten)
                //if (selectedNode.writable()) {
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
                //}
            }
        } else {
            selectedNode = null;
        }

        validate();
    }

    /**
     * verbindet mit zuvor erstelltem Server als neuer Client
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

        clientSap.setTSelLocal(settingsFrame.getTselLocal());
        clientSap.setTSelRemote(settingsFrame.getTselRemote());

        try {
            Client.association.getAllDataValues();
        } catch (ServiceError e) {
            System.out.println("Service Error requesting model." + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Fatal IOException requesting model." + e.getMessage());
            return;
        }

        ServerModelParser parser = new ServerModelParser(serverModel);
        tree.setModel(new DefaultTreeModel(parser.getModelTree()));

        validate();

    }

    private void reload() {
        if (selectedNode.readable()) {
            try {
                selectedNode.reset(Client.association);
            } catch (ServiceError e) {
                System.out.println("ServiceError on reading" + e.getMessage());
                return;
            } catch (IOException e) {
                System.out.println("IOException on reading" + e.getMessage());
                return;
            }
            validate();
        }
    }

    private void write() {
        //INFO: funktion gilt nicht für ST, MX, EX und vlt OP
        //if (selectedNode.writable()) {
        try {
            selectedNode.writeValues(Client.association);
        } catch (ServiceError e) {
            System.out.println("ServiceError on writing" + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("IOException on writing" + e.getMessage());
            return;
        }
        validate();
        //}
    }

    /**
     * grafische Funktion*
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
     * grafische Funktion*
     */
    private void showDataDetails(DataTreeNode node, String pre, Counter y) {
        if (node.getData() != null) {
            BasicDataBind<?> data = node.getData();
            JLabel nameLabel = data.getNameLabel();
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
     * grafische Funktion*
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
     * schließt aktuelles Fenster*
     */
    private void exit() {
        setVisible(false);
        dispose();
    }
}