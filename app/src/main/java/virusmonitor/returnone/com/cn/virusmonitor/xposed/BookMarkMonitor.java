package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.provider.Browser;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/6/4.
 * 监控插入书签行为
 * 测试成功
 */
public class BookMarkMonitor implements IXposedHookLoadPackage {

    private  static String myTag = "VirusMonitor";
    private String mPackageName ;
    private String clazz = "android.content.ContentResolver";
    private void init(XC_LoadPackage.LoadPackageParam param){
        mPackageName = param.packageName;
    }

    private void monitorInsertBookMark(XC_MethodHook.MethodHookParam param){
        ContentValues contentValues = (ContentValues) param.args[1];
        Object title = contentValues.get(Browser.BookmarkColumns.TITLE);
        Object url = contentValues.get(Browser.BookmarkColumns.URL);
        Log.e(myTag,mPackageName + " 插入书签- title: " + title.toString() + " url: " + url.toString());


    }





    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {
        int isSysApp =  param.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }
        init(param);


        XposedHelpers.findAndHookMethod(clazz,param.classLoader,"insert",
                Uri.class, ContentValues.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorInsertBookMark(param);
                    }
                }
        );

    }
}
