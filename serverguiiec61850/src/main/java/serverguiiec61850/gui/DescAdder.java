/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverguiiec61850.gui;

import com.beanit.openiec61850.clientgui.DataObjectTreeNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import serverguiiec61850.files.ModifyXmlFile;

/**
 *
 * @author phili
 */
public class DescAdder {

    private static final ArrayList<String> DESC_LIST = new ArrayList<>();
    private static final ArrayList<String> NODE_LIST = new ArrayList<>();

    private ModifyXmlFile xml;
    private DefaultTreeModel localTree;

    /**
     *
     * @param localtree
     * @param xml
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public DescAdder(DefaultTreeModel localtree, ModifyXmlFile xml) throws ParserConfigurationException, SAXException, IOException {
        this.xml = xml;
        this.localTree = localtree;

        try {
            DataObjectTreeNode node = ((DataObjectTreeNode) this.localTree.getRoot());
            modEl(node);
            GuiTree.tree.setModel(this.localTree);
            run = false;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DescAdder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(DescAdder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DescAdder.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    /**
     *
     * @return
     */
    public DefaultTreeModel getTree() {
        return this.localTree;
    }

    private boolean run = true;

}
