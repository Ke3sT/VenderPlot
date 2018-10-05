/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.objetos;

import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Samuel
 */
public class JogadorData {
    
    String jogadorNome;
    Plot ultimoPlotComprando;
    
    public JogadorData(String jogador) {
        jogadorNome = jogador;
    }
    
    public String getNome() {
        return jogadorNome;
    }
    
    public void setUltimoPlot(Plot plot) {
        ultimoPlotComprando = plot;
    }
    
    public Plot getUltimoPlotComprando() {
        return ultimoPlotComprando;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(jogadorNome);
    }
    
}
