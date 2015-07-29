package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/3/26.
 * 拦截命令行执行，已经测试
 */
public class RunShell implements IXposedHookLoadPackage {

    private static String Tag = "VirusMonitor";

    private static String clazz = "java.lang.Runtime";

    private String mPackageName ;

    private void init(XC_LoadPackage.LoadPackageParam param){
        mPackageName = param.packageName;

    }

    private  void monitorShell(XC_MethodHook.MethodHookParam param){
        String cmd = (String) param.args[0];
        Log.e(Tag,"package : " + mPackageName + " 执行命令: " + cmd );
    }

    private void monitorExec(XC_MethodHook.MethodHookParam param){
        String[] progArray = (String[]) param.args[0];
        String text = "";
        for(String i :progArray){
            text = text + i + " \n " ;
        }
        Log.e(Tag,"package : " + mPackageName + " 执行命令: " + text );

    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {


        int isSysApp =  loadPackageParam.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }
        init(loadPackageParam);

        XposedHelpers.findAndHookMethod(clazz,loadPackageParam.classLoader,"exec",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorShell(param);
                    }
                });
        XposedHelpers.findAndHookMethod(clazz,loadPackageParam.classLoader,"exec",
                String[].class,String[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorExec(param);
                    }
                }

            );
        XposedHelpers.findAndHookMethod(clazz,loadPackageParam.classLoader,"exec",
                String.class,String[].class, File.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorShell(param);
                    }
                });
        XposedHelpers.findAndHookMethod(clazz,loadPackageParam.classLoader,"exec",
                String[].class,String[].class,File.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorExec(param);
                    }
                });
        XposedHelpers.findAndHookMethod(clazz,loadPackageParam.classLoader,"exec",
                String[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorExec(param);
                    }
                });
        XposedHelpers.findAndHookMethod(clazz,loadPackageParam.classLoader,"exec",
                String.class,String[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorShell(param);
                    }
                });
    }
}
