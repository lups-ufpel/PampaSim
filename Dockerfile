# Use uma imagem base com o JDK 21 instalado
FROM eclipse-temurin:21-jdk

# Instala o Git e o Maven
RUN apt-get update && apt-get install  --no-install-recommends -y git maven xorg

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Clona o repositório do GitHub
RUN git clone https://github.com/lups-ufpel/PampaSim.git

# Define o diretório do projeto clonado
WORKDIR /app/PampaSim

# Limpa o cache do Maven
RUN mvn dependency:purge-local-repository -DactTransitively=false -DreResolve=false

# Compila a aplicação com Maven
RUN mvn clean install -DskipTests

# Executa a aplicação com Mavene JavaFX
CMD ["mvn", "javafx:run"]
