package br.com.zupacademy.brunomeloesilva.proposta.cartoes;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CARTAO")
public class CartaoModel {

	@Id
	private String id;
	private LocalDateTime emitidoEm;
	private String titular;
	//private List<Object> bloqueios = null;
	//private List<Object> avisos = null;
	//private List<Object> carteiras = null;
	//private List<Object> parcelas = null;
	private Integer limite;
	//private Renegociacao renegociacao;
	//private Vencimento vencimento;
	private String idProposta;
	
	@Deprecated
	public CartaoModel() {/*Para uso do Hibernate no retorno das consultas*/}
	public CartaoModel(CartaoDTOResponse cartaoGerado) {
		this.id = cartaoGerado.getId();
		this.emitidoEm = cartaoGerado.getEmitidoEm();
		this.titular = cartaoGerado.getTitular();
		this.limite = cartaoGerado.getLimite();
		this.idProposta = cartaoGerado.getIdProposta();
	}

	public String getId() {
		return id;
	}
	public LocalDateTime getEmitidoEm() {
		return emitidoEm;
	}
	public String getTitular() {
		return titular;
	}
	public Integer getLimite() {
		return limite;
	}
	public String getIdProposta() {
		return idProposta;
	}	
}