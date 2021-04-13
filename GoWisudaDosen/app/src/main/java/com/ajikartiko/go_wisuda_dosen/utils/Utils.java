package com.ajikartiko.go_wisuda_dosen.utils;

import com.ajikartiko.go_wisuda_dosen.model.Dosen;
import com.ajikartiko.go_wisuda_dosen.model.Mahasiswa;
import com.ajikartiko.go_wisuda_dosen.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class Utils {
    public static String humanReadableByteCountBin(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static String dateToString(Date date, String pattern){
        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return df.format(date);
    }

    public static Date stringToDate(String date, String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User documentToUser(DocumentSnapshot snapshot){
        User user = (snapshot.get("type").equals(UserType.MAHASISWA.name())) ? snapshot.toObject(Mahasiswa.class): snapshot.toObject(Dosen.class);
        user.setUserId(snapshot.getId());
        return user;
    }



    public static Map<String, Object> modelToMap(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
//        Map<String, Object> hashMap = new HashMap<>();
//        try {
//            Class<?> c = obj.getClass();
//            Method[] m = c.getMethods();
//            for (Method method : m) {
//                if (method.getName().indexOf("get") == 0) {
//                    Log.e("modelToMap", "name: "+(method.getName()) );
//                    String name = method.getName().toLowerCase().substring(3, 4) + method.getName().substring(4);
//
//                    hashMap.put(name, method.invoke(obj));
//                }
//            }
//        } catch (Throwable e) {
//            Log.e("modelToMap", "error: ",e );
//        }
//        return hashMap;


    }
}
