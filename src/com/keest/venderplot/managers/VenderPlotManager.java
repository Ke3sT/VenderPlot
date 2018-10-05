/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.managers;

import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.keest.venderplot.Principal;
import com.keest.venderplot.extras.Debug;
import com.keest.venderplot.objetos.JogadorData;
import com.keest.venderplot.objetos.JogadorVendas;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Samuel
 */
public class VenderPlotManager {

    private Principal plugin;

    public VenderPlotManager(Principal main) {
        this.plugin = main;
    }

    private HashMap<String, JogadorVendas> plotsVendendo = new HashMap<>();
    private HashMap<String, JogadorData> jogadoresDados = new HashMap<>();
    private HashMap<Plot, Integer> comprasTasks = new HashMap<>();

    //Metodo para iniciar a venda
    public void iniciarVenda(Player jogador, long precoVenda, Plot plotSendoVendido) {

        //Salvo os dados num objeto para facilitar minha vida
        if (plotsVendendo.containsKey(jogador.getName())) {
            plotsVendendo.get(jogador.getName()).addPlot(plotSendoVendido, precoVenda);
        } else {
            JogadorVendas vendaObjeto = new JogadorVendas(jogador.getName(), precoVenda, plotSendoVendido);
            plotsVendendo.put(jogador.getName(), vendaObjeto);
        }

        for (PlotPlayer jogadoresNoPlot : plotSendoVendido.getPlayersInPlot()) {
            if (plugin.getOpcao().getBoolean("AvisarPlayersNoPlotAoPorVender")) {
                for (String msg : plugin.getMsgMulti("AnuncioColocouAVenda")) {
                    if (!jogadoresNoPlot.getName().equalsIgnoreCase(jogador.getName())) {
                        jogadoresNoPlot.sendMessage(msg.replace("@dono@", jogador.getName()).
                                replace("@preco@", Principal.getEconomy.format(precoVenda)));
                        plugin.getMetodos.tocarSom(Bukkit.getPlayer(jogadoresNoPlot.getName()), Sound.CHICKEN_EGG_POP, 2);
                    }
                }
            }
        }

        plugin.getMetodos.tocarSom(jogador, Sound.CLICK, 2);
        jogador.sendMessage(plugin.getMsg("ColocouAVenda").replace("@preco@", Principal.getEconomy.format(precoVenda)));
    }

    public void iniciarCompra(Player jogadorComprando) {

        plugin.getMetodos.tocarSom(jogadorComprando, Sound.ORB_PICKUP, 2);

        Debug.enviar("Vendendo o plot");
        JogadorData dataDoComprador = getJogadorData(jogadorComprando.getName());
        Plot plotSendoVendido;

        if (dataDoComprador.getUltimoPlotComprando() != null) {
            plotSendoVendido = dataDoComprador.getUltimoPlotComprando();
            Debug.enviar("Encontrou o ultimo plot comprado do jogador");
        } else {
            Debug.enviar("Ultimo plot nao encontrado, pegando do local...");
            plotSendoVendido = plugin.getPlotAPI.getPlot(jogadorComprando.getLocation());
        }

        if (plotSendoVendido != null) {
            JogadorVendas dadosDonoPlot = getVenderDadosPorPlot(plotSendoVendido);
            
            if (dadosDonoPlot == null) {
                plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                jogadorComprando.closeInventory();
                return;
            }

            if (!plotEstaAVenda(dadosDonoPlot.getJogador(), plotSendoVendido)) {
                jogadorComprando.sendMessage(plugin.getMsg("RemoveuPlotDaVenda"));
                plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                jogadorComprando.closeInventory();
                return;
            }

            long compradorMoney = (long) Principal.getEconomy.getBalance(jogadorComprando.getName());
            long precoPlot = dadosDonoPlot.getPreco(plotSendoVendido);

            Debug.enviar("Preco plot: " + precoPlot + ", dono: " + dadosDonoPlot.getJogador());

            if (compradorMoney > precoPlot) {
                jogadorComprando.sendMessage(plugin.getMsg("RealizandoCompra").replace("@dono@", dadosDonoPlot.getJogador()).
                        replace("@preco@", Principal.getEconomy.format(precoPlot)));

                //Verifico se o dono da plot ta online
                if (dadosDonoPlot.estaOnline()) {
                    dadosDonoPlot.getPlayer().sendMessage(plugin.getMsg("RealizandoVenda").replace("@jogador@", jogadorComprando.getName()));
                    plugin.getMetodos.tocarSom(dadosDonoPlot.getPlayer(), Sound.ORB_PICKUP, -2);
                }

                BukkitTask taskConcluirVenda = new BukkitRunnable() {
                    @Override
                    public void run() {

                        PlotPlayer compradorDados = PlotPlayer.get(jogadorComprando.getName());

                        if (compradorDados.getPlotCount() <= compradorDados.getAllowedPlots()) {
                            if (plotSendoVendido.hasOwner()) {
                                if (plotSendoVendido.isOwner(Bukkit.getOfflinePlayer(dadosDonoPlot.getJogador()).getUniqueId())) {
                                    if (plotEstaAVenda(dadosDonoPlot.getJogador(), plotSendoVendido)) {
                                        if (Principal.getEconomy.getBalance(jogadorComprando.getName()) > precoPlot) {

                                            plotSendoVendido.setOwner(jogadorComprando.getUniqueId());
                                            plotSendoVendido.setSign(jogadorComprando.getName());

                                            if (plugin.getOpcao().getBoolean("RemoverTrustedFlagsEtc")) {
                                                for (UUID trusteds : plotSendoVendido.getTrusted()) {
                                                    plotSendoVendido.removeTrusted(trusteds);
                                                }

                                                for (UUID members : plotSendoVendido.getMembers()) {
                                                    plotSendoVendido.removeMember(members);
                                                }

                                                for (UUID denied : plotSendoVendido.getDenied()) {
                                                    plotSendoVendido.removeDenied(denied);
                                                }

                                                for (Flag flags : plotSendoVendido.getFlags().keySet()) {
                                                    plotSendoVendido.removeFlag(flags);
                                                }
                                            }

                                            for (PlotPlayer jogadoresNoPlot : plotSendoVendido.getPlayersInPlot()) {
                                                if (!jogadoresNoPlot.getName().equalsIgnoreCase(jogadorComprando.getName())) {

                                                    if (plugin.getOpcao().getBoolean("AvisarPlayersNoPlotAoComprarPlot")) {
                                                        for (String msg : plugin.getMsgMulti("AnuncioComprouPlot")) {
                                                            if (!jogadoresNoPlot.getName().equalsIgnoreCase(dadosDonoPlot.getJogador())) {
                                                                jogadoresNoPlot.sendMessage(msg.replace("@dono@", dadosDonoPlot.getJogador()).
                                                                        replace("@jogador@", jogadorComprando.getName()).
                                                                        replace("@preco@", Principal.getEconomy.format(precoPlot)));
                                                                plugin.getMetodos.tocarSom(Bukkit.getPlayer(jogadoresNoPlot.getName()), Sound.CHICKEN_EGG_POP, 2);
                                                            }
                                                        }

                                                    }

                                                    if (plugin.getOpcao().getBoolean("TeleportarJogadoresPraForaAoComprar")) {
                                                        jogadoresNoPlot.teleport(plotSendoVendido.getHome());
                                                    }

                                                }
                                            }

                                            Principal.getEconomy.depositPlayer(dadosDonoPlot.getJogador(), precoPlot);
                                            if (dadosDonoPlot.estaOnline()) {
                                                dadosDonoPlot.getPlayer().sendMessage(plugin.getMsg("VendaConcluida").replace("@jogador@", jogadorComprando.getName()).
                                                        replace("@preco@", Principal.getEconomy.format(precoPlot)));
                                                plugin.getMetodos.tocarSom(dadosDonoPlot.getPlayer(), Sound.VILLAGER_YES, 1);
                                                plugin.getMetodos.tocarSom(dadosDonoPlot.getPlayer(), Sound.LEVEL_UP, -2);
                                            }

                                            Principal.getEconomy.withdrawPlayer(jogadorComprando.getName(), precoPlot);
                                            jogadorComprando.sendMessage(plugin.getMsg("PlotAdquirido"));
                                            plugin.getMetodos.tocarSom(jogadorComprando, Sound.LEVEL_UP, -1);
                                            plugin.getMetodos.tocarSom(jogadorComprando, Sound.VILLAGER_YES, 1);

                                            dadosDonoPlot.removePlot(plotSendoVendido);
                                            dataDoComprador.setUltimoPlot(null);
                                            removeTaskComprando(plotSendoVendido);

                                            Debug.enviar("Anotando log...");
                                            plugin.getLog.escreveLog(jogadorComprando.getName() + " comprou um Plot de "
                                                    + dadosDonoPlot.getJogador() + " por " + plugin.getEconomy.format(precoPlot) + " em X:"
                                                    + plotSendoVendido.getCenter().getX() + " Y:"
                                                    + plotSendoVendido.getCenter().getY() + " Z:"
                                                    + plotSendoVendido.getCenter().getZ());
                                        } else {
                                            jogadorComprando.sendMessage(plugin.getMsg("MoneyInsuficiente"));
                                            plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                                            removeTaskComprando(plotSendoVendido);
                                        }
                                    } else {
                                        jogadorComprando.sendMessage(plugin.getMsg("RemoveuPlotDaVenda"));
                                        plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                                        removeTaskComprando(plotSendoVendido);
                                    }

                                } else {
                                    jogadorComprando.sendMessage(plugin.getMsg("ErroAoComprar"));
                                    plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                                    removeTaskComprando(plotSendoVendido);
                                }
                            } else {
                                jogadorComprando.sendMessage(plugin.getMsg("PlotExcluido"));
                                plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                                removeTaskComprando(plotSendoVendido);
                            }
                        } else {
                            jogadorComprando.sendMessage(plugin.getMsg("PossuiMuitosPlots"));
                            plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                            removeTaskComprando(plotSendoVendido);
                        }
                    }
                }.runTaskLater(plugin, 63L);

                addTaskComprando(plotSendoVendido, taskConcluirVenda.getTaskId());
                removeTodosComprandoOPlotMenos(jogadorComprando.getName(), plotSendoVendido);
                jogadorComprando.closeInventory();

            } else {
                jogadorComprando.sendMessage(plugin.getMsg("MoneyInsuficiente"));
                plugin.getMetodos.tocarSom(jogadorComprando, Sound.NOTE_PLING, 1);
                jogadorComprando.closeInventory();
            }

        }
    }

    public void cancelarVenda(Player jogador, Plot plotPraCancelar) {
        if (plotsVendendo.containsKey(jogador.getName())) {
            JogadorVendas vendaDados = plotsVendendo.get(jogador.getName());
            vendaDados.removePlot(plotPraCancelar);

            jogador.sendMessage(plugin.getMsg("CancelouVenda"));

            for (PlotPlayer jogadoresNoPlot : plotPraCancelar.getPlayersInPlot()) {
                if (plugin.getOpcao().getBoolean("AvisarPlayersNoPlotAoCancelarVenda")) {
                    if (!jogadoresNoPlot.getName().equalsIgnoreCase(jogador.getName())) {
                        for (String msg : plugin.getMsgMulti("AnuncioCancelouVenda")) {
                            jogadoresNoPlot.sendMessage(msg.replace("@dono@", jogador.getName()));
                            plugin.getMetodos.tocarSom(Bukkit.getPlayer(jogadoresNoPlot.getName()), Sound.VILLAGER_NO, 1);
                        }
                    }
                }
            }

            if (existeTask(plotPraCancelar)) {
                Debug.enviar("Achou uma task executando pra venda do plot de " + plotPraCancelar.getOwners().iterator().next());

                JogadorData compradorDoplot = getJogadorPorUltimoPlot(plotPraCancelar);

                if (compradorDoplot != null) {
                    Debug.enviar("Achou um comprador do plot cancelado: " + compradorDoplot.getNome());

                    if (compradorDoplot.getPlayer() != null) {
                        compradorDoplot.getPlayer().sendMessage(plugin.getMsg("RemoveuPlotDaVenda"));
                        plugin.getMetodos.tocarSom(compradorDoplot.getPlayer(), Sound.NOTE_PLING, 1);
                    }
                }

            }

            cancelaTask(plotPraCancelar);
            removeTaskComprando(plotPraCancelar);

            for (JogadorData compradoresPlot : jogadoresDados.values()) {
                if (compradoresPlot.getUltimoPlotComprando() != null) {
                    if (compradoresPlot.getUltimoPlotComprando().equals(plotPraCancelar)) {
                        compradoresPlot.getPlayer().sendMessage(plugin.getMsg("RemoveuPlotDaVenda"));
                        plugin.getMetodos.tocarSom(compradoresPlot.getPlayer(), Sound.NOTE_PLING, 1);
                        compradoresPlot.getPlayer().closeInventory();
                        compradoresPlot.setUltimoPlot(null);
                    }
                }
            }
        }
    }

    public JogadorVendas getVenderDados(String jogador) {
        if (plotsVendendo.containsKey(jogador)) {
            return plotsVendendo.get(jogador);
        } else {
            return null;
        }
    }

    public JogadorVendas getVenderDadosPorPlot(Plot plot) {
        for (String jogadores : plotsVendendo.keySet()) {
            JogadorVendas jogadorDados = plotsVendendo.get(jogadores);

            if (jogadorDados.estaVendendo(plot)) {
                return jogadorDados;
            }
        }

        return null;
    }

    public boolean plotEstaAVenda(String donoPlot, Plot plot) {
        if (!plotsVendendo.containsKey(donoPlot)) {
            return false;
        } else {
            return plotsVendendo.get(donoPlot).estaVendendo(plot);
        }
    }

    public Plot getPlotPorLocation(com.intellectualcrafters.plot.object.Location local) {
        return Plot.getPlot(local);
    }

    public PlotPlayer getPlayerPlotData(String jogador) {
        return PlotPlayer.get(jogador);
    }

    public JogadorData getJogadorData(String jogador) {
        if (!jogadoresDados.containsKey(jogador)) {
            jogadoresDados.put(jogador, new JogadorData(jogador));
        }

        return jogadoresDados.get(jogador);
    }

    public boolean jogadorEstaComprandoPlot(String player) {
        if (!jogadoresDados.containsKey(player)) {
            Debug.enviar(player + " nem ta na map. false....");
            return false;
        } else {
            if (jogadoresDados.get(player).getUltimoPlotComprando() != null) {
                Debug.enviar("Ultimo plot comprado e valido");
                if (existeTask(jogadoresDados.get(player).getUltimoPlotComprando())) {
                    Debug.enviar("O plot ta na map, esta comprando...");
                    return true;
                } else {
                    Debug.enviar("O plot nao ta na map, nao ta comprando...");
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public void removeUltimoPlotComprado(String jogador) {
        if (jogadoresDados.containsKey(jogador)) {
            jogadoresDados.get(jogador).setUltimoPlot(null);
        }
    }

    public void removeTodosComprandoOPlotMenos(String jogadorUnico, Plot qualPlot) {
        for (JogadorData jogador : jogadoresDados.values()) {
            if (!jogador.getNome().equalsIgnoreCase(jogadorUnico)) {
                if (jogador.getUltimoPlotComprando() != null) {
                    if (jogador.getUltimoPlotComprando().equals(qualPlot)) {
                        Debug.enviar("Achou um jogador com gui aberto de compra: " + jogador.getNome() + ". Porem a venda ja ta pro " + jogadorUnico);
                        jogador.setUltimoPlot(null);

                        if (jogador.getPlayer() != null) {
                            jogador.getPlayer().closeInventory();
                            jogador.getPlayer().sendMessage(plugin.getMsg("VoceFoiLerdo"));
                        }
                    }
                }
            }
        }
    }

    public JogadorData getJogadorPorUltimoPlot(Plot plot) {
        for (JogadorData jogador : jogadoresDados.values()) {
            if (jogador.getUltimoPlotComprando() != null) {
                if (jogador.getUltimoPlotComprando().equals(plot)) {
                    return jogador;
                }
            }
        }

        return null;
    }

    public void addTaskComprando(Plot plot, int taskId) {
        comprasTasks.put(plot, taskId);
    }

    public void removeTaskComprando(Plot plot) {
        comprasTasks.remove(plot);
    }

    public boolean existeTask(Plot plot) {
        return comprasTasks.containsKey(plot);
    }

    public boolean plotEstaSendoComprado(Plot plot) {
        if (comprasTasks.containsKey(plot)) {
            return true;
        } else {
            return false;
        }
    }

    public void cancelaTask(Plot plot) {
        if (existeTask(plot)) {
            Debug.enviar("Calcando a task...");
            Bukkit.getScheduler().cancelTask(comprasTasks.get(plot));
        }
    }

}
