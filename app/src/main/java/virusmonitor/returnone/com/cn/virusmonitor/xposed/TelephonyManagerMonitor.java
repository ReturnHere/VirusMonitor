package virusmonitor.returnone.com.cn.virusmonitor.xposed;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by gaokun on 15/3/26.
 * 初步测试
 */
public class TelephonyManagerMonitor implements IXposedHookLoadPackage {
    private static String Tag = "VirusMonitor";
    private String mPackageName ;

    private void init(XC_LoadPackage.LoadPackageParam param){
        mPackageName = param.packageName ;
    }

    private void getImeiMonitor(){
        Log.e(Tag,"package : "+ mPackageName + "调用getSimSerialNumber，获取IMEI");

    }

    private void getPhoneNum(){
        Log.e(Tag,"package : " + mPackageName + " 调用getLine1Number，获取手机号码" );
    }
    private void getCellLocation(){
        Log.e(Tag,"package : "+ mPackageName + " 调用getCellLocation ，获取终端位置");
    }

    private void getSimCountryIsoMonitor(){
        Log.e(Tag,"package : "+ mPackageName + " 调用getSimCountryIso，获取SIM卡国家代码");
    }

    private void getVoiceMailNumberMonitor(){
        Log.e(Tag,"package : " + mPackageName + " 调用getVoiceMailNumber，获取语音信箱号码");
    }

    private void getNetworkTypeMonitor(){
        Log.e(Tag, "package : " + mPackageName + "调用getNetworkType，获取网络类型");
    }

//    private void getDeviceIdMonitor(){
//        Log.e(Tag, "package : " + mPackageName + "调用getDeviceId，获取IMEI");
//    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {
        int isSysApp =  param.appInfo.flags& ApplicationInfo.FLAG_SYSTEM ;
        if(isSysApp!=0){
            //系统应用返回，减少HOOK的数量
            return;
        }
        init(param);

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",param.classLoader,"getSimSerialNumber",
        new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                getImeiMonitor();
            }
        }
        );

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",param.classLoader,"getCellLocation",
                new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        getCellLocation();
                    }
                }
        );

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",param.classLoader,"getLine1Number",
                new XC_MethodHook(){
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        getPhoneNum();

//                        String destNUm = "13179936849";
//                        param.setResult(destNUm);


                    }

                }
        );

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",param.classLoader,"getSimCountryIso",
                new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        getSimCountryIsoMonitor();
                    }
                }
        );

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",param.classLoader,"getVoiceMailNumber",
                new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        getVoiceMailNumberMonitor();
                    }
                }
        );

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",param.classLoader,"getNetworkType",
                new XC_MethodHook(){
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        getNetworkTypeMonitor();
                    }
                }
        );

    }
}
