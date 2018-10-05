/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.listeners;

import com.intellectualcrafters.plot.object.Plot;
import com.keest.venderplot.Principal;
import com.keest.venderplot.extras.Debug;
import com.keest.venderplot.objetos.JogadorVendas;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Samuel
 */
public class ComandoManager implements Listener {

    private Principal plugin;

    public ComandoManager(Principal main) {
        this.plugin = main;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerVendePlot(PlayerCommandPreprocessEvent ev) {

        //Cmd digitado
        String cmdDigitado = ev.getMessage();

        //Verifico se o comando digitado do plot tem o vender
        if (cmdDigitado.startsWith("/plot vender") || cmdDigitado.startsWith("/p vender")) {

            //Cancela o comando do plotsquared
            ev.setCancelled(true);

            Player jogador = ev.getPlayer();
            Debug.enviar(jogador.getName() + " digitou o comando de venda de plot!");

            //Verifico se a opcao de venda esta ativada
            if (plugin.getOpcao().getBoolean("AtivarVenda")) {
                Debug.enviar("A venda de plots esta ativada!");

                //Verifico se o jogador esta no mundo dos plots
                if (!plugin.getPlotAPI.isPlotWorld(jogador.getWorld())) {
                    jogador.sendMessage(plugin.getMsg("NaoEstaMundoPlots"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Pego os dados do argumento digitado
                String[] argumentos = cmdDigitado.split(" ");
                int totalArgs = argumentos.length;
                Debug.enviar("Total de argumentos digitados: " + totalArgs);

                //Pego o plot que o jogador esta em cima
                Plot plotLocal = plugin.getPlotAPI.getPlot(jogador.getLocation());

                //Verifico se ele esta em um plot
                if (plotLocal == null) {
                    jogador.sendMessage(plugin.getMsg("NaoEstaEmUmPlot"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Pego o dono do plot
                OfflinePlayer donoDoPlot = null;
                try {
                    //Instancia do dono do plot. OfflinePlayer pois se ele estiver off, ele nao retorna o objeto com os dados do jogador com a classe Player
                    donoDoPlot = Bukkit.getOfflinePlayer(plotLocal.getOwners().iterator().next());
                } catch (Exception ex) {
                    jogador.sendMessage(plugin.getMsg("PlotNaoClaimado"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Verifico se o plot em que o jogador esta é dele
                if (plotLocal.getOwners().contains(jogador.getUniqueId())) {
                    Debug.enviar(jogador.getName() + " esta tentando vender o plot..");

                    //Verifico se o plot já esta a venda
                    if (plugin.getVender.plotEstaAVenda(donoDoPlot.getName(), plotLocal)) {
                        JogadorVendas vendaDados = plugin.getVender.getVenderDados(donoDoPlot.getName());

                        jogador.sendMessage(plugin.getMsg("JaVendendo").replace("@preco@", plugin.getEconomy.format(vendaDados.getPreco(plotLocal))));
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                        return;
                    }

                    //Some shit valores pra eu usar depois
                    long valorDoPlot = 0;
                    long valorMin = plugin.getOpcao().getLong("ValorMinimo");
                    long valorMax = plugin.getOpcao().getLong("ValorMaximo");

                    //Verifico se ele digitou o preco
                    if (totalArgs <= 2) {
                        jogador.sendMessage(plugin.getMsg("DigiteValor"));
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                        return;
                    } else {
                        try {
                            valorDoPlot = Integer.parseInt(argumentos[2]);
                        } catch (NumberFormatException ex) {
                            jogador.sendMessage(plugin.getMsg("DigiteValorValido"));
                            plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                            return;
                        }
                    }

                    //Verifico se o valor digitado atende os requisitos entre o minimo e o maximo valor
                    if (valorDoPlot < valorMin) {
                        jogador.sendMessage(plugin.getMsg("ValorMuitoBaixo").replace("@minimo@", String.valueOf(valorMin)).
                                replace("@maximo@", String.valueOf(valorMax)));
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                        return;
                    } else if (valorDoPlot > valorMax) {
                        jogador.sendMessage(plugin.getMsg("ValorMuitoAlto").replace("@minimo@", String.valueOf(valorMin)).
                                replace("@maximo@", String.valueOf(valorMax)));
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                        return;
                    }

                    //Por fim, faco a venda e troca de plot|
                    Debug.enviar("Realizando venda...");
                    plugin.getVender.iniciarVenda(jogador, valorDoPlot, plotLocal);

                } else {
                    jogador.sendMessage(plugin.getMsg("PlotNaoESeu").replace("@jogador@", donoDoPlot.getName()));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                }

            } else {
                Debug.enviar("A venda de plots está desativada");
                plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerCompraPlot(PlayerCommandPreprocessEvent ev) {

        //Cmd digitado
        String cmdDigitado = ev.getMessage();

        //Verifico se o comando digitado do plot tem o vender
        if (cmdDigitado.startsWith("/plot comprar") || cmdDigitado.startsWith("/p comprar")) {
            ev.setCancelled(true);
            Player jogador = ev.getPlayer();
            Debug.enviar(jogador.getName() + " digitou o comando de comprar plot");

            if (plugin.getOpcao().getBoolean("AtivarVenda")) {
                Debug.enviar("A venda de plots esta ativada!");

                if (!plugin.getPlotAPI.isPlotWorld(jogador.getWorld())) {
                    jogador.sendMessage(plugin.getMsg("NaoEstaMundoPlots"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Pego o plot que o jogador esta em cima
                Plot plotLocal = plugin.getPlotAPI.getPlot(jogador.getLocation());

                //Verifico se ele esta em um plot
                if (plotLocal == null) {
                    jogador.sendMessage(plugin.getMsg("NaoEstaEmUmPlotComprar"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Pego o dono do plot
                OfflinePlayer donoDoPlot = null;
                try {
                    //Instancia do dono do plot. OfflinePlayer pois se ele estiver off, ele nao retorna o objeto com os dados do jogador com a classe Player
                    donoDoPlot = Bukkit.getOfflinePlayer(plotLocal.getOwners().iterator().next());
                } catch (Exception ex) {
                    jogador.sendMessage(plugin.getMsg("PlotNaoClaimadoComprar"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Verifico se o plot em que o jogador esta é dele
                if (!plotLocal.getOwners().contains(jogador.getUniqueId())) {
                    Debug.enviar(jogador.getName() + " esta tentando comprar um plot");

                    //Verifico se o plot ainda esta a venda
                    if (plugin.getVender.plotEstaAVenda(donoDoPlot.getName(), plotLocal)) {

                        //Verifico se este plot ja nao esta sendo comprado
                        if (!plugin.getVender.plotEstaSendoComprado(plotLocal)) {

                            //Abro a compra
                            plugin.getVender.getJogadorData(jogador.getName()).setUltimoPlot(plotLocal);
                            plugin.getGUI.abreMenuComprarPlot(jogador, plotLocal);

                        } else {
                            jogador.sendMessage(plugin.getMsg("PlotSendoComprado"));
                            plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                        }
                    } else {
                        jogador.sendMessage(plugin.getMsg("PlotNaoEstaAVenda"));
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    }

                } else {

                    jogador.sendMessage(plugin.getMsg("NaoPodeComprarProprioPlot"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);

                }

            } else {
                Debug.enviar("A venda de plots está desativada");
            }

        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerCancelaVenda(PlayerCommandPreprocessEvent ev) {

        //Cmd digitado
        String cmdDigitado = ev.getMessage();

        //Verifico se o comando digitado do plot tem o vender
        if (cmdDigitado.startsWith("/plot cancelarvenda") || cmdDigitado.startsWith("/p cancelarvenda")) {
            ev.setCancelled(true);
            Player jogador = ev.getPlayer();
            Debug.enviar(jogador.getName() + " digitou o comando de cancelar venda");

            if (plugin.getOpcao().getBoolean("AtivarVenda")) {
                Debug.enviar("A venda de plots esta ativada!");

                if (!plugin.getPlotAPI.isPlotWorld(jogador.getWorld())) {
                    jogador.sendMessage(plugin.getMsg("NaoEstaMundoPlots"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Pego o plot que o jogador esta em cima
                Plot plotLocal = plugin.getPlotAPI.getPlot(jogador.getLocation());

                //Verifico se ele esta em um plot
                if (plotLocal == null) {
                    jogador.sendMessage(plugin.getMsg("NaoEstaEmUmPlotCancelarVenda"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Pego o dono do plot
                OfflinePlayer donoDoPlot = null;
                try {
                    //Instancia do dono do plot. OfflinePlayer pois se ele estiver off, ele nao retorna o objeto com os dados do jogador com a classe Player
                    donoDoPlot = Bukkit.getOfflinePlayer(plotLocal.getOwners().iterator().next());
                } catch (Exception ex) {
                    jogador.sendMessage(plugin.getMsg("PlotNaoClaimadoCancelarVenda"));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    return;
                }

                //Verifico se o plot em que o jogador esta é dele
                if (plotLocal.getOwners().contains(jogador.getUniqueId())) {
                    Debug.enviar(jogador.getName() + " esta tentando cancelar a venda do proprio plot");

                    if (plugin.getVender.plotEstaAVenda(donoDoPlot.getName(), plotLocal)) {

                        plugin.getVender.cancelarVenda(jogador, plotLocal);
                    } else {
                        jogador.sendMessage(plugin.getMsg("VoceNaoBotouAVenda"));
                        plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                    }

                } else {
                    jogador.sendMessage(plugin.getMsg("PlotNaoESeuCancelarVenda").replace("@jogador@", donoDoPlot.getName()));
                    plugin.getMetodos.tocarSom(jogador, Sound.NOTE_PLING, 1);
                }

            } else {
                Debug.enviar("A venda de plots está desativada");
            }

        }

    }
}
