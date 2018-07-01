package conn;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

public class HttpByJavaNET implements HttpRequestServer{
	static{	
		//for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){
	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
		try {
			SSLSocketFactory factory = getSSLFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(factory);
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			
		}
		
	}
	@Override
	public String post(String url, Map<String, Object> params) throws IOException {
		URL target_url =new URL(url);
		String result=null;
		String bodyData =setParam(params);
		URLConnection conn =target_url.openConnection();
		try {
			Method method = conn.getClass().getMethod("setRequestMethod", new Class[]{String.class});
			method.invoke(conn,"POST");
		} catch (NoSuchMethodException | SecurityException |IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			System.out.println(e.getMessage());
		}
		conn.setRequestProperty( "charset", "utf-8");
		conn.setDoOutput(true);
		conn.setDoInput(true); 
		conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		DataOutputStream dops = new DataOutputStream(conn.getOutputStream());
		dops.write(bodyData.getBytes());
		dops.flush();
		dops.close();
		conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
		return sb.toString();
	}

	@Override
	public String postJson(String url, Map<String, Object> params) throws IOException {
		URL target_url =new URL(url);
		String result=null;
		String bodyData =new JSONObject(params).toString();
		URLConnection conn =target_url.openConnection();
		try {
			Method method = conn.getClass().getMethod("setRequestMethod", new Class[]{String.class});
			method.invoke(conn,"POST");
		} catch (NoSuchMethodException | SecurityException |IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			System.out.println(e.getMessage());
		}
		conn.setRequestProperty( "charset", "utf-8");
		conn.setDoOutput(true);
		conn.setDoInput(true); 
		conn.setRequestProperty("Content-Type","application/json");
		DataOutputStream dops = new DataOutputStream(conn.getOutputStream());
		dops.write(bodyData.getBytes());
		dops.flush();
		dops.close();
		conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
		return sb.toString();
	}
	
	@Override
	public String get(String url, Map<String, Object> params) throws IOException {
		URL target_url =new URL(setParam(url,params));
		String result=null;
		URLConnection conn =target_url.openConnection();
		result=getResponse(conn.getInputStream());
		return result;
	}
	/**
	 * 返回 application/x-www-form-urlencoded 格式 字串
	 * @param params  參數
	 * @return
	 */
	private String setParam(Map<String, Object> params) {		
		return setParam("",params);
	}
	/**
	 * url後增加參數，若url為空則只設定params
	 * @param url  目標url
	 * @param params  參數
	 * @return
	 */
	private String setParam(String url,Map<String, Object> params) {
		StringBuffer buffer =new StringBuffer(url);
		String urlParam= null;
		if(url.equals(""))buffer.append("?");
		if(params!=null) {
			//把參數添加到url後面
			for(Entry e1 :params.entrySet()) {
				if(e1.getValue().getClass().isArray()) {
					for(String s :(String[])e1.getValue()) {
						buffer.append(e1.getKey()).append("=").append(s).append("&");
					}
				}else {
					buffer.append(e1.getKey()).append("=").append(e1.getValue()).append("&");
				}
			}
			int index =(buffer.lastIndexOf("&"));
			urlParam =buffer.substring(0, index).trim();
		}else {
			urlParam =buffer.toString().trim();
		}
		return urlParam;
	}
	/**
	 * 從InputStream 取得回應資料
	 * @param  inStream 串流資料
	 * @return 伺服器回應
	 * @throws IOException 
	 */
	private String getResponse(InputStream inStream) throws IOException {
		//資料串流處理利用構造函數傳入  InputStream->InputStreamReader->BufferedReader
		BufferedReader rsv =new BufferedReader(new InputStreamReader(inStream));
		String line =null;
		StringBuffer buffer =new StringBuffer();
		while ((line = rsv.readLine()) != null) { 
			buffer.append(line+"\n");
        }
		return buffer.toString();
	}
	/**
	 * 產生一個SSL連線
	 * @return SSLSocketFactory SSL連線工廠
	 * @throws NoSuchAlgorithmException  未找到文件，錯誤的密碼，錯誤的密鑰存儲類型...
	 * @throws KeyManagementException    密鑰管理異常
	 * 
	 */
	private  static SSLSocketFactory getSSLFactory() throws NoSuchAlgorithmException, KeyManagementException  {
		TrustManager[] tm =new TrustManager[] {
				 new X509TrustManager() {
					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					@Override
					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					}
					@Override
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					}
				}
		 };
			SSLContext ctx = SSLContext.getInstance("SSL");
			ctx.init(null, tm, new java.security.SecureRandom());
			return ctx.getSocketFactory();
	}

}
