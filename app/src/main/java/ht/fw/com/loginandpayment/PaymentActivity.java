package ht.fw.com.loginandpayment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONObject;

import java.util.Map;

import ht.fw.com.loginandpayment.bean.AliPayResult;
import ht.fw.com.loginandpayment.utils.Constant;

public class PaymentActivity extends Activity implements View.OnClickListener {

    //这个参数应当从服务端获取
    String payParams = "alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2018010901726579&biz_content=%7B%22out_trade_no%22%3A%22P0020181030174916000009%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22total_amount%22%3A%220.01%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2F202.103.95.140%3A8111%2Fapi%2Fboot%2Fnotify%2FaliPayNotifyRes.htm&sign=HriU%2F1h9z2f0qc3Lf7l%2BJg1nvp86EmV%2FEM%2BF79CwX6Hv58wVb7fmN%2B6NTmGUWExq0k%2FwvFRZvHnbZkYNeebpHAhrVuUAy6QOx%2FUmfBf2%2BnLIHBFaCmv1oKfFH31ZIiMI976ts8e21xYqCSxtSNgeamRzv1VEz%2Ft8oIJ9tHydcEzeQH5a9uV5i%2FhltbA3TDd5mytNJncMmv9%2FaFTl2f3TNum5%2FqJ7mebBH%2FIdn6ai7TsoGbQy4gIPrqCPdZxsDCdZYDb2a7AdvHQfyGuGmhyXMRP0k87Ol1pigd6yy0R5EOS94D3U%2FYBa3sRuyyG1BdrOaZoiCnnAzN01DsB%2BriixuA%3D%3D&sign_type=RSA2&timestamp=2018-10-31+16%3A50%3A55&version=1.0";
    private RelativeLayout rl_weixin,rl_zhifubao,rl_yue;
    //    String payParams = "alipay_sdk=alipay-sdk-java-dynamicVersionNo&app_id=2018010901726579&biz_content=%7B%22body%22%3A%22%E6%88%91%E6%98%AF%E6%B5%8B%E8%AF%95%E6%95%B0%E6%8D%AE%22%2C%22out_trade_no%22%3A%2220181031010101test23%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22subject%22%3A%22App%E6%94%AF%E4%BB%98%E6%B5%8B%E8%AF%95Java%22%2C%22timeout_express%22%3A%2230m%22%2C%22total_amount%22%3A%220.01%22%7D&charset=utf-8&format=json&method=alipay.trade.app.pay&notify_url=%E5%95%86%E6%88%B7%E5%A4%96%E7%BD%91%E5%8F%AF%E4%BB%A5%E8%AE%BF%E9%97%AE%E7%9A%84%E5%BC%82%E6%AD%A5%E5%9C%B0%E5%9D%80&sign=Q17MA01udiyaJBQc9em62mkGAECcTIEatafG85pC%2FDB8kbzTMbT3HLi%2BNi%2BpUYHFCq%2FCg3HeGnxA%2F1Sy25C%2Fe6oajCXMA%2FdjS6fMUvFOVuyhjIhE9VBpw1cGJdnlN%2FQPKJjXcieG8XHsxFXGfjrj4kAebt9%2BrS6xFgpYLHb6q08xrBthCeJGWzeWtuR2U%2BgIJF9jZNVr5lWFt0rsGGqL4Rwryjx%2FrC1zutUxRj7J4tZ7Wj6pNYQ7nHqFa9Jx2j1pJ4L1PUzOI0GWM0ICutDumJNu4lcFaNXIZr4qbrKviK%2BE20lI0znCGYqLJYrjX10J7xwU%2FkV4dLRf%2Fcl3cnPnDQ%3D%3D&sign_type=RSA2&timestamp=2018-10-31+11%3A03%3A45&version=1.0";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_pay_page);

        rl_weixin = findViewById(R.id.rl_weixin);
        rl_zhifubao = findViewById(R.id.rl_zhifubao);
        rl_yue = findViewById(R.id.rl_yue);
        rl_weixin.setOnClickListener(this);
        rl_zhifubao.setOnClickListener(this);
        rl_yue.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_weixin:
                if (TextUtils.isEmpty(payParams) || payParams == null) {
                    Toast.makeText(this, "支付订单异常，无法支付", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (payParams.length() > 0) {
                        JSONObject data = new JSONObject(payParams);
                        PayReq req = new PayReq();
                        req.appId = data.optString("appId");
                        req.partnerId = data.optString("partnerId");
                        req.prepayId = data.optString("prepayId");  //预付ID
                        req.nonceStr = data.optString("nonceStr");
                        req.timeStamp = data.optString("timeStamp");    //时间戳
                        req.packageValue = data.optString("packageValue");
                        req.sign = data.optString("sign");
                        req.extData = "app data"; // optional
                        //L.e("正常调起支付:" + req.getType());
                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                        AndroidApplication.apiPay.sendReq(req);
                    } else {
                        Log.e("微信","服务器请求错误");
                    }
                } catch (Exception e) {
                    Log.e("微信", e.getMessage());
                }
                break;
            case R.id.rl_zhifubao:
                if (payParams == null || "".equals(payParams)) {
                    Toast.makeText(this, "支付订单异常，无法支付", Toast.LENGTH_SHORT).show();
                    return;
                }
                //        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
                /**
                 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
                 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
                 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
                 * orderInfo的获取必须来自服务端；
                 */
                Runnable payRunnable = new Runnable() {
                    @Override
                    public void run() {
                        PayTask alipay = new PayTask(PaymentActivity.this);
                        Map<String, String> result = alipay.payV2(payParams, true);
                        Log.i("msp", result.toString());

                        Message msg = new Message();
                        msg.what = Constant.ALIPAY_SDK_PAY_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };
                Thread payThread = new Thread(payRunnable);
                payThread.start();
                break;
            case R.id.rl_yue:
                //与金额有关需要进行加密处理

                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.ALIPAY_SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(PaymentActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                        EventBus.getDefault().post(new OrderEvent(true));     //做刷新页面的处理
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(PaymentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
}
