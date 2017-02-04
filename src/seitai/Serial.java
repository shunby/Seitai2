package seitai;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import seitai.world.World;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class Serial {
	private static CommPortIdentifier portID;
	private static SerialPort port;
	private static World world;
	private static SerialThread th;

	public static void init() {
		if(th != null)th.stopFlag = true;
		world = World.getInstance();

		if(!openComport("COM3"))return;
		th = new SerialThread();
		th.setDaemon(true);
		th.start();
	}

	/**
	 * ポートを開く
	 *
	 * @param portNum
	 * @return
	 */
	public static Boolean openComport(String portNum) {
		try {
			Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();

	        CommPortIdentifier p;
	        boolean contains = false;
	        while(portList.hasMoreElements()){
	            p = (CommPortIdentifier)portList.nextElement();
	            if(p.getName().equals(portNum)){contains = true;break;}
	            System.out.println(p.getName());
	        }
	        if(contains == false)return false;

			portID = CommPortIdentifier.getPortIdentifier(portNum);
			port = (SerialPort) portID.open("SirialTest", 5000);

			// ボーレート、データビット数、ストップビット数、パリティを設定
			port.setSerialPortParams(57600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// フロー制御はしない
			port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			return false;
		} catch (PortInUseException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 送信
	 *
	 * @param text
	 */
	public static void submit(String text) {
		OutputStream output = null;
		try {
			byte[] data = text.getBytes();
			output = port.getOutputStream();
			// ここでしばらく待たないとうまく表示されない
			Thread.sleep(1000);
			output.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	static class SerialThread extends Thread{
			public boolean stopFlag = false;
			//500ms間隔でLCDを更新
			@Override
			public void run() {
				try{
				while (Main.doSerial() && !stopFlag && Main.isRunning()) {
					submit("G:" + world.eater + " F:" + world.flesh +" S:" + world.superE);

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				port.close();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				port.close();
			}
		}
	}
}
