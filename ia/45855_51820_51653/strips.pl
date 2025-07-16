% Estado Inicial
posicao_robot(1,1).
holding(vazio).

peca_em(1, 1, 1).
peca_em(2, 1, 2).
peca_em(3, 1, 3).
peca_em(4, 2, 1).
peca_em(vazio, 2, 2).  
peca_em(5, 2, 3).
peca_em(6, 3, 1).
peca_em(7, 3, 2).
peca_em(8, 3, 3).

adjacente(1,1,1,2). adjacente(1,1,2,1).
adjacente(1,2,1,1). adjacente(1,2,1,3). adjacente(1,2,2,2).
adjacente(1,3,1,2). adjacente(1,3,2,3).

adjacente(2,1,1,1). adjacente(2,1,2,2). adjacente(2,1,3,1).
adjacente(2,2,1,2). adjacente(2,2,2,1). adjacente(2,2,2,3). adjacente(2,2,3,2).
adjacente(2,3,1,3). adjacente(2,3,2,2). adjacente(2,3,3,3).

adjacente(3,1,2,1). adjacente(3,1,3,2).
adjacente(3,2,2,2). adjacente(3,2,3,1). adjacente(3,2,3,3).
adjacente(3,3,2,3). adjacente(3,3,3,2).

% Estado Final
peca_em(1, 1, 1).
peca_em(2, 1, 2).
peca_em(3, 1, 3).
peca_em(4, 2, 1).
peca_em(5, 2, 2).
peca_em(8, 2, 3).
peca_em(6, 3, 1).
peca_em(7, 3, 2).
peca_em(vazio, 3, 3).

posicao_robot(3,3).
holding(vazio).


% Mover
precondicoes(mover(X1,Y1,X2,Y2), [posicao_robot(X1,Y1), adjacente(X1,Y1,X2,Y2)]).
adiciona(mover(X1,Y1,X2,Y2), [posicao_robot(X2,Y2)]).
remove(mover(X1,Y1,X2,Y2), [posicao_robot(X1,Y1)]).

% Agarrar
precondicoes(agarrar(P,X,Y), [posicao_robot(X,Y), peca_em(P,X,Y), P \= vazio, holding(vazio)]).
adiciona(agarrar(P,X,Y), [holding(P), peca_em(vazio,X,Y)]).
remove(agarrar(P,X,Y), [holding(vazio), peca_em(P,X,Y)]).

% Largar
precondicoes(largar(P,X,Y), [posicao_robot(X,Y), holding(P), P \= vazio]).
adiciona(largar(P,X,Y), [holding(vazio), peca_em(P,X,Y)]).
remove(largar(P,X,Y), [holding(P), peca_em(vazio,X,Y)]). 