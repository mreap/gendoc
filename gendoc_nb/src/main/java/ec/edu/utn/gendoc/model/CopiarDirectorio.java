/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.utn.gendoc.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JOptionPane;

/**
 *
 * @author rober
 */
public class CopiarDirectorio {

    public void copyFile(String fromFile, String toFile) {
        GenDocUtil gen = new GenDocUtil();
        File directorio = new File(fromFile);
        File directorio2 = new File(toFile);
        if (!directorio.isDirectory()) {

            String[] ficheros = directorio.list();
            for (int i = 0; i < ficheros.length; i++) {
                System.out.println(ficheros[i]);
            }

        } else {
            String[] ficheros = directorio.list();
            for (int i = 0; i < ficheros.length; i++) {
                System.out.println(fromFile + ficheros[i]);
                File origin = new File(fromFile + "/" + ficheros[i]);
                File destination = new File(toFile + "/" + ficheros[i]);
                if (origin.exists()) {
                    try {
                        InputStream in = new FileInputStream(origin);
                        OutputStream out = new FileOutputStream(destination);
                        // We use a buffer for the copy (Usamos un buffer para la copia).
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        in.close();
                        out.close();

                        System.out.println("Archivo: " + ficheros[i] + " Copiado exitoso");

                    } catch (IOException ioe) {
                        ioe.printStackTrace();

                    }
                }

            }
        }

    }

    public void copiarDirectorio(String origen, String destino) {
        comprobarCrearDirectorio(destino);
        File directorio = new File(origen);
        File f;
        if (directorio.isDirectory()) {
            comprobarCrearDirectorio(destino);

            String[] files = directorio.list();
            if (files.length > 0) {
                for (String archivo : files) {

                    f = new File(origen + File.separator + archivo);
                    if (f.isDirectory()) {
                        comprobarCrearDirectorio(destino + File.separator + archivo + File.separator);

                        copiarDirectorio(origen + File.separator + archivo + File.separator, destino + File.separator + archivo + File.separator);

                    } else { //Es un archivo
                        copiarArchivo(origen + File.separator + archivo, destino + File.separator + archivo);
                    }
                }
            }
        }
    }

    public void copiarArchivo(String sOrigen, String sDestino) {
        try {
            File origen = new File(sOrigen);
            File destino = new File(sDestino);
            InputStream in = new FileInputStream(origen);
            OutputStream out = new FileOutputStream(destino);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void comprobarCrearDirectorio(String ruta) {
        File directorio = new File(ruta);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
    }

}
