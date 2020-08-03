package ocr.baidu.wintp.top.baiduocrdemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 作者：  pyfysf
 * <p>
 * qq:  337081267
 * <p>
 * CSDN:    http://blog.csdn.net/pyfysf
 * <p>
 * 个人博客：    http://wintp.top
 * <p>
 * 时间： 2018/05/2018/5/10 8:11
 * <p>
 * 邮箱：  pyfysf@163.com
 */
public class HttpUtil {
    public static String post(String requestUrl, String accessToken, String params) {
        try {
            String generalUrl = requestUrl + "?access_token=" + accessToken;
            URL url = new URL(generalUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            // 设置通用的请求属性
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 得到请求的输出流对象
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(params);
            out.flush();
            out.close();

            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> headers = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : headers.keySet()) {
                System.out.println(key + "--->" + headers.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = null;
            if (requestUrl.contains("nlp"))
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
            else
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String result = "";
            String getLine;
            while ((getLine = in.readLine()) != null) {
                result += getLine;
            }
            in.close();
            System.out.println("result:" + result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
