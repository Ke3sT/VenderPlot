/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.objetos;

import com.intellectualcrafters.plot.object.Plot;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Samuel
 */
public class JogadorVendas {

    String jogadorNome;
    Set<PlotDados> plotsVendendo = new HashSet<>();

    public JogadorVendas(String jogadorVendendo, long precoPlot, Plot plotaVenda) {
        jogadorNome = jogadorVendendo;
        plotsVendendo.add(new PlotDados(plotaVenda, precoPlot));
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(jogadorNome);
    }
    
    public boolean estaOnline() {
        return getPlayer() != null;
    }

    public Set<PlotDados> getPlots() {
        return plotsVendendo;
    }

    public void addPlot(Plot plot, long precoPlot) {
        plotsVendendo.add(new PlotDados(plot, precoPlot));
    }

    public void removePlot(Plot plot) {
        for (PlotDados plotsData : plotsVendendo) {
            if (plotsData.getPlot().equals(plot)) {
                plotsVendendo.remove(plotsData);
                break;
            }
        }
    }

    public boolean estaVendendo(Plot plot) {
        for (PlotDados plotsData : plotsVendendo) {
            if (plotsData.getPlot().equals(plot)) {
                return true;
            }
        }

        return false;
    }

    public String getJogador() {
        return jogadorNome;
    }

    public long getPreco(Plot plot) {
        for (PlotDados plotsData : plotsVendendo) {
            if (plotsData.getPlot().equals(plot)) {
                return plotsData.getPreco();
            }
        }

        return -1;
    }
    
}
