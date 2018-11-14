package ksj.catchmind;

import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;

public class CM_Server extends CM_Server_GUI implements ActionListener
{
	ServerSocket ss;
	Socket s;
	int port = 7777;
	int readyPlayer; // 게임 준비된 클라이언트 카운트
	int score;
	boolean gameStart; // 게임 시작 상태
	String line;
	LinkedHashMap<String, DataOutputStream> clientList = new LinkedHashMap<String, DataOutputStream>(); // 클라이언트 이름, 스트림 관리
	LinkedHashMap<String, Integer> clientInfo = new LinkedHashMap<String, Integer>(); // 클라이언트 이름, 점수 관리
	
	public CM_Server(){
		btn_ServerStart.addActionListener(this);
		btn_ServerClose.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e){ // '서버 시작 & 종료' 버튼 이벤트
		if(e.getSource() == btn_ServerStart){
			new Thread(){
				public void run(){
					try{
						Collections.synchronizedMap(clientList);
						ss = new ServerSocket(port);
						label_ServerStatus.setText("[ Server Started ]");
						textArea.append("[ 서버가 시작되었습니다 ]" + "\n");
						btn_ServerStart.setEnabled(false);
						btn_ServerClose.setEnabled(true);
						while(true){
							s = ss.accept();
							if((clientList.size() + 1) > CM_ENUM.MAX_CLIENT || gameStart == true){ // 정원이 초과되었거나, 게임중이라면 소켓 연결 거부
								s.close();
							}else{
								Thread gm = new GameManager(s);
								gm.start();
							}
						}
					}catch(IOException io){
						System.exit(0);
					}
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
			}catch(IOException io){
				io.printStackTrace();
			}
		}
	}
	
	public void sendSystemMsg(String msg){ // 시스템 메시지 및 명령어 송신
		Iterator<String> it = clientList.keySet().iterator();
		while(it.hasNext()){
			try{
				DataOutputStream dos = clientList.get(it.next());
				dos.writeUTF(msg);
				dos.flush();
			}catch(IOException io){
				io.printStackTrace();
			}
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
			}catch(IOException io){
				io.printStackTrace();
			}
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
				clientMgmt(clientName, "입장");
				while(dis != null){
					String msg = dis.readUTF();
					filtering(msg); // 명령어 필터링
				}
			}catch(IOException io){
				clientList.remove(clientName); clientInfo.remove(clientName); // 클라이언트 퇴장시 제거
				closeAll();
				if(clientList.isEmpty() == true){ // 서버에 남은 클라이언트가 하나도 없다면, 서버 닫기
					try{
						ss.close();
						System.exit(0);
					}catch(IOException e){}
				}
				clientMgmt(clientName, "퇴장");
				readyPlayer = 0; // 새로운 클라이언트가 접속해도 게임 시작에 문제가 없도록 변수 초기화
				gameStart = false;
				sendSystemMsg(CM_ENUM.END); // 클라이언트 퇴장시, 즉시 라운드 종료
			}
		}
		
		void clientMgmt(String clientName, String inout){
			sendSystemMsg("[ " + clientName + "님이 " + inout + "하셨습니다. ]\n(현재 접속자 수 : " + clientList.size() + "명 / 4명)");
			textArea.append("[ 현재 접속자 명단 (총 " + clientList.size() + "명 접속중) ]\n");
			Iterator<String> it = clientList.keySet().iterator();
			while(it.hasNext()) textArea.append(it.next() + "\n");
			scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			setClientInfo(); // 클라이언트 목록 갱신
		}
		
		void setClientInfo(){
			String[] keys = new String[clientInfo.size()];
			int[] values = new int[clientInfo.size()];
			int index = 0;
			for(Map.Entry<String, Integer> mapEntry : clientInfo.entrySet()){
			    keys[index] = mapEntry.getKey();
			    values[index] = mapEntry.getValue();
			    index++;
			}
			for(int i=0; i<clientList.size(); i++){
				sendSystemMsg(CM_ENUM.UPD_CLIST + keys[i] + " " + values[i] + "#" + i); // 명령어 : 클라이언트 목록 갱신
			}
		}
		
		void closeAll(){
			try{
				if(dos != null) dos.close();
				if(dis != null) dis.close();
				if(s != null) s.close();
			}catch(IOException ie){}
		}
		
		void filtering(String msg) { // 명령어 필터링
			String temp = msg.substring(0, 7);
			if(temp.equals(CM_ENUM.CHAT)){ // 명령어 : 일반 채팅
				answerCheck(msg.substring(7).trim());
				sendSystemMsg(msg.substring(7));
			}else if(temp.equals(CM_ENUM.READY)){ // 명령어 : 클라이언트 준비 상태 체크
				 readyPlayer++;
				 if(readyPlayer >= 2 && readyPlayer == clientList.size()){ // 2명 이상 && 모든 클라이언트가 준비되었을 경우
					 for(int i=3; i>0; i--){
						 try{
						 	sendSystemMsg("[ 모든 참여자들이 준비되었습니다. ]\n[ " + i + "초 후 게임을 시작합니다 .. ]");
						 	Thread.sleep(1000);
						 }catch(InterruptedException ie){}
					 }
					 ArrayList<String> authList = new ArrayList<String>(); // 문제 출제자 랜덤 선택
					 Iterator<String> it = clientList.keySet().iterator();
					 while(it.hasNext()) authList.add(it.next());
					 Random rd = new Random();
					 sendSystemMsg(CM_ENUM.AUTH + authList.get(rd.nextInt(authList.size()))); // 명령어 : 문제 출제자 랜덤 선택
					 Exam ex = new Exam(); ex.start(); // 문제 출제
					 StopWatch tm = new StopWatch(); tm.start(); // 타이머 시작
					 gameStart = true;
					 sendSystemMsg(CM_ENUM.START); // 명령어 : 게임 시작
				 }
			}else if(temp.equals(CM_ENUM.MOUSE_XY)){ // 명령어 : 마우스 좌표 수신
				sendSystemMsg(msg);
			}else if(temp.equals(CM_ENUM.CHANGE_COLOR)){ // 명령어 : 컬러 설정
				sendSystemMsg(msg);
			}else if(temp.equals(CM_ENUM.ERASE)){ // 명령어 : 지우기
				sendSystemMsg(msg);
			}else if(temp.equals(CM_ENUM.ERASE_ALL)){ // 명령어 : 모두 지우기
				sendSystemMsg(msg);
			}else if(temp.equals(CM_ENUM.GG)){ // 명령어 : 게임 종료 (출제자가 게임을 포기했을 경우)
				sendSystemMsg("[ 출제자가 게임을 포기했습니다 !! ]");
				sendSystemMsg(msg);
				readyPlayer = 0;
				gameStart = false;
			}else if(temp.equals(CM_ENUM.END)){ // 명령어 : 게임 종료 (시간 초과나 이탈자 발생으로 게임이 종료되는 경우)
				sendSystemMsg("[ 게임이 종료되었습니다 !! ]");
				sendSystemMsg(msg);
				readyPlayer = 0;
				gameStart = false;
			}
		}
		
		void answerCheck(String msg){ // 정답 체크
			String tempNick = msg.substring(0, msg.indexOf(" ")); // 정답자 닉네임 체크
			String tempAns = msg.substring(msg.lastIndexOf(" ") + 1); // 정답 내용 체크
			if(tempAns.equals(line) && gameStart == true){ // 정답자 중복 방지를 위한 게임 시작 상태 체크
				sendSystemMsg(CM_ENUM.END);
				gameStart = false;
				readyPlayer = 0; // 새로운 게임을 시작하기 위한 변수 초기화
				sendSystemMsg("[ " + tempNick + "님 정답 !! ]");
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
				sendSystemMsg(CM_ENUM.EXAM + line);
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
					sendSystemMsg(CM_ENUM.TIMER + (toTime(time)));
					if(toTime(time).equals("00 : 00")){
						sendSystemMsg(CM_ENUM.END); // 시간 초과시, 게임 종료
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
		CM_Server cms = new CM_Server();
		cms.setVisible(true);
	}
}