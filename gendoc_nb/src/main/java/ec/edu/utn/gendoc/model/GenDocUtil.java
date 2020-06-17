package ec.edu.utn.gendoc.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Funciones asociadas al procesamiento de directorios para generar un menu HTML
 * para acceder a archivos.
 *
 * @author mrea
 */
public class GenDocUtil {

    /**
     * Escanea un directorio y sus subdirectorios generando una estructura
     * jerarquica de archivos.
     *
     * @param directorioInicial La carpeta inicial a escanear.
     * @return el nodo raiz de la estructura de archivos.
     * @throws Exception
     */
    public Archivo escanearDirectorios(String directorioInicial, List<String> extension, String TipoG) throws Exception {
        File directorio = new File(directorioInicial);
        Archivo archivoRaiz = new Archivo(directorio.isDirectory(), directorio.getName(), directorio.getCanonicalPath());
        String[] files = directorio.list();
        if (archivoRaiz.isDirectorio()) {
            archivoRaiz.setArchivosHijos(escanearDirectoriosRecursivo(archivoRaiz.getRutaCompleta(), extension, TipoG));
        }
        return archivoRaiz;
    }

    private List<Archivo> escanearDirectoriosRecursivo(String directorioInicial, List<String> extension, String TipoG) throws Exception {
        List<Archivo> archivos = new ArrayList<>();
        List<String> extens = new ArrayList<String>();
//        for (int i = 0; i < extension.size(); i++) {
//            extens.add(extension.get(i));
//            System.out.println("dede: " + extension.get(i));

        File directorio = new File(directorioInicial);

        if (TipoG.equals("Generar Todas")) {
            for (File f : directorio.listFiles()) {
                Archivo archivo = new Archivo(f.isDirectory(), f.getName(), f.getCanonicalPath());

                if (archivo.isDirectorio()) {

                    archivo.setArchivosHijos(escanearDirectoriosRecursivo(archivo.getRutaCompleta(), extension, TipoG));

                }

                archivos.add(archivo);

            }
        } else if (TipoG.equals("Seleccionar extensión")) {
            for (File f : directorio.listFiles()) {
                Archivo archivo = new Archivo(f.isDirectory(), f.getName(), f.getCanonicalPath());

                for (int i = 0; i < extension.size(); i++) {
                    if (archivo.isDirectorio()) {

                        archivo.setArchivosHijos(escanearDirectoriosRecursivo(archivo.getRutaCompleta(), extension, TipoG));

                    }

                    if (f.getName().endsWith(extension.get(i)) || (f.isDirectory() == true)) {
                        if (f.isDirectory()) {
                            if (f.getName().equals(archivo.getNombre())) {
                                archivos.remove(archivo);
                                archivos.add(archivo);
                            }
                        } else if (f.isFile()) {
                            archivos.add(archivo);

                        }

                    }
                }
            }
        }

//        }
        return archivos;
    }

//    public boolean accept(File dir, String extension) {
//        return dir.getName().endsWith(extension);
//    }
    /**
     * Imprime en consola el listado de carpetas y archivos a partir de un nodo
     * raiz.
     *
     * @param archivo El nodo raiz de la estructura.
     */
    public void imprimirListadoArchivosEnConsola(Archivo archivo) {
        //System.out.println(archivo.getRutaCompleta());
        if (archivo.isDirectorio()) {
            for (Archivo a : archivo.getArchivosHijos()) {
                imprimirListadoArchivosEnConsola(a);
            }
        }
    }

    /**
     * Version antigua de generacion de menu HTML, funciona con plantilla
     * index.html Se mantiene para referencia.
     *
     * @param myFile
     * @return
     */
    public List<String> printFiles(Archivo myFile) {
        List<String> lineasHTML = new ArrayList<>();
        if (myFile.isDirectorio()) {
            lineasHTML.add("<li>");
            lineasHTML.add("<a target=\"embed\" href='#" + myFile.getNombre() + "Submenu' data-toggle='collapse' aria-expanded='false' class='dropdown-toggle'>" + myFile.getNombre() + "</a>");
            lineasHTML.add("<ul class='collapse list-unstyled' id='" + myFile.getNombre() + "Submenu'>");
            for (Archivo mf : myFile.getArchivosHijos()) {
                List<String> lineas = printFiles(mf);
                lineasHTML.addAll(lineas);
            }
            lineasHTML.add("</ul>");
            lineasHTML.add("</li>");
        } else {
            List<String> lineas = new ArrayList<>();
            lineas.add("<li>");
            lineas.add("<a target=\"embed\" href='" + myFile.getRutaCompleta() + "'>" + myFile.getNombre() + "</a>");
            lineas.add("</li>");
            return lineas;
        }
        return lineasHTML;
    }

    /**
     * Metodo recursivo que genera una estructura de menu HTML a partir de una
     * jerarquia de archivos.
     *
     * @param archivoRaiz El nodo raiz de la jerarquia de archivos.
     * @return menu en formato HTML.
     */
    public List<String> generarMenuHTML(Archivo archivoRaiz) {
        List<String> lineasHTML = new ArrayList<>();
        if (archivoRaiz.isDirectorio()) {
            lineasHTML.add("<button class='dropdown-btn'>");
            lineasHTML.add(archivoRaiz.getNombre() + "<i class='fa fa-caret-down'></i>");
            lineasHTML.add("</button>");
            lineasHTML.add("<div class=\"dropdown-container\">");
            for (Archivo mf : archivoRaiz.getArchivosHijos()) {
                List<String> lineas = generarMenuHTML(mf);
                lineasHTML.addAll(lineas);
            }
            lineasHTML.add("</div>");
        } else {
            List<String> lineas = new ArrayList<>();
            lineas.add("<a target=\"embed\" href='" + archivoRaiz.getRutaCompleta() + "'>" + archivoRaiz.getNombre() + "</a>");
            return lineas;
        }
        return lineasHTML;
    }
    
    public List<String> generarTitulo(Archivo archivoRaiz, String TituloMenu){
        List<String> lineasHTML=new ArrayList<>();       
            lineasHTML.add("<div class=\"sidebar-header\">");
            lineasHTML.add("<h3>"+TituloMenu+"</h3>");
            lineasHTML.add("</div>");                                    
        return lineasHTML;
    }    
    
    public List<String> generarLogo(Archivo archivoRaiz){
             List<String> lineasHTML=new ArrayList<>();       
            lineasHTML.add("<div align=\"right\">");
            lineasHTML.add(" <img src=\"https://i.ibb.co/BGtGykQ/logo-csoft.png\" width=\"100\" height=\"100\">");
            lineasHTML.add("</div>");                                    
        return lineasHTML;
    } 

    /**
     * Lee todo el texto HTML desde un archivo.
     *
     * @param rutaPlantillaHTML La ruta de la plantilla HTML.
     * @return La informacion HTML leida.
     */
    public List<String> leerHTMLDesdePlantilla(String rutaPlantillaHTML) {
        List<String> lineas = new ArrayList<String>();
        File indexHTML = new File(rutaPlantillaHTML);
        System.out.println("Plantilla HTML: " + rutaPlantillaHTML);
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(indexHTML);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return lineas;
    }

    /**
     * Inserta el codigo del menu HTML en el codigo de una pagina HTML previa.
     *
     * @param archivoRaiz Informacion raiz de la estructura de carpetas.
     * @param lineasHTML Informacion de la plantilla HTML inicial.
     * @return Totalidad del codigo HTML.
     */
    public List<String> procesarInformacion(Archivo archivoRaiz, List<String> lineasHTML, String TituloMenu) {
        List<String> lineasProcesadas = new ArrayList<String>();
        for (String linea : lineasHTML) {
            lineasProcesadas.add(linea);
            if (linea.contains("<!-- inicio gendoc -->")) {
                List<String> lineasMenu = new ArrayList<>();
                lineasMenu = generarMenuHTML(archivoRaiz);
                lineasProcesadas.addAll(lineasMenu);
            }else if(linea.contains("<!-- inicio Titulo -->")){
                List<String> lineasTitulo=new ArrayList<>();
                lineasTitulo=generarTitulo(archivoRaiz, TituloMenu);
                lineasProcesadas.addAll(lineasTitulo);
            }else if(linea.contains("<!-- inicio logo -->")){
                List<String> lineasLogo=new ArrayList<>();
                lineasLogo=generarLogo(archivoRaiz);
                lineasProcesadas.addAll(lineasLogo);
            }
        }
        return lineasProcesadas;
    }

    /**
     * Genera el archivo HTML resultante de escanear carpetas y archivos.
     *
     * @param directorioInicial
     * @param rutaPlantillaHTML
     * @param rutaArchivoResultado
     */
    public void generarArchivoFinalHTML(String directorioInicial, String rutaPlantillaHTML, String rutaArchivoResultado, List<String> extension, String TipoG,String TituloMenu) {
        List<String> lineasHTML = null;
        List<String> lineasProcesadas = null;
        try {
            //recorrerDir(directorio, listaNombres);
            Archivo myFile = escanearDirectorios(directorioInicial, extension, TipoG);
            lineasHTML = leerHTMLDesdePlantilla(rutaPlantillaHTML);
            //lineasProcesadas = procesarInformacion(listaNombres, lineasHTML);
            lineasProcesadas = procesarInformacion(myFile, lineasHTML, TituloMenu);
            //creacion del archivo:
            FileWriter archivo = null;
            PrintWriter pw = null;
            try {
                archivo = new FileWriter(rutaArchivoResultado);
                pw = new PrintWriter(archivo);
                for (String linea : lineasProcesadas) {
                    pw.println(linea);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != archivo) {
                        archivo.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GenDocUtil.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
