/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.objetos;

import java.util.ArrayList;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @author Samuel
 */
public class Item {

    private String itemPathNome;
    private String itemId;
    private int itemSlot = 1;
    private String itemNome;
    private ArrayList<String> itemLore;
    private boolean itemBrilhar = false;
    private int itemQuantidade = 1;
    private String skin;

    public Item(ConfigurationSection item) {
        this.itemPathNome = item.getName();
        if (item.contains("ID")) {
            this.itemId = item.getString("ID");
        }
        if (item.contains("Nome")) {
            this.itemNome = item.getString("Nome");
        }
        if (item.contains("Lore")) {
            this.itemLore = (ArrayList<String>) item.getStringList("Lore");
        }
        if (item.contains("Brilhar")) {
            this.itemBrilhar = item.getBoolean("Brilhar");
        }
        if (item.contains("Quantidade")) {
            this.itemQuantidade = item.getInt("Quantidade");
        }
        if (item.contains("Slot")) {
            this.itemSlot = item.getInt("Slot");
        }

        if (item.contains("SkinHead")) {
            this.skin = item.getString("SkinHead");
        }

    }

    public String getSkin() {
        return this.skin;
    }

    public String getPathNome() {
        return this.itemPathNome;
    }

    public String getNome() {
        return this.itemNome;
    }

    public String getID() {
        return this.itemId;
    }

    public ArrayList<String> getLore() {
        return this.itemLore;
    }

    public boolean brilharItem() {

        return this.itemBrilhar;

    }

    public int getQuantidade() {
        return this.itemQuantidade;
    }

    public int getSlot() {
        return this.itemSlot;
    }

}
