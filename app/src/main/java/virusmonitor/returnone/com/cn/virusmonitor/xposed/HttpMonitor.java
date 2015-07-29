package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.util.Log;


import java.net.URL;
import java.net.URLStreamHandler;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/3/26.
 */
public class HttpMonitor implements IXposedHookLoadPackage {

    private static String Tag = "VirusMonitor";

    private String mPackageName ;


    private void init(XC_LoadPackage.LoadPackageParam param) {
        mPackageName = param.packageName;
    }

    private void urlMonitor(XC_MethodHook.MethodHookParam param){
        String URL = (String) param.args[0];
        Log.e(Tag,"package: " + mPackageName + " 尝试访问 ：" + URL );
    }

    private void urlMonitorOne(XC_MethodHook.MethodHookParam param){
        String protocol = (String) param.args[0];
        String host = (String) param.args[1];
        Integer port = (Integer) param.args[2];
        String file = (String) param.args[3];
        Log.e(Tag,"package: " + mPackageName + "尝试访问网络，protocol : "+ protocol + " host : "+ host +
        " port: " + port + " file : "+ file);
    }

    private void urlMonitorTwo(XC_MethodHook.MethodHookParam param){
        String protocol = (String) param.args[0];
        String host = (String) param.args[1];
        String file  = (String) param.args[2];
        Log.e(Tag , "package: "+ mPackageName + "尝试访问网络，protocol : "+ protocol + " host: " + host + " file: " +file );
    }

    private void urlMonitorThree(XC_MethodHook.MethodHookParam param){
        URL url =  (URL)param.args[0];
        String spec = (String) param.args[1];
        String host =  url.getHost();
        String message = "package: "+ mPackageName + "尝试访问网络，spec: " +spec + " host: " + host;
        Log.e(Tag,message);

    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        int isSysApp =  loadPackageParam.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }

        init(loadPackageParam);

        XposedHelpers.findAndHookConstructor("java.net.URL",loadPackageParam.classLoader,String.class,
         new XC_MethodHook() {
             @Override
             protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                 urlMonitor(param);
             }
         });

        XposedHelpers.findAndHookConstructor("java.net.URL",loadPackageParam.classLoader,
         String.class,String.class,int.class,String.class,
          new XC_MethodHook() {
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                urlMonitorOne(param);
              }
          });

        XposedHelpers.findAndHookConstructor("java.net.URL",loadPackageParam.classLoader,
         String.class,String.class ,int.class, String.class, URLStreamHandler.class,
          new XC_MethodHook() {
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                  urlMonitor(param);
              }
          });
        XposedHelpers.findAndHookConstructor("java.net.URL",loadPackageParam.classLoader,
         String.class,String.class,String.class,
          new XC_MethodHook() {
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    urlMonitorOne(param);
              }
          });
        XposedHelpers.findAndHookConstructor("java.net.URL", loadPackageParam.classLoader,
         URL.class,String.class,
         new XC_MethodHook() {
             @Override
             protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                 urlMonitorThree(param);
             }
         });

        XposedHelpers.findAndHookConstructor("java.net.URL",loadPackageParam.classLoader,
         URL.class,String.class,URLStreamHandler.class,
          new XC_MethodHook() {
              @Override
              protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                  urlMonitorThree(param);
              }
          });

    }
}
