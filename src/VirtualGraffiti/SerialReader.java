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

	int serialPortBaud;
	String serialPortName;
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	static SerialPort serialPort;
	static OutputStream out = null;
	static InputStream in = null;
	boolean newSerial = false;
	String latestMessage;
	
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


	SerialReader()
	{
		serialPortName = VirtualGraffiti.props.getStringProperty( "serialPortDev", "/dev/ttyUSB0" );
		serialPortBaud = VirtualGraffiti.props.getIntProperty( "serialPortBaud", 19200 );
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
				System.out.println( "trying to open " + serialPortName);
				portId = CommPortIdentifier.getPortIdentifier(serialPortName);
			}
			catch( NoSuchPortException e )
			{
				System.out.println( "couldn't open " + serialPortName + " : " + e );
				System.exit(1 );
			}

			System.out.println( "opening port" + portId.getName() );
			serialPort = (SerialPort) portId.open("virtual graffiti", 5000);
			// Set serial port to 57600bps-8N1..my favourite
			System.out.println( "setting port params: " + serialPortBaud );
			serialPort.setSerialPortParams(
					serialPortBaud,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			//			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			try
			{
				
				System.out.println( "reset arduino..." );
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
