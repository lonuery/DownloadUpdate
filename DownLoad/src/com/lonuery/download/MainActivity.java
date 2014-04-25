package com.lonuery.download;

import java.io.File;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	
	Button btn;
	String url ="http://192.168.1.101:8080/app/update.xml";
	String versionCode = "0";
	DownLoadReceive receiver;
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==100){
				final Bundle bundle = (Bundle)msg.obj;
				
				final MyDialog mydialog = new MyDialog(MainActivity.this,R.style.MyDialog,"下载");
				mydialog.show();
				
				WindowManager m = getWindowManager();
				Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用//
				WindowManager.LayoutParams params = mydialog.getWindow().getAttributes();  
				params.height = (int) (d.getHeight() * 0.3);
				params.width = (int) (d.getWidth() * 0.75);  
				mydialog.getWindow().setAttributes(params);
				
				Window dialogWindow = mydialog.getWindow(); 
				dialogWindow.setGravity(Gravity.CENTER);
				mydialog.setListener(new OnClickListener() {					
					@Override
					public void onClick(View view) {
						if(view.getId()==R.id.sure){
							//由于可能会下载多次，所以遍历整个文件夹，删除相同名字的文件
							String savePath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "download";
							File fileDir = new File(savePath);
							if(!fileDir.exists()){
								fileDir.mkdirs();
							}				
							File fileList[] = fileDir.listFiles();
							for (File file2 : fileList){
								if(file2.getName().equals(bundle.getString("name", "ITalkie_picc.apk"))){
									file2.delete();
								}
							}
							SystemDownLoad systemDownLoad = new SystemDownLoad(MainActivity.this, 
									bundle.getString("url", "http://192.168.1.101:8080/app/ITalkie_picc.apk"), 
									bundle.getString("name", "ITalkie_picc.apk"),
									"/download/");
							systemDownLoad.downLoad();
							mydialog.cancel();
						}else if(view.getId()==R.id.cancel){
							mydialog.cancel();
						}
					}
				});
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(this);
		
		receiver = new DownLoadReceive();
		IntentFilter intent = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		registerReceiver(receiver, intent);
	}
	
	@Override
	public void onClick(View arg0) {
		new DownLoad(this, versionCode, handler,url).execute();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		//removeDownload();//退出去后删除download 的Notification，同时也会删除下载的apk文件
		super.onDestroy();
	}
	
	private void removeDownload() { 
		DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
		SharedPreferences spf =  this.getSharedPreferences("download", Activity.MODE_PRIVATE);
		long downloadId = spf.getLong("download_id", 0);
		if(downloadId!=0){
			downloadManager.remove(downloadId);
		}
	} 
}
