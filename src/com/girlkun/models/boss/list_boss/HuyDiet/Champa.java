package com.girlkun.models.boss.list_boss.HuyDiet;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;


public class Champa extends Boss {
    private long lasttimehakai;
    private int timehakai;

    public Champa() throws Exception {
        super(Util.randomBossId(), BossesData.THAN_HUY_DIET_CHAMPA);
    }

       @Override
    public void reward(Player plKill) {
        int[] itemDos = new int[]{650,651,652,653};
        int[] itemtime = new int[]{15};
        int randomDo = new Random().nextInt(itemDos.length);
        int randomitem = new Random().nextInt(itemtime.length);
        if (Util.isTrue(20, 100)) {
            if (Util.isTrue(1, 5)) {
                Service.gI().dropItemMap(this.zone, Util.ratiDHD(zone, 656, 1, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.ratiDHD(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, itemtime[randomitem], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
        plKill.pointPvp += 1;
    }


    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if(Util.canDoWithTime(st,900000)){
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
//        @Override
//    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
//        if (plAtt != null) {
//            switch (plAtt.playerSkill.skillSelect.template.id) {
//                case Skill.KAMEJOKO:
//                case Skill.MASENKO:
//                case Skill.ANTOMIC:
//                    int hpHoi = (int) ((long) damage * 80 / 100);
//                    PlayerService.gI().hoiPhuc(this, hpHoi, 0);
//                    if (Util.isTrue(1, 5)) {
//                        this.chat("Hahaha,Các ngươi nghĩ sao vậy?");
//                    }
//                    return 0;
//            }
//        }
//        return super.injured(plAtt, damage, piercing, isMobAttack);
//    }
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
                damage = damage/2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;

}
