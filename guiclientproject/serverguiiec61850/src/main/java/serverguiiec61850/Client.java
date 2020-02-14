/*
 * Copyright 2011 The OpenIEC61850 Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package serverguiiec61850;

import com.beanit.openiec61850.BdaTriggerConditions;
import com.beanit.openiec61850.Brcb;
import com.beanit.openiec61850.ClientAssociation;
import com.beanit.openiec61850.ClientSap;
import com.beanit.openiec61850.DataSet;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.Report;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServiceError;
import com.beanit.openiec61850.Urcb;
import com.beanit.openiec61850.internal.cli.ActionException;
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
    private static ServerModel serverModel;

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
            er=e1;
            return;
        }

        association.setServerModel(serverModel);

        System.out.println("successfully read model");

    }


    /**
     *
     * @param report
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
     * @param rcbAction
     * @param dataSetReference
     * @param triggerOptionsString
     * @param integrityPeriodString
     */
    public void report(String reference, int rcbAction, String dataSetReference, String triggerOptionsString, String integrityPeriodString) {
        Urcb urcb = serverModel.getUrcb(reference);
        if (urcb == null) {
            Brcb brcb = serverModel.getBrcb(reference);
            if (brcb != null) {
                try {
                    throw new ActionException(
                            "Though buffered reporting is supported by the library it is not yet supported by the console application.");
                } catch (ActionException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        try {
            association.getRcbValues(urcb);
            System.out.println();
            System.out.println(urcb);
            System.out.println();
            System.out.println("What do you want to configure?");
            System.out.println("1 - reserve");
            System.out.println("2 - cancel reservation");
            System.out.println("3 - enable");
            System.out.println("4 - disable");
            System.out.println("5 - set data set");
            System.out.println("6 - set trigger options");
            System.out.println("7 - set integrity period");
            System.out.println("8 - send general interrogation");
            System.out.println("0 - quit");
            try {
                switch (rcbAction) {
                    case 0:
                        return;
                    case 1:
                        System.out.print("Reserving RCB..");
                        association.reserveUrcb(urcb);
                        System.out.println("done");
                        break;
                    case 2:
                        System.out.print("Canceling RCB reservation..");
                        association.cancelUrcbReservation(urcb);
                        System.out.println("done");
                        break;
                    case 3:
                        System.out.print("Enabling reporting..");
                        association.enableReporting(urcb);
                        System.out.println("done");
                        break;
                    case 4:
                        System.out.print("Disabling reporting..");
                        association.disableReporting(urcb);
                        System.out.println("done");
                        break;
                    case 5: {
                        urcb.getDatSet().setValue(dataSetReference);
                        List<ServiceError> serviceErrors = null;
                        try {
                            serviceErrors = association.setRcbValues(urcb, false, true, false, false, false, false, false, false);
                        } catch (IOException ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (null != serviceErrors.get(0)) {
                            throw serviceErrors.get(0);
                        }
                        System.out.println("done");
                        break;
                    }

                    case 6: {
                        String[] triggerOptionsStrings = triggerOptionsString.split(",");
                        BdaTriggerConditions triggerOptions = urcb.getTrgOps();
                        triggerOptions.setDataChange(Boolean.parseBoolean(triggerOptionsStrings[0]));
                        triggerOptions.setDataUpdate(Boolean.parseBoolean(triggerOptionsStrings[1]));
                        triggerOptions.setQualityChange(Boolean.parseBoolean(triggerOptionsStrings[2]));
                        triggerOptions.setIntegrity(Boolean.parseBoolean(triggerOptionsStrings[3]));
                        triggerOptions.setGeneralInterrogation(Boolean.parseBoolean(triggerOptionsStrings[4]));
                        List<ServiceError> serviceErrors
                                = association.setRcbValues(
                                        urcb, false, false, false, false, true, false, false, false);
                        if (serviceErrors.get(0) != null) {
                            throw serviceErrors.get(0);
                        }
                        System.out.println("done");
                        break;
                    }
                    case 7: {
                        urcb.getIntgPd().setValue(Long.parseLong(integrityPeriodString));
                        List<ServiceError> serviceErrors
                                = association.setRcbValues(
                                        urcb, false, false, false, false, false, true, false, false);
                        if (serviceErrors.get(0) != null) {
                            throw serviceErrors.get(0);
                        }
                        System.out.println("done");
                        break;
                    }
                    case 8:
                        System.out.print("Sending GI..");
                        association.startGi(urcb);
                        System.out.println("done");
                        break;
                    default:
                        System.err.println("Unknown option.");
                        break;
                }
            } catch (ServiceError e) {
                System.err.println("Service error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.err.println("Cannot parse number: " + e.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ServiceError | IOException ex) {
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
    public static String quit() {
        System.out.println("** Closing connection");
        association.close();
        return "Server stopped";
    }
}
