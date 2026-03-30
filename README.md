# CS489 Lab 1 – Product Management CLI

## Requirements covered (from `lab1.pdf`)

- `Product` model in package `edu.miu.cs.cs489appsd.lab1.productmgmtapp.model`
- `ProductMgmtApp` executable in package `edu.miu.cs.cs489appsd.lab1.productmgmtapp`
- Loads the provided company data into an array
- Prints products sorted by **name (ascending)** then **unitPrice (descending)** in **JSON**, **XML**, and **CSV**

## How to compile & run

```bash
javac -d out $(find src -name "*.java")
java -cp out edu.miu.cs.cs489appsd.lab1.productmgmtapp.ProductMgmtApp
```

## Screenshots required for submission

Per the PDF, place your screenshots inside `./screenshots/` (png or jpg), including:

- `javac -version` output
- `git --version` output
- IDE showing program output (JSON, XML, CSV)

