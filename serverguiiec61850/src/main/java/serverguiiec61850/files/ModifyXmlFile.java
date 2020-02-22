package serverguiiec61850.files;

/**
 *
 * @author Philipp
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import serverguiiec61850.gui.Gui;

/**
 *
 * @author Philipp
 */
public class ModifyXmlFile {

    /**
     *
     * @param filepath
     * @throws TransformerConfigurationException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws serverguiiec61850.files.ModifyXmlFile.NonSclFileException
     */
    public static void splitIed(String filepath) throws TransformerConfigurationException, TransformerException, IOException, SAXException, ParserConfigurationException, NonSclFileException {

        //String filepath =null;
        //String filepath = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\everyIed.xml";
        if (filepath == null) {
            throw new NullPointerException("no file selected");
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(filepath);

        if (!doc.getElementsByTagName("SCL").item(0).hasChildNodes()) {
            throw new NonSclFileException();
        }

        NodeList IedList = doc.getElementsByTagName("IED");
        for (int i = 0; i < IedList.getLength(); i++) {
            System.out.println(IedList.item(i).getAttributes().getNamedItem("name") + "   " + Integer.toString(i + 1) + ".IED");
            System.out.println(IedList.item(i).getAttributes().getNamedItem("name").getTextContent());
            String path = (System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\" + IedList.item(i).getAttributes().getNamedItem("name").getTextContent() + ".xml");
            File file = new File(path);
            if (file.exists()) {
                Path deletePath = Paths.get(path);
                Files.delete(deletePath);
            }
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

            for (int j = 0; j < IedList.getLength(); j++) {
                Node n = IedList.item(i);
                Node copyOfn = iedxml.importNode(n, true);
                root.appendChild(copyOfn);
            }
            NodeList datatypenode = doc.getElementsByTagName("DataTypeTemplates");
            for (int k = 0; k < datatypenode.getLength(); k++) {
                Node m = datatypenode.item(k);
                Node copyOfm = iedxml.importNode(m, true);
                root.appendChild(copyOfm);
            }

            NodeList addressNode = doc.getElementsByTagName("Communication");
            for (int l = 0; l < addressNode.getLength(); l++) {
                Node m = addressNode.item(l);
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
        Document doc = docBuilder.parse(filepath);

        Element address = null;
        NodeList ConnectedAP = doc.getElementsByTagName("ConnectedAP");
        for (int l = 0; l < ConnectedAP.getLength(); l++) {
            Node m = ConnectedAP.item(l);
            if (iedName.equals(m.getAttributes().getNamedItem("iedName").getTextContent())) {
                address = (Element) m;
            }
        }
        NodeList datatypenode;
        try {
            datatypenode = address.getElementsByTagName("P");
        } catch (Exception e) {
            datatypenode = null;
            Gui.LOGGER_GUI.error("no address in file");
        }

        for (int k = 0; k < datatypenode.getLength(); k++) {
            Node m = datatypenode.item(k);
            if ("IP".equals(m.getAttributes().getNamedItem("type").getTextContent())) {
                netInfos.add("IP: " + m.getFirstChild().getNodeValue());
                //System.out.println("IP: " + m.getFirstChild().getNodeValue()); sonst bei jedem Aufruf= [kotzsmiley]
            }
            if ("IP-SUBNET".equals(m.getAttributes().getNamedItem("type").getTextContent())) {
                netInfos.add("IP-SUBNET: " + m.getFirstChild().getNodeValue());
                //System.out.println("IP-SUBNET: " + m.getFirstChild().getNodeValue());
            }
            if ("IP-GATEWAY".equals(m.getAttributes().getNamedItem("type").getTextContent())) {
                netInfos.add("IP-GATEWAY: " + m.getFirstChild().getNodeValue());
                //System.out.println("IP-GATEWAY: " + m.getFirstChild().getNodeValue());
            }
        }
        return netInfos;
    }

    public static class NonSclFileException extends Exception {

        public NonSclFileException() {
        }
    }
}
