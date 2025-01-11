package com.girlkun.models.boss.list_boss.Broly;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.boss.list_boss.cell.SieuBoHung;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Broly extends Boss {
    
    private long lastTimeHP;
    private int timeHP;
    private boolean calledNinja;

    public Broly() throws Exception {
        super(BossID.BROLY, BossesData.BROLY_1);
    }
    
    @Override
public void active() {
    super.active();
    if(Util.canDoWithTime(st,300000)){
        this.changeStatus(BossStatus.LEAVE_MAP);
    }else if(this.nPoint.hp >= 500000){
        this.setDie(null);
        this.die(null);
    }
    try {
//            this.HoiPhuc();
        } catch (Exception ex) {
            Logger.getLogger(SieuBoHung.class.getName()).log(Level.SEVERE, null, ex);
        }
}

//private void HoiPhuc() throws Exception {
//        if (!Util.canDoWithTime(this.lastTimeHP, this.timeHP) || !Util.isTrue(1, 100)) {
//            return;
//        }
//
//        Player pl = this.zone.getRandomPlayerInMap();
//        if (pl == null || pl.isDie()) {
//            return;
//        }
//        this.nPoint.dameg += (pl.nPoint.dame * 2 / 100);
//        this.nPoint.hpg += (this.nPoint.hpg * 5 / 100);
//        this.nPoint.critg++;
//        this.nPoint.calPoint();
//        PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, 0);
//        Service.gI().sendThongBao(pl, "Tên broly hắn lại tăng sức mạnh rồi!");
//        this.chat(2, "Mọi người cẩn thận sức mạnh hắn ta tăng đột biến..");
//        this.chat("Graaaaaa...");
//        this.lastTimeHP = System.currentTimeMillis();
//        this.timeHP = Util.nextInt(15000, 20000);
//    }
//    
    @Override
    public void joinMap() {
        super.joinMap();
        st= System.currentTimeMillis();
    }
    private long st;
    
    @Override
    public void moveToPlayer(Player player) {
        this.moveTo(player.location.x, player.location.y);
    }
 @Override
    public void reward(Player plKill) {
        int[] itemDos = new int[]{16,17};
        int[] NRs = new int[]{861, 2030};
        int randomDo = new Random().nextInt(itemDos.length);
        int randomNR = new Random().nextInt(NRs.length);
        if (Util.isTrue(15, 100)) {
            if (Util.isTrue(1, 5)) {
                Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 16, 1, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 100, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }   
     @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage/2);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage/1;
            }
            this.nPoint.subHP(damage);
            if (this.nPoint.hp >= 49999999 && !this.calledNinja) {
                try {
                    new BrolySuper(this.zone, 2, Util.nextInt(1000, 10000), BossID.BROLY_SUPER);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.calledNinja = true;
            }
            if (isDie()) {
this.setDie(plAtt);
die(plAtt);
}
            return damage;
        } else {
            return 0;
        }
    }
}
