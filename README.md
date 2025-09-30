# PampaSim – Operating System Simulator

![PampaSim in execution](https://github.com/lups-ufpel/PampaSim/blob/main/doc/PampaSim-running.png?raw=true)

# 1. Overview
Hello @everyone, this project is an operating system simulator to help students in the process of understanding the inner workings of operating systems (OSes).

This program aims to supplement already existing tools and literature, so it can help students understand complex concepts related to OSes.
The project is open source as a means of providing extended support, incremental improvements and enhanced transparency regarding the implementation details.
The simplified user interface provides all the information needed to understand the simulation state at a glance, but not at the expense of providing more
intricate details upon request. 

As it is, some rough edges still remain, but many UX improvements are coming!

## Publications

PampaSim was presented at the 9th SIIEPE (Semana Integrada de Inovação, Ensino, Pesquisa e Extensão). The documents of the presentation are available below:

- [**JOÃO ANTONIO NEVES SOARES**, PAMPAOS: UM AMBIENTE PARA A SIMULAÇÃO DE SISTEMAS OPERACIONAIS](https://cti.ufpel.edu.br/siepe/arquivos/2023/CE_05483.pdf)
- [**GABRIEL LEITE BESSA**, PAMPAOS: APLICAÇÃO DO PADRÃO DE PROJETO COMMAND](https://cti.ufpel.edu.br/siepe/arquivos/2023/CE_06671.pdf)
- [**VINICIUS GARCIA PERUZZI**, PAMPAOS: INTERFACE GRÁFICA DO AMBIENTE DE SIMULAÇÃO](https://cti.ufpel.edu.br/siepe/arquivos/2023/CE_05484.pdf)
- [**LUAN MARK DA SILVA BORELA**, GERÊNCIA DE PROCESSOS NO SIMULADOR PAMPAOS](https://cti.ufpel.edu.br/siepe/arquivos/2023/CE_05754.pdf)

# 2. Features

# 3. Project Structure

## Technologies
- JavaFX - user interface
- SLF4J - logging
- Maven - build system
- JUnit - tests
- Antlr4 - domain specific language parsing
- JAXB - specification marshalling

# 4. Building and Installation PampaOs

## Requirements

_minimum version_:
- **Java:** 21.0.2
- **Maven:** 3.9.9

## Using a Terminal

First, open a terminal at the project root directory.

on Linux/macOs, do:
>>>>>>> 8b668d0 (better script)
`mvn clean install`

# 5. How to use it
Make sure you've built the project successfully!
Launch the application:
`cd sim`
`mvn javafx:run`

Simulate your scenario:
- Add optional modules ("Adicionar módulos" button, along the top bar)
- Choose the scheduler strategy ("Compilar código" button, middle of left bar)
- Register process creation events ("Criar processo" button, top of left bar)
- Run/Pause/Reset the interactive simulation

After a full interactive simulation run, a Gantt chart of process state over time will be written to "./gantt.csv"
as well as pretty-printed to standard output. Example output:
```
INFO  PampaSimViewModel [JavaFX Application Thread]:
	Report
pid    | clocks
       | 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56 57 58 59 60 61
pid 1 |  ?  I  I  I  R  I  I  I  R  i  i  i  i  i  i  i  i  I  I  I  R  i  i  i  i  i  I  I  I  R  R  i  i  i  i  I  I  I  r  r  r  r  r  r  R  R  I  I  I  R  i  i  I  I  I  R  i  i  I  I  I  R
pid 2 |  ?  ?  ?  ?  ?  i  i  i  I  I  I  R  R  R  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t
pid 3 |  ?  ?  ?  ?  ?  ?  ?  i  i  i  i  I  I  I  R  R  i  i  i  i  I  I  I  R  i  i  i  i  i  I  I  I  R  R  R  R  R  R  R  R  R  R  R  R  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t  t
pid 4 |  ?  ?  ?  ?  ?  ?  ?  i  i  i  i  i  i  i  I  I  I  R  i  i  i  i  i  I  I  I  R  i  i  i  i  i  I  I  I  r  r  r  r  r  r  r  r  r  r  r  R  i  i  I  I  I  R  i  i  I  I  I  R  R  t  t
```

Further statistics are filed under the statistics window, accessed through the rightmost button of the topbar (Estatísticas).

# 6. Contributors
- Gabriel Bessa (UFPel)
- João Neves (UFPel)
- Luan Borela (UFPel)
- Vinícius Peruzzi (UFPel)
- Igor Gomes Dutra (UFPel)
- Pedro Porto Souza (UFPel)
- Renzo Rossato Battisti (UFPel)
