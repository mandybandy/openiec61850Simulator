package serverguiiec61850;

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
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServiceError;
import com.beanit.openiec61850.Urcb;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Philipp
 */
public class Client {

    /**
     *
     */
    public static volatile ClientAssociation association;
    public static ServerModel serverModel;

    /**
     *
     * @param host
     * @param port
     */
    public Client(String host, int port) {

        InetAddress address;
        Exception er;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + host);
            return;
        }

        ClientSap clientSap = new ClientSap();

        try {
            association = clientSap.associate(address, port, null, null);
        } catch (IOException e) {
            System.out.println("Unable to connect to remote host.");
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                association.close();
            }
        });

        System.out.println("successfully connected");

        System.out.println("reading model from file...");

        try {
            serverModel = SclParser.parse(serverguiiec61850.gui.gui.iedPath).get(0);
        } catch (SclParseException e1) {
            System.out.println("Error parsing SCL file: " + e1.getMessage());
            er = e1;
            return;
        }

        association.setServerModel(serverModel);

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
        System.out.println(serverModel);
    }

    /**
     *
     * @throws IOException
     */
    public void readalldata() throws IOException {
        System.out.print("Reading all data...");
        try {
            association.getAllDataValues();
        } catch (ServiceError e) {
            System.err.println("Service error: " + e.getMessage());
        }
        System.out.println("done");
    }

    /**
     *
     * @param reference
     * @param fcString
     */
    public void getdata(String reference, String fcString) {
        if (serverModel == null) {
            System.out.println("You have to retrieve the model before reading data.");
            return;
        }

        FcModelNode fcModelNode = askForFcModelNode(reference, fcString);

        System.out.println("Sending GetDataValues request...");

        try {
            association.getDataValues(fcModelNode);
        } catch (ServiceError e) {
            System.out.println("Service error: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.out.println("Fatal error: " + e.getMessage());
            return;
        }

        System.out.println("Successfully read data.");
        System.out.println(fcModelNode);
    }

    /**
     *
     * @param reference
     * @param fcString
     * @param numberOfEntriesString
     */
    public void createdataset(String reference, String fcString, String numberOfEntriesString) {
        try {
            int numDataSetEntries = Integer.parseInt(numberOfEntriesString);

            List<FcModelNode> dataSetMembers = new ArrayList<>();
            for (int i = 0; i < numDataSetEntries; i++) {
                dataSetMembers.add(askForFcModelNode(reference, fcString));
            }

            DataSet dataSet = new DataSet(reference, dataSetMembers);
            System.out.print("Creating data set..");
            association.createDataSet(dataSet);
            System.out.println("done");
        } catch (ServiceError ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param reference
     */
    public void deletedataset(String reference) {
        DataSet dataSet = serverModel.getDataSet(reference);
        if (dataSet == null) {
            //gibs nd
        }
        System.out.print("Deleting data set..");
        try {
            association.deleteDataSet(dataSet);
        } catch (ServiceError | IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("done");
    }

    /**
     *
     * @param reference
     * @return 
     */
    public Urcb getUrcb(String reference) {

        Urcb urcb = serverModel.getUrcb(reference);
        if (urcb != null) {
            try {
                association.getRcbValues(urcb);
                return urcb;
            } catch (ServiceError ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     *
     * @param reference
     * @return
     */
    public Brcb getBrcb(String reference) {

        Brcb brcb = serverModel.getBrcb(reference);
        if (brcb != null) {
            try {
                association.getRcbValues(brcb);
                return brcb;
            } catch (ServiceError ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     *
     * @param reference
     * @return
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
     *
     * @param reference
     * @param time
     * @throws ServiceError
     * @throws IOException
     */
    public void reserveReport(String reference, short time) throws ServiceError, IOException {
        System.out.print("Reserving RCB..");
        if (getUrcb(reference) != null) {
            association.reserveUrcb(getUrcb(reference));
        } else if (getBrcb(reference) != null) {
            association.reserveBrcb(getBrcb(reference), time);
        }
    }

    /**
     *
     * @param reference
     * @throws ServiceError
     * @throws IOException
     */
    public void cancelReservation(String reference) throws ServiceError, IOException {
        System.out.print("Canceling RCB reservation..");
        association.cancelUrcbReservation(getUrcb(reference));
        System.out.println("done");
    }

    /**
     *
     * @param reference
     * @throws ServiceError
     * @throws IOException
     */
    public void enableReport(String reference) throws ServiceError, IOException {
        System.out.print("Enabling reporting..");
        association.enableReporting(getRcb(reference));
        System.out.println("done");
    }

    /**
     *
     * @param reference
     * @throws ServiceError
     * @throws IOException
     */
    public void disableReport(String reference) throws ServiceError, IOException {
        System.out.print("Disabling reporting..");
        association.disableReporting(getRcb(reference));
        System.out.println("done");
    }

    /**
     *
     * @param reference
     * @param datasetValue
     * @throws ServiceError
     * @throws IOException
     */
    public void setTriggerReport(String reference, String datasetValue) throws ServiceError, IOException {
        getRcb(reference).getDatSet().setValue(datasetValue);
        List<ServiceError> serviceErrors = null;
        try {
            serviceErrors = association.setRcbValues(getRcb(reference), false, true, false, false, false, false, false, false);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (null != serviceErrors.get(0)) {
            throw serviceErrors.get(0);
        }
        System.out.println("done");
    }

    /**
     *
     * @param reference
     * @param triggerOptionsString
     * @throws ServiceError
     * @throws IOException
     */
    public void setDatasetReport(String reference, String triggerOptionsString) throws ServiceError, IOException {
        String[] triggerOptionsStrings = triggerOptionsString.split(",");
        BdaTriggerConditions triggerOptions = getRcb(reference).getTrgOps();
        triggerOptions.setDataChange(Boolean.parseBoolean(triggerOptionsStrings[0]));
        triggerOptions.setDataUpdate(Boolean.parseBoolean(triggerOptionsStrings[1]));
        triggerOptions.setQualityChange(Boolean.parseBoolean(triggerOptionsStrings[2]));
        triggerOptions.setIntegrity(Boolean.parseBoolean(triggerOptionsStrings[3]));
        triggerOptions.setGeneralInterrogation(Boolean.parseBoolean(triggerOptionsStrings[4]));
        List<ServiceError> serviceErrors= association.setRcbValues(getRcb(reference), false, false, false, false, true, false, false, false);
        if (serviceErrors.get(0) != null) {
            throw serviceErrors.get(0);
        }
        System.out.println("done");
    }

    /**
     *
     * @param reference
     * @param integrityPeriodString
     * @throws ServiceError
     * @throws IOException
     */
    public void setIntegrityReport(String reference, String integrityPeriodString) throws ServiceError, IOException {
        try{
        getRcb(reference).getIntgPd().setValue(Long.parseLong(integrityPeriodString));
        List<ServiceError> serviceErrors = association.setRcbValues(getRcb(reference), false, false, false, false, false, true, false, false);
        System.out.println("done");
        }catch(NumberFormatException e){
            System.out.println("can not convert text to number");
        }
    }

    /**
     *
     * @param reference
     * @throws ServiceError
     * @throws IOException
     */
    public void sendGeneralInterrogationReport(String reference) throws ServiceError, IOException {
        System.out.print("Sending GI..");
        try {
            association.startGi(getRcb(reference));
            System.out.println("done");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }      
    }

    /**
     *
     * @param reference
     * @param fcString
     * @return
     */
    public FcModelNode askForFcModelNode(String reference, String fcString) {
        Fc fc = Fc.fromString(fcString);

        ModelNode modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
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
