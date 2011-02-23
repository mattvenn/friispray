package VirtualGraffiti;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.TooManyListenersException;

public class SerialReader implements SerialPortEventListener{

	int baud = 19200;
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	static SerialPort serialPort;
	static OutputStream out = null;
	static InputStream in = null;
	boolean newSerial = false;
	String latestMessage;
	
	SerialReader()
	{
		setup();
	}
	
	boolean newSerial()
	{
		return newSerial;
	}
	String getLatestMessage()
	{
		newSerial = false;
		return latestMessage;
	}
	void readSerial()
	{
		try
		{
			latestMessage = bufferedReader.readLine();
		}
		catch (IOException e)
		{
			System.out.println( "IO exception" + e );
		}
	}


	public void setup()
	{
		openSerial();

		try {
			// Add the serial port event listener
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (TooManyListenersException ex) {
			System.err.println(ex.getMessage());
		}
		bufferedReader = new BufferedReader(new InputStreamReader(in));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));

		System.out.println( "serial setup complete" );


	}

	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			//System.out.println( "got serial event" );
			newSerial = true;
			readSerial();
			break;
		}
	}

	public void openSerial()
	{
		try
		{
			CommPortIdentifier portId = null;

			try
			{
				portId = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
			}
			catch( NoSuchPortException e )
			{
				try
				{
					portId = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB1");
				}
				catch( NoSuchPortException e2 )
				{
					System.out.println( "couldn't open usb0 or 1" );
					System.exit(1 );
				}
			}

			serialPort = (SerialPort) portId.open("more meterreader", 5000);
			int baudRate = baud; // 57600; // 57600bps
			// Set serial port to 57600bps-8N1..my favourite
			serialPort.setSerialPortParams(
					baudRate,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			//			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			try
			{
				serialPort.setDTR(false);
				Thread.sleep( 100 );
				serialPort.setDTR(true);
			}
			catch( InterruptedException e )
			{}
			out = serialPort.getOutputStream();
			in = serialPort.getInputStream();

		} catch (UnsupportedCommOperationException ex) {
			System.err.println(ex.getMessage());
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeSerial()
	{
		if (serialPort != null) {
			try {
				// close the i/o streams.
				out.close();
				in.close();
			} catch (IOException ex) {
				// don't care
			}
			// Close the port.
			serialPort.close();

		}
	}


}
