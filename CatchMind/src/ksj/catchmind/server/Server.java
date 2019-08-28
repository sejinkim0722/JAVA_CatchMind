package ksj.catchmind.server;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

import ksj.catchmind.Protocol;

public class Server extends ServerGUI implements ActionListener {
	
	ServerSocket ss;
	Socket s;
	static int port = 7777;
	static int readyPlayer; // 게임 준비된 클라이언트의 수
	final static int GAMESTART_COUNT = 3; // 게임 시작 카운트
	String line; // 메시지 
	int score; // 게임 점수
	boolean isGameStarted; // 게임 시작 상태

	LinkedHashMap<String, DataOutputStream> clientList = new LinkedHashMap<String, DataOutputStream>(); // 클라이언트 닉네임(key) - 스트림(value)
	LinkedHashMap<String, Integer> clientInfo = new LinkedHashMap<String, Integer>(); // 클라이언트 닉네임(key) - 점수(value)
	
	public Server() {
		btn_ServerStart.addActionListener(this);
		btn_ServerClose.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) { // 서버 시작 및 종료 이벤트
		if(e.getSource() == btn_ServerStart) {
			new Thread() {
				public void run() {
					try {
						Collections.synchronizedMap(clientList);
						ss = new ServerSocket(port);
						label_ServerStatus.setText("[ Server Started ]");
						textArea.append("[ 서버가 시작되었습니다 ]" + "\n");
						btn_ServerStart.setEnabled(false);
						btn_ServerClose.setEnabled(true);
						
						while(true) {
							s = ss.accept();
							if((clientList.size() + 1) > Protocol.MAX_CLIENT || isGameStarted == true) { // 정원이 초과되었거나, 게임중이라면 소켓 연결 거부
								s.close();
							} else {
								Thread gm = new GameManager(s);
								gm.start();
							}
						}
					} catch(IOException io) {
						System.exit(0);
					}
				}
			}.start();
		} else if(e.getSource() == btn_ServerClose) {
			int select = JOptionPane.showConfirmDialog(null, "서버를 정말 종료하시겠습니까?", "JAVA CatchMind Server", JOptionPane.OK_CANCEL_OPTION);
			try {
				if(select == JOptionPane.YES_OPTION) {
					ss.close();
					label_ServerStatus.setText("[ Server Closed ]");
					textArea.append("[ 서버가 종료되었습니다 ]" + "\n");
					btn_ServerStart.setEnabled(true);
					btn_ServerClose.setEnabled(false);
				}
			} catch(IOException io) {
				io.printStackTrace();
			}
		}
	}
	
	public void sendSystemMsg(String msg) { // 시스템 메시지 및 프로토콜 송신
		Iterator<String> it = clientList.keySet().iterator();
		while(it.hasNext()) {
			try {
				DataOutputStream dos = clientList.get(it.next());
				dos.writeUTF(msg);
				dos.flush();
			} catch(IOException io) {
				io.printStackTrace();
			}
		}
	}

	// 게임 관리 및 통제
	public class GameManager extends Thread {
		
		Socket s;
		DataInputStream dis;
		DataOutputStream dos;
							
		public GameManager(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(this.s.getInputStream());
				dos = new DataOutputStream(this.s.getOutputStream());
			} catch(IOException io) {
				io.printStackTrace();
			}
		}
		
		public void run() {
			String clientName = "";
			try {
				clientName = dis.readUTF();
				if(!clientList.containsKey(clientName)) { // 중복 닉네임 방지
					clientList.put(clientName, dos);
					clientInfo.put(clientName, score);
				} else if(clientList.containsKey(clientName)) {
					s.close(); // 닉네임 중복시, 소켓 연결 거부
				}
				clientMgmt(clientName, "입장");
				
				while(dis != null) {
					String msg = dis.readUTF();
					filtering(msg); // 프로토콜 필터링
				}
			} catch(IOException io) {
				clientList.remove(clientName); // 중복 닉네임 방지
				closeAll();
				if(clientList.isEmpty() == true) { // 닉네임 중복시, 소켓 연결 거부
					try {
						ss.close();
						System.exit(0);
					} catch(IOException io2) {
						io2.printStackTrace();
					}
				}
				clientMgmt(clientName, "입장");
				readyPlayer = 0; // 새로운 클라이언트가 접속해도 게임 시작에 문제가 없도록 변수 초기화
				isGameStarted = false;
				sendSystemMsg(Protocol.END); // 클라이언트 퇴장시, 즉시 라운드 종료
			}
		}
		
		void clientMgmt(String clientName, String inout) {
			sendSystemMsg("[ " + clientName + "님이 " + inout + "하셨습니다. ]\n(현재 접속자 수 : " + clientList.size() + "명 / 4명)");
			textArea.append("[ 현재 접속자 명단 (총 " + clientList.size() + "명 접속중) ]\n");
			Iterator<String> it = clientList.keySet().iterator();
			
			while(it.hasNext()) textArea.append(it.next() + "\n");
			
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			setClientInfo(); // 클라이언트 목록 갱신
		}
		
		void setClientInfo() {
			String[] keys = new String[clientInfo.size()];
			int[] values = new int[clientInfo.size()];
			int index = 0;
			
			for(Map.Entry<String, Integer> mapEntry : clientInfo.entrySet()) {
			    keys[index] = mapEntry.getKey();
			    values[index] = mapEntry.getValue();
			    index++;
			}
			for(int i=0; i<clientList.size(); i++) sendSystemMsg(Protocol.UPD_CLIST + keys[i] + " " + values[i] + "#" + i); // 프로토콜 : 클라이언트 목록 갱신
		}
		
		void closeAll() {
			try {
				if(dos != null) dos.close();
				if(dis != null) dis.close();
				if(s != null) s.close();
			} catch(IOException io) {
				io.printStackTrace();
			}
		}
		
		void filtering(String msg) { // 프로토콜 필터링
			String temp = msg.substring(0, 7);
			
			if(temp.equals(Protocol.CHAT)) { // 프로토콜 : 일반 채팅
				answerCheck(msg.substring(7).trim());
				sendSystemMsg(msg.substring(7));
			} else if(temp.equals(Protocol.READY)) { // 프로토콜 : 클라이언트 준비 상태 체크
				readyPlayer++;
				if(readyPlayer >= 2 && readyPlayer == clientList.size()) { // 2명 이상의 클라이언트가 준비되었을 경우 게임 시작
					for(int count=GAMESTART_COUNT; count>0; count--) {
						try {
							sendSystemMsg("[ 모든 참여자들이 준비되었습니다. ]\n[ " + count + " 초 후 게임을 시작합니다.. ]");
							TimeUnit.MILLISECONDS.sleep(1000);
						} catch(InterruptedException ie) {
							ie.printStackTrace();
						}
					}
					 
					// 문제 출제자는 랜덤으로 선택됨
					ArrayList<String> authList = new ArrayList<String>(); 
					Iterator<String> it = clientList.keySet().iterator();
					while(it.hasNext()) authList.add(it.next());
					Random rd = new Random();
					sendSystemMsg(Protocol.AUTH + authList.get(rd.nextInt(authList.size()))); // 프로토콜 : 출제자 권한 부여
					 
					Exam exam = new Exam(); 
					exam.start(); // 문제 출제
					 
					StopWatch timer = new StopWatch(); timer.start(); // 타이머 시작
					 
					isGameStarted = true;
					sendSystemMsg(Protocol.START); // 프로토콜 : 게임 시작
				}
			} else if(temp.equals(Protocol.MOUSE_XY)) { // 프로토콜 : 마우스 좌표 수신
				sendSystemMsg(msg);
			} else if(temp.equals(Protocol.CHANGE_COLOR)) { // 프로토콜 : 컬러 변경
				sendSystemMsg(msg);
			} else if(temp.equals(Protocol.ERASE)) { // 프로토콜 : 지우기
				sendSystemMsg(msg);
			} else if(temp.equals(Protocol.ERASE_ALL)) { // 프로토콜 : 모두 지우기
				sendSystemMsg(msg);
			} else if(temp.equals(Protocol.GG)) { // 프로토콜 : 게임 종료 (출제자가 게임을 포기했을 경우)
				sendSystemMsg("[ 출제자가 게임을 포기하였습니다 !! ]");
				sendSystemMsg(msg);
				readyPlayer = 0;
				isGameStarted = false;
			} else if(temp.equals(Protocol.END)) { // 프로토콜 : 게임 종료 (시간 초과 또는 중도 이탈자 발생으로 인한 경우)
				sendSystemMsg("[ 게임이 종료되었습니다 !! ]");
				sendSystemMsg(msg);
				readyPlayer = 0;
				isGameStarted = false;
			}
		}
		
		void answerCheck(String msg) { // 정답 체크
			String tempNick = msg.substring(0, msg.indexOf(" ")); // 정답자 닉네임 체크
			String tempAns = msg.substring(msg.lastIndexOf(" ") + 1); // 정답 내용 체크
			
			if(tempAns.equals(line) && isGameStarted == true) { // 정답자가 중복 발생하는 경우를 방지하기 위해 게임 시작 상태를 체크
				sendSystemMsg(Protocol.END);
				isGameStarted = false;
				readyPlayer = 0; // 새로운 게임을 시작하기 위한 변수 초기화
				sendSystemMsg("[ " + tempNick + "님께서 정답을 맞히셨습니다 !! ]");
				clientInfo.put(tempNick, clientInfo.get(tempNick) + 1); // 정답자에게 점수 추가
				setClientInfo(); // 점수 표기 갱신
			}
		}
		
	}
	
	// 문제 출제
	class Exam extends Thread {
		
		public void run() {
			try {
				BufferedReader br = new BufferedReader(new FileReader("wordlist.txt"));
				ArrayList<String> words = new ArrayList<String>();
				
				while(br.readLine() != null) words.add(br.readLine());
				Random rd = new Random();
				line = words.get(rd.nextInt(words.size()));
				
				sendSystemMsg(Protocol.EXAM + line);
			} catch(IOException io) {
				io.printStackTrace();
			}
		}
		
	}
	
	// 타이머
	class StopWatch extends Thread {
		
		long preTime = System.currentTimeMillis();
		
		public void run() {
			try {
				while(isGameStarted == true) {
					sleep(10);
					long time = System.currentTimeMillis() - preTime;
					sendSystemMsg(Protocol.TIMER + (toTime(time)));
					if(toTime(time).equals("00 : 00")){
						sendSystemMsg(Protocol.END); // 시간 초과시 게임 종료 시스템 메시지 전송
						readyPlayer = 0;
						isGameStarted = false;
						break;
					} else if(readyPlayer == 0) {
						break;
					}
				}
			} catch (InterruptedException it) {
				it.printStackTrace();
			}
		}
		
		String toTime(long time) {
			int min = (int)(3-(time / 1000.0 / 60.0));
			int sec = (int)(60-(time % (1000.0 * 60) / 1000.0));
			return String.format("%02d : %02d", min, sec);
		}
		
	}
	
	public static void main(String[] args) {
		Server sv = new Server();
		sv.setVisible(true);
	}
}