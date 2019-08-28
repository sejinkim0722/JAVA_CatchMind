package ksj.catchmind.login;

import java.awt.event.*;
import javax.swing.*;

import ksj.catchmind.client.Client;

public class Login extends LoginGUI implements ActionListener {
	
	public static String ip, nickName;

	public Login() {
		btn_Connect.addActionListener(this);
		btn_Exit.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_Connect) {
			if(tf_nickName.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "닉네임을 입력해 주세요!", "ERROR!", JOptionPane.WARNING_MESSAGE);
			} else if(tf_Ip.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "IP 주소를 입력해 주세요!", "ERROR!", JOptionPane.WARNING_MESSAGE);
			} else if(tf_nickName.getText().trim().length() > 5) {
				JOptionPane.showMessageDialog(null, "닉네임은 최대 5글자까지만 허용됩니다!", "ERROR!", JOptionPane.WARNING_MESSAGE);
				tf_nickName.setText("");
			} else {
				nickName = tf_nickName.getText().trim();
				String temp = tf_Ip.getText();
				if(temp.matches("(^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$)")) {
					ip = temp;
					JOptionPane.showMessageDialog(null, "             로그인 성공!", "JAVA CatchMind LOGIN", JOptionPane.INFORMATION_MESSAGE);
					btn_Connect.setEnabled(false);
					tf_nickName.setEnabled(false);
					tf_Ip.setEnabled(false);
					setVisible(false);
					
					new Client();
				} else {
					JOptionPane.showMessageDialog(null, "IP 주소를 정확하게 입력해 주세요! ", "ERROR!", JOptionPane.WARNING_MESSAGE);
				}
			}
		} else if(e.getSource() == btn_Exit) {
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new Login();
	}
}