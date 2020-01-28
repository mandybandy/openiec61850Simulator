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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ModifyXMLFile {

    public static void main(String argv[]) {

        try {
            String filepath = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\master.xml";
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(filepath);

            Node startSclTag = doc.getElementsByTagName("SCL").item(0);

            NodeList IedList = doc.getElementsByTagName("IED");
            for (int i = 0; i < IedList.getLength(); i++) {
                System.out.println(IedList.item(i).getAttributes().getNamedItem("name"));
                File file = new File(System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\ied" + String.valueOf(i) + ".xml");
                String path = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\ied" + String.valueOf(i) + ".xml";
                File tmpDir = new File(path);
                boolean exists = tmpDir.exists();
                if (exists) {
                    Path deletePath = Paths.get(path);
                    Files.delete(deletePath);
                }

                String filepathIed = System.getProperty("user.dir") + "\\src\\main\\java\\serverguiiec61850\\files\\icd\\master.xml";
                Document iedxml = docBuilder.parse(filepathIed);
                Node n = IedList.item(i);
                file.createNewFile();
                
                Element scltag = iedxml.createElement("SCL");
                iedxml.appendChild(scltag);
                
                Node copyTo = iedxml.getFirstChild();
                Node copyOfn = doc.importNode(n, true);
                copyTo.appendChild(copyOfn);
            }
        } catch (SAXException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ModifyXMLFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
