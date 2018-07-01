package conn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * 使用OKHTTP 傳遞參數
 * 需要額外掛載   okhttp-3.10.0.jar
 * 			 okio-1.14.1.jar
 * 			 org.json.jar
 * 
 * @author  doublechad
 *
 */
public class HttpByOKHTTP implements HttpRequestServer{
	OkHttpClient client = new OkHttpClient();
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	/**
	 * Content-Type:application/x-www-form-urlencoded
	 * 
	 * @param url    目標網址
	 * @param params    參數
	 * @return       網頁回應狀態
	 * @throws IOException
	 */
	public String post(String url,Map<String, Object> params) throws IOException {
		
		Builder builder = new FormBody.Builder();
		builder =setParam(builder,params);
		RequestBody formBody = builder.build();
		Request request = new Request.Builder()
		      .url(url)
		      .post(formBody)
		      .build();
		  Response response = client.newCall(request).execute();
		  System.out.println(response.code());
		  String temp =response.body().string();
		  response.close();	 
		  return temp;
		}

	@Override
	public String get(String url, Map<String, Object> params) throws IOException {
		StringBuffer buffer =new StringBuffer(url);
		String urlParam= null;
		if(params!=null) {
			buffer.append("?");
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
		Request request = new Request.Builder().url(urlParam).build();
		Response response = client.newCall(request).execute();
		return response.body().string();	
	}
	
	/**
	 * Content-Type:application/json; charset=utf-8
	 * 
	 */
	@Override
	public String postJson(String url, Map<String, Object> params) throws IOException {
		
		JSONObject obj =new JSONObject(params);
		RequestBody body = RequestBody.create(JSON, obj.toString());
		  Request request = new Request.Builder()
		      .url(url)
		      .post(body)
		      .build();
		  Response response = client.newCall(request).execute();
		  String result = response.body().string();
		  System.out.println(result);
		  return result;

	}
	/**
	 * 設置參數
	 * GET-> url?x1=1&x2=2
	 * POST-> application/json or application/x-www-form-urlencoded
	 * @param builder 
	 * @param params  參數設置
	 * @return
	 */
	private Builder setParam(Builder builder,Map params) {
		Set<Entry> set = params.entrySet();
		for(Entry e1 : set) {
			if(e1.getValue().getClass().isArray()) {
				for(Object values : (Object[])e1.getValue()){
					builder.add(e1.getKey().toString(), values.toString());
				}
			}else {
				builder.add(e1.getKey().toString(), e1.getValue().toString());
			}	
		}
		return builder;
	}
	
}
