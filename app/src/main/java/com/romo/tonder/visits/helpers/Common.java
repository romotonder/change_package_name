package com.romo.tonder.visits.helpers;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Common {
    //Demo
//    public static String BASE_URL = "https://www.app.romo-tonder.dk/wp-json/wiloke/v2/";
//    public static final String BASE_URL_NEARBY = "https://www.app.romo-tonder.dk/wp-json/wiloke/v2/near-by";
//    live
    public static final String BASE_URL = "https://www.romo-tonder.dk/wp-json/wiloke/v2/";
    public static final String BASE_URL_NEARBY = "https://www.romo-tonder.dk/wp-json/wiloke/v2/near-by";
    public static final boolean isLogedIn = true;
    public static final boolean isRegistrationSetup = true;
    public static final boolean isRegistrer = true;
    public static int ZERO = 0;
    public static int ONE = 1;
    public static int TWO = 2;
    public static String TYPE_HEADING_ONE = "Heading Section";
    public static String TYPE_HEADING_TWO = "Heading Section 2";
    public static String TYPE_HEADING_THREE = "Heading Section 3";
    public static String TYPE_HEADING_FOUR = "Heading Section 4";
    public static String FROM_EVENT_PAGE = "event_page";
    public static String FROM_LIST_PAGE = "list_page";
    public static String clike = "";
    public static String descriptionTitle = "";
    public static String descriptionBody = "";
    public static String GallaryBody = "";
    public static ListingDetailsData currentListingDetails;
    public static int LISTING_DISCUSION = 1;
    public static int EVENT_DISCUSION = 2;
    public static boolean isNowOpen;
    public static final int HOME_LISTING = 1;
    public static final int CATEGORY_LISTING = 3;
    public static int LISTING_PAGE = 2;
    public static final int ListingDetailsPageViewallImages = 4;
    public static int LIST_VIDEO = 10;

    public static final int LANG_DANISH = 1;
    public static final int LANG_ENGLISH = 2;
    public static final int LANG_GERMAN = 3;

    public static int maxwidth = 1024;
    public static int maxheight = 1024;
    public static int quality = 80;
    public static String ImgCompressedFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VisitRomoTonder/UploadImageFiles/";


    public static final String FILTER_TYPE_CATEGORY = "categoryFilter";
    public static final String FILTER_TYPE_REGION = "regionFilter";
    public static final String GLOBAL_SEARCH_TYPE = "globalSearchType";
    public static final String SEARCH_TYPE_LISTING = "searchTypeListing";
    public static final String SEARCH_TYPE_EVENT = "searchTypeEvent";
    public static final String FILTER_TYPE_BOTTOM_SHEET = "filterBottomSheet";

    //intent
    public static final String FROM_HOME_ACTIVITY = "from_home";
    public static final String FROM_LISTING_ACTIVITY = "from_listing";
    public static final String FROM_EVENT_ACTIVITY = "from_event";
    public static final String FROM_USER_ACTIVITY = "from_user";

    //comments page
    public static final String LISTING = "listing";
    public static final String EVENT = "event";

    //chat page
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    //co-locator key
    public static final String CO_LOCATOR_KEY = "us4hvilu";
    public static final String CO_LOCATOR_KEY_TEST = "us4hvilu";

    //Notifiaction
    public static final String NOTIFIED = "notified";
    public static final String NOTIFIED_PREFERENCE = "notified_preference";
    public static final String NOTIFICATION_LIST_TYPE = "notification_list_type";
    public static final String NOTIFICATION_TYPE_LIST = "List";
    public static final String NOTIFICATION_TYPE_BELL_ICON = "Bell Icon";


    public static String GetCurrentDateTime() {
//        20-12-19 23:10:24
//        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calobj = Calendar.getInstance();
        String dateTime = df.format(calobj.getTime());
        System.out.println(df.format(calobj.getTime()));
        return dateTime;

    }

    public static long GetTime() {
        long times = 0;
        Timestamp timestamp;
        timestamp = new Timestamp(System.currentTimeMillis());
        times = timestamp.getTime();
        return times;
    }

    public static String dayOfWeek(int day) {
        String today = "";
        switch (day) {
            case Calendar.SUNDAY:
                // Current day is Sunday
                today = "SUNDAY";
                break;
            case Calendar.MONDAY:
                // Current day is Monday
                today = "MONDAY";
                break;
            case Calendar.TUESDAY:
                // etc.
                today = "TUESDAY";
                break;
            case Calendar.WEDNESDAY:
                // etc.
                today = "WEDNESDAY";
                break;
            case Calendar.THURSDAY:
                // etc.
                today = "THURSDAY";
                break;
            case Calendar.FRIDAY:
                // etc.
                today = "FRIDAY";
                break;
            case Calendar.SATURDAY:
                // etc.
                today = "SATURDAY";
                break;
        }
        return today;

    }

    public static boolean compareTime(String minTime, String maxTime) {
        Date dateMin = new Date();
        Date dateMax = new Date();
        Date currentTime = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            dateMin = sdf.parse(minTime);
            //dateMin = sdf.parse("00:00:00");
            System.out.print(dateMin);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            dateMax = sdf.parse(maxTime);
            //dateMax = sdf.parse("00:00:00");
            System.out.print(dateMax);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //DateFormat ct = new SimpleDateFormat("HH:mm");
        Calendar calobj = Calendar.getInstance();
        String dateTime = sdf.format(calobj.getTime());
        try {
            currentTime = sdf.parse(dateTime);
            System.out.print(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference_open = 0;
        long difference_close = 0;
        //difference = Math.abs(dateMin.getTime() - currentTime.getTime());
        difference_open = dateMin.getTime() - currentTime.getTime();
        long diffHours_open = difference_open / (60 * 60 * 1000) % 24;
        long diffDays_open = difference_open / (24 * 60 * 60 * 1000);
        long diffMinutes_open = difference_open / (60 * 1000) % 60;
        System.out.print(diffMinutes_open);

        difference_close = currentTime.getTime() - dateMax.getTime();
        long diffHours_close = difference_close / (60 * 60 * 1000) % 24;
        long diffDays_close = difference_close / (24 * 60 * 60 * 1000);
        long diffMinutes_close = difference_close / (60 * 1000) % 60;
        System.out.print(diffMinutes_close);

        if (diffHours_open <= 0 && diffHours_close <= 0) {
            if (diffMinutes_open <= 0) {
                if (diffMinutes_close > 0) {
                    //close
                    return false;
                } else {
                    //open
                    return true;
                }
            } else {
                if (diffMinutes_close <= 0) {
                    //close
                    return false;
                } else {
                    return true;
                }
            }
        } else {
            return false;
        }

    }

    public static String getRealPathFromUri(Uri uri, Activity activity) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = activity.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getTime(String dateTime) {
        String time = "";
        String[] parts = dateTime.split(" ");
        time = parts[1]; // 034556

        return time;
    }

    public static int GetInt(Object value) {
        int retVal = 0;

        if (value != null) {
            retVal = Integer.parseInt(value.toString());
        }

        return retVal;
    }

    public static long Getlong(Object value) {
        long retVal = 0;

        if (value != null) {
            retVal = Long.parseLong(value.toString());
        }

        return retVal;
    }

    public static String getDate(long timeStamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

}
