package com.lonuery.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

public class DownLoad extends AsyncTask<Void, Void, Result>{
	
	Context context;
	String versionCode,url;
	Handler handler;

	public DownLoad(Context context,String versionCode,Handler handler,String url){
		this.context = context;
		this.versionCode = versionCode;
		this.handler = handler;
		this.url = url;
	}
	
	@Override
	protected Result doInBackground(Void... arg0) {
		if(checkNetWorkStatus()&&checkSDcardStatus()){
			getServerConfInfo(url);
		}
		return null;
	}
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
	}
	
	public boolean checkNetWorkStatus(){
		ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        
		//check for wifi also
        WifiManager connec = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if(cm!=null){
        	State wifi = cm.getNetworkInfo(1).getState();
            if (connec.isWifiEnabled()
                    && wifi.toString().equalsIgnoreCase("CONNECTED")) {
                return true;
            }
        }          
        //check for network     
        try{
	        if (cm != null) {
	            NetworkInfo netInfo = cm.getActiveNetworkInfo();
	            if (netInfo != null && netInfo.isConnected()) {
	                return true;
	            }
	        }
        }catch (Exception ex){
	      Log.e("Network Avail Error", ex.getMessage());
        }
        return false;
	}
	
	public boolean checkSDcardStatus(){
		if(android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File file = Environment.getExternalStorageDirectory();
			StatFs statfs = new StatFs(file.getPath());
			long availableBlock = statfs.getAvailableBlocks();//空闲block的数量
			long blockSize = statfs.getBlockSize();//block的大小
			long availableSize = availableBlock * blockSize / 1024 /1024;	// 单位MB
			if(availableSize >= 50){
				return true;
			}
		}
		return false;
	}
	
	//比较版本号，获取的版本号大于本地版本号返回true
	public boolean compareVersionCode(String serverCode){
		if(versionCode!=null&&serverCode!=null){
			try {
				int client = Integer.parseInt(versionCode);
				int server = Integer.parseInt(serverCode);
				if(server>client){
					return true;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}							
		}
		return false;
	}
	
	public void getServerConfInfo(String path){
		try {
			URL url = new URL(path);
			
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setDoInput(true);
			connection.connect();
			InputStream input =  connection.getInputStream();			
			HashMap<String, String> hashMap = parseConf(input);
			
			if(compareVersionCode(hashMap.get("versionCode"))){
				Message msg = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("name", hashMap.get("name"));
				bundle.putString("url", hashMap.get("url"));
				msg.obj = bundle;
				msg.what = 100;
				handler.sendMessage(msg);		
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String,String> parseConf(InputStream input){	
		HashMap<String, String> hashMap = new HashMap<String, String>();
		try {			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(input);			
			Element root = document.getDocumentElement();
			NodeList childNodes = root.getChildNodes();
			for(int i = 0; i < childNodes.getLength(); i++){
				Node childNode = childNodes.item(i);
				if(childNode.getNodeType() == Node.ELEMENT_NODE){
					Element childElement = (Element)childNode;
					if(childElement.getNodeName().equals("name")){
						hashMap.put("name", childElement.getFirstChild().getNodeValue());
					}else if(childElement.getNodeName().equals("versionCode")){
						hashMap.put("versionCode", childElement.getFirstChild().getNodeValue());
					}else if(childElement.getNodeName().equals("url")){
						hashMap.put("url", childElement.getFirstChild().getNodeValue());
					}
				}
			}			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return hashMap;
	}
}
