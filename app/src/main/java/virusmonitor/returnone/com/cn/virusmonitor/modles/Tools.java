package virusmonitor.returnone.com.cn.virusmonitor.modles;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by gaokun on 15/3/26.
 */

public class Tools {

    private  static String Tag = "Tools" ;

    public boolean isSystemApp(String packageName ,Context context){
        boolean isSys = false ;
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(packageName,0);
            return ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        }catch (PackageManager.NameNotFoundException e){
           String errorLog = e.getMessage();
            Log.e(Tag,errorLog);

        }

        return isSys;

    }


}
