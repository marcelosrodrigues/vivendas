package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
public class Vaga extends Model implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Required
	public String local;
	
	@Required
	public String numeroVaga;
	
	@ManyToOne
	public Apartamento apartamento;

	public String observacao;

	@OneToOne(mappedBy="vaga",cascade={CascadeType.ALL})
	public Veiculo veiculo;
	
	@ManyToOne(optional=true)
	public Apartamento alugadoPara;
	
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
	
	public Vaga(Apartamento apartamento, String local, String vaga, String observacao) {
		this();
		this.apartamento = apartamento;
		this.local = local;
		this.numeroVaga = vaga;
		this.observacao = observacao;
	}

	public Vaga() {
	}

	public void adicionarCarro(Veiculo veiculo) {
		this.veiculo = veiculo;
		
	}

}
