% Definição de Estados
% estado(Caixa1,Caixa2,Saco)

estado_inicial(estado(0,0,0)).
estado_final(estado(_,_,300)).

% Operações Possíveis

op(estado(X,Y,S),estado(500,Y,S)):-
    X < 500.  % Encher C1

op(estado(X,Y,S),estado(X,200,S)):-
    Y < 200.  % Encher C2

op(estado(X,Y,S),estado(0,Y,S)):-
    X > 0.  % Esvaziar C1

op(estado(X,Y,S),estado(X,0,S)):-
    Y > 0.  % Esvaziar C2

op(estado(X,Y,S),estado(NX,NY,S)):-
    Cmax1 is 500 - X,
    Trans is min(Y,Cmax1),
    Trans > 0,  % Garantir que há algo para transferir
    NX is X + Trans, NY is Y - Trans.  

op(estado(X,Y,S),estado(NX,NY,S)):-
    Cmax2 is 200 - Y,
    Trans is min(X,Cmax2),
    Trans > 0,
    NX is X - Trans, NY is Y + Trans.  % Transferir de C1 para C2

op(estado(X,Y,S),estado(NX,Y,NS)):-
    Trans is min(X, 300 - S),
    Trans > 0,
    NX is X - Trans,
    NS is S + Trans.  % Transferir de C1 para Saco

op(estado(X,Y,S),estado(X,NY,NS)):-
    Trans is min(Y, 300 - S),
    Trans > 0,
    NY is Y - Trans,
    NS is S + Trans.  % Transferir de C2 para Saco

bfs([Estado | _], _) :- 
    estado_final(Estado),  
    write('Solução encontrada: '), nl,
    write(Estado), nl.

bfs([Estado | Resto], Visitados) :-
    findall(NovoEstado, (op(Estado, NovoEstado), \+ member(NovoEstado, Visitados)), NovosEstados),
    append(Resto, NovosEstados, NovaFila),
    append(Visitados, NovosEstados, NovosVisitados),
    bfs(NovaFila, NovosVisitados).  

resolver_bfs :-
    estado_inicial(EstadoInicial),
    bfs([EstadoInicial], [EstadoInicial]).

heuristica(estado(C1, C2, S), H) :-
    Remaining is 300 - S,  
    ( Remaining =< 0       
      -> H = 0             
    ; (C1 >= Remaining ; C2 >= Remaining)  
    ; H is (Remaining + 499) // 500  
    ).