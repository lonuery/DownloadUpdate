#DownloadUpdate

写了一个下载更新的小demo，使用的是系统自带的下载服务，也就是版本检测，决定是否下载由自己编
写代码来进行控制，但是下载的操作由android系统自带的下载更新服务进行完成。

##DownLoad
这个类的主要功能为:

- 检测网络环境，检测手机存储环境决定是否连接服务器。
- 获取服务器上的配置文件信息获取服务器上apk的版本信息，与本机版本进行对比，看服务器上的版本是否高于本机版本。
<!-- lang:Java -->
	@Override
	protected Result doInBackground(Void... arg0) {
		if(checkNetWorkStatus()&&checkSDcardStatus()){
			getServerConfInfo(url);
		}
		return null;
	}
由于要连接网络，所以这个类继承了AsyncTask，检测网络和本地存储空间，然后获取服务器上的配置
文件，获取其中信息来判断服务器上apk的版本。

##DownLoadReceive

这个类继承了BroadcastReceiver，因为当apk系统下载完成后，他会发出一个DownloadManager.ACTION_DOWNLOAD_COMPLETE广播，我们注册一个广播服务，然后接收这个广播
在下载完成后就能够实现：弹出安装界面，提示你进行安装。在接收到广播后实现以下代码就能够实现
弹窗提示安装
<!-- lang:Java -->
	private void installApk(String path,Context context){
		File file = new File(path);
		if(!file.exists()){
			Log.i("DownLoadReceive", "文件不存在");
			return ;
		}
		// 通过Intent安装apk文件，自动打开安装界面
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//由于是在BroadcastReceive中启动activity，所以启动方式必须设置为FLAG_ACTIVITY_NEW_TASK
		context.startActivity(intent);			
	}

##SystemDownLoad

这个类主要功能为：调用其中的downLoad()方法，在这个方法中将下载任务放进下载的任务队列中进行
下载。

##MainActivity

MainActivity中的handler接收Download中发过来的版本信息，然后弹窗让用户决定是否进行下载更新。
###注意
ServiceDownLoad类还没实现，本来是想自己做一个下载更新服务，后来知道系统有自带的更新服务就
决定使用系统的DownloadManager来管理下载。想了解使用系统下载更新功能了解一下DownloadManager就够了。


