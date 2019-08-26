package ht.fw.com.loginandpayment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import ht.fw.com.loginandpayment.bean.AuthResult;
import ht.fw.com.loginandpayment.utils.OrderInfoUtil2_0;
import ht.fw.com.loginandpayment.utils.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 支付宝支付业务：入参app_id
     */
    private static String APPID = "";
    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    private static String PID = "";
    /**
     * 支付宝账户登录授权业务：入参target_id值
     */
    private static String TARGET_ID = "";
    private static String SIGN = "";
    private Tencent mTencent;   //qq登录
    private static final int SDK_AUTH_FLAG = 2;
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "进入支付页面", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,PaymentActivity.class));
            }
        });

        ImageView ivWeChat = findViewById(R.id.ivWeChat);
        ImageView ivQQ = findViewById(R.id.ivQQ);
        ImageView ivZhiFuBao = findViewById(R.id.ivZhiFuBao);
        ivWeChat.setOnClickListener(this);
        ivQQ.setOnClickListener(this);
        ivZhiFuBao.setOnClickListener(this);

        //获取支付宝参数
        SharedPreferencesHelper sph = new SharedPreferencesHelper(this);
        APPID = (String) sph.getSharedPreference("zhifubao_app_id", "");
        PID = (String) sph.getSharedPreference("zhifubao_pid", "");
        TARGET_ID = (String) sph.getSharedPreference("zhifubao_target_id", "");
        SIGN = (String) sph.getSharedPreference("zhifubao_sign", "");

    }

    private void showLoading() {
        pg = ProgressDialog.show(this, "", "", true, true);
    }

    private void hideLoading() {
        if (pg != null)
            pg.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivWeChat:
                if (!AndroidApplication.apiPay.isWXAppInstalled()) {
                    Toast.makeText(this, "请先安装微信", Toast.LENGTH_SHORT).show();
                } else {
                    SendAuth.Req req = new SendAuth.Req();
                    req.scope = "snsapi_userinfo";
                    req.state = "wechat_sdk_loging";
                    AndroidApplication.apiPay.sendReq(req);
                }
                break;
            case R.id.ivQQ:
                //qq登录
                if (mTencent == null) {
                    mTencent = Tencent.createInstance("101491277", this);
                }
                //如果session不可用，则登录，否则说明已经登录
                if (!mTencent.isSessionValid()) {
                    mTencent.login(this, "all", new BaseUiListener());
                }
                break;
            case R.id.ivZhiFuBao:
                if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID) || TextUtils.isEmpty(TARGET_ID)) {
                    Toast.makeText(this, "缺少相关信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                /**
                 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
                 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
                 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
                 * authInfo的获取必须来自服务端；
                 */
                boolean rsa2 = true;
                Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
                String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);
                final String authInfo = info + "&" + SIGN;
                Runnable authRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造AuthTask 对象
                        AuthTask authTask = new AuthTask(LoginActivity.this);
                        // 调用授权接口，获取授权结果
                        Map<String, String> result = authTask.authV2(authInfo, true);

                        Message msg = new Message();
                        msg.what = SDK_AUTH_FLAG;
                        msg.obj = result;
                        mHandler.sendMessage(msg);
                    }
                };
                // 必须异步调用
                Thread authThread = new Thread(authRunnable);
                authThread.start();
                break;
            default:
                break;
        }
    }

    private class BaseUiListener implements IUiListener {

        public void onComplete(Object response) {
            //调用QQ登录的接口,将openid参数传递到接口中, 获取从QQ传递过来的数据,进行登录操作
            JSONObject jb = (JSONObject) response;
            try {
                jb.getString("openid");
                Toast.makeText(LoginActivity.this, "成功登录QQ,数据是:" + response.toString(), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            Toast.makeText(getApplicationContext(), "QQ登录出错", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), "QQ取消登录", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == Constants.REQUEST_LOGIN) {
                Tencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked") final AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();
                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        //L.e("授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()));
                        //登录支付宝接口,传入如下参数
                        authResult.getAuthCode();
                        Toast.makeText(LoginActivity.this, "支付宝登录成功,数据是:" + authResult.toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

}
