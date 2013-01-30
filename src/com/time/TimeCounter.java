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

	//需要的部件注册，不详述
	private Button bn_start;
	private Button bn_stop;
	private Button bn_reset;
	private TextView tx_time;
	private TextView tx_info;
	//用于感受时间流逝的线程（小文艺注释有木有）
	private Thread timeThread;
	//用于控制线程的布尔值，为true时线程能够运行
	private boolean threadFlag=true;
	//设置start和stop键是否可点击，防止录音的时候出现误操作引发异常
	private boolean startClickable=true;
	private boolean stopClickable=false;
	//定义handler，用于接受线程消息并且控制UI线程更新UI
	private Handler handler;
	//计数现在的秒数
	private int count=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_counter);
        //常识，不解释了
        bn_start=(Button)findViewById(R.id.bn_start);
        bn_stop=(Button)findViewById(R.id.bn_stop);
        bn_reset=(Button)findViewById(R.id.bn_reset);
        tx_info=(TextView)findViewById(R.id.tx_info);
        tx_time=(TextView)findViewById(R.id.tx_time);
        //初始化handler
        handler=new Handler()
        {
        	@Override
        	public void handleMessage(Message msg)
        	{
        		//当接收的message.what为对应值时，采取相应处理
        		//当为0时为时间秒表跳动
        		if(msg.what==0)
        		{
        			count++;
        			int min=count/60;
        			int sec=count%60;        		
        			tx_time.setText(String.format("%1$02d:%2$02d",min,sec));
        		}
        		//当为1时，为点击了start的提示
        		if(msg.what==1)
        		{        			
        			tx_info.setText("录音开始：");          			
        		}
        		//当为2时，为点击了stop的提示
        		if(msg.what==2)
        		{        			
        			tx_info.setText("录音停止：");            			
        		}
        		//当为3时，为点击了reset的提示
        		if(msg.what==3)
        		{
        			tx_info.setText("点击开始录音");
        			tx_time.setText("");
        		}
        	}
        	
        };
        //设置按钮的绑定事件，不解释了
        bn_start.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//因为后面stop时候直接停止时间流逝线程并且置为null，所以此处为当其为null，这是为了避免粗暴的停止线程而出现线程异常
				if(timeThread==null&&startClickable)
				{
					threadFlag=true;
					//初始化线程内容，后有具体实现
					GetThread();
					//启动线程
					timeThread.start();
					//初始化消息对象，并且赋值为1，对应handler中的处理
					Message message=new Message();
					message.what=1;
					//必须send才有效
					handler.sendMessage(message);
					//将start按键置为不可点击，stop按键置为可点击
					startClickable=false;
					stopClickable=true;
				}
			}
		});
        bn_stop.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//只有点击了start才能点击stop
				if(timeThread!=null&&stopClickable)
				{
					//将控制线程的flag置为false，停止正在运行的时间线程
					threadFlag=false;
					//直接将这个线程置为null，此处使用stop方法停止线程会报错，此方法已经不推荐使用
					timeThread=null;
					Message message=new Message();
					message.what=2;
					handler.sendMessage(message);
					//stop按键设置为不可点击
					stopClickable=false;
				}
			}
		});
        bn_reset.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//满足下面的条件的只有一种情况，就是已经录制过了，且刚好是stop了
				if(stopClickable==false)
				{
					count=0;
					Message message=new Message();
					message.what=3;
					handler.sendMessage(message);
					//只有reset了才能够再次start
					startClickable=true;
				}				
			}
		});
    }
    private void GetThread()
    {
    	//初始化时间线程，在run方法中填写线程运行代码，必须由一个循环来控制
    	   timeThread=new Thread(new Runnable() {
   			
   			public void run() {
   				// TODO Auto-generated method stub
   				while(threadFlag)
   				{
   					try {
   						//睡眠一秒，sleep方法必须在try-catch中来监控异常
   						Thread.sleep(1000);
   					} catch (InterruptedException e) {
   						// TODO Auto-generated catch block
   						e.printStackTrace();
   					}
   					//通知handler更新时间
   					Message message=new Message();
   					message.what=0;
   					handler.sendMessage(message);
   				}
   			}
   		});
    }
}
