package models;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import play.data.binding.As;
import play.data.validation.Required;
import controllers.LoginController;

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)
public class Morador extends Usuario {

	private static final long serialVersionUID = 1L;
		
	@Required
	@Column(nullable=false)
	public String nomeCompleto;
	
	@Required
	@Temporal(value = TemporalType.DATE)
	@As(lang = {"pt"}, value = {"dd-MM-yyyy"})
	public Date dataNascimento;
	
	@Column(unique=true)
	public String cpf;

	public String identidade;
	
	public String orgaoEmissor;

	@Temporal(TemporalType.DATE)
	@As(lang = {"pt"}, value = {"dd-MM-yyyy"})
	public Date dataEmissao;

	public String telefoneComercial;	
	
	@Required
	public String telefoneResidencial;
	
	@ManyToOne(cascade={CascadeType.ALL},targetEntity=ExameMedico.class,fetch=FetchType.LAZY)
	@LazyToOne(LazyToOneOption.PROXY)
	public ExameMedico exameMedico;

    @LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(cascade={CascadeType.ALL},orphanRemoval=true,mappedBy="morador",fetch=FetchType.LAZY)
	public Collection<Dependente> dependentes = new HashSet<Dependente>();
	
	@ManyToOne(fetch=FetchType.LAZY,optional=true,targetEntity=Arquivo.class)
	@LazyToOne(LazyToOneOption.PROXY)
	public Arquivo foto;

    @LazyCollection(LazyCollectionOption.EXTRA)
	@OneToMany(orphanRemoval=false,fetch = FetchType.LAZY)
	@JoinColumn(name="proprietario_id")
	@Where(clause="dataEntrada <= CURRENT_DATE() AND COALESCE(dataSaida,CURRENT_DATE()) >= CURRENT_DATE()")
	public Collection<Escritura> escrituras =  new HashSet<Escritura>();
	
	@OneToMany(orphanRemoval=false,fetch = FetchType.LAZY)
	@JoinColumn(name="inquilino_id")
	@Where(clause="dataInicioContrato <= CURRENT_DATE() AND COALESCE(dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()")	
	public Collection<ContratoLocacao> contratos =  new HashSet<ContratoLocacao>();
	
	@ManyToOne(fetch=FetchType.LAZY)
	public Usuario criadoPor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	public Usuario alteradoPor;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCriacao = DateTime.now().toDate();
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date dataAlteracao = DateTime.now().toDate();
	
	@Override
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
	
	public Morador() {
		super();
	}
	
	public Morador(String nomeCompleto, String cpf, String email,
			String telefoneResidencial, String telefoneComercial, String identidade, String orgaoEmissor ,Date dataEmissao, Date dataNascimento) {
		
		super(email);
		
		this.nomeCompleto = nomeCompleto;
		this.cpf = cpf;
		this.telefoneResidencial = telefoneResidencial;
		this.telefoneComercial = telefoneComercial;
		this.identidade = identidade;
		this.orgaoEmissor = orgaoEmissor;
		this.dataEmissao = dataEmissao;
		this.dataNascimento = dataNascimento;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof Morador ){
			
			Morador morador = (Morador)obj;
			return ( morador.cpf !=null && morador.cpf.equalsIgnoreCase(this.cpf));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		if( this.cpf !=null ) {
			return this.cpf.hashCode() * 32;
		} else if( this.nomeCompleto !=null ) {
			return this.nomeCompleto.hashCode()  * 32;
		} else {
			return super.hashCode() * 32;
		}
	}
	
	public void adicionarDependente(Collection<Dependente> dependentes) {
		for( Dependente dependente : dependentes ) {
			dependente.setMorador(this);
			this.dependentes.add(dependente);
		}
	}
	
	public static Morador create(String cpf, String nomeCompleto,
			Date dataNascimento, String identidade, String orgaoemissor,
			Date dataemissao, String email, String telefoneResidencial,
			String telefoneComercial) {
		Morador morador = Morador.find("cpf = ?", cpf).first();		
		if( morador == null ) {
			morador = new Morador(nomeCompleto, cpf, email, telefoneResidencial, telefoneComercial, identidade, orgaoemissor, dataemissao, dataNascimento);			
		} else {
			morador.cpf = cpf;
			morador.nomeCompleto = nomeCompleto;
			morador.email = email;
			morador.telefoneComercial = telefoneComercial;
			morador.telefoneResidencial = telefoneResidencial;
			morador.identidade = identidade;
			morador.orgaoEmissor = orgaoemissor;
			morador.dataEmissao = dataemissao;
			morador.dataNascimento = dataNascimento;
		}
		return morador;
	}

	public boolean isDependente() {
		return false;
	}

	public static Morador getByCPF(final String cpf) {
		return Morador.find("cpf = ?", cpf).first();
	}
	
}
