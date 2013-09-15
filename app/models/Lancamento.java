package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import controllers.LoginController;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@NamedQueries({
	@NamedQuery(name="Lancamento.FindLancamentoByApartamentoAndMonth",query="SELECT L FROM Lancamento L join L.apartamento WHERE L.apartamento = :apartamento AND MONTH(L.dataLancamento) = :mes"),
	@NamedQuery(name="Lancamento.FindLancamentoByMonth",query="SELECT L FROM Lancamento L join L.apartamento WHERE MONTH(L.dataLancamento) = :mes")
})
public class Lancamento extends Model implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Required
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	public Date dataLancamento;
	
	@As(format="#.##",lang="pt-BR")
	@Required
	@Column(nullable=false)
	public BigDecimal valor;
	
	@Required
	@Column(nullable=false)
	public String historico;
	
	@Required
	@ManyToOne(optional=false)
	public Apartamento apartamento;
	
	@ManyToOne(optional=true)
	public Boleto boleto;
	
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
	
	public Lancamento(Date dataLancamento, BigDecimal valor,String historico,
			Apartamento apartamento) {
		this.dataLancamento = dataLancamento;
		this.valor = valor;
		this.apartamento = apartamento;
		this.historico = historico;
	}
	
	public Lancamento() {
		// TODO Auto-generated constructor stub
	}

	public BigDecimal getValor() {
		return this.valor;
	}

}
