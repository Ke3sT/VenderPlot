/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.extras;

/**
 *
 * @author Samuel
 */
public class Debug {

    private static boolean ativarDebug = true;

    public static void enviar(String debug) {
        if (ativarDebug) {
            System.out.println(debug);
        }
    }

    public static void ativarDebug(boolean novoStatus) {
        ativarDebug = novoStatus;
    }

}
