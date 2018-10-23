package Controller;

import SerialPortsInteraction.Serial;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private String port;
    private String text="";
    private Serial serial;
    private ObservableList listPort= FXCollections.observableArrayList();
    private boolean access;
    private boolean stateXONXOFF=false;
    private boolean xon=true;

    @FXML
    private Text stateReceivedXonXoff;
    @FXML
    private Text receivedXonXoff;
    @FXML
    public Text statusMessageXonXoff;
    @FXML
    private Text receivedBytes;
    @FXML
    public TextArea outputZone;
    @FXML
    private TextField inputZone;
    @FXML
    private ChoiceBox<String> portNumber;
    @FXML
    private ChoiceBox<String> workType;
    @FXML
    private Label transferredBytes;
    @FXML
    public Label informationAboutXonXoff;
    @FXML
    private Label portStatus;
    @FXML
    private Label error;
    @FXML
    private Label errorMessage;
    @FXML
    private Button closePort;
    @FXML
    private Button openPort;
    @FXML
    private Button selectedXON;
    @FXML
    private Button selectedXOFF;

    @FXML
    void onClickOpenPort(){
        initPort();
        if(port==null){
          error.setText("Error:");
          errorMessage.setText("Port isn't selected. Impossible to open");
          access=false;
        }
        else if(getWorkType()==1){
            error.setText("Error:");
            errorMessage.setText("Select type work");
        }
        if(access) {
            try {
                    serial.openPort();
                    xon=true;
                    SerialPortEventListener serialPortEventListener = new SerialPortListener();
                    serial.addListener(serialPortEventListener);
                    closePort.setDisable(false);
                    portNumber.setDisable(true);
                    portStatus.setText(port + " open");
                    openPort.setDisable(true);
                    error.setText("");
                    errorMessage.setText("");
                    workType.setDisable(true);
                if (getWorkType() == 3) {
                    selectedXOFF.setDisable(false);
                    selectedXON.setDisable(false);
                    informationAboutXonXoff.setText("XON/XOFF mode is on:");
                    statusMessageXonXoff.setText("Sent to XON");
                } else if (getWorkType() == 2) {
                    selectedXOFF.setDisable(true);
                    selectedXON.setDisable(true);
                }
            } catch (SerialPortException e) {
                error.setText("Error:");
                errorMessage.setText(e.getExceptionType());
            }
        }
    }

    @FXML
    void onClickClosePort()  {
        if(port==null){
            error.setText("Error:");
            errorMessage.setText("Port isn't open. Impossible to close port");
        }
        else {
            try {
                serial.closePort();
                openPort.setDisable(false);
                closePort.setDisable(true);
                portNumber.setDisable(false);
                serial.setTransferredBytes(0);
                serial.setReceivedBytes(0);
                transferredBytes.setText(Integer.toString(this.serial.getTransferredBytes()));
                receivedBytes.setText(Integer.toString(this.serial.getReceivedBytes()));
                workType.setDisable(false);
                setStatusMessageXonXoff("");
                receivedXonXoff.setText("");
                portStatus.setText(port + " close");
                informationAboutXonXoff.setText("");
                stateReceivedXonXoff.setText("");
                error.setText("");
                errorMessage.setText("");
            } catch (SerialPortException e) {
                error.setText("Error:");
                errorMessage.setText(e.getExceptionType());
            }
        }
    }

    @FXML
    void onClickClearOutputWindow() throws SerialPortException {
        text="";
        this.outputZone.clear();
        this.serial.clearBuffer();
    }

    @FXML
    void onClickSendMessage() {
        if (getWorkType()==2){
            stateXONXOFF=false;
        }
        else if(port==null){
            error.setText("Error:");
            errorMessage.setText("Port isn't selected. Impossible to send");
            access=false;
        }
        if(access) {
            if (xon){
                if (!stateXONXOFF) {
                    try {
                        this.serial.write(inputZone.getText().getBytes());
                        error.setText("");
                        errorMessage.setText("");
                        inputZone.clear();
                        transferredBytes.setText(Integer.toString(this.serial.getTransferredBytes()));
                    } catch (Exception e) {
                        error.setText("Error:");
                        errorMessage.setText("Port isn't selected.");
                    }
                }
             }
             else {
                error.setText("Error:");
                errorMessage.setText("Can't be select, because received XOFF");
            }
        }
    }

    @FXML
    void onClickXOFF() throws SerialPortException {
        this.serial.sendXOFF();
        setStatusMessageXonXoff("Selected XOFF");
    }

    @FXML
    void onClickXON() throws SerialPortException {
        this.serial.sendXON();
        setStatusMessageXonXoff("Selected XON");
        error.setText("");
        errorMessage.setText("");
    }


    private void setOutputZone(String out){
        text=text+out+"\n";
        outputZone.setText(text);
    }
    private void setReceivedBytes(String s){
        receivedBytes.setText(s);
    }
    private void setStatusMessageXonXoff(String status){
        statusMessageXonXoff.setText(status);
    }
    private void setStateXONXOFF(boolean state){
        this.stateXONXOFF=state;
    }
    private void setReceivedXonXoff(String receivedState){
         stateReceivedXonXoff.setText("Received:");
         receivedXonXoff.setText(receivedState);
    }
    private void setAccessSend(boolean accessSend){
        this.xon=accessSend;
    }
    private void initPort(){
        port=portNumber.getValue();
        serial=new Serial(port);
        access=true;
    }
    private void getPortsNames(){
        String[] ports = SerialPortList.getPortNames();
        Collections.addAll(listPort, ports);
         portNumber.getItems().addAll(listPort);
    }

    private int  getWorkType(){
        if (workType.getValue()==null){
            return 1;
        }
        else if(workType.getValue().equals("Without XON/XOFF"))
        {return 2;}
        else return 3;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        workType.getItems().addAll("With XON/XOFF","Without XON/XOFF");
        getPortsNames();
        closePort.setDisable(true);
        selectedXOFF.setDisable(true);
        selectedXON.setDisable(true);
    }

    private class SerialPortListener implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent serialPortEvent) {
            if(serialPortEvent.isRXCHAR()&& serialPortEvent.getEventValue()>0){
                try{
                    byte[] readBytes;
                    readBytes=serial.read(serialPortEvent.getEventValue());
                    boolean stateXoff=(readBytes[0]==Serial.XOFF);
                    boolean stateXon=(readBytes[0]==Serial.XON);
                    if(stateXoff){
                       setReceivedXonXoff("XOFF");
                       setStateXONXOFF(true);
                       setAccessSend(false);
                    }
                    else if(stateXon) {
                        setReceivedXonXoff("XON");
                        setAccessSend(true);
                        setStateXONXOFF(false);
                    }
                    else if(!stateXoff){
                        String result = new String(readBytes);
                        String byteRe = Integer.toString(serial.getReceivedBytes());
                        setReceivedBytes(byteRe);
                        setOutputZone(result);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e);
                }
            }
        }
    }
}