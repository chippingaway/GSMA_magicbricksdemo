package com.app.gsmademo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class MobileAuthenticationVolley {
    public static final String CLIENT_ID = "5d1e4397-10d4-4e25-b27a-1a43474c5289";
    public static final String CLIENT_SECRET = "0c1e0bab-8cb1-4b23-ad32-ded96749e51b";
    public static final String REDIRECT_URL = "https://www.magicbricks.com";
    public static boolean tokenhit = false;
    private static MobileAuthenticationVolley mobileAuthentication;
    private boolean loadingFinished = true, redirect = false;
    private long mRequestStartTime;
    private long RequestTimeDiscovery;
    private long RequestTimeAuthorization;
    private long RequestTimeToken;
    private long AuthorizationResponseTime = 0l;
    private ProgressDialog mDialog;
    private Context mContext;
    private mobileCallback mMobileCallback;
    public String state, nounce;
    public String oldnonce;

    /**
     * get new Instance of this class
     *
     * @return
     */
    public static MobileAuthenticationVolley getInstance() {
        if (mobileAuthentication == null) {
            mobileAuthentication = new MobileAuthenticationVolley();
        }
        return mobileAuthentication;
    }


    /**
     * This method is used to get details of the home operator that serves the end user.
     *
     * @param context
     * @param msisdn
     * @param mobileCallback
     */
    public void requestDiscoveryApi(final Context context, final String msisdn, mobileCallback mobileCallback) {
        mMobileCallback = mobileCallback;
        mContext = context;
        final long mRequestStartTime = System.currentTimeMillis();
        //TestFairy.addEvent("Discovery Start Time: "+mRequestStartTime);

        final ProgressDialog mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait ...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        final String url = "https://discover.mobileconnect.io/gsma/v2/discovery";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RequestTimeDiscovery = System.currentTimeMillis() - mRequestStartTime;
                //TestFairy.addEvent("Discovery Response Time: "+RequestTimeDiscovery);

                JSONObject resultJsonObject = null;
                try {
                    resultJsonObject = new JSONObject(response);
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
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                RequestTimeDiscovery = System.currentTimeMillis() - mRequestStartTime;
                // TestFairy.addEvent("Discovery Response Time: "+RequestTimeDiscovery);
                if (error instanceof NetworkError) {
                    Toast.makeText(mContext, "Network error.Please Try again", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(mContext, "Server not found", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(mContext, "Internet connection not found", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(mContext, " Network connection is slow. Please try with other network.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("Redirect_URL", REDIRECT_URL);
                data.put("msisdn", msisdn);
                data.put("testing", "true");
                return data;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", CLIENT_ID, CLIENT_SECRET);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GsmaDemoApplication.getInstance().addToRequestQueue(stringRequest);

    }

    /**
     * web view for handling authorization process
     *
     * @param client_id     - get from discovery response
     * @param client_secret - get from discovery response
     * @param linkListArray - auth url get from discovery response
     * @param msisdn        - mobile number
     * @throws UnsupportedEncodingException
     */
    private void loadWebView(final String client_id, final String client_secret, ArrayList<UrlBean> linkListArray, String msisdn) throws UnsupportedEncodingException {
        final WebView webView = new WebView(mContext);
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Please Wait ...");
        mDialog.setCancelable(true);
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

/***********  make authorization url for authenticate mobile operator *******************/

        builder.authority(linkUrl)
                .appendQueryParameter("client_id", client_id)
                .appendQueryParameter("scope", "mc_mnv_validate_plus")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", REDIRECT_URL)
                .appendQueryParameter("acr_values", "2")
                .appendQueryParameter("nonce", state)
                .appendQueryParameter("state", nounce)
                .appendQueryParameter("login_hint", msisdn);

        String myUrl = builder.build().toString();
        String finalUrl = myUrl.replace("//", "");
        String result = java.net.URLDecoder.decode(finalUrl, "UTF-8");
        WebSettings settings = webView.getSettings();

/***
 * fixed for web redirection issue
 */
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.requestFocus(View.FOCUS_DOWN);
//        settings.setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setSupportMultipleWindows(false);
//        settings.setDomStorageEnabled(true);
//        settings.setDatabaseEnabled(true);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(false);


        final long mRequestStartTime = System.currentTimeMillis();


        final String finalToken_url = token_url;
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("shouldOverrideUrl", url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                Log.e("onPageFinished", url);
                super.onPageFinished(view, url);
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();

                        AuthorizationResponseTime = System.currentTimeMillis() - mRequestStartTime;
                        //mMobileCallback.result(false, "");
                    }

                } else {
                    redirect = false;
                }

                if (!url.contains("india.mconnect") && !url.contains("india.gateway")) {
                    // webView.setVisibility(View.GONE);
                    String s[] = url.split("=");
                    String code = s[s.length - 1];
                    String new_token_url = finalToken_url.replaceFirst("token", "tokenmnv");
                    if (!url.contains("error=")) {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        RequestTimeAuthorization = System.currentTimeMillis() - mRequestStartTime;
                        if (!tokenhit) {
                            tokenhit = true;
                            /*********  hit token Api for  access token ************/
                            tokenApi(code, client_id, client_secret, new_token_url,state);
                        }
                    } else {
                        if (mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                        tokenhit = false;
                        String description = code.replace("+", " ");
                        mMobileCallback.result(false, description);
                    }
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("onPageStarted", url);
                super.onPageStarted(view, url, favicon);
                loadingFinished = false;
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (!((MainActivity) mContext).isFinishing() && mDialog != null) {
                    mDialog.dismiss();
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                Toast.makeText(view.getContext(), "Network is down", Toast.LENGTH_SHORT).show();
                Log.e("onReceivedError", rerr.toString());
                // Redirect to deprecated method, so you can use it in all SDK versions
                if (!((MainActivity) mContext).isFinishing() && mDialog != null) {
                    mDialog.dismiss();
                }
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
            }
        });
        webView.loadUrl(result);

    }

    /**
     * Token API request is used to get the id_token in response. It contains important security information about the Mobile Connect authentication.
     *
     * @param code          - get code from authorization Api response
     * @param client_id     - get from discovery response
     * @param client_secret - get from discovery response
     * @param token_url     - get from discovery response
     */

    private void tokenApi(final String code, final String client_id, final String client_secret, final String token_url, final String old_nonce) {
        final long mRequestStartTime = System.currentTimeMillis();
        final ProgressDialog mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait ...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        // final String url = "";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, token_url + "/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject resultJsonObject = null;
                try {
                    resultJsonObject = new JSONObject(response);
                    String id_token = resultJsonObject.getString("id_token");
                    String access_token = resultJsonObject.getString("access_token");;
                    String[] token = id_token.split("\\.");
                    String nonce = "";
                    if(token.length>1)
                    {
                        nonce = token[1];
                        String decode_nounce = decodeString(nonce);
                        JSONObject jsonObject = new JSONObject(decode_nounce);
                        String new_nonce = jsonObject.getString("nonce");
                        String new_sub = jsonObject.getString("sub");
                        if(new_nonce.equalsIgnoreCase(old_nonce))
                            userSaveInfoApi(new_sub,access_token);
                        else
                            mMobileCallback.result(false, response);
                    }
                    else
                        mMobileCallback.result(false, response);
                }
                catch (Exception e)
                {e.printStackTrace();}
                long tokenResponseTime = System.currentTimeMillis() - mRequestStartTime;
                tokenhit = false;
                Log.e("token APi error ", response);
               // mMobileCallback.result(true, response);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                tokenhit = false;
                mProgressDialog.dismiss();
                mMobileCallback.result(false, error.toString());
                long tokenResponseTime = System.currentTimeMillis() - mRequestStartTime;

            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                data.put("code", code);
                data.put("grant_type", "authorization_code");
                data.put("redirect_uri", REDIRECT_URL);
                return data;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s", client_id, client_secret);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization",auth);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GsmaDemoApplication.getInstance().addToRequestQueue(stringRequest);
    }

    /**
     * save info of user
     * @param old_sub
     * @param access_token
     */
    private void userSaveInfoApi( final String old_sub, final String access_token) {
        final String url = "https://india.mconnect.wso2telco.com/oauth2/userinfo?schema=openid";
        final ProgressDialog mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("Please Wait ...");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject resultJsonObject = null;
                try {
                    resultJsonObject = new JSONObject(response);
                    String new_sub = resultJsonObject.getString("sub");
                    if(new_sub.equalsIgnoreCase(old_sub))
                        mMobileCallback.result(true, response);
                    else
                        mMobileCallback.result(false, response);

                }
                catch (Exception e)
                {e.printStackTrace();}
                long tokenResponseTime = System.currentTimeMillis() - mRequestStartTime;
                Log.e("token APi error ", response);
                // mMobileCallback.result(true, response);
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                mMobileCallback.result(false, error.toString());
                long tokenResponseTime = System.currentTimeMillis() - mRequestStartTime;

            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<String, String>();
                return data;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+access_token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GsmaDemoApplication.getInstance().addToRequestQueue(stringRequest);


    }


    public String decodeString(String encoded) {
        byte[] dataDec = Base64.decode(encoded, Base64.DEFAULT);
        String decodedString = "";
        try {

            decodedString = new String(dataDec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } finally {

            return decodedString;
        }
    }


    /**
     * used for get callback results for verifying mobile
     */
    public interface mobileCallback {

        void result(boolean b, String response);
    }
}
