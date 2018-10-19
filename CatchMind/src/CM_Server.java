import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class CM_Server extends JFrame implements ActionListener
{
	JPanel contentPane, panel_Main, panel_TextArea, panel_Btn;
	JScrollPane scrollPane;
	JTextArea textArea;
	JLabel label_ServerStatus;
	JButton btn_ServerStart, btn_ServerClose;
	
	ServerSocket ss;
	Socket s;
	int port = 7777;
	public static final int MAX_CLIENT = 4;
	int readyPlayer; // 게임 준비된 클라이언트 카운트
	int score;
	boolean gameStart; // 게임 시작 상태
	String line = "";
	LinkedHashMap<String, DataOutputStream> clientList = new LinkedHashMap<String, DataOutputStream>(); // 클라이언트 이름, 스트림 관리
	LinkedHashMap<String, Integer> clientInfo = new LinkedHashMap<String, Integer>(); // 클라이언트 이름, 점수 관리
	
	public void init(){
		setTitle("JAVA CatchMind Server");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 1, 10, 0));
		
		panel_Main = new JPanel();
		contentPane.add(panel_Main);
		panel_Main.setLayout(new BoxLayout(panel_Main, BoxLayout.Y_AXIS));
		
		label_ServerStatus = new JLabel("[ Server Waiting .. ]");
		label_ServerStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
		label_ServerStatus.setPreferredSize(new Dimension(96, 50));
		panel_Main.add(label_ServerStatus);
		label_ServerStatus.setHorizontalTextPosition(SwingConstants.CENTER);
		label_ServerStatus.setHorizontalAlignment(SwingConstants.CENTER);
		label_ServerStatus.setFont(new Font("나눔바른고딕", Font.PLAIN, 20));
		
		panel_TextArea = new JPanel();
		panel_Main.add(panel_TextArea);
		panel_TextArea.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		scrollPane.setBorder(new LineBorder(Color.DARK_GRAY));
		panel_TextArea.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		panel_Btn = new JPanel();
		panel_Btn.setPreferredSize(new Dimension(10, 43));
		panel_Btn.setAutoscrolls(true);
		panel_Main.add(panel_Btn);
		panel_Btn.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		btn_ServerStart = new JButton(" 서버 시작 ");
		btn_ServerStart.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_ServerStart.setPreferredSize(new Dimension(120, 40));
		btn_ServerStart.setFocusPainted(false);
		btn_ServerStart.setFont(new Font("나눔바른고딕", Font.BOLD, 16));
		btn_ServerStart.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn_ServerStart.setForeground(Color.WHITE);
		btn_ServerStart.setBackground(Color.DARK_GRAY);
		btn_ServerStart.setBorder(null);
		panel_Btn.add(btn_ServerStart);
		btn_ServerStart.addActionListener(this);
		
		btn_ServerClose = new JButton(" 서버 종료 ");
		btn_ServerClose.setHorizontalTextPosition(SwingConstants.CENTER);
		btn_ServerClose.setPreferredSize(new Dimension(120, 40));
		btn_ServerClose.setFocusPainted(false);
		btn_ServerClose.setFont(new Font("나눔바른고딕", Font.BOLD, 16));
		btn_ServerClose.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn_ServerClose.setForeground(Color.WHITE);
		btn_ServerClose.setBackground(Color.DARK_GRAY);
		btn_ServerClose.setBorder(null);
		panel_Btn.add(btn_ServerClose);
		btn_ServerClose.addActionListener(this);
		btn_ServerClose.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e){ // '서버 시작 & 종료' 버튼 이벤트
		if(e.getSource() == btn_ServerStart){
			new Thread(){
				public void run() {
					try{
						Collections.synchronizedMap(clientList);
						ss = new ServerSocket(port);
						label_ServerStatus.setText("[ Server Started ]");
						textArea.append("[ 서버가 시작되었습니다 ]" + "\n");
						btn_ServerStart.setEnabled(false);
						btn_ServerClose.setEnabled(true);
						while(true){
							s = ss.accept();
							if((clientList.size() + 1) > MAX_CLIENT || gameStart == true){ // 정원이 초과되었거나, 게임중이라면 소켓 연결 거부
								s.close();
							}else{
								Thread gm = new GameManager(s);
								gm.start();
							}
						}
					}catch(IOException io){}
				}
			}.start();
		}else if(e.getSource() == btn_ServerClose){
			int select = JOptionPane.showConfirmDialog(null, "서버를 정말 종료하시겠습니까?", "JAVA CatchMind Server", JOptionPane.OK_CANCEL_OPTION);
			try{
				if(select == JOptionPane.YES_OPTION){
					ss.close();
					label_ServerStatus.setText("[ Server Closed ]");
					textArea.append("[ 서버가 종료되었습니다 ]" + "\n");
					btn_ServerStart.setEnabled(true);
					btn_ServerClose.setEnabled(false);
				}
			}catch(IOException io){}
		}
	}
	
	public void showSystemMsg(String msg){ // 시스템 메시지 및 명령어 송신
		Iterator<String> it = clientList.keySet().iterator();
		while(it.hasNext()){
			try{
				DataOutputStream dos = clientList.get(it.next());
				dos.writeUTF(msg);
				dos.flush();
			}catch(IOException io){}
		}
	}

	// 내부 클래스 (게임 관리 및 통제)
	public class GameManager extends Thread
	{
		Socket s;
		DataInputStream dis;
		DataOutputStream dos;
							
		public GameManager(Socket s){
			this.s = s;
			try{
				dis = new DataInputStream(this.s.getInputStream());
				dos = new DataOutputStream(this.s.getOutputStream());
			}catch(IOException io){}
		}
		
		public void run(){
			String clientName = "";
			try{
				clientName = dis.readUTF();
				if(!clientList.containsKey(clientName)){ // 중복 닉네임 방지
					clientList.put(clientName, dos);
					clientInfo.put(clientName, score);
				}else if(clientList.containsKey(clientName)){
					s.close(); // 닉네임 중복시, 소켓 연결 거부
				}
				showSystemMsg("[ " + clientName + "님이 입장하셨습니다. ]\n(현재 접속자 수 : " + clientList.size() + "명 / 4명)");
				textArea.append("[ 현재 접속자 명단 (총 " + clientList.size() + "명 접속중) ]\n");
				Iterator<String> it1 = clientList.keySet().iterator();
				while(it1.hasNext()) textArea.append(it1.next() + "\n");
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
				setClientInfo(); // 클라이언트 목록 갱신
				while(dis != null){
					String msg = dis.readUTF();
					filter(msg); // 명령어 필터링
				}
			}catch(IOException io){
				clientList.remove(clientName); clientInfo.remove(clientName); // 클라이언트 퇴장시 제거
				closeAll();
				if(clientList.isEmpty() == true){ // 서버에 남은 클라이언트가 하나도 없다면, 서버 닫기
					try{
						ss.close(); System.exit(0);
					}catch(IOException e){}
				}
				showSystemMsg("[ " + clientName + "님이 퇴장하셨습니다. ]\n(현재 접속자 수 : " + clientList.size() + "명 / 4명)");
				textArea.append("[ 현재 접속자 명단 (총 " + clientList.size() + "명 접속중) ]\n");
				Iterator<String> it1 = clientList.keySet().iterator();
				while(it1.hasNext()) textArea.append(it1.next() + "\n");
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
				setClientInfo(); // 클라이언트 목록 갱신
				readyPlayer = 0; // 새로운 클라이언트가 접속해도 게임 시작에 문제가 없도록 변수 초기화
				gameStart = false;
				showSystemMsg("//GmEnd"); // 클라이언트 퇴장시, 즉시 라운드 종료
			}
		}
		
		public void closeAll(){
			try{
				if(dos != null) dos.close();
				if(dis != null) dis.close();
				if(s != null) s.close();
			}catch(IOException ie){}
		}
		
		public void setClientInfo(){
			String[] keys = new String[clientInfo.size()];
			int[] values = new int[clientInfo.size()];
			int index = 0;
			for(Map.Entry<String, Integer> mapEntry : clientInfo.entrySet()){
			    keys[index] = mapEntry.getKey();
			    values[index] = mapEntry.getValue();
			    index++;
			}
			for(int i=0; i<clientList.size(); i++){
				showSystemMsg("//CList" + keys[i] + " " + values[i] + "#" + i); // 명령어 : 클라이언트 목록 갱신
			}
		}
		
		public void filter(String msg) { // 명령어 필터링
			String temp = msg.substring(0, 7);
			if(temp.equals("//Chat ")){ // 명령어 : 일반 채팅
				answerCheck(msg.substring(7).trim());
				showSystemMsg(msg.substring(7));
			}else if(temp.equals("//Ready")){ // 명령어 : 클라이언트 준비 상태 체크
				 readyPlayer++;
				 if(readyPlayer >= 2 && readyPlayer == clientList.size()){ // 2명 이상 && 모든 클라이언트가 준비되었을 경우
					 for(int i=3; i>0; i--){
						 try{
						 	showSystemMsg("[ 모든 참여자들이 준비되었습니다. ]\n[ " + i + "초 후 게임을 시작합니다 .. ]");
						 	Thread.sleep(1000);
						 }catch(InterruptedException ie){}
					 }
					 ArrayList<String> authList = new ArrayList<String>(); // 문제 출제자 랜덤 선택
					 Iterator<String> it = clientList.keySet().iterator();
					 while(it.hasNext()) authList.add(it.next());
					 Random rd = new Random();
					 showSystemMsg("//Auth " + authList.get(rd.nextInt(authList.size()))); // 명령어 : 문제 출제자 랜덤 선택
					 Exam ex = new Exam(); ex.start(); // 문제 출제
					 StopWatch tm = new StopWatch(); tm.start(); // 타이머 시작
					 gameStart = true;
					 showSystemMsg("//Start"); // 명령어 : 게임 시작
				 }
			}else if(temp.equals("//Mouse")){ // 명령어 : 마우스 좌표 수신
				showSystemMsg(msg);
			}else if(temp.equals("//Color")){ // 명령어 : 컬러 설정
				showSystemMsg(msg);
			}else if(temp.equals("//Erase")){ // 명령어 : 지우기
				showSystemMsg(msg);
			}else if(temp.equals("//ErAll")){ // 명령어 : 모두 지우기
				showSystemMsg(msg);
			}else if(temp.equals("//GmGG ")){ // 명령어 : 게임 종료 (출제자가 게임을 포기했을 경우)
				showSystemMsg("[ 출제자가 게임을 포기했습니다 !! ]");
				showSystemMsg(msg);
				readyPlayer = 0;
				gameStart = false;
			}else if(temp.equals("//GmEnd")){ // 명령어 : 게임 종료 (시간 초과나 이탈자 발생으로 게임이 종료되는 경우)
				showSystemMsg("[ 게임이 종료되었습니다 !! ]");
				showSystemMsg(msg);
				readyPlayer = 0;
				gameStart = false;
			}
		}
		
		public void answerCheck(String msg){ // 정답 체크
			String tempNick = msg.substring(0, msg.indexOf(" ")); // 정답자 닉네임 체크
			String tempAns = msg.substring(msg.lastIndexOf(" ") + 1); // 정답 내용 체크
			if(tempAns.equals(line) && gameStart == true){ // 정답자 중복 방지를 위한 게임 시작 상태 체크
				showSystemMsg("//GmEnd");
				gameStart = false;
				readyPlayer = 0; // 새로운 게임을 시작하기 위한 변수 초기화
				showSystemMsg("[ " + tempNick + "님 정답 !! ]");
				clientInfo.put(tempNick, clientInfo.get(tempNick) + 1); // 정답자 점수 추가
				setClientInfo(); // 점수 표시를 위한 클라이언트 목록 갱신
			}
		}
	}
	
	// 내부 클래스 - 랜덤 문제 출제
	class Exam extends Thread
	{
		int i = 0;
		BufferedReader br;

		public void run(){
			Random r = new Random();
			int n = r.nextInt(52);
			try{
				FileReader fr = new FileReader("wordlist.txt");
				br = new BufferedReader(fr);
				for(i=0;i<=n;i++) line = br.readLine();
				showSystemMsg("//RExam" + line);
			}catch(IOException ie){}
		}
	}
	
	// 내부 클래스 - 타이머
	class StopWatch extends Thread
	{
		long preTime = System.currentTimeMillis();
		
		public void run() {
			try{
				while(gameStart == true){
					sleep(10);
					long time = System.currentTimeMillis() - preTime;
					showSystemMsg("//Timer" + (toTime(time)));
					if(toTime(time).equals("00 : 00")){
						showSystemMsg("//GmEnd"); // 시간 초과시, 게임 종료
						readyPlayer = 0;
						gameStart = false;
						break;
					}else if(readyPlayer == 0){
						break;
					}
				}
			}catch (Exception e){}
		}
		
		String toTime(long time){
			int m = (int)(3-(time / 1000.0 / 60.0));
			int s = (int)(60-(time % (1000.0 * 60) / 1000.0));
			return String.format("%02d : %02d", m, s);
		}
	}
	
	public static void main(String[] args){
		EventQueue.invokeLater(new Runnable(){
			public void run(){
				try{
					CM_Server cms = new CM_Server();
					cms.init();
					cms.setVisible(true);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
}