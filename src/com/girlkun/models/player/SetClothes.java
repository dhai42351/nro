package com.girlkun.models.player;

import com.girlkun.models.item.Item;


public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }

    public byte songoku;
    public byte thienXinHang;
    public byte kirin;

    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;

    public byte kakarot;
    public byte cadic;
    public byte nappa;

    public byte worldcup;
    public byte setDHD;
    public byte SetTinhAn;
    public byte SetNguyetAn;
    public byte SetNhatAn;
    public byte SetThienSu;

    public boolean godClothes;
    public int ctHaiTac = -1;

    public void setup() {
        setDefault();
        setupSKT();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id > 567 || item.template.id < 555) {
                    this.godClothes = false;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;

            }
        }
         this.player.setClothes.SetThienSu = 0;
           for (int i = 0; i < 5; i++) {
           Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
              if (item.template.id >= 1048 && item.template.id <= 1062) {
                 
                        player.setClothes.SetThienSu++;
                }
            }  
           }
           
                this.player.setClothes.SetTinhAn = 0;
           for (int i = 0; i < 5; i++) {
           Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                    int chiso = 0;
                  Item.ItemOption optionLevel = null;
                  for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 34) {
                        chiso = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                 if (optionLevel != null){
                        player.setClothes.SetTinhAn++;
                 }
                
            }  
           }
    }
     public boolean IsSetHuyDiet() {
        int[][] DoHuyDiet = new int[][]{
              {650, 651, 657, 658,656},// td
        {652, 653, 659, 660,656},// namec
        {654, 655, 661, 662,656},// xayda    
        };
        int z = 0;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id == DoHuyDiet[this.player.gender][i]) {
                    z++;
                }
                else {return false;}
            }
            else 
            {
                return false;
            }
            
        }
        return z == 5;
    }
    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku++;
                            break;
                        case 127:
                        case 139:
                            isActSet = true;
                            thienXinHang++;
                            break;
                        case 128:
                        case 140:
                            isActSet = true;
                            kirin++;
                            break;
                        case 131:
                        case 143:
                            isActSet = true;
                            ocTieu++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao++;
                            break;
                        case 130:
                        case 142:
                            isActSet = true;
                            picolo++;
                            break;
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic++;
                            break;
                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                    }

                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    private void setDefault() {
        this.songoku = 0;
        this.thienXinHang = 0;
        this.kirin = 0;
        this.ocTieu = 0;
        this.pikkoroDaimao = 0;
        this.picolo = 0;
        this.kakarot = 0;
        this.cadic = 0;
        this.nappa = 0;
        this.setDHD = 0;
        this.worldcup = 0;
        this.SetTinhAn = 0;
        this.SetNguyetAn = 0;
        this.SetNhatAn = 0;
        this.SetThienSu = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
    }

    public void dispose() {
        this.player = null;
    }
}
