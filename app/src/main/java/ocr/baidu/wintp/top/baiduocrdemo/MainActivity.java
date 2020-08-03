package ocr.baidu.wintp.top.baiduocrdemo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.ui.camera.CameraActivity;
import com.google.gson.Gson;

import org.w3c.dom.Text;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * 打开相机的请求码
     */
    private static final int OPEN_CAMERA_OK = 100;
    /**
     * 解析图片文字成功 Handler使用
     */
    private static final int PRESER_IMG_OK = 101;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PRESER_IMG_OK:
                    String data = (String) msg.obj;
                    preserData(data);

                    break;
            }
        }
    };
    private TextView mTv_content;


    private void preserData(String data) {
        Log.e("MainActivity", "MainActivity preserData()" + data);

        Gson gson = new Gson();
        WordInfo wordInfo = gson.fromJson(data, WordInfo.class);
//{"log_id": 1861715874821339263, "words_result_num": 2, "words_result": [{"words": "我不服"}, {"words": "除了我自己,谁还能打败我"}]}


        if (wordInfo.getError_code() != null) {
            if (wordInfo.getError_code() == 17 || wordInfo.getError_code() == 19 || wordInfo.getError_code() == 18) {
                Toast.makeText(MyApp.getContext(), "请求量超出限额", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        List<WordInfo.WordsResultBean> words_result = wordInfo.getWords_result();
        Log.e("MainActivity", "MainActivity preserData()" + wordInfo.getWords_result());

        if (wordInfo.getWords_result() == null || wordInfo.getWords_result_num() < 0 || wordInfo.getWords_result().size() == 0) {
            Toast.makeText(MyApp.getContext(), "文字扫描识别失败，请重试", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();


        for (WordInfo.WordsResultBean wordsResultBean : words_result) {
            sb.append(wordsResultBean.getWords());
        }


        mTv_content.setText(sb.toString().toUpperCase());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTv_content = findViewById(R.id.tv_content);
        Button openOcrUi = findViewById(R.id.btn_open_ocr_ui);


        openOcrUi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraByBaidu();
            }
        });

    }


    /**
     * 打开相机
     */
    public void openCameraByBaidu() {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                FileUtil.getSaveFile(getApplication()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, OPEN_CAMERA_OK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //判断请求码是否是请求打开相机的那个请求码
        if (requestCode == OPEN_CAMERA_OK && resultCode == RESULT_OK) {

            String photoPath = FileUtil.getSaveFile(this).getAbsolutePath();
            checkData(photoPath);
        }
    }


    /**
     * 请求百度API接口，进行获取数据
     *
     * @param filePath
     */
    private void checkData(String filePath) {

        try {

            //把图片文件转换为字节数组
            byte[] imgData = FileUtil.readFileByBytes(filePath);

            //对字节数组进行Base64编码
            String imgStr = Base64Util.encode(imgData);
            final String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");

            RequestParams entiry = new RequestParams(ConstantValue.BAIDU_TOKEN_URL);

            x.http().get(entiry, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(final String result) {
                    Gson gson = new Gson();
                    TokenInfo tokenInfo = gson.fromJson(result, TokenInfo.class);

                    final String access_token = tokenInfo.getAccess_token();
//{"access_token":"24.67af609b67f663650ed69a84518a0f72.2592000.1528504251.282335-11217555","session_key":"9mzdDcHuY4qqTxcI6X7Df9IwCoGQKbZmKV2yMzv7mtEdHP6EfDYgO2uL6Ujut7Rr29HRsZgQH1mtncgiYN1h9JZmxsmaAg==","scope":"public vis-ocr_ocr brain_ocr_scope brain_ocr_general brain_ocr_general_basic brain_ocr_general_enhanced vis-ocr_business_license brain_ocr_webimage brain_all_scope brain_ocr_idcard brain_ocr_driving_license brain_ocr_vehicle_license vis-ocr_plate_number brain_solution brain_ocr_plate_number brain_ocr_accurate brain_ocr_accurate_basic brain_ocr_receipt brain_ocr_business_license brain_solution_iocr wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test\u6743\u9650 vis-classify_flower lpq_\u5f00\u653e cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base","refresh_token":"25.81bce312cce25e0352d68929fcdc8c1b.315360000.1841272251.282335-11217555","session_secret":"6e04c5d4a6bda19b2c18216561ee16ea","expires_in":2592000}

                    new Thread() {
                        public void run() {
                            String resultStr = HttpUtil.post(ConstantValue.BAIDU_INTER_URL, access_token, params);
                            Log.e("MainActivity", "MainActivity onSuccess()" + resultStr);
                            Message msg = Message.obtain();
                            msg.obj = resultStr;
                            msg.what = PRESER_IMG_OK;
                            handler.sendMessage(msg);
                        }
                    }.start();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.i("MainActivity", "onError: " + ex);

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
