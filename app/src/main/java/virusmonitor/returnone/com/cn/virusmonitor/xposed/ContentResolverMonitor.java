package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.util.Log;

import java.net.URI;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/4/3.
 */
public class ContentResolverMonitor implements IXposedHookLoadPackage {


    private String Tag = "VirusMonitor" ;

    private String mPackageName ;

    private void init(XC_LoadPackage.LoadPackageParam param){
        mPackageName = param.packageName;

    }

    private void monitorQuery(XC_MethodHook.MethodHookParam param){
        Uri uri = (Uri) param.args[0];
        String strUri = uri.toString();
        String selection = (String) param.args[2];
        Log.e(Tag,"package: " +mPackageName + " 查询Uri : " + strUri + " selection: " +selection);
    }

    private void monitorInsert(XC_MethodHook.MethodHookParam param){
        Uri uri = (Uri) param.args[0];
        //ContentValues contentValues = (ContentValues) param.args[1];
        String strUri = uri.toString();
        Log.e(Tag,"package: " +mPackageName + " 向Uri : " + strUri + " 插入数据 " );


    }

    private  void monitorUpdate(XC_MethodHook.MethodHookParam param){
        Uri uri = (Uri) param.args[0];
        //ContentValues contentValues = (ContentValues) param.args[1];
        String strUri = uri.toString();
        String where = (String) param.args[2];
        Log.e(Tag,"package: " +mPackageName + " 更新数据 Uri : " + strUri + " where: " + where );

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        int isSysApp =  loadPackageParam.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }
        init(loadPackageParam);

        XposedHelpers.findAndHookMethod("android.content.ContentResolver",loadPackageParam.classLoader,"query",
                Uri.class, String[].class, String.class, String[].class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorQuery(param);
                    }
         });


        XposedHelpers.findAndHookMethod("android.content.ContentResolver",loadPackageParam.classLoader,"insert",
             Uri.class, ContentValues.class,
               new XC_MethodHook() {
                   @Override
                   protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                       monitorInsert(param);
                   }
               } );
        XposedHelpers.findAndHookMethod("android.content.ContentResolver",loadPackageParam.classLoader,"update",
                Uri.class, ContentValues.class, String.class, String[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorUpdate(param);
                    }
                });

    }
}
