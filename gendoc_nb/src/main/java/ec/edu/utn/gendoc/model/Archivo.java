package ec.edu.utn.gendoc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representacion de un archivo o directorio con sus datos mas importantes.
 * Contiene una relacion OneToMany a otros archivos o directorios para
 * mantener una estructura de datos jerarquica.
 * 
 * @author mrea
 */
public class Archivo {
    private boolean directorio;
    private String nombre;
    private String rutaCompleta;
    private String extension;
    private List<Archivo> archivosHijos;
    
    public Archivo(){
        archivosHijos=new ArrayList<>();
    }
    /**
     * Constructor con parametros. Esta clase representa a un archivo o directorio.
     * @param directorio Indica si el nodo es un directorio o no.
     * @param nombre El nombre corto.
     * @param rutaCompleta La ruta completa.
     */
    public Archivo(boolean directorio, String nombre, String rutaCompleta) {
        archivosHijos=new ArrayList<>();
        this.directorio = directorio;
        this.nombre = nombre;
        this.rutaCompleta = rutaCompleta;
        //TODO obtener la extension a partir del nombre.
    }

    public boolean isDirectorio() {
        return directorio;
    }

    public void setDirectorio(boolean directorio) {
        this.directorio = directorio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaCompleta() {
        return rutaCompleta;
    }

    public void setRutaCompleta(String rutaCompleta) {
        this.rutaCompleta = rutaCompleta;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<Archivo> getArchivosHijos() {
        return archivosHijos;
    }

    public void setArchivosHijos(List<Archivo> archivosHijos) {
        this.archivosHijos = archivosHijos;
    }
    
}