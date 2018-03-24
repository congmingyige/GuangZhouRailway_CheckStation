
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Railway_Server extends JFrame {
	ServerSocket serverSocket=null;
	Socket client=null;
	JTextArea text;
	JScrollPane jsp;
	JButton jb_info=new JButton("վ���ѯ���");
	JPanel jp_north=new JPanel();
	JPanel jp_center=new JPanel();
	private class Node {
		Node next=null;
		int d;
	}
	Node route[]=new Node[100];
	public Railway_Server() throws IOException {
		
		//MySQL
		try {
			//create database
			Railway_MySQL.regDriver();
			Railway_MySQL.conBuild();
			
			if (Railway_MySQL.ifexistDatabase("railway")==false) {
				System.out.println("create database 'railway'");
				Railway_MySQL.execUpdate("create DATABASE railway");
				//Railway_MySQL.execUpdate("create DATABASE if not exists Railway");
			}
			Railway_MySQL.closeDB();
			
			//create table
			Railway_MySQL.changeUrl();
			Railway_MySQL.regDriver();
			Railway_MySQL.conBuild();
			
			if (Railway_MySQL.ifexistTable("station")==false) {
				System.out.println("create table 'station'");
				Railway_MySQL.createTableRailway();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		int s,t;
		Node node;
		Scanner sc;
		sc=new Scanner(ClassLoader.getSystemClassLoader().getResourceAsStream("route.txt"));
		//sc=new Scanner("/route.txt");
		for (int i=0;i<100;i++) {
			route[i]=null;
		}
		while (sc.hasNext()) {
			s=sc.nextInt();
			t=sc.nextInt();
			node=new Node();
			node.next=route[s];
			node.d=t;
			route[s]=node;
			node=new Node();
			node.next=route[t];
			node.d=s;
			route[t]=node;
		}
		sc.close();
		
		text=new JTextArea(10,20);
		text.setLineWrap(true);
		jsp=new JScrollPane();
		jsp.setViewportView(text);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jp_north.add(jb_info);
		jp_center.add(jsp);
		setLayout(new BorderLayout());
		add(jp_north,BorderLayout.NORTH);
		add(jp_center,BorderLayout.CENTER);
		
		setBounds(550,100,360,360);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jb_info.addActionListener(new ActionListener() {
			@Override //check
			public void actionPerformed(ActionEvent e) {
//				ResultSet rs=Railway_MySQL.execQuery("select * from railway");
//				StringBuffer info=new StringBuffer();
//				try {
//					while (rs.next()) {
//						info.append(rs.getInt(1)).append(' ').append(rs.getInt(2));
//						JOptionPane.showMessageDialog(null,info,"վ�����",JOptionPane.PLAIN_MESSAGE);
//					}
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
			}
		});
		
		
		text.append(new Date().toString() + "\n");
		text.append("�ȴ�����...\n");
		text.setCaretPosition(text.getText().length());
		
		//ServerSocket serverSocket=new ServerSocket(0);	//ϵͳ�ṩһ��Ĭ�Ͽ��еĶ˿�
		//int port=serverSocket.getLocalPort();
//			String line=new String("");			
//			BufferedReader br=new BufferedReader(new InputStreamReader(
//				Runtime.getRuntime().exec("netstat -aon | findstr 4444").getInputStream()));
//			while ((line=br.readLine())!=null) {
//				System.out.println(line);
//				//get process id
//				Runtime.getRuntime().exec("taskkill /F /pid xxxx");
//			}
//			//�����޷�ʶ��netstat -aon | findstr 4444������ʶ��ipconfig��		//�̶��˿ں�Ҫ��֪�ͻ��ˣ����Զ˿ں�һ���ǲ����
		try {
			serverSocket=new ServerSocket(4444);
		} catch(Exception e) {
			//����н���ռ�øö˿ڣ���ServerSocket�޷�������ִ��IOException��
			//Runtime.getRuntime().exec("taskkill /F /pid xxxx");
			try {
				Runtime.getRuntime().exec("cmd /c start Port.bat");
				Thread.sleep(1000); //�㹻����ʱ����bat�ļ�ִ�����
				serverSocket=new ServerSocket(4444);
			} catch(Exception ee) {
				e.printStackTrace();
			}
		}
		//System.out.println(serverSocket.getLocalPort());
		
		while (true) {
			
			try {
				client=serverSocket.accept();
				System.out.println("new user");
				System.out.println("�ͻ���ַ��" + client.getInetAddress());
				text.append("�ͻ���ַ��" + client.getInetAddress() + "����\n");
				text.setCaretPosition(text.getText().length());
			} catch(IOException e) {
				System.out.println("���ڵȴ��û�");
			}
			if (client!=null) {
				new ServerThread(client).start();
			} else {
				continue;
			}
			
			System.out.println("xxxxx");
		}		
	}
	class ServerThread extends Thread {
		Socket socket;
		DataOutputStream out=null;
		DataInputStream in=null;
		int source,destination;
		int pre[]=new int[1000];
		ServerThread(Socket t) {
			socket=t;
			try {
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
			} catch(IOException e) {
				
			}
		}
		public void run() {
			while (true) {
				try {
					source=in.readInt();
					destination=in.readInt();
//					ResultSet rs=Railway_MySQL.execQuery("select * from railway where station='"+source+"'");
//					try {
//						//source
//						if (!rs.next())
//							Railway_MySQL.execUpdate("insert into station values("+source+")");
//						Railway_MySQL.execUpdate("update station set visit=visit+1" + "where station_id=" + source);
//						//destination
//						if (!rs.next())
//							Railway_MySQL.execUpdate("insert into station values("+destination+")");
//						Railway_MySQL.execUpdate("update station set visit=visit+1" + "where station_id=" + destination);						
//					} catch(SQLException e) {
//						
//					}

					System.out.println("source="+source+" destination="+destination);
					findPath();
					text.append(client.getInetAddress() + " send data: " + source + " " + destination + " " + "\n");
					text.setCaretPosition(text.getText().length());
					
					//���ݿ��¼վ�������������ж�վ�����������������վ�����������Ҫ�˹��Ų��Ƿ�������������
					
					//�����������,��һ�����ݱȽ��٣���ѯ�����Ƚ϶�ģ�����г�ʼ������
					System.out.println("source="+source+" destination="+destination);
					findPath();
					int d=source;
					while (d!=destination) {
						out.writeInt(d);
						d=pre[d];
					}
					out.writeInt(destination);
					out.writeInt(-1); //-1��Ϊ������־					
				} catch(IOException e) {
					System.out.println("�ͻ��뿪");
					break;
				}
			}
//			try {
//				in.close();
//				out.close();
//			} catch(IOException e) {
//				System.out.println("�ļ��޷��ر�");
//			}
		}
		public void findPath() {
			//���е�·�����ȶ�Ϊ1
			int head,tail,queue[]=new int[1000];
			Node node;
			pre[destination]=destination;
			queue[1]=destination;
			head=0;
			tail=1;
			for (int i=0;i<100;i++)
				pre[i]=-1;
			while (head<=tail) {
				head++;
				node=route[queue[head]];
				while (node!=null) {
					if (pre[node.d]==-1) {
						//System.out.println(node.d);
						tail++;
						queue[tail]=node.d;
						pre[node.d]=queue[head];
						if (node.d==source)
							return ;				
					}
					node=node.next;
				}
			}
		}
	}
	public static void main(String[] args) throws IOException {
		Railway_Server railway_Server=new Railway_Server();
	}
}
