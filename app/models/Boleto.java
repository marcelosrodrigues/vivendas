package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import play.db.jpa.Model;
import controllers.LoginController;
import dto.BoletoResultList;
import dto.ResultList;

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
	public Collection<Lancamento> lancamentos = new HashSet<Lancamento>();
	
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
	
	public boolean podeImprimir() {
		DateTime dataVencimento = new DateTime(this.dataVencimento.getTime());
		return dataVencimento.getYear() > 1997;
	}
	
	public static List<Boleto> findBoletosAbertos(String bloco , String numero) {
		
		final String QUERY = "select b from Boleto b " +
				" join b.apartamento a " +
				" join a.bloco bl " +
				" where b.dataPagamento is null " +
				"   and b.dataCancelamento is null " +
				"   and a.numero = ?" +
				"   and bl.bloco = ? " +
				"   and b.valor > 0 " +
				" order by b.dataVencimento";
		
		return Boleto.find(QUERY, numero , bloco).fetch();
		
	}
	
	public static List<Boleto> findBoletosAbertos(Morador morador) {
		
		
		final String QUERY = "select b from Boleto b " +
				" join b.apartamento a " +
				" left join a.escritura e " +
				" left join a.contratoLocacao c" +
				" where b.dataPagamento is null " +
				"   and b.dataCancelamento is null " +				
				"   and b.valor > 0 " +				
				"   and (e.proprietario = :morador or c.inquilino = :morador ) " +
				" order by b.dataVencimento";
		
		return Boleto.find(QUERY).bind("morador", morador).fetch();
		
	}
	
	public static List<Boleto> findByDataVencimento(final Date dataVencimento ){
		return Boleto.find("dataVencimento = ? and dataCancelamento is null order by apartamento.bloco.bloco , apartamento.numero" , dataVencimento).fetch();
	}
	
	public static List<Map> findInadimplentes() {
		
		final String QUERY = "select a.numero , bl.bloco , sum(valor) from boleto b " +
							 " inner join apartamento a on apartamento_id = a.id " +
							 " inner join bloco bl on bl.id = a.bloco_id " +
							 " where dataPagamento is null " +
							 "   and dataCancelamento is null " +
							 "   and datediff( current_date , dataVencimento ) > 30 " +
							 " group by a.numero , bl.bloco  " +
							 " having sum(valor) > 0 ";	
		
		return em().createNativeQuery(QUERY).getResultList();
	}
	
	public static ResultList<Boleto> findBy(final Date dataVencimento , final Bloco bloco , final Apartamento apartamento) {
		
		Session session = (Session) Boleto.em().getDelegate();
		Criteria criteria = session.createCriteria(Boleto.class)
								   .createCriteria("apartamento", "a", Criteria.INNER_JOIN)
								   .createCriteria("a.bloco", "b", Criteria.INNER_JOIN)
								   .addOrder(Order.asc("b.bloco"))
								   .addOrder(Order.asc("a.numero"))
								   .addOrder(Order.asc("dataVencimento"))
								   .addOrder(Order.asc("dataPagamento"))
								   .addOrder(Order.asc("dataCancelamento"));
								   
		if( dataVencimento != null ) {
			criteria = criteria.add(Restrictions.eq("dataVencimento", dataVencimento));
		}
		
		if( bloco != null ) {
			criteria = criteria.add(Restrictions.eq("a.bloco", bloco));
		}
		
		if( apartamento != null ) {
			criteria = criteria.add(Restrictions.eq("apartamento", apartamento));
		}
		
		return new BoletoResultList(criteria);
	}
	
}
