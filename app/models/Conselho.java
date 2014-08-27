package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import play.db.jpa.Model;

@Entity
public class Conselho extends Model implements Serializable {

	@ManyToOne(optional = false)
	public Morador sindico;

	@ManyToOne(optional = false)
	public Morador subsindico;

	@Temporal(TemporalType.DATE)
	public Date inicioMandato;

	@Temporal(TemporalType.DATE)
	public Date terminoMandato;

	@ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
	public Arquivo ata;

	@ManyToOne(fetch = FetchType.LAZY)
	public Usuario criadoPor;

	@ManyToOne(fetch = FetchType.LAZY)
	public Usuario alteradoPor;

	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCriacao = DateTime.now().toDate();

	@Temporal(TemporalType.TIMESTAMP)
	public Date dataAlteracao = DateTime.now().toDate();

	public static Conselho vigente() {
		return Conselho.find("? between inicioMandato and terminoMandato",
				DateTime.now().toDate()).first();
	}

}
