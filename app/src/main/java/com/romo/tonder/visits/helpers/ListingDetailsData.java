package com.romo.tonder.visits.helpers;

import com.romo.tonder.visits.models.GallaryModel;
import com.romo.tonder.visits.models.ServiceModel;

import java.util.ArrayList;

public class ListingDetailsData {
    public String listingId;
    public String listingUserId;
    public String listingPostTitle;
    public String listingTagLine;

    public String descriptionTitle;
    public String descriptionBody;
    public boolean isCardDesc;
    public String gallaryTitle;
    public ArrayList<GallaryModel> gallaryModelArrayList;
    public boolean isCardGallary;
    //map
    public  String mapTitle;
    public  String mapAddress;
    public  String mapLat;
    public  String mapLong;
    public  String googleMapUrl;
    public  String phone;
    public  String website;
    public  String email;
    public  boolean myFav;
    public boolean isCardInfo;
    //Opening hour;
    public  String day_id;
    public  String objectID;
    public  String dayOfWeek;
    public  String isOpen;
    public  String firstOpenHour;
    public  String firstCloseHour;
    public  String secondOpenHour;
    public  String firstCloseHourUTC;
    public  String secondOpenHourUTC;
    public  String secondCloseHourUTC;
    public  String openingHourButtonColor;
    public boolean isCardTiming;
    //product card
    public String productName;
    public String productCat;
    public String productCur;
    public String productPrice;
    public String productUrl;
    public String productImg;
    public String productTitle;
    public boolean isCardProduct;

    //avrg review card
    public String acTitile="";
    public String aclTitile="";
    public String aclValue="";
    public String acqTitile="";
    public String acqValue="";
    public String acgTitile="";
    public String acgValue="";
    public String acsTitile="";
    public String acsValue="";
    public boolean isCardAverageRating;
    //service
    public ArrayList<ServiceModel> serviceModelArrayList;
    public String serviceTitle;
    public String serviceID;
    public String serviceName;
    public String serviceIcon;
    public boolean isCardService;

    //termInfo
    public String termInfoTitle;
    public boolean isCardTermInfo;
    //statics
    public String statTitle;
    public String statTotalView;
    public String statTotalReview;
    public String statTotalFavourite;
    public boolean isCardStatDetails;
    //reviewRating
    public String reviewHeading;
    public boolean isCardReviewRating;
    public boolean isReviewRatingDetails;
    //user review details
    public String userReviewTitle;
    public String mode;
    public String quality;
    public String average;
    //button booknow
    public String booknowTitle;
    public String booknowLink;
    public boolean isbooknow;
    public String yourReviewTitle;
    public boolean isYourReview;
    //video section
    public String videoUrl;
    public String videoTitle;
    public String videoThumbnail;
    public boolean isVideoCard;
    //author
    public String authorID;
    public String authorDisplayName;





    public ListingDetailsData() {
        listingId="";
        listingUserId="";
        listingPostTitle="";
        listingTagLine="";
        descriptionTitle="";
        descriptionBody="";
        gallaryModelArrayList=new ArrayList<>();
        mapTitle="";
        mapAddress="";
        mapLat="";
        mapLong="";
        googleMapUrl="";
        phone="";
        website="";
        email="";
        day_id="";
        objectID="";
        dayOfWeek="";
        isOpen="";
        firstOpenHour="";
        firstCloseHour="";
        openingHourButtonColor="";
        productName="";
        productCat="";
        productCur="";
        productImg="";
        productPrice="";
        productUrl="";
        productTitle="";
        acTitile="";
        acqTitile="";
        acqValue="";
        aclTitile="";
        aclValue="";
        acgTitile="";
        acgValue="";
        acsTitile="";
        acsValue="";
        serviceModelArrayList=new ArrayList<>();
        serviceTitle="";
        serviceID="";
        serviceName="";
        serviceIcon="";
        termInfoTitle="";
        statTitle="";
        statTotalView="";
        statTotalReview="";
        statTotalFavourite="";
        reviewHeading="";
        userReviewTitle="";
        mode="";
        quality="";
        average="";
        booknowTitle="";
        booknowLink="";
        yourReviewTitle="";
        videoTitle="";
        videoThumbnail="";
        videoUrl="";

        isCardDesc=false;
        isCardGallary=false;
        isCardTiming=false;
        isCardAverageRating=false;
        isCardProduct=false;
        isCardProduct=false;
        isCardInfo=false;
        isCardService=false;
        isCardReviewRating=false;
        isReviewRatingDetails=false;
        isCardTermInfo=false;
        isCardStatDetails=false;

        isbooknow=false;
        isYourReview=false;
        myFav=false;
        isVideoCard=false;

        authorID="";
        authorDisplayName="";

    }
}
