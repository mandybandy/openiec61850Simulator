/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.files.ModifyXmlFile.java
 * @author Philipp Mandl
 */
package guiIec61850.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static java.lang.System.getProperty;
import static java.nio.file.Files.delete;
import static java.nio.file.Paths.get;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * modify xml files, XML file controler
 */
public class ModifyXmlFile {

    private Document doc;
    private String ied;
    private static final Logger LOGGER_XML = getLogger(ModifyXmlFile.class);

    /**
     * modify XML file
     *
     * @param filepath filepath
     */
    public ModifyXmlFile(String filepath) {
        try {
            if (filepath == null) {
                LOGGER_XML.error("no file selected");
            }
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            try {
                this.doc = docBuilder.parse(filepath);
            }
            catch (SAXException | IOException e) {
                LOGGER_XML.error("", e);
            }
        }
        catch (ParserConfigurationException e) {
            LOGGER_XML.error("", e);
        }
    }

    /**
     * creates icd from scl file, split into IEDs
     *
     * @throws TransformerConfigurationException transformer error
     * @throws java.io.IOException io error
     * @throws javax.xml.parsers.ParserConfigurationException parser error
     * @throws org.xml.sax.SAXException saxe error
     */
    public void splitIed() throws TransformerConfigurationException, TransformerException, IOException, SAXException, ParserConfigurationException {

        //Info: wurde erstellt für eine Datei mit allen IEDs die aber gekürzt ist
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        //Info:IED Namen werden herausgefiltert
        NodeList IedList = doc.getElementsByTagName("IED");
        for (int i = 0; i < IedList.getLength(); i++) {
            LOGGER_XML.info(IedList.item(i).getAttributes().getNamedItem("name") + "   " + Integer.toString(i + 1) + ".IED");
            LOGGER_XML.info(IedList.item(i).getAttributes().getNamedItem("name").getTextContent());
            String path = (getProperty("user.dir") + "\\files\\icd\\" + IedList.item(i).getAttributes().getNamedItem("name").getTextContent());
            File file = new File(path);
            //Info:alle alten IEDs löschen
            if (file.exists()) {
                Path deletePath = get(path);
                delete(deletePath);
            }
            //Info: SCL Datei wird erstellt aus IED, eigentlich keine
            //SCL mehr.
            // es werden der Adressblock, der DataTypeTemplates Block und die IED Namen benötigt
            file.createNewFile();

            Document iedxml = docBuilder.newDocument();

            Element root = iedxml.createElement("SCL");
            iedxml.appendChild(root);

            Node scl = doc.getElementsByTagName("SCL").item(0);
            iedxml.importNode(scl, true);

            for (int j = 0; j < scl.getAttributes().getLength(); j++) {
                Node a = scl.getAttributes().item(j);
                root.setAttribute(a.getNodeName(), a.getNodeValue());
            }

            for (int iedNameCounter = 0; iedNameCounter < IedList.getLength(); iedNameCounter++) {
                Node iedNameNode = IedList.item(i);
                Node copyOfn = iedxml.importNode(iedNameNode, true);
                root.appendChild(copyOfn);
            }
            NodeList datatypenode = doc.getElementsByTagName("DataTypeTemplates");
            for (int dataTypeTemplatesCounter = 0; dataTypeTemplatesCounter < datatypenode.getLength(); dataTypeTemplatesCounter++) {
                Node dataTypeTemplatesNode = datatypenode.item(dataTypeTemplatesCounter);
                Node copyOfm = iedxml.importNode(dataTypeTemplatesNode, true);
                root.appendChild(copyOfm);
            }

            NodeList addressNodeList = doc.getElementsByTagName("Communication");
            for (int addressNodeCounter = 0; addressNodeCounter < addressNodeList.getLength(); addressNodeCounter++) {
                Node m = addressNodeList.item(addressNodeCounter);
                Node copyOfm = iedxml.importNode(m, true);
                root.appendChild(copyOfm);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(iedxml);
            StreamResult streamResult = new StreamResult(new File(path));
            transformer.transform(domSource, streamResult);
        }

    }

    /**
     * returns a list consists of the network settings
     *
     * @return networkinfo list
     * @throws SAXException sax error
     * @throws IOException io error
     * @throws ParserConfigurationException parser error
     */
    public ArrayList<String> getIp() throws SAXException, IOException, ParserConfigurationException {
        ArrayList<String> netInfos = new ArrayList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        try {
            Element address = null;
            NodeList ConnectedAP = this.doc.getElementsByTagName("ConnectedAP");
            for (int l = 0; l < ConnectedAP.getLength(); l++) {
                Node networkNode = ConnectedAP.item(l);
                if (this.ied.equals(networkNode.getAttributes().getNamedItem("iedName").getTextContent())) {
                    address = (Element) networkNode;
                }
            }
            NodeList datatypenode;

            datatypenode = address.getElementsByTagName("P");
            for (int pCounter = 0; pCounter < datatypenode.getLength(); pCounter++) {
                Node node = datatypenode.item(pCounter);
                if ("IP".equals(node.getAttributes().getNamedItem("type").getTextContent())) {
                    netInfos.add("IP: " + node.getFirstChild().getNodeValue());
                    //System.out.println("IP: " + m.getFirstChild().getNodeValue()); sonst bei jedem Aufruf= [kotzsmiley]
                }
                if ("IP-SUBNET".equals(node.getAttributes().getNamedItem("type").getTextContent())) {
                    netInfos.add("IP-SUBNET: " + node.getFirstChild().getNodeValue());
                }
                if ("IP-GATEWAY".equals(node.getAttributes().getNamedItem("type").getTextContent())) {
                    netInfos.add("IP-GATEWAY: " + node.getFirstChild().getNodeValue());
                }
            }
        }
        catch (DOMException e) {
            LOGGER_XML.error("DOM exception");
        }
        catch (NullPointerException e) {
            LOGGER_XML.error("no address in file");
        }

        return netInfos;
    }

    /**
     * sets IED in class
     *
     * @param ied string ied
     */
    public void setIed(String ied) {
        this.ied = ied;
    }
}
