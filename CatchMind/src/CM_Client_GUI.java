import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class CM_Client_GUI extends JFrame
{
	JPanel contentPane, panel_Main, panel_Chat, panel_Exam, panel_Canvas, panel_Option;
	JButton btn_Ready, btn_Exit, btn_Color1, btn_Color2, btn_Color3, btn_Color4, btn_Color5, btn_Erase, btn_EraseAll, btn_GG;
	JLabel label_Canvas, label_Exam, label_Exam_Sub, label_Timer, label_Client1, label_Client2, label_Client3, label_Client4;
	Label label_Client1_Sub, label_Client2_Sub, label_Client3_Sub, label_Client4_Sub;
	JTextField textField;
	JTextArea textArea;
	JScrollPane scrollPane;
	
	public CM_Client_GUI(){
		// 기본 GUI 설정
		setFont(new Font("나눔바른고딕", Font.PLAIN, 13));
		setVisible(true);
		setResizable(false);
		setTitle("JAVA CatchMind Client");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1280, 720);
		setLocationRelativeTo(null);
		
		// 베이스 패널
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		panel_Main = new JPanel();
		panel_Main.setFont(new Font("나눔바른고딕", Font.PLAIN, 13));
		panel_Main.setBackground(new Color(228, 242, 254));
		contentPane.add(panel_Main);
		panel_Main.setLayout(null);
		
		// 참여자 목록 영역
		JPanel panel_ClientList = new JPanel();
		JLabel label_ClientList = new JLabel(new ImageIcon("image\\user.png"));
		panel_ClientList.setOpaque(false);
		label_ClientList.setOpaque(false);
		panel_ClientList.setBorder(new LineBorder(new Color(127, 219, 254), 4, true));
		panel_ClientList.setBounds(10, 105, 156, 500);
		label_ClientList.setBounds(10, 105, 156, 500);
		panel_Main.add(panel_ClientList);
		panel_Main.add(label_ClientList);
		panel_ClientList.setLayout(null);
		
		label_Client1 = new JLabel(new ImageIcon("image\\p0.png"));
		label_Client1.setBackground(Color.GRAY);
		label_Client1.setBounds(18, 15, 120, 80);
		panel_ClientList.add(label_Client1);
		
		label_Client1_Sub = new Label("[ 닉네임 & 점수 ]");
		label_Client1_Sub.setFont(new Font("나눔바른고딕", Font.BOLD, 13));
		label_Client1_Sub.setAlignment(Label.CENTER);
		label_Client1_Sub.setBackground(Color.WHITE);
		label_Client1_Sub.setBounds(18, 95, 120, 30);
		panel_ClientList.add(label_Client1_Sub);
		
		label_Client2 = new JLabel(new ImageIcon("image\\p0.png"));
		label_Client2.setBackground(Color.GRAY);
		label_Client2.setBounds(18, 135, 120, 80);
		panel_ClientList.add(label_Client2);
		
		label_Client2_Sub = new Label("[ 닉네임 & 점수 ]");
		label_Client2_Sub.setFont(new Font("나눔바른고딕", Font.BOLD, 13));
		label_Client2_Sub.setAlignment(Label.CENTER);
		label_Client2_Sub.setBackground(Color.WHITE);
		label_Client2_Sub.setBounds(18, 215, 120, 30);
		panel_ClientList.add(label_Client2_Sub);
		
		label_Client3 = new JLabel(new ImageIcon("image\\p0.png"));
		label_Client3.setBackground(Color.GRAY);
		label_Client3.setBounds(18, 255, 120, 80);
		panel_ClientList.add(label_Client3);
		
		label_Client3_Sub = new Label("[ 닉네임 & 점수 ]");
		label_Client3_Sub.setFont(new Font("나눔바른고딕", Font.BOLD, 13));
		label_Client3_Sub.setAlignment(Label.CENTER);
		label_Client3_Sub.setBackground(Color.WHITE);
		label_Client3_Sub.setBounds(18, 335, 120, 30);
		panel_ClientList.add(label_Client3_Sub);
		
		label_Client4 = new JLabel(new ImageIcon("image\\p0.png"));
		label_Client4.setBackground(Color.GRAY);
		label_Client4.setBounds(18, 375, 120, 80);
		panel_ClientList.add(label_Client4);
		
		label_Client4_Sub = new Label("[ 닉네임 & 점수 ]");
		label_Client4_Sub.setFont(new Font("나눔바른고딕", Font.BOLD, 13));
		label_Client4_Sub.setAlignment(Label.CENTER);
		label_Client4_Sub.setBackground(Color.WHITE);
		label_Client4_Sub.setBounds(18, 455, 120, 30);
		panel_ClientList.add(label_Client4_Sub);
		
		// 문제 출제 영역
		panel_Exam = new JPanel();
		panel_Exam.setBounds(10, 10, 1245, 85);
		panel_Main.add(panel_Exam);
		panel_Exam.setLayout(null);
		
		JLabel label_Exam_Back = new JLabel(new ImageIcon("image\\exam_bg.png"));
		label_Exam_Back.setOpaque(true);
		label_Exam_Back.setBounds(0, 0, 1245, 85);
		panel_Exam.add(label_Exam_Back);
		panel_Exam.setLayout(null);
		
		label_Exam = new JLabel(new ImageIcon("image\\exam.png"));
		label_Exam.setOpaque(false);
		label_Exam.setBounds(166, 10, 803, 65);
		label_Exam_Back.add(label_Exam);
		
		label_Exam_Sub = new JLabel();
		label_Exam_Sub.setOpaque(false);
		label_Exam_Sub.setBounds(45, 0, 803, 65);
		label_Exam_Sub.setFont(new Font("나눔바른고딕", Font.PLAIN, 24));
		label_Exam_Sub.setForeground(Color.BLACK);
		label_Exam_Sub.setHorizontalAlignment(SwingConstants.CENTER);
		label_Exam.add(label_Exam_Sub);
		
		// 우상단 버튼 영역
		btn_Ready = new JButton(new ImageIcon("image\\ready.png"));
		btn_Ready.setFocusPainted(false);
		btn_Ready.setBorderPainted(false);
		btn_Ready.setContentAreaFilled(false);
		btn_Ready.setBounds(991, 11, 115, 65);
		label_Exam_Back.add(btn_Ready);
		
		btn_Exit = new JButton(new ImageIcon("image\\close.png"));
		btn_Exit.setPressedIcon(new ImageIcon("image\\close_clicked.png"));
		btn_Exit.setFocusPainted(false);
		btn_Exit.setBorderPainted(false);
		btn_Exit.setContentAreaFilled(false);
		btn_Exit.setBounds(1118, 11, 115, 65);
		label_Exam_Back.add(btn_Exit);
		
		
		// 로고 영역
		JLabel label_Logo = new JLabel(new ImageIcon("image\\logo.png"));
		label_Logo.setOpaque(false);
		label_Logo.setBorder(null);
		label_Logo.setBounds(12, 10, 142, 65);
		label_Exam_Back.add(label_Logo);
		
		// 채팅 영역
		panel_Chat = new JPanel();
		panel_Chat.setBounds(992, 105, 263, 567);
		panel_Main.add(panel_Chat);
		panel_Chat.setLayout(null);
		
		scrollPane = new JScrollPane(textArea);
		scrollPane.setFont(new Font("나눔바른고딕", Font.PLAIN, 13));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0, 0, 263, 535);
		panel_Chat.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setBorder(new LineBorder(new Color(127, 219, 254), 4, true));
		textArea.setFont(new Font("나눔바른고딕", Font.PLAIN, 13));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		textArea.setBackground(Color.WHITE);
		
		textField = new JTextField();
		textField.setBorder(new LineBorder(new Color(22, 103, 152), 4, true));
		textField.setBackground(Color.WHITE);
		textField.setBounds(0, 537, 263, 30);
		panel_Chat.add(textField);
		textField.setColumns(10);
		
		// 캔버스 영역
		JLabel labell_Canvas_Top = new JLabel(new ImageIcon("image\\canvas.png"));
		labell_Canvas_Top.setBounds(176, 105, 802, 34);
		labell_Canvas_Top.setOpaque(true);
		panel_Main.add(labell_Canvas_Top);
		
		panel_Canvas = new JPanel();
		panel_Canvas.setOpaque(false);
		panel_Canvas.setBounds(177, 105, 800, 500);
		panel_Main.add(panel_Canvas);
		panel_Canvas.setLayout(new BorderLayout(0, 0));
		
		panel_Option = new JPanel();
		panel_Option.setOpaque(true);
		panel_Option.setBackground(Color.WHITE);
		panel_Option.setBounds(10, 615, 967, 57);
		panel_Main.add(panel_Option);
		panel_Option.setLayout(null);
		
		btn_Color1 = new JButton(new ImageIcon("image\\red.png"));
		btn_Color1.setPressedIcon(new ImageIcon("image\\red_clicked.png"));
		btn_Color1.setFocusPainted(false);
		btn_Color1.setContentAreaFilled(false);
		btn_Color1.setBorder(null);
		btn_Color1.setBounds(179, 10, 75, 37);
		panel_Option.add(btn_Color1);
		
		btn_Color2 = new JButton(new ImageIcon("image\\green.png"));
		btn_Color2.setPressedIcon(new ImageIcon("image\\green_clicked.png"));
		btn_Color2.setFocusPainted(false);
		btn_Color2.setContentAreaFilled(false);
		btn_Color2.setBorder(null);
		btn_Color2.setBounds(266, 10, 75, 37);
		panel_Option.add(btn_Color2);
		
		btn_Color3 = new JButton(new ImageIcon("image\\blue.png"));
		btn_Color3.setPressedIcon(new ImageIcon("image\\blue_clicked.png"));
		btn_Color3.setFocusPainted(false);
		btn_Color3.setContentAreaFilled(false);
		btn_Color3.setBorder(null);
		btn_Color3.setBounds(353, 10, 75, 37);
		panel_Option.add(btn_Color3);
		
		btn_Color4 = new JButton(new ImageIcon("image\\yellow.png"));
		btn_Color4.setPressedIcon(new ImageIcon("image\\yellow_clicked.png"));
		btn_Color4.setFocusPainted(false);
		btn_Color4.setContentAreaFilled(false);
		btn_Color4.setBorder(null);
		btn_Color4.setBounds(440, 10, 75, 37);
		panel_Option.add(btn_Color4);
		
		btn_Color5 = new JButton(new ImageIcon("image\\black.png"));
		btn_Color5.setPressedIcon(new ImageIcon("image\\black_clicked.png"));
		btn_Color5.setFocusPainted(false);
		btn_Color5.setContentAreaFilled(false);
		btn_Color5.setBorder(null);
		btn_Color5.setBounds(527, 10, 75, 37);
		panel_Option.add(btn_Color5);
		
		btn_Erase = new JButton(new ImageIcon("image\\erase.png"));
		btn_Erase.setPressedIcon(new ImageIcon("image\\erase_clicked.png"));
		btn_Erase.setFocusPainted(false);
		btn_Erase.setContentAreaFilled(false);
		btn_Erase.setBorder(null);
		btn_Erase.setBounds(641, 10, 60, 37);
		panel_Option.add(btn_Erase);
		
		btn_EraseAll = new JButton(new ImageIcon("image\\eraseall.png"));
		btn_EraseAll.setPressedIcon(new ImageIcon("image\\eraseall_clicked.png"));
		btn_EraseAll.setFocusPainted(false);
		btn_EraseAll.setContentAreaFilled(false);
		btn_EraseAll.setBorder(null);
		btn_EraseAll.setBounds(713, 10, 90, 37);
		panel_Option.add(btn_EraseAll);
		
		btn_GG = new JButton(new ImageIcon("image\\gg.png"));
		btn_GG.setPressedIcon(new ImageIcon("image\\gg_clicked.png"));
		btn_GG.setFocusPainted(false);
		btn_GG.setContentAreaFilled(false);
		btn_GG.setBorder(null);
		btn_GG.setBounds(855, 10, 100, 37);
		panel_Option.add(btn_GG);
		
		// 타이머 영역
		JLabel label_Timer_Back = new JLabel(new ImageIcon("image\\time.png"));
		label_Timer_Back.setOpaque(true);
		label_Timer = new JLabel("00 : 00");
		label_Timer.setHorizontalTextPosition(SwingConstants.CENTER);
		label_Timer.setHorizontalAlignment(SwingConstants.CENTER);
		label_Timer.setFont(new Font("나눔바른고딕", Font.PLAIN, 24));
		label_Timer.setForeground(Color.BLACK);
		label_Timer_Back.setBounds(0, 0, 158, 57);
		label_Timer.setBounds(0, 10, 158, 57);
		panel_Option.add(label_Timer_Back);
		label_Timer_Back.add(label_Timer);
	}
}