package com.lonuery.download;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;

public class SystemDownLoad {
	
	Context context;
	String url,path,name;
	public SystemDownLoad(Context context,String url,String name,String path){
		this.context = context;
		this.url = url;
		this.name = name;
		this.path = path;
	}
	
	public void downLoad(){
		if(url!=null && name!=null && path!=null){
			DownloadManager download = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
			Uri uri = Uri.parse(url);
			Request request = new Request(uri);
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
			request.setVisibleInDownloadsUi(true);//设置是否显示下载的notification
			request.setTitle(name);
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			request.setDestinationInExternalPublicDir(path, "ITalkie_picc.apk");
			//设置下载文件的存放外部路径以及文件名,如果下载的是音乐等文件，这样设置能够让其他音乐扫描到。
			long id = download.enqueue(request);//将请求放入下载请求队列，并返回标志id
			
			SharedPreferences spf =  context.getSharedPreferences("download", Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = spf.edit();
			editor.putLong("download_id", id);//保存下载ID
			editor.commit();
		}	
	}
}
