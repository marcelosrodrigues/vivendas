package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;
import controllers.LoginController;

@Entity
@org.hibernate.annotations.Entity(dynamicUpdate=true)
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

	@ManyToOne(optional=true)
	public Acordo acordo;
	
	@PrePersist
	public void preInsert() {
		
		final Date dataCriacao = DateTime.now().toDate();
		final Usuario criadoPor = LoginController.getUserAuthenticated();
		
		this.dataCriacao = dataCriacao;
		this.dataAlteracao = dataCriacao;
		this.criadoPor = criadoPor;
		this.alteradoPor = criadoPor;
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

	public Lancamento(Date dataLancamento, BigDecimal valor,String historico,
			Apartamento apartamento, Acordo acordo) {
		this(dataLancamento,valor,historico,apartamento);
		this.acordo = acordo;
	}

	public BigDecimal getValor() {
		return this.valor;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if( other instanceof Lancamento ){
			
			Lancamento lancamento = (Lancamento) other;
			DateTime dataLancamento = new DateTime(this.dataLancamento.getTime());
			DateTime dataNovoLancamento = new DateTime(lancamento.dataLancamento.getTime());
			
			return lancamento.historico.equalsIgnoreCase(this.historico) && 
				   dataLancamento.getMonthOfYear() == dataNovoLancamento.getMonthOfYear() && 
				   dataLancamento.getYear() == dataNovoLancamento.getYear() &&
				   lancamento.apartamento.equals(this.apartamento);
			
		}
		return false;
	}
		
	public static List<Lancamento> findLancamentoPorData(DateTime dataLancamento) {	
		return Lancamento.find("SELECT L from Lancamento L inner join fetch L.apartamento A inner join fetch a.bloco B left join A.escritura left join A.contratoLocacao WHERE MONTH(dataLancamento) = ? AND YEAR(dataLancamento) = ? ORDER BY B.bloco , A.numero",  dataLancamento.getMonthOfYear(), dataLancamento.getYear() ).fetch();		
	}
	
	public static List<Lancamento> findLancamentoPorData(DateTime dataLancamento, Apartamento apartamento) {		
		return Lancamento.find("MONTH(dataLancamento) = ? AND YEAR(dataLancamento) = ? AND apartamento = ?",  dataLancamento.getMonthOfYear(), dataLancamento.getYear(), apartamento ).fetch();		
	}

}
