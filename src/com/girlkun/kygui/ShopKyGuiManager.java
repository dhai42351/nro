package com.girlkun.kygui;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.result.GirlkunResultSet;
import com.girlkun.utils.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author BTH
 */
public class ShopKyGuiManager {
    private static ShopKyGuiManager instance;
    public static ShopKyGuiManager gI() {
        if (instance == null) {
            instance = new ShopKyGuiManager();
        }
        return instance;
    }
    
    public long lastTimeUpdate;
    
    public String[] tabName = {"Trang bị","Phụ kiện","Hỗ trợ","Linh tinh",""};
    
    public List<ItemKyGui> listItem = new ArrayList<>();
    
    
    public void save(){
        try (Connection con = GirlkunDB.getConnection();) {
            Statement s = con.createStatement();
            s.execute("TRUNCATE shop_ky_gui");
            for(ItemKyGui it : this.listItem){
                if(it != null){
                    s.execute(String.format("INSERT INTO `shop_ky_gui`(`id`, `player_id`, `tab`, `item_id`, `gold`, `gem`, `quantity`, `itemOption`, `isUpTop`, `isBuy`) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')"
                            , it.id,it.player_sell,it.tab,it.itemId,it.goldSell,it.gemSell,it.quantity,JSONValue.toJSONString(it.options).equals("null") ? "[]" : JSONValue.toJSONString(it.options),it.isUpTop,it.isBuy ? 1 : 0));
                }
            }
        }catch(Exception e){
        }
    }
    public void Load() {
        try {
            PreparedStatement ps = null;
            Connection con = GirlkunDB.getConnection();
            ps = con.prepareStatement("SELECT * FROM shop_ky_gui");
            ResultSet rs = null;
            rs = ps.executeQuery();
            ShopKyGuiManager.gI().listItem.clear();
            while (rs.next()) {
                int i = rs.getInt("id");
                int idPl = rs.getInt("player_id");
                byte tab = rs.getByte("tab");
                short itemId = rs.getShort("item_id");
                int gold = rs.getInt("gold");
                int gem = rs.getInt("gem");
                int quantity = rs.getInt("quantity");
                byte isUp = rs.getByte("isUpTop");
                boolean isBuy = rs.getByte("isBuy") == 1;
                List<Item.ItemOption> op = new ArrayList<>();
                JSONArray jsa2 = (JSONArray) JSONValue.parse(rs.getString("itemOption"));
                for (int j = 0; j < jsa2.size(); ++j) {
                    JSONObject jso2 = (JSONObject) jsa2.get(j);
                    int idOptions = Integer.parseInt(jso2.get("id").toString());
                    int param = Integer.parseInt(jso2.get("param").toString());
                    op.add(new Item.ItemOption(idOptions, param));
                }
                ShopKyGuiManager.gI().listItem.add(new com.girlkun.kygui.ItemKyGui(i, itemId, idPl, tab, gold, gem, quantity, isUp, op, isBuy));
            }
            Logger.log(Logger.PURPLE, "Load Item Ký Gửi Thành Công[" + ShopKyGuiManager.gI().listItem.size() + "] Vật Phẩm!\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void SaveandLoad() {
//    List<ItemKyGui> listItemSave = new ArrayList<>(this.listItem); 
//    try {
//        save();
//        Load();
//    } catch (Exception e) {
//        this.listItem = listItemSave;
//        e.printStackTrace();
//    }
    }
}

