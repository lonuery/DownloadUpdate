package com.lonuery.download;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class MyDialog extends Dialog{

	Context context;	
	TextView sure,cancle,describe;
	String taskDescribe;

   public MyDialog(Context context, int theme,String taskDescribe){
		super(context, theme);
		this.taskDescribe = taskDescribe;
   }
   @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog);
		sure = (TextView)findViewById(R.id.sure);
		cancle = (TextView)findViewById(R.id.cancel);
		describe = (TextView)findViewById(R.id.warn);
		describe.setText("È·¶¨Òª"+taskDescribe+"Âð?");
   }
   public void setListener(android.view.View.OnClickListener listener){
	   	cancle.setOnClickListener(listener);
		sure.setOnClickListener(listener);			
   }
}
