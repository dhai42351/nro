
package com.girlkun.services.func;

import com.girlkun.card.Card;
import com.girlkun.card.RadarCard;
import com.girlkun.card.RadarService;
import com.girlkun.consts.ConstMap;
import com.girlkun.models.item.Item;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Inventory;
import com.girlkun.services.NpcService;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.Manager;
import com.girlkun.utils.SkillUtil;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import com.girlkun.server.io.MySession;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.ItemService;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.TaskService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.MapService;
import com.girlkun.services.NgocRongNamecService;
import com.girlkun.services.RewardService;
import com.girlkun.services.SkillService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import java.util.Date;
import java.util.Random;

public class UseItem {

    private static final int ITEM_BOX_TO_BODY_OR_BAG = 0;
    private static final int ITEM_BAG_TO_BOX = 1;
    private static final int ITEM_BODY_TO_BOX = 3;
    private static final int ITEM_BAG_TO_BODY = 4;
    private static final int ITEM_BODY_TO_BAG = 5;
    private static final int ITEM_BAG_TO_PET_BODY = 6;
    private static final int ITEM_BODY_PET_TO_BAG = 7;

    private static final byte DO_USE_ITEM = 0;
    private static final byte DO_THROW_ITEM = 1;
    private static final byte ACCEPT_THROW_ITEM = 2;
    private static final byte ACCEPT_USE_ITEM = 3;

    private static UseItem instance;

    private UseItem() {

    }

    public static UseItem gI() {
        if (instance == null) {
            instance = new UseItem();
        }
        return instance;
    }

    public void getItem(MySession session, Message msg) {
        Player player = session.player;

        TransactionService.gI().cancelTrade(player);
        try {
            int type = msg.reader().readByte();
            int index = msg.reader().readByte();
            if (index == -1) {
                return;
            }
            switch (type) {
              case ITEM_BOX_TO_BODY_OR_BAG:
                    InventoryServiceNew.gI().itemBoxToBodyOrBag(player, index);
                    TaskService.gI().checkDoneTaskGetItemBox(player);
                    break;
                case ITEM_BAG_TO_BOX:
                    InventoryServiceNew.gI().itemBagToBox(player, index);
                    break;
                case ITEM_BODY_TO_BOX:
                    InventoryServiceNew.gI().itemBodyToBox(player, index);
                    break;
                case ITEM_BAG_TO_BODY:
                    InventoryServiceNew.gI().itemBagToBody(player, index);
                    break;
                case ITEM_BODY_TO_BAG:
                    InventoryServiceNew.gI().itemBodyToBag(player, index);
                    break;
                case ITEM_BAG_TO_PET_BODY:
                    InventoryServiceNew.gI().itemBagToPetBody(player, index);
                    break;
                case ITEM_BODY_PET_TO_BAG:
                    InventoryServiceNew.gI().itemPetBodyToBag(player, index);
                    break;
            }
            player.setClothes.setup();
            if (player.pet != null) {
                player.pet.setClothes.setup();
            }
            player.setClanMember();
            Service.gI().point(player);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);

        }
    }

    public void testItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        try {
            byte type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
            System.out.println("type: " + type);
            System.out.println("where: " + where);
            System.out.println("index: " + index);
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    public void doItem(Player player, Message _msg) {
        TransactionService.gI().cancelTrade(player);
        Message msg;
        byte type;
        try {
            type = _msg.reader().readByte();
            int where = _msg.reader().readByte();
            int index = _msg.reader().readByte();
//            System.out.println(type + " " + where + " " + index);
            switch (type) {
                case DO_USE_ITEM:
                    if (player != null && player.inventory != null) {
                        if (index != -1) {
                            Item item = player.inventory.itemsBag.get(index);
                            if (item.isNotNullItem()) {
                                if (item.template.type == 7) {
                                    msg = new Message(-43);
                                    msg.writer().writeByte(type);
                                    msg.writer().writeByte(where);
                                    msg.writer().writeByte(index);
                                    msg.writer().writeUTF("Bạn chắc chắn học " + player.inventory.itemsBag.get(index).template.name + "?");
                                    player.sendMessage(msg);
                                } else {
                                    UseItem.gI().useItem(player, item, index);
                                }
                            }
                        } else {
                            this.eatPea(player);
                        }
                    }
                    break;
                case DO_THROW_ITEM:
                    if (!(player.zone.map.mapId == 21 || player.zone.map.mapId == 22 || player.zone.map.mapId == 23)) {
                        Item item = null;
                        if (where == 0) {
                            item = player.inventory.itemsBody.get(index);
                        } else {
                            item = player.inventory.itemsBag.get(index);
                        }
                        msg = new Message(-43);
                        msg.writer().writeByte(type);
                        msg.writer().writeByte(where);
                        msg.writer().writeByte(index);
                        msg.writer().writeUTF("Bạn chắc chắn muốn vứt " + item.template.name + "?");
                        player.sendMessage(msg);
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                    break;
                case ACCEPT_THROW_ITEM:
                    InventoryServiceNew.gI().throwItem(player, where, index);
                    Service.gI().point(player);
                    InventoryServiceNew.gI().sendItemBags(player);
                    break;
                case ACCEPT_USE_ITEM:
                    UseItem.gI().useItem(player, player.inventory.itemsBag.get(index), index);
                    break;
            }
        } catch (Exception e) {
//            Logger.logException(UseItem.class, e);
        }
    }

    private void useItem(Player pl, Item item, int indexBag) {
        if (item.template.strRequire <= pl.nPoint.power) {
            switch (item.template.type) {
                case 7: //sách học, nâng skill
                    learnSkill(pl, item);
                    break;
                case 33:
                    UseCard(pl,item);
                    break;
                case 6: //đậu thần
                    this.eatPea(pl);
                    break;
                case 12: //ngọc rồng các loại
                    controllerCallRongThan(pl, item);
                    controllerCalltrb(pl, item);
                    controllerCallrx(pl, item);
                    
                    break;
                case 23: //thú cưỡi mới
                case 24: //thú cưỡi cũ
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    break;
                case 11: //item bag
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.gI().sendFlagBag(pl);
                    break;
                case 72: {
                    InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                    Service.gI().sendPetFollow(pl, (short) (item.template.iconID - 1));
                    break;
                }
                default:
                    switch (item.template.id) {
                        case 992:
                            pl.type = 1;
                            pl.maxTime = 5;
                            Service.gI().Transport(pl);
                            break;
                        case 1999:
                            pl.type = 2;
                            pl.maxTime = 5;
                            Service.gI().Transport(pl);
                            break;
                        case 361:
                            if (pl.idNRNM != -1) {
                                Service.gI().sendThongBao(pl, "Không thể thực hiện");
                                return;
                            }
                            pl.idGo = (short) Util.nextInt(0, 6);
                            NpcService.gI().createMenuConMeo(pl, ConstNpc.CONFIRM_TELE_NAMEC, -1, "1 Sao (" + NgocRongNamecService.gI().getDis(pl, 0, (short) 353) + " m)\n2 Sao (" + NgocRongNamecService.gI().getDis(pl, 1, (short) 354) + " m)\n3 Sao (" + NgocRongNamecService.gI().getDis(pl, 2, (short) 355) + " m)\n4 Sao (" + NgocRongNamecService.gI().getDis(pl, 3, (short) 356) + " m)\n5 Sao (" + NgocRongNamecService.gI().getDis(pl, 4, (short) 357) + " m)\n6 Sao (" + NgocRongNamecService.gI().getDis(pl, 5, (short) 358) + " m)\n7 Sao (" + NgocRongNamecService.gI().getDis(pl, 6, (short) 359) + " m)", "Đến ngay\nViên " + (pl.idGo + 1) + " Sao\n50 ngọc", "Kết thức");
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            InventoryServiceNew.gI().sendItemBags(pl);
                            break;
                        case 892: //thỏ xám
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 882, 883, 884);
                            Service.gI().point(pl);
                            break;
                        case 893: //thỏ trắng
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 885, 886, 887);
                            Service.gI().point(pl);
                            break;
                        case 909: //Thần chết cute
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 897, 898, 899);
                            Service.gI().point(pl);
                            break;
                        case 942:// hổ vàng
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 966, 967, 968);
                            Service.gI().point(pl);
                            break;
                        case 943:// hổ xanh
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 969, 970, 971);
                            Service.gI().point(pl);
                            break;
                        case 944:// hổ trắng
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 972, 973, 974);
                            Service.gI().point(pl);
                            break;
                        case 967://sao la
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1050, 1051, 1052);
                            Service.gI().point(pl);
                            break;
                        case 1407: // Con cún vàng
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 663, 664, 665);
                            Service.gI().point(pl);
                            break;
                        case 1408: // Cua đỏ
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1074, 1075, 1076);
                            Service.gI().point(pl);
                            break;
                        case 1409: // phuf thuyr
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1158, 1159, 1160);
                            Service.gI().point(pl);
                            break;   
                        case 1410: // Bí ma vương
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1155, 1156, 1157);
                            Service.gI().point(pl);
                            break;
                        case 1411: // Mèo đuôi vàng đen
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1183, 1184, 1185);
                            Service.gI().point(pl);
                            break;
                        case 1412: // Mèo đuôi vàng trắng
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1201, 1202, 1203);
                            Service.gI().point(pl);
                            break;
                        case 1413: // Gà 9 cựa
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1239, 1240, 1241);
                            Service.gI().point(pl);
                            break;
                        case 1414: // Ngựa 9 hồng mao
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1242, 1243, 1244);
                            Service.gI().point(pl);
                            break;
                        case 1415: // Voi 9 ngà
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1245, 1246, 1247);
                            Service.gI().point(pl);
                            break;
                        case 1416: // Pet Minions
                            InventoryServiceNew.gI().itemBagToBody(pl, indexBag);
                            PetService.Pet2(pl, 1254, 1255, 1256);
                            Service.gI().point(pl);
                            break;
                        case 293:
                            openGoiDau1(pl, item);
                            break;
                        case 294:
                            openGoiDau2(pl, item);
                            break;
                        case 295:
                            openGoiDau3(pl, item);
                            break;
                        case 296:
                            openGoiDau4(pl, item);
                            break;
                        case 297:
                            openGoiDau5(pl, item);
                            break;
                        case 298:
                            openGoiDau6(pl, item);
                            break;
                        case 299:
                            openGoiDau7(pl, item);
                            break;
                        case 596:
                            openGoiDau8(pl, item);
                            break;
                        case 597:
                            openGoiDau9(pl, item);
                            break;
                        case 211: //nho tím
                        case 212: //nho xanh
                            eatGrapes(pl, item);
                            break;
                        case 457:
                            UseItem.gI().usethoivang(pl);
                            break;
                        case 1278:
                            hopquact(pl, item);
                            break;    
                         case 1259:
                            UseItem.gI().hopquat1NV(pl);
                            break;    
                        case 1260:
                            UseItem.gI().hopquat2NV(pl);
                            break;
                        case 1261:
                            UseItem.gI().hopquat3NV(pl);
                            break;   
                        case 1263:
                            UseItem.gI().hopquat1SM(pl);
                            break;
                        case 1264:
                            UseItem.gI().hopquat2SM(pl);
                            break;
                        case 1265:
                            UseItem.gI().hopquat3SM(pl);
                            break;
                        case 1266:
                            UseItem.gI().hopquat1nap(pl);
                            break;
                        case 1267:
                            UseItem.gI().hopquat2nap(pl);
                            break;
                        case 1268:
                            UseItem.gI().hopquat3nap(pl);
                            break;
                        case 1270:
                            UseItem.gI().hopquat4nap(pl);
                            break;
                        case 1271:
                            UseItem.gI().hopquat5nap(pl);
                            break;
                        case 1272:
                            UseItem.gI().hopquadenbu(pl);
                            break; 
                        case 1246:
                              Input.gI().tanghongngoc(pl);
                            break;
                        case 1255:
                            UseItem.gI().top1(pl);
                            break; 
                        case 1256:
                            UseItem.gI().top2(pl);
                            break; 
                        case 1257:
                            UseItem.gI().top3(pl);
                            break; 
                        case 1258:
                            UseItem.gI().top410(pl);
                            break;
                        case 1273:
                            UseItem.gI().hopquabtc2(pl);
                            break;
                         case 962:
                            UseItem.gI().hopquacaitrang(pl);
                            break;
                        case 963:
                            UseItem.gI().hopquapet(pl);
                            break;
                        case 1242:
                            UseItem.gI().hopquacaitrang1(pl);
                            break;
                        case 1243:
                            UseItem.gI().hopquapet1(pl);
                            break; 
                        case 1244:
                            UseItem.gI().hopquavpdl(pl);
                            break;
                        case 1245:
                            UseItem.gI().hopquahongngoc(pl);
                            break;
                        case 1105://hop qua skh, item 2002 xd
                            UseItem.gI().Hopts(pl, item);
                            break;
                        case 1269://hop qua skh, item 2002 xd
                            UseItem.gI().Hopdothanlinh(pl, item);
                            break;
                        case 342:
                        case 343:
                        case 344:
                        case 345:
                            if (pl.zone.items.stream().filter(it -> it != null && it.itemTemplate.type == 22).count() < 5) {
                                Service.gI().DropVeTinh(pl, item, pl.zone, pl.location.x, pl.location.y);
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            } else {
                                Service.gI().sendThongBao(pl, "Đặt ít vệ tinh thôi");
                            }
                            break;
                        case 380: //cskb
                            UseItem.gI().openCSKB(pl);
                            break;
                        case 628:
                            openPhieuCaiTrangHaiTac(pl, item);
                        case 381: //cuồng nộ
                        case 382: //bổ huyết
                        case 383: //bổ khí
                        case 384: //giáp xên
                        case 385: //ẩn danh
                        case 379: //máy dò capsule
                        case 2037: //máy dò cosmos
                        case 663: //bánh pudding
                        case 664: //xúc xíc
                        case 665: //kem dâu
                        case 666: //mì ly
                        case 667: //sushi
                        case 752:
                        case 753:
                        case 902:
                        case 903:
                        case 900:
                        case 899:
                        case 1099:
                        case 1100:
                        case 1101:
                        case 1102:
                        case 1103:
                        case 1016:
                        case 1017:
                             case 472:
                                case 473:
                            useItemTime(pl, item);
                            break;
                        case 570:
                            openWoodChest(pl,item);
                        case 521: //tdlt
                            useTDLT(pl, item);
                            break;
                        case 454: //bông tai
                            UseItem.gI().usePorata(pl);
                            break;
                        case 1199:// bông tai c3 
                            UseItem.gI().usePorata3(pl);
                            break;
                        case 1197:// ngọc hợp thể siêu cấp
                            UseItem.gI().usePorata5(pl);
                            break;    
                            
                        case 193: //gói 10 viên capsule
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                        case 194: //capsule đặc biệt
                            openCapsuleUI(pl);
                            break;
                        case 401: //đổi đệ tử
                            changePet(pl, item);
                            break;
                        case 1108: //đổi đệ tử
                            changeBerusPet(pl, item);
                            break;

                        case 1160: //đổi đệ tử
                            changePetPic(pl, item);
                            break;
                        case 1161: //đổi đệ tử
                            changeGokuPet(pl, item);
                            break;
                        case 1162: //đổi đệ tử
                            changeCumberPet(pl, item);
                            break;
                        case 1210: //đổi đệ tử
                            changeWhisPet(pl, item);
                            break;
                        case 1211: //đổi đệ tử
                            changeBillPet(pl, item);
                            break;        
                         case 1198: //bông tai c4
                            UseItem.gI().usePorata4(pl);
                            break;
                        case 402: //sách nâng chiêu 1 đệ tử
                        case 403: //sách nâng chiêu 2 đệ tử
                        case 404: //sách nâng chiêu 3 đệ tử
                        case 759: //sách nâng chiêu 4 đệ tử
                            upSkillPet(pl, item);
                            break;
                        case 921: //bông tai c2
                            UseItem.gI().usePorata2(pl);     
                            break;    
                        case 736:
                            ItemService.gI().OpenItem736(pl, item);
                            break;
                        case 987:
                            Service.gI().sendThongBao(pl, "Bảo vệ trang bị không bị rớt cấp"); //đá bảo vệ
                            break;
                        case 1098:
                            useItemHopQuaTanThu(pl);
                            break;
                        case 1128:
                            openDaBaoVe(pl, item);
                            break;
                        case 1129:
                            openSPL(pl, item);
                            break;
                        case 1130:
                            openDaNangCap(pl, item);
                            break;
                        case 1131:
                            openManhTS(pl, item);
                            break;
                        case 2000://hop qua skh, item 2000 td
                        case 2001://hop qua skh, item 2001 nm
                        case 2002://hop qua skh, item 2002 xd
                            UseItem.gI().ItemSKH(pl, item);
                            break;

                        case 2003://hop qua skh, item 2003 td
                        case 2004://hop qua skh, item 2004 nm
                        case 2005://hop qua skh, item 2005 xd
                            UseItem.gI().ItemDHD(pl, item);
                            break;
                        case 1169:
                            
                            SkillService.gI().learSkillSpecial(pl, Skill.SUPER_KAME);
                            break;
                        case 1170:
                            SkillService.gI().learSkillSpecial(pl, Skill.MA_PHONG_BA);
                            break;
                        case 1171:
//                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
//                            InventoryServiceNew.gI().sendItemBags(pl);
                            SkillService.gI().learSkillSpecial(pl, Skill.LIEN_HOAN_CHUONG);
                            break;
                        case 2006:
                            Input.gI().createFormChangeNameByItem(pl);
                            break;
                         case 1158:
                            if (pl.pet == null) {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                break;
                            }

                            if(pl.pet.playerSkill.skills.get(1).skillId != -1 && pl.pet.playerSkill.skills.get(2).skillId != -1) {
                                pl.pet.openSkill2();
                                pl.pet.openSkill3();
                                InventoryServiceNew.gI().subQuantityItem(pl.inventory.itemsBag, item, 1);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.gI().sendThongBao(pl, "Đã đổi thành công chiêu 2 3 đệ tử");
                            } else {
                                Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 2 chứ!");
                            }
                            break; 
                             case 1159:
                            if (pl.pet == null) {
                                Service.gI().sendThongBao(pl, "Ngươi làm gì có đệ tử?");
                                break;
                            }

                            if(pl.pet.playerSkill.skills.get(3).skillId != -1 && pl.pet.playerSkill.skills.get(4).skillId != -1) {
                                pl.pet.openSkill4();
                                pl.pet.openSkill5();
                                InventoryServiceNew.gI().subQuantityItem(pl.inventory.itemsBag, item, 3);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.gI().sendThongBao(pl, "Đã đổi thành công chiêu 4 5 đệ tử");
                            } else {
                                Service.gI().sendThongBao(pl, "Ít nhất đệ tử ngươi phải có chiêu 4 chứ!");
                            }
                            break; 
                            case 1250:
                                
                             int time = 5000;
                            if (pl != null) {
                                
                               EffectSkillService.gI().setBlindDCTT(pl, System.currentTimeMillis(), time);
                                EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.CANCAUTHUONG_EFFECT);
                                ItemTimeService.gI().sendItemTime(pl, 9595,time / 1000);
                                try {
                                    Thread.sleep(time);
                                } catch (Exception e) {
                                }
                               
                              ItemService.gI().OpenItem1355(pl, item);
                                    
                            }
                  
                            break;
                            case 1251:
                               
                            time = 5500;
                            if (pl != null) {
                                EffectSkillService.gI().setBlindDCTT(pl, System.currentTimeMillis(), time);
                                EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.CANCAUCAOCAP_EFFECT);
                                ItemTimeService.gI().sendItemTime(pl, 9596, time / 1000);
                                try {
                                    Thread.sleep(time);
                                } catch (Exception e) {
                                }
                                
                               ItemService.gI().OpenItem1356(pl, item);
                                    
                                
                            }
                             
                            break;
                             case 1262:
                            openManhTA(pl, item);
                            break;
                        case 2027:
                        case 2028: {
                            if (InventoryServiceNew.gI().getCountEmptyBag(pl) == 0) {
                                Service.gI().sendThongBao(pl, "Hành trang không đủ chỗ trống");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                                Item linhThu = ItemService.gI().createNewItem((short) Util.nextInt(2019, 2026));
                                linhThu.itemOptions.add(new Item.ItemOption(50, 10));
                                linhThu.itemOptions.add(new Item.ItemOption(77, 5));
                                linhThu.itemOptions.add(new Item.ItemOption(103, 5));
                                linhThu.itemOptions.add(new Item.ItemOption(95, 3));
                                linhThu.itemOptions.add(new Item.ItemOption(96, 3));
                                InventoryServiceNew.gI().addItemBag(pl, linhThu);
                                InventoryServiceNew.gI().sendItemBags(pl);
                                Service.gI().sendThongBao(pl, "Chúc mừng bạn nhận được Linh thú " + linhThu.template.name);
                            }
                            break;
                           
                        }
                    }
                    break;
            }
            InventoryServiceNew.gI().sendItemBags(pl);
        } else {
            Service.gI().sendThongBaoOK(pl, "Sức mạnh không đủ yêu cầu");
        }
    }
    private void hopquahongngoc (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item hopquahongngoc = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1245) {
                    hopquahongngoc = item;
                    break;
                }
            }
            if (hopquahongngoc != null){
           Item trungLinhThu = ItemService.gI().createNewItem((short)861);
            int rdUp = Util.nextInt(0, 7);
                    if (rdUp == 0) {
                        trungLinhThu.quantity = 10 ;
                    } else if (rdUp == 1) {
                        trungLinhThu.quantity = 20 ;
                    } else if (rdUp == 2) {
                        trungLinhThu.quantity = 30 ;
                    } else if (rdUp == 3) {
                        trungLinhThu.quantity = 40 ;                    
                    } else if (rdUp == 4) {
                        trungLinhThu.quantity = 50 ;
                    } else if (rdUp == 5) {
                        trungLinhThu.quantity = 60 ;
                    } else if (rdUp == 6) {
                        trungLinhThu.quantity = 70 ;
                    } else if (rdUp == 7) {
                        trungLinhThu.quantity = 100 ;
                    } 
        InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquahongngoc, 1);
            InventoryServiceNew.gI().addItemBag(pl, trungLinhThu);                       
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + trungLinhThu.template.name);                                }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void hopquact (Player pl, Item item) {
if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            boolean vinhvien = Manager.TotalCaiTrang >= 100;
            int[] rdct = new int[]{1180,1181,1182,1183,1224,1225};           
            int randomct = new Random().nextInt(rdct.length);           
            Item ct = ItemService.gI().createNewItem((short) rdct[randomct]);          
            if (!vinhvien) {
              ct.itemOptions.add(new ItemOption(0, Util.nextInt(500,5000)));
              ct.itemOptions.add(new ItemOption(101, Util.nextInt(35,75)));
              ct.itemOptions.add(new ItemOption(50, Util.nextInt(10,35)));
              ct.itemOptions.add(new ItemOption(77, Util.nextInt(10,35)));
              ct.itemOptions.add(new ItemOption(103, Util.nextInt(10,35)));
               ct.itemOptions.add(new ItemOption(14, Util.nextInt(1,15)));
              ct.itemOptions.add(new ItemOption(93, Util.nextInt(1,10)));
                Manager.TotalCaiTrang += 1;
            } else {
              ct.itemOptions.add(new ItemOption(0, Util.nextInt(500,5000)));
              ct.itemOptions.add(new ItemOption(101, Util.nextInt(35,75)));
              ct.itemOptions.add(new ItemOption(50, Util.nextInt(10,35)));
              ct.itemOptions.add(new ItemOption(77, Util.nextInt(10,35)));
              ct.itemOptions.add(new ItemOption(103, Util.nextInt(1,15)));            
                Manager.TotalCaiTrang = 0;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().addItemBag(pl, ct);

            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name);
        } else {
            Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 2 ô trống trong hành trang.");
        }
    }
    private void hopquavpdl (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item hopquavpdl = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1244) {
                    hopquavpdl = item;
                    break;
                }
            }
            if (hopquavpdl != null){
           Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(1989,1994));
             trungLinhThu.itemOptions.add(new ItemOption(50, Util.nextInt(5,15)));
              trungLinhThu.itemOptions.add(new ItemOption(77, Util.nextInt(5,15)));
               trungLinhThu.itemOptions.add(new ItemOption(103, Util.nextInt(5,15)));
                trungLinhThu.itemOptions.add(new ItemOption(117, Util.nextInt(1,5)));
                 trungLinhThu.itemOptions.add(new ItemOption(5, Util.nextInt(5,15)));
                  trungLinhThu.itemOptions.add(new ItemOption(30, Util.nextInt(0,0)));
     InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquavpdl, 1);
            InventoryServiceNew.gI().addItemBag(pl, trungLinhThu);                       
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + trungLinhThu.template.name);                                }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     private void hopquacaitrang1 (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item hopquacaitrang = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1242) {
                    hopquacaitrang = item;
                    break;
                }
            }
            if (hopquacaitrang != null){
           Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(1212,1223));
             trungLinhThu.itemOptions.add(new ItemOption(50, Util.nextInt(10,30)));
              trungLinhThu.itemOptions.add(new ItemOption(77, Util.nextInt(10,30)));
               trungLinhThu.itemOptions.add(new ItemOption(103, Util.nextInt(10,30)));
                trungLinhThu.itemOptions.add(new ItemOption(117, Util.nextInt(10,30)));
                 trungLinhThu.itemOptions.add(new ItemOption(5, Util.nextInt(10,30)));
                  trungLinhThu.itemOptions.add(new ItemOption(33, Util.nextInt(0,0)));

            

            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquacaitrang, 1);
            InventoryServiceNew.gI().addItemBag(pl, trungLinhThu);                       
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + trungLinhThu.template.name);                                }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        private void hopquapet1 (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item hopquacaitrang = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1243) {
                    hopquacaitrang = item;
                    break;
                }
            }
            if (hopquacaitrang != null){
           Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(1413,1415));
             trungLinhThu.itemOptions.add(new ItemOption(50, Util.nextInt(5,15)));
              trungLinhThu.itemOptions.add(new ItemOption(77, Util.nextInt(5,15)));
               trungLinhThu.itemOptions.add(new ItemOption(103, Util.nextInt(5,15)));
                trungLinhThu.itemOptions.add(new ItemOption(117, Util.nextInt(5,15)));
                 trungLinhThu.itemOptions.add(new ItemOption(5, Util.nextInt(15,15)));

            

            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquacaitrang, 1);
            InventoryServiceNew.gI().addItemBag(pl, trungLinhThu);                       
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + trungLinhThu.template.name);                                }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
      private void hopquacaitrang (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item hopquacaitrang = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 962) {
                    hopquacaitrang = item;
                    break;
                }
            }
            if (hopquacaitrang != null){
           Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(1212,1223));
             trungLinhThu.itemOptions.add(new ItemOption(50, Util.nextInt(10,25)));
              trungLinhThu.itemOptions.add(new ItemOption(77, Util.nextInt(10,25)));
               trungLinhThu.itemOptions.add(new ItemOption(103, Util.nextInt(10,25)));
                trungLinhThu.itemOptions.add(new ItemOption(117, Util.nextInt(10,25)));
                 trungLinhThu.itemOptions.add(new ItemOption(5, Util.nextInt(10,25)));
                  trungLinhThu.itemOptions.add(new ItemOption(33, Util.nextInt(0,0)));
                  trungLinhThu.itemOptions.add(new ItemOption(93, Util.nextInt(1,3)));
            

            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquacaitrang, 1);
            InventoryServiceNew.gI().addItemBag(pl, trungLinhThu);                       
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + trungLinhThu.template.name);                                }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        private void hopquapet (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item hopquacaitrang = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 963) {
                    hopquacaitrang = item;
                    break;
                }
            }
            if (hopquacaitrang != null){
           Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(1413,1415));
             trungLinhThu.itemOptions.add(new ItemOption(50, Util.nextInt(5,10)));
              trungLinhThu.itemOptions.add(new ItemOption(77, Util.nextInt(5,10)));
               trungLinhThu.itemOptions.add(new ItemOption(103, Util.nextInt(5,10)));
                trungLinhThu.itemOptions.add(new ItemOption(117, Util.nextInt(5,10)));
                 trungLinhThu.itemOptions.add(new ItemOption(5, Util.nextInt(5,10)));
                  trungLinhThu.itemOptions.add(new ItemOption(93, Util.nextInt(1,3)));
            

            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquacaitrang, 1);
            InventoryServiceNew.gI().addItemBag(pl, trungLinhThu);                       
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + trungLinhThu.template.name);                                }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void top1 (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item top1 = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1255) {
                    top1 = item;
                    break;
                }
            }
            if (top1 != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item ao = ItemService.gI().createNewItem((short)1066);
            Item quan = ItemService.gI().createNewItem((short)1067);
            Item gang = ItemService.gI().createNewItem((short)1068);
            Item giay = ItemService.gI().createNewItem((short)1069);
            Item rada = ItemService.gI().createNewItem((short)1070);
            
             
            nro1s.quantity = 10 ;
            nro2s.quantity = 10 ;
            nro3s.quantity = 10 ;
            nro4s.quantity = 10 ;
            nro5s.quantity = 10 ; 
            nro6s.quantity = 10 ;
            nro7s.quantity = 10 ;
            ao.quantity = 20000 ;
            quan.quantity = 20000 ;
            gang.quantity = 20000 ;
            giay.quantity = 20000 ;
            rada.quantity = 20000 ; 
             nro1s.itemOptions.add(new Item.ItemOption(30,0));
            nro2s.itemOptions.add(new Item.ItemOption(30,0));
            nro3s.itemOptions.add(new Item.ItemOption(30,0));
            nro4s.itemOptions.add(new Item.ItemOption(30,0));
            nro5s.itemOptions.add(new Item.ItemOption(30,0));
            nro6s.itemOptions.add(new Item.ItemOption(30,0));
            nro7s.itemOptions.add(new Item.ItemOption(30,0));
            ao.itemOptions.add(new Item.ItemOption(30,0));
            quan.itemOptions.add(new Item.ItemOption(30,0));
            gang.itemOptions.add(new Item.ItemOption(30,0));
            giay.itemOptions.add(new Item.ItemOption(30,0));
            rada.itemOptions.add(new Item.ItemOption(30,0));
                      
                  
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, top1, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, ao);
            InventoryServiceNew.gI().addItemBag(pl, quan);
            InventoryServiceNew.gI().addItemBag(pl, gang);
            InventoryServiceNew.gI().addItemBag(pl, giay);
            InventoryServiceNew.gI().addItemBag(pl, rada);
            
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ao.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + quan.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + gang.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + giay.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + rada.template.name);
            
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     private void hopquabtc2 (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item hopquabtc2 = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1273) {
                    hopquabtc2 = item;
                    break;
                }
            }
            if (hopquabtc2 != null){           
            Item btc2 = ItemService.gI().createNewItem((short)1164);                                
            int rdUp = Util.nextInt(0, 7);
                    if (rdUp == 0) {
                        btc2.quantity = 100 ;
                    } else if (rdUp == 1) {
                        btc2.quantity = 200 ;
                    } else if (rdUp == 2) {
                         btc2.quantity = 300 ;
                    } else if (rdUp == 3) {
                         btc2.quantity = 400 ;
                    } else if (rdUp == 4) {
                         btc2.quantity = 500 ;
                    } else if (rdUp == 5) {
                        btc2.quantity = 600 ;
                    } else if (rdUp == 6) {
                        btc2.quantity = 900 ;
                    } else if (rdUp == 7) {
                        btc2.quantity = 1500 ;
                    }
                                            
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquabtc2, 1);           
            InventoryServiceNew.gI().addItemBag(pl, btc2);           
            InventoryServiceNew.gI().sendItemBags(pl);           
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + btc2.template.name);
            
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void top2 (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item top2 = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1256) {
                    top2 = item;
                    break;
                }
            }
            if (top2 != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item ao = ItemService.gI().createNewItem((short)1066);
            Item quan = ItemService.gI().createNewItem((short)1067);
            Item gang = ItemService.gI().createNewItem((short)1068);
            Item giay = ItemService.gI().createNewItem((short)1069);
            Item rada = ItemService.gI().createNewItem((short)1070);
            
             
            nro1s.quantity = 7 ;
            nro2s.quantity = 7 ;
            nro3s.quantity = 7 ;
            nro4s.quantity = 7 ;
            nro5s.quantity = 7 ; 
            nro6s.quantity = 7 ;
            nro7s.quantity = 7 ;
            ao.quantity = 15000 ;
            quan.quantity = 15000 ;
            gang.quantity = 15000 ;
            giay.quantity = 15000 ;
            rada.quantity = 15000 ; 
             nro1s.itemOptions.add(new Item.ItemOption(30,0));
            nro2s.itemOptions.add(new Item.ItemOption(30,0));
            nro3s.itemOptions.add(new Item.ItemOption(30,0));
            nro4s.itemOptions.add(new Item.ItemOption(30,0));
            nro5s.itemOptions.add(new Item.ItemOption(30,0));
            nro6s.itemOptions.add(new Item.ItemOption(30,0));
            nro7s.itemOptions.add(new Item.ItemOption(30,0));
            ao.itemOptions.add(new Item.ItemOption(30,0));
            quan.itemOptions.add(new Item.ItemOption(30,0));
            gang.itemOptions.add(new Item.ItemOption(30,0));
            giay.itemOptions.add(new Item.ItemOption(30,0));
            rada.itemOptions.add(new Item.ItemOption(30,0));
                      
                  
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, top2, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, ao);
            InventoryServiceNew.gI().addItemBag(pl, quan);
            InventoryServiceNew.gI().addItemBag(pl, gang);
            InventoryServiceNew.gI().addItemBag(pl, giay);
            InventoryServiceNew.gI().addItemBag(pl, rada);
            
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ao.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + quan.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + gang.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + giay.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + rada.template.name);
            
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void top3 (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item top3 = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1257) {
                    top3 = item;
                    break;
                }
            }
            if (top3 != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item ao = ItemService.gI().createNewItem((short)1066);
            Item quan = ItemService.gI().createNewItem((short)1067);
            Item gang = ItemService.gI().createNewItem((short)1068);
            Item giay = ItemService.gI().createNewItem((short)1069);
            Item rada = ItemService.gI().createNewItem((short)1070);
            
             
            nro1s.quantity = 5 ;
            nro2s.quantity = 5 ;
            nro3s.quantity = 5 ;
            nro4s.quantity = 5 ;
            nro5s.quantity = 5 ; 
            nro6s.quantity = 5 ;
            nro7s.quantity = 5 ;
            ao.quantity = 10000 ;
            quan.quantity = 10000 ;
            gang.quantity = 10000 ;
            giay.quantity = 10000 ;
            rada.quantity = 10000 ; 
             nro1s.itemOptions.add(new Item.ItemOption(30,0));
            nro2s.itemOptions.add(new Item.ItemOption(30,0));
            nro3s.itemOptions.add(new Item.ItemOption(30,0));
            nro4s.itemOptions.add(new Item.ItemOption(30,0));
            nro5s.itemOptions.add(new Item.ItemOption(30,0));
            nro6s.itemOptions.add(new Item.ItemOption(30,0));
            nro7s.itemOptions.add(new Item.ItemOption(30,0));
            ao.itemOptions.add(new Item.ItemOption(30,0));
            quan.itemOptions.add(new Item.ItemOption(30,0));
            gang.itemOptions.add(new Item.ItemOption(30,0));
            giay.itemOptions.add(new Item.ItemOption(30,0));
            rada.itemOptions.add(new Item.ItemOption(30,0));
                      
                  
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, top3, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, ao);
            InventoryServiceNew.gI().addItemBag(pl, quan);
            InventoryServiceNew.gI().addItemBag(pl, gang);
            InventoryServiceNew.gI().addItemBag(pl, giay);
            InventoryServiceNew.gI().addItemBag(pl, rada);
            
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ao.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + quan.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + gang.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + giay.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + rada.template.name);
            
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void top410 (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item top410 = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1258) {
                    top410 = item;
                    break;
                }
            }
            if (top410 != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item ao = ItemService.gI().createNewItem((short)1066);
            Item quan = ItemService.gI().createNewItem((short)1067);
            Item gang = ItemService.gI().createNewItem((short)1068);
            Item giay = ItemService.gI().createNewItem((short)1069);
            Item rada = ItemService.gI().createNewItem((short)1070);
            
             
            nro1s.quantity = 3 ;
            nro2s.quantity = 3 ;
            nro3s.quantity = 3 ;
            nro4s.quantity = 3 ;
            nro5s.quantity = 3 ; 
            nro6s.quantity = 3 ;
            nro7s.quantity = 3 ;
            ao.quantity = 5000 ;
            quan.quantity = 5000 ;
            gang.quantity = 5000 ;
            giay.quantity = 5000 ;
            rada.quantity = 5000 ; 
            nro1s.itemOptions.add(new Item.ItemOption(30,0));
            nro2s.itemOptions.add(new Item.ItemOption(30,0));
            nro3s.itemOptions.add(new Item.ItemOption(30,0));
            nro4s.itemOptions.add(new Item.ItemOption(30,0));
            nro5s.itemOptions.add(new Item.ItemOption(30,0));
            nro6s.itemOptions.add(new Item.ItemOption(30,0));
            nro7s.itemOptions.add(new Item.ItemOption(30,0));
            ao.itemOptions.add(new Item.ItemOption(30,0));
            quan.itemOptions.add(new Item.ItemOption(30,0));
            gang.itemOptions.add(new Item.ItemOption(30,0));
            giay.itemOptions.add(new Item.ItemOption(30,0));
            rada.itemOptions.add(new Item.ItemOption(30,0));
                      
                  
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, top410, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, ao);
            InventoryServiceNew.gI().addItemBag(pl, quan);
            InventoryServiceNew.gI().addItemBag(pl, gang);
            InventoryServiceNew.gI().addItemBag(pl, giay);
            InventoryServiceNew.gI().addItemBag(pl, rada);
            
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ao.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + quan.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + gang.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + giay.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + rada.template.name);
            
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   private void hopquat1SM (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 11) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 11 ô trống hành trang");
                return;
            }
            Item hopquat1SM = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1263) {
                    hopquat1SM = item;
                    break;
                }
            }
            if (hopquat1SM != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item dl = ItemService.gI().createNewItem((short)1980);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            tv.quantity = 500 ;
            hn.quantity = 50000 ; 
            nro1s.quantity = 10 ;
            nro2s.quantity = 10 ;
            nro3s.quantity = 10 ;
            nro4s.quantity = 10 ;
            nro5s.quantity = 10 ; 
            nro6s.quantity = 10 ;
            nro7s.quantity = 10 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(25,25)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(25,25)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(25,25)));                   
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat1SM, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat2SM (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 11) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 11 ô trống hành trang");
                return;
            }
            Item hopquat2SM = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1264) {
                    hopquat2SM = item;
                    break;
                }
            }
            if (hopquat2SM != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item dl = ItemService.gI().createNewItem((short)1981);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            tv.quantity = 300 ;
            hn.quantity = 40000 ; 
            nro1s.quantity = 6 ;
            nro2s.quantity = 6 ;
            nro3s.quantity = 6 ;
            nro4s.quantity = 6 ;
            nro5s.quantity = 6 ; 
            nro6s.quantity = 6 ;
            nro7s.quantity = 6 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(20,20)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(20,20)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(20,20)));                   
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat2SM, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat3SM (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 11) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 11 ô trống hành trang");
                return;
            }
            Item hopquat3SM = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1265) {
                    hopquat3SM = item;
                    break;
                }
            }
            if (hopquat3SM != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item dl = ItemService.gI().createNewItem((short)1982);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            tv.quantity = 100 ;
            hn.quantity = 30000 ; 
            nro1s.quantity = 3 ;
            nro2s.quantity = 3 ;
            nro3s.quantity = 3 ;
            nro4s.quantity = 3 ;
            nro5s.quantity = 3 ; 
            nro6s.quantity = 3 ;
            nro7s.quantity = 3 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(15,15)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(15,15)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(15,15)));                   
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat3SM, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat1nap(Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item hopquat1nap = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1266) {
                    hopquat1nap = item;
                    break;
                }
            }
            if (hopquat1nap != null){
            Item ct = ItemService.gI().createNewItem((short)1144);
            Item dl = ItemService.gI().createNewItem((short)1235);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            Item kc = ItemService.gI().createNewItem((short)1998);
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            tv.quantity = 1000 ;
            hn.quantity = 200000 ;
            kc.quantity = 200000 ;
            ct.itemOptions.add(new ItemOption(50, Util.nextInt(45,45)));
            ct.itemOptions.add(new ItemOption(77, Util.nextInt(45,45)));
            ct.itemOptions.add(new ItemOption(103, Util.nextInt(45,45)));
            ct.itemOptions.add(new ItemOption(14, Util.nextInt(30,30))); 
            ct.itemOptions.add(new ItemOption(5, Util.nextInt(50,50)));
            ct.itemOptions.add(new ItemOption(117, Util.nextInt(30,30)));
            ct.itemOptions.add(new ItemOption(116, Util.nextInt(0,0)));
            ct.itemOptions.add(new ItemOption(106, Util.nextInt(0,0)));
            nro1s.quantity = 25 ;
            nro2s.quantity = 25 ;
            nro3s.quantity = 25 ;
            nro4s.quantity = 25 ;
            nro5s.quantity = 25 ; 
            nro6s.quantity = 25 ;
            nro7s.quantity = 25 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(35,35)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(35,35)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(35,35)));
            dl.itemOptions.add(new ItemOption(0, Util.nextInt(10000,10000))); 
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat1nap, 1);
            InventoryServiceNew.gI().addItemBag(pl, ct);           
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().addItemBag(pl, kc);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + kc.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat2nap (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item hopquat2nap = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1267) {
                    hopquat2nap = item;
                    break;
                }
            }
            if (hopquat2nap != null){
            Item ct = ItemService.gI().createNewItem((short)1144);
            Item dl = ItemService.gI().createNewItem((short)1236);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            Item kc = ItemService.gI().createNewItem((short)1998);
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            tv.quantity = 700 ;
            hn.quantity = 150000 ; 
            kc.quantity = 150000 ;
            ct.itemOptions.add(new ItemOption(50, Util.nextInt(40,40)));
            ct.itemOptions.add(new ItemOption(77, Util.nextInt(40,40)));
            ct.itemOptions.add(new ItemOption(103, Util.nextInt(40,40)));
            ct.itemOptions.add(new ItemOption(14, Util.nextInt(27,27))); 
            ct.itemOptions.add(new ItemOption(5, Util.nextInt(50,50)));
            ct.itemOptions.add(new ItemOption(117, Util.nextInt(27,27)));
            ct.itemOptions.add(new ItemOption(116, Util.nextInt(0,0)));
            ct.itemOptions.add(new ItemOption(106, Util.nextInt(0,0)));
            nro1s.quantity = 20 ;
            nro2s.quantity = 20 ;
            nro3s.quantity = 20 ;
            nro4s.quantity = 20 ;
            nro5s.quantity = 20 ; 
            nro6s.quantity = 20 ;
            nro7s.quantity = 20 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(30,30)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(30,30)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(30,30)));
            dl.itemOptions.add(new ItemOption(0, Util.nextInt(8000,8000))); 
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat2nap, 1);
            InventoryServiceNew.gI().addItemBag(pl, ct);           
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().addItemBag(pl, kc);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + kc.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat3nap (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item hopquat3nap = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1268) {
                    hopquat3nap = item;
                    break;
                }
            }
            if (hopquat3nap != null){
            Item ct = ItemService.gI().createNewItem((short)1144);
            Item dl = ItemService.gI().createNewItem((short)1237);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            Item kc = ItemService.gI().createNewItem((short)1998);
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            tv.quantity = 500 ;
            hn.quantity = 100000 ;
            kc.quantity = 100000 ;
            ct.itemOptions.add(new ItemOption(50, Util.nextInt(35,35)));
            ct.itemOptions.add(new ItemOption(77, Util.nextInt(35,35)));
            ct.itemOptions.add(new ItemOption(103, Util.nextInt(35,35)));
            ct.itemOptions.add(new ItemOption(14, Util.nextInt(25,25))); 
            ct.itemOptions.add(new ItemOption(5, Util.nextInt(50,50)));
            ct.itemOptions.add(new ItemOption(117, Util.nextInt(25,25)));
            ct.itemOptions.add(new ItemOption(116, Util.nextInt(0,0)));
            ct.itemOptions.add(new ItemOption(106, Util.nextInt(0,0)));
            nro1s.quantity = 15 ;
            nro2s.quantity = 15 ;
            nro3s.quantity = 15 ;
            nro4s.quantity = 15 ;
            nro5s.quantity = 15 ; 
            nro6s.quantity = 15 ;
            nro7s.quantity = 15 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(25,25)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(25,25)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(25,25)));
            dl.itemOptions.add(new ItemOption(0, Util.nextInt(7000,7000))); 
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat3nap, 1);
            InventoryServiceNew.gI().addItemBag(pl, ct);           
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().addItemBag(pl, kc);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + kc.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat4nap (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item hopquat4nap = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1270) {
                    hopquat4nap = item;
                    break;
                }
            }
            if (hopquat4nap != null){
//            Item ct = ItemService.gI().createNewItem((short)1144);
            Item dl = ItemService.gI().createNewItem((short)1228);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            Item kc = ItemService.gI().createNewItem((short)1998);
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            tv.quantity = 300 ;
            hn.quantity = 50000 ;
            kc.quantity = 50000 ;
 //           ct.itemOptions.add(new ItemOption(50, Util.nextInt(35,35)));
 //           ct.itemOptions.add(new ItemOption(77, Util.nextInt(35,35)));
  //          ct.itemOptions.add(new ItemOption(103, Util.nextInt(35,35)));
  //          ct.itemOptions.add(new ItemOption(14, Util.nextInt(25,25))); 
 //           ct.itemOptions.add(new ItemOption(5, Util.nextInt(50,50)));
  //          ct.itemOptions.add(new ItemOption(117, Util.nextInt(25,25)));
  //          ct.itemOptions.add(new ItemOption(116, Util.nextInt(0,0)));
   //         ct.itemOptions.add(new ItemOption(106, Util.nextInt(0,0)));
            nro1s.quantity = 5 ;
            nro2s.quantity = 5 ;
            nro3s.quantity = 5 ;
            nro4s.quantity = 5 ;
            nro5s.quantity = 5 ; 
            nro6s.quantity = 5 ;
            nro7s.quantity = 5 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(15,15)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(15,15)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(15,15)));
            dl.itemOptions.add(new ItemOption(0, Util.nextInt(5000,5000))); 
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat4nap, 1);
  //          InventoryServiceNew.gI().addItemBag(pl, ct);           
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().addItemBag(pl, kc);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().sendItemBags(pl);
 //           Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ct.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + kc.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat5nap (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 9) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 9 ô trống hành trang");
                return;
            }
            Item hopquat5nap = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1271) {
                    hopquat5nap = item;
                    break;
                }
            }
            if (hopquat5nap != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item banh2trung = ItemService.gI().createNewItem((short)466);           
            Item tv = ItemService.gI().createNewItem((short)457); 
            
            tv.quantity = 500 ;
            
            nro1s.quantity = 1 ;
            nro2s.quantity = 1 ;
            nro3s.quantity = 11 ;
            nro4s.quantity = 1 ;
            nro5s.quantity = 1 ; 
            nro6s.quantity = 1 ;
            nro7s.quantity = 1 ;
            banh2trung.quantity = 1 ;
            
            tv.itemOptions.add(new Item.ItemOption(30,0));
                               
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat5nap, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, banh2trung);
            
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + banh2trung.template.name);          
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquadenbu (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 13) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 13 ô trống hành trang");
                return;
            }
            Item hopquadenbu = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1272) {
                    hopquadenbu = item;
                    break;
                }
            }
            if (hopquadenbu != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item ao = ItemService.gI().createNewItem((short)1066);
            Item quan = ItemService.gI().createNewItem((short)1067); 
            Item gang = ItemService.gI().createNewItem((short)1068); 
            Item giay = ItemService.gI().createNewItem((short)1069); 
            Item nhan = ItemService.gI().createNewItem((short)1070); 
            ao.quantity = 99 ;
            quan.quantity = 99 ;
            gang.quantity = 99 ;
            giay.quantity = 99 ;
            nhan.quantity = 99 ;
            nro1s.quantity = 1 ;
            nro2s.quantity = 1 ;
            nro3s.quantity = 1 ;
            nro4s.quantity = 1 ;
            nro5s.quantity = 1 ; 
            nro6s.quantity = 1 ;
            nro7s.quantity = 1 ;
            ao.itemOptions.add(new Item.ItemOption(30,0));
            quan.itemOptions.add(new Item.ItemOption(30,0));
            gang.itemOptions.add(new Item.ItemOption(30,0));
            giay.itemOptions.add(new Item.ItemOption(30,0));
            nhan.itemOptions.add(new Item.ItemOption(30,0));
            nro1s.itemOptions.add(new Item.ItemOption(30,0));
            nro2s.itemOptions.add(new Item.ItemOption(30,0));
            nro3s.itemOptions.add(new Item.ItemOption(30,0));
            nro4s.itemOptions.add(new Item.ItemOption(30,0));
            nro5s.itemOptions.add(new Item.ItemOption(30,0));
            nro6s.itemOptions.add(new Item.ItemOption(30,0));
            nro7s.itemOptions.add(new Item.ItemOption(30,0));
            
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquadenbu, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, ao);
            InventoryServiceNew.gI().addItemBag(pl, quan);
            InventoryServiceNew.gI().addItemBag(pl, gang);
            InventoryServiceNew.gI().addItemBag(pl, giay);
            InventoryServiceNew.gI().addItemBag(pl, nhan);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + ao.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + quan.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + gang.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + giay.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nhan.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat1NV (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 11) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 11 ô trống hành trang");
                return;
            }
            Item hopquat1NV = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1259) {
                    hopquat1NV = item;
                    break;
                }
            }
            if (hopquat1NV != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item dl = ItemService.gI().createNewItem((short)1229);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            tv.quantity = 500 ;
            hn.quantity = 50000 ; 
            nro1s.quantity = 10 ;
            nro2s.quantity = 10 ;
            nro3s.quantity = 10 ;
            nro4s.quantity = 10 ;
            nro5s.quantity = 10 ; 
            nro6s.quantity = 10 ;
            nro7s.quantity = 10 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(25,25)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(25,25)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(25,25)));                   
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat1NV, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat2NV (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 4) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 5 ô trống hành trang");
                return;
            }
            Item hopquat2NV = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1260) {
                    hopquat2NV = item;
                    break;
                }
            }
            if (hopquat2NV != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item dl = ItemService.gI().createNewItem((short)1230);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            tv.quantity = 300 ;
            hn.quantity = 40000 ; 
            nro1s.quantity = 6 ;
            nro2s.quantity = 6 ;
            nro3s.quantity = 6 ;
            nro4s.quantity = 6 ;
            nro5s.quantity = 6 ; 
            nro6s.quantity = 6 ;
            nro7s.quantity = 6 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(20,20)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(20,20)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(20,20)));                   
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat2NV, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
private void hopquat3NV (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 4) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 5 ô trống hành trang");
                return;
            }
            Item hopquat3NV = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1261) {
                    hopquat3NV = item;
                    break;
                }
            }
            if (hopquat3NV != null){
            Item nro1s = ItemService.gI().createNewItem((short)14);
            Item nro2s = ItemService.gI().createNewItem((short)15);
            Item nro3s = ItemService.gI().createNewItem((short)16);
            Item nro4s = ItemService.gI().createNewItem((short)17);
            Item nro5s = ItemService.gI().createNewItem((short)18);
            Item nro6s = ItemService.gI().createNewItem((short)19);
            Item nro7s = ItemService.gI().createNewItem((short)20);
            Item dl = ItemService.gI().createNewItem((short)1231);
            Item tv = ItemService.gI().createNewItem((short)457); 
            Item hn = ItemService.gI().createNewItem((short)861);
            tv.quantity = 100 ;
            hn.quantity = 30000 ; 
            nro1s.quantity = 3 ;
            nro2s.quantity = 3 ;
            nro3s.quantity = 3 ;
            nro4s.quantity = 3 ;
            nro5s.quantity = 3 ; 
            nro6s.quantity = 3 ;
            nro7s.quantity = 3 ;
            tv.itemOptions.add(new Item.ItemOption(30,0));
            dl.itemOptions.add(new ItemOption(50, Util.nextInt(15,15)));
            dl.itemOptions.add(new ItemOption(77, Util.nextInt(15,15)));
            dl.itemOptions.add(new ItemOption(103, Util.nextInt(15,15)));
        
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquat3NV, 1);
            InventoryServiceNew.gI().addItemBag(pl, nro1s);
            InventoryServiceNew.gI().addItemBag(pl, nro2s);
            InventoryServiceNew.gI().addItemBag(pl, nro3s);
            InventoryServiceNew.gI().addItemBag(pl, nro4s);
            InventoryServiceNew.gI().addItemBag(pl, nro5s);
            InventoryServiceNew.gI().addItemBag(pl, nro6s);
            InventoryServiceNew.gI().addItemBag(pl, nro7s);
            InventoryServiceNew.gI().addItemBag(pl, dl);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, hn);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro1s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro2s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro3s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro4s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro5s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro6s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + nro7s.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dl.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hn.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  public void usethoivang(Player player) {
        Item tv = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.id == 457) {
                tv = item;
                break;
            }
        }
        if (tv != null) {
            if(player.inventory.gold <= 1999999999999L){
                InventoryServiceNew.gI().subQuantityItemsBag(player, tv, 1);
                player.inventory.gold += 500000000;
                PlayerService.gI().sendInfoHpMpMoney(player);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "không được vượt quá 2000 tỷ vàng");
            }     
        }
    } 
    public void UseCard(Player pl, Item item){
        RadarCard radarTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(c -> c.Id == item.template.id).findFirst().orElse(null);
        if(radarTemplate == null) return;
        if (radarTemplate.Require != -1){
            RadarCard radarRequireTemplate = RadarService.gI().RADAR_TEMPLATE.stream().filter(r -> r.Id == radarTemplate.Require).findFirst().orElse(null); 
            if(radarRequireTemplate == null) return;
            Card cardRequire = pl.Cards.stream().filter(r -> r.Id == radarRequireTemplate.Id).findFirst().orElse(null);  
            if (cardRequire == null || cardRequire.Level < radarTemplate.RequireLevel){
                Service.gI().sendThongBao(pl,"Bạn cần sưu tầm "+radarRequireTemplate.Name+" ở cấp độ "+radarTemplate.RequireLevel+" mới có thể sử dụng thẻ này");
                return;
            }
        }
        Card card = pl.Cards.stream().filter(r->r.Id == item.template.id).findFirst().orElse(null);
        if (card == null){
            Card newCard = new Card(item.template.id,(byte)1,radarTemplate.Max,(byte)-1,radarTemplate.Options);
            if (pl.Cards.add(newCard)){
                RadarService.gI().RadarSetAmount(pl,newCard.Id, newCard.Amount, newCard.MaxAmount);
                RadarService.gI().RadarSetLevel(pl,newCard.Id, newCard.Level);
                InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                InventoryServiceNew.gI().sendItemBags(pl);
            }
        }else{
            if (card.Level >= 2){
                Service.gI().sendThongBao(pl,"Thẻ này đã đạt cấp tối đa");
                return;
            }
            card.Amount++;
            if (card.Amount >= card.MaxAmount){
                card.Amount = 0;
                if (card.Level == -1){
                    card.Level = 1;
                }else {
                    card.Level++;
                }
                Service.gI().point(pl);
            }
            RadarService.gI().RadarSetAmount(pl,card.Id, card.Amount, card.MaxAmount);
            RadarService.gI().RadarSetLevel(pl,card.Id, card.Level);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
        }
    }

    private void useItemChangeFlagBag(Player player, Item item) {
        switch (item.template.id) {
            case 994: //vỏ ốc
                break;
            case 995: //cây kem
                break;
            case 996: //cá heo
                break;
            case 997: //con diều
                break;
            case 998: //diều rồng
                break;
            case 999: //mèo mun
                if (!player.effectFlagBag.useMeoMun) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useMeoMun = !player.effectFlagBag.useMeoMun;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1000: //xiên cá
                if (!player.effectFlagBag.useXienCa) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useXienCa = !player.effectFlagBag.useXienCa;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1001: //phóng heo
                if (!player.effectFlagBag.usePhongHeo) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.usePhongHeo = !player.effectFlagBag.usePhongHeo;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
            case 1202: //Hào quang
                if (!player.effectFlagBag.useHaoQuang) {
                    player.effectFlagBag.reset();
                    player.effectFlagBag.useHaoQuang = !player.effectFlagBag.useHaoQuang;
                } else {
                    player.effectFlagBag.reset();
                }
                break;
        }
        Service.gI().point(player);
        Service.gI().sendFlagBag(player);
    }

    private void changePet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender + 1;
            if (gender > 2) {
                gender = 0;
            }
            PetService.gI().changeNormalPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    private void changeBerusPet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changeBerusPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }
    
 private void changePetPic(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changePicPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }
 private void changeGokuPet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changeGokuPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }
 private void changeCumberPet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changeCumberPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }
 private void changeWhisPet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changeWhisPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }
 private void changeBillPet(Player player, Item item) {
        if (player.pet != null) {
            int gender = player.pet.gender;
            PetService.gI().changeBillPet(player, gender);
            InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }
private void openDaBaoVe(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 987, 987 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.itemOptions.add(new ItemOption(73, 0));
        newItem.quantity = (short) Util.nextInt(1, 10);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openSPL(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 441, 442, 443, 444, 445, 446, 447 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.itemOptions.add(new ItemOption(73, 0));
        newItem.quantity = (short) Util.nextInt(1, 10);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openDaNangCap(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 220, 221, 222, 223, 224 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.itemOptions.add(new ItemOption(73, 0));
        newItem.quantity = (short) Util.nextInt(1, 10);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openManhTS(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 1066, 1067, 1068, 1069, 1070 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.itemOptions.add(new ItemOption(73, 0));
        newItem.quantity = (short) Util.nextInt(1, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}
private void openManhTA(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 1232,1233,1234 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.itemOptions.add(new ItemOption(73, 0));
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau1(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 13, 13 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau2(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 60, 60 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau3(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 61, 61 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau4(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 62, 62 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau5(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 63, 63 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau6(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 64, 64 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau7(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 65, 65 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau8(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 352, 352 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}

private void openGoiDau9(Player player, Item item) {
    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
        short[] possibleItems = { 523, 523 };
        byte selectedIndex = (byte) Util.nextInt(0, possibleItems.length - 2);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        Item newItem = ItemService.gI().createNewItem(possibleItems[selectedIndex]);
        newItem.quantity = (short) Util.nextInt(99, 99);
        InventoryServiceNew.gI().addItemBag(player, newItem);
        icon[1] = newItem.template.iconID;

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);

        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    } else {
        Service.gI().sendThongBao(player, "Hàng trang đã đầy");
    }
}
 
    private void openPhieuCaiTrangHaiTac(Player pl, Item item) {
        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
            Item ct = ItemService.gI().createNewItem((short) Util.nextInt(618, 626));
            ct.itemOptions.add(new ItemOption(147, 25));
            ct.itemOptions.add(new ItemOption(77, 20));
            ct.itemOptions.add(new ItemOption(103, 20));
            ct.itemOptions.add(new ItemOption(149, 0));
            if (item.template.id == 2006) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
            } else if (item.template.id == 2007) {
                ct.itemOptions.add(new ItemOption(93, Util.nextInt(7, 30)));
            }
            InventoryServiceNew.gI().addItemBag(pl, ct);
            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
            InventoryServiceNew.gI().sendItemBags(pl);
            CombineServiceNew.gI().sendEffectOpenItem(pl, item.template.iconID, ct.template.iconID);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

    private void eatGrapes(Player pl, Item item) {
        int percentCurrentStatima = pl.nPoint.stamina * 100 / pl.nPoint.maxStamina;
        if (percentCurrentStatima > 50) {
            Service.gI().sendThongBao(pl, "Thể lực vẫn còn trên 50%");
            return;
        } else if (item.template.id == 211) {
            pl.nPoint.stamina = pl.nPoint.maxStamina;
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 100%");
        } else if (item.template.id == 212) {
            pl.nPoint.stamina += (pl.nPoint.maxStamina * 20 / 100);
            Service.gI().sendThongBao(pl, "Thể lực của bạn đã được hồi phục 20%");
        }
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        InventoryServiceNew.gI().sendItemBags(pl);
        PlayerService.gI().sendCurrentStamina(pl);
    }

//    private void openCSKB(Player pl, Item item) {
//        if (InventoryServiceNew.gI().getCountEmptyBag(pl) > 0) {
//            short[] temp = {1099,1100,1101,1102,1099};
//            int[][] gold = {{5000, 20000}};
//            byte index = (byte) Util.nextInt(0, temp.length - 1);
//            short[] icon = new short[2];
//            icon[0] = item.template.iconID;
//            if (index <= 3) {
//                pl.inventory.gold += Util.nextInt(gold[0][0], gold[0][1]);
//                if (pl.inventory.gold > Inventory.LIMIT_GOLD) {
//                    pl.inventory.gold = Inventory.LIMIT_GOLD;
//                }
//                PlayerService.gI().sendInfoHpMpMoney(pl);
//                icon[1] = 930;
//            } else {
//                Item it = ItemService.gI().createNewItem(temp[index]);
//                it.itemOptions.add(new ItemOption(73, 0));
//                InventoryServiceNew.gI().addItemBag(pl, it);
//                icon[1] = it.template.iconID;
//            }
//            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
//            InventoryServiceNew.gI().sendItemBags(pl);
//
//            CombineServiceNew.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
//        } else {
//            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
//        }
//    }
    private void openCSKB (Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 1) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 1 ô trống hành trang");
                return;
            }
            Item openCSKB = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 380) {
                    openCSKB = item;
                    break;
                }
            }
            if (openCSKB != null){
           Item trungLinhThu = ItemService.gI().createNewItem((short) Util.nextInt(381,385));
           trungLinhThu.quantity = 1 ;
            InventoryServiceNew.gI().subQuantityItemsBag(pl, openCSKB, 1);
            InventoryServiceNew.gI().addItemBag(pl, trungLinhThu);                       
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + trungLinhThu.template.name);                                }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     private void useItemHopQuaTanThu(Player pl) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(pl) <= 9) {
                Service.getInstance().sendThongBao(pl, "Bạn phải có ít nhất 10 ô trống hành trang");
                return;
            }
            Item hopquatanthu = null;
            for (Item item : pl.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1098) {
                    hopquatanthu = item;
                    break;
                }
            }
            if (hopquatanthu != null){
            Item gang = ItemService.gI().createNewItem((short)2011);
            Item tv = ItemService.gI().createNewItem((short)457);
            Item dh = ItemService.gI().createNewItem((short)1205);
            Item glt = ItemService.gI().createNewItem((short)531);
            Item bay = ItemService.gI().createNewItem((short)920);
            Item tdt = ItemService.gI().createNewItem((short)194);
            Item hongngoc = ItemService.gI().createNewItem((short)861);
            Item br = ItemService.gI().createNewItem((short)1108);
            Item hopquact = ItemService.gI().createNewItem((short)1278);
            tv.quantity = 2500 ;
            tdt.quantity = 1 ;
            br.quantity = 1 ;
            hopquact.quantity = 1 ;
            hongngoc.quantity = 50000 ;
            dh.itemOptions.add(new ItemOption(147, Util.nextInt(5,10)));
            dh.itemOptions.add(new ItemOption(77, Util.nextInt(5,10)));
            dh.itemOptions.add(new ItemOption(103, Util.nextInt(5,10)));
            dh.itemOptions.add(new ItemOption(95,Util.nextInt(5,10)));
            dh.itemOptions.add(new ItemOption(96,Util.nextInt(5,10)));
            dh.itemOptions.add(new ItemOption(30,0));
            dh.itemOptions.add(new Item.ItemOption(93, Util.nextInt(0,5)));
            gang.itemOptions.add(new ItemOption(147, Util.nextInt(10,20)));
            gang.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10,20)));
            gang.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10,20)));
            gang.itemOptions.add(new Item.ItemOption(101, Util.nextInt(15,30)));
            gang.itemOptions.add(new Item.ItemOption(30,0));
            gang.itemOptions.add(new Item.ItemOption(93, Util.nextInt(0,5)));
            glt.itemOptions.add(new ItemOption(9, Util.nextInt(1,1000)));
            bay.itemOptions.add(new ItemOption(101, Util.nextInt(1,20)));
            bay.itemOptions.add(new ItemOption(93, Util.nextInt(1,5)));
            bay.itemOptions.add(new ItemOption(84,0));
            tv.itemOptions.add(new Item.ItemOption(30,0));
            br.itemOptions.add(new Item.ItemOption(30,0));
            tdt.itemOptions.add(new Item.ItemOption(30,0));
            hopquact.itemOptions.add(new Item.ItemOption(30,0));
            InventoryServiceNew.gI().subQuantityItemsBag(pl, hopquatanthu, 1);
            InventoryServiceNew.gI().addItemBag(pl, gang);
            InventoryServiceNew.gI().addItemBag(pl, dh);
            InventoryServiceNew.gI().addItemBag(pl, glt);
            InventoryServiceNew.gI().addItemBag(pl, tv);
            InventoryServiceNew.gI().addItemBag(pl, br);
            InventoryServiceNew.gI().addItemBag(pl, bay);
            InventoryServiceNew.gI().addItemBag(pl, tdt);
             InventoryServiceNew.gI().addItemBag(pl, hongngoc);
             InventoryServiceNew.gI().addItemBag(pl, hopquact);
            InventoryServiceNew.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + gang.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + glt.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + bay.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tv.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + dh.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + br.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + tdt.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hongngoc.template.name);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + hopquact.template.name);
            }          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
             case 903: //banh sau
                if (pl.itemTime.isUseBanhSau == true) {
                    Service.getInstance().sendThongBao(pl, "Ae ơi tôi phê quá");
                    return;
                }
                pl.itemTime.lastTimeBanhSau = System.currentTimeMillis();
                pl.itemTime.isUseBanhSau = true;
                break;
            case 902: //bánh nhện
                if (pl.itemTime.isUseBanhNhen == true) {
                    Service.getInstance().sendThongBao(pl, "Trúng mẹ nó độc rồi ae cứu! ");
                    return;
                }
                pl.itemTime.lastTimeBanhNhen = System.currentTimeMillis();
                pl.itemTime.isUseBanhNhen = true;
                break;
            case 900: //súp bí
                if (pl.itemTime.isUseSupBi == true) {
                    Service.getInstance().sendThongBao(pl, "Muì vị khá tuyệt vời đấy");
                    return;
                }
                pl.itemTime.lastTimeSupBi = System.currentTimeMillis();
                pl.itemTime.isUseSupBi = true;
                Service.getInstance().point(pl);
                break;
            case 899: //kẹo 1 mắt
                if (pl.itemTime.isUseKeoMotMat == true) {
                    Service.getInstance().sendThongBao(pl, "Nhin ghê mà ăn vẫn ngon");
                    return;
                }
                pl.itemTime.lastTimeKeoMotMat = System.currentTimeMillis();
                pl.itemTime.isUseKeoMotMat = true;
                break;
            case 753://bánh chưng
                if (pl.itemTime.isUseBanhChung == true) {
                    Service.getInstance().sendThongBao(pl, "Bánh Chưng Vô Địch");
                    return;
                }
                pl.itemTime.lastTimeBanhChung = System.currentTimeMillis();
                pl.itemTime.isUseBanhChung = true;
                break;
            case 752://bánh tét
                if (pl.itemTime.isUseBanhTet == true) {
                    Service.getInstance().sendThongBao(pl, "Bánh Tét Vô Địch");
                    return;
                }
                pl.itemTime.lastTimeBanhTet = System.currentTimeMillis();
                pl.itemTime.isUseBanhTet = true;
                break;
            case 382: //bổ huyết
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                break;
            case 383: //bổ khí
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                break;
            case 384: //giáp xên
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                break;
            case 381: //cuồng nộ
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.gI().point(pl);
                break;
            case 385: //ẩn danh
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 379: //máy dò capsule
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 1099:// cn
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                Service.gI().point(pl);

                break;
            case 1100:// bo huyet
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                break;
            case 1101://bo khi
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                break;
            case 1102://xbh
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                break;
            case 1103://an danh
                pl.itemTime.lastTimeAnDanh2 = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh2 = true;
                break;
            case 1016:
                pl.itemTime.lastTimeThuocmo= System.currentTimeMillis();
                pl.itemTime.isUseThuocmo = true;
                break;
            case 1017:
                pl.itemTime.lastTimeThuocmo2 = System.currentTimeMillis();
                pl.itemTime.isUseThuocmo2 = true;
                break;
             case 472:
                pl.itemTime.lastTimeTrungthu = System.currentTimeMillis();
                pl.itemTime.isUseTrungthu = true;
                break;
                 case 473:
                pl.itemTime.lastTimeHoptrungthu= System.currentTimeMillis();
                pl.itemTime.isUseHoptrungthu = true;
                break;   
            case 663: //bánh pudding
            case 664: //xúc xíc
            case 665: //kem dâu
            case 666: //mì ly
            case 667: //sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
            case 2037: //máy dò đồ
                pl.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis();
                pl.itemTime.isUseMayDo2 = true;
                break;
        }
        Service.gI().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        InventoryServiceNew.gI().sendItemBags(pl);
    }

    private void controllerCallRongThan(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONG_1_SAO && tempId <= SummonDragon.NGOC_RONG_7_SAO) {
            switch (tempId) {
                case SummonDragon.NGOC_RONG_1_SAO:
                case SummonDragon.NGOC_RONG_2_SAO:
                case SummonDragon.NGOC_RONG_3_SAO:
                    SummonDragon.gI().openMenuSummonShenron(pl, (byte) (tempId - 13));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGON,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 3 sao, 2 sao, 1 sao", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
    }
private void controllerCalltrb(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= SummonDragon.NGOC_RONGTRB1 && tempId <= SummonDragon.NGOC_RONGTRB3) {
            switch (tempId) {
                case SummonDragon.NGOC_RONGTRB1:
                    SummonDragon.gI().openMenuSummonShenronTRB(pl, (byte) (tempId - 1165));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_SUMMON_DRAGONTRB,
                            -1, "Bạn chỉ có thể gọi rồng từ ngọc 1 sao TRB ", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
    }
private void controllerCallrx(Player pl, Item item) {
        int tempId = item.template.id;
        if (tempId >= GoiRongXuong.XUONG_1_SAO) {
            switch (tempId) {
                case GoiRongXuong.XUONG_1_SAO:
                    GoiRongXuong.gI().openMenuRongXuong(pl, (byte) (tempId - 701));
                    break;
                default:
                    NpcService.gI().createMenuConMeo(pl, ConstNpc.TUTORIAL_RONG_XUONG,
                            -1, "Bạn chỉ có thể gọi rồng từ bí ngô 1 sao  ", "Hướng\ndẫn thêm\n(mới)", "OK");
                    break;
            }
        }
    }
    private void learnSkill(Player pl, Item item) {
        Message msg;
        try {
            if (item.template.gender == pl.gender || item.template.gender == 3) {
                String[] subName = item.template.name.split("");
                byte level = Byte.parseByte(subName[subName.length - 1]);
                Skill curSkill = SkillUtil.getSkillByItemID(pl, item.template.id);
                if (curSkill.point == 7) {
                    Service.gI().sendThongBao(pl, "Kỹ năng đã đạt tối đa!");
                } else {
                    if (curSkill.point == 0) {
                        if (level == 1) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 23);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Skill skillNeed = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            Service.gI().sendThongBao(pl, "Vui lòng học " + skillNeed.template.name + " cấp " + skillNeed.point + " trước!");
                        }
                    } else {
                        if (curSkill.point + 1 == level) {
                            curSkill = SkillUtil.createSkill(SkillUtil.getTempSkillSkillByItemID(item.template.id), level);
                            //System.out.println(curSkill.template.name + " - " + curSkill.point);
                            SkillUtil.setSkill(pl, curSkill);
                            InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                            msg = Service.gI().messageSubCommand((byte) 62);
                            msg.writer().writeShort(curSkill.skillId);
                            pl.sendMessage(msg);
                            msg.cleanup();
                        } else {
                            Service.gI().sendThongBao(pl, "Vui lòng học " + curSkill.template.name + " cấp " + (curSkill.point + 1) + " trước!");
                        }
                    }
                    InventoryServiceNew.gI().sendItemBags(pl);
                }
            } else {
                Service.gI().sendThongBao(pl, "Không thể thực hiện");
            }
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    private void useTDLT(Player pl, Item item) {
        if (pl.itemTime.isUseTDLT) {
            ItemTimeService.gI().turnOffTDLT(pl, item);
        } else {
            ItemTimeService.gI().turnOnTDLT(pl, item);
        }
    }

    private void usePorata(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }
    private void usePorata2(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 4 || pl.fusion.typeFusion == 6 || pl.fusion.typeFusion == 10 || pl.fusion.typeFusion == 12) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion2(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }
    private void usePorata4(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 22 || pl.fusion.typeFusion == 24 || pl.fusion.typeFusion == 28 || pl.fusion.typeFusion == 30) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion4(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }
private void usePorata3(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 12 || pl.fusion.typeFusion == 14 || pl.fusion.typeFusion == 18 || pl.fusion.typeFusion == 20) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion3(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }
private void usePorata5(Player pl) {
        if (pl.pet == null || pl.fusion.typeFusion == 32 || pl.fusion.typeFusion == 34 || pl.fusion.typeFusion == 38 || pl.fusion.typeFusion == 40) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
        } else {
            if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                pl.pet.fusion5(true);
            } else {
                pl.pet.unFusion();
            }
        }
    }

    private void openCapsuleUI(Player pl) {
        pl.iDMark.setTypeChangeMap(ConstMap.CHANGE_CAPSULE);
        ChangeMapService.gI().openChangeMapTab(pl);
    }

    public void choseMapCapsule(Player pl, int index) {
        int zoneId = -1;
        Zone zoneChose = pl.mapCapsule.get(index);
        //Kiểm tra số lượng người trong khu

        if (zoneChose.getNumOfPlayers() > 25
                || MapService.gI().isMapDoanhTrai(zoneChose.map.mapId)
                || MapService.gI().isMapBanDoKhoBau(zoneChose.map.mapId)
                || MapService.gI().isMapKhiGas(zoneChose.map.mapId)
                || MapService.gI().isMapMaBu(zoneChose.map.mapId)
                || MapService.gI().isMapHuyDiet(zoneChose.map.mapId)) {
            Service.gI().sendThongBao(pl, "Hiện tại không thể vào được khu!");
            return;
        }
        if (index != 0 || zoneChose.map.mapId == 21
                || zoneChose.map.mapId == 22
                || zoneChose.map.mapId == 23) {
            pl.mapBeforeCapsule = pl.zone;
        } else {
            zoneId = pl.mapBeforeCapsule != null ? pl.mapBeforeCapsule.zoneId : -1;
            pl.mapBeforeCapsule = null;
        }
        ChangeMapService.gI().changeMapBySpaceShip(pl, pl.mapCapsule.get(index).map.mapId, zoneId, -1);
    }

    public void eatPea(Player player) {
        Item pea = null;
        for (Item item : player.inventory.itemsBag) {
            if (item.isNotNullItem() && item.template.type == 6) {
                pea = item;
                break;
            }
        }
        if (pea != null) {
            int hpKiHoiPhuc = 0;
            int lvPea = Integer.parseInt(pea.template.name.substring(13));
            for (Item.ItemOption io : pea.itemOptions) {
                if (io.optionTemplate.id == 2) {
                    hpKiHoiPhuc = io.param * 10000;
                    break;
                }
                if (io.optionTemplate.id == 48) {
                    hpKiHoiPhuc = io.param;
                    break;
                }
            }
            player.nPoint.setHp(player.nPoint.hp + hpKiHoiPhuc);
            player.nPoint.setMp(player.nPoint.mp + hpKiHoiPhuc);
            PlayerService.gI().sendInfoHpMp(player);
            Service.gI().sendInfoPlayerEatPea(player);
            if (player.pet != null && player.zone.equals(player.pet.zone) && !player.pet.isDie()) {
                int statima = 100 * lvPea;
                player.pet.nPoint.stamina += statima;
                if (player.pet.nPoint.stamina > player.pet.nPoint.maxStamina) {
                    player.pet.nPoint.stamina = player.pet.nPoint.maxStamina;
                }
                player.pet.nPoint.setHp(player.pet.nPoint.hp + hpKiHoiPhuc);
                player.pet.nPoint.setMp(player.pet.nPoint.mp + hpKiHoiPhuc);
                Service.gI().sendInfoPlayerEatPea(player.pet);
                Service.gI().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã cho con đậu thần");
            }

            InventoryServiceNew.gI().subQuantityItemsBag(player, pea, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        }
    }

    private void upSkillPet(Player pl, Item item) {
        if (pl.pet == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402: //skill 1
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 0)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403: //skill 2
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 1)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404: //skill 3
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 2)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759: //skill 4
                    if (SkillUtil.upSkillPet(pl.pet.playerSkill.skills, 3)) {
                        Service.gI().chatJustForMe(pl, pl.pet, "Cảm ơn sư phụ");
                        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;

            }

        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    private void ItemSKH(Player pl, Item item) {//hop qua skh
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }
    private void Hopdothanlinh(Player pl, Item item) {//hop qua do thần linh
         NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của Bạn đi", "Set trái đất", "Set namec", "Set xayda", "Từ chổi");
    }
    private void ItemDHD(Player pl, Item item) {//hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Hãy chọn một món quà", "Áo", "Quần", "Găng", "Giày", "Rada", "Từ Chối");
    }

    private void Hopts(Player pl, Item item) {//hop qua do huy diet
        NpcService.gI().createMenuConMeo(pl, item.template.id, -1, "Chọn hành tinh của mày đi", "Set trái đất", "Set namec", "Set xayda", "Từ chổi");
    }
    
    private void openWoodChest(Player pl, Item item) {
        int time = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
        if (time != 0) {
            Item itemReward = null;
            int param = item.itemOptions.size();
            int gold = 0;
        int[] listItem = {441, 442, 443, 444, 445, 446, 447, 220, 221, 222, 223, 224, 225};
        int[] listClothesReward;
        int[] listItemReward;
        String text = "Bạn nhận được\n";
        if (param < 8) {
            gold = 100000000 * param;
            listClothesReward = new int[]{randClothes(param)};
            listItemReward = Util.pickNRandInArr(listItem, 3);
        } else if (param < 10) {
            gold = 250000000 * param;
            listClothesReward = new int[]{randClothes(param), randClothes(param)};
            listItemReward = Util.pickNRandInArr(listItem, 4);
        } else {
            gold = 500000000 * param;
            listClothesReward = new int[]{randClothes(param), randClothes(param), randClothes(param)};
            listItemReward = Util.pickNRandInArr(listItem, 5000);
            int ruby = Util.nextInt(1, 5000);
            pl.inventory.ruby += ruby;
            pl.textRuongGo.add(text + "|1| " + ruby + " Hồng Ngọc");
        }
        for (int i : listClothesReward) {
            itemReward = ItemService.gI().createNewItem((short) i);
            RewardService.gI().initBaseOptionClothes(itemReward.template.id, itemReward.template.type, itemReward.itemOptions);
            RewardService.gI().initStarOption(itemReward, new RewardService.RatioStar[]{new RewardService.RatioStar((byte) 1, 1, 2), new RewardService.RatioStar((byte) 2, 1, 3), new RewardService.RatioStar((byte) 3, 1, 4), new RewardService.RatioStar((byte) 4, 1, 5),});
            InventoryServiceNew.gI().addItemBag(pl, itemReward);
            pl.textRuongGo.add(text + itemReward.getInfoItem());
        }
        for (int i : listItemReward) {
            itemReward = ItemService.gI().createNewItem((short) i);
            RewardService.gI().initBaseOptionSaoPhaLe(itemReward);
            itemReward.quantity = Util.nextInt(1, 5);
            InventoryServiceNew.gI().addItemBag(pl, itemReward);
            pl.textRuongGo.add(text + itemReward.getInfoItem());
        }
        if (param == 11) {
            itemReward = ItemService.gI().createNewItem((short) 0);
            itemReward.quantity = Util.nextInt(1, 3);
            InventoryServiceNew.gI().addItemBag(pl, itemReward);
            pl.textRuongGo.add(text + itemReward.getInfoItem());
        }
        NpcService.gI().createMenuConMeo(pl, ConstNpc.RUONG_GO, -1, "Bạn nhận được\n|1|+" + Util.numberToMoney(gold) + " vàng", "OK [" + pl.textRuongGo.size() + "]");
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        pl.inventory.addGold(gold);
        InventoryServiceNew.gI().sendItemBags(pl);
        PlayerService.gI().sendInfoHpMpMoney(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Vui lòng đợi 24h");
        }
    }
    
    private int randClothes(int level) {
        return LIST_ITEM_CLOTHES[Util.nextInt(0, 2)][Util.nextInt(0, 4)][level - 1];
    }
    
    public static final int[][][] LIST_ITEM_CLOTHES = {
            // áo , quần , găng ,giày,rada
            //td -> nm -> xd
            {{0, 33, 3, 34, 136, 137, 138, 139, 230, 231, 232, 233, 555}, {6, 35, 9, 36, 140, 141, 142, 143, 242, 243, 244, 245, 556}, {21, 24, 37, 38, 144, 145, 146, 147, 254, 255, 256, 257, 562}, {27, 30, 39, 40, 148, 149, 150, 151, 266, 267, 268, 269, 563}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
            {{1, 41, 4, 42, 152, 153, 154, 155, 234, 235, 236, 237, 557}, {7, 43, 10, 44, 156, 157, 158, 159, 246, 247, 248, 249, 558}, {22, 46, 25, 45, 160, 161, 162, 163, 258, 259, 260, 261, 564}, {28, 47, 31, 48, 164, 165, 166, 167, 270, 271, 272, 273, 565}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}},
            {{2, 49, 5, 50, 168, 169, 170, 171, 238, 239, 240, 241, 559}, {8, 51, 11, 52, 172, 173, 174, 175, 250, 251, 252, 253, 560}, {23, 53, 26, 54, 176, 177, 178, 179, 262, 263, 264, 265, 566}, {29, 55, 32, 56, 180, 181, 182, 183, 274, 275, 276, 277, 567}, {12, 57, 58, 59, 184, 185, 186, 187, 278, 279, 280, 281, 561}}
    };

}