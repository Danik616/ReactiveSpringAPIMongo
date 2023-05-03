package com.api.rest.mongo.repaso.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import com.api.rest.mongo.repaso.documents.Contacto;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactoRepositoryTest {
    
    @Autowired
    private ContactoRepository contactoRepository;

    @Autowired
    private ReactiveMongoOperations mongoOperations;

    @BeforeAll
    public void insertarDatos(){
        Contacto contacto1= new Contacto("Jhon", "jhon@ejemplo.com", "1234566");  
        Contacto contacto2= new Contacto("jonny", "jonny@ejemplo.com", "1234566"); 
        Contacto contacto3= new Contacto("Hello", "hello@ejemplo.com", "1234566");     

        //Guardamos los contactos

        StepVerifier.create(contactoRepository.insert(contacto1).log())
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete();

        StepVerifier.create(contactoRepository.save(contacto2).log())
        .expectSubscription()
        .expectNextCount(1)
        .verifyComplete();
        StepVerifier.create(contactoRepository.save(contacto3).log())
        .expectSubscription()
        .expectNextCount(1)
        .verifyComplete();
    }

    @Test
    @Order(1)
    public void testListarContactos(){
        StepVerifier.create(contactoRepository.findAll().log())
        .expectSubscription()
        .expectNextCount(3)
        .verifyComplete();
    }

    @Test
    @Order(2)
    public void testBuscarPorEmail(){
        StepVerifier.create(contactoRepository.findFirstByEmail("jonny@ejemplo.com").log())
        .expectSubscription()
        .expectNextMatches(contacto -> contacto.getEmail().equals("jonny@ejemplo.com"))
        .verifyComplete();
    }

    @Test
    @Order(3)
    public void testActualizarcontacto(){
        Mono<Contacto> contactoActualizado = contactoRepository.findFirstByEmail("jonny@ejemplo.com")
        .map(contacto -> {contacto.setTelefono("1111111"); return contacto;})
        .flatMap(contacto -> {
            return contactoRepository.save(contacto);
        });

        StepVerifier.create(contactoActualizado.log()).expectSubscription()
        .expectNextMatches(contacto -> (
            contacto.getTelefono().equals("1111111")
        ))
        .verifyComplete();
    } 

    @Test
    @Order(4)
    public void testEliminarContactoPorId(){
        Mono<Void> contactoEliminado = contactoRepository.findFirstByEmail("hello@ejemplo.com")
        .flatMap(contacto -> {
            return contactoRepository.deleteById(contacto.getId());
        }).log();

        StepVerifier.create(contactoEliminado.log()).expectSubscription()
        .verifyComplete();
    }

    @Test
    public void testEliminarContacto(){
        Mono<Void> contactoEliminado = contactoRepository.findFirstByEmail("hello@ejemplo.com")
        .flatMap(contacto -> {
            return contactoRepository.delete(contacto);
        }).log();

        StepVerifier.create(contactoEliminado.log()).expectSubscription()
        .verifyComplete();
    }


    @AfterAll
    public void limpiarData(){
        Mono<Void> elementosEliminados= contactoRepository.deleteAll();

        StepVerifier.create(elementosEliminados.log()).expectSubscription()
        .verifyComplete();
    }

}
