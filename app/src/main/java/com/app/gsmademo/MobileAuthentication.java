/*
package com.app.gsmademo;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

*/
/**
 * Created by anu on 25/10/16.
 *//*

public class MobileAuthentication {

    private boolean loadingFinished = true, redirect = false;
    private long mRequestStartTime;
    private long RequestTimeDiscovery;
    private long RequestTimeAuthorization;
    private long RequestTimeToken;
    private long AuthorizationResponseTime = 0l;
    private ProgressDialog mDialog;
    private boolean tokenhit = false;
    private Context mContext;
    private mobileCallback mMobileCallback;
    private String state, nounce;
    private static MobileAuthentication mobileAuthentication;

   */
/* public static final String CLIENT_ID = "79bb90d5-2be9-4db5-9d0f-1ff600a14ed6";
    public static final String CLIENT_SECRET = "89947399-81d6-4d8c-9794-e4e6de290c06";
    public static final String REDIRECT_URL ="http://gsma.applaurels.com/android";*//*


    public static final String CLIENT_ID = "5d1e4397-10d4-4e25-b27a-1a43474c5289";
    public static final String CLIENT_SECRET = "0c1e0bab-8cb1-4b23-ad32-ded96749e51b";
    public static final String REDIRECT_URL = "https://www.magicbricks.com";

    */
/**
     * get new Instance of this class
     *
     *
     * @return
     *//*

    public static MobileAuthentication getInstance() {
        if (mobileAuthentication == null) {
            mobileAuthentication = new MobileAuthentication();
        }
        return mobileAuthentication;
    }

    */
/**
     * This method is used to get details of the home operator that serves the end user.
     *
     * @param context
     * @param msisdn
     * @param mobileCallback
     *//*

    public void requestDiscoveryApi(final Context context, final String msisdn, mobileCallback mobileCallback) {
        mMobileCallback = mobileCallback;
        mContext = context;

        final long mRequestStartTime = System.currentTimeMillis();
        TestFairy.addEvent("Discovery Start Time: "+mRequestStartTime);

        final ProgressDialog mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String url = "https://discover.mobileconnect.io/gsma/v2/";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("Redirect_URL", REDIRECT_URL);
        params.put("msisdn", msisdn);
        params.put("testing", "true");

        ApiInterface service = RestApi.createService(ApiInterface.class, url, true);

        Call<ResponseBody> call = service.hitDiscovery(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                RequestTimeDiscovery = System.currentTimeMillis() - mRequestStartTime;
                TestFairy.addEvent("Discovery Response Time: "+RequestTimeDiscovery);

                JSONObject resultJsonObject = null;
                try {
                    resultJsonObject = new JSONObject(response.body().string());
                    if (resultJsonObject.has("error")) {
                        mMobileCallback.result(false, resultJsonObject.getString("description"));
                    } else {
                        JSONObject jsonResponse = resultJsonObject.getJSONObject("response");
                        LoginBean loginBean = new LoginBean();
                        String client_id = jsonResponse.getString("client_id");
                        String client_secret = jsonResponse.getString("client_secret");
                        loginBean.setServingOperator(jsonResponse.getString("serving_operator"));
                        loginBean.setCountry(jsonResponse.getString("country"));
                        JSONObject operatorJson = jsonResponse.getJSONObject("apis").getJSONObject("operatorid");
                        JSONArray linkArray = operatorJson.getJSONArray("link");
                        ArrayList<UrlBean> linkListArray = new ArrayList<>();
                        for (int i = 0; i < linkArray.length(); i++) {
                            JSONObject linkObject = linkArray.getJSONObject(i);
                            UrlBean urlBean = new UrlBean();
                            urlBean.setLink(linkObject.getString("href"));
                            urlBean.setRelation(linkObject.getString("rel"));
                            linkListArray.add(urlBean);

                        }
                        loginBean.setLinkArray(linkListArray);
                        loadWebView(client_id, client_secret, linkListArray, msisdn);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                   // Toast.makeText(context,"Pro")
                   // Toast.makeText()
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                RequestTimeDiscovery = System.currentTimeMillis() - mRequestStartTime;
                TestFairy.addEvent("Discovery Response Time: "+RequestTimeDiscovery);
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    */
/**
     * web view for handling authorization process
     * @param client_id     - get from discovery response
     * @param client_secret - get from discovery response
     * @param linkListArray - auth url get from discovery response
     * @param msisdn        - mobile number
     * @throws UnsupportedEncodingException
     *//*

    private void loadWebView(final String client_id, final String client_secret, ArrayList<UrlBean> linkListArray, String msisdn) throws UnsupportedEncodingException {
        final WebView webView = new WebView(mContext);
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Please Wait ...");
        mDialog.setCancelable(false);
        mDialog.show();
        String linkUrl = "";
        String token_url = "";
        for (int i = 0; i < linkListArray.size(); i++) {
            if (linkListArray.get(i).getRelation().equalsIgnoreCase("authorization")) {
                linkUrl = linkListArray.get(i).getLink();
            } else if (linkListArray.get(i).getRelation().equalsIgnoreCase("token")) {
                token_url = linkListArray.get(i).getLink();

            }

        }

        state = "state123" + System.currentTimeMillis();//unique alphanumeric 36 base
        nounce = "nounce123" + System.currentTimeMillis();
        Uri.Builder builder = new Uri.Builder();

*/
/***********  make authorization url for authenticate mobile operator *******************//*


        builder.authority(linkUrl)
                .appendQueryParameter("client_id", client_id)
                .appendQueryParameter("scope", "mc_mnv_validate_plus")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", REDIRECT_URL*/
/*"http://gsma.applaurels.com/android"*//*
)
                .appendQueryParameter("acr_values", "2")
                .appendQueryParameter("nonce", state)
                .appendQueryParameter("state", nounce)
                .appendQueryParameter("login_hint", msisdn);

        String myUrl = builder.build().toString();
        String finalUrl = myUrl.replace("//", "");
        String result = java.net.URLDecoder.decode(finalUrl, "UTF-8");
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        final long mRequestStartTime = System.currentTimeMillis();
        TestFairy.addEvent("Authorization Start Time: "+mRequestStartTime);


        final String finalToken_url = token_url;
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, final String url) {
                Log.e("url", url);

                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();

                        AuthorizationResponseTime = System.currentTimeMillis() - mRequestStartTime;
                        TestFairy.addEvent("Authorization Response Time: "+AuthorizationResponseTime);
                        mMobileCallback.result(false, "");
                    }

                } else {
                    redirect = false;
                }

                if (!url.contains("india.mconnect") && !url.contains("india.gateway")) {
                    webView.setVisibility(View.GONE);
                    String s[] = url.split("=");
                    String code = s[s.length - 1];
                    String new_token_url = finalToken_url.replaceFirst("token", "tokenmnv");
                    if (!url.contains("error=")) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();

                        }
                        RequestTimeAuthorization = System.currentTimeMillis() - mRequestStartTime;
                        TestFairy.addEvent("Authorization Response Time: "+AuthorizationResponseTime);

                        if (!tokenhit) {
                            tokenhit = true;

                            */
/*********  hit token Api for  access token ************//*

                            tokenApi(code, client_id, client_secret, new_token_url);
                        }
                    } else {
                        tokenhit = false;
                        String description = code.replace("+", " ");
                        mMobileCallback.result(false, description);
                    }
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadingFinished = false;


            }


            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("Tag", "Error: " + description);

            }
        });
        webView.loadUrl(result);

    }

    */
/**
     * Token API request is used to get the id_token in response. It contains important security information about the Mobile Connect authentication.
     *
     * @param code          - get code from authorization Api response
     * @param client_id     - get from discovery response
     * @param client_secret - get from discovery response
     * @param token_url     - get from discovery response
     *//*

    private void tokenApi(final String code, final String client_id, final String client_secret, final String token_url) {
       */
/* final long mRequestStartTime = System.currentTimeMillis();
        TestFairy.addEvent("Token Start Time: "+mRequestStartTime);

        final ProgressDialog mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        final String url = "http://gsma.applaurels.com/android/";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("token_url", token_url);
        params.put("client_id", client_id);
        params.put("client_secret", client_secret);

        ApiInterface service = RestApi.createService(ApiInterface.class, url, false);
        Call<ResponseBody> call = service.hitToken(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    long tokenResponseTime = System.currentTimeMillis() - mRequestStartTime;
                    TestFairy.addEvent("Token Response Time: "+tokenResponseTime);
                    tokenhit = false;
                    String res = response.body().string();
                    Log.e("token APi error ", res);
                    mMobileCallback.result(true, res);
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                long tokenResponseTime = System.currentTimeMillis() - mRequestStartTime;
                TestFairy.addEvent("Token Response Time: "+tokenResponseTime);
                mMobileCallback.result(false, t.getMessage());
            }
        });*//*

    }

    */
/**
     * used for get callback results for verifying mobile
     *//*

    public interface mobileCallback {

        void result(boolean b, String response);
    }
}
*/
