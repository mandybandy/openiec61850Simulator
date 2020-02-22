package serverguiiec61850.network;

import com.beanit.openiec61850.BdaTriggerConditions;
import com.beanit.openiec61850.Brcb;
import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.ClientSap;
import com.beanit.openiec61850.DataSet;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.Rcb;
import com.beanit.openiec61850.Report;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServiceError;
import com.beanit.openiec61850.Urcb;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philipp Mandl
 */
public class Client {

    private Server server;
    /**
     *
     */
    public static volatile ClientAssociation association;

    private static final Logger LOGGER_CLIENT = LoggerFactory.getLogger(Server.class);

    /**
     *
     * @param host
     * @param port
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     * @throws com.beanit.openiec61850.SclParseException
     */
    public Client(String host, int port) throws UnknownHostException, IOException, SclParseException {

        InetAddress address;
        Exception er;

        address = InetAddress.getByName(host);

        ClientSap clientSap = new ClientSap();

        association = clientSap.associate(address, port, null, null);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                association.close();
            }
        });
        LOGGER_CLIENT.info(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("SUCCESSFULLY CONNECTED"));
        LOGGER_CLIENT.info(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("READING MODEL FROM FILE..."));
        server.serverModel = SclParser.parse(serverguiiec61850.gui.Gui.iedPath).get(0);
        association.setServerModel(server.serverModel);
        LOGGER_CLIENT.debug(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("SUCCESSFULLY READ MODEL"));
    }

    /**
     *
     * @param report
     * @return
     */
    public Report newReport(Report report) {
        return report;
    }

    /**
     *
     */
    public void printservermodel() {
        System.out.println(server.serverModel);
    }

    /**
     *
     * @throws IOException
     * @throws com.beanit.openiec61850.ServiceError
     */
    public void readalldata() throws IOException, ServiceError {
        System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("READING ALL DATA..."));
        association.getAllDataValues();
        System.out.println(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("DONE"));
    }

    /**
     *
     * @param reference
     * @param fcString
     * @throws com.beanit.openiec61850.ServiceError
     * @throws java.io.IOException
     */
    public void getdata(String reference, String fcString) throws ServiceError, IOException {
        if (server.serverModel == null) {
            System.out.println(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("YOU HAVE TO RETRIEVE THE MODEL BEFORE READING DATA."));
            return;
        }

        FcModelNode fcModelNode = askForFcModelNode(reference, fcString);

        System.out.println(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("SENDING GETDATAVALUES REQUEST..."));
        association.getDataValues(fcModelNode);
        System.out.println(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("SUCCESSFULLY READ DATA."));
        System.out.println(fcModelNode);
    }

    /**
     *
     * @param reference
     * @param fcString
     * @param numberOfEntriesString
     * @return
     * @throws com.beanit.openiec61850.ServiceError
     * @throws java.io.IOException
     */
    public String createdataset(String reference, String fcString, String numberOfEntriesString) throws ServiceError, IOException {
        int numDataSetEntries = Integer.parseInt(numberOfEntriesString);

        List<FcModelNode> dataSetMembers = new ArrayList<>();
        for (int i = 0; i < numDataSetEntries; i++) {
            dataSetMembers.add(askForFcModelNode(reference, fcString));
        }
        for (int i = 0; i < dataSetMembers.size(); i++) {
            if (dataSetMembers.get(i) == null) {
                return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("A MEMBER IS NOT DEFINED");
            }
        }
        DataSet dataSet = new DataSet(reference, dataSetMembers);
        System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("CREATING DATA SET.."));
        association.createDataSet(dataSet);
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("CREATED DATASET");
    }

    /**
     *
     * @param reference
     * @return
     * @throws com.beanit.openiec61850.ServiceError
     * @throws java.io.IOException
     */
    public String deletedataset(String reference) throws ServiceError, IOException {
        DataSet dataSet = server.serverModel.getDataSet(reference);
        if (dataSet == null) {
            //gibs nd
            return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("DATASET NOT FOUND ERROR WHILE DELETING DATASET");
        }
        System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("DELETING DATA SET.."));
        association.deleteDataSet(dataSet);

        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("DELETED DATASET");
    }

    /**
     *
     * @param reference
     * @return
     * @throws com.beanit.openiec61850.ServiceError
     * @throws java.io.IOException
     */
    public Urcb getUrcb(String reference) throws ServiceError, IOException {

        Urcb urcb = server.serverModel.getUrcb(reference);
        if (urcb != null) {
            association.getRcbValues(urcb);
            return urcb;
        }
        return null;
    }

    /**
     *
     * @param reference
     * @return
     * @throws com.beanit.openiec61850.ServiceError
     * @throws java.io.IOException
     */
    public Brcb getBrcb(String reference) throws ServiceError, IOException {

        Brcb brcb = server.serverModel.getBrcb(reference);
        if (brcb != null) {
            association.getRcbValues(brcb);
            return brcb;
        }
        return null;
    }

    /**
     *
     * @param reference
     * @return
     */
    public Rcb getRcb(String reference) {
        Brcb brcb = server.serverModel.getBrcb(reference);
        Urcb urcb = server.serverModel.getUrcb(reference);
        if (urcb != null) {
            Rcb rcb = server.serverModel.getUrcb(reference);
            return rcb;
        } else if (brcb != null) {
            Rcb rcb = server.serverModel.getBrcb(reference);
            return rcb;
        }
        return null;
    }

    /**
     *
     * @param reference
     * @param time
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String reserveReport(String reference, short time) throws ServiceError, IOException {
        System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("RESERVING RCB.."));
        if (getUrcb(reference) != null) {
            association.reserveUrcb(getUrcb(reference));
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("RESERVED UNBUFFERED REPORT: {0}"), new Object[] {reference});
        } else if (getBrcb(reference) != null) {
            association.reserveBrcb(getBrcb(reference), time);
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("RESERVED BUFFERED REPORT: {0}"), new Object[] {reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE RESERVING REPORT");
    }

    /**
     *
     * @param reference
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String cancelReservation(String reference) throws ServiceError, IOException {
        System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("CANCELING RCB RESERVATION.."));
        Urcb urcb = getUrcb(reference);
        if (urcb != null) {
            association.cancelUrcbReservation(urcb);
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("CANCELD RESERVATION: {0}"), new Object[] {reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE CANCELLING RESERVATION");
    }

    /**
     *
     * @param reference
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String enableReport(String reference) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("ENABLING REPORTING.."));
            association.enableReporting(getRcb(reference));
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT ENABLED: {0}"), new Object[] {reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE ENABLING REPORT");
    }

    /**
     *
     * @param reference
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String disableReport(String reference) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("DISABLING REPORTING.."));
            association.disableReporting(getRcb(reference));
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT DISABLED: {0}"), new Object[] {reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE DISABLING REPORT");
    }

    /**
     *
     * @param reference
     * @param datasetValue
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String setTriggerReport(String reference, String datasetValue) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            rcb.getDatSet().setValue(datasetValue);
            List<ServiceError> serviceErrors = null;
            serviceErrors = association.setRcbValues(rcb, false, true, false, false, false, false, false, false);
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("VALUE {0} SET ON: {1}"), new Object[] {datasetValue, reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE SETTING TRIGGER");
    }

    /**
     *
     * @param reference
     * @param triggerOptionsString
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String setDatasetReport(String reference, String triggerOptionsString) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            String[] triggerOptionsStrings = triggerOptionsString.split(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString(","));
            BdaTriggerConditions triggerOptions = rcb.getTrgOps();
            triggerOptions.setDataChange(Boolean.parseBoolean(triggerOptionsStrings[0]));
            triggerOptions.setDataUpdate(Boolean.parseBoolean(triggerOptionsStrings[1]));
            triggerOptions.setQualityChange(Boolean.parseBoolean(triggerOptionsStrings[2]));
            triggerOptions.setIntegrity(Boolean.parseBoolean(triggerOptionsStrings[3]));
            triggerOptions.setGeneralInterrogation(Boolean.parseBoolean(triggerOptionsStrings[4]));
            List<ServiceError> serviceErrors = association.setRcbValues(rcb, false, false, false, false, true, false, false, false);
            if (serviceErrors.get(0) != null) {
                throw serviceErrors.get(0);
            }
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("DATASET TRIGGER {0} SET ON {1}"), new Object[] {triggerOptionsString, reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE SETTING DATASET");
    }

    /**
     *
     * @param reference
     * @param integrityPeriodString
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String setIntegrityReport(String reference, String integrityPeriodString) throws ServiceError, IOException {

        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            rcb.getIntgPd().setValue(Long.parseLong(integrityPeriodString));
            List<ServiceError> serviceErrors = association.setRcbValues(rcb, false, false, false, false, false, true, false, false);
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("INTEGRITY {0} SET ON {1}"), new Object[] {integrityPeriodString, reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE CHANGING INTEGRITY");
    }

    /**
     *
     * @param reference
     * @return
     * @throws ServiceError
     * @throws IOException
     */
    public String sendGeneralInterrogationReport(String reference) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        System.out.print(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("SENDING GI.."));
        if (rcb != null) {
            association.startGi(rcb);
            return java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("GENERAL INTERROGATION SENT ON{0}"), new Object[] {reference});
        }
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("REPORT NOT FOUND, ERROR WHILE SENDING GI");
    }

    /**
     *
     * @param reference
     * @param fcString
     * @return
     */
    public FcModelNode askForFcModelNode(String reference, String fcString) {
        Fc fc = Fc.fromString(fcString);

        ModelNode modelNode = server.serverModel.findModelNode(reference, Fc.fromString(fcString));
        if (modelNode == null) {
            System.err.println(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("MODELNODE NOT FOUND"));
        }

        if (!(modelNode instanceof FcModelNode)) {
            System.err.println(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("THE GIVEN MODEL NODE IS NOT A FUNCTIONALLY CONSTRAINT MODEL NODE."));
        }

        FcModelNode fcModelNode = (FcModelNode) modelNode;
        return fcModelNode;
    }

    /**
     *
     * @return
     */
    public String quit() {
        System.out.println(java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("** CLOSING CONNECTION"));
        association.close();
        return java.util.ResourceBundle.getBundle("serverguiiec61850/Bundle").getString("SERVER STOPPED");
    }
}
