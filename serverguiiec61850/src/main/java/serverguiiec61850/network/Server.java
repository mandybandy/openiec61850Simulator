package serverguiiec61850.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaCheck;
import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaFloat64;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt32U;
import com.beanit.openiec61850.BdaInt64;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaInt8U;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static serverguiiec61850.gui.Gui.main;

/**
 *
 * @author Philipp Mandl
 */
public class Server {

    /**
     *
     * @param icdpath
     * @param args
     * @throws IOException
     */
    private ServerSap serverSap;

    /**
     *
     */
    public static ServerModel serverModel;

    private static final Logger LOGGER_SERVER = LoggerFactory.getLogger(Server.class);

    /**
     *
     */
    /**
     *
     * @param sclPath
     * @param portServer
     * @throws java.io.IOException
     * @throws com.beanit.openiec61850.SclParseException
     */
    public Server(String sclPath, int portServer) throws IOException, SclParseException {
        serverSap = null;

        List<ServerModel> serverModels = null;
        try {
            serverModels = SclParser.parse(sclPath);
        } catch (SclParseException e) {
            LOGGER_SERVER.error("file failed parsing.");
        }
        LOGGER_SERVER.info("file passed parsing.");

        serverSap = new ServerSap(portServer, 0, null, serverModels.get(0), null);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (serverSap != null) {
                    serverSap.stop();
                }
                LOGGER_SERVER.info("Server was stopped.");
                main.setEnabledAt(1, false);
                main.setEnabledAt(2, false);
            }
        });

        serverModel = serverSap.getModelCopy();
        serverSap.startListening(new EventListener());
        LOGGER_SERVER.info("server started.");

    }

    private class EventListener implements ServerEventListener {

        @Override
        public void serverStoppedListening(ServerSap serverSap) {
            LOGGER_SERVER.info("The SAP stopped listening");
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
     *
     * @param reference
     * @param fcString
     * @param valueString
     */
    public void writeValue(String reference, String fcString, String valueString) {
        Fc fc = Fc.fromString(fcString);
        if (fc == null) {
            LOGGER_SERVER.info("Unknown functional constraint.");
            return;
        }

        ModelNode modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
        if (modelNode == null) {
            LOGGER_SERVER.info("A model node with the given reference and functional constraint could not be found.");
            return;
        }

        if (!(modelNode instanceof BasicDataAttribute)) {
            LOGGER_SERVER.info("The given model node is not a basic data attribute.");
            return;
        }

        BasicDataAttribute bda = (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));

        setBdaValue(bda, valueString);

        List<BasicDataAttribute> bdas = new ArrayList<>();
        bdas.add(bda);
        serverSap.setValues(bdas);

        LOGGER_SERVER.info("Successfully wrote data.");
        System.out.println(bda);
        LOGGER_SERVER.info(bda.toString());
    }

    private void setBdaValue(BasicDataAttribute bda, String valueString) {
        if (bda instanceof BdaFloat32) {
            float value = Float.parseFloat(valueString);
            ((BdaFloat32) bda).setFloat(value);
            LOGGER_SERVER.debug("BdaFloat32");
        } else if (bda instanceof BdaVisibleString) {
            String value = (valueString);
            ((BdaVisibleString) bda).setValue(value);
            LOGGER_SERVER.debug("BdaVisibleString");
        } else if (bda instanceof BdaFloat64) {
            double value = Float.parseFloat(valueString);
            ((BdaFloat64) bda).setDouble(value);
            LOGGER_SERVER.debug("BdaFloat64");
        } else if (bda instanceof BdaInt8) {
            byte value = Byte.parseByte(valueString);
            ((BdaInt8) bda).setValue(value);
            LOGGER_SERVER.debug("BdaInt8");
        } else if (bda instanceof BdaInt8U) {
            short value = Short.parseShort(valueString);
            ((BdaInt8U) bda).setValue(value);
            LOGGER_SERVER.debug("BdaInt8U");
        } else if (bda instanceof BdaInt16) {
            short value = Short.parseShort(valueString);
            ((BdaInt16) bda).setValue(value);
            LOGGER_SERVER.debug("BdaInt16");
        } else if (bda instanceof BdaInt16U) {
            int value = Integer.parseInt(valueString);
            ((BdaInt16U) bda).setValue(value);
            LOGGER_SERVER.debug("BdaInt16U");
        } else if (bda instanceof BdaInt32) {
            int value = Integer.parseInt(valueString);
            ((BdaInt32) bda).setValue(value);
            LOGGER_SERVER.debug("BdaInt32");
        } else if (bda instanceof BdaInt32U) {
            long value = Long.parseLong(valueString);
            ((BdaInt32U) bda).setValue(value);
            LOGGER_SERVER.debug("BdaInt32U");
        } else if (bda instanceof BdaInt64) {
            long value = Long.parseLong(valueString);
            ((BdaInt64) bda).setValue(value);
            LOGGER_SERVER.debug("BdaInt64");
        } else if (bda instanceof BdaBoolean) {
            boolean value = Boolean.parseBoolean(valueString);
            ((BdaBoolean) bda).setValue(value);
            LOGGER_SERVER.debug("BdaFloat32");
        } else if (bda instanceof BdaCheck) {
            boolean value = Boolean.parseBoolean(valueString);
            ((BdaBoolean) bda).setValue(value);
            LOGGER_SERVER.debug("BdaFloat32");
        } else {
            LOGGER_SERVER.info("datatypes are not the same");
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     */
    public void quit() {
        LOGGER_SERVER.info("** Stopping server.");
        serverSap.stop();
    }
}
