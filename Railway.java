
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;
import java.text.*;

public class Railway extends JFrame implements Runnable {
//	ImageIcon imgicon_start=new ImageIcon(Toolkit.getDefaultToolkit().createImage("Start.png"));
//	ImageIcon imgicon_end=new ImageIcon(Toolkit.getDefaultToolkit().createImage("End.png"));
//  ImageIcon img_railway=new ImageIcon(Toolkit.getDefaultToolkit().createImage("Railway.jpg"));
	
//	Image img_start=Toolkit.getDefaultToolkit().createImage("\\Start.png");
//	Image img_end=Toolkit.getDefaultToolkit().createImage("\\End.png");
//	Image img_railway=Toolkit.getDefaultToolkit().createImage("\\Railway.jpg");
	Image img_start;
	Image img_end;
	Image img_railway;
	Image img_icon;
	
	ImageIcon imgicon_railway;
	ImageIcon imgicon_start;
	ImageIcon imgicon_end;
	JLabel jl_railway=new JLabel();
	JLabel jl_start=new JLabel();
	JLabel jl_end=new JLabel();

	private int width_std=1000;
	private int height_std=1000;
	private int x_std=0;
	private int y_std=20;
	private int x=x_std;
	private int y=y_std;
	private int dx=50;
	private int dy=50;
	private int width=width_std;
	private int height=height_std;
	
	private double perx_start;
	private double pery_start;
	private double x_start;
	private double y_start;
	
	private double perx_end;
	private double pery_end;
	private double x_end;
	private double y_end;
	
	private double width_std_op=20.0;
	private double height_std_op=12.5;
	private double dx_op=1.0;
	private double dy_op=0.625;
	private double width_op=width_std_op;
	private double height_op=height_std_op;
	
	private java.awt.Point point=null;
	private java.awt.Point point_pre=null;
	private double r=0.01;
	private class st {
		double x,y;
		String name;
	};
	st station[]=new st[100];
	private int count_s=0;
	private int num_start=-1;
	private int num_end=-1;
	
	private int x_offset=9;
	private int y_offset=36;
	
	JLayeredPane layeredPane;
	JTextField tf_startS=new JTextField("���");
	JTextField tf_endS=new JTextField("�յ�");
	JTextField tf_startStation=new JTextField("");
	JTextField tf_endStation=new JTextField("");
	
	JTextArea ta_route=new JTextArea(5,5);
	JButton jb_connect=new JButton("����������ʧ��");
	
	Socket socket=new Socket();
	DataInputStream in=null;
	DataOutputStream out=null;
	ThreadTime thread_time=new ThreadTime();
	Thread thread=new Thread(this);
	
	static String history=new String(); //check from database
	static int count=0;
	
//	Box boxV1;
//	Box boxV2;
//	Box baseBox;
//	JPanel jp_route=new JPanel();
//	int anchor=GridBagConstraints.CENTER;
//	int fill=GridBagConstraints.BOTH;
//	Insets insets=new Insets(0,0,0,0);
	
	public Railway() {
		
		//JOptionPane.showMessageDialog(null,url,"��������ʧ��",JOptionPane.PLAIN_MESSAGE);
		try {
			URL url=Railway.class.getResource("/Start.png");
			InputStream input=url.openStream();
			img_start=javax.imageio.ImageIO.read(input);
			input.close();
		} catch(IOException e) {}
		
		try {
			URL url=Railway.class.getResource("/End.png");
			InputStream input=url.openStream();
			img_end=javax.imageio.ImageIO.read(input);
			input.close();
		} catch(IOException e) {}
		
		try {
			URL url=Railway.class.getResource("/Railway.jpg");
			InputStream input=url.openStream();
			img_railway=javax.imageio.ImageIO.read(input);
			input.close();
		} catch(IOException e) {}
		
		//����station����
		Scanner sc;
		sc=new Scanner(ClassLoader.getSystemClassLoader().getResourceAsStream("station.txt"));
//			JOptionPane.showMessageDialog(null,path_jar+"/station.txt","��������ʧ��",JOptionPane.PLAIN_MESSAGE);
//			sc=new Scanner(new File("/station.txt"));
		while (sc.hasNext()) {
			station[count_s]=new st();
			station[count_s].x=sc.nextDouble();
			station[count_s].y=sc.nextDouble();
			station[count_s].name=sc.next();//Line
			count_s++;
			int st_num=sc.nextInt();
		}
		sc.close();
		System.out.println("count_s="+count_s);
		
		//���Ͻǲ�ѯ��
		//�ѳ�����Box��GridBagLayout������
//		boxV1=Box.createVerticalBox();
//		boxV1.add(jl_startS);
//		boxV1.add(jl_endS);
//		boxV2=Box.createVerticalBox();
//		boxV2.add(jl_startStation);
//		boxV2.add(jl_endStation);
//		baseBox=Box.createHorizontalBox();
//		baseBox.add(boxV1);
//		baseBox.add(boxV2);
//		baseBox.add(ta_route);
//		baseBox.setBounds(0,0,300,300);

//		jp_route.setLayout(new GridBagLayout());
//		jp_route.add(jl_startS,new GridBagConstraints(0,0,1,1,30,20,anchor,fill,insets,0,0));
//		jp_route.add(jl_startStation,new GridBagConstraints(1,0,1,1,70,20,anchor,fill,insets,0,0));
//		jp_route.add(jl_endS,new GridBagConstraints(0,1,1,1,30,20,anchor,fill,insets,0,0));
//		jp_route.add(jl_endStation,new GridBagConstraints(1,1,1,1,70,20,anchor,fill,insets,0,0));
//		jp_route.add(ta_route,new GridBagConstraints(0,2,1,1,100,60,anchor,fill,insets,0,0));
//		jp_route.setVisible(true);
//		jp_route.setBounds(0,0,200,200);
		
		//��ʼ����ֹ������ͼ��(Խ��ǰ�棬���õ���ֵԽ��)
		jl_start.setVisible(false);
		jl_end.setVisible(false);
		layeredPane=getLayeredPane();		
		layeredPane.add(jl_railway,new Integer(0));
//		layeredPane.add(jp_route,new Integer(2));
//		layeredPane.add(baseBox,new Integer(2));
		layeredPane.add(jl_start,new Integer(3));
		layeredPane.add(jl_end,new Integer(3));
		
		//��ʼ�����ֹ��
		JTextField_add(tf_startS,layeredPane,0,150,50,50);
		JTextField_add(tf_startStation,layeredPane,60,150,100,50);
		JTextField_add(tf_endS,layeredPane,0,210,50,50);
		JTextField_add(tf_endStation,layeredPane,60,210,100,50);
		//JTextField_add(tf_connect,layeredPane,0,270,160,50);

		jb_connect.setHorizontalAlignment(JLabel.CENTER);
		jb_connect.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		jb_connect.setFont(new Font("����",Font.PLAIN,20));
		jb_connect.setBounds(0,270,160,50);
		layeredPane.add(jb_connect,new Integer(1));
		jb_connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connection();
			}
		});
		
		//�ı�����ʾ·��
		ta_route.setBackground(Color.LIGHT_GRAY);
		ta_route.setEditable(false);
		ta_route.setVisible(false);
		ta_route.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		ta_route.setFont(new Font("����",Font.PLAIN,20));
		ta_route.setLineWrap(true);
		ta_route.setWrapStyleWord(true);
		layeredPane.add(ta_route,new Integer(3));
		//�����ı��򣬸���·��
		ta_route.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
		        Clipboard clip=Toolkit.getDefaultToolkit().getSystemClipboard();
		        Transferable tText=new StringSelection(ta_route.getText());
		        clip.setContents(tText,null);
			}
		});
		
		//���ô��ڴ�С������
		setLayout(null);
		setBounds(0,0,1000,1000);
		setVisible(true);
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //һ�����ڵĹرղ��ᵼ���������ڵĹر�
  								 //JFrame.EXIT_ON_CLOSE
		
		Railway.count++;		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				Railway.count--;
				System.out.println("count="+Railway.count);
				if (Railway.count==0) //���ڱ���JFrame.DISPOSE_ON_CLOSE���رս��̵�����
					System.exit(0);
			}
		});
		
		//����ͼ��
		try {
			URL url=Railway.class.getResource("/icon_railway.png");
			InputStream input=url.openStream();
			img_icon=javax.imageio.ImageIO.read(input);
			input.close();
		} catch(IOException e) {}
		setIconImage(img_icon);
		//setIconImage(Toolkit.getDefaultToolkit().createImage("icon_railway.png"));
		
		//�˵�
		JMenuBar mb=new JMenuBar();
		JMenu MenuBasic=new JMenu("������Ϣ");
		JMenuItem MenuVersion=new JMenuItem("�汾");
		JMenuItem MenuReset=new JMenuItem("����");
		JMenuItem MenuNew=new JMenuItem("�´���");
		JMenuItem MenuHistory=new JMenuItem("��ʷ��¼");
		JMenuItem MenuExit=new JMenuItem("�˳�");
		
		MenuBasic.add(MenuVersion);
		MenuBasic.add(MenuReset);
		MenuBasic.add(MenuNew);
		MenuBasic.add(MenuHistory);
		MenuBasic.add(MenuExit);
		mb.add(MenuBasic);
		setJMenuBar(mb);
//		layeredPane.add(mb,new Integer(4));	//������Ȼ���У�����ֻ��JFrame�������JMenuBar

		MenuVersion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,"v1.0.0","version",JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		//����ʽ�˵�
		JPopupMenu popupMenu=new JPopupMenu();
		JMenuItem MenuReset1=new JMenuItem("����");
		JMenuItem MenuNew1=new JMenuItem("�´���");
		JMenuItem MenuHistory1=new JMenuItem("��ʷ��¼");
		JMenuItem MenuExit1=new JMenuItem("�˳�");
		popupMenu.add(MenuReset1);
		popupMenu.add(MenuNew1);		
		popupMenu.add(MenuExit1);
		popupMenu.add(MenuHistory1);

		ActionListener ListenerReset=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		};
		MenuReset.addActionListener(ListenerReset);
		MenuReset1.addActionListener(ListenerReset);

		ActionListener ListenerNew=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Railway railway=new Railway();
			}
		};
		MenuNew.addActionListener(ListenerNew);
		MenuNew1.addActionListener(ListenerNew);
		
		ActionListener ListenerHistory=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//һ���µĴ�����ʷ  ���Ե�� ����һ���µĴ���
				JOptionPane.showMessageDialog(null,history,"��ʷ��¼",JOptionPane.PLAIN_MESSAGE);
			}
		};
		MenuHistory.addActionListener(ListenerHistory);
		MenuHistory1.addActionListener(ListenerHistory);		
		
		ActionListener ListenerExit=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		MenuExit.addActionListener(ListenerExit);
		MenuExit1.addActionListener(ListenerExit);
		
		//ͼƬ�������
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				System.out.println("1-1");
				//����ֻ�����ͼƬ�Ŵ�
				if (e.getWheelRotation()<0)
					zoom();
				//����ֻ�����ͼƬ��С
				else
					reduce();
			}
		});
		
		//Pressed -> Released -> Clicked
		this.addMouseListener(new MouseAdapter() {
			//��ԭͼƬλ�úʹ�С
			public void mouseClicked(MouseEvent e) {
				
				//������
				//if (e.getButton()==MouseEvent.BUTTON1)
				//�Ҽ����
				//if (e.getButton()==MouseEvent.BUTTON3)
				
				System.out.println("2-1");
				System.out.println(e.getX()+" "+e.getY());
				
				//�����ͼƬ��Χ��
				if (x<=e.getX() && e.getX()<=x+width && y<=e.getY() && e.getY()<=y+height) {
					double pos_x=(double)(e.getX()-x-x_offset)/width;
					double pos_y=(double)(e.getY()-y-y_offset)/height;
					//���ڻ��վ���λ��
					System.out.printf("percentage = %.4f %.4f\n",pos_x,pos_y);
					
					int i;
					for (i=0;i<count_s;i++) {
						if (Math.pow(pos_x-station[i].x,2) + Math.pow(pos_y-station[i].y,2) < Math.pow(r,2)) {
							
							if (num_start!=-1 && num_end!=-1) {
								//ȡ����ʼ�����ֹ��
								num_start=-1;
								jl_start.setVisible(false);
								tf_startStation.setText("");
								num_end=-1;
								jl_end.setVisible(false);
								tf_endStation.setText("");
								ta_route.setVisible(false);
								
								//��������ʼ��
								num_start=i;
								perx_start=station[i].x;
								pery_start=station[i].y;
								x_start=x+perx_start*width-width_op*0.5;
								y_start=y+pery_start*height-height_op;
								//System.out.println("x_start y_start = "+x_start+" "+y_start);
								
								jl_start.setBounds((int)Math.round(x_start),(int)Math.round(y_start),(int)Math.round(width_op),(int)Math.round(height_op));
								imgicon_start=new ImageIcon(img_start.getScaledInstance((int)Math.round(width_op),(int)Math.round(height_op),Image.SCALE_DEFAULT));
								jl_start.setIcon(imgicon_start);
								jl_start.setVisible(true);
								tf_startStation.setText(station[num_start].name);								
							}
							//������ʼ��
							else if (num_start==-1) {
								num_start=i;
								perx_start=station[i].x;
								pery_start=station[i].y;
								x_start=x+perx_start*width-width_op*0.5;
								y_start=y+pery_start*height-height_op;
								//System.out.println("x_start y_start = "+x_start+" "+y_start);
								
								jl_start.setBounds((int)Math.round(x_start),(int)Math.round(y_start),(int)Math.round(width_op),(int)Math.round(height_op));
								imgicon_start=new ImageIcon(img_start.getScaledInstance((int)Math.round(width_op),(int)Math.round(height_op),Image.SCALE_DEFAULT));
								jl_start.setIcon(imgicon_start);
								jl_start.setVisible(true);
								tf_startStation.setText(station[num_start].name);
							}
							//ȡ����ʼ��
							else if (i==num_start) {
								num_start=-1;
								jl_start.setVisible(false);
								tf_startStation.setText("");
								ta_route.setVisible(false);
							}
							//ȡ����ֹ��
							else if (i==num_end) {
								num_end=-1;
								jl_end.setVisible(false);
								tf_endStation.setText("");
								ta_route.setVisible(false);
							}
							//������ֹ��
							else {
								num_end=i;
								perx_end=station[i].x;
								pery_end=station[i].y;
								x_end=x+perx_end*width-width_op*0.5;
								y_end=y+pery_end*height-height_op;
								
								jl_end.setBounds((int)Math.round(x_end),(int)Math.round(y_end),(int)Math.round(width_op),(int)Math.round(height_op));
								imgicon_end=new ImageIcon(img_end.getScaledInstance((int)Math.round(width_op),(int)Math.round(height_op),Image.SCALE_DEFAULT));
								jl_end.setIcon(imgicon_end);
								jl_end.setVisible(true);
								tf_endStation.setText(station[num_end].name);
								
								if (socket.isConnected()) {
									try {
										//System.out.println(num_start+" "+num_end);
										out.writeInt(num_start);
										out.writeInt(num_end);
										jb_connect.setVisible(false);
										break;	//�����ͳɹ����˳�
									} catch (IOException e1) {
										jb_connect.setVisible(true);
									}									
								}
								else
									jb_connect.setVisible(true);
							}
							//�ҵ�������ѭ��
							break;
						}						
					}
					//�����մ���ȡ����ʼ��
					if (i==count_s) {
						num_start=-1;
						jl_start.setVisible(false);
						tf_startStation.setText("");
						num_end=-1;
						jl_end.setVisible(false);
						tf_endStation.setText("");
						ta_route.setVisible(false);
					}
					
					if (e.getClickCount()==2) {
						reset();
					}
				}
				
			}
			//��갴��
			public void mousePressed(MouseEvent e) {
				System.out.println("2-2");
				checkForTriggerEvent(e);
			}
			//����ɿ�
			public void mouseReleased(MouseEvent e) {
				System.out.println("2-3");
				point=null;
				checkForTriggerEvent(e);
			}
			//������Ҽ�
			public void checkForTriggerEvent(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(),e.getX(),e.getY());
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionListener() {
			//����ƶ�
			public void mouseMoved(MouseEvent e) {
				//System.out.println("3-1");
			}
			//����϶�
			public void mouseDragged(MouseEvent e) {
				System.out.println("3-2");
				//�����ͼƬ��Χ��
				if (x<=e.getX() && e.getX()<=x+width && y<=e.getY() && e.getY()<=y+height) {
					point_pre=point;
					point=e.getPoint();
					if (point_pre!=null) {
						int dif_x=point.x-point_pre.x;
						int dif_y=point.y-point_pre.y;
						if (dif_x!=0 || dif_y!=0) {
							x+=dif_x;
							y+=dif_y;
							x_start+=dif_x;
							y_start+=dif_y;
							x_end+=dif_x;
							y_end+=dif_y;
						}
						repaint();
					}					
				}
			}
		});
		
		connection();

	}
	
	public void JTextField_add(JTextField tf,JLayeredPane lp,int x,int y,int w,int h) {
		tf.setEditable(false);
		tf.setHorizontalAlignment(JLabel.CENTER);
		tf.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		tf.setFont(new Font("����",Font.PLAIN,20));
		tf.setBounds(x,y,w,h);
		lp.add(tf,new Integer(1));
	}
	
	//λ���ƶ�����С���ŵ��µ�ͼƬ�ĸı�
	public void paint(Graphics g) {
		super.paint(g);
		
		jl_start.setBounds((int)Math.round(x_start),(int)Math.round(y_start),(int)Math.round(width_op),(int)Math.round(height_op));
		imgicon_start=new ImageIcon(img_start.getScaledInstance((int)Math.round(width_op),(int)Math.round(height_op),Image.SCALE_DEFAULT));
		jl_start.setIcon(imgicon_start);
		
		jl_end.setBounds((int)Math.round(x_end),(int)Math.round(y_end),(int)Math.round(width_op),(int)Math.round(height_op));
		imgicon_end=new ImageIcon(img_end.getScaledInstance((int)Math.round(width_op),(int)Math.round(height_op),Image.SCALE_DEFAULT));
		jl_end.setIcon(imgicon_end);
		
		jl_railway.setBounds(x,y,width,height);
		imgicon_railway=new ImageIcon(img_railway.getScaledInstance(width,height,Image.SCALE_DEFAULT));
		jl_railway.setIcon(imgicon_railway);
		
	}
	
	//��С
	public void reduce() {
		if (width>2*dx && height>2*dy) {
			width-=2*dx;
			height-=2*dy;			
			x+=dx;
			y+=dy;

			width_op-=2*dx_op;
			height_op-=2*dy_op;
			x_start=x+perx_start*width-width_op*0.5;
			y_start=y+pery_start*height-height_op;
			x_end=x+perx_end*width-width_op*0.5;
			y_end=y+pery_end*height-height_op;
			repaint();
		}
	}
	//�Ŵ�
	public void zoom() {
		width+=2*dx;
		height+=2*dy;
		x-=dx;
		y-=dy;
		
		width_op+=2*dx_op;
		height_op+=2*dy_op;
		x_start=x+perx_start*width-width_op*0.5;
		y_start=y+pery_start*height-height_op;
		x_end=x+perx_end*width-width_op*0.5;
		y_end=y+pery_end*height-height_op;
		repaint();
	}
	public void reset() {
//		width=getWidth();
//		height=getHeight();						
		width=width_std;
		height=height_std;
		width_op=width_std_op;
		height_op=height_std_op;
		x=x_std;
		y=y_std;
		x_start=x+perx_start*width-width_op*0.5;
		y_start=y+pery_start*height-height_op;
		x_end=x+perx_end*width-width_op*0.5;
		y_end=y+pery_end*height-height_op;
		repaint();
	}
	
	//�ڴ��ڱ��⴦��ʾʱ��
	class ThreadTime implements Runnable {
		Thread thread=new Thread(this);
		public ThreadTime() {
			thread.start();
		}
		public void run() {
			SimpleDateFormat format=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
			Calendar cal;
			while (true) {
				try {
					cal=Calendar.getInstance();
					setTitle(format.format(cal.getTime()).toString());
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}
		}
	}
	
	//����ͷ����������׽�������
	void connection() {
		try {
			if (socket.isConnected()) {
				
			}
			else
			{
				InetAddress address=InetAddress.getByName("127.0.0.1");
				InetSocketAddress sockAddress=new InetSocketAddress(address,4444);
				socket.connect(sockAddress);
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
				ta_route.setText("");
				jb_connect.setVisible(false);
				thread.start();		//�����߳�
			}
		}
		catch(IOException ee) {
			
		}
	}
	
	//��ͣ��÷���˴��͹���������
	public void run() {
		//�ַ��������䶯����StringBuffer�ȽϺ�
		StringBuffer data=new StringBuffer();
		int num;
		while (true) {
			if (socket.isConnected()) {
				try {
					//�����֣�ֱ������-1Ϊֹ (DataInputStream������readLine())
					num=in.readInt();
					data=new StringBuffer();
					data.append(station[num].name);
					while ((num=in.readInt())!=-1) {
						System.out.println("num="+num);
						data.append("->");
						data.append(station[num].name);
					}
					//System.out.println("route="+data);
					
					ta_route.setLocation(0,270);
					ta_route.setText("");
					ta_route.setText(data.toString());
					ta_route.setSize(160,(ta_route.getText().length()+4)/5*20);
					ta_route.setCaretPosition(ta_route.getText().length());
					ta_route.setVisible(true);
					jb_connect.setVisible(false);
					history=history+data+"\n";
				} catch (IOException e) {
					//e.printStackTrace();
					jb_connect.setVisible(true);
				}				
			}
			else
			{
//				connection();
				break;
			}
		}
	}
	
	public static void main(String[] args) {
		Railway railway=new Railway();
	}
}
