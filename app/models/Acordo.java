package models;

import java.math.BigDecimal;
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
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table
public class Acordo extends Model {

	@ManyToOne(optional = false)
	public Apartamento apartamento;
	
	@Temporal(TemporalType.DATE)
	public Date dataInicio;
	
	@Temporal(TemporalType.DATE)
	public Date dataTermino;
	
	@As(format = "#,##")
	@Required
	public BigDecimal valor;
	
	@Required
	public Integer quantidade;
	
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
	
}
