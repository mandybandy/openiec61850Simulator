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
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private  ServerModel serverModel;
    public String error;

    /**
     *
     * @param icdpath
     * @param portServer
     */
    public Server(String icdpath, int portServer) {
        error = "no error";
        serverSap=null;
        
        try {

            List<ServerModel> serverModels = null;
            try {
                serverModels = SclParser.parse(icdpath);
                System.out.println("icd file passed parsing.");

            } catch (SclParseException e) {
                System.out.println("Error parsing SCL/ICD file: " + e.getMessage());
                System.out.println(e.getCause().getClass().getCanonicalName());
                if ("java.io.FileNotFoundException".equals(e.getCause().getClass().getCanonicalName())) {
                    error="file not found";
                }
            }

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
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            //return "server already running";
        }
        
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

        BasicDataAttribute bda= (BasicDataAttribute) serverModel.findModelNode(reference, Fc.fromString(fcString));

        try {
            setBdaValue(bda, valueString);
        } catch (Exception e) {
            System.out.println(
                    "The console server does not support writing this type of basic data attribute.");
            return;
        }

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
