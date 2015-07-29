package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.app.PendingIntent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/3/26.
 * 监控发送短信 ，已经测试
 */
public class SmsMonitor implements IXposedHookLoadPackage {

    private static String Tag = "VirusMonitor";

    private String mPackageName ;




    private void monitorSendTextMessage(XC_MethodHook.MethodHookParam param){
        String destinationAddress = (String)param.args[0] ;
        String text = (String)param.args[2];
        Log.e(Tag,"package : " + mPackageName  + " sendTextMessage - 发送给 :" + destinationAddress + " 内容 : " + text);

    }

    private void monitorSendMultipartTextMessage(XC_MethodHook.MethodHookParam param){
        String destAdr = (String) param.args[0];
        ArrayList<String> data  = (ArrayList<String>) param.args[2];
        String text = "" ;
        for (String i :data){
            text = text  + " \n " + i;
        }
        Log.e(Tag,"package : " + mPackageName +" sendMultipartTextMessage - 发送给 : " + destAdr + " 内容 : " + text);
     }

    private void monitorSendDataMessage(XC_MethodHook.MethodHookParam param){
        String destAdr = (String) param.args[0];
        byte[] data = (byte[]) param.args[3];
        String text = new String(data);
        Log.e(Tag,"package : " + mPackageName + " sendDataMessage - " + "发送给 : " + destAdr +  " 内容 : " + text );


    }

    private void getAllMessagesFromSimMonitor(){
        Log.e(Tag, "package : "+ mPackageName + "调用getAllMessagesFromSim ，获取sim卡上短信");
    }
    private void init(XC_LoadPackage.LoadPackageParam param) {
        mPackageName = param.packageName;
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        init(loadPackageParam);



        int isSysApp =  loadPackageParam.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }


        XposedHelpers.findAndHookMethod("android.telephony.SmsManager",loadPackageParam.classLoader,"sendTextMessage",
         String.class,String.class,String.class, PendingIntent.class,PendingIntent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorSendTextMessage(param);
                    }
                }
                );
        XposedHelpers.findAndHookMethod("android.telephony.SmsManager" ,loadPackageParam.classLoader,"sendMultipartTextMessage",
        String.class, String.class,ArrayList.class , ArrayList.class,ArrayList.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorSendMultipartTextMessage(param);
                    }
                }
               );

        XposedHelpers.findAndHookMethod("android.telephony.SmsManager" ,loadPackageParam.classLoader,"sendDataMessage",
                String.class,String.class,short.class,byte[].class,PendingIntent.class,PendingIntent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        monitorSendDataMessage(param);
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.gsm.SmsManager" ,loadPackageParam.classLoader,"getAllMessagesFromSim",

                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        getAllMessagesFromSimMonitor();
                    }
                });


    }
}

