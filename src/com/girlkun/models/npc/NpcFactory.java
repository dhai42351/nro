package com.girlkun.models.npc;

import com.barcoll.MaQuaTang.MaQuaTangManager;
import com.girlkun.consts.ConstMap;
import com.girlkun.services.*;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.database.GirlkunDB;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.kygui.ShopKyGuiService;
import com.girlkun.models.ThanhTich.ThanhTichPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.list_boss.NhanBan;
import com.girlkun.models.boss.list_boss.DuongTank;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.clan.ClanMember;

import java.util.HashMap;
import java.util.List;

import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.SummonDragon;

import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static com.girlkun.services.func.SummonDragon.SHENRON_SAY;
import static com.girlkun.services.func.GoiRongXuong.HALLOWEN_SAY;
import static com.girlkun.services.func.GoiRongXuong.HALLOWEN_1_STAR_WISHES_1;

import com.girlkun.models.player.Player;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.models.map.MapMaBu.MapMaBu;
import com.girlkun.models.map.challenge.MartialCongressService;
import com.girlkun.models.map.doanhtrai.DoanhTrai;
import com.girlkun.models.map.doanhtrai.DoanhTraiService;
import com.girlkun.models.map.BDKB.BanDoKhoBau;
import com.girlkun.models.map.BDKB.BanDoKhoBauService;
import com.girlkun.models.map.Mapmabu2h.mabu2h;
import com.girlkun.models.map.gas.Gas;
import com.girlkun.models.map.gas.GasService;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.NPoint;
import com.girlkun.models.matches.PVPService;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.matches.pvp.DaiHoiVoThuatService;
import com.girlkun.models.npc.BXH;
import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Client;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.server.ServerNotify;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.services.func.Input;
import com.girlkun.services.func.LuckyRound;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import com.girlkun.services.func.ChonAiDay;
import java.util.Random;

import java.util.logging.Level;
import java.util.Timer;
import java.util.TimerTask;

import java.util.logging.Level;

public class NpcFactory {

    private static final int COST_HD = 50000000;
    int rubyCost = 50;

    private static boolean nhanVang = false;
    private static boolean nhanDeTu = false;
    
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();


    private NpcFactory() {

    }
    ///////////////////////////////////////////NPC Potage///////////////////////////////////////////
    private static Npc poTaGe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đa vũ trụ song song \b|7|Con muốn gọi con trong đa vũ trụ \b|1|Với giá 200tr vàng không?", "Gọi Boss\nNhân bản", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Boss oldBossClone = BossManager.gI().getBossById(Util.createIdBossClone((int) player.id));
                                    if (oldBossClone != null) {
                                        this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldBossClone.zone.zoneId);
                                    } else if (player.inventory.gold < 200_000_000) {
                                        this.npcChat(player, "Nhà ngươi không đủ 200 Triệu vàng ");
                                    } else {
                                        List<Skill> skillList = new ArrayList<>();
                                        for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
                                            Skill skill = player.playerSkill.skills.get(i);
                                            if (skill.point > 0) {
                                                skillList.add(skill);
                                            }
                                        }
                                        int[][] skillTemp = new int[skillList.size()][3];
                                        for (byte i = 0; i < skillList.size(); i++) {
                                            Skill skill = skillList.get(i);
                                            if (skill.point > 0) {
                                                skillTemp[i][0] = skill.template.id;
                                                skillTemp[i][1] = skill.point;
                                                skillTemp[i][2] = skill.coolDown;
                                            }
                                        }
                                        BossData bossDataClone = new BossData(
                                                "Nhân Bản" + player.name,
                                                player.gender,
                                                new short[]{player.getHead(), player.getBody(), player.getLeg(), player.getFlagBag(), player.idAura, player.getEffFront()},
                                                player.nPoint.dame,
                                                new long[]{player.nPoint.hpMax},
                                                new int[]{140},
                                                skillTemp,
                                                new String[]{"|-2|Boss nhân bản đã xuất hiện rồi"}, //text chat 1
                                                new String[]{"|-1|Ta sẽ chiếm lấy thân xác của ngươi hahaha!"}, //text chat 2
                                                new String[]{"|-1|Lần khác ta sẽ xử đẹp ngươi"}, //text chat 3
                                                60
                                        );

                                        try {
                                            new NhanBan(Util.createIdBossClone((int) player.id), bossDataClone, player.zone);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        player.inventory.gold -= 200_000_000;
                                        Service.gI().sendMoney(player);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }
   public static Npc noibanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14 || this.mapId == 5) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Hãy đem đến cho ta những nguyên liệu sau:\n Thúng nếp, thịt heo, thúng đậu xanh, lá dong"
                            + "\n Bánh tét tăng 20% x80 mỗi loại nguyên liệu đầu vào và x20 thỏi vàng\nBánh chưng tăng 30% x99 mỗi loại nguyên liệu đầu vào và x30 thỏi vàng", "Nấu\nbánh tét", "Nấu\nbánh chưng","Cửa hàng");

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item thitheo = null;
                                    Item thungnep = null;
                                    Item thungdxanh = null;
                                    Item ladong = null;
                                    Item thoivang = null;

                                    try {
                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 748);
                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 749);
                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 750);
                                        ladong = InventoryServiceNew.gI().findItemBag(player, 751);
                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (thitheo == null || thitheo.quantity < 80 || thungnep == null || thungnep.quantity < 80 || thungdxanh == null || thungdxanh.quantity < 80 || ladong == null || ladong.quantity < 80) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");
                                    } else if (thoivang == null || thoivang.quantity < 20) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 20);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 752);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh tét");
                                    }
                                    break;
                                }
                                case 1: {
                                    Item thitheo = null;
                                    Item thungnep = null;
                                    Item thungdxanh = null;
                                    Item ladong = null;
                                    Item thoivang = null;

                                    try {
                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 748);
                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 749);
                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 750);
                                        ladong = InventoryServiceNew.gI().findItemBag(player, 751);
                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (thitheo == null || thitheo.quantity < 99 || thungnep == null || thungnep.quantity < 99 || thungdxanh == null || thungdxanh.quantity < 99 || ladong == null || ladong.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");
                                    } else if (thoivang == null || thoivang.quantity < 30) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 30);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 753);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh chưng");
                                    }
                                    break;
                                }
                                case 2:
                            ShopServiceNew.gI().opendShop(player, "SHOPBANH", true);
                            break;
                            
//                                case 2: {
//                                    Item thitheo = null;
//                                    Item thungnep = null;
//                                    Item thungdxanh = null;
//                                    Item ladong = null;
//                                    Item thoivang = null;
//
//                                    try {
//                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 748);
//                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 749);
//                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 750);
//                                        ladong = InventoryServiceNew.gI().findItemBag(player, 751);
//                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if (thitheo == null || thitheo.quantity < 99 || thungnep == null || thungnep.quantity < 99 || thungdxanh == null || thungdxanh.quantity < 99 || ladong == null || ladong.quantity < 99) {
//                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");
//                                    } else if (thoivang == null || thoivang.quantity < 50) {
//                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 5);
//                                        Service.getInstance().sendMoney(player);
//                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 457, 200);
//                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Bạn nhận được 200 thỏi vàng");
//                                    }
//                                    break;
//                                }
                            }
                        }
                    }
                }
            }
        };
    } 
    public static Npc thoren(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 5) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "\b|7|Bạn cần đổi gì?\b|7|", "Đổi đồ\nHủy Diệt\nTrái Đất", "Đổi đồ\nHuy Diệt\nNamek", "Đổi Đồ\nHủy Diệt\nxayda", "Đổi Đồ\nThiên Sứ\nTrái Đất", "Đổi Đồ\nThiên Sứ\nNamek", "Đổi Đồ\nThiên Sú\nXayda");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 1,
                                            "\b|7|Bạn muốn đổi 1 món đồ thần linh \nTrái đất cùng loại và x30 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ ra SKH", "Áo\nHúy Diệt", "Quần\nHúy Diệt", "Găng\nDúy Diệt", "Giày\nHúy Diệt", "Nhẫn\nHúy Diệt", "Thôi Khỏi");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 2,
                                            "\b|7|Bạn muốn đổi 1 món đồ thần linh \nNamek cùng loại và x30 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ ra SKH", "Áo\nHúy Diệt", "Quần\nHúy Diệt", "Găng\nDúy Diệt", "Giày\nHúy Diệt", "Nhẫn\nHúy Diệt", "Thôi Khỏi");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 3,
                                            "\b|7|Bạn muốn đổi 1 món đồ thần linh \nXayda cùng loại và x30 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ ra SKH", "Áo\nHúy Diệt", "Quần\nHúy Diệt", "Găng\nDúy Diệt", "Giày\nHúy Diệt", "Nhẫn\nHúy Diệt", "Thôi Khỏi");
                                    break;
                                case 3:
                                    this.createOtherMenu(player, 4,
                                            "\b|7|Bạn muốn đổi 1 món đồ húy diệt \nTrái đất cùng loại và x99 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ ra SKH", "Áo\nThiên sứ", "Quần\nThiên sứ", "Găng\nThiên sứ", "Giày\nThiên Sứ", "Nhẫn\nThiên Sứ", "Từ Chối");
                                    break;
                                case 4:
                                    this.createOtherMenu(player, 5,
                                            "\b|7|Bạn muốn đổi 1 món đồ húy diệt \nNamek cùng loại và x99 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ ra SKH", "Áo\nThiên sứ", "Quần\nThiên sứ", "Găng\nThiên sứ", "Giày\nThiên Sứ", "Nhẫn\nThiên Sứ", "Từ Chối");
                                    break;
                                case 5:
                                    this.createOtherMenu(player, 6,
                                            "\b|7|Bạn muốn đổi 1 món đồ húy diệt \nXayda cùng loại và x99 đá ngũ sắc \n|6|Để đổi lấy 1 món đồ thiên sứ có tý lệ ra SKH", "Áo\nThiên sứ", "Quần\nThiên sứ", "Găng\nThiên sứ", "Giày\nThiên Sứ", "Nhẫn\nThiên Sứ", "Từ Chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) { // action đổi dồ húy diệt
                            switch (select) {
                                case 0: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 555);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 1; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 555 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 555) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 650);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Áo Thần linh trái đất + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 1: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 556);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 556 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 556 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 651 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Quần Thần linh trái đất + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 2: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 562);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 562 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 562 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 657 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Găng Thần linh trái đất + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 3: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 563);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 563 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 563 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 658 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Giày Thần linh trái đất + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 4: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 561 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 656 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Nhận Thần linh trái đất + x30 Đá Ngũ Sắc!");
                                        }
                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 5: // canel
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 2) { // action đổi dồ húy diệt
                            switch (select) {
                                case 0: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 557);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 557 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 557 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 650 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Áo Thần linh namec + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 1: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 558);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 558 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 558 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 651 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Quần Thần linh namec + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 2: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 564);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 564);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 564) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 659);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Găng Thần linh namec + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 3: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 565);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 565 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 565 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 658 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Giày Thần linh namec + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 4: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 561 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 656 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Nhận Thần linh namec + x30 Đá Ngũ Sắc!");
                                        }
                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 5: // canel
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 3) { // action đổi dồ húy diệt
                            switch (select) {
                                case 0: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 559);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 559 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 559 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 650 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Áo Thần linh xayda + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 1: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 560);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 560 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 560 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 655 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Quần Thần linh xayda + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 2: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 566);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 566 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 566 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 661 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Găng Thần linh xayda + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 3: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 567);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 567 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 567 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 658 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Giày Thần linh xayda + x30 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 4: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 561 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 561 + i) && soLuong >= 30) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoathuydiet(player, 656 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");
                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Nhận Thần linh xayde + x30 Đá Ngũ Sắc!");
                                        }
                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 5: // canel
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 4) { // action đổi dồ thiên sứ
                            switch (select) {
                                case 0: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 650);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 650 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 650 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1048 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Áo húy diệt trái đất + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 1: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 651);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 651 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 651 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1051 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Quần húy diệt trái đất + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 2: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 657);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 657 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 657 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1054);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Găng húy diệt trái đất + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 3: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 658);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 658 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 658 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1057 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Giày húy diệt trái đất + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 4: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 656 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1060 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần nhận húy diệt trái đất + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 5: // canel
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 5) { // action đổi dồ thiên sứ
                            switch (select) {
                                case 0: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 652);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 652 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 652 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1049 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Áo húy diệt namec + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 1: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 653);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 653 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 653 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1052 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Quần húy diệt namec + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 2: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 659);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 659 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 659 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1055 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Găng húy diệt namec + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 3: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 660);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 660 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 660 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1058 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Giày húy diệt namec + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 4: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 656 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1061 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần nhận húy diệt namec + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 5: // canel
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 6) { // action đổi dồ thiên sứ
                            switch (select) {
                                case 0: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 654);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 654 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 654 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1050 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Áo húy diệt xayda + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 1: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 655);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 655 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 655 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1053 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Quần húy diệt xayda + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 2: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 661);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 661 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 661 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1056 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Găng húy diệt xayda + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 3: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 662);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 662 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 662 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1059 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần Giày húy diệt xayda + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 4: // trade
                                    try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item tl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item thl = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 656 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 656 + i) && soLuong >= 99) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 99);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, thl, 1);
                                            CombineServiceNew.gI().GetTrangBiKichHoatthiensu(player, 1062 + i);
                                            this.npcChat(player, "Chuyển Hóa Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần nhận húy diệt xayda + x99 Đá Ngũ Sắc!");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 5: // canel
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

public static Npc hungvuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Đến map sự kiện săn boss Ngộ Không kiếm kim cương và dưa hấu",
                            "Sự kiện", "Map sự kiện", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.HUNG_VUONG, "|2|Kim cương với thỏi vàng của nhà người đâu???\nCó 3 cách tặng quà cho Hùng Vương để nhận vật phẩm hiếm\nCách 1: Cần 500 thỏi vàng nhận cải trang ngẫu nhiên chỉ số\nCách 2: Cần x20 king cương + 1 Tỷ vàng = CT Sơn Tinh\nCách 3: Cần x20 king cương + 1 Tỷ vàng = CT Thủy Tinh", "Đổi\nCT Mị nương",
                                            "Đổi CT\nSơn tinh", "Đổi CT\nThủy tinh", "đóng");
                                    break;
                                case 1:
                                     if (player.getSession().player.nPoint.power >= 120000000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 158, -1, 354);
                                     } else {
                                    this.npcChat(player, "Bạn chưa đủ 120 tỷ sức mạnh để vào");
                              }
                                    break;                                    // 
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.HUNG_VUONG) {
                            switch (select) {
                                case 0:
                                    Item honLinhThu1 = null;
                                    try {
                                        honLinhThu1 = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu1 == null || honLinhThu1.quantity <= 500) {
                                        this.npcChat(player, "Bạn không đủ 500tv");
                                    } else if (player.inventory.gold < 0) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 0;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu1, 500);
                                        Service.gI().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 860);
                                        
trungLinhThu.itemOptions.add(new Item.ItemOption(50, new Random().nextInt(15) + 10));

trungLinhThu.itemOptions.add(new Item.ItemOption(117, new Random().nextInt(15) + 12));

trungLinhThu.itemOptions.add(new Item.ItemOption(148, new Random().nextInt(15) + 10));
 
trungLinhThu.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(15) + 15));   
 
trungLinhThu.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(15) + 15));

trungLinhThu.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(15) + 5));

trungLinhThu.itemOptions.add(new Item.ItemOption(5, new Random().nextInt(15) + 5));

                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được cải trang mị nương");
                                        ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa nhận được cải trang mị nương" + " tại NPC Hùng Vương");
                             break;
                                    }
                                case 1:
                                    Item honLinhThu = null;
                                    try {
                                        honLinhThu = InventoryServiceNew.gI().findItemBag(player, 1998);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu == null || honLinhThu.quantity < 20) {
                                        this.npcChat(player, "Bạn không đủ x20 kim cương");
                                    } else if (player.inventory.gold < 1_000_000_000) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 1_000_000_000) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 1_000_000_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 20);
                                        Service.gI().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 421);
                                        
trungLinhThu.itemOptions.add(new Item.ItemOption(49, new Random().nextInt(35) + 5));
  
trungLinhThu.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(35) + 5));   
 
trungLinhThu.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(35) + 5));

trungLinhThu.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(10) + 5));

trungLinhThu.itemOptions.add(new Item.ItemOption(5, new Random().nextInt(10) + 5));

trungLinhThu.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(10) + 5));   
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 cải trang Sơn tinh");
                             break;
                                    }
                                case 2:  
                                  Item honLinhThu2 = null;
                                    try {
                                        honLinhThu2 = InventoryServiceNew.gI().findItemBag(player, 1998);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu2 == null || honLinhThu2.quantity < 20) {
                                        this.npcChat(player, "Bạn không đủ x20 kim cương");
                                    } else if (player.inventory.gold < 1_000_000_000) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 1_000_000_000) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 1_000_000_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu2, 20);
                                        Service.gI().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 422);
                                        
trungLinhThu.itemOptions.add(new Item.ItemOption(49, new Random().nextInt(35) + 5));
  
trungLinhThu.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(35) + 5));   
 
trungLinhThu.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(35) + 5));

trungLinhThu.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(10) + 5));

trungLinhThu.itemOptions.add(new Item.ItemOption(5, new Random().nextInt(10) + 5));

trungLinhThu.itemOptions.add(new Item.ItemOption(93, new Random().nextInt(10) + 5));   
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 cải trang Thủy tinh");
                             break;
                                    }

//                                case 3:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 178, -1, 354);
//                                    break;
                            }
                            }
                        }
                    }
                }
        };
    }
            public static Npc berry(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                           // "Để đổi được cải trang bí ẩn\nCần 1000 thỏi vàng\noption ngẫu nhiên cực cao và Vĩnh Viễn",
                           "Đủ 80 tỉ sức mạnh có thể soi boss thế giới",
                            "Soi Boss","Đến Địa Ngục");
                }
            }

             public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
//                                case 1:
//                                    Item thoivang = null;
//                                    try {
//                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if  (thoivang == null || thoivang.quantity < 1000) {
//                                       this.npcChat(player, "Bạn không đủ 1000 Thỏi vàng");                                                                       
//                                   } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {                                       
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 1000);
//
//                                Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(1180,1183));
//trungLinhThu.itemOptions.add(new Item.ItemOption(49, new Random().nextInt(20) + 25));
//trungLinhThu.itemOptions.add(new Item.ItemOption(117, new Random().nextInt(15) + 12)); 
//  
//trungLinhThu.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(15) + 25));   
// 
//trungLinhThu.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(15) + 25));
//
//trungLinhThu.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(10) + 5));
//
//trungLinhThu.itemOptions.add(new Item.ItemOption(5, new Random().nextInt(25) + 5));
//                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Bạn nhận được cải trang");
//                                        
//                             break;
//                                    }

                                case 0: 
                                     
                                    if (player.getSession().player.nPoint.power >= 80000000000L) {
                                    BossManager.gI().showListBoss1(player);
                                     } else {
                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để xem");
                              }
                                    break;
                                    case 1:
                                     if (player.getSession().player.nPoint.power >= 80000000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 198, -1, 96);
                                     } else {
                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                              }
                                    break;
                               }
                            }
                        }
                    }
                }
        };
    }
                       public static Npc onggianoel(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                             "|2|Đổi vật phẩm sự kiện...\ncần 200 sao biển + 200 con cua + 200 vỏ sò + 200 vỏ ốc+ 20 thỏi vàng để đổi vật phẩm đeo lưng\ncần 50 sao biển + 50 con cua + 2 thỏi vàng để đổi Capsule hồng ngọc\nĐổi ván bay vip cần x99 Trái Dừa",
                            "Đổi họp quà\nvật phẩm đeo lưng","Đổi Capsule Hồng Ngọc","Đổi Ván Bay Vip ");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                 case 0:
                                 {
                                Item honLinhThu11 = null;
                                Item thoivang = null;
                                Item honLinhThu12 = null;
                                Item honLinhThu13 = null;
                                Item honLinhThu14 = null;

                                    try {
                                        honLinhThu11 = InventoryServiceNew.gI().findItemBag(player, 697);
                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                        honLinhThu12 = InventoryServiceNew.gI().findItemBag(player, 698);
                                        honLinhThu13 = InventoryServiceNew.gI().findItemBag(player, 695);
                                        honLinhThu14 = InventoryServiceNew.gI().findItemBag(player, 696);


                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu11 == null || honLinhThu11.quantity < 200) {
                                        this.npcChat(player, "Bạn không đủ vật phẩm");
                                    } else if (thoivang == null || thoivang.quantity < 20) {

                                    } else if (honLinhThu12 == null || honLinhThu12.quantity < 200) {
    
                                    } 
                                    if (honLinhThu13 == null || honLinhThu11.quantity < 200) {

                                    }
                                    if (honLinhThu14 == null || honLinhThu11.quantity < 200) {

                                    }else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu11, 200);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 20);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu12, 200);
                                         InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu13, 200);
                                          InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu14, 200);
                                          Item trungLinhThu = ItemService.gI().createNewItem((short)1244);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được hộp quà vật phẩm đeo lưng");
                                        
                             break;
                                    }
                            }
                                    
                                
                                 

//                                case 2:
//                                    Item honLinhThu111 = null;
//                                    Item thoivang1 = null;
//                                    Item honLinhThu121 = null;
//
//                                    try {
//                                        honLinhThu111 = InventoryServiceNew.gI().findItemBag(player, 695);
//                                        thoivang1 = InventoryServiceNew.gI().findItemBag(player, 457);
//                                        honLinhThu121 = InventoryServiceNew.gI().findItemBag(player, 696);
//
//
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if (honLinhThu111 == null || honLinhThu111.quantity < 99) {
//                                        this.npcChat(player, "Bạn không đủ vỏ ốc");
//                                    } else if (thoivang1 == null || thoivang1.quantity < 5) {
//                                        this.npcChat(player, "Bạn không đủ Thỏi vàng");
//                                    } else if (honLinhThu121 == null || honLinhThu121.quantity < 99) {
//                                        this.npcChat(player, "Bạn không đủ vỏ sò");    
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu111, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang1, 5);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu121, 99);
//
//                                Item trungLinhThu1 = ItemService.gI().createNewItem((short) Util.nextInt(998, 1001));
//                                trungLinhThu1.itemOptions.add(new Item.ItemOption(49, new Random().nextInt(21) + 5));
//  
//                                trungLinhThu1.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(21) + 5));   
// 
//                                trungLinhThu1.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(21) + 5));
//
//
//                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu1);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Chúc mừng bạn nhận " + trungLinhThu1.template.name);
//                                        
//                             break;
//                                    } 
                                 case 1:
                                 {
                                    Item honLinhThu110 = null;
                                    Item thoivang0 = null;
                                    Item honLinhThu120 = null;

                                    try {
                                        honLinhThu110 = InventoryServiceNew.gI().findItemBag(player, 697);
                                        thoivang0 = InventoryServiceNew.gI().findItemBag(player, 457);
                                        honLinhThu120 = InventoryServiceNew.gI().findItemBag(player, 698);


                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu110 == null || honLinhThu110.quantity < 50) {
                                        this.npcChat(player, "Bạn không đủ con cua");
                                    } else if (thoivang0 == null || thoivang0.quantity < 2) {
                                        this.npcChat(player, "Bạn không đủ 2 Thỏi vàng");
                                    } else if (honLinhThu120 == null || honLinhThu120.quantity < 50) {
                                        this.npcChat(player, "Bạn không đủ sao biển");    
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu110, 50);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang0, 2);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu120, 50);

                                Item trungLinhThu = ItemService.gI().createNewItem((short)1245);
                                
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Chúc mừng bạn nhận " + trungLinhThu.template.name);
                                        
                             break;
                                    }
                                 }
                                 case 2:
                                 {
                                    Item honLinhThu1 = null;
                                    try {
                                        honLinhThu1 = InventoryServiceNew.gI().findItemBag(player, 694);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu1 == null || honLinhThu1.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ Trái Dừa");
                                    } else if (player.inventory.gold < 0) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 0;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu1, 99);
                                        Service.gI().sendMoney(player);
                                       Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(1203, 1204));
                                        
trungLinhThu.itemOptions.add(new Item.ItemOption(49, new Random().nextInt(1) + 20));

trungLinhThu.itemOptions.add(new Item.ItemOption(148, new Random().nextInt(1) + 20));
 
trungLinhThu.itemOptions.add(new Item.ItemOption(77, new Random().nextInt(1) + 20));   
 
trungLinhThu.itemOptions.add(new Item.ItemOption(103, new Random().nextInt(1) + 20));

trungLinhThu.itemOptions.add(new Item.ItemOption(5, new Random().nextInt(1) + 20));

                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Chúc mừng bạn nhận " + trungLinhThu.template.name);
                             break;
                                    } 
                                 }
                               }
                            }
                        }
                    }
                }
        };
    }

        
    ///////////////////////////////////////////NPC Quy Lão Kame///////////////////////////////////////////
//    private static Npc quyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            public void chatWithNpc(Player player) {
//                String[] chat = {
//                    "Là lá la",
//                    "La lá là",
//                    "Lá là la"
//                };
//                Timer timer = new Timer();
//                timer.scheduleAtFixedRate(new TimerTask() {
//                    int index = 0;
//                    @Override
//                    public void run() {
//                        npcChat(player, chat[index]);
//                        index = (index + 1) % chat.length;
//                    }
//                }, 10000, 10000);
//            }
//            @Override
//            public void openBaseMenu(Player player) {
//                chatWithNpc(player);
//                Item ruacon = InventoryServiceNew.gI().findItemBag(player, 874);
//                if (canOpenNpc(player)) {
//                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
//                        if (ruacon != null && ruacon.quantity >= 1) {
////                            this.createOtherMenu(player, 12, "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
////                                    "Giao\nRùa con", "Nói chuyện", "Bảng\nXếp Hạng");
//                        } else {
//                            this.createOtherMenu(player, 13, "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
//                                     "Nói chuyện", "Bảng\nXếp Hạng");
//                        }
//                    }
//                }
//            }
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    if (player.iDMark.getIndexMenu() == 12) {
//                        switch (select) {
//                            case 0:
//                            this.createOtherMenu(player, 5,
//                                    "Cảm ơn cậu đã cứu con rùa của ta\n Để cảm ơn ta sẽ tặng cậu món quà.",
//                                    "Nhận quà","Đóng");
//                            break;
//                            case 1:
//                                this.createOtherMenu(player, 6,
//                                        "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
//                                        "Về khu\nvực bang", "Giải tán\nBang hội","Kho Báu\ndưới biển","Đảo\nnghỉ dưỡng");
//                                break;
//                            case 2:
//                                    Service.gI().showListTop(player, Manager.topSM);
//                                break;
//                        }
//                    } else if (player.iDMark.getIndexMenu() == 5) {
//                        switch (select) {
//                            case 0:
//                                try {
//                                    Item RuaCon = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 874);
//                                    if (RuaCon != null) {
//                                        if (RuaCon.quantity >= 1 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
//                                            int randomItem = Util.nextInt(6); // Random giữa 0 và 1
//                                            if (randomItem == 0) {
//                                                Item VatPham = ItemService.gI().createNewItem((short) (865));
//                                                VatPham.itemOptions.add(new Item.ItemOption(50, 20));
//                                                VatPham.itemOptions.add(new Item.ItemOption(77, 10));
//                                                VatPham.itemOptions.add(new Item.ItemOption(103, 10));
//                                                VatPham.itemOptions.add(new Item.ItemOption(14, 5));
//                                                VatPham.itemOptions.add(new Item.ItemOption(93, 7));
//                                                InventoryServiceNew.gI().addItemBag(player, VatPham);
//                                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Kiếm Z", "Ok");
//                                            } else if (randomItem == 1) {
//                                                Item VatPham = ItemService.gI().createNewItem((short) (865));
//                                                VatPham.itemOptions.add(new Item.ItemOption(50, 20));
//                                                VatPham.itemOptions.add(new Item.ItemOption(77, 10));
//                                                VatPham.itemOptions.add(new Item.ItemOption(103, 10));
//                                                VatPham.itemOptions.add(new Item.ItemOption(14, 5));
//                                                VatPham.itemOptions.add(new Item.ItemOption(93, 14));
//                                                InventoryServiceNew.gI().addItemBag(player, VatPham);
//                                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Kiếm Z", "Ok");
//                                            } else if (randomItem == 2) {
//                                                Item VatPham = ItemService.gI().createNewItem((short) (865));
//                                                VatPham.itemOptions.add(new Item.ItemOption(50, 20));
//                                                VatPham.itemOptions.add(new Item.ItemOption(77, 10));
//                                                VatPham.itemOptions.add(new Item.ItemOption(103, 10));
//                                                VatPham.itemOptions.add(new Item.ItemOption(14, 5));
//                                                VatPham.itemOptions.add(new Item.ItemOption(93, 30));
//                                                InventoryServiceNew.gI().addItemBag(player, VatPham);
//                                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Kiếm Z", "Ok");
//                                            } else if (randomItem == 3) {
//                                                Item VatPham = ItemService.gI().createNewItem((short) 733);
//                                                VatPham.itemOptions.add(new Item.ItemOption(84, 0));
//                                                VatPham.itemOptions.add(new Item.ItemOption(30, 0));
//                                                VatPham.itemOptions.add(new Item.ItemOption(93, 7));
//                                                InventoryServiceNew.gI().addItemBag(player, VatPham);
//                                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Cân đẩu vân ngũ sắc", "Ok");
//                                            } else if (randomItem == 4) {
//                                                Item VatPham = ItemService.gI().createNewItem((short) 733);
//                                                VatPham.itemOptions.add(new Item.ItemOption(84, 0));
//                                                VatPham.itemOptions.add(new Item.ItemOption(30, 0));
//                                                VatPham.itemOptions.add(new Item.ItemOption(93, 14));
//                                                InventoryServiceNew.gI().addItemBag(player, VatPham);
//                                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Cân đẩu vân ngũ sắc", "Ok");
//                                            } else if (randomItem == 5) {
//                                                Item VatPham = ItemService.gI().createNewItem((short) 733);
//                                                VatPham.itemOptions.add(new Item.ItemOption(84, 0));
//                                                VatPham.itemOptions.add(new Item.ItemOption(30, 0));
//                                                VatPham.itemOptions.add(new Item.ItemOption(93, 14));
//                                                InventoryServiceNew.gI().addItemBag(player, VatPham);
//                                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Cân đẩu vân ngũ sắc", "Ok");
//                                            } else {
//                                                Item VatPham = ItemService.gI().createNewItem((short) 16);
//                                                InventoryServiceNew.gI().addItemBag(player, VatPham);
//                                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Ta tặng cậu Ngọc rồng 3 sao", "Ok");
//                                            }
//                                            InventoryServiceNew.gI().subQuantityItemsBag(player, RuaCon, 1);
//                                            InventoryServiceNew.gI().sendItemBags(player);
//                                        }
//                                    }
//                                } catch (Exception ex) {
//                                    ex.printStackTrace();
//                                }
//                                break;
//                            default:
//                                break;
//                        }
//                    } else if (player.iDMark.getIndexMenu() == 6) {
//                        switch (select) {
//                            case 0:
//                                if (player.getSession().player.nPoint.power >= 80000000000L) {
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
//                                } else {
//                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
//                                }
//                                break;
//                            case 1:
//                                Clan clan = player.clan;
//                                if (clan != null) {
//                                    ClanMember cm = clan.getClanMember((int) player.id);
//                                    if (cm != null) {
//                                        if (clan.members.size() > 1) {
//                                            Service.gI().sendThongBao(player, "Bang phải còn một người");
//                                            break;
//                                        }
//                                        if (!clan.isLeader(player)) {
//                                            Service.gI().sendThongBao(player, "Phải là bảng chủ");
//                                            break;
//                                        }
//                                        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
//                                                "Đồng ý", "Từ chối!");
//                                    }
//                                    break;
//                                }
//                                Service.gI().sendThongBao(player, "bạn đã có bang hội đâu!!!");
//                                break;
//                            case 2:
//                                 if (player.clan != null) {
//                                    if (player.clan.banDoKhoBau != null) {
//                                        this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
//                                                "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
//                                                        + player.clan.banDoKhoBau.level + "\nCon có muốn đi theo không?",
//                                                "Đồng ý", "Từ chối");
//                                    } else {
//
//                                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
//                                                "Đây là bản đồ kho báu \nCác con cứ yên tâm lên đường\n"
//                                                        + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
//                                                "Chọn\ncấp độ", "Từ chối");
//                                    }
//                                } else {
//                                    this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
//                                }
//                                break;
//                        }
//                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
//                        switch (select) {
//                            case 0:
//                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
//                                    ChangeMapService.gI().goToDBKB(player);
//                                } else {
//                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
//                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
//                                }
//                                break;
//                        }
//                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
//                        switch (select) {
//                            case 0:
//                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
//                                    Input.gI().createFormChooseLevelBDKB(player);
//                                } else {
//                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
//                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
//                                }
//                                break;
//                        }
//                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
//                        switch (select) {
//                            case 0:
//                                BanDoKhoBauService.gI().openBanDoKhoBau(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
//                                break;
//                                case 3: 
//                                if (player.getSession().player.nPoint.power >= 40000000000L) {
//
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 186, -1, 432);
//                                } else {
//                                    this.npcChat(player, "Bạn chưa đủ 40 tỷ sức mạnh để vào");
//                                }
//                        }
//                    }
//                } if (player.iDMark.getIndexMenu() == 13) {
//                    switch (select) {
//                        case 0:
//                            this.createOtherMenu(player, 7,
//                                    "Chào con, ta rất vui khi gặp con\n Con muốn làm gì nào ?",
//                                    "Về khu\nvực bang", "Giải tán\nBang hội","Kho Báu\ndưới biển","Đảo\nnghỉ dưỡng");
//                            break;
//                        case 1:
//                            confirmMenu(Player player, int select) {
//                            if (canOpenNpc(player)) {
//                    createOtherMenu(player, ConstNpc.BASE_MENU,
//                            "|2|Ta Vừa Hắc Mắp Xêm Được T0p Của Toàn Server\b|7|Mi Muống Xem Tóp Gì?",
//                            "Top Sức Mạnh", "Top Nhiệm Vụ", "Top Hồng Ngọc", "Top Sức Đánh", "Đóng");
//                }
//                            
//                    else if (player.iDMark.getIndexMenu() == 7)
//                        if (canOpenNpc(player)) {
//                    switch (this.mapId) {
//                        case 5:
//                                   switch (player.iDMark.getIndexMenu()) {
//                                case ConstNpc.BASE_MENU:
//                                    if (select == 0) {
//                                        Service.gI().showListTop(player, Manager.topSM);
//                                        break;
//                                    }
//                                    if (select == 1) {
//                                        Service.gI().showListTop(player, Manager.topNV);
//                                        break;
//                                    }
//                                    if (select == 2) {
//                                        Service.gI().showListTop(player, Manager.topPVP);
//                                        break;
//                                    }
//                                    if (select == 3) {
//                                        Service.gI().showListTop(player, Manager.topSD);
//                                        break;
//                                    }
//                                    break;
//                            }
//                            break;
//                    }
//                        }
//                    }
//                    }
//                }
//                } else if (player.iDMark.getIndexMenu() == 7) {
//                    switch (select) {
//                        case 0:
//                            if (player.getSession().player.nPoint.power >= 80000000000L) {
//                                ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
//                            } else {
//                                this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
//                            }
//                            break;
//                        case 1:
//                            Clan clan = player.clan;
//                            if (clan != null) {
//                                ClanMember cm = clan.getClanMember((int) player.id);
//                                if (cm != null) {
//                                    if (clan.members.size() > 1) {
//                                        Service.gI().sendThongBao(player, "Bang phải còn một người");
//                                        break;
//                                    }
//                                    if (!clan.isLeader(player)) {
//                                        Service.gI().sendThongBao(player, "Phải là bảng chủ");
//                                        break;
//                                    }
//                                    NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
//                                            "Đồng ý", "Từ chối!");
//                                }
//                                break;
//                            }
//                            Service.gI().sendThongBao(player, "bạn đã có bang hội đâu!!!");
//                            break;
//                        case 2:
//                            if (player.clan != null) {
//                                    if (player.clan.banDoKhoBau != null) {
//                                        this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
//                                                "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
//                                                        + player.clan.banDoKhoBau.level + "\nCon có muốn đi theo không?",
//                                                "Đồng ý", "Từ chối");
//                                    } else {
//
//                                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
//                                                "Đây là bản đồ kho báu \nCác con cứ yên tâm lên đường\n"
//                                                        + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
//                                                "Chọn\ncấp độ", "Từ chối");
//                                    }
//                                } else {
//                                    this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
//                                }
//                                break;
//                    }
//                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
//                    switch (select) {
//                        case 0:
//                            if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
//                                ChangeMapService.gI().goToDBKB(player);
//                            } else {
//                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
//                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
//                            }
//                            break;
//                    }
//                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
//                    switch (select) {
//                        case 0:
//                            if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
//                                Input.gI().createFormChooseLevelBDKB(player);
//                            } else {
//                                this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
//                                        + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
//                            }
//                            break;
//                    }
//                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
//                    switch (select) {
//                        case 0:
//                            BanDoKhoBauService.gI().openBanDoKhoBau(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
//                            break;
//                            case 3: 
//                                if (player.getSession().player.nPoint.power >= 40000000000L) {
//
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 186, -1, 432);
//                                } else {
//                                    this.npcChat(player, "Bạn chưa đủ 40 tỷ sức mạnh để vào");
//                                }
//                    }
//                }
//            }
//        };
//    }
//            
private static Npc quyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.getSession().is_gift_box) {
//                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào?", "Giải tán bang hội", "Nhận quà\nđền bù");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào?", "Giải tán bang hội", "Lãnh địa Bang Hội","Kho báu dưới biển");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                if (clan != null) {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (clan.members.size() > 1) {
                                            Service.gI().sendThongBao(player, "Bang phải còn một người");
                                            break;
                                        }
                                        if (!clan.isLeader(player)) {
                                            Service.gI().sendThongBao(player, "Phải là bảng chủ");
                                            break;
                                        }
//                                        
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                                "Yes you do!", "Từ chối!");
                                    }
                                    break;
                                }
                                Service.gI().sendThongBao(player, "Có bang hội đâu ba!!!");
                                break;
                            case 1:
                                if (player.getSession().player.nPoint.power >= 80000000000L) {

                                    ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                                }
                                break;                             
                            case 2:
                                if (player.clan != null) {
                                    if (player.clan.banDoKhoBau != null) {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                                                "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                                + player.clan.banDoKhoBau.level + "\nCon có muốn đi theo không?",
                                                "Đồng ý", "Từ chối");
                                    } else {

                                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                                                "Đây là bản đồ kho báu x4 tnsm\nCác con cứ yên tâm lên đường\n"
                                                + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                                "Chọn\ncấp độ", "Từ chối");
                                    }
                                } else {
                                    this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                    ChangeMapService.gI().goToDBKB(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                }
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                                    Input.gI().createFormChooseLevelBDKB(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                                }
                                break;
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
                        switch (select) {
                            case 0:
                                BanDoKhoBauService.gI().openBanDoKhoBau(player, Byte.parseByte(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                break;
                     
                                
                        }

                    }
                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Trưởng Lão Guru Namec///////////////////////////////////////////
    public static Npc truongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Vua Vegeta Xayda///////////////////////////////////////////
    public static Npc vuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Ký Gửi///////////////////////////////////////////
    private static Npc kyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, 0, "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.", "Hướng\ndẫn\nthêm", "Mua bán\nKý gửi", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player  pl, int select) {
                if (canOpenNpc(pl)) {
                    switch (select) {
                        case 0:
                            Service.getInstance().sendPopUpMultiLine(pl, tempId, avartar, "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\bChỉ với 5 hồng ngọc\bGiá trị ký gửi 10k-200Tr vàng hoặc 2-2k ngọc\bMột người bán, vạn người mua, mại dô, mại dô");
                            break;
                        case 1:
                           if (pl.nPoint.power > 80000000000L) {
                            ShopKyGuiService.gI().openShopKyGui(pl);                                                                
                                    if (pl.getSession().actived) {                                   
                                    } else {
                                        this.createOtherMenu(pl, ConstNpc.IGNORE_MENU, "Chức năng chỉ dành cho thành viên");
                                    }
                                }                                  
                            break;
                    }
                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Ông Gohan, Ông Moori, Ông Paragus///////////////////////////////////////////
    public static Npc ongGohan_ongMoori_ongParagus(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con cố gắng theo %1 học thành tài, đừng lo lắng cho ta.\nHỗ trợ các mốc nhiệm vụ 22, 28."
                                        .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                                : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta"),
                                "Giftcode","Nhận ngọc xanh","Nhận đệ tử","Đổi mật khẩu","Hỗ trợ\nNV");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
//                            case 0:
//                                ShopServiceNew.gI().opendShop(player, "SHOPHOME", false);
//                                break;
                            case 0:
                                Input.gI().createFormGiftCode(player);
                                break;
                        case 1:
                                if (player.inventory.gem == 200000) {
                                    this.npcChat(player, "Đạt giới hạn!!!");
                                    break;
                                }
                                player.inventory.gem = 200000;
                                Service.gI().sendMoney(player);
                                Service.gI().sendThongBao(player, "Bạn vừa nhận được 200K ngọc xanh");
                                break;
//                             case 2:
//                             if (!(player.inventory.gold == Inventory.LIMIT_GOLD)) {
//                             player.inventory.gold = Inventory.LIMIT_GOLD;
//                             Service.gI().sendMoney(player);
//                             Service.gI().sendThongBao(player, "Bạn vừa nhận được 2 tỉ vàng");
//                             } else {
//                             this.npcChat(player, "Bú ít thôi con");
//                             }
//                             break;
                            case 2:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                } else {
                                    this.npcChat(player, "Bạn đã có rồi");
                                }
                                break;
                            case 3:
                                Input.gI().createFormChangePassword(player);
                                break; 
                            
                       case 4: 
                                if (player.playerTask.taskMain.id == 22) {
                               switch (player.playerTask.taskMain.index) {
                                   
                                   case 0:
                                       if (player.getSession().player.nPoint.power >= 400000000000L) {
                                       TaskService.gI().DoneTask(player, ConstTask.TASK_22_0);
                                       } else {
                                    this.npcChat(player, "Bạn chưa đủ 400 tỷ sức mạnh để thực hiện");
                              }
                                       break;
                                   case 1:
                                       if (player.getSession().player.nPoint.power >= 400000000000L) {
                                       TaskService.gI().DoneTask(player, ConstTask.TASK_22_1);
                                        } else {
                                    this.npcChat(player, "Bạn chưa đủ 400 tỷ sức mạnh để thực hiện");
                              }
                                       break;
                                   case 2:
                                        if (player.getSession().player.nPoint.power >= 400000000000L) {
                                       TaskService.gI().DoneTask(player, ConstTask.TASK_22_2);
                                        } else {
                                    this.npcChat(player, "Bạn chưa đủ 400 tỷ sức mạnh để thực hiện");
                              }
                                       break;
                                   case 3:
                                        if (player.getSession().player.nPoint.power >= 450000000000L) {
                                       TaskService.gI().DoneTask(player, ConstTask.TASK_22_3);
                                        } else {
                                    this.npcChat(player, "Bạn chưa đủ 450 tỷ sức mạnh để thực hiện");
                              }
                                       break;
                                   case 4:
                                        if (player.getSession().player.nPoint.power >= 450000000000L) {
                                       TaskService.gI().DoneTask(player, ConstTask.TASK_22_4);
                                        } else {
                                    this.npcChat(player, "Bạn chưa đủ 450 tỷ sức mạnh để thực hiện");
                              }
                                       break;
                                   default:
                                       Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                       break;
                               }
                                }                                                                                              
                                  if (player.playerTask.taskMain.id == 28) {
                                    switch (player.playerTask.taskMain.index) {
                                        case 0:
                                             if (player.getSession().player.nPoint.power >= 600000000000L) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_28_0);
                                             } else {
                                    this.npcChat(player, "Bạn chưa đủ 600 tỷ sức mạnh để thực hiện");
                              }
                                            break;
                                        case 1:
                                             if (player.getSession().player.nPoint.power >= 600000000000L) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_28_1);
                                             } else {
                                    this.npcChat(player, "Bạn chưa đủ 600 tỷ sức mạnh để thực hiện");
                              }
                                            break;
                                        case 2:
                                             if (player.getSession().player.nPoint.power >= 650000000000L) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_28_2);
                                             } else {
                                    this.npcChat(player, "Bạn chưa đủ 650 tỷ sức mạnh để thực hiện");
                              }
                                            break;
                                        case 3:
                                             if (player.getSession().player.nPoint.power >= 700000000000L) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_28_3);
                                             } else {
                                    this.npcChat(player, "Bạn chưa đủ 700 tỷ sức mạnh để thực hiện");
                              }
                                            break; 
                                        case 4:
                                             if (player.getSession().player.nPoint.power >= 750000000000L) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_28_4);
                                             } else {
                                    this.npcChat(player, "Bạn chưa đủ 750 tỷ sức mạnh để thực hiện");
                              }
                                            break;
                                         case 5:
                                              if (player.getSession().player.nPoint.power >= 750000000000L) {
                                            TaskService.gI().DoneTask(player, ConstTask.TASK_28_5);
                                             } else {
                                    this.npcChat(player, "Bạn chưa đủ 750 tỷ sức mạnh để thực hiện");
                              }
                                            break;   
                                         default:
                                       Service.getInstance().sendThongBao(player, "Ta đã giúp con hoàn thành nhiệm vụ rồi mau đi trả nhiệm vụ");
                                       break;
                                    }
                                  }                                
                                break;                         
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                        switch (select) {
                            case 2:
                                if (nhanDeTu) {
                                    if (player.pet == null) {
                                        PetService.gI().createNormalPet(player);
                                        Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                    } else {
                                        this.npcChat("Con đã nhận đệ tử rồi");
                                    }
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
                        switch (select) {

                        }
                    }
                }

            }

        };
    }
    
    ///////////////////////////////////////////NPC Tori-Bot///////////////////////////////////////////
    public static Npc toribot(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Tôi là Thiên Sứ, bạn muốn tôi giúp đỡ gì cho bạn?",
                                
                                "Cửa hàng\nHồng Ngọc","Chức Năng","Cửa hàng\nThỏi Vàng");
                    }
                }
            }
            @Override
            public void confirmMenu(Player player, int select) {
                if (!canOpenNpc(player)) {
                    return;
                }
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ShopServiceNew.gI().opendShop(player, "RUBY", true);
                            break;
                         case 2:
                            ShopServiceNew.gI().opendShop(player, "THOIVANG", true);
                            break;    
                        case 1:
                            this.createOtherMenu(player, 5,
                                    "Hiện tại bạn đang có " + player.session.vnd + " VND\n"
                                    +"Mở thành viên  cần 20k VND\n"
                                    +"Chào mừng cư dân đến với Ngọc Rồng NIGHT",
                                    "Mở thành viên","Đổi\nThỏi vàng", "Đổi\nHồng Ngọc");
                            break;
//                        case 2:
//                            Item sach = InventoryServiceNew.gI().findItemBag(player, 1125);
//                            if(sach !=null && sach.quantity >= 1){
//                                SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
//                                SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
//                                SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
//                                InventoryServiceNew.gI().subQuantityItem(player.inventory.itemsBag, sach, 1);
//                                InventoryServiceNew.gI().sendItemBags(player);
//                            }
//                            Service.gI().sendThongBao(player, "Hello");
//                            return;
                    }
                } else if (player.iDMark.getIndexMenu() == 5) {
                    switch (select) {
                        case 0:
                            if (player.getSession().actived) {
                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bạn đã mở thành viên rồi, không thể mở nữa !","Tạm biệt");
                                break;
                            }
                            if (player.session.vnd >= 20000) {
                                player.getSession().actived = true;
                                if (PlayerDAO.subvndBar(player, 20000)) {
                                } else {
                                    createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng bạn đã mở thành viên thành công!", "Tạm biệt");
                                }
                            } else {
                                createOtherMenu(player, ConstNpc.IGNORE_MENU, "Không đủ VND, bạn còn thiếu " + (20000 - player.session.vnd) + " VND nữa !","Tạm biệt");
                            }
                            break;                       
                        case 1:
                            Input.gI().createFormQDTV(player);
                                    break;
//                        case 2:
//                            
//                            Item honLinhThu1 = null;
//                                    try {
//                                        honLinhThu1 = InventoryServiceNew.gI().findItemBag(player, 457);
//                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                    if (honLinhThu1 == null || honLinhThu1.quantity < 1) {
//                                        this.npcChat(player, "Bạn không đủ 1 thỏi vàng");
//                                    } else if (player.inventory.gold < 0) {
//                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        player.inventory.gold -= 0;
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu1, 100);
//                                        Service.gI().sendMoney(player);
//                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 861, 1000);
//                                       
//                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Bạn nhận được 1000 hồng ngọc");
//                             break;
//                                    };
                        case 2:
                            Input.gI().createFormQDHN(player);
                                    break;
                    }
                }
            }
        };
    }
     public static Npc npcThienSu64(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {         
                if (this.mapId == 48) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!",
                            "Hướng Dẫn","Đổi SKH VIP");                
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {                  
                    if (player.iDMark.isBaseMenu() && this.mapId == 48) {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOI_SKH_VIP);
                        }
                        if (select == 1) {
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VIP);
                        }

                        if (select == 3) {
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.CHE_TAO_TRANG_BI_TS);
                        }                
                    } // hết map 48
                    else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.CHE_TAO_TRANG_BI_TS:
                                case CombineServiceNew.NANG_CAP_SKH_VIP:  
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;                                
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }                                              
                        }
                         else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DOI_SKH_VIP) {
                        if (select == 0) {
                            CombineServiceNew.gI().startCombine(player);}
                        }
                    
                }
            } 
        };
     }
    ///////////////////////////////////////////NPC MR Popo///////////////////////////////////////////
    public static Npc mrpopo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.getSession().is_gift_box) {
                        } else {
                             this.createOtherMenu(player, ConstNpc.BASE_MENU, "Thượng đế vừa phát hiện 1 loại khí đang âm thầm\nhủy diệt mọi mầm sống trên Trái Đất,\nnó được gọi là Destron Gas.\nTa sẽ đưa các cậu đến nơi ấy, các cậu sẵn sàng chưa?","Thông Tin Chi Tiết","OK","Từ Chối");
                       }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 1:
                                if (player.clan != null) {
                                    if (player.clan.khiGas != null) {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPENED_GAS,
                                                "Bang hội của con đang đi DesTroy Gas cấp độ "
                                                        + player.clan.khiGas.level + "\nCon có muốn đi theo không?",
                                                "Đồng ý", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_GAS,
                                                "Khí Gas Huỷ Diệt đã chuẩn bị tiếp nhận các đợt tấn công của quái vật\n"
                                                        + "các con hãy giúp chúng ta tiêu diệt quái vật \n"
                                                        + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                                                "Chọn\ncấp độ", "Từ chối");
                                    }
                                } else {
                                    this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                                }
                                break;
                            
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_GAS) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= Gas.POWER_CAN_GO_TO_GAS) {
                                    ChangeMapService.gI().goToGas(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(Gas.POWER_CAN_GO_TO_GAS));
                                }
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_GAS) {
                        switch (select) {
                            case 0:
                                if (player.isAdmin() || player.nPoint.power >= Gas.POWER_CAN_GO_TO_GAS) {
                                    Input.gI().createFormChooseLevelGas(player);
                                } else {
                                    this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                                            + Util.numberToMoney(Gas.POWER_CAN_GO_TO_GAS));
                                }
                                break;
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCPET_GO_TO_GAS) {
                        switch (select) {
                            case 0:
                                GasService.gI().openBanDoKhoBau(player, Integer.parseInt(String.valueOf(PLAYERID_OBJECT.get(player.id))));
                                break;
                        }
                    }
                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Chopper///////////////////////////////////////////
    public static Npc chopper(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Êi êi cậu có muốn cùng Chopper đi đến Đảo Kho Báu không,\nnhóm Hải Tặc Mũ Rơm đang chờ đợi cậu đến đó\n Có rất nhiều phần quà mùa hấp dẫn ở đó.\n Đi thôi nào....",
                                "Đi đến\nĐảo Kho Báu","Map up hồng ngọc ", "Từ chối");
                    }
                    if (this.mapId == 170) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn quay về Đảo kame à,\nChopper tôi sẽ đưa cậu đi",
                                "Đi thôi","Từ chối");
                    }
                    if (this.mapId == 174) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn quay về Đảo kame à,\nChopper tôi sẽ đưa cậu đi",
                                "Đi thôi","Từ chối");
                    }
                }
            }
            
            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().player.nPoint.power >= 40000000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 170, -1, 1560);                                  
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 40 tỷ sức mạnh để vào");
                              }      
                        break;
                        case 1: 
                                if (player.getSession().player.nPoint.power >= 80000000000L) {

                                    ChangeMapService.gI().changeMapBySpaceShip(player, 144, -1, 432);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                                }  
                            }
                        }
                    }
                    if (this.mapId == 170) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 312);
                                    break;
                            }
                    if (this.mapId == 174) 
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {                
                                case 0: 
                                ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 312);
                                    break;
                            }
                        }
                            
                        }
                    }
                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Nami///////////////////////////////////////////
    public static Npc nami(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|1|Oh hoan nghên bạn đến với của hàng của tôi\n bạn có muốn đổi vỏ ốc, cua đỏ\nlấy các món đồ mùa hè không?.", 
                                "Cửa hàng\nNami");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                    ShopServiceNew.gI().opendShop(player, "EVENT_MUA_HE", true);
                                break;
                        }
                    }
                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Franky///////////////////////////////////////////
    public static Npc franky(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 170) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn đi ra khơi khám phá?\n Nghe nói Luffy và mọi người đang tìm tên\ngấu tướng cướp ở ngoài đó.",
                                "Ra khơi\nthôi nào", "Từ chối");
                    }
                    if (this.mapId == 0) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "|1|Cậu muốn quay về Đảo kame à,\nđể Franky tôi đưa cậu đi",
                                "Đi thôi","Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 170) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapInYard(player, 171, -1, 48);
                        break;
                            }
                        }
                    }
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 312);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }
    
    ///////////////////////////////////////////Tổ Sư Kaio///////////////////////////////////////////////
    public static Npc tosukaio(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con thấy thanh kiếm đằng kia không, chỉ những ai được chọn mới có thể nhấc thanh kiếm đó lên.",
                                "Rèn\nKiếm");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.REN_KIEM_Z);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.REN_KIEM_Z:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }
    
    ///////////////////////////////////////////NPC Bumma///////////////////////////////////////////
    public static Npc bulmaQK(int mapId, int status, int cx, int cy, int tempId, int avartar) {
    return new Npc(mapId, status, cx, cy, tempId, avartar) {
        
        @Override
        public void openBaseMenu(Player player) {
            if (canOpenNpc(player)) {
                if (this.mapId == 0) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng", "Đi đến\nSau làng");
                    }
                } else if (this.mapId == 164) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Cậu muốn quay lại làng Aru?", "Đồng ý", "Tạm Biệt");
                }
                
            }
        }
            
        @Override
        public void confirmMenu(Player player, int select) {
            if (canOpenNpc(player)) {
                if (this.mapId == 0) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: // Shop
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ bán đồ cho cư dân Trái Đất.", "Tạm biệt");
                                }
                                break;
                            case 1:
                                this.createOtherMenu(player, 1,
                                        "Cưng muốn đến khu sau làng à,",
                                        "Sau làng\nAru",
                                        "Hướng dẫn");
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0:
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                    if (player.getSession().actived) {
                                        if (player.nPoint.power >= 500000 && player.nPoint.power < 5000000L) {
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 164, -1, 144);
                                        } else {
                                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ cho cư dân đã đạt sức mạnh từ 500k đến 5 triệu để đi đến Sau làng Aru.", "Tạm biệt");
                                        }
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ cho cư dân đã mở thành viên đi đến Sau làng Aru.", "Tạm biệt");
                                    }
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ cho cư dân Trái Đất đi đến Sau làng Aru.", "Tạm biệt");
                                }
                                break;
                            case 1:
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, 165, -1, 144);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ cho cư dân Trái Đất đi thôi nha.", "Tạm biệt");
                                }
                                break;
                            case 2:
                            if (player.gender == ConstPlayer.TRAI_DAT) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.NPC_BUMMA);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ cho cư dân Trái Đất đi thôi nha.", "Tạm biệt");
                                }
                                break;
                        }
                    }
                } else if (this.mapId == 164) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                        if (player.gender == ConstPlayer.TRAI_DAT) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 192);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ cho cư dân Trái Đất đi thôi nha.", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        }
    };
}


    public static Npc dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.idNRNM != -1) {
                            if (player.zone.map.mapId == 7) {
                                this.createOtherMenu(player, 1, "Ồ, ngọc rồng namếc, bạn thật là may mắn\nnếu tìm đủ 7 viên sẽ được Rồng Thiêng Namếc ban cho điều ước", "Hướng\ndẫn\nGọi Rồng", "Gọi rồng", "Từ chối");
                            }
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.NAMEC) {
                                    ShopServiceNew.gI().opendShop(player, "DENDE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        if (player.zone.map.mapId == 7 && player.idNRNM != -1) {
                            if (player.idNRNM == 353) {
                                NgocRongNamecService.gI().tOpenNrNamec = System.currentTimeMillis() + 86400000;
                                NgocRongNamecService.gI().firstNrNamec = true;
                                NgocRongNamecService.gI().timeNrNamec = 0;
                                NgocRongNamecService.gI().doneDragonNamec();
                                NgocRongNamecService.gI().initNgocRongNamec((byte) 1);
                                NgocRongNamecService.gI().reInitNrNamec((long) 86399000);
                                SummonDragon.gI().summonNamec(player);
                            } else {
                                Service.gI().sendThongBao(player, "Anh phải có viên ngọc rồng Namếc 1 sao");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.XAYDA) {
                                    ShopServiceNew.gI().opendShop(player, "APPULE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc drDrief(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 84) {
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất" : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                         } else if (pl.getSession().player.nPoint.power >= 1500000000L) {
                            this.createOtherMenu(pl, 2, "Tàu Vũ Trụ của ta có thể đưa cầu thủ đến hành tinh khác chỉ trong 3 giây. Cầu muốn đi đâu?",
                                    "Đến\nNamếc", "Đến\nXayda", "Siêu thị");
                        } else {
                            this.createOtherMenu(pl, 3,
                                    "Tàu Vũ Trụ của ta có thể đưa cầu thủ đến hành tinh khác chỉ trong 3 giây. Cầu muốn đi đâu?",
                                    "Đến\nNamếc", "Đến\nXayda");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                    } else if (player.iDMark.getIndexMenu() == 2) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 3) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                            break;
                        }
                    }
                }
            }
        };
    }
    
    

    public static Npc cargo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(player, this.avartar, "Hãy lên con đường cầu vồng đưa bé nhà tôi\n"
                                    + "Chắc bây giờ nó sẽ không còn sợ hãi nữa");
                        } else if (player.getSession().player.nPoint.power >= 1500000000L) {
                            this.createOtherMenu(player, 2, "Tàu Vũ Trụ của ta có thể đưa cầu thủ đến hành tinh khác chỉ trong 3 giây. Cầu muốn đi đâu?",
                                    "Đến\nTrái đất", "Đến\nXayda", "Siêu thị");
                        } else {
                            this.createOtherMenu(player, 3,
                                    "Tàu Vũ Trụ của ta có thể đưa cầu thủ đến hành tinh khác chỉ trong 3 giây. Cầu muốn đi đâu?",
                                    "Đến\nTrái đất", "Đến\nXayda");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == 2) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                            break;
                        case 2:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 3) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                            break;
                    }
                }
            }
        }
    };
    }
    

    public static Npc cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_FIND_BOSS = 50000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(player, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            if (this.mapId == 19) {

                                int taskId = TaskService.gI().getIdTask(player);
                                switch (taskId) {
                                    case ConstTask.TASK_19_0:
                                        this.createOtherMenu(player, ConstNpc.MENU_FIND_KUKU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_1:
                                        this.createOtherMenu(player, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nMập đầu đinh\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_2:
                                        this.createOtherMenu(player, ConstNpc.MENU_FIND_RAMBO,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến Cold", "Đến\nNappa", "Từ chối");
                                        break;
                                    default:
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến Cold", "Đến\nNappa", "Từ chối");

                                        break;
                                }
                            } else if (this.mapId == 69) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                        "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                            } else if (player.getSession().player.nPoint.power >= 1500000000L) {
                            this.createOtherMenu(player, 2, "Tàu Vũ Trụ của ta có thể đưa cầu thủ đến hành tinh khác chỉ trong 3 giây. Cầu muốn đi đâu?",
                                    "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                        } else {
                            this.createOtherMenu(player, 3,
                                    "Tàu Vũ Trụ của ta có thể đưa cầu thủ đến hành tinh khác chỉ trong 3 giây. Cầu muốn đi đâu?",
                                    "Đến\nTrái Đất", "Đến\nNamếc");
                        }
                    }
                }
            }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 26) {
                        if (player.iDMark.getIndexMenu() == 2) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                            break;
                        case 2:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 3) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                            break;
                        case 1:
                            ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                            break;
                    }
                }
            }
        }
                    if (this.mapId == 19) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().player.nPoint.power >= 80000000000L) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    } else {
                                        this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                                    }
                                    break;
                                case 1:
                                    if (player.getSession().player.nPoint.power >= 2000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 69, -1, -90);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 2 triệu sức mạnh để đi đến đây.");
                                break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.KUKU);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z != null && z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.gI().sendMoney(player);
                                            } else {
                                                Service.gI().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.gI().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    if (player.getSession().player.nPoint.power >= 80000000000L) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    } else {
                                        this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                                    }
                                    break;
                                case 2:
                                    if (player.getSession().player.nPoint.power >= 2000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 69, -1, -90);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 2 triệu sức mạnh để đi đến đây.");
                                break;
                                    }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.MAP_DAU_DINH);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z != null && z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.gI().sendMoney(player);
                                            } else {
                                                Service.gI().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.gI().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    if (player.getSession().player.nPoint.power >= 80000000000L) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    } else {
                                        this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                                    }
                                    break;
                                case 2:
                                    if (player.getSession().player.nPoint.power >= 2000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 69, -1, -90);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 2 triệu sức mạnh để đi đến đây.");
                                break;
                                    }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.RAMBO);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z != null && z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.gI().sendMoney(player);
                                            } else {
                                                Service.gI().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.gI().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    if (player.getSession().player.nPoint.power >= 80000000000L) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    } else {
                                        this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                                    }
                                    break;
                                case 2:
                                    if (player.getSession().player.nPoint.power >= 2000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 69, -1, -90);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 2 triệu sức mạnh để đi đến đây.");
                                break;
                                    }
                            }
                        }
                    }
                    if (this.mapId == 69) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                    break;
                            }
                        }
                    }
                }
        };
    }

    public static Npc santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?",
                            "Cửa hàng","Shop VIP");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: // shop
                                    ShopServiceNew.gI().opendShop(player, "SANTA", false);
                                    break;
//                                case 1:
//                                    this.createOtherMenu(player, ConstNpc.NAP_THE, "|2|Bạn Hãy Chọn Loại Thẻ Đi :3",
//                                            "VIETTEL",
//                                            "MOBIFONE", "VINAPHONE");
//                                    break;
//                                case 2:
//                                    this.createOtherMenu(player, ConstNpc.QUY_DOI,
//                                            "|7|Số tiền của bạn còn : " + player.getSession().coinBar + "\n"
//                                                    + "Tỉ lệ quy đổi là x3\n" + "Muốn quy đổi không",
//                                            "Quy đổi\n Thỏi vàng", "không");
//                                    break;

                               
                                case 1:
                                    // if (player.session.actived == 1) {
                                    ShopServiceNew.gI().opendShop(player, "SANTA_RUBY", false);
                                    // return;
                                    break;
                                // }
                                // Service.gI().sendThongBao(player, "Bạn chưa mở thành viên!!");
                                // break;

                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.VIETTEL,
                                            "Nhâp đủ 2 dòng rồi ấn nạp chờ hệ thống check :3", "10k",
                                            "20k", "50k", "100k", "200k", "500k", "1tr");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.MOBIFONE,
                                            "Nhâp đủ 2 dòng rồi ấn nạp chờ hệ thống check :3", "10k",
                                            "20k", "50k", "100k", "200k", "500k", "1tr");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, ConstNpc.VINAPHONE,
                                            "Nhâp đủ 2 dòng rồi ấn nạp chờ hệ thống check :3", "10k",
                                            "20k", "50k", "100k", "200k", "500k", "1tr");
                                    break;
                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.VINAPHONE) {
                            switch (select) {
                                case 0:
                                    this.npcChat(player, "Hệ thống đang bảo trì!");
                                case 1:
                                    Input.gI().createFormNapThe(player, "VINAPHONE", "20000");
                                    break;
                                case 2:
                                    Input.gI().createFormNapThe(player, "VINAPHONE", "50000");
                                    break;
                                case 3:
                                    Input.gI().createFormNapThe(player, "VINAPHONE", "100000");
                                    break;
                                case 4:
                                    Input.gI().createFormNapThe(player, "VINAPHONE", "200000");
                                    break;
                                case 5:
                                    Input.gI().createFormNapThe(player, "VINAPHONE", "500000");
                                    break;
                                case 6:
                                    Input.gI().createFormNapThe(player, "VINAPHONE", "1000000");
                                    break;
                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MOBIFONE) {
                            switch (select) {
                                case 0:
                                    this.npcChat(player, "Hệ thống đang bảo trì!");
                                case 1:
                                    Input.gI().createFormNapThe(player, "MOBIFONE", "20000");
                                    break;
                                case 2:
                                    Input.gI().createFormNapThe(player, "MOBIFONE", "50000");
                                    break;
                                case 3:
                                    Input.gI().createFormNapThe(player, "MOBIFONE", "100000");
                                    break;
                                case 4:
                                    Input.gI().createFormNapThe(player, "MOBIFONE", "200000");
                                    break;
                                case 5:
                                    Input.gI().createFormNapThe(player, "MOBIFONE", "500000");
                                    break;
                                case 6:
                                    Input.gI().createFormNapThe(player, "MOBIFONE", "1000000");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.VIETTEL) {
                            switch (select) {
                                case 0:
                                    this.npcChat(player, "Hệ thống đang bảo trì!");
                                case 1:
                                    Input.gI().createFormNapThe(player, "VIETTEL", "20000");
                                    break;
                                case 2:
                                    Input.gI().createFormNapThe(player, "VIETTEL", "50000");
                                    break;
                                case 3:
                                    Input.gI().createFormNapThe(player, "VIETTEL", "100000");
                                    break;
                                case 4:
                                    Input.gI().createFormNapThe(player, "VIETTEL", "200000");
                                    break;
                                case 5:
                                    Input.gI().createFormNapThe(player, "VIETTEL", "500000");
                                    break;
                                case 6:
                                    Input.gI().createFormNapThe(player, "VIETTEL", "1000000");
                                    break;
                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.QUY_DOI) {
                            switch (select) {
                                case 0:
                                    Input.gI().createFormQDTV(player);
                                    break;
                                case 1:
                                    Input.gI().createFormQDHN(player);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc thodaika(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 5) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "\b|7|Hãy đem đến cho ta:\n+x80 Cá Nóc\t+x80 Cá Bảy Màu\t+x80 Cá Diêu Hồng\t+x30 thỏi vàng\n"
                            + "\b|3|up cá ỡ coler :3"
                            + "\nTa sẽ giúp ngươi lấy được túi quà\n Dùng hộp quà để nấu thỏi cũng đc lun nha :3", "Đổi Hộp Quà", "Nấu\nThỏi vàng");

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item canoc = null;
                                    Item cabaymau = null;
                                    Item cadieuhong = null;

                                    Item thoivang = null;

                                    try {
                                        canoc = InventoryServiceNew.gI().findItemBag(player, 1002);
                                        cabaymau = InventoryServiceNew.gI().findItemBag(player, 1003);
                                        cadieuhong = InventoryServiceNew.gI().findItemBag(player, 1004);

                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (canoc == null || canoc.quantity < 80 || cabaymau == null || cabaymau.quantity < 80 || cadieuhong == null || cadieuhong.quantity < 80) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để đổi quà");
                                    } else if (thoivang == null || thoivang.quantity < 30) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, canoc, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, cabaymau, 80);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, cadieuhong, 80);

                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 30);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 1098);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 Túi quà");
                                    }
                                    break;
                                }
//                                case 1: {
//                                    Item thitheo = null;
//                                    Item thungnep = null;
//                                    Item thungdxanh = null;
//                                    Item ladong = null;
//                                    Item thoivang = null;
//
//                                    try {
//                                        thitheo = InventoryServiceNew.gI().findItemBag(player, 748);
//                                        thungnep = InventoryServiceNew.gI().findItemBag(player, 749);
//                                        thungdxanh = InventoryServiceNew.gI().findItemBag(player, 750);
//                                        ladong = InventoryServiceNew.gI().findItemBag(player, 751);
//                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if (thitheo == null || thitheo.quantity < 99 || thungnep == null || thungnep.quantity < 99 || thungdxanh == null || thungdxanh.quantity < 99 || ladong == null || ladong.quantity < 99) {
//                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");
//                                    } else if (thoivang == null || thoivang.quantity < 5) {
//                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thitheo, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungnep, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thungdxanh, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ladong, 99);
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 5);
//                                        Service.getInstance().sendMoney(player);
//                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 753);
//                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Bạn nhận được 1 bánh chưng");
//                                    }
//                                    break;
//                                }
                                case 1: {
                                    Item canoc = null;

                                    Item thoivang = null;

                                    try {
                                        canoc = InventoryServiceNew.gI().findItemBag(player, 2117);

                                        thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (canoc == null || canoc.quantity < 1) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu vàng");
                                    } else if (thoivang == null || thoivang.quantity < 50) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, canoc, 1);

                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 50);
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 457, 200);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 200 thỏi vàng");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    ShopServiceNew.gI().opendShop(pl, "URON", false);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc baHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Ép sao\ntrang bị", "Pha lê\nhóa\ntrang bị", "Nâng cấp\nĐồ hủy diệt",
                        "Nâng cấp\nBông tai\nPorataCấp3", "Mở chỉ số\nBông tai\nPorataCấp3",
                        "Nâng cấp\nBông tai\nPorataCấp4", "Mở chỉ số\nBông tai\nPorataCấp4","Nâng cải trang","Nâng cấp SKH");
                    } else if (this.mapId == 121) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Về đảo\nrùa");

                    } else {

                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi tìm ta có việc gì?",
                                "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                "Nâng cấp\nBông tai\nPorata", "Mở chỉ số\nBông tai\nPorata",
                                "Nhập\nNgọc Rồng", "Mở chỉ số\nNgọc Hợp Thể","Nâng cấp\nLinh thú");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
//                                               
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                    break;
                                case 2:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_HUY_DIET);
                                    break;
                                
                                case 3: 
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI_CAP3);
                                    break;    
                                case 4: 
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP3);
                                    break;
                                case 5:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI_CAP4);
                                    break;
                                case 6: 
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP4);
                                    break; 
                                case 7:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAI_TRANG);
                                    break; 
                                    case 8:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH);
                                    break; 
                                 case 9:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.AN_TRANG_BI);
                                    break;    
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.EP_SAO_TRANG_BI:
                                case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                case CombineServiceNew.AN_TRANG_BI:
                                case CombineServiceNew.NANG_CAP_HUY_DIET:
                                case CombineServiceNew.NANG_CAP_BONG_TAI_CAP3:
                                case CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP3:
                                case CombineServiceNew.NANG_CAP_BONG_TAI_CAP4:
                                case CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP4:
                                case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                case CombineServiceNew.NANG_CAI_TRANG:
                                     case CombineServiceNew.NANG_CAP_SKH:
                                    switch (select) {
                                        case 0:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 1:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 10;
                                            }
                                            break;
                                        case 2:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 100;
                                            }
                                            break;      
                                    }
                                        CombineServiceNew.gI().startCombine(player);
                            
                            }
                        }
                    } else if (this.mapId == 112) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                    break;
                            }
                        }
                    } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44 || this.mapId == 176) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop bùa
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                            + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                            "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                                    break;
                                case 1:

                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                                    break;
                                case 2: //nâng cấp bông tai
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                                    break;
                                case 3: //làm phép nhập đá
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI);
                                    break;
                                case 4:

                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                                    break;
//                                case 5: //phân rã đồ thần linh
//                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHAN_RA_DO_THAN_LINH);
//
//                                    break;
//                                case 6:
//                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
//                                    break;
                                case 6:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_LINH_THU);
                                    break;
                                 case 5:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP5);
                                    break;   
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1H", true);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "BUA_8H", true);
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1M", true);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                case CombineServiceNew.NANG_CAP_BONG_TAI:
                                case CombineServiceNew.MO_CHI_SO_BONG_TAI:
                                case CombineServiceNew.NHAP_NGOC_RONG:
                                case CombineServiceNew.PHAN_RA_DO_THAN_LINH:
                                case CombineServiceNew.NANG_CAP_DO_TS:
                                case CombineServiceNew.NANG_CAP_SKH_VIP:
                                case CombineServiceNew.MO_CHI_SO_BONG_TAI_CAP5:
                                    case CombineServiceNew.NANG_CAP_LINH_THU:

                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_RA_DO_THAN_LINH) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS) {
                            if (select == 0) {
                                CombineServiceNew.gI().startCombine(player);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc ruongDo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    InventoryServiceNew.gI().sendItemBox(player);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc duongtank(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (mapId == 0) {
                        this.createOtherMenu(player, 0, "A mi khò khò, thí chủ hãy giúp giải cứu đệ tử của bần tăng đang bị phong ấn tại ngũ hành sơn.", "Đồng ý", "Từ chối");
                    }
                    if (mapId == 122) {
                        this.createOtherMenu(player, 0, "Thí chủ muốn quay về làng Aru?", "Đồng ý", "Từ chối");

                    }
                    if (mapId == 124) {
                        this.createOtherMenu(player, 0, "A mi khò khò, ở Ngũ hành sơn có lũ khỉ đã ăn trộm Hồng Đào\b Thí chủ có thể giúp ta lấy lại Hồng Đào từ chúng\bTa sẽ đổi 1 ít đồ để đổi lấy Hồng Đào.", "Cửa hàng", "Về\nLàng Aru", "Từ chối");
                    }
                    if (mapId == 5) {
                        this.createOtherMenu(player, 0, "A mi khò khò, ở Ngũ hành sơn có lũ khỉ đã ăn trộm Hồng Đào\b Thí chủ có thể giúp ta lấy lại Hồng Đào từ chúng\bTa sẽ đổi 1 ít đồ để đổi lấy Hồng Đào.", "Cửa hàng", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (select) {
                        case 0:
                            if (mapId == 0) {
                                if (player.nPoint.power < 10000000L || player.nPoint.power >= 80000000000L) {
                                    this.npcChat(player, "Sức mạnh thí chủ không đủ 10tr sức mạnh để qua Ngũ Hành Sơn!");
                                    return;
                                }
                                ChangeMapService.gI().changeMapBySpaceShip(player, 122, -1, 96);
                            }
                            if (mapId == 122) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 936);
                            }
                            if (mapId == 124) {
                                if (select == 0) {
                                    ShopServiceNew.gI().opendShop(player, "TAYDUKY", true);
                                    break;
                                }
                                if (select == 1) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 936);
                                }
                            }
                             if (mapId == 5) {
                                if (select == 0) {
                                    ShopServiceNew.gI().opendShop(player, "TAYDUKY", true);
                                    break;
                                }
                             }
                            break;
                    }
                }
            }
        };
    }

    public static Npc dauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.magicTree.openMenuTree();
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                if (player.magicTree.level == 10) {
                                    player.magicTree.fastRespawnPea();
                                } else {
                                    player.magicTree.showConfirmUpgradeMagicTree();
                                }
                            } else if (select == 2) {
                                player.magicTree.fastRespawnPea();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUpgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                            if (select == 0) {
                                player.magicTree.upgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_UPGRADE:
                            if (select == 0) {
                                player.magicTree.fastUpgradeMagicTree();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUnuppgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                            if (select == 0) {
                                player.magicTree.unupgradeMagicTree();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            private final byte COUNT_CHANGE = 50;
            private int count;

            private void changeMap() {
                if (this.mapId != 102) {
                    count++;
                    if (this.count >= COUNT_CHANGE) {
                        count = 0;
                        this.map.npcs.remove(this);
                        Map map = MapService.gI().getMapForCalich();
                        if (map != null) {
                            this.mapId = map.mapId;
                            this.cx = Util.nextInt(100, map.mapWidth - 100);
                            this.cy = map.yPhysicInTop(this.cx, 0);
                            this.map = map;
                            this.map.npcs.add(this);
                        }
                    }
                }
            }

            @Override
            public void openBaseMenu(Player player) {
                player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                    Service.gI().hideWaitDialog(player);
                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                    return;
                }
                if (this.mapId != player.zone.map.mapId) {
                    Service.gI().sendThongBao(player, "Calích đã rời khỏi map!");
                    Service.gI().hideWaitDialog(player);
                    return;
                }

                if (this.mapId == 102) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?",
                            "Kể\nChuyện", "Quay về\nQuá khứ");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chào chú, cháu có thể giúp gì?", "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 102) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            //kể chuyện
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                        } else if (select == 1) {
                            //về quá khứ
                            ChangeMapService.gI().goToQuaKhu(player);
                        }
                    }
                } else if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        //kể chuyện
                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    } else if (select == 1) {
                        //đến tương lai
                                    changeMap();
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                            ChangeMapService.gI().goToTuongLai(player);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                }
            }
        };
    }

    public static Npc jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
                    } else if (this.mapId == 139) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        if (player.getSession().player.nPoint.power >= 1000000000L) {

                            ChangeMapService.gI().goToPotaufeu(player);
                        } else {
                            this.npcChat(player, "Bạn chưa đủ 10 tỉ sức mạnh để vào!");
                        }
                    } else if (this.mapId == 139) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về trạm vũ trụ
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

//public static Npc Potage(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 149) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "tét", "Gọi nhân bản");
//                    }
//                }
//            }
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                   if (select == 0){
//                        BossManager.gI().createBoss(-214);
//                   }
//                }
//            }
//        };
//    }
    public static Npc npclytieunuong54(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
//                        if (player.getSession().is_gift_box) {
//} else {
//                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "|7|Số tiền của bạn còn : " + player.getSession().vnd + "\n",
//                                            "Đổi đệ tử");
//                                   
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        if (player.iDMark.isBaseMenu()) {
//                            switch (select) {
//                              case 0:
//                                NpcService.gI().createMenuConMeo(player, ConstNpc.DOI_DTVIP, 564,
//                                        "Ngươi có muốn đổi đệ tử xịn hay không?\n"
//                                                + "|3|Tùy chọn 1: Đệ Berus[30k] : Hợp thể tăng 30% \n|3|"
//                                                + "Tùy chọn 2: Đệ Pic[50k] : Hợp thể tăng 40% \n|3|"
//                                                + "Tùy chọn 3: Đệ Goku vô cực[100k] : Hợp thể tăng 50% \n|3|"
//                                                + "Tùy chọn 4: Đệ Khỉ Whiter Ylerbo[150k] : Hợp thể tăng 60%",
////                                                + "Tùy chọn 5: Đệ goku rose[250k] : Hợp thể tăng 80%",
//                                         "Tùy chọn\n1", "Tùy chọn\n2","Tùy chọn\n3","Tùy chọn\n4","Từ Chối");
//
//                                break;
//                                    }
//                                }
//                            }
//                        }
//            }
//        };
//    }  
 return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
           public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {                   
                  if (this.mapId == 5) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Cậu muốn giúp đỡ tôi à\b tôi sẽ giao cho cậu vài nhiệm vụ của hôm nay.", "Đổi Qùa Nạp","Thông tin\bquà nạp");
                        }
                    }
                }
            

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                               
                                case 0 :
                                    this.createOtherMenu(player, 1,
                                        "\b|1|Ngươi muốn đổi Quà Nạp à?\n|5|Khi quy đổi một mốc, số VND trong hành trang ngươi sẽ bị trừ đi\n|5|Hãy lưu ý đọc kỹ quà nhận trước khi đổi\n|9|Cảm Ơn Bạn Đã Ủng Hộ!!!Mãi Keo:vv"
                                        + "\b|7|Bạn đang có :" + player.getSession().vnd + " VND",
                                        "Nhận Mốc 100k","Nhận Mốc 300k","Nhận Mốc 500k","Nhận Mốc 1TR");
                                break;
                                case 1 :
                                   
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MOC_NAP);
                                        break;
                            }
                        }  
                        else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            
                            case 0:
                                if (player.getSession().vnd < 100000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 100k VND");
                                    return;}
                                 if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 12) {
                                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 12 ô trống hành trang");
                                     return;
                                           }
                                if (PlayerDAO.subvnd(player, 100000)) {
                                    player.getSession().vnd -= 100000;
                                    
                                    Item i0 = ItemService.gI().createNewItem((short) 2000, 50);
                                    Item i1 = ItemService.gI().createNewItem((short) 2001, 50);
                                    Item i2 = ItemService.gI().createNewItem((short) 2002, 50);
                                    Item i3 = ItemService.gI().createNewItem((short) 14, 5);
                                    Item i4 = ItemService.gI().createNewItem((short) 15, 5);
                                    Item i5 = ItemService.gI().createNewItem((short) 16, 5);
                                    Item i6 = ItemService.gI().createNewItem((short) 17, 5);
                                    Item i7 = ItemService.gI().createNewItem((short) 18, 5);
                                    Item i8 = ItemService.gI().createNewItem((short) 20, 5);
                                    Item i9 = ItemService.gI().createNewItem((short) 19, 5);
                                    
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i2);
                                    InventoryServiceNew.gI().addItemBag(player, i4);
                                    InventoryServiceNew.gI().addItemBag(player, i3);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i7);
                                    InventoryServiceNew.gI().addItemBag(player, i8);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 100k, Xin chúc mừng");
                                   } 
//                                else {
//                                 Service.gI().sendThongBao(player, "Bạn phải có ít nhất 11 ô trống trong hành trang.");
//                            }                                 
                                break;
                             case 1:
                                if (player.getSession().vnd < 300000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 300k VND");
                                    return;}
                                if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 16) {
                                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 16 ô trống hành trang");
                                     return;
                                           }
                                if (PlayerDAO.subvnd(player, 300000)) {
                                    player.getSession().vnd -= 300000;
                                    Item i0 = ItemService.gI().createNewItem((short) 1216, 1);
                                    Item i1 = ItemService.gI().createNewItem((short) 1161, 3);
                                    Item i2 = ItemService.gI().createNewItem((short) 1158, 3);
                                    Item i00 = ItemService.gI().createNewItem((short) 1099, 999);
                                    Item i10 = ItemService.gI().createNewItem((short) 1100, 999);
                                    Item i20 = ItemService.gI().createNewItem((short) 1101, 999);
                                    Item i200= ItemService.gI().createNewItem((short) 1102, 999);
                                    Item i3 = ItemService.gI().createNewItem((short) 14, 15);
                                    Item i4 = ItemService.gI().createNewItem((short) 15, 15);
                                    Item i5 = ItemService.gI().createNewItem((short) 16, 15);
                                    Item i6 = ItemService.gI().createNewItem((short) 17, 15);
                                    Item i7 = ItemService.gI().createNewItem((short) 18, 15);
                                    Item i9 = ItemService.gI().createNewItem((short) 19, 15);
                                    Item i8 = ItemService.gI().createNewItem((short) 20, 15);
                                    i0.itemOptions.add(new Item.ItemOption(50, 35));
                                    i0.itemOptions.add(new Item.ItemOption(77, 35));
                                    i0.itemOptions.add(new Item.ItemOption(103, 35));
                                    i0.itemOptions.add(new Item.ItemOption(5, 35));
                                    i0.itemOptions.add(new Item.ItemOption(0, 3500));
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i2);
                                    InventoryServiceNew.gI().addItemBag(player, i00);
                                    InventoryServiceNew.gI().addItemBag(player, i10);
                                    InventoryServiceNew.gI().addItemBag(player, i20);
                                    InventoryServiceNew.gI().addItemBag(player, i200);
                                    InventoryServiceNew.gI().addItemBag(player, i4);
                                    InventoryServiceNew.gI().addItemBag(player, i3);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i7);
                                    InventoryServiceNew.gI().addItemBag(player, i8);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 300k, Xin chúc mừng");
                                 } 
                                  
                                break; 
                             case 2 :
                                if (player.getSession().vnd < 500000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 500k VND");
                                    return;}
                                if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 17) {
                                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 17 ô trống hành trang");
                                     return;
                                           }
                                if (PlayerDAO.subvnd(player, 500000)) {
                                    player.getSession().vnd -= 500000;
                                    Item i0 = ItemService.gI().createNewItem((short) 956, 50);
                                    Item i1 = ItemService.gI().createNewItem((short) 1162, 3);
                                    Item i4 = ItemService.gI().createNewItem((short) 1243, 1);
                                    Item i5 = ItemService.gI().createNewItem((short) 220, 2000);
                                    Item i6 = ItemService.gI().createNewItem((short) 221, 2000);
                                    Item i7 = ItemService.gI().createNewItem((short) 222, 2000);
                                    Item i8 = ItemService.gI().createNewItem((short) 223, 2000);
                                    Item i9 = ItemService.gI().createNewItem((short) 16, 50);
                                    Item i10 = ItemService.gI().createNewItem((short) 1276, 1);
                                    i10.itemOptions.add(new Item.ItemOption(50, 40));
                                    i10.itemOptions.add(new Item.ItemOption(77, 40));
                                    i10.itemOptions.add(new Item.ItemOption(103, 40));
                                    i10.itemOptions.add(new Item.ItemOption(5, 40));
                                    i10.itemOptions.add(new Item.ItemOption(95, 15));
                                    i10.itemOptions.add(new Item.ItemOption(96, 15));
                                    i10.itemOptions.add(new Item.ItemOption(0, 5000));                                 
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i4);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i7);
                                    InventoryServiceNew.gI().addItemBag(player, i8);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().addItemBag(player, i10);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 500k, Xin chúc mừng");
                                } 
                               
                                break; 
                                case 3:
                                if (player.getSession().vnd < 1000000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 1TR VND");
                                    return;
                                }
                                if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 13) {
                                    Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 13 ô trống hành trang");
                                     return;
                                           }
                                if (PlayerDAO.subvnd(player, 1000000)) {
                                    player.getSession().vnd -= 1000000;
                                    Item i0 = ItemService.gI().createNewItem((short) 956, 100);
                                    Item i1 = ItemService.gI().createNewItem((short) 1138, 100);
                                    Item i3 = ItemService.gI().createNewItem((short) 1242, 1);
                                    Item i5 = ItemService.gI().createNewItem((short) 1243, 1);
                                    Item i6 = ItemService.gI().createNewItem((short) 16, 99);
                                    Item i9 = ItemService.gI().createNewItem((short) 1277, 1);
                                    i9.itemOptions.add(new Item.ItemOption(50, 45));
                                    i9.itemOptions.add(new Item.ItemOption(77, 45));
                                    i9.itemOptions.add(new Item.ItemOption(103, 45));
                                    i9.itemOptions.add(new Item.ItemOption(0, 7200));
                                    i9.itemOptions.add(new Item.ItemOption(95, 15));
                                    i9.itemOptions.add(new Item.ItemOption(96, 15));
                                    i9.itemOptions.add(new Item.ItemOption(5, 45));                                  
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i3);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 1TR, Xin chúc mừng");
                               } 
                             break;
                        }
                        
                        }
                    }
                }
            }
        };
    }
    public static Npc thuongDe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 45) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào","Den Kaio", "Quay số\nmay mắn");
                    }                    
                    if (this.mapId == 141 ) {
                        this.createOtherMenu(player, 0,
                                "Con muốn gì nào?", "Quay về");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {                                      
                    if (this.mapId == 141) {
                        switch (select) {
                            case 0: // quay ve
                                ChangeMapService.gI().changeMapBySpaceShip(player, 50, -1, 354);
                                break;
                        }
                    }
                    if (this.mapId == 45) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                            "Con muốn làm gì nào?", "Quay bằng\nvàng",
                                            "Rương phụ\n("
                                            + (player.inventory.itemsBoxCrackBall.size()
                                            - InventoryServiceNew.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                            + " món)",
                                            "Xóa hết\ntrong rương", "Đóng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                            switch (select) {
                                case 0:
                                    LuckyRound.gI().openCrackBallUI(player, LuckyRound.USING_GOLD);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "ITEMS_LUCKY_ROUND", true);
                                    break;
                                case 2:
                                    NpcService.gI().createMenuConMeo(player,
                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                            "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                            + "sẽ không thể khôi phục!",
                                            "Đồng ý", "Hủy bỏ");
                                    break;
                            }
                        }
                    }

                }
            }
        };
    }
    public static Npc thanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Di chuyển");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMap(player, 141, -1, 318, 336);//con đường rắn độc
                                    break;
                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
           @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    mabu2h.gI().setTimeJoinmabu2h();} 
                            if (this.mapId == 52) {                                               
                                long now = System.currentTimeMillis();
                                if (now > mabu2h.TIME_OPEN_2h && now < mabu2h.TIME_CLOSE_2h) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB,   "Ma Bư đã hồi sinh hãy giải cứu Piccolo",
                                           "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                        "Xin hãy cứu lấy người dân",
                                             "Hướng dẫn", "Từ chối");
                                }                           
                            }
                        }    
            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                   switch (this.mapId) {
                        case 52:
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.MENU_REWARD_MMB:
                            case ConstNpc.MENU_OPEN_MMB:
                                    if (select == 0)
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_2h);
                                    if (!player.getSession().actived) {                                   
                                    }
                                   if (select == 1){
                                        ChangeMapService.gI().changeMap(player, 127, 0, 66, 312);
                                        break;
                                                            }
                                break;
                            case ConstNpc.MENU_NOT_OPEN_BDW:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_2h);
                                }
                                break;
                               }
                          }
                      }
                  }    
              };
           }

    public static Npc osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn đi đến Vùng Đất của các Vị Thần ?",
                                 "Đến \nVùng Đất của Thần", "Hướng dẫn");
                    } else if (this.mapId == 154) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn đi về Trái Đất hay đi đến Thung Lũng Hủy Diệt?",
                                "Về \nSiêu Thị","Đến\n Thung Lũng Hủy Diệt", "Đến\n Hành tinh\nNgục tù", "Hướng dẫn");
                    } else if (this.mapId == 176) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp ngươi trở về Vùng Đất của các Vị Thần ?",
                                "Quay về\n Vùng Đất của Thần", "Tạm biệt");
                    } else if (this.mapId == 155) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp ngươi trở về Vùng Đất của các Vị Thần ?",
                                "Quay về\n Vùng Đất của Thần", "Tạm biệt");
                    } else if (this.mapId == 52) {
                        try {
                            MapMaBu.gI().setTimeJoinMapMaBu();
                            if (this.mapId == 52) {
                                long now = System.currentTimeMillis();
                                if (now > MapMaBu.TIME_OPEN_MABU && now < MapMaBu.TIME_CLOSE_MABU) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Đại chiến Ma Bư đã mở, "
                                            + "ngươi có muốn tham gia không?",
                                            "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }

                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu osin");
                        }

                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX) {
                            this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Lên Tầng!", "Quay về", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Quay về", "Từ chối");
                        }
                    } else if (this.mapId == 120) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 154, -1, 624, 432);
                                    break;
                                case 1:
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.NPC_OSIN);
                                    break;
                            }
                        }
                    } else if (this.mapId == 154) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, 1416);
                                    break;
                                case 1:
                                     if (player.getSession().player.nPoint.power >= 80000000000L) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 176, -1, 96);
                                     } else {
                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                              }
                                    break;
                                case 2:
                                    if (player.getSession().player.nPoint.power >= 80000000000L) {

                                    ChangeMapService.gI().changeMapBySpaceShip(player, 155, -1, 144);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 80 tỷ sức mạnh để vào");
                              }

                                    break;
                            }
                        }
                    } else if (this.mapId == 176) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 154, -1, 624, 432);
                            }
                        }
                        } else if (this.mapId == 155) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 154, -1, 624, 432);
                            }
                        }
                    } else if (this.mapId == 52) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.MENU_REWARD_MMB:
                                break;
                            case ConstNpc.MENU_OPEN_MMB:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                } else if (select == 1) {
//                                    if (!player.getSession().actived) {
//                                        Service.gI().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                    } else
                                    ChangeMapService.gI().changeMap(player, 114, -1, 318, 336);
                                }
                                break;
                            case ConstNpc.MENU_NOT_OPEN_BDW:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                }
                                break;
                        }
                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.GO_UPSTAIRS_MENU) {
                            if (select == 0) {
                                player.fightMabu.clear();
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            } else if (select == 1) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        } else {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    } else if (this.mapId == 120) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    }
                }
            }
        };
    }

      public static Npc linhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                        return;
                    }
                    if (player.clan.getMembers().size() < DoanhTrai.N_PLAYER_CLAN) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
                        return;
                    }
                    if (player.clan.doanhTrai != null) {
                        createOtherMenu(player, ConstNpc.MENU_OPENED_DOANH_TRAI,
                                "Bang hội của ngươi đang đánh trại độc nhãn\n"
                                + "Thời gian còn lại là "
                                + TimeUtil.getSecondLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000)
                                + " phút. Ngươi có muốn tham gia không?",
                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    int nPlSameClan = 0;
                    for (Player pl : player.zone.getPlayers()) {
                        if (!pl.equals(player) && pl.clan != null
                                && pl.clan.equals(player.clan) && pl.location.x >= 1285
                                && pl.location.x <= 1645) {
                            nPlSameClan++;
                        }
                    }
                    if (nPlSameClan < DoanhTrai.N_PLAYER_MAP) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi phải có ít nhất " + DoanhTrai.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\n"
                                + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Doanh trại chỉ cho phép những người ở trong bang trên 1 ngày. Hẹn ngươi quay lại vào lúc khác",
                                "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clan.haveGoneDoanhTrai) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội của ngươi đã đi trại lúc " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm:ss") + " hôm nay. Người mở\n"
                                + "(" + player.clan.playerOpenDoanhTrai + "). Hẹn ngươi quay lại vào ngày mai", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                            "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\n"
                            + "không?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                            "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI:
                            if (select == 0) {
                                if (player.clan.doanhTrai != null && TimeUtil.getMinLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000) == 0)
                                {
                                    Service.getInstance().sendThongBao(player, "Hết 30p gòi, đợi mai đê !!!!");
                                }else if (player.clan.doanhTrai == null)
                                {
                                    DoanhTraiService.gI().joinDoanhTrai(player);
                                }else if (player.clan.doanhTrai != null && TimeUtil.getMinLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000)>0)
                                {
                                    ChangeMapService.gI().changeMapInYard(player, 53, -1, 60);

                                }
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        case ConstNpc.IGNORE_MENU:
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                    }
                }
            }
        };
    }
    public static Npc quaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_AP_TRUNG_NHANH = 1000000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == (21 + player.gender)) {
                        player.mabuEgg.sendMabuEgg();
                        if (player.mabuEgg.getSecondDone() != 0) {
                            this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                    "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                        } else {
                            this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                        }
                    }
                    if (this.mapId == 154) {
                        player.billEgg.sendBillEgg();
                        if (player.billEgg.getSecondDone() != 0) {
                            this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                    "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                        } else {
                            this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == (21 + player.gender)) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.CAN_NOT_OPEN_EGG:
                                if (select == 0) {
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                } else if (select == 1) {
                                    if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                        player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                        player.mabuEgg.timeDone = 0;
                                        Service.gI().sendMoney(player);
                                        player.mabuEgg.sendMabuEgg();
                                    } else {
                                        Service.gI().sendThongBao(player,
                                                "Bạn không đủ vàng để thực hiện, còn thiếu "
                                                + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                    }
                                }
                                break;
                            case ConstNpc.CAN_OPEN_EGG:
                                switch (select) {
                                    case 0:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                                "Bạn có chắc chắn cho trứng nở?\n"
                                                + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                                "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                        break;
                                    case 1:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_OPEN_EGG:
                                switch (select) {
                                    case 0:
                                        player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                        break;
                                    case 1:
                                        player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                        break;
                                    case 2:
                                        player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_DESTROY_EGG:
                                if (select == 0) {
                                    player.mabuEgg.destroyEgg();
                                }
                                break;
                        }
                    }
                    if (this.mapId == 154) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.CAN_NOT_OPEN_BILL:
                                if (select == 0) {
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_BILL,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                } else if (select == 1) {
                                    if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                        player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                        player.billEgg.timeDone = 0;
                                        Service.gI().sendMoney(player);
                                        player.billEgg.sendBillEgg();
                                    } else {
                                        Service.gI().sendThongBao(player,
                                                "Bạn không đủ vàng để thực hiện, còn thiếu "
                                                + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                    }
                                }
                                break;
                            case ConstNpc.CAN_OPEN_EGG:
                                switch (select) {
                                    case 0:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_BILL,
                                                "Bạn có chắc chắn cho trứng nở?\n"
                                                + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                                "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                        break;
                                    case 1:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_BILL,
                                                "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_OPEN_BILL:
                                switch (select) {
                                    case 0:
                                        player.billEgg.openEgg(ConstPlayer.TRAI_DAT);
                                        break;
                                    case 1:
                                        player.billEgg.openEgg(ConstPlayer.NAMEC);
                                        break;
                                    case 2:
                                        player.billEgg.openEgg(ConstPlayer.XAYDA);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_DESTROY_BILL:
                                if (select == 0) {
                                    player.billEgg.destroyEgg();
                                }
                                break;
                        }
                    }

                }
            }
        };
    }

    public static Npc quocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                        "Bản thân", "Đệ tử", "Từ chối");
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                            + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                            "Nâng\ngiới hạn\nsức mạnh",
                                            "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Sức mạnh của con đã đạt tới giới hạn",
                                            "Đóng");
                                }
                                break;
                            case 1:
                                if (player.pet != null) {
                                    if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                "Đóng");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                                }
                                //giới hạn đệ tử
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
                        switch (select) {
                            case 0:
                                OpenPowerService.gI().openPowerBasic(player);
                                break;
                            case 1:
                                if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                    if (OpenPowerService.gI().openPowerSpeed(player)) {
                                        player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                        Service.gI().sendMoney(player);
                                    }
                                } else {
                                    Service.gI().sendThongBao(player,
                                            "Bạn không đủ vàng để mở, còn thiếu "
                                            + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                        if (select == 0) {
                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                    Service.gI().sendMoney(player);
                                }
                            } else {
                                Service.gI().sendThongBao(player,
                                        "Bạn không đủ vàng để mở, còn thiếu "
                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc bulmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
                        }
                    } else if (this.mapId == 104) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_FUTURE", true);
                            }
                        }
                    } else if (this.mapId == 104) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_LINHTHU", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc rongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    BlackBallWar.gI().setTime();
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        try {
                            long now = System.currentTimeMillis();
                            if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW, "Đường đến với ngọc rồng sao đen đã mở, "
                                        + "ngươi có muốn tham gia không?",
                                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                            } else {
                                String[] optionRewards = new String[7];
                                int index = 0;
                                for (int i = 0; i < 7; i++) {
                                    if (player.rewardBlackBall.timeOutOfDateReward[i] > System.currentTimeMillis()) {
                                        String quantily = player.rewardBlackBall.quantilyBlackBall[i] > 1 ? "x" + player.rewardBlackBall.quantilyBlackBall[i] + " " : "";
                                        optionRewards[index] = quantily + (i + 1) + " sao";
                                        index++;
                                    }
                                }
                                if (index != 0) {
                                    String[] options = new String[index + 1];
                                    for (int i = 0; i < index; i++) {
                                        options[i] = optionRewards[i];
                                    }
                                    options[options.length - 1] = "Từ chối";
                                    this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW, "Ngươi có một vài phần thưởng ngọc "
                                            + "rồng sao đen đây!",
                                            options);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }
                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu rồng Omega");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_REWARD_BDW:
                            player.rewardBlackBall.getRewardSelect((byte) select);
                            break;
                        case ConstNpc.MENU_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            } else if (select == 1) {
//                                if (!player.getSession().actived) {
//                                    Service.gI().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//
//                                } else
                                player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                ChangeMapService.gI().openChangeMapTab(player);
                            }
                            break;
                        case ConstNpc.MENU_NOT_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            }
                            break;
                    }
                }
            }

        };
    }

    public static Npc rong1_to_7s(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isHoldBlackBall()) {
                        this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?", "Phù hộ", "Từ chối");
                    } else {
                        if (BossManager.gI().existBossOnPlayer(player)
                                || player.zone.items.stream().anyMatch(itemMap -> ItemMapService.gI().isBlackBall(itemMap.itemTemplate.id))
                                || player.zone.getPlayers().stream().anyMatch(p -> p.iDMark.isHoldBlackBall())) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối", "Gọi BOSS");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                    "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                    "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                    "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                    "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                    "Từ chối"
                            );
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                        } else if (select == 2) {
                            BossManager.gI().callBoss(player, mapId);
                        } else if (select == 1) {
                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                        if (player.effectSkin.xHPKI > 1) {
                            Service.gI().sendThongBao(player, "Bạn đã được phù hộ rồi!");
                            return;
                        }
                        switch (select) {
                            case 0:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                break;
                            case 1:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                break;
                            case 2:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                break;
                            case 3:
                                this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Đói bụng quá.. ngươi mang cho ta 99 phần đồ ăn,\nta sẽ cho một món đồ Hủy Diệt.\n Nếu tâm trạng ta vui ngươi có thể nhận được trang bị\ntăng đến 15%!",
                            "OK", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 48:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:
                                   if (select == 0){
                                        Item pudding = InventoryServiceNew.gI().findItemBag(player, 663);
                                       Item xucxich = InventoryServiceNew.gI().findItemBag(player, 664);
                                       Item  kemdau = InventoryServiceNew.gI().findItemBag(player, 665);
                                        Item mily = InventoryServiceNew.gI().findItemBag(player, 666);
                                        Item sushi = InventoryServiceNew.gI().findItemBag(player, 667);
                                        if ( pudding != null && pudding.quantity >= 99 ||
                                                xucxich != null && xucxich.quantity >= 99||
                                                kemdau != null && kemdau.quantity  >=99||
                                                mily != null && mily.quantity >= 99 ||
                                                sushi !=null && sushi.quantity >= 99) {
                                        ShopServiceNew.gI().opendShop(player, "HUY_DIET", true);
                                        break;
                                        }
                                        else {
                                            this.npcChat(player, "Còn không mau đem x99 thức ăn đến cho ta !!");
                                            break;
                                        }
                                    }
                            }
                            break;
                    }
                }
            }
        };
    }
    
    public static Npc whis(int mapId, int status, int cx, int cy, int tempId, int avartar) {
    return new Npc(mapId, status, cx, cy, tempId, avartar) {
        @Override
        public void openBaseMenu(Player player) {
            if (this.mapId == 154) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, "Thử đánh với ta xem nào.\nNgươi còn 1 lượt cơ mà.!",
                        "Chế tạo TBTS", "Học \nTuyệt kỹ", "Hướng dẫn");
            }
        }

        @Override
        public void confirmMenu(Player player, int select) {
            if (canOpenNpc(player)) {
                if (player.iDMark.isBaseMenu() && this.mapId == 154) {
                    switch (select) {
                        case 0:
                            this.createOtherMenu(player, 5, "Ta sẽ giúp ngươi chế tạo trang bị thiên sứ","Cửa hàng", "Chế tạo", "Đóng");
                            break;
                        case 1:
                            Item BiKiepTuyetKy = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1125);
                            if (BiKiepTuyetKy != null) {
                            this.createOtherMenu(player, 6, "Ta sẽ giúp ngươi học tuyệt kỹ: %skill \nBí kiếp tuyệt kỹ: " + BiKiepTuyetKy.quantity + "/999\nGiá vàng: 1.500.000.000\nGiá ngọc: 99999",
                                    "Đồng ý\nHọc", "Từ chối");
                            }
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == 5) {
                    switch (select) {
                        case 0:
                            ShopServiceNew.gI().opendShop(player, "THIEN_SU", false);
                            break;
                        case 1:
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.CHE_TAO_TRANG_BI_TS);
                            break;
                    }
                } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DAP_DO) {
                    if (select == 0) {
                        CombineServiceNew.gI().startCombine(player);
                } else if (player.iDMark.getIndexMenu() == 6) {
                    switch (select) {
                        case 0:
                              Item BiKiepTuyetKy = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1125);
                            if (BiKiepTuyetKy.quantity >= 9999){
                                switch(player.gender){
//                                    
//                                SkillService.gI().learSkillSpecial(player, Skill.SUPER_KAME);
//                                SkillService.gI().learSkillSpecial(player, Skill.MA_PHONG_BA);
//                                SkillService.gI().learSkillSpecial(player, Skill.LIEN_HOAN_CHUONG);
//                                InventoryServiceNew.gI().subQuantityItem(player.inventory.itemsBag, BiKipTuyetKi, 999);
//                                InventoryServiceNew.gI().sendItemBags(player);
//                            }
//                            Service.gI().sendThongBao(player, "Chưa có đủ bí kíp tuyệt kĩ");
//                            return;
                                    case 0:
                                        Service.gI().sendThongBao(player, "Trái đất lè");
                                        break;
                                    case 1:
                                        Service.gI().sendThongBao(player, "Namek lè");
                                        break;
                                    case 2:
                                        Service.gI().sendThongBao(player, "Xaday lè");
                                        break;
                                        
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Con không đủ bí kíp tuyệt kỹ , hãy luyện tập để mạnh hơn");
                                break;
                            }
                            break;
                       
                    }
        }
                }
            }
        }
    };
}


    public static Npc boMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (this.mapId == 47 || this.mapId == 84) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Cậu muốn giúp đỡ tôi à\b tôi sẽ giao cho cậu vài nhiệm vụ của hôm nay.", "Nhiệm vụ\nhàng ngày","Thành Tựu");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.playerTask.sideTask.template != null) {
                                        String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.sideTask.getName() + " ("
                                                + player.playerTask.sideTask.getLevel() + ")"
                                                + "\nHiện tại đã hoàn thành: " + player.playerTask.sideTask.count + "/"
                                                + player.playerTask.sideTask.maxCount + " ("
                                                + player.playerTask.sideTask.getPercentProcess() + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                                + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                "Tôi có vài nhiệm vụ theo cấp bậc, "
                                                + "sức cậu có thể làm được cái nào?",
                                                "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa ngục", "Từ chối");
                                    }
                                    break;
                                case 1 :
                                    ThanhTichPlayer.SendThanhTich(player);
                                    break;
                                    
                                case 2 :
                                    this.createOtherMenu(player, 2,
                                        "\b|1|Ngươi muốn đổi Quà Nạp à?\n|5|Khi quy đổi một mốc, số VND trong hành trang ngươi sẽ bị trừ đi\n|5|Hãy lưu ý đọc kỹ quà nhận trước khi đổi\n|9|Cảm Ơn Bạn Đã Ủng Hộ!!!Mãi Keo:vv"
                                        + "\b|7|Bạn đang có :" + player.getSession().vnd + " VND",
                                        "Nhận Mốc 100k","Nhận Mốc 300k","Nhận Mốc 500k","Nhận Mốc 1000TR");
                                break;
                                case 3 :
                                   
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MOC_NAP);
                                        break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    TaskService.gI().changeSideTask(player, (byte) select);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                    TaskService.gI().paySideTask(player);
                                    break;
                                case 1:
                                    TaskService.gI().removeSideTask(player);
                                    break;
                            }
                         
                        }
                        else if (player.iDMark.getIndexMenu() == 2) {
                        switch (select) {
                            
                            case 0:
                                if (player.getSession().vnd < 100000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 100k VND");
                                    return;}
                                if (PlayerDAO.subvndBar(player, 100000)) {
                                    player.getSession().vnd -= 100000;
                                    
                                    Item i0 = ItemService.gI().createNewItem((short) 2001, 50);
                                    Item i1 = ItemService.gI().createNewItem((short) 2002, 50);
                                    Item i2 = ItemService.gI().createNewItem((short) 2003, 50);
                                    Item i3 = ItemService.gI().createNewItem((short) 14, 5);
                                    Item i4 = ItemService.gI().createNewItem((short) 15, 5);
                                    Item i5 = ItemService.gI().createNewItem((short) 16, 5);
                                    Item i6 = ItemService.gI().createNewItem((short) 17, 5);
                                    Item i7 = ItemService.gI().createNewItem((short) 18, 5);
                                    Item i8 = ItemService.gI().createNewItem((short) 20, 5);
                                    Item i9 = ItemService.gI().createNewItem((short) 19, 5);
                                    
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i2);
                                    InventoryServiceNew.gI().addItemBag(player, i4);
                                    InventoryServiceNew.gI().addItemBag(player, i3);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i7);
                                    InventoryServiceNew.gI().addItemBag(player, i8);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    //Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 100k, Xin chúc mừng");
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i0.template.name);
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i1.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i2.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i3.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i4.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i5.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i6.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i7.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i8.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i9.template.name); 
                                Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 11 ô trống trong hành trang.");
                                }   break;
                             case 1:
                                if (player.getSession().vnd < 300000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 300k VND");
                                    return;}
                                if (PlayerDAO.subvndBar(player, 300000)) {
                                    player.getSession().vnd -= 300000;
                                    Item i0 = ItemService.gI().createNewItem((short) 1216, 1);
                                    Item i1 = ItemService.gI().createNewItem((short) 1161, 3);
                                    Item i2 = ItemService.gI().createNewItem((short) 1158, 3);
                                    Item i00 = ItemService.gI().createNewItem((short) 1099, 999);
                                    Item i10 = ItemService.gI().createNewItem((short) 1100, 999);
                                    Item i20 = ItemService.gI().createNewItem((short) 1101, 999);
                                    Item i200= ItemService.gI().createNewItem((short) 1102, 999);
                                    Item i3 = ItemService.gI().createNewItem((short) 14, 15);
                                    Item i4 = ItemService.gI().createNewItem((short) 15, 15);
                                    Item i5 = ItemService.gI().createNewItem((short) 16, 15);
                                    Item i6 = ItemService.gI().createNewItem((short) 17, 15);
                                    Item i7 = ItemService.gI().createNewItem((short) 18, 15);
                                    Item i9 = ItemService.gI().createNewItem((short) 19, 15);
                                    Item i8 = ItemService.gI().createNewItem((short) 20, 15);
                                    i0.itemOptions.add(new Item.ItemOption(50, 35));
                                    i0.itemOptions.add(new Item.ItemOption(77, 35));
                                    i0.itemOptions.add(new Item.ItemOption(103, 35));
                                    i0.itemOptions.add(new Item.ItemOption(5, 35));
                                    i0.itemOptions.add(new Item.ItemOption(0, 3500));
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i2);
                                    InventoryServiceNew.gI().addItemBag(player, i00);
                                    InventoryServiceNew.gI().addItemBag(player, i10);
                                    InventoryServiceNew.gI().addItemBag(player, i20);
                                    InventoryServiceNew.gI().addItemBag(player, i200);
                                    InventoryServiceNew.gI().addItemBag(player, i4);
                                    InventoryServiceNew.gI().addItemBag(player, i3);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i7);
                                    InventoryServiceNew.gI().addItemBag(player, i8);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    //Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 300k, Xin chúc mừng");
                                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i0.template.name);
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i1.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i2.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i00.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i10.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i20.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i200.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i3.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i4.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i5.template.name);
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i6.template.name);
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i7.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i8.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i9.template.name);              
                                Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 15 ô trống trong hành trang.");
                                }   break; 
                             case 2 :
                                if (player.getSession().vnd < 500000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 500k VND");
                                    return;}
                                if (PlayerDAO.subvndBar(player, 500000)) {
                                    player.getSession().vnd -= 500000;
                                    Item i0 = ItemService.gI().createNewItem((short) 956, 50);
                                    Item i1 = ItemService.gI().createNewItem((short) 1162, 3);
                                    Item i4 = ItemService.gI().createNewItem((short) 1243, 1);
                                    Item i5 = ItemService.gI().createNewItem((short) 220, 2000);
                                    Item i6 = ItemService.gI().createNewItem((short) 221, 2000);
                                    Item i7 = ItemService.gI().createNewItem((short) 222, 2000);
                                    Item i8 = ItemService.gI().createNewItem((short) 223, 2000);
                                    Item i9 = ItemService.gI().createNewItem((short) 16, 50);
                                    Item i10 = ItemService.gI().createNewItem((short) 1276, 1);
                                    i10.itemOptions.add(new Item.ItemOption(50, 40));
                                    i10.itemOptions.add(new Item.ItemOption(77, 40));
                                    i10.itemOptions.add(new Item.ItemOption(103, 40));
                                    i10.itemOptions.add(new Item.ItemOption(5, 40));
                                    i10.itemOptions.add(new Item.ItemOption(95, 15));
                                    i10.itemOptions.add(new Item.ItemOption(96, 15));
                                    i10.itemOptions.add(new Item.ItemOption(0, 5000));                                 
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i4);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i7);
                                    InventoryServiceNew.gI().addItemBag(player, i8);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().addItemBag(player, i10);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    //Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 500k, Xin chúc mừng");
                                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i0.template.name);
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i1.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i4.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i5.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i6.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i7.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i8.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i9.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i10.template.name); 
                                     
                               Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 16 ô trống trong hành trang.");
                                }   break; 
                                case 3:
                                if (player.getSession().vnd < 1000000) {
                                    Service.gI().sendThongBao(player, "Bạn không tích đủ mốc 1000TR VND");
                                    return;}
                                if (PlayerDAO.subvndBar(player, 1000000)) {
                                    player.getSession().vnd -= 1000000;
                                    Item i0 = ItemService.gI().createNewItem((short) 956, 100);
                                    Item i1 = ItemService.gI().createNewItem((short) 1138, 100);
                                    Item i3 = ItemService.gI().createNewItem((short) 1242, 1);
                                    Item i5 = ItemService.gI().createNewItem((short) 1243, 99);
                                    Item i6 = ItemService.gI().createNewItem((short) 16, 99);
                                    Item i9 = ItemService.gI().createNewItem((short) 1277, 1);
                                    i9.itemOptions.add(new Item.ItemOption(50, 45));
                                    i9.itemOptions.add(new Item.ItemOption(77, 45));
                                    i9.itemOptions.add(new Item.ItemOption(103, 45));
                                    i9.itemOptions.add(new Item.ItemOption(0, 7200));
                                    i9.itemOptions.add(new Item.ItemOption(95, 15));
                                    i9.itemOptions.add(new Item.ItemOption(96, 15));
                                    i9.itemOptions.add(new Item.ItemOption(5, 45));                                  
                                    InventoryServiceNew.gI().addItemBag(player, i0);
                                    InventoryServiceNew.gI().addItemBag(player, i1);
                                    InventoryServiceNew.gI().addItemBag(player, i3);
                                    InventoryServiceNew.gI().addItemBag(player, i5);
                                    InventoryServiceNew.gI().addItemBag(player, i6);
                                    InventoryServiceNew.gI().addItemBag(player, i9);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                   // Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 1000k, Xin chúc mừng");
                                    Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i0.template.name);
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i1.template.name);                                     
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i3.template.name);                                     
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i5.template.name); 
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i6.template.name);             
                                     Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + i9.template.name); 
                                Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 10 ô trống trong hành trang.");
                                    
                                     
                            
                                } break;
                        }
                        
                        }
                    }
                }
            }
        };
    }
                    
    public static Npc karin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
       return new Npc(mapId, status, cx, cy, tempId, avartar) {
        public void Npcchat(Player player) {
                String[] chat = {
                    "Giúp Ta đẫn Mị Nương Về Nha",
                    "Em buông tay anh vì lí do gì ",
                    "Người hãy nói đi , đừng Bắt Anh phải nghĩ suy"
                };
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    int index = 0;

                    @Override
                    public void run() {
                        npcChat(player, chat[index]);
                        index = (index + 1) % chat.length;
                    }
                }, 6000, 6000);
            }
            @Override
              public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                               "Mị nương đang đi lạc ngươi hãy giúp ta đưa nàng đến noi chỉ định \n Ta trao thưởng quà Hậu hĩnh,"
                                        
                                        , "Hướng dẫn\n Hộ Tống mị", "Hộ Tống" ,"Đóng");
                        
                
                }

            }                                   
                                    
                                    
            @Override
         
              public void confirmMenu(Player player, int select) {
                   Npcchat(player);
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) { 
                           switch (select) {
                                case 0: 
                             NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_THAN_MEO);
                                case 1:
                                    
                                     if (player.getSession().actived) {   
                                Boss oldDuongTank = BossManager.gI().getBossById(Util.createIdDuongTank((int) player.id));
                                if (oldDuongTank != null) {
                                    this.npcChat(player, " Mị Nương đang được hộ tống" + oldDuongTank.zone.zoneId);
                                }
                                else if (player.inventory.ruby < 250) {
                                   this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Không đủ Hồng Ngọc");
                                } else 
                                {
                                    Item honLinhThu = null;
                                    try {
                                        honLinhThu = InventoryServiceNew.gI().findItemBag(player, 2030);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu == null || honLinhThu.quantity < 1) {
                                        this.npcChat(player, "Bạn không đủ 1 Đá Ma Thuật ");                                                                                                        
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 1);               
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    }
                                    BossData bossDataClone = new BossData(
                                            "Mị nương do" +" "+ player.name + " hộ tống",
                                            (byte) 2,
                                            new short[]{841, 842, 843, -1, -1, -1},
                                            100000,
                                            new long[]{player.nPoint.hpMax * 2},
                                            new int[]{103},
                                            new int[][]{
                                            {Skill.TAI_TAO_NANG_LUONG, 1, 10000}},
                                            new String[]{}, //text chat 1
                                            new String[]{}, //text chat 2
                                            new String[]{}, //text chat 3
                                            60
                                    );

                                    try {
                                        DuongTank dt = new DuongTank(Util.createIdDuongTank((int) player.id), bossDataClone, player.zone, player.location.x - 20, player.location.y);
                                        dt.playerTarger = player;
                                        int[] map = {64,65,66,92,96,100,103};
                                        dt.mapCongDuc = map[Util.nextInt(map.length)];
                                        player.haveDuongTang = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //trừ vàng khi gọi boss
                                    player.inventory.ruby -= 250;
                                    Service.getInstance().sendMoney(player);
                                break;
                                }
                                {
                                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thiếu vật phẩm hộ tống");
                                        }
                                    }
                               
                                break;
//                            case 2:
//                                    ShopServiceNew.gI().opendShop(player, "MI", true);
//                                    break;
                                                                                 
                            } 
                    }
                }
            }
        }
     };
     }   
    public static Npc vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|2|Ta Vừa Hắc Mắp Xêm Được T0p Của Toàn Server\b|7|Mi Muống Xem Tóp Gì?",
                            "Top Sức Mạnh", "Top Nhiệm Vụ", "Top Nạp","Top Săn boss");
                }
            }

            @Override
            public void confirmMenu(Player player,int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:
                                    if (select == 0) {
                                        Service.gI().showListTop(player, Manager.topSM);
                                        break;
                                    }
                                    if (select == 1) {
                                        Service.gI().showListTop(player, Manager.topNV);
                                        break;
                                    }
                                    if (select == 2) {
                                        Service.getInstance().sendThongBaoOK(player,TopService.getTopNap());
                                        break;
                                    }
                                    if (select == 3) {
                                        Service.getInstance().sendThongBaoOK(player,TopService.getTopSB());
                                        break;
                                    }
                                    break;
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//      return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
//                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "-Sự Kiện Quốc Khánh 2/9-\nx3000 Sao Biển, 1.5tỉ vàng: Nhận Hồn Pic(40%)\nx5000 Con Cua, 1.5tỉ vàng: Nhận Hồn Cumber(60%)\nx4000 Vò Sò, 1.5tỉ vàng: Hồn Goku(50%)\nx200 Vỏ Ốc: Nhận Ngọc rồng ngẫu nhiên(1s-7s)", "Sao Biển", "Con Cua","Vỏ Sò","Vỏ Ốc");
//                        }                  
//                    }
//                }
//            }
//
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 5) {
//                        if (player.iDMark.isBaseMenu()) {
//                            if (select == 0) {
//                                Item SaoBien = null;
//                                    try {
//                                        SaoBien = InventoryServiceNew.gI().findItemBag(player, 698);
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if (SaoBien == null || SaoBien.quantity < 3000) {
//                                        this.npcChat(player, "Bạn không đủ 3000 sao biển");
//                                    } else if (player.inventory.gold < 1_500_000_000) {
//                                        this.npcChat(player, "Bạn không đủ 1.5 Tỷ vàng");
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        player.inventory.gold -= 1.5_000_000_000;
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, SaoBien, 3000);
//                                        Service.gI().sendMoney(player);
//                                        Item honpic = ItemService.gI().createNewItem((short) 1160);
//                                        InventoryServiceNew.gI().addItemBag(player, honpic);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Bạn nhận được Hồn Pic");
//                                    }
//                            }if (select == 1) {
//                            Item ConCua = null;
//                                    try {
//                                        ConCua = InventoryServiceNew.gI().findItemBag(player, 697);
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if (ConCua == null || ConCua.quantity < 5000) {
//                                        this.npcChat(player, "Bạn không đủ 5000 con cua");
//                                    } else if (player.inventory.gold < 1_500_000_000) {
//                                        this.npcChat(player, "Bạn không đủ 1.5 Tỷ vàng");
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        player.inventory.gold -= 1_500_000_000;
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, ConCua, 5000);
//                                        Service.gI().sendMoney(player);
//                                        Item honcumber = ItemService.gI().createNewItem((short) 1162);                                   
//                                        InventoryServiceNew.gI().addItemBag(player, honcumber);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Bạn nhận được Hồn Cumber");
//                                    }
//                            }if (select == 2) {
//                            Item VoSo = null;
//                                    try {
//                                        VoSo = InventoryServiceNew.gI().findItemBag(player, 696);
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if (VoSo == null || VoSo.quantity < 4000) {
//                                        this.npcChat(player, "Bạn không đủ 4000 Vỏ xò");
//                                    } else if (player.inventory.gold < 1_500_000_000) {
//                                        this.npcChat(player, "Bạn không đủ 1.5 Tỷ vàng");
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        player.inventory.gold -= 1_500_000_000;
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, VoSo, 4000);
//                                        Service.gI().sendMoney(player);
//                                        Item hongoku = ItemService.gI().createNewItem((short) 1161); 
//                                        InventoryServiceNew.gI().addItemBag(player, hongoku);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        this.npcChat(player, "Bạn nhận được Hồn Goku");
//                                    }
//                            }if (select == 3) {
//                            Item VoOc = null;
//                                    try {
//                                        VoOc = InventoryServiceNew.gI().findItemBag(player, 695);
//                                    } catch (Exception e) {
////                                        throw new RuntimeException(e);
//                                    }
//                                    if (VoOc == null || VoOc.quantity < 200) {
//                                        this.npcChat(player, "Bạn không đủ 200 vỏ ốc");
//                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
//                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
//                                    } else {
//                                        
//                                        InventoryServiceNew.gI().subQuantityItemsBag(player, VoOc, 200);
//                                        Service.gI().sendMoney(player);
//                                        Item ruongbau = ItemService.gI().createNewItem((short) Util.nextInt(14, 20));
//                                        InventoryServiceNew.gI().addItemBag(player, ruongbau);
//                                        InventoryServiceNew.gI().sendItemBags(player);
//                                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + ruongbau.template.name);
//                                    }
//                            }
//                        }
////                   
//                    }
//                }
//            }
//        };
//    }
 return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đổi vật phẩm sự kiện"
                                + "\nĐổi Vật Phẩm",
                                 " Đổi vật phẩm sự kiện trung thu",
                                 "Đổi vật phẩm sự kiện hè",
                                 "Đổi vàng nhanh");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {                   
                                case 0:
                                      if (player.getSession().player.nPoint.power >= 20000000000L) {
                                    createOtherMenu(player, ConstNpc.MENU_1,
                                            "Ngươi muốn mang đủ vật phẩm tới chưa! "
                                                    + "\n Nấu bánh trung thu Gà quay cần, x10 Gà quay nguyên con, x199(bột mì, trứng vịt muối, đậu xanh) "
                                                    + "\n Nấu bánh trung thu Thập cẩm cần x199(bột mì, trứng vịt muối, đậu xanh) "
                                                    + "\n Nấu bánh trung thu 1 Trứng cần x5TV, x299(bột mì, trứng vịt muối, đậu xanh) "
                                                    + "\n Đổi bánh trung thu 2 Trứng cần x10TV, x399 (bột mì, trứng vịt muối, đậu xanh) "
                                                    + "\n Đổi x99(bột mì, trứng vịt muối, đậu xanh)nhận ngẫu nhiên 1-5 tỉ vàng"
                                                    + "\n Cửa hàng bánh trung thu",
                                            "Nấu bánh\n trung thu Gà quay",
                                            "Nấu bánh\n trung thu Thập cẩm",
                                            "Nấu bánh\n trung thu 1 Trứng", 
                                            "Nấu bánh\n trung thu 2 Trứng",
                                            "Đổi vàng",
                                            "Cửa hàng");
                                    } else {
                                    this.npcChat(player, "Bạn chưa đủ 20 tỷ sức mạnh để xem sự kiện");
                              }

                                    break;
                                case 1:
                                     createOtherMenu(player, ConstNpc.MENU_2, "-Sự Kiện Quốc Khánh 2/9-\nx3000 Sao Biển, 1.5tỉ vàng: Nhận Hồn Pic(40%)\nx200 Vỏ Ốc: Nhận Ngọc rồng ngẫu nhiên(1s-7s)", "Sao Biển","Vỏ Ốc");
                                
                                     break;
                                case 2:
                                     createOtherMenu(player, ConstNpc.MENU_3,
                                            "Ngươi muốn đổi vàng nhanh!\nLƯU Ý KHI VÀNG TRÊN 2 TỈ HOÀN THÀNH NHIỆM VỤ SẼ RESET VỀ MỐC 2 TỈ "
                                                    + "\n Đổi 20TV(10tỉ vàng) "
                                                    + "\n Đổi 100TV(50tỉ vàng)"
                                                    + "\n Đổi 500TV(250tỉ vàng"
                                                    + "\n Đổi 2000TV(1000tỉ vàng)",
                                
                                            "Đổi nhanh 20TV",
                                            "Đổi nhanh 100TV",
                                            "Đổi nhanh 500TV", 
                                            "Đổi nhanh 2000TV");
                                     break;
                                
                            }
                        }
                        
                        
                        else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_1) {
                            switch (select) {
                                
                                case 0: 
                                { Item botmi = null;
                                Item trungvitmuoi = null;
                                Item dauxanh = null;
                                Item gaquay = null;
                                    
                                    try {
                                        botmi = InventoryServiceNew.gI().findItemBag(player, 888); 
                                        trungvitmuoi = InventoryServiceNew.gI().findItemBag(player, 886);
                                        dauxanh = InventoryServiceNew.gI().findItemBag(player, 889);
                                        gaquay = InventoryServiceNew.gI().findItemBag(player, 887);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (botmi == null || botmi.quantity < 199 || trungvitmuoi == null || trungvitmuoi.quantity < 199 || dauxanh == null || dauxanh.quantity < 199 || gaquay == null || gaquay.quantity < 10  ) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");                                                                                                        
                                   } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                       InventoryServiceNew.gI().subQuantityItemsBag(player, botmi, 199);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, trungvitmuoi, 199);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 199);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, gaquay, 10);                            
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 890);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh trung thu Gà quay");
                                    }
                                    break;
                                }
                                case 1: 
                                   { Item botmi = null;
                                Item trungvitmuoi = null;
                                Item dauxanh = null;
                               
                                    
                                    try {
                                        botmi = InventoryServiceNew.gI().findItemBag(player, 888); 
                                        trungvitmuoi = InventoryServiceNew.gI().findItemBag(player, 886);
                                        dauxanh = InventoryServiceNew.gI().findItemBag(player, 889);
                                      
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (botmi == null || botmi.quantity < 199 || trungvitmuoi == null || trungvitmuoi.quantity < 199 || dauxanh == null || dauxanh.quantity < 199 ) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");                                                                                                        
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                       InventoryServiceNew.gI().subQuantityItemsBag(player, botmi, 199);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, trungvitmuoi, 199);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 199);
                                        
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 891);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh trung thu Thập cẩm");
                                    }
                                    break;
                                }
                                case 2: { Item botmi = null;
                                Item trungvitmuoi = null;
                                Item dauxanh = null;
                                  Item thoivang = null;
                                    
                                    try {
                                        botmi = InventoryServiceNew.gI().findItemBag(player, 888); 
                                        trungvitmuoi = InventoryServiceNew.gI().findItemBag(player, 886);
                                        dauxanh = InventoryServiceNew.gI().findItemBag(player, 889);
                                         thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                      
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (botmi == null || botmi.quantity < 299 || trungvitmuoi == null || trungvitmuoi.quantity < 299 || dauxanh == null || dauxanh.quantity < 299 ) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");                                                                                                        
                                   } else if (thoivang == null || thoivang.quantity < 5) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                       InventoryServiceNew.gI().subQuantityItemsBag(player, botmi, 299);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, trungvitmuoi, 299);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 299);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 5);
                                        
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 465);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh trung thu 1 Trứng");
                                    }
                                    break;
                                }
                                case 3: 
                                
                             { Item botmi = null;
                                Item trungvitmuoi = null;
                                Item dauxanh = null;
                                  Item thoivang = null;
                                    
                                    try {
                                        botmi = InventoryServiceNew.gI().findItemBag(player, 888); 
                                        trungvitmuoi = InventoryServiceNew.gI().findItemBag(player, 886);
                                        dauxanh = InventoryServiceNew.gI().findItemBag(player, 889);
                                         thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
                                      
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (botmi == null || botmi.quantity < 299 || trungvitmuoi == null || trungvitmuoi.quantity < 299 || dauxanh == null || dauxanh.quantity < 299 ) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");                                                                                                        
                                   } else if (thoivang == null || thoivang.quantity < 10) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                       InventoryServiceNew.gI().subQuantityItemsBag(player, botmi, 299);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, trungvitmuoi, 299);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 299);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, thoivang, 10);
                                        
                                        Service.getInstance().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 466);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 bánh trung thu 2 Trứng");
                                    }
                                    break;
                                }
                                case 4:
                                {
                                   Item botmi = null;
                                    Item trungvitmuoi = null; 
                                    Item dauxanh = null;
                                    try {
                                       botmi = InventoryServiceNew.gI().findItemBag(player, 888); 
                                        trungvitmuoi = InventoryServiceNew.gI().findItemBag(player, 886);
                                        dauxanh = InventoryServiceNew.gI().findItemBag(player, 889);                                               
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                   if (botmi == null || botmi.quantity < 99 || trungvitmuoi == null || trungvitmuoi.quantity < 99 || dauxanh == null || dauxanh.quantity < 99 ) {
                                        this.npcChat(player, "Bạn không đủ nguyên liệu để nấu bánh");                                                                                                        
                                    } else {
                                        player.inventory.gold += 5_000_000_000L;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, botmi, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, trungvitmuoi, 99);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 99);
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 5 Tỷ Vàng");
                                    }
                                    break;
                                }                              
                                case 5: // shop
                                {
                                    ShopServiceNew.gI().opendShop(player, "TRUNGTHU", false);
                                    break;
                                }
                            }
                        }  
                         else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_2) {
                            switch (select) {
                                case 0:
                                    Item SaoBien = null;
                                    try {
                                        SaoBien = InventoryServiceNew.gI().findItemBag(player, 698);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (SaoBien == null || SaoBien.quantity < 3000) {
                                        this.npcChat(player, "Bạn không đủ 3000 sao biển");
                                    } else if (player.inventory.gold < 1_500_000_000) {
                                        this.npcChat(player, "Bạn không đủ 1.5 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 1.5_000_000_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, SaoBien, 3000);
                                        Service.gI().sendMoney(player);
                                        Item honpic = ItemService.gI().createNewItem((short) 1160);
                                        InventoryServiceNew.gI().addItemBag(player, honpic);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được Hồn Pic");
                                    }
                                    break;
                            
                            
                           case 1: 
                             Item VoOc = null;
                                    try {
                                        VoOc = InventoryServiceNew.gI().findItemBag(player, 695);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (VoOc == null || VoOc.quantity < 200) {
                                        this.npcChat(player, "Bạn không đủ 200 vỏ ốc");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, VoOc, 200);
                                        Service.gI().sendMoney(player);
                                        Item ruongbau = ItemService.gI().createNewItem((short) Util.nextInt(14, 20));
                                        InventoryServiceNew.gI().addItemBag(player, ruongbau);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + ruongbau.template.name);
                                    }
                                    break;
                            }
                         }
                             else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_3) {
                            switch (select) {
                                
                                case 0: 
                                {
                                   Item tv = null;
                                    try {
                                       tv = InventoryServiceNew.gI().findItemBag(player, 457); 
                                                                                      
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                   if (tv == null || tv.quantity < 20) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");                                                                                                        
                                    } else {
                                        player.inventory.gold += 10_000_000_000L;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, tv, 20);
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 10 Tỷ Vàng");
                                    }
                                    break;
                                }
                                case 1: 
                                  {
                                   Item tv = null;
                                    try {
                                       tv = InventoryServiceNew.gI().findItemBag(player, 457); 
                                                                                      
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                   if (tv == null || tv.quantity < 100) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");                                                                                                        
                                    } else {
                                        player.inventory.gold += 50_000_000_000L;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, tv, 100);
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 50 Tỷ Vàng");
                                    }
                                    break;
                                }
                                case 2: 
                                {
                                   Item tv = null;
                                    try {
                                       tv = InventoryServiceNew.gI().findItemBag(player, 457); 
                                                                                      
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                   if (tv == null || tv.quantity < 500) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");                                                                                                        
                                    } else {
                                        player.inventory.gold += 250_000_000_000L;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, tv, 500);
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 250 Tỷ Vàng");
                                    }
                                    break;
                                }
                                case 3: 
                                
                           {
                                   Item tv = null;
                                    try {
                                       tv = InventoryServiceNew.gI().findItemBag(player, 457); 
                                                                                      
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                   if (tv == null || tv.quantity < 2000) {
                                        this.npcChat(player, "Bạn không đủ thỏi vàng");                                                                                                        
                                    } else {
                                        player.inventory.gold += 1_000_000_000_000L;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, tv, 2000);
                                        Service.getInstance().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1000 Tỷ Vàng");
                                    }
                                    break;
                                
                                }                              
                               
                            }
                        }
                  
                    }
                }
            }
        };
    }

    public static Npc mavuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 153) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin chào, tôi có thể giúp gì cho cậu?", "Tây thánh địa", "Từ chối");
                    } else if (this.mapId == 156) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 153) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                //đến tay thanh dia
                                ChangeMapService.gI().changeMapBySpaceShip(player, 156, -1, 360);
                            }
                        }
                    } else if (this.mapId == 156) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về lanh dia bang hoi
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    try {
                        Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                        if (biKiep != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + biKiep.quantity + " bí kiếp.\n"
                                    + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart", "Học dịch\nchuyển", "Đóng");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    try {
                        Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                        if (biKiep != null) {
                            if (biKiep.quantity >= 10000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                yardart.itemOptions.add(new Item.ItemOption(47, 400));
                                yardart.itemOptions.add(new Item.ItemOption(108, 10));
                                InventoryServiceNew.gI().addItemBag(player, yardart);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, biKiep, 10000);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn vừa nhận được trang phục tộc Yardart");
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        };
    }
public static Npc khidaumoi(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Bạn muốn nâng cấp khỉ ư?", "Nâng cấp\nkhỉ", "Shop Sự kiện", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 14) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 1,
                                            "|7|Cần Khỉ Lv1 hoặc 2,4,6 để nâng cấp lên lv8\b|2|Mỗi lần nâng cấp tiếp thì mỗi cấp cần thêm 10 đá ngũ sắc",
                                            "Khỉ\ncấp 2",
                                            "Khỉ\ncấp 4",
                                            "Khỉ\ncấp 6",
                                            "Khỉ\ncấp 8",
                                            "Từ chối");
                                    break;
                                case 1: //shop
                                    ShopServiceNew.gI().opendShop(player, "SHOPKHI", false);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) { // action đổi dồ húy diệt
                            switch (select) {
                                case 0: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item klv1 = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1145);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item klv = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1145 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 1145 + i) && soLuong >= 20) {
                                            CombineServiceNew.gI().khilv2(player, 1146 + i);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 20);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, klv, 1);
                                            this.npcChat(player, "Upgrede Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần cái trang khỉ cấp 1 với 20 đá ngũ sắc");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 1: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item klv2 = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1146);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item klv = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1146 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 1146 + i) && soLuong >= 30) {
                                            CombineServiceNew.gI().khilv3(player, 1147 + i);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 30);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, klv, 1);
                                            this.npcChat(player, "Upgrede Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần cái trang khỉ cấp 2 với 30 đá ngũ sắc");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 2: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item klv2 = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1147);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item klv = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1147 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 1147 + i) && soLuong >= 40) {
                                            CombineServiceNew.gI().khilv4(player, 1148 + i);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 40);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, klv, 1);
                                            this.npcChat(player, "Upgrede Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần cái trang khỉ cấp 3 với 40 đá ngũ sắc");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;
                                case 3: // trade
                                try {
                                    Item dns = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 674);
                                    Item klv2 = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1148);
                                    int soLuong = 0;
                                    if (dns != null) {
                                        soLuong = dns.quantity;
                                    }
                                    for (int i = 0; i < 12; i++) {
                                        Item klv = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1148 + i);

                                        if (InventoryServiceNew.gI().isExistItemBag(player, 1148 + i) && soLuong >= 50) {
                                            CombineServiceNew.gI().khilv5(player, 1144 + i);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, dns, 50);
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, klv, 1);
                                            this.npcChat(player, "Upgrede Thành Công!");

                                            break;
                                        } else {
                                            this.npcChat(player, "Yêu cầu cần cái trang khỉ cấp 3 với 50 đá ngũ sắc");
                                        }

                                    }
                                } catch (Exception e) {

                                }
                                break;

                                case 5: // canel
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }
    public static Npc GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            String[] menuselect = new String[]{};
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 52) {
                    createOtherMenu(pl, 0, DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Giai(pl), "Thông tin\nChi tiết", DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(pl) ? "Đăng ký" : "OK", "Đại Hội\nVõ Thuật\nLần thứ\n23");
                }else if(this.mapId == 129){
                        int goldchallenge = pl.goldChallenge;
                        if (pl.levelWoodChest == 0) {
                            menuselect = new String[]{"Hướng\ndẫn\nthêm","Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng", "Về\nĐại Hội\nVõ Thuật"};
                        } else {
                            menuselect = new String[]{"Hướng\ndẫn\nthêm","Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng", "Nhận thưởng\nRương cấp\n" + pl.levelWoodChest, "Về\nĐại Hội\nVõ Thuật"};
                        }
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm,ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào", menuselect, "Từ chối");

                    }else{
                    super.openBaseMenu(pl);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                           if(this.mapId == 52) {
                        switch (select) {
                             case 0:
                            Service.gI().sendPopUpMultiLine(player, tempId, avartar, DaiHoiVoThuat.gI().Info());
                            break;
                        case 1:
                            if (DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(player)) {
                                DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Reg(player);
                            }
                            break;
                            case 2:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 129, player.location.x, 360);
                                break;
                        }
                    }
                    else if (this.mapId == 129) {
                            int goldchallenge = player.goldChallenge;
                            if (player.levelWoodChest == 0) {
                                switch (select) {
                                    case 0:
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.NPC_DHVT23);
                                        break;
                                    case 1:
                                        if (InventoryServiceNew.gI().finditemWoodChest(player)) {
                                            if (player.inventory.gold >= goldchallenge) {
                                                MartialCongressService.gI().startChallenge(player);
                                                player.inventory.gold -= (goldchallenge);
                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                player.goldChallenge += 2000000;
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu " + Util.numberToMoney(goldchallenge - player.inventory.gold) + " vàng");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
                                        }
                                        break;
                                    case 2:
                                        ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                        break;
                                }
                            } else {
                                switch (select) {
                                    case 0:
                                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.NPC_DHVT23);
                                        break;
                                    case 1:
                                        if (InventoryServiceNew.gI().finditemWoodChest(player)) {
                                            if (player.inventory.gold >= goldchallenge) {
                                                MartialCongressService.gI().startChallenge(player);
                                                player.inventory.gold -= (goldchallenge);
                                                PlayerService.gI().sendInfoHpMpMoney(player);
                                                player.goldChallenge += 2000000;
                                            } else {
                                                Service.getInstance().sendThongBao(player, "Không đủ vàng, còn thiếu " + Util.numberToMoney(goldchallenge - player.inventory.gold) + " vàng");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
                                        }
                                        break;
                                    case 2:
                                        if (!player.receivedWoodChest) {
                                            if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                                Item it = ItemService.gI().createNewItem((short) 570);
                                                it.itemOptions.add(new Item.ItemOption(72, player.levelWoodChest));
                                                it.itemOptions.add(new Item.ItemOption(30, 0));
                                                it.createTime = System.currentTimeMillis();
                                                InventoryServiceNew.gI().addItemBag(player, it);
                                                InventoryServiceNew.gI().sendItemBags(player);

                                                player.receivedWoodChest = true;
                                                player.levelWoodChest = 0;
                                                Service.getInstance().sendThongBao(player, "Bạn nhận được rương gỗ");
                                            } else {
                                                this.npcChat(player, "Hành trang đã đầy");
                                            }
                                        } else {
                                            Service.getInstance().sendThongBao(player, "Mỗi ngày chỉ có thể nhận rương báu 1 lần");
                                        }
                                        break;
                                    case 3:
                                        ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                        break;
                                }
                            }
                        }
                    }
            }
        };
    }
public static Npc monaito(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 7) {
                        this.createOtherMenu(player, 0,
                                "Chào bạn tôi sẽ đưa bạn đến hành tinh Cereal?", "Đồng ý", "Từ chối");
                    }
                    if (this.mapId == 182) {
                        this.createOtherMenu(player, 0,
                                "Ta ở đây để đưa con về", "Về Làng Mori", "Từ chối");
                    }
                    if (this.mapId == 198) {
                        this.createOtherMenu(player, 0,
                                "Ta ở đây để đưa con về", "Về Đảo Kame", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 7) {
                        if (player.iDMark.getIndexMenu() == 0) { //
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 182, -1, 264);
                                    break; // den hanh tinh cereal
                            }
                        }
                    }
                    if (this.mapId == 182) {
                        if (player.iDMark.getIndexMenu() == 0) { //
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 7, -1, 432);
                                    break; // quay ve

                            }
                        }
                    }
                    if (this.mapId == 198) {
                        if (player.iDMark.getIndexMenu() == 0) { //
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 624);
                                    break; // quay ve

                            }
                        }
                    }
                }
            }
        };
    }
public static Npc granala(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 183) {
                        this.createOtherMenu(player, 0,
                                "Ngươi!\n Hãy cầm đủ 7 viên ngọc rồng \n Monaito đến đây gặp ta ta sẽ ban cho ngươi\n 1 điều ước ",
                                "Gọi rồng", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 182) {
                        if (player.iDMark.getIndexMenu() == 0) { //
                            switch (select) {
                                case 0:
                                    this.npcChat(player, "Chức Năng Đang Được Update!");
                                    break; // goi rong

                            }
                        }
                    }
                }
            }
        };
    }
//     Service.gI().showListTop(player, Manager.topNV);

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            switch (tempId) {
                case ConstNpc.TORI_BOT:
                    return toribot(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_64:
                    return npcThienSu64(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.GHI_DANH:
                    return GhiDanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE:
                    return poTaGe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME:
                    return quyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUA_HANG_KY_GUI:
                   return kyGui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THO_DAI_CA:
                    return thodaika(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU:
                    return truongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA:
                    return vuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    return ongGohan_ongMoori_ongParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA:
                    return bulmaQK(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE:
                    return dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE:
                    return appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF:
                    return drDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO:
                    return cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI:
                    return cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA:
                    return santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON:
                    return uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT:
                    return baHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO:
                    return ruongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN:
                    return dauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK:
                    return calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO:
                    return jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.MR_POPO:
                    return mrpopo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE:
                    return thuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GIUMA_DAU_BO:
                    return mavuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.Granola:
                    return granala(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.Monaito:
                    return monaito(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.VADOS:
                    return vados(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KHI_DAU_MOI:
                    return khidaumoi(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.THAN_VU_TRU:
                    return thanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT:
                    return kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN:
                    return osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TO_SU_KAIO:
                    return tosukaio(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG:
                    return npclytieunuong54(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH:
                    return linhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG:
                    return quaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG:
                    return quocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL:
                    return bulmaTL(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA:
                    return rongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    return rong1_to_7s(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CHOPPER:
                    return chopper(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.FRANKY:
                    return franky(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NAMI:
                    return nami(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BERRY:
                    return berry(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL:
                    return bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.WHIS:
                    return whis(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG:
                    return boMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_MEO_KARIN:
                    return karin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ:
                    return gokuSSJ_1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_:
                    return gokuSSJ_2(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUONG_TANG:
                    return duongtank(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.HUNG_VUONG:
                    return hungvuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NOI_BANH:
                    return noibanh(mapId, status, cx, cy, tempId, avatar);    
                case ConstNpc.THOREN:
                    return thoren(mapId, status, cx, cy, tempId, avatar);    
                 case ConstNpc.ONGGIANOEL:
                    return onggianoel(mapId, status, cx, cy, tempId, avatar);   
                default:
                    return new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
//                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                            }
                        }
                    };
            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class, e, "Lỗi load npc");
            return null;
        }
    }

    //girlbeo-mark
    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP:
                        if (player.getSession().actived) 
                    {
                        if (Maintenance.isRuning) {
                            break;
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                        break;
                    }
                        else {
                            Service.gI().sendThongBao(player, "Vui lòng mở thẻ để sử dụng chức năng này.");
                            break;
                        }
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                        case ConstNpc.DOI_DTVIP:  // dang sua
                        switch (select) {
                            case 0:
                                if (player.getSession().vnd >= 30000) {
                                    if (player.pet == null) {
                                        Service.getInstance().sendThongBao(player, "Bạn đéo có pet");
                                    } else {
                                   try {
                                        player.getSession().vnd -= 30000; // dong nay nefix dum e vdoi hn ko tru vs
                                        // cho nao

                                        PlayerDAO.subcoinBar(player, 30000);
                                         PetService.gI().changeBerusPet(player, player.gender);
//                                        GirlkunDB.executeUpdate("update player set vnd = (vnd - 30000) where id = " + player.id);
                                        Service.getInstance().sendThongBao(player, "doi thanh cong");
                                    } catch (Exception ex) {
                                        java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }                                    
                                }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn không đủ tiền");
                                }
                                break;          
                             case 1:
                                if (player.getSession().vnd >= 50000) {
                                    if (player.pet == null) {
                                        Service.getInstance().sendThongBao(player, "Bạn đéo có pet");
                                    } else {
                                   try {
                                        player.getSession().vnd -= 50000; // dong nay nefix dum e vdoi hn ko tru vs
                                        // cho nao

                                        PlayerDAO.subcoinBar(player, 50000);
                                         PetService.gI().changePicPet(player, player.gender);
//                                        GirlkunDB.executeUpdate("update player set vnd = (vnd - 50000) where id = " + player.id);
                                        Service.getInstance().sendThongBao(player, "doi thanh cong");
                                    } catch (Exception ex) {
                                        java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }                                    
                                }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn không đủ tiền");
                                }
                                break;          
                                
                            case 2:  //

                               if (player.getSession().vnd >= 100000) {
                                    if (player.pet == null) {
                                        Service.getInstance().sendThongBao(player, "Bạn đéo có pet");
                                    } else {
                                   try {
                                        player.getSession().vnd -= 100000;
                                       PlayerDAO.subcoinBar(player, 100000);
                                         PetService.gI().changeGokuPet(player, player.gender);
//                                        GirlkunDB.executeUpdate("update player set vnd = (vnd - 100000) where id = " + player.id);
                                        Service.getInstance().sendThongBao(player, "doi thanh cong");
                                    } catch (Exception ex) {
                                        java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }                                    
                                }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn không đủ tiền");
                                }
                                break;
                            
                             case 3:  //

                               if (player.getSession().vnd >= 150000) {
                                    if (player.pet == null) {
                                        Service.getInstance().sendThongBao(player, "Bạn đéo có pet");
                                    } else {
                                  try {
                                        player.getSession().vnd -= 150000;
                                        PlayerDAO.subcoinBar(player, 150000);
                                         PetService.gI().changeCumberPet(player, player.gender);
//                                        GirlkunDB.executeUpdate ("player set vnd = (vnd - 100000 where id =  + player.id");
                                        Service.getInstance().sendThongBao(player, "doi thanh cong");
                                    } catch (Exception ex) {
                                        java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }                                    
                                }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn không đủ tiền");
                                }
                                break;
                                case 4:  //

                               if (player.getSession().vnd >= 200000) {
                                    if (player.pet == null) {
                                        Service.getInstance().sendThongBao(player, "Bạn đéo có pet");
                                    } else {
                                  try {
                                        player.getSession().vnd -= 200000;
                                        PlayerDAO.subcoinBar(player, 200000);
                                         PetService.gI().changezamatPet(player, player.gender);
//                                        GirlkunDB.executeUpdate("update player set vnd = (vnd - 200000) where id = " + player.id);
                                        Service.getInstance().sendThongBao(player, "doi thanh cong");
                                    } catch (Exception ex) {
                                        java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }                                    
                                }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Bạn không đủ tiền");
                                }
                                break;

                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1105:
                        if (select == 0) {
                            IntrinsicService.gI().sattd(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().satnm(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().setxd(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2000:
                    case ConstNpc.MENU_OPTION_USE_ITEM2001:
                    case ConstNpc.MENU_OPTION_USE_ITEM2002:
                        try {
                        ItemService.gI().OpenSKH(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2003:
                    case ConstNpc.MENU_OPTION_USE_ITEM2004:
                    case ConstNpc.MENU_OPTION_USE_ITEM2005:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.MENU_OPTION_USE_ITEM736:
                        try {
                        ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                    } catch (Exception e) {
                        Logger.error("Lỗi mở hộp quà");
                    }
                    break;
                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.gI().sendThongBao(player, "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;

                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.gI().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.UP_TOP_ITEM:
                        break;
                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryServiceNew.gI().addItemBag(player, item);
                                }
                                InventoryServiceNew.gI().sendItemBags(player);
                                break;
                            case 1:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                } else {
                                    if (player.pet.typePet == 1) {
                                        PetService.gI().changePicPet(player);
                                    } else if (player.pet.typePet == 2) {
                                        PetService.gI().changeMabuPet(player);
                                    }
                                    PetService.gI().changeBerusPet(player);
                                }
                                break;
                            case 2:
                                if (player.isAdmin()) {
                                    System.out.println(player.name);
                                    Maintenance.gI().start(15);
                                    System.out.println(player.name);
                                }
                                break;
                            case 3:
                                Input.gI().createFormFindPlayer(player);
                                break;
                            case 4:
                                this.createOtherMenu(player, ConstNpc.CALL_BOSS,
                                        "Chọn Boss?", "Full Cụm\nANDROID", "BLACK", "BROLY", "Cụm\nCell",
                                        "Cụm\nDoanh trại", "DOREMON", "FIDE", "FIDE\nBlack", "Cụm\nGINYU", "Cụm\nNAPPA", "NGỤC\nTÙ", "JACKYCHUN\nQUYLAO");
                                break;
                            case 5:
                                MaQuaTangManager.gI().checkInfomationGiftCode(player);
                                break;
                        }
                        break;
                       case ConstNpc.CALL_BOSS:
                        switch (select) {
                            case 0:
                                BossManager.gI().createBoss(BossID.ANDROID_13);
                                BossManager.gI().createBoss(BossID.ANDROID_14);
                                BossManager.gI().createBoss(BossID.ANDROID_15);
                                BossManager.gI().createBoss(BossID.ANDROID_19);
                                BossManager.gI().createBoss(BossID.DR_KORE);
                                BossManager.gI().createBoss(BossID.KING_KONG);
                                BossManager.gI().createBoss(BossID.PIC);
                                BossManager.gI().createBoss(BossID.POC);
                                break;
                            case 1:
                                BossManager.gI().createBoss(BossID.BLACK);
                                break;
                            case 2:
                                BossManager.gI().createBoss(BossID.BROLY);
                                break;
                            case 3:
                                BossManager.gI().createBoss(BossID.SIEU_BO_HUNG);
                                BossManager.gI().createBoss(BossID.XEN_BO_HUNG);
                                break;
                            case 4:
                                Service.getInstance().sendThongBao(player, "Không có boss");
                                break;
                            case 5:
                                BossManager.gI().createBoss(BossID.CHAIEN);
                                BossManager.gI().createBoss(BossID.XEKO);
                                BossManager.gI().createBoss(BossID.XUKA);
                                BossManager.gI().createBoss(BossID.NOBITA);
                                BossManager.gI().createBoss(BossID.DORAEMON);
                                break;
                            case 6:
                                BossManager.gI().createBoss(BossID.FIDE);
                                break;
                            case 7:
                                BossManager.gI().createBoss(BossID.FIDE_ROBOT);
                                BossManager.gI().createBoss(BossID.VUA_COLD);
                                break;
                            case 8:
                                BossManager.gI().createBoss(BossID.SO_1);
                                BossManager.gI().createBoss(BossID.SO_2);
                                BossManager.gI().createBoss(BossID.SO_3);
                                BossManager.gI().createBoss(BossID.SO_4);
                                BossManager.gI().createBoss(BossID.TIEU_DOI_TRUONG);
                                break;
                            case 9:
                                BossManager.gI().createBoss(BossID.KUKU);
                                BossManager.gI().createBoss(BossID.MAP_DAU_DINH);
                                BossManager.gI().createBoss(BossID.RAMBO);
                                break;
                            case 10:
                                BossManager.gI().createBoss(BossID.COOLER_GOLD);
                                BossManager.gI().createBoss(BossID.CUMBER);
                                BossManager.gI().createBoss(BossID.SONGOKU_TA_AC);
                                break;
                              case 11:
                                BossManager.gI().createBoss(BossID.JACKY);
                                BossManager.gI().createBoss(BossID.QUYLAO);
                                break;   
                        }
                        break;
                    case ConstNpc.menutd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().settaiyoken(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgenki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setkamejoko(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.menunm:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodki(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setgoddam(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setsummon(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;
                    case ConstNpc.XU_HRZ:
                        try {
                            if (select == 0) {
                                NapVangService.ChonGiaTien(20, player);
                            } else if (select == 1) {
                                NapVangService.ChonGiaTien(50, player);
                            } else if (select == 2) {
                                NapVangService.ChonGiaTien(100, player);
                                } else if (select == 3) {
                                NapVangService.ChonGiaTien(500, player);
                                
                            } else {
                                
                                break;
                            }
                            break;
                        } catch (Exception e) {
                            break;
                        }

                    case ConstNpc.menuxd:
                        switch (select) {
                            case 0:
                                try {
                                ItemService.gI().setgodgalick(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 1:
                                try {
                                ItemService.gI().setmonkey(player);
                            } catch (Exception e) {
                            }
                            break;
                            case 2:
                                try {
                                ItemService.gI().setgodhp(player);
                            } catch (Exception e) {
                            }
                            break;
                        }
                        break;

                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                clan.deleteDB(clan.id);
                                Manager.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.gI().sendThongBao(player, "Đã giải tán bang hội.");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.gI().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
                                        Service.gI().sendThongBao(player, "Đại thiên sứ đã được chuyển tức thời đến vị trí: " + p.name + " !");
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(p, player.zone, player.location.x, player.location.y);
                                        
                                        Service.gI().sendThongBao(player, "Cư dân " + p.name + " đã được Đại thiên sứ dịch chuyển tức thời đến đây!");
                                    }
                                    break;
                                case 2:
                                    Input.gI().createFormChangeName(player, p);
                                    break;
                                case 3:
                                    String[] selects = new String[]{"Hủy diệt", "Tha"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                            "Đại thiên sứ có muốn hủy diệt cư dân: " + p.name, selects, p);
                                    break;
                                case 4:
                                    Service.gI().sendThongBao(player, "Đại thiên sứ đã Logout cư dân " + p.name + " thành công");
                                    Client.gI().getPlayers().remove(p);
                                    Client.gI().kickSession(p.getSession());
                                    break;
                            }
                        }
                        break;
                    case ConstNpc.MENU_GIAO_BONG:
                        ItemService.gI().giaobong(player, (int) Util.tinhLuyThua(10, select + 2));
                        break;
                    case ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN:
                        if (select == 0) {
                            ItemService.gI().openBoxVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_TELE_NAMEC:
                        if (select == 0) {
                            NgocRongNamecService.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.gI().sendMoney(player);
                        }
                        break;
                }
            }
        };
    }

}
