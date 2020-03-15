/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.SetIedValue.java
 * @author Philipp Mandl
 */
package serverguiiec61850.gui;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import serverguiiec61850.network.Server;

/**
 * sets the values from scl, getvalues not implemented
 *
 * @author Philipp
 */
public class SetIedValue {

    private String ied;
    private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder docBuilder;
    private String filepath;
    private Document docdesc;
    private ServerModel localTree;
    private Server server;

    /**
     *
     * @param localtree
     * @param ied
     * @param server
     */
    public SetIedValue(ServerModel localtree, String ied, Server server) {
        try {
            this.localTree = localtree;
            this.ied = ied;
            this.docBuilder = docFactory.newDocumentBuilder();
            this.filepath = System.getProperty("user.dir") + "\\files\\icd\\" + this.ied;
            this.docdesc = docBuilder.parse(filepath);
            this.server = server;

            NodeList sclList = docdesc.getElementsByTagName("SCL");
            List<BasicDataAttribute> bdaList = this.localTree.getBasicDataAttributes();
            List<BasicDataAttribute> bdas = new ArrayList<>();
            for (int i = 0; i < bdaList.size(); i++) {
                BasicDataAttribute bda = bdaList.get(i);
                bda = bdaList.get(i);
                //TODO: getValue
//                this.server.setBdaValue(bda, "");
//                bdas.add(bda);
            }
            this.server.serverSap.setValues(bdas);

        } catch (SAXException e) {
            Logger.getLogger(SetIedValue.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(SetIedValue.class.getName()).log(Level.SEVERE, null, e);
        } catch (ParserConfigurationException e) {
            Logger.getLogger(SetIedValue.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
