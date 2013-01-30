package com.time;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class TimeCounter extends Activity {

	//��Ҫ�Ĳ���ע�ᣬ������
	private Button bn_start;
	private Button bn_stop;
	private Button bn_reset;
	private TextView tx_time;
	private TextView tx_info;
	//���ڸ���ʱ�����ŵ��̣߳�С����ע����ľ�У�
	private Thread timeThread;
	//���ڿ����̵߳Ĳ���ֵ��Ϊtrueʱ�߳��ܹ�����
	private boolean threadFlag=true;
	//����start��stop���Ƿ�ɵ������ֹ¼����ʱ���������������쳣
	private boolean startClickable=true;
	private boolean stopClickable=false;
	//����handler�����ڽ����߳���Ϣ���ҿ���UI�̸߳���UI
	private Handler handler;
	//�������ڵ�����
	private int count=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_counter);
        //��ʶ����������
        bn_start=(Button)findViewById(R.id.bn_start);
        bn_stop=(Button)findViewById(R.id.bn_stop);
        bn_reset=(Button)findViewById(R.id.bn_reset);
        tx_info=(TextView)findViewById(R.id.tx_info);
        tx_time=(TextView)findViewById(R.id.tx_time);
        //��ʼ��handler
        handler=new Handler()
        {
        	@Override
        	public void handleMessage(Message msg)
        	{
        		//�����յ�message.whatΪ��Ӧֵʱ����ȡ��Ӧ����
        		//��Ϊ0ʱΪʱ���������
        		if(msg.what==0)
        		{
        			count++;
        			int min=count/60;
        			int sec=count%60;        		
        			tx_time.setText(String.format("%1$02d:%2$02d",min,sec));
        		}
        		//��Ϊ1ʱ��Ϊ�����start����ʾ
        		if(msg.what==1)
        		{        			
        			tx_info.setText("¼����ʼ��");          			
        		}
        		//��Ϊ2ʱ��Ϊ�����stop����ʾ
        		if(msg.what==2)
        		{        			
        			tx_info.setText("¼��ֹͣ��");            			
        		}
        		//��Ϊ3ʱ��Ϊ�����reset����ʾ
        		if(msg.what==3)
        		{
        			tx_info.setText("�����ʼ¼��");
        			tx_time.setText("");
        		}
        	}
        	
        };
        //���ð�ť�İ��¼�����������
        bn_start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//��Ϊ����stopʱ��ֱ��ֹͣʱ�������̲߳�����Ϊnull�����Դ˴�Ϊ����Ϊnull������Ϊ�˱���ֱ���ֹͣ�̶߳������߳��쳣
				if(timeThread==null&&startClickable)
				{
					threadFlag=true;
					//��ʼ���߳����ݣ����о���ʵ��
					GetThread();
					//�����߳�
					timeThread.start();
					//��ʼ����Ϣ���󣬲��Ҹ�ֵΪ1����Ӧhandler�еĴ���
					Message message=new Message();
					message.what=1;
					//����send����Ч
					handler.sendMessage(message);
					//��start������Ϊ���ɵ����stop������Ϊ�ɵ��
					startClickable=false;
					stopClickable=true;
				}
			}
		});
        bn_stop.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//ֻ�е����start���ܵ��stop
				if(timeThread!=null&&stopClickable)
				{
					//�������̵߳�flag��Ϊfalse��ֹͣ�������е�ʱ���߳�
					threadFlag=false;
					//ֱ�ӽ�����߳���Ϊnull���˴�ʹ��stop����ֹͣ�̻߳ᱨ���˷����Ѿ����Ƽ�ʹ��
					timeThread=null;
					Message message=new Message();
					message.what=2;
					handler.sendMessage(message);
					//stop��������Ϊ���ɵ��
					stopClickable=false;
				}
			}
		});
        bn_reset.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//���������������ֻ��һ������������Ѿ�¼�ƹ��ˣ��Ҹպ���stop��
				if(stopClickable==false)
				{
					count=0;
					Message message=new Message();
					message.what=3;
					handler.sendMessage(message);
					//ֻ��reset�˲��ܹ��ٴ�start
					startClickable=true;
				}				
			}
		});
    }
    private void GetThread()
    {
    	//��ʼ��ʱ���̣߳���run��������д�߳����д��룬������һ��ѭ��������
    	   timeThread=new Thread(new Runnable() {
   			
   			public void run() {
   				// TODO Auto-generated method stub
   				while(threadFlag)
   				{
   					try {
   						//˯��һ�룬sleep����������try-catch��������쳣
   						Thread.sleep(1000);
   					} catch (InterruptedException e) {
   						// TODO Auto-generated catch block
   						e.printStackTrace();
   					}
   					//֪ͨhandler����ʱ��
   					Message message=new Message();
   					message.what=0;
   					handler.sendMessage(message);
   				}
   			}
   		});
    }
}
