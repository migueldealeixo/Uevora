 create table cliente(name varchar(100),
 idNumber int not null ,
 numeroTele int,
 clienteID serial int primary key);

 create table veiculo(matricula varchar(6),
  modelo varchar(50),
   tipo varchar(50),
    localizacao varchar(100),
     estadoAluguer varchar(100),
      estadoAdmin boolean,
       veiculoID serial primary key);

 create table aluguer(veiculoID integer references veiculo(veiculoID),
  clienteID integer references cliente(clienteID),
  valor float ,
  dataInicio varchar(10),
   duracaoPrev float,
    aluguerID serial primary key);