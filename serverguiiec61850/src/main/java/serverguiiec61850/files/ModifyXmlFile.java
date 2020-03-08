package serverguiiec61850.files;

/**
 *
 * @author Philipp Mandl
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import serverguiiec61850.gui.Gui;

/**
 * modifiziert die xml dateien, bekommt die SCL und erstellt SCL Dateien mit den
 * einzelnen IEDs die Netzwerkeinstellungen werden hier auch gelesen. bzw. alles
 * was mit den Dateien zu tun hat befindet sich hier.
 *
 */
public class ModifyXmlFile {

    /**
     * erstellt aus einer SCL mehrere SCLs mit den jeweiligen IEDs
     *
     * @param filepath
     * @throws TransformerConfigurationException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     */
    public static void splitIed(String filepath) throws TransformerConfigurationException, TransformerException, IOException, SAXException, ParserConfigurationException {

        //Info: wurde erstellt für eine Datei mit allen IEDs die aber gekürzt ist
        //String filepath =null;
        //String filepath = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\everyIed.xml";
        if (filepath == null) {
            Gui.LOGGER_GUI.error("no file selected");
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        try {
            Document doc = docBuilder.parse(filepath);

            //Info:überprüft ob der erste richtige Tag ein SCL Tag ist
            if (!doc.getElementsByTagName("SCL").item(0).hasChildNodes()) {
                Gui.LOGGER_GUI.error("the file is no scl file");
            }

            //Info:IED Namen werden herausgefiltert
            NodeList IedList = doc.getElementsByTagName("IED");
            for (int i = 0; i < IedList.getLength(); i++) {
                Gui.LOGGER_GUI.info(IedList.item(i).getAttributes().getNamedItem("name") + "   " + Integer.toString(i + 1) + ".IED");
                Gui.LOGGER_GUI.info(IedList.item(i).getAttributes().getNamedItem("name").getTextContent());
                String path = (System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\" + IedList.item(i).getAttributes().getNamedItem("name").getTextContent());
                File file = new File(path);
                //Info:alle alten IEDs löschen 
                if (file.exists()) {
                    Path deletePath = Paths.get(path);
                    Files.delete(deletePath);
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
        } catch (IOException | TransformerException | DOMException | SAXException e) {
            Gui.LOGGER_GUI.error("error modify file", e);
        }

    }

    /**
     * gibt eine Liste mit den Netzwerkeinstellungen der gegebenen IED zurück
     *
     * @param filepath
     * @param iedName
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static ArrayList<String> getIp(String filepath, String iedName) throws SAXException, IOException, ParserConfigurationException {
        ArrayList<String> netInfos = new ArrayList<>();
        //String filepath = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\everyIed.xml";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        try {
            Document doc = docBuilder.parse(filepath);
            Element address = null;
            NodeList ConnectedAP = doc.getElementsByTagName("ConnectedAP");
            for (int l = 0; l < ConnectedAP.getLength(); l++) {
                Node networkNode = ConnectedAP.item(l);
                if (iedName.equals(networkNode.getAttributes().getNamedItem("iedName").getTextContent())) {
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
        } catch (IOException | SAXException e) {
            Document doc = null;
        } catch (DOMException e) {
            Gui.LOGGER_GUI.error("DOM exception");
        } catch (NullPointerException e) {
            Gui.LOGGER_GUI.error("no address in file");
        }

        return netInfos;
    }
}
