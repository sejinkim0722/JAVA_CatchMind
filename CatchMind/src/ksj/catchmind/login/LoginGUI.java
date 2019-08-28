package ksj.catchmind.login;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class LoginGUI extends JFrame {
	
	JPanel panel_TOP, panel_CENTER, panel_BOTTOM;
	JLabel label_nickName, label_Ip;
	JTextField tf_nickName, tf_Ip;
	JButton btn_Connect, btn_Exit;
	
	public LoginGUI() {
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

		setVisible(true);
		setTitle("JAVA CatchMind LOGIN");
		setSize(300, 150);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}