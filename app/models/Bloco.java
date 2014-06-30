package models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import play.db.jpa.Model;
import controllers.LoginController;

@Entity
@Table
public class Bloco extends Model implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(unique=true)
	public String bloco;
	
	@ManyToOne(fetch=FetchType.LAZY)
	public Usuario criadoPor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	public Usuario alteradoPor;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCriacao = DateTime.now().toDate();
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataAlteracao = DateTime.now().toDate();
	
	@PrePersist
	public void preInsert() {
		this.dataCriacao = DateTime.now().toDate();
		this.dataAlteracao = DateTime.now().toDate();
		this.criadoPor = LoginController.getUserAuthenticated();
		this.alteradoPor = LoginController.getUserAuthenticated();
	}

	@PreUpdate
	public void preUpdate() {
		this.dataAlteracao = DateTime.now().toDate();
		this.alteradoPor = LoginController.getUserAuthenticated();
	}

	public Bloco(String bloco) {
		this();
		this.bloco = bloco;
	}
	
	public static List<Bloco> list() {
		return Bloco.find("order by bloco")
			 .fetch();
	}
	
	public Bloco() {}

	@Override
	public boolean equals(Object obj) {
		if( obj instanceof Bloco){
			Bloco other = (Bloco) obj;
			return this.bloco.equals(other.bloco);
		}
		return false;
	}
	
	
	@Override
	public String toString() {
		return this.bloco;
	}

}
