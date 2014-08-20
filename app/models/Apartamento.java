package models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Where;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import play.data.validation.Required;
import play.db.jpa.Model;
import controllers.LoginController;
import dto.ApartamentoResultList;
import dto.ResultList;



@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "bloco_id",
		"numero" }) })
@NamedQueries({ @NamedQuery(name = "Apartamento.ListByBloco", query = "SELECT a from Apartamento a join a.bloco where a.bloco = :bloco") })
public class Apartamento extends Model implements Serializable {

	private static final long serialVersionUID = 1L;

	@Required
	@Column
	public String numero;

	@Required
	@Column
	public BigDecimal area;

	@Required
	@ManyToOne(optional = false, fetch=FetchType.EAGER)
	@JoinColumn(name = "bloco_id", referencedColumnName = "id")
	public Bloco bloco;

	@OneToOne(cascade = { CascadeType.ALL }, mappedBy = "apartamento", fetch = FetchType.LAZY)
	@Where(clause = "dataInicioContrato <= CURRENT_DATE() AND COALESCE(dataTerminoContrato,CURRENT_DATE()) >= CURRENT_DATE()")
	public ContratoLocacao contratoLocacao;

	@OneToOne(cascade = { CascadeType.ALL }, mappedBy = "apartamento", fetch = FetchType.LAZY)
	@Where(clause = "dataEntrada <= CURRENT_DATE() AND COALESCE(dataSaida,CURRENT_DATE()) >= CURRENT_DATE()")
	public Escritura escritura;	

	@OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL }, mappedBy = "apartamento", fetch = FetchType.LAZY)
	public Collection<Vaga> vagas = new HashSet<Vaga>();

	@OneToMany(orphanRemoval = true, cascade = { CascadeType.ALL }, mappedBy = "alugadoPara", fetch = FetchType.LAZY)
	public Collection<Vaga> vagasAlugadas = new HashSet<Vaga>();

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "apartamento")
	@OrderBy("dataLancamento desc")
	public Collection<Lancamento> lancamento = new HashSet<Lancamento>();

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "apartamento")
	@OrderBy("dataVencimento desc")
	public Collection<Boleto> boletos = new HashSet<Boleto>();

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
	
	public Apartamento(Bloco bloco, String numero, BigDecimal area) {
		this.bloco = bloco;
		this.numero = numero;
		this.area = area;
	}

	public Apartamento() {}

	public void adicionarProprietario(Morador proprietario) {
		Escritura escritura = new Escritura(proprietario, this, DateTime.now()
				.toDate());
		this.escritura = escritura;
	}
	public ContratoLocacao getContratoLocacao() {
		return this.contratoLocacao;
	}

	public Escritura getEscritura() {
		return this.escritura;
	}

	public Morador getProprietario() {
		return this.getEscritura().getProprietario();
	}

	public Morador getMorador() {

		if (this.getContratoLocacao() == null) {
			return this.getProprietario();
		} else {
			return this.getContratoLocacao().getInquilino();
		}

	}

	public void adicionarVaga(Vaga vaga) {
		if (!this.vagas.contains(vaga)) {
			this.vagas.add(vaga);
		}
	}

	public void fazerLancamento(Date dataLancamento, BigDecimal valor,
			String historico) {
		Lancamento conta = new Lancamento(dataLancamento, valor, historico,
				this);
		this.lancamento.add(conta);
	}

	public void fazerLancamento(BigDecimal valor) {
		this.fazerLancamento(DateTime.now().toDate(), valor, null);
	}

	public Boleto gerarBoleto(Date dataVencimento) {

		for (Boleto boleto : this.boletos) {
			if (boleto.getDataVencimento().equals(dataVencimento)
					&& !boleto.isCancelado() && !boleto.isPago()) {
				boleto.cancela();
			}
		}

		Boleto boleto = new Boleto(this, dataVencimento);
		this.boletos.add(boleto);
		return boleto;

	}

	public void adicionarBoleto(Boleto boleto) {
		boleto.calcular();

		for (Boleto b : this.boletos) {
			if (b.getDataVencimento().equals(boleto.getDataVencimento())
					&& !b.isCancelado() && !b.isPago()) {
				b.cancela();
			}
		}

		this.boletos.add(boleto);

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Apartamento) {
			Apartamento other = (Apartamento) obj;
			return other.bloco.equals(this.bloco)
					&& other.numero.equals(this.numero);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("Apartamento[bloco=%s , numero=%s]",
				this.bloco, this.numero);
	}

	public void fazerLancamento(Lancamento lancamento) {
		this.lancamento.add(lancamento);
	}

	public static List<Apartamento> list() {
        return Apartamento.find("ORDER BY bloco.bloco ASC, numero ASC").fetch();
	}

    public static ResultList<Apartamento> findBy( final Bloco bloco , final Apartamento apartamento ) {
        final Session session = (Session) Apartamento.em().getDelegate();
        Criteria criteria = session.createCriteria(Apartamento.class)
                                   .createAlias("bloco","b", Criteria.INNER_JOIN)
                                   .createAlias("escritura","e",Criteria.INNER_JOIN)
                                   .createAlias("e.proprietario","p",Criteria.INNER_JOIN)
                                   .createAlias("contratoLocacao","c",Criteria.LEFT_JOIN)                                   
                                   .createAlias("c.inquilino","i",Criteria.LEFT_JOIN)
                                   .addOrder(Order.asc("b.bloco"))
                                   .addOrder(Order.asc("numero"));

        if( bloco != null ) {
            criteria = criteria.add(Restrictions.eq("bloco",bloco));
        }

        if( apartamento != null ){
            criteria = criteria.add(Restrictions.eq("b.apartamento",apartamento));
        }

        return new ApartamentoResultList(criteria);

    }
    

	public static List<Apartamento> listByBloco(final Bloco bloco) {
		return Apartamento.find("SELECT a from Apartamento a join a.bloco where a.bloco = ? order by a.numero", bloco)
						  .fetch();
	}

    public static List<Apartamento> listByBlocoId(final Long bloco) {
        return Apartamento.find("SELECT a from Apartamento a join a.bloco b where b.id = ? order by a.numero", bloco)
                .fetch();
    }
    
    public static BigDecimal getAreaTotal() {
    	return (BigDecimal) Apartamento.em()
    								   .createQuery("SELECT sum(area) from Apartamento")
    								   .getSingleResult();
    }

}
