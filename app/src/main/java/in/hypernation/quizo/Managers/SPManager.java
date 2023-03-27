package in.hypernation.quizo.Managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {
    private static SharedPreferences sharedPreferences;

    private SPManager(){}

    public static void init(Context context){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        }
    }

    public static String getStringValue(String key, String defValue){
        return sharedPreferences.getString(key, defValue);
    }

    public static void setStringValue(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static Boolean getBooleanValue(String key, Boolean defValue){
        return sharedPreferences.getBoolean(key, defValue);
    }

    public static void setBooleanValue(String key, Boolean value){
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

}
