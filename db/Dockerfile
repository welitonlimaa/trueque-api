FROM postgres:17

# Copiar o script de inicialização para dentro do container
COPY truequedb.sql /docker-entrypoint-initdb.d/

RUN chmod 755 /docker-entrypoint-initdb.d/truequedb.sql