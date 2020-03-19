/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.SetIedValue.java
 * @author Philipp Mandl
 */
package guiIec61850.gui;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.ServerModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import guiIec61850.network.Server;
import static java.lang.System.getProperty;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;

/**
 * sets the values from scl, getvalues not implemented
 */
public class SetIedValue {

    private final String ied;
    private final DocumentBuilderFactory docFactory = newInstance();
    private final DocumentBuilder docBuilder;
    private final String filepath;
    private final Document docdesc;
    private final ServerModel localTree;
    private final Server server;

    /**
     *sets values
     * @param localtree server model
     * @param ied string ied
     * @param server server
     * @throws org.xml.sax.SAXException sax error
     * @throws javax.xml.parsers.ParserConfigurationException parser error
     * @throws java.io.IOException io error
     */
    public SetIedValue(ServerModel localtree, String ied, Server server) throws SAXException, ParserConfigurationException, IOException {
        this.localTree = localtree;
        this.ied = ied;
        this.docBuilder = docFactory.newDocumentBuilder();
        this.filepath = getProperty("user.dir") + "\\files\\icd\\" + this.ied;
        this.docdesc = docBuilder.parse(filepath);
        this.server = server;

        NodeList sclList = docdesc.getElementsByTagName("SCL");
        List<BasicDataAttribute> bdaList = this.localTree.getBasicDataAttributes();
        List<BasicDataAttribute> bdas = new ArrayList<>();
        for (int i = 0; i < bdaList.size(); i++) {
            BasicDataAttribute bda = bdaList.get(i);
            bda = bdaList.get(i);
            //TODO: getValue if you want to get scl val
//                this.server.setBdaValue(bda, "");
//                bdas.add(bda);
        }
        this.server.serverSap.setValues(bdas);

    }
}
