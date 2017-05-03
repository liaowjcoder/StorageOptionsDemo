package storage.zeal.com.storageoptionsdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testExternalStoragePrivateDir();

    }

    private void testInnerStorage() {
        //写
        try {
            //打开（没有就创建）文件，返回一个输出流
            //写完数据之后记得关闭输出流
            FileOutputStream outputStream = openFileOutput("hello.txt", MODE_PRIVATE);
            outputStream.write("I love java".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //读
        try {
            FileInputStream inputStream = openFileInput("zeal");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String s = br.readLine();
            Log.e(TAG, "結果：" + s);
            br.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //返回一个内部文件系统的绝对路径，这个路径就是存储 通过 openFileOut 的目录
        File filesDir = getFilesDir();
        // /data/user/0/storage.zeal.com.storageoptionsdemo/files
        Log.e(TAG, filesDir.getAbsolutePath());

        File dir = getDir("zeal", MODE_PRIVATE);
        //  Caused by: java.lang.SecurityException: MODE_WORLD_READABLE no longer supported
        // 在 Android N 之后就不支持 MODE_WORLD_READABLE/WRITEABLE
        File dir2 = getDir("zeal2", MODE_PRIVATE);
        //注意：该名字系统会在其前面加上 app_
        // /data/user/0/storage.zeal.com.storageoptionsdemo/app_zeal
        Log.e(TAG, dir.getAbsolutePath());
        // /data/user/0/storage.zeal.com.storageoptionsdemo/app_zeal2
        Log.e(TAG, dir2.getAbsolutePath());


        //getDir 和 getFileDir 的区别是什么？
        //getDir 是需要指定文件目录的名字和该目录的类型，若是不存在这个目录，系统则会帮你创建
        //文件目录名生成：app_(name)
        //路径：就是内部存储空间下/该文件夹名称
        //作用：可以在内存存储空间的根目录下创建新的文件夹

        //getFileDir
        //返回的内部存储空间下的 files 目录
        //路径：内部存储空间/files
        //通过 openFileOut 的写入的数据保存的目录就是在该目录下

        //访问内部存储空间是不需要任何读取权限的。

        //列出 files 文件夹下有多少个文件目录
        String[] files = fileList();
        for (int i = 0; i < files.length; i++) {
            Log.e(TAG, files[i]);
        }

        //databaseList()
    }

    /**
     * 删除 files 下的文件，返回值为是否删除成功
     */
    private void deleteInnerFile() {
        Log.e(TAG, deleteFile("hello") + "");//false
        Log.e(TAG, deleteFile("hello.txt") + "");//true

    }


    /**
     * sp 练习
     */
    private void testSp() {
        //这个方法是 activity 的方法
        //sp 默认就是当前 activity 的名字，因此这个 sp 只是服务于当前 Activity
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        sp.edit().putString("key", "zeal").commit();

        String key = sp.getString("key", "");
        Log.e(TAG, "VALUE:" + key);

        //这种方式获取的 sharedPreferences 是整个 Application 公用的。
        SharedPreferences sharedPreferences = getSharedPreferences("zeal", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("key", "HELLO").commit();

        String value = sharedPreferences.getString("key", "");
        Log.e(TAG, "VALUE:" + value);
    }


    /**
     * 外部存储缓存目录
     */
    private void cacehDir() {
        // /storage/emulated/0/Android/data/storage.zeal.com.storageoptionsdemo/cache
        File[] externalCacheDirs = getExternalCacheDirs();
        // /storage/emulated/0/Android/data/storage.zeal.com.storageoptionsdemo/cache
        File externalFilesDir = getExternalCacheDir();
        Log.e(TAG, externalFilesDir.getAbsolutePath());
        for (int i = 0; i < externalCacheDirs.length; i++) {
            Log.e(TAG, externalCacheDirs[i].getAbsolutePath());
        }
    }

    /**
     * 外部私有存储目录
     */
    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void testExternalStoragePrivateDir() {
        //在 Android 4.4 API18 之后访问外部存储私有目录是不需要权限的
        //不过还是需要在清单文件中配置，保证兼容低版本。
        // /storage/sdcard/Android/data/storage.zeal.com.storageoptionsdemo/files/Pictures
        //系统媒体不会去扫描这个文件夹，也就是说他们不会被 MediaStore 内容提供者访问。
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //4.4 之后的方法
        // /storage/emulated/0/Android/data/storage.zeal.com.storageoptionsdemo/files
        File[] externalFilesDirs = getExternalFilesDirs(null);
        //Log.e(TAG, "FILE dir：" + externalFilesDir);
        for (int i = 0; i < externalFilesDirs.length; i++) {
            Log.e(TAG, externalFilesDirs[i].getAbsolutePath());
        }
        Log.e(TAG, "----------------------");

        // /storage/emulated/0/Android/data/storage.zeal.com.storageoptionsdemo/files/Pictures
        File[] externalDirs = ContextCompat.getExternalFilesDirs(this, Environment.DIRECTORY_PICTURES);
        for (int i = 0; i < externalDirs.length; i++) {
            Log.e(TAG, externalDirs[i].getAbsolutePath());
        }

        //Android 4.3 的手机访问的结果
        // /storage/sdcard/Android/data/storage.zeal.com.storageoptionsdemo/files/Pictures



        //Android 模拟器访问的结果
        // /storage/emulated/0/Android/data/storage.zeal.com.storageoptionsdemo/files
        // /storage/0EEB-1C0B/Android/data/storage.zeal.com.storageoptionsdemo/files
        //这可以看到数组有两条记录，官方表示第一条记录是主要的外部存储目录
    }


    /**
     * 公共的共享目录 例如图片，音乐，视频
     */
    public void saveFileSharedByOtherApp() throws IOException {
        if (!isExternalStorageWritable()) {
            Log.e(TAG, "sd 卡不可用");
            return;
        }
        /**
         * DIRECTORY_MUSIC
         * DIRECTORY_RINGTONES
         * DIRECTORY_NOTIFICATIONS
         * DIRECTORY_MOVIES
         * DIRECTORY_DCIM
         */
        // DIRECTORY_PICTURES  /storage/sdcard/Pictures
        // DIRECTORY_MUSIC      /storage/sdcard/Music
        // DIRECTORY_DCIM      /storage/sdcard/DCIM
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.e(TAG, "picDir:" + picturesDir.getAbsolutePath());

        //在公共目录的基础上新建自己的文件夹
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "appappapp");

        if (!file.exists()) {
            file.mkdir();
        }

        Log.e(TAG, "picturesDir.exists():" + file.exists());

        File textFile = new File(file, "aa.png");
        textFile.createNewFile();

        Log.e(TAG, "file:" + textFile.getAbsolutePath());
        Log.e(TAG, "file exists:" + textFile.exists());
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
