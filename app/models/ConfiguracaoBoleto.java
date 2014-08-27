package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.DateTime;

import play.data.validation.Required;
import play.db.jpa.Model;
import controllers.LoginController;

@Entity
public class ConfiguracaoBoleto extends Model implements Serializable {

	@Required(message = "Banco emissor é obrigatório")
	public String banco;

	@Required(message = "Cedente é obrigatório")
	public String cedente;

	@Required(message = "Código da carteira é obrigatório")
	public String carteira;

	@Required(message = "Nosso número é obrigatório")
	public String nossoNumero;

	@Required(message = "")
	public String digitoNossoNumero;

	@Required(message = "Conta corrente é obrigatório")
	public String contacorrente;

	@Required(message = "")
	public String digitoContaCorrente;

	@ManyToOne(fetch = FetchType.LAZY)
	public Usuario criadoPor;

	@ManyToOne(fetch = FetchType.LAZY)
	public Usuario alteradoPor;

	@Temporal(TemporalType.TIMESTAMP)
	public Date dataCriacao = DateTime.now().toDate();

	@Temporal(TemporalType.TIMESTAMP)
	public Date dataAlteracao = DateTime.now().toDate();

	@Required(message = "Agência é obrigatório")
	public int agencia;

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

}
