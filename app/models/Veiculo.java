package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import controllers.LoginController;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table
public class Veiculo extends Model implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Required
	public String marca;
	
	@Required
	public String modelo;
	
	@Required
	public String placa;
	
	@Required
	public String cor;
	
	@Required
	public String ano;
	
	@ManyToOne
	public Vaga vaga;
	
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
	
	public Veiculo(Vaga vaga, String veiculo, String marca, String cor,
			String chapa, String ano) {
		this.vaga = vaga;
		this.marca = marca;
		this.modelo = veiculo;
		this.cor = cor;
		this.ano = ano;
		this.placa = chapa;
	}

	public Veiculo() {
		// TODO Auto-generated constructor stub
	}

}
