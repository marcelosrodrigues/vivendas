package models;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)
@NamedQueries({
	@NamedQuery(name="Usuario.getByLogin",query = "select u from Usuario u left join u.grupos where u.email = :email")
})
public class Usuario extends Model implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int PRAZO_TROCAR_SENHA = 90;
	
	public static final int NUMERO_MAXIMO_TENTATIVAS_FALHAS = 5;
	
	@Required
	@Email
	@Column(unique=true)
	public String email;
	
	public String password;
	
	public boolean bloqueado = false;
	
	public Date dataGeracaoSenha = new Date();

	public boolean trocasenha = true;

	public int tentativasFalhas = 0;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "membros", joinColumns = { @JoinColumn(nullable = false, insertable = true, name = "membro_id") }, 
								 inverseJoinColumns = { @JoinColumn(nullable = false, insertable = true, name = "grupo_id") })
	public Collection<Grupo> grupos = new HashSet<Grupo>();
	
	public Usuario(String email) {
		this();
		this.email = email;		
	}

	public Usuario() {}

	public String getPassword() {
		return this.password;
	}

	public boolean isBloqueado() {
		return this.bloqueado;
	}

	public Date getDataGeracaoSenha() {
		return this.dataGeracaoSenha;
	}

	public boolean isPrecisaTrocarSenha() {
		if( trocasenha  )
			return true;
		else {
			int dias = DateTime.now().minus(this.dataGeracaoSenha.getTime()).getDayOfYear();
			return ( dias >= Usuario.PRAZO_TROCAR_SENHA );
		}
	}

	public void bloquear() {
		this.bloqueado = true;
	}

	public String gerarNovaSenha() {
		this.password = RandomStringUtils.randomAlphanumeric(10);
		this.bloqueado = false;
		this.trocasenha = true;
		this.dataGeracaoSenha = DateTime.now().toDate();
		return this.password;
	}

	public void aumentarNumeroTentativasFalhas() {
		this.tentativasFalhas ++;
		
		if( this.tentativasFalhas == Usuario.NUMERO_MAXIMO_TENTATIVAS_FALHAS ){
			this.bloquear();
		}
		
	}

	public void liberarTentativas() {
		this.tentativasFalhas = 0;		
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void fazParte(Grupo grupo) {
		this.grupos.add(grupo);		
	}

	public void novaSenha(String password) {
		this.password = password;
		this.trocasenha = false;
		this.dataGeracaoSenha = DateTime.now().toDate();
	}

	public Collection<Grupo> getGrupos() {
		return this.grupos;
	}
	
	@PrePersist
	public void preInsert() {
		this.password = RandomStringUtils.randomAlphanumeric(10);
	}
	
	public static Usuario getByEmail(String email) {
		return Usuario.find("email = ?", email).first();
	}
		
}
