package org.droidplanner.core.MAVLink.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Provides support for mavlink connection via udp.
 */
public abstract class UdpConnection extends MavLinkConnection {

	private DatagramSocket socket;
	private int serverPort;

	private int hostPort;
	private InetAddress hostAdd;

	private void getUdpStream() throws IOException {
		hostPort = 14551;
		//hostAdd = InetAddress.getByName("192.168.190.1");
		hostAdd = InetAddress.getByName("192.168.31.238");

		socket = new DatagramSocket(serverPort);
		socket.setBroadcast(true);
		socket.setReuseAddress(true);

		//sendInitPacket();
	}

	private void sendInitPacket() throws IOException {
		byte[] buf = new byte[2]; //('\x55', '\xee');
		buf[0] = 0x55;
		buf[1] = (byte)0xee;
		DatagramPacket packet = new DatagramPacket(buf, buf.length, hostAdd, hostPort);
		socket.send(packet);
	}

	@Override
	public final void closeConnection() throws IOException {
		if (socket != null)
			socket.close();
	}

	@Override
	public final void openConnection() throws IOException {
		getUdpStream();
	}

	@Override
	public final void sendBuffer(byte[] buffer) throws IOException {
		try {
			if (hostAdd != null) { // We can't send to our sister until they
				// have connected to us
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, hostAdd, hostPort);
				socket.send(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public final int readDataBlock(byte[] readData) throws IOException {
		DatagramPacket packet = new DatagramPacket(readData, readData.length);
		socket.receive(packet);
		//hostAdd = packet.getAddress();
		//hostPort = packet.getPort();
		return packet.getLength();
	}

	@Override
	public final void loadPreferences() {
		serverPort = loadServerPort();
	}

	@Override
	public final int getConnectionType() {
		return MavLinkConnectionTypes.MAVLINK_CONNECTION_UDP;
	}

	protected abstract int loadServerPort();
}
