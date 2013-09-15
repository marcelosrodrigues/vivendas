package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import controllers.LoginController;

import play.db.jpa.Model;

@Entity
public class Boleto extends Model implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@ManyToOne(optional=false)
	public Apartamento apartamento;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	public Date dataVencimento;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	public Date dataCancelamento;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	public Date dataPagamento;

	@Column(nullable=false)
	public BigDecimal valor;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	public Date dataGeracao = DateTime.now().toDate();
	
	@OneToMany(mappedBy="boleto",cascade={CascadeType.ALL,CascadeType.PERSIST})
	public Collection<Lancamento> lancamentos = new HashSet<>();
	
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
		this.calcular();
	}

	@PreUpdate
	public void preUpdate() {
		this.dataAlteracao = DateTime.now().toDate();
		this.alteradoPor = LoginController.getUserAuthenticated();
	}
	
	public Boleto(Apartamento apartamento, Date dataVencimento) {
		
		this.apartamento = apartamento;
		this.dataVencimento = dataVencimento;
	}

	public boolean isPago() {
		return dataPagamento != null;
	}
	
	public boolean isVencido() {
		return (DateTime.now().toDate().after(this.dataVencimento) && dataPagamento == null && !this.isCancelado());
	}
	
	public boolean isCancelado() {
		return this.dataCancelamento != null;
	}

	public Date getDataVencimento() {
		return this.dataVencimento;
	}

	public void cancela() {
		this.dataCancelamento = DateTime.now().toDate();		
	}

	public void adicionarLancamento(Lancamento lancamento) {
		this.lancamentos.add(lancamento);	
		lancamento.boleto = this;
		
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
		
	}

	public void calcular() {
		BigDecimal valor = new BigDecimal(0);
		for(Lancamento lancamento : this.lancamentos ){
			valor = valor.add(lancamento.getValor());
		}
		this.valor = valor;
		
	}	
	
}
