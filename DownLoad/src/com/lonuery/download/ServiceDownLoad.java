package com.lonuery.download;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

/**    
 *这个文件暂时没有用,本来想自己写一个服务去连接服务器下载。
 * @Author <mail="lonuery@gmail.com">冯金龙   
 *  
 */
public class ServiceDownLoad extends Service{

	String url,apkName;
	@Override
	public IBinder onBind(Intent intent) {
		Bundle bundle = (Bundle)intent.getExtras();
		url = bundle.getString("url");
		apkName = bundle.getString("name");
		return null;
	}
	
	@Override
	public void onCreate() {		
		download(url,apkName,true);
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void download(String str,String name,boolean isDownload){
		String savePath = android.os.Environment.getExternalStorageDirectory() + "/" + "download";
		try {
			URL url = new URL(str);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(5000);
			connection.connect();
			InputStream input = connection.getInputStream();
			File fileDir = new File(savePath);
			if(!fileDir.exists()){
				fileDir.mkdirs();
			}
			
			File fileList[] = fileDir.listFiles();
			for (File file2 : fileList){
				if(file2.getName().equals(name)){
					file2.delete();
				}
			}			
			File file = new File(fileDir, name);
			
			FileOutputStream  output = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			while(input.read(buf)!=-1 || isDownload){				
				output.write(buf);
				//更新进度条
				
			}
			//自动安装
			output.close();
			input.close();
			if(isDownload){
				installApk(savePath,name);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void installApk(String path,String fileName){
		File apk = new File(path, fileName);
		if(!apk.exists()){
			return ;
		}
		// 通过Intent安装apk文件
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apk.toString()), 
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
}
