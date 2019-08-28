package ksj.catchmind.client;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javafx.embed.swing.*;
import javafx.scene.media.*;
import ksj.catchmind.Protocol;
import ksj.catchmind.login.Login;

public class Client extends ClientGUI implements ActionListener {
	
	Canvas canvas = new Brush();
	CanvasHandler ch = new CanvasHandler();
	Color color;
	Graphics g;
	Graphics2D g2d;
	MediaPlayer p;
	
	static int port = 7777;
	String playerName, playerScore, playerIndex; // 클라이언트 이름, 점수, 번호(인덱스) 관리
	boolean gameStart, gameAuth; // 게임 시작 상태 & 문제 출제자 권한 상태
	
	public Client() {
		init();
	}
	
	void init() {
		String nickName = Login.nickName;
		String ip = Login.ip;
		
		try {
			Socket s = new Socket(ip, port);
			Sender sender = new Sender(s, nickName);
			Listener listener = new Listener(s);
			new Thread(sender).start();
			new Thread(listener).start();
			
			// 이벤트 리스너 설정 (채팅, 게임, 캔버스 컨트롤)
			textField.addKeyListener(new Sender(s, nickName));
			canvas.setBackground(Color.WHITE);
			panel_Canvas.add(canvas, BorderLayout.CENTER);
			canvas.addMouseMotionListener(new Sender(s, nickName)); canvas.addMouseMotionListener(ch);
			btn_Color1.addActionListener(new Sender(s, nickName)); btn_Color1.addActionListener(ch);
			btn_Color2.addActionListener(new Sender(s, nickName)); btn_Color2.addActionListener(ch);
			btn_Color3.addActionListener(new Sender(s, nickName)); btn_Color3.addActionListener(ch);
			btn_Color4.addActionListener(new Sender(s, nickName)); btn_Color4.addActionListener(ch);
			btn_Color5.addActionListener(new Sender(s, nickName)); btn_Color5.addActionListener(ch);
			btn_Erase.addActionListener(new Sender(s, nickName)); btn_Erase.addActionListener(ch);
			btn_EraseAll.addActionListener(new Sender(s, nickName)); btn_EraseAll.addActionListener(ch);
			btn_GG.addActionListener(new Sender(s, nickName));
			btn_Ready.addActionListener(new Sender(s, nickName));
			btn_Exit.addActionListener(this);
		} catch(UnknownHostException uh) {
			JOptionPane.showMessageDialog(null, "호스트를 찾을 수 없습니다!", "ERROR", JOptionPane.WARNING_MESSAGE);
		} catch(IOException io) {
			JOptionPane.showMessageDialog(null, "서버 접속 실패!\n서버가 닫혀 있습니다!", "ERROR", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
	
	// 게임 종료 버튼 액션 이벤트
	public void actionPerformed(ActionEvent e) { 
		if(e.getSource() == btn_Exit) {
			int select = JOptionPane.showConfirmDialog(null, "정말로 게임을 종료하시겠습니까?", "EXIT", JOptionPane.OK_CANCEL_OPTION);
			if(select == JOptionPane.YES_OPTION) System.exit(0);
		}
	}
	
	// 송신 처리
	class Sender extends Thread implements KeyListener, ActionListener, MouseMotionListener {
		
		DataOutputStream dos;
		Socket s;
		String nickName;

		Sender(Socket s, String nickName) throws IOException {
			this.s = s;
			dos = new DataOutputStream(this.s.getOutputStream());
			this.nickName = nickName;
		}

		public void run() {
			try {
				dos.writeUTF(nickName);
			} catch(IOException io) {
				io.printStackTrace();
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			try {
				if(e.getSource() == btn_Ready) { // 게임 준비 버튼
					dos.writeUTF(Protocol.CHAT + "[ " + nickName + " 님 준비 완료! ]");
					dos.writeUTF(Protocol.READY);
					btn_Ready.setEnabled(false);
				} else if(e.getSource() == btn_Color1 && gameAuth == true) { // 색상 선택 버튼
					dos.writeUTF(Protocol.CHANGE_COLOR + "Red");
				} else if(e.getSource() == btn_Color2 && gameAuth == true) {
					dos.writeUTF(Protocol.CHANGE_COLOR + "Green");
				} else if(e.getSource() == btn_Color3 && gameAuth == true) {
					dos.writeUTF(Protocol.CHANGE_COLOR + "Blue");
				} else if(e.getSource() == btn_Color4 && gameAuth == true) {
					dos.writeUTF(Protocol.CHANGE_COLOR + "Yellow");
				} else if(e.getSource() == btn_Color5 && gameAuth == true) {
					dos.writeUTF(Protocol.CHANGE_COLOR + "Black");
				} else if(e.getSource() == btn_Erase && gameAuth == true) { // 지우기 버튼
					dos.writeUTF(Protocol.ERASE);
				} else if(e.getSource() == btn_EraseAll && gameAuth == true) { // 전체 지우기 버튼
					dos.writeUTF(Protocol.ERASE_ALL);
				} else if(e.getSource() == btn_GG && gameAuth == true) { // 게임 포기 버튼
					dos.writeUTF(Protocol.GG);
				}
				dos.flush();
			} catch(IOException io) {
				io.printStackTrace();
			}
		}
		
		public void keyReleased(KeyEvent e) { // 채팅 입력(Enter Key)
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				String chat = textField.getText();
				textField.setText("");
				try {
					dos.writeUTF(Protocol.CHAT + nickName + " : " + chat);
					dos.flush();
				} catch(IOException io){
					io.printStackTrace();
				}
			}
		}
		public void keyTyped(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {}
		
		public void mouseDragged(MouseEvent e){ // 마우스 좌표 송신
		    try{
		    	if(gameAuth == true){
		    		int x = e.getX(); int y = e.getY();
		    		dos.writeUTF(Protocol.MOUSE_XY + x + "." + y);
		    		dos.flush();
		    	}
		    }catch(IOException io){
		    	io.printStackTrace();
		    }
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
	}

	// 수신 처리
	class Listener extends Thread {
		
		Socket s;
		DataInputStream dis;

		Listener(Socket s) throws IOException {
			this.s = s;
			dis = new DataInputStream(this.s.getInputStream());
		}

		public void run(){
			while(dis != null) {
				try {
					String msg = dis.readUTF();
					if(msg.startsWith(Protocol.UPD_CLIST)) { // 프로토콜 : 클라이언트 목록 갱신
						playerName = msg.substring(7, msg.indexOf(" "));
						playerScore = msg.substring(msg.indexOf(" ") + 1, msg.indexOf("#"));
						playerIndex = msg.substring(msg.indexOf("#") + 1);
						addClientList();
					} else if(msg.startsWith(Protocol.START)) { // 프로토콜 : 게임 시작 및 타이머 작동
						gameStart = true;
						g = canvas.getGraphics(); // 캔버스 설정 초기화
						g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
						Brush canvas2 = (Brush)canvas;
						canvas2.color = Color.BLACK;
						color = Color.BLACK;
						bgm(Protocol.BGM_PLAY); // BGM 재생
					} else if(msg.equals(Protocol.GG)) { // 프로토콜 : 출제자 게임 포기
						gameStart = false;
						gameAuth = false;
						textField.setEnabled(true);
						btn_Ready.setEnabled(true);
						bgm(Protocol.BGM_STOP); // BGM 정지
					} else if(msg.equals(Protocol.END)) { // 프로토콜 : 게임 종료
						gameStart = false;
						gameAuth = false;
						textField.setEnabled(true);
						btn_Ready.setEnabled(true);
						label_Timer.setText("00 : 00");
						bgm(Protocol.BGM_STOP); // BGM 정지
					} else if(msg.startsWith(Protocol.EXAM)) { // 프로토콜 : 문제 출제
						if(gameAuth == true) {
							label_Exam_Sub.setText(msg.substring(7));
						} else {
							label_Exam_Sub.setText(" ??? ");
						}
					} else if(msg.startsWith(Protocol.AUTH)) { // 프로토콜 : 출제자 권한 부여
						if(Login.nickName.equals(msg.substring(7))) {
							gameAuth = true;
							textArea.append("\n[ 당신이 문제 출제자입니다 !! ]" + "\n\n");
							textField.setEnabled(false); // 문제 출제자일 경우 채팅 불가
						}
					} else if(msg.startsWith(Protocol.MOUSE_XY)){ // 프로토콜 : 캔버스 공유 (마우스 좌표 수신)
						if(gameAuth == false) {
							int tempX = Integer.parseInt(msg.substring(7, msg.indexOf("."))); 
							int tempY = Integer.parseInt(msg.substring(msg.indexOf(".") + 1));
							g = canvas.getGraphics();
							g2d = (Graphics2D)g;
							g2d.setColor(color);
				            g2d.setStroke(new BasicStroke(6));
				            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		                    g.drawLine(tempX, tempY, tempX, tempY);
						}
					} else if(msg.startsWith(Protocol.TIMER)) { // 프로토콜 : 타이머 시간 표시
						label_Timer.setText(msg.substring(7));
					} else if(msg.startsWith(Protocol.CHANGE_COLOR)) { // 프로토콜 : 컬러 설정
						String temp = msg.substring(7);
						switch(temp) {
							case "Red": color = Color.RED; break;
							case "Green": color = Color.GREEN; break;
							case "Blue": color = Color.BLUE; break;
							case "Yellow": color = Color.YELLOW; break;
							case "Black": color = Color.BLACK; break;
						}
					} else if(msg.equals(Protocol.ERASE)) { // 프로토콜 : 지우기
						color = Color.WHITE;
					} else if(msg.equals(Protocol.ERASE_ALL)) { // 프로토콜 : 모두 지우기
						g = canvas.getGraphics();
						g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					} else { // 일반 채팅 출력
						textArea.append(msg + "\n");
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()); // 채팅 영역 스크롤 바 항상 최하단으로 이동
					}
				} catch(IOException io) {
					textArea.append("[ 서버와의 연결이 끊어졌습니다. 닉네임 중복, 서버 정원 초과, 게임 진행 중인 경우 연결이 거부됩니다. ]\n[ 3초 후 클라이언트를 종료합니다.. ]");
					try {
						TimeUnit.MILLISECONDS.sleep(3000);
					} catch(InterruptedException it) {
						it.printStackTrace();
					} finally {
						System.exit(0);
					}
				}
			}
		}
		
		ImageIcon ii;
		void addClientList(){ // 클라이언트 목록 추가
			switch(playerIndex) {
				case "0":
					ii = new ImageIcon("image\\p1.png");
					ii.getImage().flush();
					label_Client1.setIcon(ii);
					label_Client1_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
					deleteClientList();
					break;
				case "1":
					ii = new ImageIcon("image\\p2.png");
					ii.getImage().flush();
					label_Client2.setIcon(ii);
					label_Client2_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
					deleteClientList();
					break;
				case "2":
					ii = new ImageIcon("image\\p3.png");
					ii.getImage().flush();
					label_Client3.setIcon(ii);
					label_Client3_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
					deleteClientList();
					break;
				case "3":
					ii = new ImageIcon("image\\p4.png");
					ii.getImage().flush();
					label_Client4.setIcon(ii);
					label_Client4_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
					deleteClientList();
					break;
			}
		}
		
		void deleteClientList() { // 클라이언트 목록 제거
			ii = new ImageIcon("image\\p0.png");
			
			switch(playerIndex) {
				case "0":
					label_Client2.setIcon(ii);
					label_Client2_Sub.setText("[ 닉네임 / 점수 ]");
					label_Client3.setIcon(ii);
					label_Client3_Sub.setText("[ 닉네임 / 점수 ]");
					label_Client4.setIcon(ii);
					label_Client4_Sub.setText("[ 닉네임 / 점수 ]");
					break;
				case "1":
					label_Client3.setIcon(ii);
					label_Client3_Sub.setText("[ 닉네임 / 점수 ]");
					label_Client4.setIcon(ii);
					label_Client4_Sub.setText("[ 닉네임 / 점수 ]");
					break;
				case "2":
					label_Client4.setIcon(ii);
					label_Client4_Sub.setText("[ 닉네임 / 점수 ]");
					break;
			}
		}
		
		void bgm(String play) { // BGM 재생 및 정지
			try {
				if(play.equals(Protocol.BGM_PLAY)) {
					new JFXPanel();
					File f = new File("bgm\\bgm.mp3");
					Media bgm = new Media(f.toURI().toURL().toString());
			        p = new MediaPlayer(bgm);
					p.play();
				} else if(play.equals(Protocol.BGM_STOP)) {
					p.stop();
					p.setMute(true);
					p.dispose();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 캔버스 제어
	class CanvasHandler extends JFrame implements ActionListener, MouseMotionListener {	
		
		int x1, x2, y1, y2;
		
		public void mouseDragged(MouseEvent e) {
		    x1 = e.getX(); 
		    y1 = e.getY();
		    ((Brush)canvas).x1 = x1; 
		    ((Brush)canvas).y1 = y1;
		    
		    canvas.repaint();
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
		
		public void actionPerformed(ActionEvent e) {
			Object obj = e.getSource();
			Brush canvas2 = (Brush)canvas;
		   
			if(gameAuth == true) { // 출제자 권한을 가진 상태여야만 캔버스 조작 가능
			    if(obj == btn_Color1) {
				    canvas2.color = Color.RED;
			    } else if(obj == btn_Color2) {
			    	canvas2.color = Color.GREEN;
			    } else if(obj == btn_Color3) {
			    	canvas2.color = Color.BLUE;
			    } else if(obj == btn_Color4) {
			    	canvas2.color = Color.YELLOW;
			    } else if(obj == btn_Color5) {
			    	canvas2.color = Color.BLACK;
			    } else if(obj == btn_Erase) {
			    	canvas2.color = canvas.getBackground();
			    } else if(obj == btn_EraseAll) {
			    	Graphics g = canvas2.getGraphics();
				    g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); 
			    }
			}
		}
	}
	
	// 캔버스 브러쉬 설정
	class Brush extends Canvas {
		
		int x1, x2, y1, y2;
		Color color = Color.BLACK;

		void paintComponent(Graphics g) {
			if(gameStart == true && gameAuth == true) { // 게임이 시작되었고, 출제자 권한을 가진 상태여야 그리기 가능
				Graphics2D g2d = (Graphics2D)g;
	            g2d.setColor(color);
	            g2d.setStroke(new BasicStroke(6));
	            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2d.drawLine(x1, y1, x1, y1);
			}
		}
		
		public void update(Graphics g) {
			paintComponent(g);
		}
	}
}