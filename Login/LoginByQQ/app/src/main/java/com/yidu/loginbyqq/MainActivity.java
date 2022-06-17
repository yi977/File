package com.yidu.loginbyqq;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.tencent.tauth.Tencent;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {

    public static Tencent mTencent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }
    //unity调用的登录方法
    public void LoginQQ()
    {//定义一个对象，里面的第一个参数是自己在QQ开放平台上申请的APPID。
        mTencent = Tencent.createInstance("101987553",this.getApplicationContext());
        if (!mTencent.isSessionValid())
        {
            mTencent.login(this, "all", loginListener);
        }
    }
    //登陆成功设置token和openid 这两个值用于服务器校验账号或者相关逻辑
    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
            Log.e("initOpenidAndToken",jsonObject.toString());
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    //登陆成功回调
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            initOpenidAndToken(values);//上面设置token和openid的
            GetInfo();//获取个人信息的函数
        }
    };
    //获取个人信息
    public void GetInfo(){
        Log.e("GetInfo","GetInfo");
        UserInfo info = new UserInfo(MainActivity.this, mTencent.getQQToken());
        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                Log.e("onComplete", "个人信息：" + object.toString());
                try {
                    String nickname = ((JSONObject) object).getString("nickname");
                    UnityPlayer.UnitySendMessage("GameManager","AndroidCallBack",nickname);//向Unity发送回调信息
                } catch (JSONException e) {
                }
            }
            @Override
            public void onError(UiError uiError) {
            }
            @Override
            public void onCancel() {
            }

            @Override
            public void onWarning(int i) {

            }
        });
    }
    //重要！回调反馈
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //监听回调的类
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            if (null == response) {
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (jsonResponse.length() == 0) {
                return;
            }
            doComplete((JSONObject) response);
        }
        protected void doComplete(JSONObject values) {
        }
        @Override
        public void onError(UiError e) {
        }
        @Override
        public void onCancel() {
        }
        @Override
        public void onWarning(int i) {

        }
    }
}