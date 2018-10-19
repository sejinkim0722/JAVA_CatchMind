import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class CM_Login extends JFrame implements ActionListener
{
	JPanel panel_TOP, panel_CENTER, panel_BOTTOM;
	JLabel label_nickName, label_Ip;
	JTextField tf_nickName, tf_Ip;
	JButton btn_Connect, btn_Exit;
	
	public static String ip, nickName;

	public void init(){
		panel_TOP = new JPanel();
		panel_CENTER = new JPanel();
		panel_BOTTOM = new JPanel();

		label_nickName = new JLabel(new ImageIcon("image\\nm.png"));
		label_Ip = new JLabel(new ImageIcon("image\\ip.png"));
		tf_nickName = new JTextField(15);
		tf_Ip = new JTextField(15);
		btn_Connect = new JButton("접속");
		btn_Exit = new JButton("종료");

		panel_TOP.setBackground(new Color(50, 50, 50));
		panel_CENTER.setBackground(new Color(50, 50, 50));
		panel_BOTTOM.setBackground(new Color(50, 50, 50));
		add(panel_TOP, BorderLayout.NORTH);
		add(panel_CENTER, BorderLayout.CENTER);
		add(panel_BOTTOM, BorderLayout.SOUTH);
		panel_TOP.setLayout(new FlowLayout());
		panel_CENTER.setLayout(new FlowLayout());
		panel_BOTTOM.setLayout(new FlowLayout());

		tf_nickName.setBorder(new LineBorder(Color.WHITE, 3));
		tf_nickName.setForeground(Color.WHITE);
		tf_nickName.setBackground(new Color(50, 50, 50));
		tf_Ip.setBorder(new LineBorder(Color.WHITE, 3));
		tf_Ip.setForeground(Color.WHITE);
		tf_Ip.setBackground(new Color(50, 50, 50));
		panel_TOP.add(label_nickName);
		panel_TOP.add(tf_nickName);

		label_nickName.setForeground(Color.WHITE);
		label_Ip.setForeground(Color.WHITE);
		panel_CENTER.add(label_Ip);
		panel_CENTER.add(tf_Ip);

		btn_Connect.setBorderPainted(false);
		btn_Exit.setBorderPainted(false);
		btn_Connect.setFocusPainted(false);
		btn_Exit.setFocusPainted(false);
		btn_Connect.setBackground(new Color(100, 181, 246)); 
		btn_Exit.setBackground(new Color(100, 181, 246));
		btn_Connect.setForeground(Color.WHITE);
		btn_Exit.setForeground(Color.WHITE);

		panel_BOTTOM.add(btn_Connect);
		panel_BOTTOM.add(btn_Exit);
		btn_Connect.addActionListener(this);
		btn_Exit.addActionListener(this);

		// 기본 GUI 설정
		setVisible(true);
		setTitle("JAVA CatchMind LOGIN");
		setSize(300, 150);
		setResizable(false);
		setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == btn_Connect){
			if(tf_nickName.getText().equals("")){
				JOptionPane.showMessageDialog(null, "닉네임을 입력해 주세요!", "ERROR!", JOptionPane.WARNING_MESSAGE);
			}else if(tf_Ip.getText().equals("")){
				JOptionPane.showMessageDialog(null, "IP 주소를 입력해 주세요!", "ERROR!", JOptionPane.WARNING_MESSAGE);
			}else if(tf_nickName.getText().trim().length() > 5){
				JOptionPane.showMessageDialog(null, "닉네임은 5글자까지만 입력할 수 있습니다!", "ERROR!", JOptionPane.WARNING_MESSAGE);
				tf_nickName.setText("");
			}else{
				nickName = tf_nickName.getText().trim();
				String temp = tf_Ip.getText();
				if(temp.matches("(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$)")){
					ip = temp;
					JOptionPane.showMessageDialog(null, "             로그인 성공!", "JAVA CatchMind LOGIN", JOptionPane.INFORMATION_MESSAGE);
					btn_Connect.setEnabled(false);
					tf_nickName.setEnabled(false);
					tf_Ip.setEnabled(false);
					setVisible(false);
					CM_Client cmc = new CM_Client();
				}else{
					JOptionPane.showMessageDialog(null, "IP 주소를 정확하게 입력해 주세요! ", "ERROR!", JOptionPane.WARNING_MESSAGE);
				}
			}
		}else if(e.getSource() == btn_Exit){
			System.exit(0);
		}
	}

	public static void main(String[] args){
		CM_Login cml = new CM_Login();
		cml.init();
	}
}