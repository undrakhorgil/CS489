# Lab 7b - GraphQL Welcome API (Postgres)

## Prereqs
- Java 21
- Maven
- A local Postgres running (your Docker container `postgres1`), with:
  - **db**: `ads_lab7b_db`
  - **username**: `postgres`
  - **password**: `postgres`
  - **port**: `5432` exposed to localhost

## Run

```bash
cd "lab7b/ads-lab7b-graphql-welcome"
mvn spring-boot:run
```

## Open GraphiQL
- `http://localhost:8080/graphiql`

## Example query

```graphql
query {
  welcome(name: "MIU")
}
```

Or:

```graphql
query {
  welcome
}
```

