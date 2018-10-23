package SerialPortsInteraction;

import jssc.SerialPort;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Serial {
    public static final byte XON = 17;
    public static final byte XOFF = 19;

    private int transferredBytes = 0;
    private int receivedBytes = 0;
    private SerialPort serialPort;

    public Serial(String portName) {
        this.serialPort = new SerialPort(portName);
    }

    public void openPort() throws SerialPortException {
        serialPort.openPort();
        serialPort.setParams(9600, 8, 1, 0);
    }

    public void closePort() throws SerialPortException {
        transferredBytes = 0;
        receivedBytes = 0;
        serialPort.closePort();
        this.serialPort = null;
    }

    public void write(byte[] bytes) throws SerialPortException {
        transferredBytes += bytes.length;
        this.serialPort.writeBytes(bytes);
    }

    public byte[] read(int byteCount) throws SerialPortException {
        byte[] result = this.serialPort.readBytes(byteCount);
        receivedBytes += result.length;
        return result;

    }

    public void clearBuffer() throws SerialPortException {
        this.serialPort.purgePort(SerialPort.PURGE_RXCLEAR);
    }

    public void addListener(SerialPortEventListener listener) throws SerialPortException {
        this.serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        this.serialPort.addEventListener(listener);
    }

    public void sendXOFF() throws SerialPortException {
         this.serialPort.writeByte(XOFF);
    }

    public void sendXON() throws SerialPortException {
         this.serialPort.writeByte(XON);
    }

    public void setTransferredBytes(int transferredBytes) {
        this.transferredBytes = transferredBytes;
    }

    public int getTransferredBytes() {
        return transferredBytes;
    }

    public void setReceivedBytes(int receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public int getReceivedBytes() {
        return receivedBytes;
    }

}


