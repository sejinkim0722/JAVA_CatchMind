import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javafx.embed.swing.*;
import javafx.scene.media.*;

public class CM_Client extends CM_Client_GUI implements ActionListener
{
	Canvas canvas = new Brush();
	CanvasHandler ch = new CanvasHandler();
	Color color;
	Graphics g;
	Graphics2D g2d;
	MediaPlayer p;
	
	int port = 7777;
	String playerName, playerScore, playerIdx; // 클라이언트 이름, 점수, 인덱스 관리
	boolean gameStart, auth; // 게임 시작 상태 & 출제자 권한 체크 
	
	public CM_Client(){
		init();
	}
	
	void init(){
		String nickName = CM_Login.nickName;
		String ip = CM_Login.ip;
		try{
			Socket s = new Socket(ip, port);
			Sender sender = new Sender(s, nickName);
			Listener listener = new Listener(s);
			new Thread(sender).start();
			new Thread(listener).start();
			
			// 이벤트 리스너 추가 (채팅 & 게임 & 캔버스 컨트롤)
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
		}catch(UnknownHostException uh){
			JOptionPane.showMessageDialog(null, "호스트를 찾을 수 없습니다!", "ERROR", JOptionPane.WARNING_MESSAGE);
		}catch(IOException io){
			JOptionPane.showMessageDialog(null, "서버 접속 실패!\n서버가 닫혀 있는 것 같습니다.", "ERROR", JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
	
	public void actionPerformed(ActionEvent e){ // 종료 버튼 액션 이벤트 처리
		if(e.getSource() == btn_Exit){
			int select = JOptionPane.showConfirmDialog(null, "정말 게임을 종료하시겠습니까?", "Exit", JOptionPane.OK_CANCEL_OPTION);
			if(select == JOptionPane.YES_OPTION) System.exit(0);
		}
	}
	
	// 내부 클래스 - 송신
	class Sender extends Thread implements KeyListener, ActionListener, MouseMotionListener
	{
		DataOutputStream dos;
		Socket s;
		String nickName;

		Sender(Socket s, String nickName){
			this.s = s;
			try{
				dos = new DataOutputStream(this.s.getOutputStream());
				this.nickName = nickName;
			}catch(IOException io){}
		}

		public void run(){
			try{
				dos.writeUTF(nickName);
			}catch(IOException io){}
		}
		
		public void actionPerformed(ActionEvent e){
			if(e.getSource() == btn_Ready){ // '준비' 버튼
				try{
					dos.writeUTF(CM_ENUM.CHAT + "[ " + nickName + " 님 준비 완료 ! ]");
					dos.flush();
					dos.writeUTF(CM_ENUM.READY);
					dos.flush();
					btn_Ready.setEnabled(false);
				}catch(IOException io){}
			}else if(e.getSource() == btn_Color1 && auth == true){ // 색상 설정 버튼
				try{
					dos.writeUTF(CM_ENUM.CHANGE_COLOR + "Red");
					dos.flush();
				}catch(IOException io){}
			}else if(e.getSource() == btn_Color2 && auth == true){
				try{
					dos.writeUTF(CM_ENUM.CHANGE_COLOR + "Green");
					dos.flush();
				}catch(IOException io){}
			}else if(e.getSource() == btn_Color3 && auth == true){
				try{
					dos.writeUTF(CM_ENUM.CHANGE_COLOR + "Blue");
					dos.flush();
				}catch(IOException io){}
			}else if(e.getSource() == btn_Color4 && auth == true){
				try{
					dos.writeUTF(CM_ENUM.CHANGE_COLOR + "Yellow");
					dos.flush();
				}catch(IOException io){}
			}else if(e.getSource() == btn_Color5 && auth == true){
				try{
					dos.writeUTF(CM_ENUM.CHANGE_COLOR + "Black");
					dos.flush();
				}catch(IOException io){}
			}else if(e.getSource() == btn_Erase && auth == true){ // '지우기' 버튼
				try{
					dos.writeUTF(CM_ENUM.ERASE);
					dos.flush();
				}catch(IOException io){}
			}else if(e.getSource() == btn_EraseAll && auth == true){ // '모두 지우기' 버튼
				try{
					if(auth == true){
						dos.writeUTF(CM_ENUM.ERASE_ALL);
						dos.flush();
					}
				}catch(IOException io){}
			}else if(e.getSource() == btn_GG && auth == true){ // '포기' 버튼
				try{
					if(auth == true){
						dos.writeUTF(CM_ENUM.GG);
						dos.flush();
					}
				}catch(IOException io){}
			}
		}
		
		public void keyReleased(KeyEvent e){ // 채팅 입력
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				String chat = textField.getText();
				textField.setText("");
				try{
					dos.writeUTF(CM_ENUM.CHAT + nickName + " : " + chat);
					dos.flush();
				}catch(IOException io){}
			}
		}
		public void keyTyped(KeyEvent e){}
		public void keyPressed(KeyEvent e){}
		
		public void mouseDragged(MouseEvent e){ // 마우스 좌표 전송
		    try{
		    	if(auth == true){
		    		int x = e.getX(); int y = e.getY();
		    		dos.writeUTF(CM_ENUM.MOUSE_XY + x + "." + y);
		    		dos.flush();
		    	}
		    }catch(IOException io){}
		}
		public void mousePressed(MouseEvent e){}
		public void mouseMoved(MouseEvent e){}
	}

	// 내부 클래스 - 수신
	class Listener extends Thread
	{
		Socket s;
		DataInputStream dis;

		Listener(Socket s){
			this.s = s;
			try{
				dis = new DataInputStream(this.s.getInputStream());
			}catch(IOException io){}
		}

		public void run(){
			while(dis != null){
				try{
					String msg = dis.readUTF();
					if(msg.startsWith(CM_ENUM.UPD_CLIST)){ // 명령어 : 클라이언트 목록 갱신
						playerName = msg.substring(7, msg.indexOf(" "));
						playerScore = msg.substring(msg.indexOf(" ") + 1, msg.indexOf("#"));
						playerIdx = msg.substring(msg.indexOf("#") + 1);
						updateClientList(); // 클라이언트 목록 갱신
					}else if(msg.startsWith(CM_ENUM.START)){ // 명령어 : 게임 시작 ( + 타이머)
						gameStart = true;
						g = canvas.getGraphics(); // 캔버스 설정 초기화
						g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
						Brush canvas2 = (Brush)canvas;
						canvas2.color = Color.BLACK;
						color = Color.BLACK;
						bgm(CM_ENUM.BGM_PLAY); // BGM 재생
					}else if(msg.equals(CM_ENUM.GG)){ // 명령어 : 출제자 게임 포기
						gameStart = false;
						auth = false;
						textField.setEnabled(true);
						btn_Ready.setEnabled(true);
						bgm(CM_ENUM.BGM_STOP); // BGM 정지
					}else if(msg.equals(CM_ENUM.END)){ // 명령어 : 게임 종료
						gameStart = false;
						auth = false;
						textField.setEnabled(true);
						btn_Ready.setEnabled(true);
						label_Timer.setText("00 : 00");
						bgm(CM_ENUM.BGM_STOP); // BGM 정지
					}else if(msg.startsWith(CM_ENUM.EXAM)){ // 명령어 : 문제 랜덤 출제
						if(auth == true){
							label_Exam_Sub.setText(msg.substring(7));
						}else{
							label_Exam_Sub.setText(" ??? ");
						}
					}else if(msg.startsWith(CM_ENUM.AUTH)){ // 명령어 : 출제자 권한 부여
						if(CM_Login.nickName.equals(msg.substring(7))){
							auth = true;
							textArea.append("\n[ 당신이 문제 출제자입니다 !! ]" + "\n\n");
							textField.setEnabled(false);
						}
					}else if(msg.startsWith(CM_ENUM.MOUSE_XY)){ // 명령어 : 캔버스 공유 (마우스 좌표 수신)
						if(auth == false){
							int tempX = Integer.parseInt(msg.substring(7, msg.indexOf("."))); 
							int tempY = Integer.parseInt(msg.substring(msg.indexOf(".") + 1));
							g = canvas.getGraphics();
							g2d = (Graphics2D)g;
							g2d.setColor(color);
				            g2d.setStroke(new BasicStroke(6));
				            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		                    g.drawLine(tempX, tempY, tempX, tempY);
						}
					}else if(msg.startsWith(CM_ENUM.TIMER)){ // 명령어 : 타이머 시간 표시
						label_Timer.setText(msg.substring(7));
					}else if(msg.startsWith(CM_ENUM.CHANGE_COLOR)){ // 명령어 : 컬러 설정
						String temp = msg.substring(7);
						switch(temp){
							case "Red": color = Color.RED; break;
							case "Green": color = Color.GREEN; break;
							case "Blue": color = Color.BLUE; break;
							case "Yellow": color = Color.YELLOW; break;
							case "Black": color = Color.BLACK; break;
						}
					}else if(msg.equals(CM_ENUM.ERASE)){ // 명령어 : 지우기
						color = Color.WHITE;
					}else if(msg.equals(CM_ENUM.ERASE_ALL)){ // 명령어 : 모두 지우기
						g = canvas.getGraphics();
						g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					}else{ // 일반 채팅 출력
						textArea.append(msg + "\n");
						scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
					}
				}catch(IOException io){
					textArea.append("[ 서버와의 연결이 끊어졌습니다. 닉네임 중복, 서버 정원 초과, 게임 진행중인 경우 연결이 거부됩니다. ]\n[ 3초 후 프로그램을 종료합니다 .. ]");
					try{
						Thread.sleep(3000);
						System.exit(0);
					}catch(InterruptedException it){}
				}
			}
		}
		
		public void updateClientList(){ // 클라이언트 목록 추가
			ImageIcon ii;
			if(Integer.parseInt(playerIdx) == 0){
				ii = new ImageIcon("image\\p1.png");
				ii.getImage().flush();
				label_Client1.setIcon(ii);
				label_Client1_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
				deleteClientList();
			}else if(Integer.parseInt(playerIdx) == 1){
				ii = new ImageIcon("image\\p2.png");
				ii.getImage().flush();
				label_Client2.setIcon(ii);
				label_Client2_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
				deleteClientList();
			}else if(Integer.parseInt(playerIdx) == 2){
				ii = new ImageIcon("image\\p3.png");
				ii.getImage().flush();
				label_Client3.setIcon(ii);
				label_Client3_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
				deleteClientList();
			}else if(Integer.parseInt(playerIdx) == 3){
				ii = new ImageIcon("image\\p4.png");
				ii.getImage().flush();
				label_Client4.setIcon(ii);
				label_Client4_Sub.setText("[" + playerName + " / " + "점수: " + playerScore + "]");
				deleteClientList();
			}
		}
		
		public void deleteClientList(){ // 클라이언트 목록 제거
			ImageIcon ii2;
			ii2 = new ImageIcon("image\\p0.png");
			if(Integer.parseInt(playerIdx) == 0){
				label_Client2.setIcon(ii2);
				label_Client2_Sub.setText("[ 닉네임 / 점수 ]");
				label_Client3.setIcon(ii2);
				label_Client3_Sub.setText("[ 닉네임 / 점수 ]");
				label_Client4.setIcon(ii2);
				label_Client4_Sub.setText("[ 닉네임 / 점수 ]");
			}else if(Integer.parseInt(playerIdx) == 1){
				label_Client3.setIcon(ii2);
				label_Client3_Sub.setText("[ 닉네임 / 점수 ]");
				label_Client4.setIcon(ii2);
				label_Client4_Sub.setText("[ 닉네임 / 점수 ]");
			}else if(Integer.parseInt(playerIdx) == 2){
				label_Client4.setIcon(ii2);
				label_Client4_Sub.setText("[ 닉네임 / 점수 ]");
			}
		}
		
		void bgm(String play){ // BGM 재생 & 정지
			try{
				if(play.equals(CM_ENUM.BGM_PLAY)){
					new JFXPanel();
					File f = new File("bgm\\bgm.mp3");
					Media bgm = new Media(f.toURI().toURL().toString());
			        p = new MediaPlayer(bgm);
					p.play();
				}else if(play.equals(CM_ENUM.BGM_STOP)){
					p.stop();
					p.setMute(true);
					p.dispose();
				}
			}catch(Exception e){}
		}
	}

	// 내부 클래스 - 캔버스 핸들러
	class CanvasHandler extends JFrame implements ActionListener, MouseMotionListener
	{	
		int x1, x2, y1, y2;
		public void mouseDragged(MouseEvent e){
		    x1 = e.getX(); y1 = e.getY();
		    ((Brush)canvas).x1 = x1; ((Brush)canvas).y1 = y1;
		    canvas.repaint();
		}
		public void mousePressed(MouseEvent e){}
		public void mouseMoved(MouseEvent e){}
		
		public void actionPerformed(ActionEvent e){
			Object obj = e.getSource();
			Brush canvas2 = (Brush)canvas;
		   
			if(auth == true){ // 출제자 권한을 가진 상태여야만 캔버스 조작 가능
			    if(obj == btn_Color1){
				    canvas2.color = Color.RED;
			    }else if(obj == btn_Color2){
			    	canvas2.color = Color.GREEN;
			    }else if(obj == btn_Color3){
			    	canvas2.color = Color.BLUE;
			    }else if(obj == btn_Color4){
			    	canvas2.color = Color.YELLOW;
			    }else if(obj == btn_Color5){
			    	canvas2.color = Color.BLACK;
			    }else if(obj == btn_Erase){
			    	canvas2.color = canvas.getBackground();
			    }else if(obj == btn_EraseAll){
			    	Graphics g = canvas2.getGraphics();
				    g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); 
			    }
			}
		}
	}
	
	// 내부 클래스 - 캔버스 브러쉬 설정
	class Brush extends Canvas
	{
		int x1, x2;
		int y1, y2;
		Color color = Color.BLACK;

		void paintComponent(Graphics g){
			if(gameStart == true && auth == true){ // 게임이 시작되었고, 출제자 권한을 가진 상태여야 그리기 가능
				Graphics2D g2d = (Graphics2D)g;
	            g2d.setColor(color);
	            g2d.setStroke(new BasicStroke(6));
	            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2d.drawLine(x1, y1, x1, y1);
			}
		}
		
		public void update(Graphics g){
			paintComponent(g);
		}
	}
}