package Client.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import Client.socket.client;
import Object.Code;

import java.awt.TextArea;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Color;

public class ClientUI {

	private JFrame frame;
	private JPanel contentPane;
	private RSyntaxTextArea textArea_Src;
	private TextArea textArea_Result;
	private ComboSuggestion cb;
	private JLayeredPane layeredPane;
	private LoadingScreen loadingScreen;

	private client clientSocket = null;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// try {
	// ClientUI frame = new ClientUI();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Create the frame.
	 */
	public ClientUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		frame = new JFrame();
		frame.setTitle("Code Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setBounds(100, 100, 1076, 681);
		frame.setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setBackground(new Color(230, 230, 230));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBounds(0, 0, 1076, 681);

		layeredPane = new JLayeredPane();
		layeredPane.setLayout(null);
		layeredPane.setOpaque(true);
		layeredPane.setBackground(new Color(255, 255, 255, 60));
		frame.setContentPane(layeredPane);
		loadingScreen = new LoadingScreen();

		textArea_Src = new RSyntaxTextArea(20, 60);
		textArea_Src.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
		textArea_Src.setCodeFoldingEnabled(true);
		RTextScrollPane sp = new RTextScrollPane(textArea_Src);
		sp.setBounds(10, 59, 1040, 387);
		contentPane.add(sp);

		textArea_Result = new TextArea();
		textArea_Result.setFont(new Font("Dialog", Font.PLAIN, 14));
		textArea_Result.setBounds(10, 450, 1040, 180);
		textArea_Result.setEditable(true);
		contentPane.add(textArea_Result);

		// JPanel statusPanel = new JPanel();
		// statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		// statusPanel.setBounds(0, contentPane.getHeight(), contentPane.getWidth(),
		// 16);
		// statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		// JLabel statusLabel = new JLabel("status");
		// statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		// statusPanel.add(statusLabel);
		// contentPane.add(statusPanel);

		Button btnExecute = new Button("Execute");
		btnExecute.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnExecute.setIcon(new ImageIcon(new ImageIcon("src\\Client\\ui\\logo\\refresh.png")
				.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH)));
		btnExecute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execute();
			}
		});
		btnExecute.setBounds(790, 18, 125, 30);
		contentPane.add(btnExecute);

		Button btnFormat = new Button("Format");
		btnFormat.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnFormat.setIcon(new ImageIcon(new ImageIcon("src\\Client\\ui\\logo\\edit.png")
				.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH)));
		btnFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				format();
			}
		});

		btnFormat.setBounds(650, 18, 125, 30);
		contentPane.add(btnFormat);

		Button btnUpload = new Button("Upload");
		btnUpload.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnUpload.setIcon(new ImageIcon(new ImageIcon("src\\Client\\ui\\logo\\upload.png")
				.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH)));
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(btnUpload);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					try {
						String content = Files.readString(selectedFile.toPath());
						textArea_Src.setText(content);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		btnUpload.setBounds(500, 18, 125, 30);
		contentPane.add(btnUpload);

		cb = new ComboSuggestion();
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ComboSuggestion comboBox = (ComboSuggestion) event.getSource();

				Object selected = comboBox.getSelectedItem();
				if (selected.toString().equals("C"))
					textArea_Src.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
				else if (selected.toString().equals("Python"))
					textArea_Src.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
				else if (selected.toString().equals("PHP"))
					textArea_Src.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
				else if (selected.toString().equals("Javascript"))
					textArea_Src.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
				else if (selected.toString().equals("Java"))
					textArea_Src.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

			}
		});
		cb.setLocation(925, 18);
		cb.setModel(new DefaultComboBoxModel(new String[] { "C", "Python", "Java", "Javascript", "PHP" }));
		cb.setEditable(false);
		cb.setSize(125, 30);
		contentPane.add(cb);

		layeredPane.add(contentPane, 1, 0);
		layeredPane.add(loadingScreen, 2, 0);

		frame.setVisible(true);
		connectServer();

	}

	private void connectServer() {
		JOptionPane optionPane = new JOptionPane();
		String ipServer = optionPane.showInputDialog(frame, "Server IP");
		loadingScreen.setVisible(true);

		SwingWorker swingWorker = new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				clientSocket = new client(ipServer, 1234);
				return clientSocket.connect();
			}

			@Override
			protected void done() {
				loadingScreen.setVisible(false);
				try {
					if (get()) {
						Notification panel = new Notification(frame, Notification.Type.SUCCESS,
								Notification.Location.TOP_LEFT, "Connected to server");
						panel.showNotification();
					} else {
						Notification panel = new Notification(frame, Notification.Type.WARNING,
								Notification.Location.TOP_LEFT, "Unable to connect to server");
						panel.showNotification();
					}
				} catch (HeadlessException | InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		swingWorker.execute();
	}

	private void execute() {
		if (clientSocket.connect()) {
			loadingScreen.setVisible(true);
			SwingWorker swingWorker = new SwingWorker<Boolean, Void>() {
				Code code;

				@Override
				protected Boolean doInBackground() throws Exception {
					code = new Code();
					code.setFunction("execute");
					code.setLanguage((String) cb.getSelectedItem());
					System.out.println(textArea_Src.getText());
					code.setSource(textArea_Src.getText());
					clientSocket.send(code);
					String result = (String) clientSocket.receive();
					textArea_Result.setText(result);
					return true;
				}

				protected void done() {
					try {
						if (get()) {
							loadingScreen.setVisible(false);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			};
			swingWorker.execute();
		} else {
			Notification panel = new Notification(frame, Notification.Type.WARNING, Notification.Location.TOP_LEFT,
					"Unable to connect to server");
			panel.showNotification();
		}

	}

	private void format() {
		if (clientSocket.connect()) {
			loadingScreen.setVisible(true);
			SwingWorker swingWorker = new SwingWorker<Boolean, Void>() {
				Code code;

				@Override
				protected Boolean doInBackground() throws Exception {
					code = new Code();
					code.setFunction("format");
					code.setLanguage((String) cb.getSelectedItem());
					code.setSource(textArea_Src.getText());
					clientSocket.send(code);
					String result = (String) clientSocket.receive();
					textArea_Src.setText(result);
					return true;
				}

				protected void done() {
					try {
						if (get()) {
							loadingScreen.setVisible(false);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			};
			swingWorker.execute();
		} else {
			Notification panel = new Notification(frame, Notification.Type.WARNING, Notification.Location.TOP_LEFT,
					"Unable to connect to server");
			panel.showNotification();
		}
	}
}
