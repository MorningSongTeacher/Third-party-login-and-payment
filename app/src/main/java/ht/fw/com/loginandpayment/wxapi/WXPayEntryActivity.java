package ht.fw.com.loginandpayment.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import ht.fw.com.loginandpayment.R;
import ht.fw.com.loginandpayment.utils.Constant;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "进入微信支付";

	private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
		api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {  //0成功  -1失败  -2取消授权
			if (resp.errCode == 0) {
				// 支付成功
				Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
			} else if (resp.errCode == -1) {
				// 支付失败
				Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
			} else {
				// 取消
				Toast.makeText(this, "支付取消", Toast.LENGTH_SHORT).show();
			}
			//发送EventBus关闭页面
//			EventBus.getDefault().post(new WeChatPayCodeEvent(resp.errCode));
		}
		finish();
	}
}