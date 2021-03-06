/**
 * @project IEC61850 simulator
 * @date 10.03.2020
 * @path serverguiiec61850.network.Server.java
 * @author Philipp Mandl
 */
package guiIec61850.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBitString;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaCheck;
import com.beanit.openiec61850.BdaDoubleBitPos;
import com.beanit.openiec61850.BdaEntryTime;
import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaFloat64;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt32U;
import com.beanit.openiec61850.BdaInt64;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaInt8U;
import com.beanit.openiec61850.BdaOctetString;
import com.beanit.openiec61850.BdaOptFlds;
import com.beanit.openiec61850.BdaQuality;
import com.beanit.openiec61850.BdaReasonForInclusion;
import com.beanit.openiec61850.BdaTapCommand;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaTriggerConditions;
import com.beanit.openiec61850.BdaUnicodeString;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.Fc;
import static com.beanit.openiec61850.Fc.fromString;
import com.beanit.openiec61850.FcModelNode;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.SclParseException;
import static com.beanit.openiec61850.SclParser.parse;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;
import org.slf4j.Logger;
import static guiIec61850.gui.Gui.mainFrame;
import static guiIec61850.network.Client.association;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Byte.parseByte;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Runtime.getRuntime;
import static java.lang.Short.parseShort;
import static org.slf4j.LoggerFactory.getLogger;
//import serverguiiec61850.gui.SetIedVal;

/**
 * server
 */
public class Server {

    /**
     * ServerSAP
     */
    public ServerSap serverSap;

    /**
     * server Model including all Nodes, Data etc
     */
    public ServerModel serverModel;

    private static final Logger LOGGER_SERVER = getLogger(Server.class);

    /**
     * creates server
     *
     * @param sclPath string sclpath
     * @param portServer int port
     * @param ied string ied name
     * @throws java.io.IOException io error
     * @throws com.beanit.openiec61850.SclParseException parse error
     */
    public Server(String sclPath, int portServer, String ied) throws IOException, SclParseException, NullPointerException {
        serverSap = null;

        List<ServerModel> serverModels = null;
        try {
            serverModels = parse(sclPath);

        }
        catch (SclParseException e) {
            LOGGER_SERVER.error("file failed parsing.");
        }
        LOGGER_SERVER.info("file passed parsing.");
        serverSap = new ServerSap(portServer, 0, null, serverModels.get(0), null);

        getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (serverSap != null) {
                    serverSap.stop();
                }
                LOGGER_SERVER.info("Server was stopped.");
                mainFrame.setEnabledAt(1, false);
                mainFrame.setEnabledAt(2, false);
            }
        });
        this.serverModel = serverSap.getModelCopy();
        serverSap.startListening(new EventListener());
        //SetIedVal set = new SetIedVal(this.serverModel, this.ied, this);
        LOGGER_SERVER.info("server started.");
    }

    private class EventListener implements ServerEventListener {

        @Override
        public void serverStoppedListening(ServerSap serverSap) {
            LOGGER_SERVER.info("The server stopped listening");
        }

        @Override
        public List<ServiceError> write(List<BasicDataAttribute> bdas) {
            bdas.forEach((bda) -> {
                LOGGER_SERVER.info("got a write request: " + bda);
            });
            return null;
        }
    }

    /**
     * writes values on server
     *
     * @param reference string reference
     * @param fcString string fc
     * @param valueString string value
     * @throws java.io.IOException io error
     */
    public void writeValue(String reference, String fcString, String valueString) throws IOException, IllegalArgumentException {
        Fc fc = fromString(fcString);
        if (fc == null) {
            LOGGER_SERVER.info("Unknown functional constraint.");
            return;
        }

        ModelNode modelNode = this.serverModel.findModelNode(reference, fromString(fcString));
        if (modelNode == null) {
            LOGGER_SERVER.info("A model node with the given reference and functional constraint could not be found.");
            return;
        }

        if (!(modelNode instanceof BasicDataAttribute)) {
            LOGGER_SERVER.info("The given model node is not a basic data attribute.");
            return;
        }

        BasicDataAttribute bda = (BasicDataAttribute) this.serverModel.findModelNode(reference, fromString(fcString));
        setBdaValue(bda, valueString);

        List<BasicDataAttribute> bdas = new ArrayList<>();
        bdas.add(bda);
        serverSap.setValues(bdas);

        try {
            association.setDataValues(((FcModelNode) bda.getParent()));
        }
        catch (ServiceError e) {
            LOGGER_SERVER.error("error", e);
        }

        LOGGER_SERVER.debug("successfully wrote data.");
        LOGGER_SERVER.debug(bda.toString());
    }

//sets the value on data type
    private void setBdaValue(BasicDataAttribute bda, String valueString) {
        try {
            if (bda instanceof BdaFloat32) {
                float value = parseFloat(valueString);
                ((BdaFloat32) bda).setFloat(value);
                LOGGER_SERVER.debug("BdaFloat32");
            } else if (bda instanceof BdaVisibleString) {
                String value = (valueString);
                ((BdaVisibleString) bda).setValue(value);
                LOGGER_SERVER.debug("BdaVisibleString");
            } else if (bda instanceof BdaFloat64) {
                double value = parseFloat(valueString);
                ((BdaFloat64) bda).setDouble(value);
                LOGGER_SERVER.debug("BdaFloat64");
            } else if (bda instanceof BdaInt8) {
                byte value = parseByte(valueString);
                ((BdaInt8) bda).setValue(value);
                LOGGER_SERVER.debug("BdaInt8");
            } else if (bda instanceof BdaInt8U) {
                short value = parseShort(valueString);
                ((BdaInt8U) bda).setValue(value);
                LOGGER_SERVER.debug("BdaInt8U");
            } else if (bda instanceof BdaInt16) {
                short value = parseShort(valueString);
                ((BdaInt16) bda).setValue(value);
                LOGGER_SERVER.debug("BdaInt16");
            } else if (bda instanceof BdaInt16U) {
                int value = parseInt(valueString);
                ((BdaInt16U) bda).setValue(value);
                LOGGER_SERVER.debug("BdaInt16U");
            } else if (bda instanceof BdaInt32) {
                int value = parseInt(valueString);
                ((BdaInt32) bda).setValue(value);
                LOGGER_SERVER.debug("BdaInt32");
            } else if (bda instanceof BdaInt32U) {
                long value = parseLong(valueString);
                ((BdaInt32U) bda).setValue(value);
                LOGGER_SERVER.debug("BdaInt32U");
            } else if (bda instanceof BdaInt64) {
                long value = parseLong(valueString);
                ((BdaInt64) bda).setValue(value);
                LOGGER_SERVER.debug("BdaInt64");
            } else if (bda instanceof BdaBoolean) {
                boolean value = parseBoolean(valueString);
                ((BdaBoolean) bda).setValue(value);
                LOGGER_SERVER.debug("BdaFloat32");
            } else if (bda instanceof BdaCheck) {
                boolean value = parseBoolean(valueString);
                ((BdaBoolean) bda).setValue(value);
                LOGGER_SERVER.debug("BdaFloat32");
            } else if (bda instanceof BdaOctetString) {
                byte[] value = valueString.getBytes();
                ((BdaOctetString) bda).setValue(value);
                LOGGER_SERVER.debug("BdaOctetString");
            } else if (bda instanceof BdaTimestamp) {
                byte[] value = valueString.getBytes();
                ((BdaTimestamp) bda).setValue(value);
                LOGGER_SERVER.debug("BdaTimestamp");
            } else if (bda instanceof BdaUnicodeString) {
                byte[] value = valueString.getBytes();
                ((BdaUnicodeString) bda).setValue(value);
                LOGGER_SERVER.debug("BdaUnicodeString");
            } else if (bda instanceof BdaEntryTime) {
                byte[] value = valueString.getBytes();
                ((BdaEntryTime) bda).setValue(value);
                LOGGER_SERVER.debug("BdaEntryTime");
            } else if (bda instanceof BdaDoubleBitPos) {
                byte[] value = valueString.getBytes();
                ((BdaDoubleBitPos) bda).setValue(value);
                LOGGER_SERVER.debug("BdaDoubleBitPos");
            } else if (bda instanceof BdaOptFlds) {
                byte[] value = valueString.getBytes();
                ((BdaOptFlds) bda).setValue(value);
                LOGGER_SERVER.debug("BdaOptFlds");
            } else if (bda instanceof BdaQuality) {
                byte[] value = valueString.getBytes();
                ((BdaQuality) bda).setValue(value);
                LOGGER_SERVER.debug("BdaQuality");
            } else if (bda instanceof BdaReasonForInclusion) {
                byte[] value = valueString.getBytes();
                ((BdaReasonForInclusion) bda).setValue(value);
                LOGGER_SERVER.debug("BdaReasonForInclusion");
            } else if (bda instanceof BdaTapCommand) {
                byte[] value = valueString.getBytes();
                ((BdaTapCommand) bda).setValue(value);
                LOGGER_SERVER.debug("BdaTapCommand");
            } else if (bda instanceof BdaTriggerConditions) {
                byte[] value = valueString.getBytes();
                ((BdaTriggerConditions) bda).setValue(value);
                LOGGER_SERVER.debug("BdaTriggerConditions");
            } else if (bda instanceof BdaBitString) {
                byte[] value = valueString.getBytes();
                ((BdaBitString) bda).setValue(value);
                LOGGER_SERVER.debug("BdaBitString");

            } else {
                LOGGER_SERVER.error("datatypes are not the same");
                throw new IllegalArgumentException();
            }

        }
        catch (NumberFormatException e) {
            LOGGER_SERVER.error("invalid entry for " + bda.getName());
        }
    }

    /**
     * returns server model
     *
     * @return ServerModel server model
     */
    public ServerModel getModel() {
        return this.serverModel;
    }

    /**
     * stops server
     */
    public void quit() {
        LOGGER_SERVER.info("** Stopping server.");
        serverSap.stop();
    }
}
