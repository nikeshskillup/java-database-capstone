## Architecture Summary

This Spring Boot application follows a three-tier architecture that cleanly separates Presentation, Application, and Data concerns :contentReference[oaicite:0]{index=0}.  
The Presentation tier comprises Thymeleaf templates for server-rendered dashboards (Admin, Doctor) alongside API clients that consume REST endpoints :contentReference[oaicite:1]{index=1}.  
The Application tier is built on Spring Boot and includes MVC and REST controllers, a service layer for business logic, and utility components to coordinate workflows :contentReference[oaicite:2]{index=2}.  
The Data tier integrates a relational store (MySQL) via Spring Data JPA for structured entities and a document store (MongoDB) via Spring Data MongoDB for flexible records such as prescriptions :contentReference[oaicite:3]{index=3}.  

## Numbered Flow of Data and Control

1. **User Interaction**  
   A user accesses either a Thymeleaf-rendered dashboard URL in their browser or invokes a REST API endpoint via a client (e.g., single-page app or mobile) :contentReference[oaicite:4]{index=4}.

2. **Request Routing**  
   The incoming HTTP request is routed by Spring’s DispatcherServlet to the appropriate controller based on URL patterns and HTTP method (GET, POST, etc.) :contentReference[oaicite:5]{index=5}.

3. **Controller Processing**  
   The selected MVC or REST controller validates input parameters, handles authentication checks, and delegates core logic to the service layer :contentReference[oaicite:6]{index=6}.

4. **Service Layer Execution**  
   The service layer applies business rules (e.g., checking doctor availability) and orchestrates transactions by invoking one or more repository methods :contentReference[oaicite:7]{index=7}.

5. **Repository Data Access**  
   Repository interfaces abstract data storage: Spring Data JPA repositories execute SQL against MySQL, while Spring Data MongoDB repositories interact with MongoDB collections :contentReference[oaicite:8]{index=8}.

6. **Model Binding**  
   Retrieved records are mapped to Java model classes—JPA entities for relational data (`@Entity`) and document classes for MongoDB (`@Document`)—enabling type-safe access :contentReference[oaicite:9]{index=9}.

7. **Response Rendering**  
   Finally, MVC controllers merge model data into Thymeleaf templates and return HTML, whereas REST controllers serialize Java objects (or DTOs) to JSON for the client :contentReference[oaicite:10]{index=10}.
