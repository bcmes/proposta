package br.com.zupacademy.brunomeloesilva.proposta.cartoes;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.zupacademy.brunomeloesilva.proposta.biometria.BiometriaDTORequest;
import br.com.zupacademy.brunomeloesilva.proposta.biometria.BiometriaModel;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
	
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	APICartao apiCartao;
	
	@PostMapping("/{numeroCartao}")
	@Transactional
	public ResponseEntity<Void> cadastrar(@PathVariable String numeroCartao
			, @RequestBody @Valid BiometriaDTORequest biometriaDTORequest
			,UriComponentsBuilder uriComponentsBuilder) {
		
		CartaoModel CartaoModel = entityManager.find(CartaoModel.class, numeroCartao);
		BiometriaModel biometriaModel = null;
		if(CartaoModel != null) {
			biometriaModel = biometriaDTORequest.toModel(CartaoModel);
			entityManager.persist(biometriaModel);
			URI uri = uriComponentsBuilder.path("/cartoes/{id}").build(biometriaModel.getId());
			return ResponseEntity.created(uri).build();
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@PatchMapping("/{numeroCartao}")
	@Transactional
	public ResponseEntity<Void> bloquear(@PathVariable String numeroCartao
			, @RequestHeader("user-agent") String userAgent
			, @RequestHeader("host") String host) {
		
		CartaoModel cartaoModel = entityManager.find(CartaoModel.class, numeroCartao);
		if(cartaoModel == null)
			return ResponseEntity.notFound().build();
		
		if(cartaoModel.getBloqueio() != null)
			return ResponseEntity.unprocessableEntity().build();
			
		BloqueioModel bloqueioModel = new BloqueioModel(userAgent, host);
		entityManager.persist(bloqueioModel);
		cartaoModel.setBloqueio(bloqueioModel);
		
		//TODO O correto para resolver este ponto aqui, seria com publicação de eventos.
		//Porque em um cenário real, se essa requisição demorar, a transação no banco fica
		//aberta esperando o retorno do request !
		try{
			CartaoStatusDTOResposnse informaBloqueioCartao = apiCartao.informaBloqueioCartao(numeroCartao, new BloqueioDTORequest());
			cartaoModel.setStatus(informaBloqueioCartao.getResultado());
		}catch (Exception e) {}

		return ResponseEntity.ok().build();		
	}
}
