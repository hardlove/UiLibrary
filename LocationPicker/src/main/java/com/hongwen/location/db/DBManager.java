package com.hongwen.location.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.hongwen.location.db.room.AppRoomDatabase;
import com.hongwen.location.model.Location;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author Bro0cL on 2016/1/26.
 */
public class DBManager {
    private static final int BUFFER_SIZE = 1024;

    private final String DB_PATH;
    private final Context mContext;
    private static final String DB_NAME = "location_picker_v1.db";

    public DBManager(Context context) {
        this.mContext = context;
        DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + "databases" + File.separator;
        copyDBFile();
    }

    private void copyDBFile() {
        File dir = new File(DB_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //拷贝数据库
        File dbFile = new File(DB_PATH + DB_NAME);
        if (!dbFile.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getResources().getAssets().open(DB_NAME);
                os = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("Range")
    public List<Location> getAllCities() {
        List<Location> result = AppRoomDatabase.getInstance(mContext).locationDao().queryAll();
        Collections.sort(result, new LocationComparator());
        return result;
    }

    @SuppressLint("Range")
    public List<Location> searchLocation(final String keyword) {
        List<Location> result = AppRoomDatabase.getInstance(mContext).locationDao().search(keyword);
        LocationComparator comparator = new LocationComparator();
        Collections.sort(result, comparator);
        return result;
    }

    /**
     * sort by a-z
     */
    private static class LocationComparator implements Comparator<Location> {
        @Override
        public int compare(Location lhs, Location rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }
}
