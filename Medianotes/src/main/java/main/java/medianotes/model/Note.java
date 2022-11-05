package main.java.medianotes.model;
import java.io.Serializable;
import java.time.Instant;

//класс записки
public class Note implements Serializable,Comparable<Note>{
	//поля класса
	
	private static final long serialVersionUID = 1L;
	private Folder parentFolder; //папка в которой лежит записка
	private String name; //поле с названием записки
	private String text; //поле с текстом записки
	private String author; //поле с автором записки 
	private final Instant creationDate; //поле с датой создания записки 
	private int author_id; //id автора
	private String status; //поле хранящее статус записки (completed,not completed)
	
	//методы класса 
	
	//конструктор класса (выполняется при создании объекта)
	public Note(String name_in, String text_in, String author_in, Folder parentFolder_in, int user_id_in, String noteStatus_in,Instant creationDate_in ){
		name = name_in;
		text = text_in;
		
		parentFolder = parentFolder_in;
		
		author = author_in;
		
		creationDate = creationDate_in;
		
		author_id=user_id_in;
		
		status=noteStatus_in;
	}
	//геттер названия записки (получение названия записки)
	public String getName() {
		return name;
	}
	//геттер текста записки (получение текста записки)
	public String getText() {
		return text;
	}
	//геттер автора записки (получение автора записки)
	public String getAuthor() {
		return author;
	}
	//геттер даты создания записки (получение даты создания записки)
	public Instant getCreationDate() {
		return creationDate;
	}
	//геттер каталога записки (получение каталога записки)
	public Folder getParentFolder() {
		return parentFolder;
	}
	//геттер статуса записки (получение статуса записки)
	public String getStatus() {
		return status;
	}
	//сеттер названия записки (установка названия записки)
	public void setName(String newName) {
		name=newName;
	}
	//сеттер текста записки (установка текста записки)
	public void setText(String newText) {
		text=newText;
	}
	//сеттер автора записки (установка автора записки)
	public void setAuthor(String newAuthor) {
		author=newAuthor;
	}
	//сеттер каталога записки (установка каталога записки)
	public void setParentFolder(Folder newParentFolder) {
		parentFolder=newParentFolder;
	}
	//сеттер статуса записки (установка статуса записки)
	public void setStatus(String newStatus) {
		status=newStatus;
	}
	//компаратор объектов класса
	public int compareTo(Note n) {
		if(this.getStatus()==null) {
			this.setStatus("notCompleted");
		}
		if(n.getStatus()==null) {
			n.setStatus("notCompleted");
		}
		return this.getStatus().compareTo(n.getStatus());
	}
}
