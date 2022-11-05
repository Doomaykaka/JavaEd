package main.java.medianotes.repository;

import java.util.List;

import main.java.medianotes.model.Note;

//интерфейс для хранилища записок
public interface NoteRepository {
	//описание методов класса	
	Note save(Note note,int user_id_in);  //описание метода сохранения записки
	
	List<Note> getAllNotes(int user_id_in);  //описание метода получения списка со всеми записками
	
	void remove(String name,int user_id_in);  //описание метода удаления записки
	
	Note edit(Note note,int user_id_in);  //описание метода изменения записки
}
