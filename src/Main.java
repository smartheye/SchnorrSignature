import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Main extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;

    private JTextField fileName, fileName2;
    private JTextArea messageArea;
    private JPasswordField passwordField, passwordField2;
    private JTextArea publicKeyArea, publicKeyArea2, publicKeyArea3;

    private JLabel messageLabel, passwordLabel, publicKeyLabel;

    private JButton signButton, openButton, verifyButton, openButton2, signButton2;

    private JFileChooser fileChooser, fileChooser2;
    private Appendable log;

    private SchnorrSignature signature = null;

    private byte[] data = null;
    private JLabel passwordLabel2;

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(600, 400));

        setUI();

        setVisible(true);
    }

    private void setUI() {
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

        tabbedPane = new JTabbedPane();

        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();

        tabbedPane.addTab("Podpisz wiadomość", panel1);
        setMessageSignUI();

        tabbedPane.addTab("Podpisz plik", panel3);
        setFileSignUI();

        tabbedPane.addTab("Weryfikuj", panel2);
        setVerifyUI();

        this.add(tabbedPane);
    }

    private void setFileSignUI() {
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.PAGE_AXIS));

        fileChooser2 = new JFileChooser();

        openButton2 = new JButton("Otwórz plik");
        openButton2.addActionListener(new ClickListener());

        fileName2 = new JTextField();
        fileName2.setAlignmentX(Component.LEFT_ALIGNMENT);
        fileName2.setEditable(false);
        fileName2.setMaximumSize(new Dimension(600, 30));

        passwordField2 = new JPasswordField();
        passwordField2.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField2.setPreferredSize(new Dimension(600, 30));
        passwordField2.setMaximumSize(passwordField.getPreferredSize());

        passwordLabel2 = new JLabel("Hasło");
        passwordLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);

        publicKeyArea3 = new JTextArea();
        publicKeyArea3.setAlignmentX(Component.LEFT_ALIGNMENT);
        publicKeyArea3.setEditable(false);
        publicKeyArea3.setLineWrap(true);

        signButton2 = new JButton("Podpisuj");
        signButton2.setAlignmentX(Component.LEFT_ALIGNMENT);
        signButton2.addActionListener(new ClickListener());

        panel3.add(openButton2);
        panel3.add(fileName2);
        panel3.add(passwordLabel2);
        panel3.add(passwordField2);
        panel3.add(publicKeyLabel);
        panel3.add(publicKeyArea3);
        panel3.add(signButton2);
    }

    private void setMessageSignUI() {
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));

        messageArea = new JTextArea();
        messageArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageArea.setLineWrap(true);
        messageLabel = new JLabel("Wiadomość");
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setPreferredSize(new Dimension(600, 30));
        passwordField.setMaximumSize(passwordField.getPreferredSize());
        passwordLabel = new JLabel("Hasło");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        publicKeyArea = new JTextArea();
        publicKeyArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        publicKeyArea.setEditable(false);
        publicKeyArea.setLineWrap(true);
        publicKeyLabel = new JLabel("Klucz Publiczny");
        publicKeyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        signButton = new JButton("Podpisuj");
        signButton.addActionListener(new ClickListener());

        panel1.add(messageLabel, BorderLayout.CENTER);
        panel1.add(messageArea, BorderLayout.CENTER);
        panel1.add(passwordLabel, BorderLayout.CENTER);
        panel1.add(passwordField, BorderLayout.CENTER);
        panel1.add(publicKeyLabel, BorderLayout.CENTER);
        panel1.add(publicKeyArea, BorderLayout.CENTER);
        panel1.add(signButton, BorderLayout.SOUTH);
    }

    private void setVerifyUI() {
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        fileChooser = new JFileChooser();

        openButton = new JButton("Otwórz plik");
        openButton.addActionListener(new ClickListener());

        fileName = new JTextField();
        fileName.setEditable(false);
        fileName.setMaximumSize(new Dimension(600, 30));

        verifyButton = new JButton("Weryfikuj");
        verifyButton.addActionListener(new ClickListener());

        publicKeyArea2 = new JTextArea();
        publicKeyArea2.setAlignmentX(Component.LEFT_ALIGNMENT);
        publicKeyArea2.setLineWrap(true);

        panel2.add(openButton);
        panel2.add(fileName);
        panel2.add(publicKeyLabel);
        panel2.add(publicKeyArea2);
        panel2.add(verifyButton);
    }

    private class ClickListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == signButton) {
                if (!messageArea.getText().equals("") && passwordField.getPassword().length != 0) {
                    SchnorrSign sign = new SchnorrSign(new String(passwordField.getPassword()).getBytes());
                    try {
                        SchnorrSignature signature = sign.sign(messageArea.getText().getBytes());
                        publicKeyArea.setText(sign.getPublicKey().toString());


                        FileOutputStream fileOut =
                                new FileOutputStream("message_signature");
                        ObjectOutputStream out = new ObjectOutputStream(fileOut);
                        out.writeObject(signature);
                        out.close();
                        fileOut.close();
                        System.out.printf("Zapisaliśmy podpis wiadomości");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            else if (actionEvent.getSource() == openButton) {
                int returnVal = fileChooser.showOpenDialog(Main.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try
                    {
                        FileInputStream fileIn = new FileInputStream(file);
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        signature = (SchnorrSignature) in.readObject();

                        fileName.setText(file.getName());

                        in.close();
                        fileIn.close();
                    } catch(IOException i)
                    {
                        i.printStackTrace();
                        return;
                    } catch(ClassNotFoundException c)
                    {
                        c.printStackTrace();
                        return;
                    }
                }
            }
            else if(actionEvent.getSource() == verifyButton) {
                if(signature != null && publicKeyArea2.getText().length() != 0) {
                    try {
                        BigInteger pubKey = new BigInteger(publicKeyArea2.getText());
                        SchnorrScheme verify = new SchnorrVerify(pubKey);
                        if(verify.verify(signature)) {
                            System.out.println("OK");

                            JOptionPane.showMessageDialog(panel2, "Podpis zweryfikowany");
                        }
                        else {
                            System.out.println("NIE OK");

                            JOptionPane.showMessageDialog(panel2, "Podpis nie został zweryfikowany");
                        }
                    }
                    catch(Exception ex) {

                    }
                }
            }
            else if (actionEvent.getSource() == openButton2) {
                int returnVal = fileChooser2.showOpenDialog(Main.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser2.getSelectedFile();
                    try
                    {
                        Path path = Paths.get(file.getAbsolutePath());
                        data = Files.readAllBytes(path);

                        fileName2.setText(file.getName());
                    } catch(IOException i)
                    {
                        i.printStackTrace();
                        return;
                    }
                }
            }
            else if(actionEvent.getSource() == signButton2) {
                if (data != null && passwordField2.getPassword().length != 0) {
                    SchnorrSign sign = new SchnorrSign(new String(passwordField2.getPassword()).getBytes());
                    try {
                        SchnorrSignature signature = sign.sign(data);
                        publicKeyArea2.setText(sign.getPublicKey().toString());
                        publicKeyArea3.setText(sign.getPublicKey().toString());

                        FileOutputStream fileOut =
                                new FileOutputStream("signature");
                        ObjectOutputStream out = new ObjectOutputStream(fileOut);
                        out.writeObject(signature);
                        out.close();
                        fileOut.close();
                        System.out.printf("Zapisaliśmy podpis pliku " + fileName2.getText());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}
