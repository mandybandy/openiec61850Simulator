/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.network.Client.java
 * @author Philipp Mandl
 */
package guiIec61850.network;

import com.beanit.openiec61850.BdaTriggerConditions;
import com.beanit.openiec61850.Brcb;
import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.ClientSap;
import com.beanit.openiec61850.DataSet;
import static com.beanit.openiec61850.Fc.fromString;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.Rcb;
import com.beanit.openiec61850.SclParseException;
import static com.beanit.openiec61850.SclParser.parse;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServiceError;
import com.beanit.openiec61850.Urcb;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import static guiIec61850.gui.Gui.iedPath;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Runtime.getRuntime;
import static java.net.InetAddress.getByName;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * client
 */
public class Client {

    private final ServerModel serverModel;

    /**
     * clientinformations
     */
    public static volatile ClientAssociation association;

    private static final Logger LOGGER_CLIENT = getLogger(Client.class);

    /**
     * creates client
     *
     * @param host string host ip
     * @param port int port
     * @param serverModel server model
     * @throws java.net.UnknownHostException unknown host
     * @throws java.io.IOException io error
     * @throws com.beanit.openiec61850.SclParseException parse error
     * @throws java.net.ConnectException connection refused
     */
    public Client(String host, int port, ServerModel serverModel) throws UnknownHostException, IOException, SclParseException, java.net.ConnectException {
        this.serverModel = serverModel;

        InetAddress address;
        if ("localhost".equals(host)) {
            host = "127.0.0.1";
        }
        address = getByName(host);

        ClientSap clientSap = new ClientSap();

        association = clientSap.associate(address, port, null, null);

        getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                association.close();
            }
        });
        LOGGER_CLIENT.info("successfully connected");

        LOGGER_CLIENT.info("reading model from file...");
        if (serverModel != null) {
            serverModel = parse(iedPath).get(0);
            association.setServerModel(serverModel);
            LOGGER_CLIENT.debug("successfully read model");
        } else {
            LOGGER_CLIENT.debug("failed reading model");
        }

    }

    /**
     * creates dataset
     *
     * @param reference string reference
     * @param fcString string fc
     * @param numberOfEntriesString string number of entries
     * @throws com.beanit.openiec61850.ServiceError service error
     * @throws java.io.IOException io error
     */
    //ToDo: funktioniert ned mit den unteren Elementen not in function
    public void createdataset(String reference, String fcString, String numberOfEntriesString) throws ServiceError, IOException {
        int numDataSetEntries = parseInt(numberOfEntriesString);

        List<FcModelNode> dataSetMembers = new ArrayList<>();
        for (int datasetCounter = 0; datasetCounter < numDataSetEntries; datasetCounter++) {
            dataSetMembers.add(askForFcModelNode(reference, fcString));
        }
        //if a member is empty
        for (int i = 0; i < dataSetMembers.size(); i++) {
            if (dataSetMembers.get(i) == null) {
                LOGGER_CLIENT.debug("a member is not defined");
            }
        }
        DataSet dataSet = new DataSet(reference, dataSetMembers);
        LOGGER_CLIENT.debug("creating data set..");
        association.createDataSet(dataSet);
        LOGGER_CLIENT.debug("created dataset");
    }

    /**
     * deletes dataset
     *
     * @param reference string reference
     * @throws com.beanit.openiec61850.ServiceError service error
     * @throws java.io.IOException io error
     */
    public void deletedataset(String reference) throws ServiceError, IOException {
        DataSet dataSet = serverModel.getDataSet(reference);
        LOGGER_CLIENT.debug("deleting data set..");

        if (dataSet == null) {
            //gibs nd
            LOGGER_CLIENT.error("dataset not found error while deleting dataset");
        }
        association.deleteDataSet(dataSet);

        LOGGER_CLIENT.debug("deleted dataset");
    }

    //get unbuffered rcb
    private Urcb getUrcb(String reference) throws ServiceError, IOException {

        Urcb urcb = serverModel.getUrcb(reference);
        if (urcb != null) {
            association.getRcbValues(urcb);
            return urcb;
        }
        return null;
    }

    //get buffered RCB
    private Brcb getBrcb(String reference) throws ServiceError, IOException {

        Brcb brcb = serverModel.getBrcb(reference);
        if (brcb != null) {
            association.getRcbValues(brcb);
            return brcb;
        }
        return null;
    }

    /**
     * returns report control block
     *
     * @param reference string reference
     * @return Rcb report control block
     */
    public Rcb getRcb(String reference) {
        Brcb brcb = serverModel.getBrcb(reference);
        Urcb urcb = serverModel.getUrcb(reference);
        if (urcb != null) {
            Rcb rcb = serverModel.getUrcb(reference);
            return rcb;
        } else if (brcb != null) {
            Rcb rcb = serverModel.getBrcb(reference);
            return rcb;
        }
        return null;
    }

    /**
     * reserve report
     *
     * @param reference string reference
     * @param time short reserve time
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void reserveReport(String reference, short time) throws ServiceError, IOException {
        LOGGER_CLIENT.debug("Reserving RCB..");
        if (getUrcb(reference) != null) {
            association.reserveUrcb(getUrcb(reference));
            LOGGER_CLIENT.info("reserved unbuffered report: " + reference);
        } else if (getBrcb(reference) != null) {
            association.reserveBrcb(getBrcb(reference), time);
            LOGGER_CLIENT.info("reserved buffered report: " + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while reserving report");
        }
    }

    /**
     * cancel reservation
     *
     * @param reference string reference
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void cancelReservation(String reference) throws ServiceError, IOException {
        LOGGER_CLIENT.debug("Canceling RCB reservation..");
        Urcb urcb = getUrcb(reference);
        if (urcb != null) {
            association.cancelUrcbReservation(urcb);
            LOGGER_CLIENT.debug("canceld reservation: " + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while cancelling reservation");
        }
    }

    /**
     * enable report
     *
     * @param reference string reference
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void enableReport(String reference) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            LOGGER_CLIENT.debug("Enabling reporting..");
            association.enableReporting(getRcb(reference));
            LOGGER_CLIENT.debug("report enabled: " + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while enabling report");
        }
    }

    /**
     * disable report
     *
     * @param reference string reference
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void disableReport(String reference) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            LOGGER_CLIENT.debug("Disabling reporting..");
            association.disableReporting(getRcb(reference));
            LOGGER_CLIENT.debug("report disabled: " + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while disabling report");
        }
    }

    /**
     * set trigger on report
     *
     * @param datasetValue data set value
     * @param reference string reference
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void setDatasetReport(String reference, String datasetValue) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            rcb.getDatSet().setValue(datasetValue);
            List<ServiceError> serviceErrors = null;
            serviceErrors = association.setRcbValues(rcb, false, true, false, false, false, false, false, false);
            LOGGER_CLIENT.debug("value " + datasetValue + " set on: " + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while set dataset");
        }
    }

    /**
     * set dataset
     *
     * @param reference string reference
     * @param triggerOptionsString string trigger options
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void setTriggerReport(String reference, String triggerOptionsString) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        if (rcb != null) {
            String[] triggerOptionsStrings = triggerOptionsString.split(",");
            BdaTriggerConditions triggerOptions = rcb.getTrgOps();
            triggerOptions.setDataChange(parseBoolean(triggerOptionsStrings[0]));
            triggerOptions.setDataUpdate(parseBoolean(triggerOptionsStrings[1]));
            triggerOptions.setQualityChange(parseBoolean(triggerOptionsStrings[2]));
            triggerOptions.setIntegrity(parseBoolean(triggerOptionsStrings[3]));
            triggerOptions.setGeneralInterrogation(parseBoolean(triggerOptionsStrings[4]));
            List<ServiceError> serviceErrors = association.setRcbValues(rcb, false, false, false, false, true, false, false, false);
            if (serviceErrors.get(0) != null) {
                throw serviceErrors.get(0);
            }
            LOGGER_CLIENT.debug("dataset trigger " + triggerOptionsString + " set on " + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while set trigger");
        }
    }

    /**
     * set integrity on report
     *
     * @param reference string reference
     * @param integrityPeriodString string integrity
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void setIntegrityReport(String reference, String integrityPeriodString) throws ServiceError, IOException, NumberFormatException {

        Rcb rcb = getRcb(reference);
        if (rcb != null) {

            rcb.getIntgPd().setValue(parseLong(integrityPeriodString));
            List<ServiceError> serviceErrors = association.setRcbValues(rcb, false, false, false, false, false, true, false, false);

            LOGGER_CLIENT.debug("integrity " + integrityPeriodString + " set on " + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while changing integrity");
        }
    }

    /**
     * send GI (general interrogation)
     *
     * @param reference string reference
     * @throws ServiceError service error
     * @throws IOException io error
     */
    public void sendGeneralInterrogationReport(String reference) throws ServiceError, IOException {
        Rcb rcb = getRcb(reference);
        LOGGER_CLIENT.debug("Sending GI..");
        if (rcb != null) {
            association.startGi(rcb);
            LOGGER_CLIENT.debug("general interrogation sent on" + reference);
        } else {
            LOGGER_CLIENT.error("report not found, error while sending GI");
        }
    }

    private FcModelNode askForFcModelNode(String reference, String fcString) {

        ModelNode modelNode = serverModel.findModelNode(reference, fromString(fcString));
        if (modelNode == null) {
            LOGGER_CLIENT.error("modelNode not found");
        }

        if (!(modelNode instanceof FcModelNode)) {
            LOGGER_CLIENT.error("The given model node is not a functionally constraint model node.");
        }

        FcModelNode fcModelNode = (FcModelNode) modelNode;
        return (fcModelNode);
    }

    /**
     * stops client
     */
    public void quit() {
        LOGGER_CLIENT.debug("** stopping client");
        association.close();
        association = null;
        LOGGER_CLIENT.info("client stopped");
    }
}
