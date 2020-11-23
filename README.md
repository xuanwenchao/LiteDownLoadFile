# LiteDownLoadFile
down load file with okhttpclient Lightly in android.

# How to use
Confirm your project added it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add it in your root build.gradle at the end of repositories:

```
dependencies {
        implementation 'com.github.xuanwenchao:LiteDownLoadFile:0.14'
}
```

# Sample code

```java
String strDWDir = "down load diretory...";
String strDWName = "local file name...";

DownLoadUtilWithOKHttp dw = DownLoadUtilWithOKHttp.getInstance();
dw.start(checkUpdate.getNewVersionDownloadUrl(), strDWDir, strDWName, new DownLoadUtilWithOKHttp.OnDownloadListener() {

  @Override
  public void onDownloadSuccess(File file) {
  //do your job.
  }

  @Override
  public void onDownloading(int progress) {
  //do your job.
  }

  @Override
  public void onDownloadFailed(Exception e) {
  //do your job.
  }
});
```
