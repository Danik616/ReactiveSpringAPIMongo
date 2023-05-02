package com.api.rest.mongo.repaso.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.api.rest.mongo.repaso.documents.Contacto;

import reactor.core.publisher.Mono;

public interface ContactoRepository extends ReactiveMongoRepository<Contacto, String>{
    Mono<Contacto> findFirstByEmail(String email);
    Mono<Contacto> findAllByTelefonoOrNombre(String telefonoOrNombre);
}
