package com.example.admin.piemapdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.admin.piemapdemo.constats.Path;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

import pie.core.GisNative;
import pie.core.Workspace;

/**
 *首次启动主页面
 */

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener{

    private String mapPath;
    private String mapResourcePath;
    private SharedPreferences mPreferences;
    private static final String MAP_RESOURCE_NAME = "piesdk.zip";
    private Button but1;
    private Button but2;
    private Button but3;
    private Button but4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGisNative();
        setContentView(R.layout.activity_home_page);


        initData();
        initView();//初始化控件


        boolean needCopy = mPreferences.getBoolean("needCopyMapRes", true);
        if (needCopy) {
            new MapResourceAsyncTask().execute();
        }

        openWorkspace();//打开工作区间

    }


    /**
     * 初始地图所需资源的路径
     * 确保地图资源已存在
     * 注意：初始化一定要在初始化MapView之前
     */
    private void initGisNative() {
        // 路径确保存在，确保地图资源存在
        mapPath = Path.PATH_PIE_MAP_RES_DEFAULT;
        GisNative.init(this, mapPath);
    }


    private void initView() {
        but1 = (Button) findViewById(R.id.but_1);
        but2 = (Button) findViewById(R.id.but_2);
        but3 = (Button) findViewById(R.id.but_3);
        but4 = (Button) findViewById(R.id.but_4);
        but1.setOnClickListener(this);
        but2.setOnClickListener(this);
        but3.setOnClickListener(this);
        but4.setOnClickListener(this);

    }


    private void initData() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            mapResourcePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/PIE/sdk";
        } else {
            Toast.makeText(this, "SD卡不可用，请确保SD卡可用", Toast.LENGTH_SHORT).show();
            finish();
        }
        mPreferences = getPreferences(Context.MODE_PRIVATE);
    }




    private void openWorkspace() {

        if (PieMapApiApplication.mWorkspace != null) {
            return;
        }
        PieMapApiApplication.mWorkspace = new Workspace();
        boolean isOpen = PieMapApiApplication.mWorkspace.open(mapPath
                + "workspace.xml");

        if (!isOpen) {
            Toast.makeText(this, "打开工作区间失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private class MapResourceAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            boolean isSuccess = copyMapResource(mapResourcePath,
                    MAP_RESOURCE_NAME);
            if (isSuccess) {
                return 1;// 复制和解压成功
            } else {
                return 0;// 复制和解压失败
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            SharedPreferences.Editor editor = mPreferences.edit();
            switch (result) {
                case 0:
                    Toast.makeText(HomePageActivity.this, "复制地图资源失败，请稍后重试",
                            Toast.LENGTH_SHORT).show();
                    editor.putBoolean("needCopyMapRes", true);
                    editor.commit();
                    finish();
                    break;
                case 1:
                    Toast.makeText(HomePageActivity.this, "地图资源初始化成功",
                            Toast.LENGTH_SHORT).show();

                    editor.putBoolean("needCopyMapRes", false);
                    editor.commit();
                    break;

                default:
                    break;
            }
        }

    }

    /**
     * 复制 Assets文件夹下文件到SDK，并解压
     *
     * @param savePath       复制和解压文件路径
     * @param assetsFileName assets 文件名字
     * @return 是否复制和解压成功
     */
    public boolean copyMapResource(String savePath, String assetsFileName) {
        // 确保路径存在
        File saveRootPath = new File(savePath);
        if (!saveRootPath.exists()) {
            saveRootPath.mkdirs();
        }

        File targetFile = new File(savePath + "/" + assetsFileName);
        if (targetFile.exists()) {
            targetFile.delete();
        }

        File dir = new File(savePath + "/" + assetsFileName);

        try {

            InputStream is = this.getResources().getAssets()
                    .open(assetsFileName);
            FileOutputStream fos = new FileOutputStream(dir);
            byte[] buffer = new byte[8 * 1024];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        boolean flag = unZip(savePath + "/" + assetsFileName, savePath + "/");
        targetFile.delete();
        return flag;
    }


    // 解压缩
    public static boolean unZip(String archive, String decompressDir) {
        try {
            BufferedInputStream bufferedInputStream;
            ZipFile zf = new ZipFile(archive, "GBK");
            Enumeration<?> e = zf.getEntries();
            while (e.hasMoreElements()) {
                ZipEntry ze2 = (ZipEntry) e.nextElement();
                String entryName = ze2.getName();
                String path = decompressDir + "/" + entryName;
                if (ze2.isDirectory()) {
                    File decompressDirFile = new File(path);
                    if (!decompressDirFile.exists()) {
                        decompressDirFile.mkdirs();
                    }
                } else {
                    String fileDir = path.substring(0, path.lastIndexOf("/"));
                    File fileDirFile = new File(fileDir);
                    if (!fileDirFile.exists()) {
                        fileDirFile.mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(decompressDir + "/"
                                    + entryName));
                    bufferedInputStream = new BufferedInputStream(
                            zf.getInputStream(ze2));
                    byte[] readContent = new byte[8 * 1024];
                    int readCount = bufferedInputStream.read(readContent);
                    while (readCount != -1) {
                        bos.write(readContent, 0, readCount);
                        readCount = bufferedInputStream.read(readContent);
                    }
                    bos.flush();
                    bos.close();
                }
            }
            zf.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // 关闭工作区间，释放工作区间
        if (PieMapApiApplication.mWorkspace != null) {
            PieMapApiApplication.mWorkspace.close();
            PieMapApiApplication.mWorkspace = null;
        }
        Log.i("zrc", "onDestroy");
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_1:
                Intent intent = new Intent(this, BasicMapActivity.class);
                startActivity(intent);
                break;
            case R.id.but_2:
                Intent intent2 = new Intent(this, RotateAndZoomActivity.class);
                startActivity(intent2);
                break;
            case R.id.but_3:
                Toast.makeText(this, "第三个", Toast.LENGTH_SHORT).show();
                break;
            case R.id.but_4:
                Toast.makeText(this, "第四个", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
