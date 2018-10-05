/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.managers;

import com.intellectualcrafters.plot.object.Plot;
import com.keest.venderplot.Principal;
import com.keest.venderplot.extras.Debug;
import com.keest.venderplot.extras.Metodos;
import com.keest.venderplot.objetos.Item;
import com.keest.venderplot.objetos.JogadorVendas;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author Samuel
 */
public class GuiManager {

    private Principal plugin;

    public GuiManager(Principal main) {
        this.plugin = main;
    }

    public void abreMenuComprarPlot(Player quemEstaComprando, Plot plotSendoComprado) {

        Inventory inv = Bukkit.createInventory(null, plugin.getGuis().getInt("InfoDaVendaPlot.Tamanho"), Metodos.ff(plugin.getGuis().getString("InfoDaVendaPlot.Titulo")));
        JogadorVendas donoDoPlot = plugin.getVender.getVenderDadosPorPlot(plotSendoComprado);

        if (donoDoPlot == null) {
            Debug.enviar("Erro ao pegar informacoes do dono do plot");
            return;
        } else {
            Debug.enviar("O dono do plot que " + quemEstaComprando.getName() + " esta comprando pertence a " + donoDoPlot.getJogador());
        }

        ConfigurationSection itemsNoGui = plugin.getGuis().getConfigurationSection("InfoDaVendaPlot.ItensNoGui");
        for (String itemGui : itemsNoGui.getKeys(false)) {
            Item itemDados = new Item(itemsNoGui.getConfigurationSection(itemGui));

            inv.setItem(itemDados.getSlot(), montaItemStackPlotInfo(plotSendoComprado, donoDoPlot, itemDados, quemEstaComprando));
        }

        plugin.getMetodos.tocarSom(quemEstaComprando, Sound.CLICK, 1);
        quemEstaComprando.openInventory(inv);
    }

    private ItemStack montaItemStackPlotInfo(Plot plotAVenda, JogadorVendas donoDoPlot, Item itemDados, Player jogadorComprando) {

        long precoDoPlot = donoDoPlot.getPreco(plotAVenda);
        long saldoQuemCompra = (long) Principal.getEconomy.getBalance(jogadorComprando.getName());

        ////////////////////
        ItemStack stackitem;

        if (itemDados.getID().contains(":")) {
            String[] ggc = itemDados.getID().split(":");
            stackitem = new ItemStack(Material.getMaterial(Integer.parseInt(ggc[0])), 0, (short) Short.valueOf(ggc[1]));
        } else {
            stackitem = new ItemStack(Material.getMaterial(Integer.parseInt(itemDados.getID())));
        }

        if (stackitem.getType() == Material.SKULL || stackitem.getType() == Material.SKULL_ITEM) {
            SkullMeta skullmeta = (SkullMeta) stackitem.getItemMeta();

            if (itemDados.getSkin() != null) {
                if (itemDados.getSkin().equalsIgnoreCase("@donodoplot@")) {
                    skullmeta.setOwner(itemDados.getSkin().replace("@donodoplot@", donoDoPlot.getJogador()));
                } else {
                    skullmeta.setOwner(itemDados.getSkin());
                }
            }

            stackitem.setItemMeta(skullmeta);
        }

        ItemMeta meta = stackitem.getItemMeta();
        if (itemDados.brilharItem()) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        }

        //setar lore e name
        meta.setDisplayName(Metodos.ff(itemDados.getNome().replace("@donodoplot@", donoDoPlot.getJogador()).
                replace("@preco@", Principal.getEconomy.format(precoDoPlot))));

        if (itemDados.getLore() != null) {
            ArrayList<String> loreOriginal = new ArrayList<>();
            ArrayList<String> loreNova = new ArrayList<>();

            for (String linha : itemDados.getLore()) {

                if (linha.contains("@status@")) {
                    if (saldoQuemCompra > precoDoPlot) {
                        for (String loreStatus : plugin.getMsgMulti("StatusPodeComprar")) {
                            loreOriginal.add(loreStatus);
                        }
                    } else {
                        for (String loreStatus : plugin.getMsgMulti("StatusNaoPodeComprar")) {
                            loreOriginal.add(loreStatus);
                        }
                    }
                } else {

                    loreOriginal.add(linha);
                }
            }

            for (String linha : loreOriginal) {

                loreNova.add(Metodos.ff(linha).replace("@donodoplot@", donoDoPlot.getJogador()).
                        replace("@preco@", Principal.getEconomy.format(precoDoPlot)).
                        replace("@saldoatual@", Principal.getEconomy.format(saldoQuemCompra)).
                        replace("@saldorestante@", Principal.getEconomy.format(saldoQuemCompra - precoDoPlot)).
                        replace("@saldonecessario@", Principal.getEconomy.format(precoDoPlot - saldoQuemCompra)));
            }

            meta.setLore(loreNova);
        }

        stackitem.setAmount(itemDados.getQuantidade());
        stackitem.setItemMeta(meta);

        return stackitem;
    }

}
