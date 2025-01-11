package com.girlkun.models.boss.list_boss.ginyu;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;
import java.util.Random;


public class sukientrungthu extends Boss {

    public sukientrungthu() throws Exception {
        super(BossID.sukientrungthu, BossesData.THODAIKA, BossesData.BULLMA);
    }

    @Override
    public void moveTo(int x, int y) {
        if (this.currentLevel == 1) {
            return;
        }
        super.moveTo(x, y);
    }

    @Override
    public void reward(Player plKill) {
        
        if (Util.isTrue(10, 100)) {
            ItemMap ngocrong3s = new ItemMap(this.zone, 16, 1, this.location.x - 20, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            ngocrong3s.options.add(new Item.ItemOption(30, 1));
            ngocrong3s.options.add(new Item.ItemOption(86, 1));
            Service.getInstance().dropItemMap(this.zone, ngocrong3s);
        }if (Util.isTrue(5, 100)) {
            ItemMap ngocrong2s = new ItemMap(this.zone, 15, 1, this.location.x - 40, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            ngocrong2s.options.add(new Item.ItemOption(30, 1));
            ngocrong2s.options.add(new Item.ItemOption(86, 1));
            Service.getInstance().dropItemMap(this.zone, ngocrong2s);
        }if (Util.isTrue(1, 100)) {
            ItemMap ngocrong1s = new ItemMap(this.zone, 14, 1, this.location.x - 80, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            ngocrong1s.options.add(new Item.ItemOption(30, 1));
            ngocrong1s.options.add(new Item.ItemOption(86, 1));
            Service.getInstance().dropItemMap(this.zone, ngocrong1s);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang = new ItemMap(this.zone, 190, 30000, this.location.x + 20, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang2 = new ItemMap(this.zone, 190, 30000, this.location.x + 40, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang2);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang3 = new ItemMap(this.zone, 190, 30000, this.location.x + 60, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang3);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang4 = new ItemMap(this.zone, 190, 30000, this.location.x + 80, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang4);
        }
        if (Util.isTrue(100, 100)) {
            ItemMap vang5 = new ItemMap(this.zone, 886, 1, this.location.x + 90, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang5);
        }
        if (Util.isTrue(100, 100)) {
            ItemMap vang6 = new ItemMap(this.zone, 886, 1, this.location.x + 70, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang6);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang7 = new ItemMap(this.zone, 886, 1, this.location.x + 70, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang7);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang8 = new ItemMap(this.zone, 887, 1, this.location.x +60, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang8);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang9 = new ItemMap(this.zone, 887, 1, this.location.x + 64, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang9);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang10 = new ItemMap(this.zone, 889, 1, this.location.x + 68, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang10);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang11 = new ItemMap(this.zone, 889, 1, this.location.x + 74, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang11);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang12 = new ItemMap(this.zone, 888, 1, this.location.x + 76, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang12);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang13 = new ItemMap(this.zone, 888, 1, this.location.x + 86, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang13);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang14 = new ItemMap(this.zone, 889, 1, this.location.x + 89, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang14);
        }
if (Util.isTrue(100, 100)) {
            ItemMap vang15 = new ItemMap(this.zone, 886, 1, this.location.x + 87, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang15);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang16 = new ItemMap(this.zone, 888, 1, this.location.x + 81, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang16);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang17 = new ItemMap(this.zone, 889, 1, this.location.x + 84, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang17);
        }if (Util.isTrue(100, 100)) {
            ItemMap vang18 = new ItemMap(this.zone, 887, 1, this.location.x + 83, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang18);
        }
        if (Util.isTrue(30, 100)) {
            ItemMap vang19 = new ItemMap(this.zone, 890, 1, this.location.x + 93, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang19);
        }
        if (Util.isTrue(20, 100)) {
            ItemMap vang20 = new ItemMap(this.zone, 891, 1, this.location.x + 95, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang20);
        }




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
    protected void notifyJoinMap() {
        if (this.currentLevel == 1) {
            return;
        }
        super.notifyJoinMap();
    }
    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 300000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - GirlBeo
 */
