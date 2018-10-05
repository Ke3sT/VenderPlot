/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.listeners;

import com.keest.venderplot.Principal;
import com.keest.venderplot.extras.Debug;
import com.keest.venderplot.extras.Metodos;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Samuel
 */
public class CompraPlot implements Listener {

    private Principal plugin;

    public CompraPlot(Principal main) {
        this.plugin = main;
    }

    @EventHandler
    public void clicaGuiComprarPlot(InventoryClickEvent ev) {

        //metodo para garantir que somente o player clique, meio inutil mas...
        if (!(ev.getWhoClicked() instanceof Player)) {
            return;
        }

        //Jogador que clicou
        Player jogador = (Player) ev.getWhoClicked();

        //Verifico se o jogador clicou em uma gui
        if (ev.getClickedInventory() != null) {
            Debug.enviar("Clicou em um inventario valido");

            Inventory inventarioClick = ev.getClickedInventory();

            //Verifico se o inventario clicado é o menu principal
            if (inventarioClick.getTitle().equalsIgnoreCase(Metodos.ff(plugin.getGuis().getString("InfoDaVendaPlot.Titulo")))) {

                //Cancelo o evento do click
                ev.setCancelled(true);

                if (inventarioClick.getType() == InventoryType.PLAYER) {
                    Debug.enviar("Clicou no proprio inv, ignorando...");
                    return;
                }

                //Verifico se o jogador clicou em algum item
                if (ev.getCurrentItem() != null && ev.getCurrentItem().getType() != Material.AIR) {

                    //O slot que foi clicado
                    int slotClicado = ev.getSlot();
                    Debug.enviar("Slot clicado: " + slotClicado);

                    //Confirma compra do plot
                    if (slotClicado == plugin.getGuis().getInt("InfoDaVendaPlot.ItensNoGui.ConfirmarCompra.Slot")) {
                        Debug.enviar("Clicou no confirmar");
                        plugin.getVender.iniciarCompra(jogador);
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_STICKS, -1);
                    } else if (slotClicado == plugin.getGuis().getInt("InfoDaVendaPlot.ItensNoGui.CancelarCompra.Slot")) {
                        Debug.enviar("Clicou no fechar");
                        jogador.sendMessage(plugin.getMsg("CancelouCompra"));
                        jogador.getOpenInventory().close();
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_SNARE_DRUM, 1);
                    }

                } else {
                    Debug.enviar("Não clicou em um item");
                }

            } else {
                Debug.enviar("Inventario clicado não pertence ao menu principal");
            }
        } else {
            Debug.enviar("Clicou fora de um inventario");
        }
    }

    @EventHandler
    public void playerFechaInv(InventoryCloseEvent ev) {

        if (ev.getInventory().getTitle().equalsIgnoreCase(Metodos.ff(plugin.getGuis().getString("InfoDaVendaPlot.Titulo")))) {
            Player jogador = (Player) ev.getPlayer();

            if (!plugin.getVender.jogadorEstaComprandoPlot(jogador.getName())) {
                Debug.enviar(jogador.getName() + " nao esta comprando o plot. Triando ele da map..");
                plugin.getVender.removeUltimoPlotComprado(jogador.getName());
                plugin.getMetodos.tocarSom(jogador, Sound.NOTE_SNARE_DRUM, 1);
            } else {
                Debug.enviar(jogador.getName() + " esta comprando um plot!");
            }

        }
    }

}
