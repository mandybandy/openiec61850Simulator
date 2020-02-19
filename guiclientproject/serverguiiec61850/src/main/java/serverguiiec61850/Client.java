package serverguiiec61850;

import com.beanit.openiec61850.BasicDataAttribute;
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



/**
 *
 * @author Philipp
 */
public class Client {

    private Server server;
    /**
     *
     */
    public static volatile ClientAssociation association;


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

        System.out.println("successfully connected");
        System.out.println("reading model from file...");
        server.serverModel = SclParser.parse(serverguiiec61850.gui.gui.iedPath).get(0);
        association.setServerModel(server.serverModel);
        System.out.println("successfully read model");
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
        System.out.print("Reading all data...");
        association.getAllDataValues();
        System.out.println("done");
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
            System.out.println("You have to retrieve the model before reading data.");
            return;
        }

        FcModelNode fcModelNode = askForFcModelNode(reference, fcString);

        System.out.println("Sending GetDataValues request...");
        association.getDataValues(fcModelNode);
        System.out.println("Successfully read data.");
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
            if (dataSetMembers.get(i)==null) {
                return "a member is not defined";
            }
        }
        DataSet dataSet = new DataSet(reference, dataSetMembers);
        System.out.print("Creating data set..");
        association.createDataSet(dataSet);
        return "created dataset";
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
            return "dataset not found error while deleting dataset";
        }
        System.out.print("Deleting data set..");
        association.deleteDataSet(dataSet);

        return "deleted dataset";
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
        System.out.print("Reserving RCB..");
        if (getUrcb(reference) != null) {
            association.reserveUrcb(getUrcb(reference));
            return "reserved unbuffered report: " + reference;
        } else if (getBrcb(reference) != null) {
            association.reserveBrcb(getBrcb(reference), time);
            return "reserved buffered report: " + reference;
        }
        return "report not found, error while reserving report";
    }

    /**
     *
     * @param reference
     * @return 
     * @throws ServiceError
     * @throws IOException
     */
    public String cancelReservation(String reference) throws ServiceError, IOException {
        System.out.print("Canceling RCB reservation..");
        Urcb urcb = getUrcb(reference);
        if (urcb != null) {
            association.cancelUrcbReservation(urcb);
            return "canceld reservation: " + reference;
        } 
            return "report not found, error while cancelling reservation";
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
            System.out.print("Enabling reporting..");
            association.enableReporting(getRcb(reference));
            return "report enabled: " + reference;
        }
        return "report not found, error while enabling report";
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
            System.out.print("Disabling reporting..");
            association.disableReporting(getRcb(reference));
            return "report disabled: " + reference;
        }
        return "report not found, error while disabling report";
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
            return "value " + datasetValue + " set on: " + reference;
        }
        return "report not found, error while setting trigger";
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
            String[] triggerOptionsStrings = triggerOptionsString.split(",");
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
            return "dataset trigger " + triggerOptionsString + " set on " + reference;
        }
        return "report not found, error while setting Dataset";
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
            return "integrity " + integrityPeriodString + " set on " + reference;
        }
        return "report not found, error while changing integrity";
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
        System.out.print("Sending GI..");
        if (rcb != null) {
            association.startGi(rcb);
            return "general interrogation sent on" + reference;
        }
        return "report not found, error while sending GI";
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
            System.err.println("modelNode not found");
        }

        if (!(modelNode instanceof FcModelNode)) {
            System.err.println("The given model node is not a functionally constraint model node.");
        }

        FcModelNode fcModelNode = (FcModelNode) modelNode;
        return fcModelNode;
    }

    /**
     *
     * @return
     */
    public String quit() {
        System.out.println("** Closing connection");
        association.close();
        return "Server stopped";
    }
}
