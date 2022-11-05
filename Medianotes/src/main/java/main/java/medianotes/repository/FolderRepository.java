package main.java.medianotes.repository;

import java.util.Set;

import main.java.medianotes.model.Folder;

//интерфейс для хранилища папок
public interface FolderRepository{
	//описание методов класса
	Set<Folder> getFolders(int user_id_in);  //описание метода получения всех папок
	void createFolder(String name,Folder parentFolder,int user_id_in);  //описание метода создания папки
	void removeFolder(String name,String parentFolder,int user_id_in, NoteRepository noteRepository);  //описание метода удаления папки
	String getPath(String name,Folder parentFolder,int user_id_in);  //описание метода получения пути папки
	Folder findFolder(String name,Folder parentFolder,int user_id_in);  //описание метода поиска папки
	void setCurrent(String currentDir_in,int user_id_in);  //описание метода установки текущей папки
	Folder getCurrent(int user_id_in); //описание метода получения текущей папки
	Folder pathToFolder(String path,int user_id_in); //описание метода получение папки из пути
}
