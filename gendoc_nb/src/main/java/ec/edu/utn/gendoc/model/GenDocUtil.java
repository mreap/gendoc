package ec.edu.utn.gendoc.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
//                System.out.println("dede"+ archivo.getNombre());
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
        String nombre = "";
        String ruta = "";
        List<String> lineasHTML = new ArrayList<>();

        Collections.sort(archivoRaiz.getArchivosHijos(), new Comparator<Archivo>() {
            @Override
            public int compare(Archivo p1, Archivo p2) {
                if (p1.isDirectorio()) {
                    return -1;
                } else if (!p2.isDirectorio()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        Collections.sort(archivoRaiz.getArchivosHijos(), new Comparator<Archivo>() {
            @Override
            public int compare(Archivo p1, Archivo p2) {
                if (p1.isDirectorio() && p2.isDirectorio()) {

                    if (!p1.equals(p2) && p1.equals(p1.getNombre())) {
                        return -1;
                    } else if (!p1.equals(p2) && p2.equals(p2.getNombre())) {
                        return 1;
                    } else {
                        return p1.getNombre().compareTo(p2.getNombre());
                    }

                }
                return 0;
            }
        });
        for (int i = 0; i < archivoRaiz.getNombre().length(); i++) {

            if (archivoRaiz.getNombre().charAt(i) == 'á') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "á", "&aacute;");
                } else {
                    nombre = reemplazar(nombre, "á", "&aacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'é') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "é", "&eacute;");
                } else {
                    nombre = reemplazar(nombre, "é", "&eacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'í') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "í", "&iacute;");
                } else {
                    nombre = reemplazar(nombre, "í", "&iacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'ó') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "ó", "&oacute;");
                } else {
                    nombre = reemplazar(nombre, "ó", "&oacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'ú') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "ú", "&uacute;");
                } else {
                    nombre = reemplazar(nombre, "ú", "&uacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'Á') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "Á", "&Aacute;");
                } else {
                    nombre = reemplazar(nombre, "Á", "&Aacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'É') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "É", "&Eacute;");
                } else {
                    nombre = reemplazar(nombre, "É", "&Eacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'Í') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "Í", "&Iacute;");
                } else {
                    nombre = reemplazar(nombre, "Í", "&Iacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'Ó') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "Ó", "&Oacute;");
                } else {
                    nombre = reemplazar(nombre, "Ó", "&Oacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'Ú') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "Ú", "&Uacute;");
                } else {
                    nombre = reemplazar(nombre, "Ú", "&Uacute;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'ñ') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "ñ", "&ntilde;");
                } else {
                    nombre = reemplazar(nombre, "ñ", "&ntilde;");
                }

            } else if (archivoRaiz.getNombre().charAt(i) == 'Ñ') {
                if (nombre.equals("")) {
                    nombre = reemplazar(archivoRaiz.getNombre(), "Ñ", "&Ntilde;");
                } else {
                    nombre = reemplazar(nombre, "Ñ", "&Ntilde;");
                }

            }

        }

        if (archivoRaiz.isDirectorio()) {
            lineasHTML.add("<button class='dropdown-btn'>");
            if (nombre.equals("")) {
                lineasHTML.add(archivoRaiz.getNombre() + "<i class='fa fa-caret-down'></i>");
            } else {
                lineasHTML.add(nombre + "<i class='fa fa-caret-down'></i>");
            }
            lineasHTML.add("</button>");
            lineasHTML.add("<div class=\"dropdown-container\">");
            for (Archivo mf : archivoRaiz.getArchivosHijos()) {
                List<String> lineas = generarMenuHTML(mf);
                lineasHTML.addAll(lineas);
            }
            lineasHTML.add("</div>");
        } else {
            for (int i = 0; i < archivoRaiz.getRutaCompleta().length(); i++) {

                if (archivoRaiz.getRutaCompleta().charAt(i) == 'á') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "á", "&aacute;");
                    } else {
                        ruta = reemplazar(ruta, "á", "&aacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'é') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "é", "&eacute;");
                    } else {
                        ruta = reemplazar(ruta, "é", "&eacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'í') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "í", "&iacute;");
                    } else {
                        ruta = reemplazar(ruta, "í", "&iacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'ó') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "ó", "&oacute;");
                    } else {
                        ruta = reemplazar(ruta, "ó", "&oacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'ú') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "ú", "&uacute;");
                    } else {
                        ruta = reemplazar(ruta, "ú", "&uacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'Á') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "Á", "&Aacute;");
                    } else {
                        ruta = reemplazar(ruta, "Á", "&Aacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'É') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "É", "&Eacute;");
                    } else {
                        ruta = reemplazar(ruta, "É", "&Eacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'Í') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "Í", "&Iacute;");
                    } else {
                        ruta = reemplazar(ruta, "Í", "&Iacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'Ó') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "Ó", "&Oacute;");
                    } else {
                        ruta = reemplazar(ruta, "Ó", "&Oacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'Ú') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "Ú", "&Uacute;");
                    } else {
                        ruta = reemplazar(ruta, "Ú", "&Uacute;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'ñ') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "ñ", "&ntilde;");
                    } else {
                        ruta = reemplazar(ruta, "ñ", "&ntilde;");
                    }

                } else if (archivoRaiz.getRutaCompleta().charAt(i) == 'Ñ') {
                    if (ruta.equals("")) {
                        ruta = reemplazar(archivoRaiz.getRutaCompleta(), "Ñ", "&Ntilde;");
                    } else {
                        ruta = reemplazar(ruta, "Ñ", "&Ntilde;");
                    }

                }

            }
            List<String> lineas = new ArrayList<>();
            if (nombre.equals("")) {
                if (ruta.equals("")) {
                    lineas.add("<a target=\"embed\" href='" + archivoRaiz.getRutaCompleta() + "'>" + archivoRaiz.getNombre() + "</a>");
                } else {
                    lineas.add("<a target=\"embed\" href='" + ruta + "'>" + archivoRaiz.getNombre() + "</a>");
                }

            } else {
                if (ruta.equals("")) {
                    lineas.add("<a target=\"embed\" href='" + archivoRaiz.getRutaCompleta() + "'>" + nombre + "</a>");
                } else {
                    lineas.add("<a target=\"embed\" href='" + ruta + "'>" + nombre + "</a>");
                }

            }
            //lineas.add("<a target=\"embed\" href='" + archivoRaiz.getRutaCompleta() + "'>" + archivoRaiz.getNombre() + "</a>");
            return lineas;
        }
        return lineasHTML;
    }

    public String generarnombres(String nombre) {
        String titulonuevo = "";
        for (int i = 0; i < nombre.length(); i++) {

            if (nombre.charAt(i) == 'á') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "á", "&aacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "á", "&aacute;");
                }

            } else if (nombre.charAt(i) == 'é') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "é", "&eacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "é", "&eacute;");
                }

            } else if (nombre.charAt(i) == 'í') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "í", "&iacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "í", "&iacute;");
                }

            } else if (nombre.charAt(i) == 'ó') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "ó", "&oacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "ó", "&oacute;");
                }

            } else if (nombre.charAt(i) == 'ú') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "ú", "&uacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "ú", "&uacute;");
                }

            } else if (nombre.charAt(i) == 'Á') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "Á", "&Aacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "Á", "&Aacute;");
                }
            } else if (nombre.charAt(i) == 'É') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "É", "&Eacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "É", "&Eacute;");
                }

            } else if (nombre.charAt(i) == 'Í') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "Í", "&Iacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "Í", "&Iacute;");
                }

            } else if (nombre.charAt(i) == 'Ó') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "Ó", "&Oacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "Ó", "&Oacute;");
                }

            } else if (nombre.charAt(i) == 'Ú') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "Ú", "&Uacute;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "Ú", "&Uacute;");
                }

            } else if (nombre.charAt(i) == 'ñ') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "ñ", "&ntilde;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "ñ", "&ntilde;");
                }

            } else if (nombre.charAt(i) == 'Ñ') {
                if (titulonuevo.equals("")) {
                    titulonuevo = reemplazar(nombre, "Ñ", "&Ntilde;");
                } else {
                    titulonuevo = reemplazar(titulonuevo, "Ñ", "&Ntilde;");
                }

            }

        }

        return titulonuevo;
    }

    public static String reemplazar(String cadena, String busqueda, String reemplazo) {
        return cadena.replaceAll(busqueda, reemplazo);
    }

    public List<String> generarTitulo(Archivo archivoRaiz, String TituloMenu) {
        List<String> lineasHTML = new ArrayList<>();

        lineasHTML.add("<div class=\"sidebar-header\">");
        lineasHTML.add("<h3>" + generarnombres(TituloMenu) + "</h3>");
        lineasHTML.add("</div>");
        return lineasHTML;
    }

    public List<String> generarLogo(Archivo archivoRaiz, String imagen) {
        List<String> lineasHTML = new ArrayList<>();
        lineasHTML.add("<div align=\"right\">");
        lineasHTML.add(" <img  src=\"" + imagen + "\" class=\"logo\" style=\"width:100px;height:100px;\">");
        lineasHTML.add("</div>");
        return lineasHTML;
    }

    public List<String> tituloIndex(Archivo archivoRaiz, String TituloIndex) {

        List<String> lineasHTML = new ArrayList<>();
        lineasHTML.add("<span >" + generarnombres(TituloIndex) + "</span>");
        return lineasHTML;
    }

    public List<String> nombreBoton(Archivo archivoRaiz, String nombreBoton) {
        String nombreBtn = "Menú";
        List<String> lineasHTML = new ArrayList<>();
        lineasHTML.add("<span >" + generarnombres(nombreBtn) + "</span>");
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
    public List<String> procesarInformacion(Archivo archivoRaiz, List<String> lineasHTML, String TituloMenu, String nombreMenu, String TituloIndex, String imagen) {
        List<String> lineasProcesadas = new ArrayList<String>();
        for (String linea : lineasHTML) {
            lineasProcesadas.add(linea);
            if (linea.contains("<!-- inicio gendoc -->")) {
                List<String> lineasMenu = new ArrayList<>();
                lineasMenu = generarMenuHTML(archivoRaiz);
                lineasProcesadas.addAll(lineasMenu);
            } else if (linea.contains("<!-- inicio Titulo -->")) {
                List<String> lineasTitulo = new ArrayList<>();
                lineasTitulo = generarTitulo(archivoRaiz, TituloMenu);
                lineasProcesadas.addAll(lineasTitulo);
            } else if (linea.contains("<!-- inicio logo -->")) {
                List<String> lineasLogo = new ArrayList<>();
                lineasLogo = generarLogo(archivoRaiz, imagen);
                lineasProcesadas.addAll(lineasLogo);
            } else if (linea.contains("<!-- inicio nombreMenu -->")) {
                List<String> lineasnombreMenu = new ArrayList<>();
                lineasnombreMenu = nombreBoton(archivoRaiz, nombreMenu);
                lineasProcesadas.addAll(lineasnombreMenu);
            } else if (linea.contains("<!-- inicio tituloIndex -->")) {
                List<String> lineastituloIndex = new ArrayList<>();
                lineastituloIndex = tituloIndex(archivoRaiz, TituloIndex);
                lineasProcesadas.addAll(lineastituloIndex);
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
    public void generarArchivoFinalHTML(String directorioInicial, String rutaPlantillaHTML, String rutaArchivoResultado, List<String> extension, String TipoG, String TituloMenu, String nombreMenu, String TituloIndex, String imagen) {
        List<String> lineasHTML = null;
        List<String> lineasProcesadas = null;
        try {
            //recorrerDir(directorio, listaNombres);
            Archivo myFile = escanearDirectorios(directorioInicial, extension, TipoG);
            lineasHTML = leerHTMLDesdePlantilla(rutaPlantillaHTML);
            //lineasProcesadas = procesarInformacion(listaNombres, lineasHTML);
            lineasProcesadas = procesarInformacion(myFile, lineasHTML, TituloMenu, nombreMenu, TituloIndex, imagen);
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
