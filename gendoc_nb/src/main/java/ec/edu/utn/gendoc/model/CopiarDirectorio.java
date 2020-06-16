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
import java.util.ArrayList;
import java.util.List;
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

    public void copiarDirectorio(String origen, String destino, List<String> extension, String tipoG, String menu) {
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

                        copiarDirectorio(origen + File.separator + archivo + File.separator, destino + File.separator + archivo + File.separator, extension, tipoG, menu);

                    } else { //Es un archivo
                        copiarArchivo(origen + File.separator + archivo, destino + File.separator + archivo, extension, tipoG, menu);

                    }
                }
            }
        }
    }

    public void copiarArchivo(String sOrigen, String sDestino, List<String> extension, String tipoG, String menu) {
        try {
            String men = menu + ".html";
            File origen = new File(sOrigen);
            File destino = new File(sDestino);

            List<String> extens = new ArrayList<String>();
            for (int i = 0; i < extension.size(); i++) {
                extens.add(extension.get(i));
            }

            InputStream in = new FileInputStream(origen);
            InputStream ine = new FileInputStream(origen);
            if (tipoG.equals("Generar Todas")) {
                try {

                    OutputStream out = new FileOutputStream(destino);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();
                } catch (Exception e) {
                }
            } else if (tipoG.equals("Seleccionar extensión")) {
                try {

                    for (int i = 0; i < extens.size(); i++) {
                        
                        if (origen.getName().endsWith(extens.get(i))) {
                            OutputStream out = new FileOutputStream(destino);
                            
                            byte[] buf = new byte[1024];
                            int len;

                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }

                            in.close();
                            out.close();
                            System.out.println("frfrfr" + extens.get(i));
                            
                        } 
                       
                        

                    }
                     if (origen.getName().equals(men)) {
                            System.out.println("Estas aqui: " + origen.getName() + "  " + men);
                            OutputStream out2 = new FileOutputStream(destino);

                            byte[] bufe = new byte[1024];
                            int lene;

                            while ((lene = ine.read(bufe)) > 0) {
                                out2.write(bufe, 0, lene);
                            }

                            ine.close();
                            out2.close();
//                            System.out.println("frfrfr" + extens.get(i));
                        }

                } catch (Exception e) {
                }
            }

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
