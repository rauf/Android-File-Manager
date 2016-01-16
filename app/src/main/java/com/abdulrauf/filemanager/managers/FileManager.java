package com.abdulrauf.filemanager.managers;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

/**
 * Created by abdul on 31/12/15.
 */

public class FileManager {


    private Stack<File> pathStack;
    private boolean showHiddenFiles;
    private String sortingType;
    private boolean caseSensitive;



    public FileManager() {
        pathStack = new Stack<>();
    }


    public void pushToPathStack(File file) {
        pathStack.push(file);
    }

    public File popFromPathStack() {
        return pathStack.pop();
    }

    public int getPathStackItemsCount(){
        return pathStack.size();
    }


    public void initialisePathStackWithAbsolutPath(String dir) {



    }

    public File getCurrentDirectory(){
        return pathStack.peek();
    }


    public String getExtension(String url) {


        if (url.contains("?")) {
             url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf("."));
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }


    public boolean copyToDirectory(File old, File newDir) throws IOException {


        int temp;
        File copiedFile = new File(newDir + "/" + old.getName());

        if( !old.exists() || !newDir.exists())
            return false;

        if( old.isFile() && old.canRead() && newDir.isDirectory() && newDir.canWrite()) {

            BufferedInputStream in = null;
            BufferedOutputStream out = null;

            try {

                in = new BufferedInputStream(new FileInputStream(old));
                out = new BufferedOutputStream(new FileOutputStream(copiedFile));

                while ( (temp = in.read()) != -1) {
                    out.write(temp);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(in != null)
                    in.close();
                if(out != null)
                    out.close();
            }
        }

        else if (old.isDirectory() && old.canRead() && newDir.isDirectory() && newDir.canExecute()) {

            File[] files = old.listFiles();

            if(!copiedFile.mkdirs())
                return false;

            for (File singleFile : files) {
                copyToDirectory(singleFile, copiedFile);
            }
        }

        return true;
    }


    public boolean deleteFile(File file) {

            if(file.isFile()){
                file.delete();
            }
            else if(file.isDirectory() && file.canWrite() ) {
                ArrayList<File> subFiles = new ArrayList<>(Arrays.asList(file.listFiles()));

                for (File subFile : subFiles) {

                    deleteFile(subFile);
                }
                file.delete();
            }

        return true;
    }



    public boolean renameFileTo(File file,String newName) {

        if(!file.exists() || !file.canRead())
            return false;

        File newFile = new File(file.getParent() ,newName);

        if(newFile.exists())
            return false;

        return file.renameTo(newFile);
    }


    public boolean moveToDirectory(File old,File newDir){

        if(!old.exists() || !newDir.exists())
            return false;

        return old.renameTo(new File(newDir,old.getName()));
    }


    public ArrayList<File> sort(ArrayList<File> files) {

        ArrayList<File> sortedFiles = new ArrayList<>();

        switch (sortingType) {

            case EventManager.SORT_ASC :
                sortedFiles = sortAscending(files);
                break;

            case EventManager.SORT_DESC:
                sortedFiles = sortDescending(files);
                break;

            default:
                return files;
        }

        return sortedFiles;
    }



    public ArrayList<File> sortAscending (ArrayList<File> files) {

        if (caseSensitive) {
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });

        } else {
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            });
        }
    return files;
    }


    public ArrayList<File> sortDescending (ArrayList<File> files) {

        if (caseSensitive) {
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return rhs.getName().compareTo(lhs.getName());
                }
            });

        } else {
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return rhs.getName().compareToIgnoreCase(lhs.getName());
                }
            });
        }
        return files;
    }

    public boolean newFolder(File dir, String name) {

        return new File(dir,name).mkdir();
    }


    public boolean isHiddenFile(File file) {

        return file.getName().startsWith(".");
    }

    public ArrayList<File> removeHiddenFiles(ArrayList<File> files) {

        ArrayList<File> list = new ArrayList<>();

        for(File file: files) {
            if (!isHiddenFile(file))
                list.add(file);
        }

        return list;
    }



    public void setShowHiddenFiles(boolean showHiddenFiles) {
        this.showHiddenFiles = showHiddenFiles;
    }

    public boolean isFileHidden() {
        return showHiddenFiles;
    }

    public String getSortingType() {
        return sortingType;
    }

    public void setSortingType(String sortingType) {
        this.sortingType = sortingType;
    }

    public void setSortingStyle(String sortingType, boolean caseSensitive) {
        this.sortingType = sortingType;
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }


}
