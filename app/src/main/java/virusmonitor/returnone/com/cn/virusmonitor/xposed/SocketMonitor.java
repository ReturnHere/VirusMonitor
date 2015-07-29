package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.net.InetAddress;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/7/29.
 */
public class SocketMonitor implements IXposedHookLoadPackage{

    private static String Tag = "VirusMonitor";

    private String mPackageName ;

    private String clazz = "java.net.Socket";


    private void init(XC_LoadPackage.LoadPackageParam param) {
        mPackageName = param.packageName;
    }

    private void hookConstructorOne(XC_MethodHook.MethodHookParam param){
        InetAddress inetAddress = (InetAddress) param.args[0];
        int port =(Integer) param.args[1];
        Log.e(Tag,mPackageName  + " 尝试建立套接字 ,地址为： " + inetAddress.toString() + " port :" + port);

    }

    private void hookConstructorTwo(XC_MethodHook.MethodHookParam param){
        InetAddress inetAddress = (InetAddress) param.args[0];
        int port =(Integer) param.args[1];
        InetAddress localAddr = (InetAddress) param.args[2];
        int localort =(Integer) param.args[3];
        Log.e(Tag,mPackageName  + " 尝试建立套接字 ,地址为： " + inetAddress.toString() + " port :" + port +  " 本地地址 ： " + localAddr.toString() + " 本地端口 ： " + localort);

    }

    private void hookConstructorThree(XC_MethodHook.MethodHookParam param){
       String  host = (String) param.args[0];
        int port =(Integer) param.args[1];

        Log.e(Tag,mPackageName  + " 尝试建立套接字 ,地址为： " + host + " port :" + port );

    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        init(loadPackageParam);



        int isSysApp =  loadPackageParam.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }
        XposedHelpers.findAndHookConstructor(clazz, loadPackageParam.classLoader, InetAddress.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                hookConstructorOne(param);
            }

        });

        XposedHelpers.findAndHookConstructor(clazz, loadPackageParam.classLoader, InetAddress.class, int.class, InetAddress.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                hookConstructorTwo(param);
            }
        });

        XposedHelpers.findAndHookConstructor(clazz, loadPackageParam.classLoader, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                hookConstructorThree(param);
            }
        });

        XposedHelpers.findAndHookConstructor(clazz, loadPackageParam.classLoader, String.class, int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                hookConstructorThree(param);
            }
        });

        XposedHelpers.findAndHookConstructor(clazz, loadPackageParam.classLoader, String.class, int.class, InetAddress.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                hookConstructorThree(param);
            }
        });

    }
}
