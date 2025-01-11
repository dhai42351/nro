package com.girlkun.models.player;

import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.services.MapService;
import com.girlkun.consts.ConstMap;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.server.Manager;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Util;
// đây
import java.util.ArrayList;
import java.util.List;

/**
 * @author BTH sieu cap vippr0
 */
public class Referee1 extends Player {

    private long lastTimeChat;
    private Player playerTarget;

    private long lastTimeTargetPlayer;
    private long timeTargetPlayer = 5000;
    private long lastZoneSwitchTime;
    private long zoneSwitchInterval;
    private List<Zone> availableZones;

    public void initReferee1() {
        init();
    }

    @Override
    public short getHead() {
        return 1398;
    }

    @Override
    public short getBody() {
        return 1399;
    }

    @Override
    public short getLeg() {
        return 1400;
    }

    public void joinMap(Zone z, Player player) {
        MapService.gI().goToMap(player, z);
        z.load_Me_To_Another(player);
    }

    @Override
    public void update() {
        if (Util.canDoWithTime(lastTimeChat, 5000)) {
            Service.getInstance().chat(this, "Chào Các Bạn Đến Với Ngọc Rồng NIGHT ");
//            Service.getInstance().chat(this, "Danh Sách"
//            Service.getInstance().chat(this,TopService.getTopNap());
//                                    + "\n|3|[ Top 1 ]  "
//                                    + "\n|2|[ Top 2 ]  "                                   
//                                    + "\n|4|[ Top 3 ] ");
            Service.getInstance().chat(this, "Chúc Các Cư Dân Online Vui Vẻ ");
            lastTimeChat = System.currentTimeMillis();
        }
    }

    private void init() {
        int id = -1000000;
        for (Map m : Manager.MAPS) {
            if (m.mapId == 0) {
                for (Zone z : m.zones) {
                    Referee1 pl = new Referee1();
                    pl.name = "Ngọc Rồng NIGHT";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 69;
                    pl.nPoint.hpg = 69;
                    pl.nPoint.hp = 69;
                    pl.nPoint.setFullHpMp();
                    pl.location.x = 714;
                    pl.location.y = 432;
                    joinMap(z, pl);
                    z.setReferee(pl);
                }
            } else if (m.mapId == 7) {                      
                    for (Zone z : m.zones) {
                    Referee1 pl = new Referee1();
                    pl.name = "Ngọc Rồng NIGHT";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 69;
                    pl.nPoint.hpg = 69;
                    pl.nPoint.hp = 69;
                    pl.nPoint.setFullHpMp();
                    pl.location.x = 761;
                    pl.location.y = 432;
                    joinMap(z, pl);
                    z.setReferee(pl);
                 }
              } else if (m.mapId == 14) {                      
                    for (Zone z : m.zones) {
                    Referee1 pl = new Referee1();
                    pl.name = "Ngọc Rồng NIGHT";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 69;
                    pl.nPoint.hpg = 69;
                    pl.nPoint.hp = 69;
                    pl.nPoint.setFullHpMp();
                    pl.location.x = 752;
                    pl.location.y = 408;
                    joinMap(z, pl);
                    z.setReferee(pl);
                 }
            }
        }
    }
}

