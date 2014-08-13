package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;
import org.joda.time.DateTime;

import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.MimeTypes;
import services.MoradorService;
import controllers.LoginController;
import exceptions.DuplicateRegisterException;

@Entity
@Table
@NamedQueries({
	 @NamedQuery(name="ContratoLocacao.getByApartamento", 
			    query="SELECT e from ContratoLocacao e WHERE e.apartamento = :apartamento AND e.dataInicioContrato <= CURRENT_DATE() AND COALESCE(e.dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()")
})
@Where(clause = "dataInicioContrato <= CURRENT_DATE() AND COALESCE(dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()")
public class ContratoLocacao extends Model implements Serializable , Documentacao{
	
	private static final long serialVersionUID = 1L;
	
	@Transient
	private MoradorService service;
	
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	public Arquivo contrato;
	
	@Temporal(TemporalType.DATE)
	public Date dataInicioContrato;
	
	@Temporal(TemporalType.DATE)
	public Date dataTerminoContrato;
	
	@ManyToOne(optional=false,cascade={CascadeType.ALL})
	public Morador inquilino;
	
	@ManyToOne(optional=false,fetch = FetchType.LAZY)
	public Apartamento apartamento;
	
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
	
	public ContratoLocacao(Morador morador, Apartamento apartamento , Date dataInicioContrato ) {
		this.inquilino = morador;
		this.dataInicioContrato = dataInicioContrato;
		this.apartamento = apartamento;
	}
	
	public ContratoLocacao() {}

	public Morador getInquilino() {
		return this.inquilino;
	}

	public boolean isNovoInquilino() {
		
		return (this.getInquilino().getId() == null || this.getInquilino().getId() == 0L);
		
	}
	
	
	@Override
	public void add(File arquivo) throws FileNotFoundException {
		if( arquivo != null ) {
			contrato = new Arquivo(arquivo.getName(),new Blob());
			contrato.contentFile.set(new FileInputStream(arquivo), MimeTypes.getContentType(arquivo.getName()));
		}
	}

	@Override
	public void setService(MoradorService service) {
		this.service = service;
		
	}
	
	@Override
	public void salvar() throws DuplicateRegisterException {
		this.service.adicionar(this);
	}
	
	@Override
	public boolean equals(Object other) {
		boolean equals = false;
		
		if( other instanceof ContratoLocacao ){
			ContratoLocacao e = (ContratoLocacao)other;
			equals = e.apartamento.equals(this.apartamento) && 
					 e.inquilino.equals(this.inquilino);
		}
		
		return equals;
	}
	
	@Override
	public void setDataSaidaImovel(Date dataSaidaImovel) {
		dataTerminoContrato = dataSaidaImovel;		
	}

	public static ContratoLocacao findByApartamento(final Apartamento apartamento) {
		return ContratoLocacao.find("SELECT e from ContratoLocacao e WHERE e.apartamento = ?", apartamento).first();
	}

}
