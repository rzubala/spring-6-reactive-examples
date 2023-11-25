package guru.springframework.spring6reactiveexamples.repositories;

import guru.springframework.spring6reactiveexamples.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryImplTest {
    private PersonRepository personRepository = new PersonRepositoryImpl();

    @Test
    void testGetByIdMonoBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        Person person = personMono.block();

        System.out.println(person.toString());
    }

    @Test
    void testGetByIdMonoSubscribe() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.subscribe(p -> System.out.println(p.toString()));
    }

    @Test
    void testMapOperation() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxBlockFirst() {
        Flux<Person> fluxPerson = personRepository.findAll();

        Person person = fluxPerson.blockFirst();

        System.out.println(person.toString());
    }

    @Test
    void testFluxSubscriber() {
        Flux<Person> fluxPerson = personRepository.findAll();

        fluxPerson.subscribe(p -> System.out.println(p.toString()));
    }

    @Test
    void testFluxMapOperation() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxToList() {
        Flux<Person> fluxPerson = personRepository.findAll();

        Mono<List<Person>> listMono = fluxPerson.collectList();
        listMono.subscribe(list -> list.forEach(p -> System.out.println(p.getFirstName())));
    }

    @Test
    void testFilterOnName() {
        personRepository.findAll()
                .filter(p -> p.getFirstName().equals("Fiona"))
                .subscribe(System.out::println);
    }

    @Test
    void testFilterGetMono() {
        Mono<Person> monoPerson = personRepository.findAll().filter(p -> p.getFirstName().equals("Fiona"))
                .next();
        monoPerson.subscribe(System.out::println);
    }

    @Test
    void testGetByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 8;

        Mono<Person> personMono = personFlux.filter(p -> Objects.equals(p.getId(), id)).single()
                .doOnError(throwable -> {
                    System.out.println("Error in flux");
                    System.out.println(throwable.toString());
                });

        personMono.subscribe(p -> System.out.println(p.getFirstName()), throwable -> {
            System.out.println("Error in mono");
            System.out.println(throwable.toString());
        });
    }
}