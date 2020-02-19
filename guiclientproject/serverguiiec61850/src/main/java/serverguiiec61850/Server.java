package serverguiiec61850;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaFloat64;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt32U;
import com.beanit.openiec61850.BdaInt64;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaInt8U;
import com.beanit.openiec61850.Fc;
import com.beanit.openiec61850.ModelNode;
import com.beanit.openiec61850.SclParseException;
import com.beanit.openiec61850.SclParser;
import com.beanit.openiec61850.ServerEventListener;
import com.beanit.openiec61850.ServerModel;
import com.beanit.openiec61850.ServerSap;
import com.beanit.openiec61850.ServiceError;

/**
 *
 * @author Philipp
 */
public class Server {

    /**
     *
     * @param icdpath
     * @param args
     * @throws IOException
     */
    private ServerSap serverSap;
    public static ServerModel serverModel;

    /**
     *
     */


    /**
     *
     * @param icdpath
     * @param portServer
     * @throws java.io.IOException
     * @throws com.beanit.openiec61850.SclParseException
     */
    public Server(String icdpath, int portServer) throws IOException, SclParseException {
        serverSap = null;

        List<ServerModel> serverModels = null;

        serverModels = SclParser.parse(icdpath);
        System.out.println("icd file passed parsing.");

        serverSap = new ServerSap(portServer, 0, null, serverModels.get(0), null);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (serverSap != null) {
                    serverSap.stop();
                }
                System.out.println("Server was stopped.");
            }
        });

        serverModel = serverSap.getModelCopy();
        serverSap.startListening(new EventListener());
        System.out.println("server started.");
        //return "Server started";

    }

    private class EventListener implements ServerEventListener {

        @Override
        public void serverStoppedListening(ServerSap serverSap) {
            System.out.println("The SAP stopped listening");
        }

        @Override
        public List<ServiceError> write(List<BasicDataAttribute> bdas) {
            bdas.forEach((bda) -> {
                System.out.println("got a write request: " + bda);
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
            System.out.println("Unknown functional constraint.");
            return;
        }

        ModelNode modelNode = serverModel.findModelNode(reference, Fc.fromString(fcString));
        if (modelNode == null) {
            System.out.println(
                    "A model node with the given reference and functional constraint could not be found.");
            return;
        }

        if (!(modelNode instanceof BasicDataAttribute)) {
            System.out.println("The given model node is not a basic data attribute.");
            return;
        }

        BasicDataAttribute bda = (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));

        setBdaValue(bda, valueString);

        List<BasicDataAttribute> bdas = new ArrayList<>();
        bdas.add(bda);
        serverSap.setValues(bdas);

        System.out.println("Successfully wrote data.");
        System.out.println(bda);
    }

    private void setBdaValue(BasicDataAttribute bda, String valueString) {
        if (bda instanceof BdaFloat32) {
            float value = Float.parseFloat(valueString);
            ((BdaFloat32) bda).setFloat(value);
        } else if (bda instanceof BdaFloat64) {
            double value = Float.parseFloat(valueString);
            ((BdaFloat64) bda).setDouble(value);
        } else if (bda instanceof BdaInt8) {
            byte value = Byte.parseByte(valueString);
            ((BdaInt8) bda).setValue(value);
        } else if (bda instanceof BdaInt8U) {
            short value = Short.parseShort(valueString);
            ((BdaInt8U) bda).setValue(value);
        } else if (bda instanceof BdaInt16) {
            short value = Short.parseShort(valueString);
            ((BdaInt16) bda).setValue(value);
        } else if (bda instanceof BdaInt16U) {
            int value = Integer.parseInt(valueString);
            ((BdaInt16U) bda).setValue(value);
        } else if (bda instanceof BdaInt32) {
            int value = Integer.parseInt(valueString);
            ((BdaInt32) bda).setValue(value);
        } else if (bda instanceof BdaInt32U) {
            long value = Long.parseLong(valueString);
            ((BdaInt32U) bda).setValue(value);
        } else if (bda instanceof BdaInt64) {
            long value = Long.parseLong(valueString);
            ((BdaInt64) bda).setValue(value);
        } else if (bda instanceof BdaBoolean) {
            boolean value = Boolean.parseBoolean(valueString);
            ((BdaBoolean) bda).setValue(value);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     */
    public void quit() {
        System.out.println("** Stopping server.");
        serverSap.stop();
    }
}
