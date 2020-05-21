package com.gnetop.ltgame.core.model;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.gnetop.ltgame.core.common.Constants;
import com.gnetop.ltgame.core.common.LTGameCommon;
import com.gnetop.ltgame.core.common.LTGameOptions;
import com.gnetop.ltgame.core.platform.Target;
import com.gnetop.ltgame.core.util.PreferencesUtils;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AccessToken基类
 */
public abstract class AccessToken {
    //线程池
    private static ExecutorService sService;
    private String openid;//授权用户唯一标识。
    private String unionid;
    private String access_token;//接口调用凭证
    private long expires_in;//access_token接口调用凭证超时时间，单位（秒）。

    /**
     * 是否可用
     */
    public boolean isValid() {
        if (getLoginTarget() == Target.LOGIN_WX) {
            return getAccess_token() != null && getUnionid() != null;
        } else
            return getAccess_token() != null && getOpenid() != null;
    }

    public String getSocialId() {
        if (getLoginTarget() == Target.LOGIN_WX) {
            return unionid;
        } else
            return openid;
    }

    private String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public abstract int getLoginTarget();


    @Override
    public String toString() {
        return "BaseAccessToken{" +
                "openid='" + openid + '\'' +
                ", unionid='" + unionid + '\'' +
                ", access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                '}';
    }

    /**
     * 保存Token
     */
    public static void saveToken(final Context context, final String key,String tokenTimeKey,
                                 final Object token) {
        LTGameOptions opts = LTGameCommon.getInstance().options();
        if (opts.getTokenExpiresHoursMs() <= 0) {
            return;
        }
        if (sService == null) {
            sService = Executors.newSingleThreadExecutor();
        }
        sService.execute(() -> {
            try {
                PreferencesUtils.init(context);
                if (token != null) {
                    PreferencesUtils.putString(context, key, token + "");
                    PreferencesUtils.putLong(tokenTimeKey, System.currentTimeMillis());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Json解析
     */
    public static <T> T getWXToken(final Context context, String key, final Class<T> tokenClazz) {
        LTGameOptions opts = LTGameCommon.getInstance().options();
        if (opts.getTokenExpiresHoursMs() <= 0) {
            return null;
        }
        long time = PreferencesUtils.getLong(context, Constants.LT_QQ_TOKEN_TIME);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - time < opts.getTokenExpiresHoursMs()) {
            T t = null;
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, key))) {
                String token = PreferencesUtils.getString(context, key).substring(15,
                        PreferencesUtils.getString(context, key).length());
                t = new Gson().fromJson
                        (token, tokenClazz);
            }
            return t;
        } else {
            return null;
        }
    }
    /**
     * Json解析
     */
    public static <T> T getQQToken(final Context context, String key, final Class<T> tokenClazz) {
        LTGameOptions opts = LTGameCommon.getInstance().options();
        if (opts.getTokenExpiresHoursMs() <= 0) {
            return null;
        }
        long time = PreferencesUtils.getLong(context, Constants.LT_QQ_TOKEN_TIME);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - time < opts.getTokenExpiresHoursMs()) {
            T t = null;
            if (!TextUtils.isEmpty(PreferencesUtils.getString(context, key))) {
                String token = PreferencesUtils.getString(context, key).substring(15,
                        PreferencesUtils.getString(context, key).length());
                Log.e("TAG","==============qqToken========="+token);
                t = new Gson().fromJson
                        (token, tokenClazz);
            }
            return t;
        } else {
            return null;
        }
    }
}
