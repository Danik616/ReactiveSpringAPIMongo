package com.api.rest.mongo.repaso.functional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.BodyInserters;


import com.api.rest.mongo.repaso.documents.Contacto;
import com.api.rest.mongo.repaso.repository.ContactoRepository;

import reactor.core.publisher.Mono;

@Component
public class ContactoHandler {
    
    @Autowired
    ContactoRepository contactoRepository;

    private Mono<ServerResponse> response404 = ServerResponse.notFound().build(); 
    private Mono<ServerResponse> response406 = ServerResponse.status(HttpStatus.NOT_ACCEPTABLE).build();

    //Listar contactos

    public Mono<ServerResponse> listarContactos(ServerRequest request){
        return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(contactoRepository.findAll(), Contacto.class);
    }

    //Listar un solo contacto

    public Mono<ServerResponse> obtenerContactoPorId(ServerRequest request){
        String id=request.pathVariable("id");

        return contactoRepository.findById(id)
        .flatMap(contacto -> 
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(contacto)))
        .switchIfEmpty(response404);
    }

    // Listar un solo contacto por Email

    public Mono<ServerResponse> obtenerContactoPorEmail(ServerRequest request){
        String email=request.pathVariable("email");

        return contactoRepository.findFirstByEmail(email)
        .flatMap(contacto -> 
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(contacto)))
        .switchIfEmpty(response404);
    }

    // Insertar un contacto

    public Mono<ServerResponse> insertarContacto(ServerRequest request){
        Mono<Contacto> contactoMono = request.bodyToMono(Contacto.class);
        
        return contactoMono.flatMap(contacto -> contactoRepository.save(contacto)
            .flatMap(contactoGuardado -> ServerResponse.accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(contactoGuardado))))
            .switchIfEmpty(response406);
    }

    //Actualizar Contacto

    public Mono<ServerResponse> actualizarContacto (ServerRequest request){
        Mono<Contacto> contactoMono = request.bodyToMono(Contacto.class);
        String id=request.pathVariable("id");

        Mono<Contacto> contactoActualizado = contactoMono.flatMap(contacto -> 
                        contactoRepository
                            .findById(id)
                            .flatMap(oldContacto -> {
                                    oldContacto.setTelefono(contacto.getTelefono());
                                    oldContacto.setEmail(contacto.getEmail());
                                    oldContacto.setNombre(contacto.getNombre());
                                    return contactoRepository.save(oldContacto);
                                })
                        );
        return contactoActualizado.flatMap(contacto -> 
            ServerResponse.accepted()
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(contacto)))
        .switchIfEmpty(response404);
    }

    // Eliminar un contacto

    public Mono<ServerResponse> eliminarContacto(ServerRequest request){
        String id=request.pathVariable("id");
        Mono<Void> contactoEliminado = contactoRepository.deleteById(id);

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(contactoEliminado, Void.class);
    }
}
