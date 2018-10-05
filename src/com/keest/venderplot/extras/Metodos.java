/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.extras;

import com.keest.venderplot.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 *
 * @author Samuel
 */
public class Metodos {

    private Principal plugin;

    public Metodos(Principal main) {
        this.plugin = main;
    }

    //Toca um som pro jogador
    public void tocarSom(Player jogador, Sound som, int pitch) {

        if (plugin.getOpcao().getBoolean("EfeitoDeSom")) {
            if (som != null) {
                jogador.playSound(jogador.getLocation(), som, 1, pitch);
            }
        }

    }

    public String getDataTraduzida(Calendar time) {
        String dia = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
        if (dia.length() == 1) {
            dia = "0" + dia;
        }
        String mes = String.valueOf(time.get(Calendar.MONTH) + 1);
        if (mes.length() == 1) {
            mes = "0" + mes;
        }
        String ano = String.valueOf(time.get(Calendar.YEAR));

        String hora = String.valueOf(time.get(Calendar.HOUR_OF_DAY));
        if (hora.length() == 1) {
            hora = "0" + hora;
        }
        String minuto = String.valueOf(time.get(Calendar.MINUTE));
        if (minuto.length() == 1) {
            minuto = "0" + minuto;
        }
        String segundo = String.valueOf(time.get(Calendar.SECOND));
        if (segundo.length() == 1) {
            segundo = "0" + segundo;
        }

        return String.valueOf(dia + "/" + mes + "/" + ano + ", as " + hora + ":" + minuto + ":" + segundo);
    }

    //Traduz as cores da string
    public static String ff(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    //Traduz as cores das msg no array
    public static ArrayList<String> ffM(ArrayList<String> msg) {
        ArrayList<String> array = new ArrayList<>();

        for (String linha : msg) {
            array.add(ff(linha));
        }

        return array;
    }

}
