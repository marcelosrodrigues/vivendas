package models;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table
public class Grupo extends Model implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(unique = true)
	public String nome;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "membros", joinColumns = { @JoinColumn(nullable = false, insertable = true, name = "grupo_id") }, 
								 inverseJoinColumns = { @JoinColumn(nullable = false, insertable = true, name = "membro_id") })
	public Collection<Usuario> usuarios = new HashSet<Usuario>();
	
	public Grupo(String nome) {
		this.nome = nome;
	}
	
	public void adicionar(Usuario membro){
		this.usuarios.add(membro);
		membro.fazParte(this);
	}

}
