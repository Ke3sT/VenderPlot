/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.keest.venderplot.comandos.Comandos;
import com.keest.venderplot.extras.Metodos;
import com.keest.venderplot.managers.ArquivoManager;
import com.keest.venderplot.extras.Debug;
import com.keest.venderplot.extras.Log;
import com.keest.venderplot.managers.GuiManager;
import com.keest.venderplot.listeners.ComandoManager;
import com.keest.venderplot.listeners.CompraPlot;
import com.keest.venderplot.managers.VenderPlotManager;
import java.io.File;
import java.util.ArrayList;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Samuel
 */
public class Principal extends JavaPlugin {

    public static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    /////////// [ INSTANCIAS ] /////////////
    public static Economy getEconomy = null;
    public ArquivoManager getArquivos = new ArquivoManager(this);
    public VenderPlotManager getVender = new VenderPlotManager(this);
    public GuiManager getGUI = new GuiManager(this);
    public PlotAPI getPlotAPI;
    public Metodos getMetodos = new Metodos(this);
    public Log getLog = new Log(this);
    //////////////////

    //////////// [ VARS ] ////////////////
    private FileConfiguration configCache;
    ///////////

    @Override
    public void onEnable() {
        plugin = this;
        Debug.ativarDebug(false);
        if (plugin.getName().equals("KeesTVenderPlot")) {
            CS("&a-#-#-#-#-#-#-#-#-#--#-[KeesTVenderPlot]-#-#-#-#-#-#-#-#-#-#-");
            CS("&eIniciando...");
            criaConfig();

            carregaConfig();
            getCommand("venderplot").setExecutor(new Comandos(this));
            carregaListeners();
            carregaHooks();

            CS("&a#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#");

        } else {
            Bukkit.getPluginManager().disablePlugin(this);
        }

    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    //////////// [ METODOS DE INICIO ] //////////////////
    public void CS(String msg) {
        Bukkit.getConsoleSender().sendMessage(Metodos.ff(msg));
    }

    private void carregaListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new ComandoManager(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new CompraPlot(this), plugin);
    }

    private void carregaHooks() {
        hookEconomy();
        hookPlotSquared();
    }

    /////////////////////////////////////////////
    ////////////// [ METODOS DE GET/SET ]/////////////////////////////////////////
    public ConfigurationSection getOpcao() {
        return getConfigCache().getConfigurationSection("Configuracoes");
    }

    public ConfigurationSection getGuis() {
        return getConfigCache().getConfigurationSection("GUI");
    }

    public String getMsg(String msg) {
        return Metodos.ff(configCache.getString("Mensagens." + msg));
    }

    public ArrayList<String> getMsgMulti(String msg) {
        return Metodos.ffM((ArrayList<String>) configCache.getStringList("Mensagens." + msg));
    }

    ////////////////////////////////////////////////////////////////
    ////////////////// [ METODOS DE CONFIG ] /////////////////
    public void criaConfig() {
        CS("");
        if (!this.getDataFolder().exists()) {
            CS("&eCriando uma nova pasta...");
            this.getDataFolder().mkdir();
        }

        File config = new File(plugin.getDataFolder().getAbsolutePath() + File.separatorChar + "config.yml");

        if (!config.exists()) {
            CS("&eCriando uma nova config.yml...");
            getArquivos.copiaArquivoDeResource(config, "config.yml");
        }

    }

    public void carregaConfig() {
        reloadConfig();
        configCache = getConfig();
        getArquivos.carregaArquivos();
        getLog.carregaLog();
    }

    public FileConfiguration getConfigCache() {
        return configCache;
    }
    //////////////////////////////////////////////////////////////////

    //////////////////[ METODOS EXTRAS] /////////////////////
    public boolean existeSection(String section) {
        return configCache.contains(section);
    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////[ METODOS DE API ] ///////////////////////////////////
    private void hookEconomy() {

        try {
            if (getServer().getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                if (economyProvider != null) {
                    getEconomy = economyProvider.getProvider();
                }

            }
        } catch (Exception ex) {
            Debug.enviar("KeesTVenderPlot: Erro ao tentar dar hook no Vault.");
        }
    }

    private void hookPlotSquared() {
        try {
            if (getServer().getPluginManager().getPlugin("PlotSquared") != null) {
                getPlotAPI = new PlotAPI();
            }
        } catch (Exception ex) {
            Debug.enviar("KeesTVenderPlot: Erro ao tentar dar hook em PlotSquared.");
        }
    }

////////////////////////////////////////////////////////////////////
}
