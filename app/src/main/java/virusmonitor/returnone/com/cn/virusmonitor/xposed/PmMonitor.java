package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/6/2.
 * 测试成功
 */
public class PmMonitor implements IXposedHookLoadPackage{
    private String Tag = "VirusMonitor" ;

    private String mPackageName ;

    private String clazz = "android.app.ApplicationPackageManager" ;


    private void init(XC_LoadPackage.LoadPackageParam param){
        mPackageName = param.packageName;
    }


    private void monitorComponentEnable(XC_MethodHook.MethodHookParam param){
        int state = ((Integer)param.args[1]).intValue();
        ComponentName componentName = (ComponentName) param.args[0];
        if ( state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
            Log.e(Tag,mPackageName + " 禁用组件: " + componentName.getClassName());
        }

    }

    private void monitorGetInstallPackage(XC_MethodHook.MethodHookParam param){
        String packageName  = (String) param.args[0];
        Log.e(Tag, mPackageName + " 调用getInstallerPackageName ，参数 ：" + packageName );

    }

    private void monitorGetInstall(String api){

        Log.e(Tag ,mPackageName + "调用API:" + api + " 获取安装软件。 " );

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {
        int isSysApp =  param.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }
        init(param);

        XposedHelpers.findAndHookMethod(clazz,param.classLoader,"setComponentEnabledSetting",
                ComponentName.class,int.class,int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorComponentEnable(param);
                    }
                }
                );



        XposedHelpers.findAndHookMethod(clazz,param.classLoader,"getInstalledPackages",
                int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String api = "getInstalledPackages";
                        monitorGetInstall(api);
                    }
                });

        XposedHelpers.findAndHookMethod(clazz, param.classLoader, "getInstallerPackageName",String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorGetInstallPackage(param);
                    }
                });

        XposedHelpers.findAndHookMethod(clazz,param.classLoader, "getInstalledApplications",
                int.class,new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String api = "getInstalledApplications";
                        monitorGetInstall(api);
                    }
                });
    }

}
