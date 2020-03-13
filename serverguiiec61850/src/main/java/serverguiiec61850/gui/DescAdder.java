/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverguiiec61850.gui;

import com.beanit.openiec61850.clientgui.DataObjectTreeNode;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import serverguiiec61850.files.ModifyXmlFile;

/**
 *
 * @author phili
 */
public class DescAdder {

    public static final ArrayList<String> DESC_LIST = new ArrayList<>();
    public static final ArrayList<String> NODE_LIST = new ArrayList<>();

    private ModifyXmlFile xml;
    private DefaultTreeModel tree;

    public DescAdder(DefaultTreeModel tree, ModifyXmlFile xml) throws ParserConfigurationException, SAXException, IOException {
        this.xml = xml;
        this.tree = tree;
        DataObjectTreeNode node = ((DataObjectTreeNode) this.tree.getRoot());

        modEl(node);
    }

    private void modEl(DataObjectTreeNode node) throws ParserConfigurationException, SAXException, IOException {

        if (node.getAllowsChildren()) {
            for (int i = 0; i < node.getChildCount(); i++) {
                modEl((DataObjectTreeNode) node.getChildAt(i));
            }
        }
        String nodedesc = "";
        //if nodename exists in list take list desc
        if (NODE_LIST.contains((String) node.getUserObject())) {
            nodedesc = DESC_LIST.get(NODE_LIST.indexOf((String) node.getUserObject()));
        } else {
            nodedesc = xml.getDesc((String) node.getUserObject());
            NODE_LIST.add((String) node.getUserObject());
            DESC_LIST.add(NODE_LIST.indexOf((String) node.getUserObject()), nodedesc);
        }
        if ((!"".equals(nodedesc))) {
            Object obj = (String) node.getUserObject() + "   " + nodedesc;
            node.setUserObject(obj);
        }
    }

    public DefaultTreeModel getTree() {
        return this.tree;
    }

}
