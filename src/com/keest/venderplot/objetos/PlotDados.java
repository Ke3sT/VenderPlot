/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.objetos;

import com.intellectualcrafters.plot.object.Plot;

/**
 *
 * @author Samuel
 */
public class PlotDados {
    
    private Plot plotJogador;
    long precoPlot = 1;
    
    public PlotDados(Plot plot, long precoplot) {
        plotJogador = plot;
        precoPlot = precoplot;
    }
    
    public Plot getPlot() {
        return plotJogador;
    }
    
    public long getPreco() {
        return precoPlot;
    }
}
