/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.venderplot.managers;

import com.keest.venderplot.extras.Debug;
import com.keest.venderplot.Principal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.ChatColor;

/**
 *
 * @author Samuel
 */
public class ArquivoManager {

    private Principal plugin;

    public ArquivoManager(Principal main) {
        this.plugin = main;
    }

    //Nessa hashmap eu guardo todos os arquivos que quero.
    public HashMap<String, File> fileCache = new HashMap<>();

    //Nesse metodo é o carregamento global de tudo que preciso carregar
    public void carregaArquivos() {

    }

    //Retorna todas as config section no cache
    public ArrayList<File> getArquivosCache() {
        return (ArrayList<File>) fileCache.values();
    }

    //Pega os dados de um arquivo especifico
    public File getCacheDoArquivo(String arquivoNome) {
        arquivoNome = arquivoNome.toLowerCase();

        if (fileCache.containsKey(arquivoNome)) {
            Debug.enviar("O cache do arquivo " + arquivoNome + " foi encontrado.");
            return fileCache.get(arquivoNome);
        } else {
            Debug.enviar("O arquivo " + arquivoNome + " não existe.");
            return null;
        }
    }

    //Metodo para copiar tudo de um arquivo nas resources do plugin para um arquivo especifico
    public void copiaArquivoDeResource(File destinoArquivo, String resourceOrigem) {

        try {

            File pastaArquivo = new File(destinoArquivo.getParent());

            if (!pastaArquivo.exists()) {
                pastaArquivo.mkdirs();

            }

            //Se o arquivo não existir, eu crio um novo
            if (!destinoArquivo.exists()) {
                destinoArquivo.createNewFile();

            }

            if (plugin.getResource(resourceOrigem) == null) {

                return;
            }

            InputStream is = plugin.getResource(resourceOrigem);
            OutputStream os = new FileOutputStream(destinoArquivo);

            byte[] buffer = new byte[4096];
            int lenght;
            while ((lenght = is.read(buffer)) > 0) {
                os.write(buffer, 0, lenght);
            }

            is.close();
            os.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            plugin.CS("&cErro ao criar o arquivo: " + destinoArquivo.getName());
            plugin.CS("");
        }
    }

    //Esse metodo abaixo serve para criar arquivos padroes ao iniciar o plugin pela primeira vez
    //Caso o arquivo já existe, eu apenas ignoro. Pois tem outro metodo que irá carregar os arquivos.
    public String formatarPath(String original) {
        return original.replace("/", File.separator);
    }

    private String ff(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private ArrayList<String> ffM(ArrayList<String> msg) {
        ArrayList<String> array = new ArrayList<>();

        for (String linha : msg) {
            array.add(ff(linha));
        }

        return array;
    }

}
