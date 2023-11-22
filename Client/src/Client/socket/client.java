package Client.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Client.ui.ClientUI;

public class client {
	private Socket socket;
	private int port;
	private String hostName;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private encryption encryption;

	public client(String hostName, int port) {
		this.hostName = hostName;
		this.port = port;
	}
	
	public boolean connect() {
		try {
			this.socket = new Socket(this.hostName, this.port);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.in = new ObjectInputStream(this.socket.getInputStream());
			generateKey();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void generateKey() {
		try {
			encryption = new encryption();
			this.out.writeObject(encryption.getKeyPair().getPublic());
			this.out.flush();

			byte[] encryptedKey = (byte[]) this.in.readObject();
			encryption.setEncryptedKey(encryptedKey);
			encryption.decryptKey();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if (socket != null) {
				this.in.close();
				this.out.close();
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Object object) {
		try {
			if(socket != null) {
				byte[] bytes = encryption.encryptData(object.toString());
				this.out.writeObject(bytes);
				this.out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object receive() {
		try {
			if (socket != null) {
				byte[] bytes = (byte[])this.in.readObject();
				Object object = (Object) encryption.decryptData(bytes);
				return object;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
