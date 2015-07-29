package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.net.URI;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/4/3.
 */
public class FileMonitor implements IXposedHookLoadPackage{

    private String Tag = "VirusMonitor" ;
    private static String sysTag = "/system" ;

    private String mPackageName ;

    private void init(XC_LoadPackage.LoadPackageParam param){
        mPackageName = param.packageName;

    }

    private void monitorFile(XC_MethodHook.MethodHookParam param){
        String pathname =  (String) param.args[0] ;

         if (pathname.startsWith(sysTag) || pathname.startsWith(".")){
                Log.e(Tag,"package: "+ mPackageName +" 生成File对象，pathname: " +pathname);
            }




    }

    private void monitorFileOne(XC_MethodHook.MethodHookParam param){
        File parent = (File) param.args[0];
        if(parent.getAbsolutePath().startsWith(sysTag)){
            String parentPath = parent.getAbsolutePath();
            String child = (String) param.args[1];
            Log.e(Tag , "package: "+ mPackageName + " 生成File对象，parent :" + parentPath + " child: " +child);
        }

    }


    private void monitorFileTwo(XC_MethodHook.MethodHookParam param){
        String parent = (String) param.args[0];
        if (parent.startsWith(sysTag)){
            String child = (String) param.args[1];
            Log.e(Tag , "package: "+ mPackageName + " 生成File对象，parent :" + parent+ " child: " +child);

        }

    }


    private void monitorFileUri(XC_MethodHook.MethodHookParam param){
        URI uri = (URI) param.args[0];
        String strUri = uri.toASCIIString();
        Log.e(Tag , "package: "+ mPackageName + " 生成File对象，URI :" + strUri);

    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        int isSysApp =  loadPackageParam.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }
        init(loadPackageParam);

        XposedHelpers.findAndHookConstructor("java.io.File",loadPackageParam.classLoader,
        String.class,
         new XC_MethodHook() {
             @Override
             protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                monitorFile(param);
             }
         });

        XposedHelpers.findAndHookConstructor("java.io.File",loadPackageParam.classLoader,
         File.class,String.class,
         new XC_MethodHook() {
             @Override
             protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                monitorFileOne(param);
             }
         });
        XposedHelpers.findAndHookConstructor("java.io.File", loadPackageParam.classLoader,
                String.class,String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorFileTwo(param);
                    }
                });
        XposedHelpers.findAndHookConstructor("java.io.File", loadPackageParam.classLoader,
                URI.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorFileUri(param);
                    }
                });
    }
}
