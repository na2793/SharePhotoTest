package com.study.hancom.sharephototest.util.shareUtil;

import android.os.Environment;
import android.util.Log;

import com.study.hancom.sharephototest.util.HttpUtil;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class WepublShareStrategy<T> extends AsyncTaskShareStrategy<T> implements IShareStrategy {
    private HttpURLConnection mHttpURLConnection;

    @Override
    public final void perform(File file, List params) {
        execute(file, params);
    }

    @Override
    protected final T validate(File file, List params) {
        return null;
    }

    @Override
    protected final T sendRequest(File file, List params) throws IOException {
        String boundary = "0000000000HancomOffice";

        URL url = new URL("http://s-api.wepubl.com/api/bupload"); // s-api.wepubl.com/member/rlogin
        mHttpURLConnection = (HttpURLConnection) url.openConnection();
        mHttpURLConnection.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
        mHttpURLConnection.setRequestProperty("Cache-Control", "no-cache");
        mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        mHttpURLConnection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
        mHttpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        mHttpURLConnection.setUseCaches(false); //캐시를 사용하지 않게 설정
        mHttpURLConnection.setDoInput(true); //input을 사용하도록 설정 (default : true)
        mHttpURLConnection.setDoOutput(true); //output을 사용하도록 설정 (default : false)

        //mHttpURLConnection.setConnectTimeout(1000); //타임아웃 시간 설정 (default : 무한대기)

        DataOutputStream os = null;
        try {
            os = new DataOutputStream(mHttpURLConnection.getOutputStream());

            // 오늘 날짜
            Date date = new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            HttpUtil.addDefaultForm(os, "userKey", "dd3724a3-bbe7-423b-9d1c-109119540c50", boundary);
            HttpUtil.addDefaultForm(os, "category", "3001", boundary);
            HttpUtil.addDefaultForm(os, "bookId", "0", boundary);
            HttpUtil.addDefaultForm(os, "title", "test", boundary);
            HttpUtil.addDefaultForm(os, "author", "SharePhoto", boundary);
            HttpUtil.addDefaultForm(os, "bookKey", "CE0CE046-4238-4153-925B-B3691D51F2DB", boundary);
            HttpUtil.addDefaultForm(os, "publisher", "hancom", boundary);
            HttpUtil.addDefaultForm(os, "publishDate", dateFormat.format(date), boundary);
            HttpUtil.addDefaultForm(os, "isbn", "uploadTest", boundary);
            HttpUtil.addDefaultForm(os, "BookIntro", "", boundary);
            HttpUtil.addDefaultForm(os, "BookIndex", "1", boundary);
            HttpUtil.addDefaultForm(os, "authorBio", "SharePhoto", boundary);
            HttpUtil.addFileForm(os, "cover", new File(Environment.getExternalStorageDirectory() + "/Pictures/KakaoTalk/1489466740778.jpg"), boundary);
            HttpUtil.addFileForm(os, "ebook", file, boundary);

            os.writeBytes("--" + boundary + "--\r\n");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                os.close();
            }
        }

        return null;
    }

    @Override
    protected final T receiveResponse(StringBuilder result) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(mHttpURLConnection.getInputStream(), "UTF-8")); //캐릭터셋 설정
            String line;
            while ((line = br.readLine()) != null) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                br.close();
            }
            if (mHttpURLConnection != null) {
                mHttpURLConnection.disconnect();
            }
        }

        return null;
    }
}
