/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.comandos;

import com.keest.venderplot.Principal;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Samuel
 */
public class Comandos implements CommandExecutor {

    private Principal plugin;

    public Comandos(Principal main) {
        this.plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("venderplot")) {

            if (!sender.hasPermission("keestvenderplot.admin")) {
                if (plugin.getOpcao().getString("NaoMostrarMensagemSemPermissao").equalsIgnoreCase("true")) {
                    return false;
                } else {
                    sender.sendMessage(plugin.getMsg("ComandoInexistente"));
                    return true;
                }
            }

            if (args.length == 0) {

                for (String msg : plugin.getMsgMulti("Comando")) {
                    sender.sendMessage(msg);
                }

                if (sender.hasPermission("keestvenderplot.admin")) {
                    for (String msg : plugin.getMsgMulti("ComandoAdmin")) {
                        sender.sendMessage(msg);
                    }
                }
                tocarSom(sender, Sound.ORB_PICKUP, 1);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("keestvenderplot.admin")) {
                    if (plugin.getOpcao().getString("NaoMostrarMensagemSemPermissao").equalsIgnoreCase("true")) {
                        return false;
                    } else {
                        sender.sendMessage(plugin.getMsg("SemPermissao"));
                        tocarSom(sender, Sound.NOTE_PLING, 1);
                        return true;
                    }
                }

                plugin.criaConfig();
                plugin.carregaConfig();
                sender.sendMessage(plugin.getMsg("Reload"));
                tocarSom(sender, Sound.ORB_PICKUP, 1);
                return true;
            }

            return true;
        }

        return true;
    }

    private void tocarSom(CommandSender sender, Sound som, int pitch) {
        if (plugin.getOpcao().getString("EfeitoDeSom").equalsIgnoreCase("true")) {

            if (sender instanceof Player) {
                Player jogador = (Player) sender;

                jogador.playSound(jogador.getLocation(), som, 1, pitch);
            }

        }

    }

    private String ff(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private String toS(Object object) {
        return String.valueOf(object);
    }

    private boolean temArgs(String[] argumento, int qualArgs) {
        return argumento.length > qualArgs;

    }

}
