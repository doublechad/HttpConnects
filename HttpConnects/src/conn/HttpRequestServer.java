package conn;

import java.io.IOException;
import java.util.Map;

public interface HttpRequestServer {
	/**
	 *  以post方法傳遞
	 *  Content-Type:application/x-www-form-urlencoded
	 * @param url   目標url
	 * @param params   傳遞的參數
	 * @return
	 * @throws IOException
	 */
	public String post(String url,Map<String, Object> params) throws IOException;
	/**
	 * 以post方法傳遞
	 * Content-Type:application/json
	 * 
	 * @param url  目標url
	 * @param params 傳遞的參數
	 * @return
	 * @throws IOException
	 */
	public String postJson(String url,Map<String, Object> params) throws IOException;
	
	/**
	 * 
	 * @param url    目標url
	 * @param params 參數
	 * @return
	 * @throws IOException
	 */
	public String get(String url,Map<String,Object> params) throws IOException;
}
