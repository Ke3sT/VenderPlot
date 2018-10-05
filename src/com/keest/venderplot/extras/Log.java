/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.extras;

import com.keest.venderplot.Principal;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Samuel
 */
public class Log {

    private Principal plugin;

    public Log(Principal main) {
        this.plugin = main;
    }

    private File dadosLog;
    public FileConfiguration editaDados;

    public void carregaLog() {
        if (plugin.getOpcao().getBoolean("AtivarLog")) {
            File file = new File(this.plugin.getDataFolder(), "log.yml");
            dadosLog = new File(plugin.getDataFolder(), "log.yml");

            if (file.exists()) {
                editaDados = YamlConfiguration.loadConfiguration(dadosLog);
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Arquivo de log carregado com sucesso!");

            } else {
                try {
                    file.createNewFile();
                    editaDados = YamlConfiguration.loadConfiguration(dadosLog);
                    Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Criando um novo arquivo log.yml");
                } catch (Exception ex) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Erro ao criar do arquivo de log!");
                    ex.printStackTrace();
                }
            }

        }
    }

    public void escreveLog(String conteudo) {
        if (this.plugin.getOpcao().getBoolean("AtivarLog")) {
            editaDados.set(plugin.getMetodos.getDataTraduzida(Calendar.getInstance()), conteudo);

            try {
                editaDados.save(dadosLog);
            } catch (IOException ex) {
                Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
