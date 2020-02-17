/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Philipp
 */
public class ModifyXMLFile {

    /**
     *
     * @throws TransformerConfigurationException
     */
    public static void splitIed() throws TransformerConfigurationException {

        try {
            String filepath = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\everyIed.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);
            //ToDo: throw exception
            if (!doc.getElementsByTagName("SCL").item(0).hasChildNodes()) {
                return;
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
                //ToDo: get Attributes from File, insert to iedxml
                Node scl = doc.getElementsByTagName("SCL").item(0);
                iedxml.importNode(scl, true);

                for (int j = 0; j < scl.getAttributes().getLength(); j++) {
                    Node a = scl.getAttributes().item(j);
                    root.setAttribute(a.getNodeName(), a.getNodeValue());
                }
                System.out.println(doc.getElementsByTagName("SCL").item(0).getNodeName().toString());

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
        } catch (SAXException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static ArrayList<String> getIp(String filepath,String iedName) {
        ArrayList<String> netInfos = new ArrayList<>();
        try {
            //String filepath = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\everyIed.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            //ToDo: get Name of IED (default to 1)
            Element address = null;
            NodeList ConnectedAP = doc.getElementsByTagName("ConnectedAP");
            for (int l = 0; l < ConnectedAP.getLength(); l++) {
                Node m = ConnectedAP.item(l);
                if (iedName.equals(m.getAttributes().getNamedItem("iedName").getTextContent())) {
                    System.out.println("ha gay");
                    address = (Element)m;
                }
            }
                NodeList datatypenode = address.getElementsByTagName("P");

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
        } catch (SAXException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
