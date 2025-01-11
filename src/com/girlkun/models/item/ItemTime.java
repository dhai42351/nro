package com.girlkun.models.item;

import com.girlkun.models.player.NPoint;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import com.girlkun.services.ItemTimeService;


public class ItemTime {

    //id item text
    public static final byte DOANH_TRAI = 0;
    public static final byte BAN_DO_KHO_BAU = 1;
    public static final byte KHI_GAS = 2;

    public static final int TIME_ITEM = 600000;
    public static final int TIME_BI_NGO = 1800000;
    public static final int TIME_OPEN_POWER = 86400000;
    public static final int TIME_MAY_DO = 1800000;
    public static final int TIME_MAY_DO2 = 1800000;
    public static final int TIME_EAT_MEAL = 600000;
     public static final int TIME_ITEM45P = 2700000;
     public static final int TIME_ITEM60P = 3600000;
     public static final int TIME_ITEM90P = 5400000;

    private Player player;

    public boolean isUseBoHuyet;
    public boolean isUseBoKhi;
    public boolean isUseGiapXen;
    public boolean isUseCuongNo;
    public boolean isUseAnDanh;
    public boolean isUseBoHuyet2;
    public boolean isUseBoKhi2;
    public boolean isUseGiapXen2;
    public boolean isUseCuongNo2;
    public boolean isUseAnDanh2;
    public boolean isUseBanhSau;
    public boolean isUseBanhNhen;
    public boolean isUseSupBi;
    public boolean isUseKeoMotMat;
    public boolean isUseBanhTet;
    public boolean isUseBanhChung;
    public boolean isUseThuocmo;
    public boolean isUseThuocmo2;
    public boolean isUseTrungthu;
    public boolean isUseHoptrungthu;
    
    
    public long lastTimeBoHuyet;
    public long lastTimeBoKhi;
    public long lastTimeGiapXen;
    public long lastTimeCuongNo;
    public long lastTimeAnDanh;
     public long lastTimeBanhSau;
    public long lastTimeBanhNhen;
    public long lastTimeSupBi;
    public long lastTimeKeoMotMat;
    public long lastTimeBanhTet;
    public long lastTimeBanhChung;
    public long lastTimeThuocmo;
    public long lastTimeThuocmo2;

    public long lastTimeBoHuyet2;
    public long lastTimeBoKhi2;
    public long lastTimeGiapXen2;
    public long lastTimeCuongNo2;
    public long lastTimeAnDanh2;
    public long lastTimeTrungthu;
    public long lastTimeHoptrungthu;
    
    
    public boolean isUseMayDo;
    public long lastTimeUseMayDo;//lastime de chung 1 cai neu time = nhau
    public boolean isUseMayDo2;
    public long lastTimeUseMayDo2;
    
     public boolean isBiNgo;
    public long lastTimeBiNgo;
    public boolean isOpenPower;
    public long lastTimeOpenPower;

    public boolean isUseTDLT;
    public long lastTimeUseTDLT;
    public int timeTDLT;

    public boolean isEatMeal;
    public long lastTimeEatMeal;
    public int iconMeal;
    public boolean istrbsd;
    public boolean istrbhp;
    public boolean istrbki;
    public static final int TIME_TRB = 1800000;
    
    public long lastTimetrbsd;
    public long lastTimetrbhp;
    public long lastTimetrbki;

    public ItemTime(Player player) {
        this.player = player;
    }

    public void update() {
        if (isEatMeal) {
            if (Util.canDoWithTime(lastTimeEatMeal, TIME_EAT_MEAL)) {
                isEatMeal = false;
                Service.gI().point(player);
            }
        }
        if (isUseBoHuyet) {
            if (Util.canDoWithTime(lastTimeBoHuyet, TIME_ITEM)) {
                isUseBoHuyet = false;
                Service.gI().point(player);
            }
        }
        
        if (isBiNgo) {
            if (Util.canDoWithTime(lastTimeBiNgo, TIME_BI_NGO)) {
                isBiNgo = false;
            }
        }
        if (isUseBoKhi) {
            if (Util.canDoWithTime(lastTimeBoKhi, TIME_ITEM)) {
                isUseBoKhi = false;
                Service.gI().point(player);
            }
        }
       
        if (isUseGiapXen) {
            if (Util.canDoWithTime(lastTimeGiapXen, TIME_ITEM)) {
                isUseGiapXen = false;
            }
        }
        if (isUseCuongNo) {
            if (Util.canDoWithTime(lastTimeCuongNo, TIME_ITEM)) {
                isUseCuongNo = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh) {
            if (Util.canDoWithTime(lastTimeAnDanh, TIME_ITEM)) {
                isUseAnDanh = false;
            }
        }
       
        if (isUseBoHuyet2) {
            if (Util.canDoWithTime(lastTimeBoHuyet2, TIME_ITEM)) {
                isUseBoHuyet2 = false;
                Service.gI().point(player);
            }
        }
        
        if (isUseBoKhi2) {
            if (Util.canDoWithTime(lastTimeBoKhi2, TIME_ITEM)) {
                isUseBoKhi2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseGiapXen2) {
            if (Util.canDoWithTime(lastTimeGiapXen2, TIME_ITEM)) {
                isUseGiapXen2 = false;
            }
        }
        if (isUseCuongNo2) {
            if (Util.canDoWithTime(lastTimeCuongNo2, TIME_ITEM)) {
                isUseCuongNo2 = false;
                Service.gI().point(player);
            }
        }
        if (isUseAnDanh2) {
            if (Util.canDoWithTime(lastTimeAnDanh2, TIME_ITEM)) {
                isUseAnDanh2 = false;
            }
        }
        if(isUseBanhTet){
            if (Util.canDoWithTime(lastTimeBanhTet, TIME_ITEM45P)) {
                isUseBanhTet = false;
            }
        }
        if(isUseBanhChung){
            if (Util.canDoWithTime(lastTimeBanhChung, TIME_ITEM45P)) {
                isUseBanhChung = false;
            }
        }
         if (isUseBanhNhen) {
            if (Util.canDoWithTime(lastTimeBanhNhen, TIME_MAY_DO)) {
                isUseBanhNhen = false;
                Service.getInstance().point(player);
            }
        }
       
        if (isUseBanhSau) {
            if (Util.canDoWithTime(lastTimeBanhSau, TIME_MAY_DO)) {
                isUseBanhSau= false;
            }
        }
        if (isUseSupBi) {
            if (Util.canDoWithTime(lastTimeSupBi, TIME_MAY_DO)) {
                isUseSupBi = false;
                Service.getInstance().point(player);
            }
        }
       
        if (isUseKeoMotMat) {
            if (Util.canDoWithTime(lastTimeKeoMotMat, TIME_MAY_DO)) {
                isUseKeoMotMat= false;
            }
        }
        if (isUseThuocmo) {
            if (Util.canDoWithTime(lastTimeThuocmo, TIME_ITEM)) {
                isUseThuocmo= false;
            }
        }
        if (isUseThuocmo2) {
            if (Util.canDoWithTime(lastTimeThuocmo2, TIME_MAY_DO)) {
                isUseThuocmo2= false;
            }
        }
        if (isUseTrungthu) {
            if (Util.canDoWithTime(lastTimeTrungthu, TIME_ITEM60P)) {
                isUseTrungthu= false;
            }
        }
        if (isUseHoptrungthu) {
            if (Util.canDoWithTime(lastTimeHoptrungthu, TIME_ITEM90P)) {
                isUseHoptrungthu= false;
            }
        }
        if (isOpenPower) {
            if (Util.canDoWithTime(lastTimeOpenPower, TIME_OPEN_POWER)) {
                player.nPoint.limitPower++;
                if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                    player.nPoint.limitPower = NPoint.MAX_LIMIT;
                }
                Service.gI().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
                isOpenPower = false;
            }
        }
        if (isUseMayDo) {
            if (Util.canDoWithTime(lastTimeUseMayDo, TIME_MAY_DO)) {
                isUseMayDo = false;
            }
        }
        if (isUseMayDo2) {
            if (Util.canDoWithTime(lastTimeUseMayDo2, TIME_MAY_DO2)) {
                isUseMayDo2 = false;
            }
        }
        if (isUseTDLT) {
            if (Util.canDoWithTime(lastTimeUseTDLT, timeTDLT)) {
                this.isUseTDLT = false;
                ItemTimeService.gI().sendCanAutoPlay(this.player);
            }
        }
                if (istrbsd) {
            if (Util.canDoWithTime(lastTimeEatMeal, TIME_TRB)) {
                istrbsd = false;
                Service.gI().point(player);
            }
        }
         
        if (istrbhp) {
            if (Util.canDoWithTime(lastTimeBoHuyet, TIME_TRB)) {
                istrbhp = false;
                Service.gI().point(player);
            }
        }
        
        if (istrbki) {
            if (Util.canDoWithTime(lastTimeBoKhi, TIME_TRB)) {
                istrbki = false;
                Service.gI().point(player);
            }
        }
    }
    
    public void dispose(){
        this.player = null;
    }
}
