package ht.fw.com.loginandpayment;

import android.app.Application;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import ht.fw.com.loginandpayment.utils.Constant;


/**
 * Android Main Application
 */
public class AndroidApplication extends Application {

    private static AndroidApplication instance;


    // IWXAPI 是第三方app和微信通信的openapi接口
    public static IWXAPI apiPay;
    public static final String APP_ID = "wx9243246125edd242";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //开启支付宝沙箱环境
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);

        //微信登陆
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
//        api = WXAPIFactory.createWXAPI(this, APP_ID, false);
//        // 将该app注册到微信
//        api.registerApp(APP_ID);

        //支付
        apiPay = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);
        apiPay.registerApp(Constant.WX_APP_ID);

    }

    public static AndroidApplication getContext() {
        return instance;
    }

    public static AndroidApplication getInstance() {
        if (null == instance) {
            instance = new AndroidApplication();
        }
        return instance;
    }

}
