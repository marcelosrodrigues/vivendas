package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
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

@Entity
@Table
public class Dependente extends Morador implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	public GrauParentesco grauParentesco;
	
	@ManyToOne(optional=false,fetch=FetchType.LAZY)
	public Morador morador;
	
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
	
	public Dependente(String nomeCompleto, String cpf, String email,
			String telefoneResidencial, String telefoneComercial, String identidade, String orgaoEmissor ,Date dataEmissao, Date dataNascimento, GrauParentesco grauParentesco) {
		super(nomeCompleto , cpf , email , telefoneResidencial , telefoneComercial , identidade, orgaoEmissor ,dataEmissao , dataNascimento );
		this.grauParentesco = grauParentesco;
	}
	

	public Dependente() {
	}

	public void setMorador(Morador morador) {
		this.morador = morador;
	}
	
	public boolean isDependente() {
		return true;
	}

	
}
