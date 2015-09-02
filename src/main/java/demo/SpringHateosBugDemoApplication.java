package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@SpringBootApplication
public class SpringHateosBugDemoApplication implements ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(SpringHateosBugDemoApplication.class, args);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("application/x-spring-data-verbose+json"));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PagedResources<Person>> springCoreResult = restTemplate.exchange
                ("http://localhost:8080/persons", HttpMethod.GET, entity, new
                        ParameterizedTypeReference<PagedResources<Person>>() {
                        });

        System.out.println("Persons loaded from http://localhost:8080/persons using 'new " +
                "ParameterizedTypeReference<PagedResources<Person>>() {}':");
        System.out.println("(content is loaded)" + springCoreResult.getBody().getContent());

        System.out.println();
        System.out.println("Persons loaded from http://localhost:8080/persons using 'new TypeReferences" +
                ".PagedResourcesType<Person>()':");
        ResponseEntity<PagedResources<Person>> springHateosResult = restTemplate.exchange
                ("http://localhost:8080/persons", HttpMethod.GET, entity, new TypeReferences
                        .PagedResourcesType<Person>());

        System.out.println("(content is empty) " + springHateosResult.getBody().getContent());
    }

    @Autowired
    private PersonRepository personRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        personRepository.save(Arrays.asList(new Person("Alice", "Smith"), new Person("Bob", "Builder")));
    }
}
