/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.gui.NodeDescription.java
 * @author Philipp Mandl
 */
package guiIec61850.files;

import com.beanit.openiec61850.clientgui.DataObjectTreeNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static guiIec61850.gui.GuiTree.tree;
import static java.lang.System.getProperty;
import static java.util.Arrays.asList;
import static javax.xml.parsers.DocumentBuilderFactory.newInstance;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * gets and sets the Node Description
 *
 * @author Philip Mandl
 */
public class NodeDescription {

    /**
     *
     */
    public static final org.slf4j.Logger LOGGER_NODEDESCRIPTION = getLogger(NodeDescription.class);

    private static final ArrayList<String> DESC_LIST = new ArrayList<>();
    private static final ArrayList<String> NODE_LIST = new ArrayList<>();

    private ModifyXmlFile xml;
    private DefaultTreeModel localTree;
    private final String ied;

    /**
     *
     * @param localtree
     * @param xml
     * @param ied
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public NodeDescription(DefaultTreeModel localtree, ModifyXmlFile xml, String ied) throws ParserConfigurationException, SAXException, IOException {
        this.xml = xml;
        this.localTree = localtree;
        this.ied = ied;

        try {
            DataObjectTreeNode node = ((DataObjectTreeNode) this.localTree.getRoot());
            modEl(node);
            tree.setModel(this.localTree);
            tree.doLayout();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER_NODEDESCRIPTION.error("", e);
        } catch (ClassCastException e) {
            LOGGER_NODEDESCRIPTION.error("a wrong node detected", e);
        }
    }

    private void modEl(DataObjectTreeNode node) throws ParserConfigurationException, SAXException, IOException {
        if (node.getAllowsChildren()) {
            for (int i = 0; i < node.getChildCount(); i++) {
                try {
                    modEl((DataObjectTreeNode) node.getChildAt(i));
                } catch (ClassCastException e) {
                    LOGGER_NODEDESCRIPTION.debug("dataset found", e);
                }
            }
        }

        String nodedesc;
        String nodename = (String) node.getUserObject();

        if (NODE_LIST.contains(nodename)) {
            nodedesc = DESC_LIST.get(NODE_LIST.indexOf(nodename));
        } else if ((nodename).contains("[") && (nodename).contains("]")) {
            NODE_LIST.add(nodename);
            DESC_LIST.add(NODE_LIST.indexOf(nodename), "FC");
            nodedesc = "FC";

        } else if (nodeList.contains(((String) node.getUserObject()).substring(0, ((String) node.getUserObject()).length() - 1))) {
            nodedesc = nodeDescList.get(nodeList.indexOf(((String) node.getUserObject()).substring(0, ((String) node.getUserObject()).length() - 1)));
        } else if (nodeList.contains(nodename)) {
            nodedesc = nodeDescList.get(nodeList.indexOf(nodename));
        } else {
            nodedesc = getDesc(nodename);
            NODE_LIST.add(nodename);
            DESC_LIST.add(NODE_LIST.indexOf(nodename), nodedesc);
        }

        if ((!"".equals(nodedesc))) {
            Object obj = nodename + "   " + nodedesc;
            node.setUserObject(obj);
        }

    }

    /**
     * gets JTree
     *
     * @return JTree
     */
    public DefaultTreeModel getTree() {
        return this.localTree;
    }

    /**
     * gets description of node
     *
     * @param name
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public String getDesc(String name) throws ParserConfigurationException, SAXException, IOException {
        try {
            DocumentBuilderFactory docFactory = newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            String filepath = getProperty("user.dir") + "\\files\\icd\\" + this.ied;
            Document docdesc = docBuilder.parse(filepath);

            NodeList doiList = docdesc.getElementsByTagName("DOI");
            NodeList lnList = docdesc.getElementsByTagName("LN");
            NodeList ln0List = docdesc.getElementsByTagName("LN0");
            NodeList reportList = docdesc.getElementsByTagName("ReportControl");
            NodeList lDeviceList = docdesc.getElementsByTagName("LDevice");

            //ldDevice
            if (name.length() >= this.ied.length()) {
                for (int i = 0; i < lDeviceList.getLength(); i++) {
                    Node childNode = lDeviceList.item(i);
                    if (childNode.getAttributes() != null) {
                        Node nameAttribute = childNode.getAttributes().getNamedItem("inst");
                        String newname = name.substring(this.ied.length(), name.length());
                        if (nameAttribute != null && nameAttribute.getNodeValue().equals(newname)) {
                            try {
                                return childNode.getAttributes().getNamedItem("desc").getNodeValue();
                            } catch (DOMException | NullPointerException e) {
                            }
                        }
                    }
                }
            }

            //LN0
            for (int i = 0; i < ln0List.getLength(); i++) {
                Node childNode = ln0List.item(i);
                if (childNode.getAttributes() != null) {
                    String nameText = "";
                    String instText = "";
                    Node nameAttribute = childNode.getAttributes().getNamedItem("lnClass");
                    if (nameAttribute != null) {
                        nameText = childNode.getAttributes().getNamedItem("lnClass").getNodeValue();
                    }
                    childNode = ln0List.item(i);
                    Node instAttribute = childNode.getAttributes().getNamedItem("inst");
                    if (instAttribute != null) {
                        instText = childNode.getAttributes().getNamedItem("inst").getNodeValue();
                    }
                    String nodeName = nameText + instText;

                    if (nodeName.equals(name)) {
                        childNode = ln0List.item(i);
                        try {
                            if (childNode.getAttributes().getNamedItem("desc").getNodeValue() != null) {
                                return childNode.getAttributes().getNamedItem("desc").getNodeValue();
                            }

                        } catch (DOMException e) {
                            return childNode.getAttributes().getNamedItem("type").getNodeValue();

                        }
                    }
                }
            }
            //DOI
            for (int i = 0; i < doiList.getLength(); i++) {
                Node childNode = doiList.item(i);
                if (childNode.getAttributes() != null) {
                    Node nameAttribute = childNode.getAttributes().getNamedItem("name");
                    if (nameAttribute != null && nameAttribute.getNodeValue().equals(name)) {
                        try {
                            return childNode.getAttributes().getNamedItem("desc").getNodeValue();
                        } catch (DOMException e) {
                            childNode = doiList.item(i);
                            return childNode.getAttributes().getNamedItem("type").getNodeValue();
                        } catch (NullPointerException e) {

                        }
                    }
                }
            }
            //Report
            for (int i = 0; i < reportList.getLength(); i++) {
                Node childNode = reportList.item(i);
                if (childNode.getAttributes() != null) {
                    Node nameAttribute = childNode.getAttributes().getNamedItem("name");
                    if (nameAttribute != null && nameAttribute.getNodeValue().equals(name)) {
                        try {
                            return childNode.getChildNodes().item(1).getAttributes().getNamedItem("type").getNodeValue();
                        } catch (NullPointerException e) {

                        }
                    }
                }
            }

        } catch (DOMException e) {

        }
        return "";
    }

    private String[] nodeNameString = {"ACTM ", "AJCL ", "ANCR", "APSF ", "APSS ", "APST ", "ARCO", "ARIS", "ASEQ ",
        "ATCC", "AVCO", "CALH", "CCGR", "CILO", "CPOW", "CSWI", "CSYN", "DCCT", "DCHB", "DCHC",
        "DCIP", "DCRP", "DCTS", "DEXC", "DFCL", "DFLV", "DFPM", "DGEN", "DOPA", "DOPM", "DOPR",
        "DPST", "DPVA", "DPVC", "DPVM", "DRAT", "DRAZ", "DRCC", "DRCS", "DRCT", "DREX", "DSCC",
        "DSCH", "DSFC", "DSTK", "EBCF", "EFCV", "EGTU", "ESCV", "ESPD", "ESTU", "EUNT", "FCNT",
        "FCSD", "FDBF", "FFIL", "FHBT", "FLIM", "FMTX", "FPID", "FRMP", "FSCH", "FSEQ", "FSPT",
        "FXOT", "FXPS", "FXUT ", "GAPC", "GGIO", "GLOG", "GSAL", "GUNT", "HBRG", "HCOM", "HDAM",
        "HDLS", "HGPI", "HGTE", "HITG", "HJCL", "HLKG", "HLVL", "HMBR", "HNDL", "HNHD", "HOTP",
        "HRES", "HSPD", "HSST", "HTGV", "HTRB", "HTRK", "HTUR", "HVLV", "HWCL", "HUNT", "GUNT",
        "HSEQ", "ASEQ", "IARC", "IFIR", "IHMI", "IHND", "ISAF", "ITCI", "ITMI", "ITPC", "KFIL",
        "KHTR", "KPMP", "KTNK", "KVLV", "KFAN", "LCCH", "LGOS", "LLN0", "LPHD", "LSVS", "LTIM",
        "LTMS", "LTRK", "MENV", "MFLK", "MFLW", "MFUL", "MHAI", "MHAN", "MHET", "MHYD", "MMDC",
        "MMET", "MMTN", "MMTR", "MMXN", "MMXU", "MPRS", "MSQI", "PDIF", "PDIR", "PDIS", "PDOP",
        "PDUP", "PFRC", "PHAR", "PHIZ", "PIOC", "PMRI", "PMSS", "POPF", "PPAM", "PRTR", "PSCH",
        "PSDE", "PTEF", "PTHF", "PTOC", "PTOF", "PTOV", "PTRC", "PTTR", "PTUC", "PTUF", "PTUR",
        "PTUV", "PUPF", "PVOC", "PVPH", "PZSU", "QFVR", "QITR", "QIUB", "QVTR", "QVUB", "QVVR",
        "RADR", "RBDR", "RBRF", "RDIR", "RDRE", "RDRS", "RFBC", "RFLO", "RMXU", "RPSB", "RREC",
        "RSYN", "SARC", "SCBR", "SECW", "SFLW", "SIMG", "SIML", "SLEV", "SLTC", "SOPM", "SPOS",
        "SPDC", "SPRS", "SPTR", "SSWI", "STMP", "SVBR", "TANG", "TAXD", "TCTR", "TDST", "TECW",
        "TFLW", "TFRQ", "TGSN", "THUM", "TLEV", "TLVL", "TMGF", "TMVM", "TPOS", "TPRS", "TRTN",
        "TSND", "TTMP", "TTNS", "TVBR", "TVTR", "TWPH", "WALG", "WALM", "WAPC", "WCNV", "WCON",
        "WGEN", "WMET", "WNAC", "WREP", "WROT", "WRPC", "WSLG", "WTOW", "WTRF", "WTRM", "WTUR",
        "WYAW", "XCBR", "XFFL", "XFUS", "XSWI", "YEFN", "YLTC", "YPSH", "YPTR", "ZAXN", "ZBAT",
        "ZBSH", "ZBTC", "ZCAB", "ZCAP", "ZCON", "ZGEN", "ZGIL", "ZINV", "ZLIN", "ZMOT", "ZRCT",
        "ZREA", "ZRES", "ZRRC", "ZSAR", "ZSCR", "ZSMC", "ZTCF", "ZTCR", "Mod", "stVal", "q", "t", "setVal", "RptID", "GI", "RptEna"};
    private List<String> nodeList = asList(nodeNameString);

    private String[] nodeDescription = {"Auswahl des Steuermodus. Gesamt-LN für Steuerungen mit verschiedenen möglichen Modi.", "Gemeinsame Steuerfunktion zum Ausgleich der Gesamtleistung aus verschiedenen Quellen.", "Automatisierung: Neutralstromreglersteuerung", "PSS 4B Filter. Stellt einen Filter gemäß IEEE 421.5-2005 dar.", "PSS-Steuerung. Allgemeine Informationen einer PSS-Funktion.", "PSS 2A / B Filter. Stellt einen Filter gemäß IEEE 421.5-2005 dar.	", "Automatisierung: Blindleistungsregelung", "Automatisierung: Widerstandssteuerung", "Generischer Steuerungs-Action-Sequenzer", "Automatisierung: Stufenschaltersteuerung", "Automatisierung: Spannungsregelung", "Steuerung: Alarmbehandlung", "Steuerung: Kühlgruppensteuerung", "Steuerung: Verriegelung", "Steuerung: Point-on-Wave-Umschaltung", "Steuerung: Controller wechseln", "Steuerung: Synchronizer-Controller", "DER wirtschaftliche Versandparameter", "Kessel", "KWK-Systemsteuerung", "Hubkolbenmotor", "Unternehmensmerkmale der DER-Anlage bei der ECP", "Wärmespeicherung", "Erregung", "Brennstoffzellensteuerung", "Kraftstoffzufuhrsystem", "Kraftstoffverarbeitungsmodul", "DER-Einheitsgenerator", "DER Betriebsbehörde bei der ECP", "Betriebsart bei ECP", "Betriebsmerkmale bei ECP", "Statusinformationen bei der ECP", "Eigenschaften des Photovoltaik-Arrays", "Photovoltaik-Array-Controller", "Nennwerte für Photovoltaikmodule", "DER-Generatorwerte", "DER erweiterte Generatorbewertungen", "DER Aufsichtskontrolle", "DER-Controller-Status", "DER-Reglereigenschaften", "Anregungsbewertungen", "DER Energie- und / oder Nebendienstplan", "DER Energie- und / oder Nebendienstplan", "Drehzahl- / Frequenzregler", "Brennstoffzellenstapel", "Blocksteuerfunktion.", "Kraftstoffsteuerventil.", "Gasturbinenproduktionseinheit.", "Dampfregelventil.", "Geschwindigkeitsüberwachung.", "Dampfturbinenproduktionseinheit.", "Betriebsart der thermischen Einheit.", "Zähler", "Beschreibung der Kurvenform", "Totbandfilter.", "Generischer Filter", "Herzschlag.", "Ausgangsbeschränkung der Steuerfunktion", "Auslösematrix.", "PID-Regler", "Rampenfunktion", "Planer.", "Sequenzer", "Sollwertregelfunktion", "Aktion über der Schwelle", "Funktionsprioritätsstatus.", "Aktion unter der Schwelle", "Generische automatische Prozesssteuerung", "Generische Prozess-E / A.", "Generisches Protokoll", "Generische Sicherheitsanwendung", "Betriebsart der Produktionseinheit.", "Turbine - Generatorwellenlager", "Kombinator", "Wasserkraftdamm", "Überwachung der Dammleckage", "Torpositionsanzeige", "Dammtor", "Einlasstor", "Gemeinsame Kontrolle", "Leckageüberwachung", "Wasserstandsanzeige", "Mechanische Bremse", "Nadelkontrolle", "Wassernetzkopfdaten", "Dammüberdeckungsschutz", "Wasserkraft- / Wasserreservoir", "Geschwindigkeitsüberwachung", "Schwallwelle oder Ausgleichsbehälter.", "Leitschaufeln (Schlupftor).", "Laufschaufeln.", "Papierkorb,", "Turbine.", "Ventil", "Wasserkontrollfunktion.", "Wasserkraftwerk", "Wasserkraftwerk", "Wasserkraftwerk s", "Wasserkraftwerk s", "Archivierung", "Generische Branderkennungs- und Alarmfunktion.", "Mensch-Maschine-Schnittstelle", "Generische physische Mensch-Maschine-Schnittstelle. ZB ein Druckknopf", "Sicherheitsalarmfunktion", "Telecontrol-Schnittstelle", "Telemonitoring-Schnittstelle", "Teleprotection-Kommunikationsschnittstellen", "Filter", "Heizung", "Pumpe", "Panzer", "Ventil", "Ventilator", "Überwachung des physischen Kommunikationskanals", "GOOSE-Abonnement", "Logischer Knoten Null", "Informationen zu physischen Geräten", "Sampled Value-Abonnement", "Zeiteinteilung", "Time Master Supervision", "Service-Tracking", "Umweltinformationen", "Flimmern Messung", "Durchflussmessungen", "Kraftstoffeigenschaften", "3ph Harmonische", "1ph Harmonische - Nicht phasenbezogene Harmonische oder Interharmonische", "Wärmemesswerte", "Hydrologische Informationen", "Gleichstrommessung", "Meteorologische Informationen", "1ph Wh, Varh oder VAh", "3ph Wh, Varh oder VAh", "Nicht phasenbezogene Messung", "Effektivwert I, V und F und Angabe von W, Var, VA und PF.", "Druckmessungen", "Positive, negative und Null-Sequenzkomponenten", "Differential", "Richtungsvergleich", "Entfernung", "Richtung über Macht", "Direkt unter Strom", "Änderungsrate der Frequenz", "Harmonische Zurückhaltung", "Bodendetektor", "Momentaner Überstrom", "Motorneustart sperren", "Überwachung der Motorstartzeit", "Überleistungsfaktor", "Phasenwinkelmessung", "Rotorschutz", "Schutzschema", "Empfindlicher gerichteter Erdschluss", "Vorübergehender Erdschluss", "Thyristorschutz", "Zeitüberstrom", "Überfrequenz", "Überspannung", "Schutzauslösung Konditionierung", "Thermische Überlastung", "Unterströmung", "Unterfrequenz", "Unter Widerstand", "Unterspannung", "Unterleistungsfaktor", "Spannungsgesteuerter Zeitüberstrom", "Volt pro Hz", "Null Geschwindigkeit oder Untergeschwindigkeit", "Frequenzänderung", "Strom transient", "Stromungleichgewichtsänderung", "Spannungsübergang", "Spannungsunsymmetrieänderung", "Spannungsänderung", "Störschreiberkanal analog", "Störschreiber Kanal binär", "Leistungsschalterfehler", "Richtungselement", "Störschreiberfunktion", "Störungsaufzeichnung", "Konfiguration des Feldschalters.", "Fehlersuche", "Differenzmessungen", "Power Swing Erkennung / Blockierung", "Autoreclosing", "Synchronitätsprüfung", "Überwachung und Diagnose von Lichtbögen", "Leistungsschalterüberwachung", "Überwachung der elektrischen Leitfähigkeit in Wasser.", "Überwachung des Medienflusses.", "Überwachung des Isoliermediums (Gas)", "Überwachung des Isoliermediums (flüssig)", "Überwachung auf Medienebene.", "Stufenschalterüberwachung", "Überwachung des Betriebsmechanismus", "Überwachung der Geräteposition.", "Überwachung und Diagnose von Teilentladungen", "Überwachung des Mediendrucks.", "Überwachung des Leistungstransformators", "Schaltkreisüberwachung", "Temperaturüberwachung", "Vibrationsüberwachung", "Winkel", "Axiale Verschiebung", "Stromwandler", "Entfernung", "Messung der elektrischen Leitfähigkeit in Wasser.", "Flüssigkeitsstrom", "Frequenz", "Generischer Sensor", "Feuchtigkeit", "Füllstandssensor", "Medienebene", "Magnetfeld", "Bewegungssensor", "Positionsanzeiger", "Drucksensor", "Rotationssender", "Schalldrucksensor", "Temperatursensor", "Mechanische Spannung / Spannung", "Schwingungssensor", "Spannungswandler", "Wassersäure", "Analoge Protokollinformationen für Windkraftanlagen", "Alarminformationen für Windkraftanlagen", "Informationen zur Wirkleistungssteuerung von Windkraftanlagen", "Informationen zum Windkraftanlagenkonverter", "Informationen zur Überwachung des Windkraftanlagenzustands", "Informationen zum Windkraftanlagengenerator", "Meteorologische Informationen zu Windkraftanlagen", "Informationen zu Windkraftanlagengondeln", "Informationen zum Windkraftanlagenbericht", "Informationen zum Rotor der Windkraftanlage", "Informationen zur Blindleistungsregelung von Windkraftanlagen", "Protokollinformationen zum Windkraftanlagenstatus", "Informationen zum Windturbinenturm", "Informationen zu Windkraftanlagen", "Informationen zur Übertragung von Windkraftanlagen", "Allgemeine Informationen zu Windkraftanlagen", "Informationen zum Gieren von Windkraftanlagen", "Leistungsschalter", "Feld blinkt.", "Sicherung", "Schaltkreisschalter", "Erdschlussneutralisator (Petersen-Spule)", "Stufenschalter", "Power Shunt", "Leistungstransformator", "Hilfsnetz", "Batterie", "Buchse", "Akkuladegerät", "Stromkabel", "Kondensatorbank", "Konverter", "Generator", "Gasisolierte Leitung", "Wandler", "Freileitung", "Motor", "Gleichrichter", "Reaktor", "Widerstand", "Rotierende reaktive Komponente", "Überspannungsableiter", "Halbleitergesteuerter Gleichrichter", "Synchronmaschine", "Thyristorgesteuerter Frequenzumrichter",
        "Thyristorgesteuerte reaktive Komponente", "Modus", "aktueller Wert", "Qualität", "Zeitstempel", "Wert", "Report ID", "general interrogation", "aktiv"};
    private List<String> nodeDescList = asList(nodeDescription);
}
