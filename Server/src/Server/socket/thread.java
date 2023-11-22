package Server.socket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Random;


import Object.Code;

public class thread implements Runnable {
    private Socket socket = null;
	private ObjectInputStream in = null;
	private ObjectOutputStream out = null;
	private encryption encryption;

    public thread(Socket socket) {
        try {
            this.socket = socket;
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
			generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        handleData();
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
			return;
		}
	}
	
	public void send(Object object) {
		try {
			byte[] bytes = encryption.encryptData(object.toString());
			this.out.writeObject(bytes);
			this.out.flush();
		} catch (java.net.SocketException e) {
			System.out.println("[Notification] Client: " + this.socket + " lost connection");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object receive() {
		try {
			byte[] bytes = (byte[])this.in.readObject();
			Object object = (Object) encryption.decryptData(bytes);
			return object;
		} catch (java.net.SocketException e) {
			System.out.println("[Notification] Client: " + this.socket + " lost connection");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void generateKey() {
		try {
			encryption = new encryption();
			PublicKey publicKey = (PublicKey) this.in.readObject();

			encryption.setPublicKey(publicKey);
			encryption.encryptKey();
		
			this.out.writeObject(encryption.getEncryptedKey());
			this.out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handleData() {
		while (this.socket != null) {
			try {
				Object object = this.receive();
				if (object != null) {
					Code code = new Code(object.toString());
					if (code.getFunction().equals("execute")) {
						execute(code.getSource(), code.getLanguage());
					} else if (code.getFunction().equals("format")) {
						format(code.getSource(), code.getLanguage());
					}
				} else 
					break;
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	private void execute(String src, String lang) {
		try {
			Random rd = new Random();
			String index = rd.nextInt(100000) + 900000 + "";
			String filePath = ".\\src\\Server\\temp\\" + index;
			String command = "";

			ProcessBuilder pb = new ProcessBuilder();

			switch (lang) {
				case "C":
					String fileExe = filePath + ".exe";
					filePath += ".c";	
					writeToFile(filePath, src);
					command = "gcc " + filePath + " -o " + fileExe;
					pb.command("cmd.exe", "/c", command + " 2>&1");
					Process process = pb.start();
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = "", lines = "";
					while ((line = br.readLine()) != null) {
						lines += line + "\n";
					}
					int exitedCode = process.waitFor();
					lines += "\nExited code: " + exitedCode;
					send(lines);
					if (exitedCode == 0) {
						Thread.sleep(1000);
						pb.command("cmd.exe", "/c", fileExe + " 2>&1");
					}
					break;
				case "Python":
					command = ".\\cf\\Compiler\\Python311\\python.exe";
					filePath += ".py";
					writeToFile(filePath, src);
					pb.command("cmd.exe", "/c", command + " " + filePath + " 2>&1");
					break;
				case "PHP":
					command = ".\\cf\\Compiler\\php-8.2.12-nts-Win32-vs16-x64\\php.exe";
					filePath += ".php";
					writeToFile(filePath, src);
					pb.command("cmd.exe", "/c", command + " " + filePath + " 2>&1");
					break;
				case "Javascript":
					command = "node";
					filePath += ".js";
					writeToFile(filePath, src);
					pb.command("cmd.exe", "/c", command + " " + filePath + " 2>&1");
					break;
				case "Java":
					filePath += ".java";	
					writeToFile(filePath, src);
					command = "javac";
					pb.command("cmd.exe", "/c", command + " " + filePath);
					pb.start();
					Thread.sleep(1000);
					command = "java"; 
					pb.command("cmd.exe", "/c", command + " " + filePath + " 2>&1");
					break;
			}
			Process process = pb.start();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "", lines = "";
			while ((line = br.readLine()) != null) {
				lines += line + "\n";
			}
			lines += "\nExited code: " + process.waitFor();
			send(lines);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void format(String src, String lang) {
		try {
			Random rd = new Random();
			String index = rd.nextInt(100000) + 10000 + "";
			String filePath = ".\\src\\Server\\temp\\" + index;
			String command = "";
			ProcessBuilder pb = new ProcessBuilder();

			switch (lang) {
				case "C":
					filePath += ".c";	
					writeToFile(filePath, src);
					command = ".\\cf\\Formatter\\astyle-3.4.10-x64\\astyle.exe";
					pb.command("cmd.exe", "/c", command + " -H -U -xe -p -f -E -n " + filePath);
					pb.start();
					break;
				case "Python":
					command = "black";
					filePath += ".py";
					writeToFile(filePath, src);
					pb.command("cmd.exe", "/c", command + " " + filePath);
					pb.start();
					break;
				case "PHP":
					command = "";
					filePath += ".php";
					writeToFile(filePath, src);
					pb.command("cmd.exe", "/c", command + " " + filePath + " > " + filePath);
					pb.start();
					break;
				case "Javascript":
					command = ".\\cf\\Formatter\\astyle-3.4.10-x64\\astyle.exe";
					filePath += ".js";
					writeToFile(filePath, src);
					pb.command("cmd.exe", "/c", command + " -H -U -xe -p -f -E -n " + filePath);
					pb.start();
					break;
				case "Java":
					command = ".\\cf\\Formatter\\astyle-3.4.10-x64\\astyle.exe";
					filePath += ".java";
					writeToFile(filePath, src);
					pb.command("cmd.exe", "/c", command + " -H -U -xe -p -f -E -n " + filePath);
					pb.start();
					break;
			}
			
			Thread.sleep(2000);
			String result = readFromFile(filePath);
			send(result);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeToFile(String filePath, String content) {
		try {
			File file = new File(filePath);
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(content);
			fileWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String readFromFile(String filePath) {
		String lines = "";
		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			String line = "";
			while ((line = fileReader.readLine()) != null){
				lines += line + "\n";
			}
			return lines;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
}
