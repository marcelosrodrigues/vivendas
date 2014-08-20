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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.Model;
import play.libs.MimeTypes;
import services.MoradorService;
import controllers.LoginController;
import dto.EscrituraResultList;
import dto.ResultList;
import exceptions.DuplicateRegisterException;

@Entity
@Table
@Where(clause = "dataEntrada <= CURRENT_DATE() AND COALESCE(dataSaida,CURRENT_DATE()) >= CURRENT_DATE()")
public class Escritura extends Model implements Serializable, Documentacao {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Arquivo escritura;

    @Required
    @Temporal(TemporalType.DATE)
    public Date dataEntrada;

    @Temporal(TemporalType.DATE)
    public Date dataSaida;

    @Required
    @ManyToOne(optional = false, cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    public Morador proprietario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    public Apartamento apartamento;

    @ManyToOne(fetch = FetchType.LAZY)
    public Usuario criadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    public Usuario alteradoPor;

    @Temporal(TemporalType.TIMESTAMP)
    public Date dataCriacao = DateTime.now().toDate();

    @Temporal(TemporalType.TIMESTAMP)
    public Date dataAlteracao = DateTime.now().toDate();

    @Transient
    private MoradorService service;

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

    public Morador getProprietario() {
        return this.proprietario;
    }

    public Escritura(Morador proprietario, Apartamento apartamento, Date dataEntrada) {
        this.proprietario = proprietario;
        this.dataEntrada = dataEntrada;
        this.apartamento = apartamento;

    }

    public Escritura() {

    }

    @Override
    public void add(File arquivo) throws FileNotFoundException {
        if (arquivo != null) {
            escritura = new Arquivo(arquivo.getName(), new Blob());
            escritura.contentFile.set(new FileInputStream(arquivo), MimeTypes.getContentType(arquivo.getName()));
        }
    }

    @Override
    public void setService(MoradorService service) {
        this.service = service;

    }

    @Override
    public void salvar() throws DuplicateRegisterException {
        service.adicionar(this);

    }

    @Override
    public void setDataSaidaImovel(Date dataSaidaImovel) {
        dataSaida = dataSaidaImovel;
    }

    public static Escritura findByApartamento(final Apartamento apartamento) {
        return Escritura.find("SELECT e from Escritura e WHERE e.apartamento = ?", apartamento).first();
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = false;

        if (other instanceof Escritura) {
            Escritura e = (Escritura) other;
            equals = e.apartamento.equals(this.apartamento) &&
                    e.proprietario.equals(this.proprietario);
        }

        return equals;
    }

    public static ResultList<Escritura> findBy(final Long bloco, final Long apartamento, final String morador) {

        final Session session = (Session) Escritura.em().getDelegate();

        Criteria criteria = session.createCriteria(Escritura.class)
                .createAlias("apartamento", "a", Criteria.INNER_JOIN)
                .createAlias("proprietario", "p", Criteria.INNER_JOIN)
                .createAlias("a.bloco", "b", Criteria.INNER_JOIN)
                .addOrder(Order.asc("b.bloco"))
                .addOrder(Order.asc("a.numero"))
                .addOrder(Order.asc("p.nomeCompleto"));

        if (apartamento != null && apartamento > 0L) {
            criteria = criteria.add(Restrictions.eq("a.id", apartamento));
        } else if (bloco != null && bloco > 0L) {
            criteria = criteria.add(Restrictions.eq("b.id", bloco));
        }

        if (!StringUtils.isBlank(morador)) {
            criteria = criteria.add(Restrictions.ilike("p.nomeCompleto", morador + "%"));
        }
        return new EscrituraResultList(criteria);

    }
}
