# LiteDownLoadFile
down load file with okhttpclient Lightly in android.


String strDWDir = "down load diretory...";
String strDWName = "local file name...";

DownLoadUtilWithOKHttp dw = DownLoadUtilWithOKHttp.getInstance();
dw.start(checkUpdate.getNewVersionDownloadUrl(), strDWDir, strDWName, new DownLoadUtilWithOKHttp.OnDownloadListener() {
  
  @Override
  public void onDownloadSuccess(File file) {
  }
  
  @Override
  public void onDownloading(int progress) {
  }
  
  @Override
  public void onDownloadFailed(Exception e) {
  }
  
});

