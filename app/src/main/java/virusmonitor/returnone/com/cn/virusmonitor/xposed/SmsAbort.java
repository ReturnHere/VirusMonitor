package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/3/26.
 * 测试成功
 */
public class SmsAbort implements IXposedHookLoadPackage {

    private static String Tag = "VirusMonitor";
    private String mPackageName ;

    private static String clazz = "android.content.BroadcastReceiver$PendingResult";

    private void init(XC_LoadPackage.LoadPackageParam param){
        mPackageName = param.packageName;
    }

    private void monitorAbort(){
        Log.e(Tag,"package : "+ mPackageName + " 拦截广播") ;
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        int isSysApp =  loadPackageParam.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }

        init(loadPackageParam);
        XposedHelpers.findAndHookMethod(clazz,loadPackageParam.classLoader,"abortBroadcast",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorAbort();
                    }
                });


    }
}
