package com.landschoot.restftp.controller;

import com.landschoot.restftp.entity.FileEntity;
import com.landschoot.restftp.exception.FileException;
import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.landschoot.restftp.config.ServerConfig.GLOBAL_PATH;

/**
 * Created by landschoot on 14/05/17.
 * Controller qui permet la gestion des fichiers
 */
@RestController
@RequestMapping(value = "/api")
public class FileController {

    /**
     * Permet de retourner le chemin à partir de la requête
     * @param request
     * @return
     */
    private String getPath(HttpServletRequest request){
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        path = path.replace("/api/file", "");
        path = path.replace("/api/copy/file", "");
        path = path.replace("/api/directory", "");
        path = path.replace("/api/directories", "");
        if(!path.endsWith("/")){
            path = path+"/";
        }
        return path;
    }

    /**
     * Permet de retourner un file à partir d'un chemin en vérifiant si il existe ou non
     * @param path
     * @return
     * @throws FileException
     */
    private File getFile(String path) throws FileException {
        File file = new File(GLOBAL_PATH + path);
        if (!file.exists()) {
            throw new FileException("Le fichier n'existe pas");
        }
        return file;
    }

    /**
     * Permet de retourner l'ensemble des fichiers d'un répertoire
     * @param filter : la chaine qui permet de filtrer les résultats par nom
     * @param recursif : boolean permettant de déterminer si on souhaite récupérer les sous fichiers ou seulement le premier niveau
     * @param request
     * @return
     * @throws FileException
     */
    @GetMapping(value = "/directories/**")
    public List<FileEntity> getFiles(@RequestParam(value = "chaine", required = false) String filter, @RequestParam(value = "recursif", required = false) boolean recursif, HttpServletRequest request) throws FileException {
        String path = getPath(request);
        File folder = getFile(path);
        if (!folder.isDirectory()) {
            throw new FileException("Le fichier n'est pas un répertoire");
        }
        return getFiles(folder, path, filter, recursif);
    }

    private List<FileEntity> getFiles(File file, String path, String filter, boolean recursif){
        List<FileEntity> toReturn = new ArrayList<>();
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                if (filter == null || subFile.getName().contains(filter)) {
                    toReturn.add(new FileEntity(subFile.getName(), subFile.getPath().replace(GLOBAL_PATH, ""), subFile.length(), subFile.lastModified(), subFile.getParent().replace(GLOBAL_PATH, ""), subFile.isDirectory()));
                }
                if (recursif) {
                    toReturn.addAll(getFiles(subFile, path, filter, recursif));
                }
            }
        }
        return toReturn;
    }

    /**
     * Permet d'ajouter un répertoire
     * @param folder : le répertoire à ajouter
     * @param request
     * @return
     * @throws FileException
     */
    @PostMapping(value = "/directory/**")
    public FileEntity addFolder(@RequestBody FileEntity folder, HttpServletRequest request) throws FileException {
        String path = getPath(request);
        File file = new File(GLOBAL_PATH + path + folder.getName());
        if(file.exists()){
            throw new FileException("Un fichier du même nom existe déjà");
        }
        boolean isCreate = file.mkdirs();
        if(!isCreate){
            throw new FileException("Erreur lors de la création du dossier");
        }
        return new FileEntity(file.getName(), path+file.getName(), file.length(), file.lastModified(), path, file.isDirectory());
    }

    /**
     * Permet de coller un fichier
     * @param file : le fichier à coller
     * @param request
     * @return
     * @throws FileException
     */
    @PostMapping(value = "copy/file/**")
    public FileEntity pasteFile(@RequestBody FileEntity file, HttpServletRequest request) throws FileException {
        String path = getPath(request);
        File fileToPaste = getFile(file.getPath());
        File newFile = new File(GLOBAL_PATH+path+file.getName());
        if(newFile.exists()){
            throw new FileException("Un fichier du même nom existe déjà");
        }
        try {
            newFile.createNewFile();
            FileCopyUtils.copy(fileToPaste, newFile);
        } catch (IOException e) {
            throw new FileException("Erreur lors de la copie du fichier");
        }
        return new FileEntity(fileToPaste.getName(), path+fileToPaste.getName(), fileToPaste.length(), fileToPaste.lastModified(), path, fileToPaste.isDirectory());
    }

    /**
     * Permet de mofifier un fichier
     * @param fileEntity : le fichier à modifier
     * @param request
     * @return
     * @throws FileException
     */
    @PutMapping(value = "/file/**")
    public FileEntity updateFile(@RequestBody FileEntity fileEntity, HttpServletRequest request) throws FileException {
        String path = getPath(request);
        File file = getFile(path);
        String newPath = file.getParent().replace(GLOBAL_PATH, "")+"/"+fileEntity.getName();
        File newFile = new File(GLOBAL_PATH+newPath);
        if(newFile.exists()){
            throw new FileException("Un fichier du même nom existe déjà");
        }
        boolean isUpdate = file.renameTo(newFile);
        if(!isUpdate){
            throw new FileException("Erreur lors de la modification du dossier");
        }
        return new FileEntity(newFile.getName(), newPath, newFile.length(), newFile.lastModified(), newFile.getParent(), newFile.isDirectory());
    }

    /**
     * Permet de supprimer un fichier
     * @param request
     * @throws FileException
     */
    @DeleteMapping(value = "/file/**")
    public void deleteFile(HttpServletRequest request) throws FileException {
        String path = getPath(request);
        File file = getFile(path);
        boolean isDelete;
        if(file.isDirectory()){
            isDelete = deleteDir(file);
        } else {
            isDelete = file.delete();
        }
        if(!isDelete){
            throw new FileException("Erreur lors de la suppression du fichier");
        }
    }

    /**
     * Permet de supprimer un répertoire et ses sous fichiers
     * @param folder : le répertoire à supprimer
     * @return
     * @throws FileException
     */
    private boolean deleteDir(File folder) throws FileException {
        File[] contents = folder.listFiles();
        if (contents != null) {
            for (File f : contents) {
                boolean isDelete = deleteDir(f);
                if(!isDelete){
                    throw new FileException("Erreur lors de la suppression d'un sous fichier du répertoire");
                }
            }
        }
        return folder.delete();
    }

    /**
     * Permet d'ajouter un fichier
     * @param file : le fichier à upload
     * @param request
     * @return
     * @throws FileException
     * @throws IOException
     */
    @PostMapping(value = "/file/**")
    public FileEntity uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws FileException, IOException {
        String pathString = getPath(request);
        byte[] bytes = file.getBytes();
        Path path = Paths.get(GLOBAL_PATH + pathString + file.getOriginalFilename());
        File verifFileExist = new File(GLOBAL_PATH + pathString + file.getOriginalFilename()); // pour vérifier si un fichier du même nom existe déjà
        if(verifFileExist.exists()){
            throw new FileException("Un fichier du même nom existe déjà");
        }
        Files.write(path, bytes);
        File toReturn = getFile(pathString + file.getOriginalFilename());
        return new FileEntity(toReturn.getName(), pathString + toReturn.getName(), toReturn.length(), toReturn.lastModified(), pathString, toReturn.isDirectory());
    }

    /**
     * Permet de récupérer un fichier
     * @param request
     * @param response
     * @throws FileException
     * @throws IOException
     */
    @GetMapping(value = "/file/**", headers = "Accept=*/*")
    public void loadFile(HttpServletRequest request, final HttpServletResponse response) throws FileException, IOException {
        String path = getPath(request);
        File file = getFile(path);
        final InputStream is = new FileInputStream(file);
        IOUtils.copy(is, response.getOutputStream());
        is.close();
        response.flushBuffer();
    }
}
