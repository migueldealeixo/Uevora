% Estados
estado_inicial(1,1,[]).
estado_final(2,7,[a,b]).

% Objetos
objeto_a(2,4).
objeto_b(2,6).

% Obstaculos
obstaculo(1,6).
obstaculo(1,2).
obstaculo(3,5).
obstaculo(3,6).
obstaculo(4,1).
obstaculo(4,2).
obstaculo(4,3).
obstaculo(4,4).
obstaculo(4,7).
obstaculo(7,6).

% Atualiza lista de objetos ao entrar em uma célula
atualiza_objetos(X, Y, Objs, NovosObjs) :-
    (objeto_a(X,Y), \+ member(a, Objs) -> NovosObjs = [a|Objs];
     objeto_b(X,Y), \+ member(b, Objs) -> NovosObjs = [b|Objs];
     NovosObjs = Objs).

% Movimentos (Cima, Baixo, Esquerda, Direita)
movimento((X,Y,Objs), (X,Y1,NovosObjs)):-
    Y1 is Y + 1, 
    \+ obstaculo(X, Y1),
    atualiza_objetos(X, Y1, Objs, NovosObjs).

movimento((X,Y,Objs), (X,Y1,NovosObjs)):-
    Y1 is Y - 1, 
    \+ obstaculo(X, Y1),
    atualiza_objetos(X, Y1, Objs, NovosObjs).

movimento((X,Y,Objs), (X1,Y,NovosObjs)):-
    X1 is X - 1, 
    \+ obstaculo(X1, Y),
    atualiza_objetos(X1, Y, Objs, NovosObjs).

movimento((X,Y,Objs), (X1,Y,NovosObjs)):-
    X1 is X + 1, 
    \+ obstaculo(X1, Y),
    atualiza_objetos(X1, Y, Objs, NovosObjs).

% Verifica se o estado final foi alcançado
verifica_estado_final((X, Y, Objetos)):-
    estado_final(X, Y, ObjetosFinal),
    subset(ObjetosFinal, Objetos).

% Implementação de subset
subset([], _).
subset([H|T], L) :- member(H, L), subset(T, L).

% BFS - Busca em Largura
bfs(CaminhoFinal) :-
    estado_inicial(X,Y,Objs),
    bfs([[ (X,Y,Objs) ]], [], CaminhoFinal).

% Processo de BFS
bfs([], _, _) :- !, fail.
bfs([Caminho|Fila], Visitados, Solucao) :-
    Caminho = [EstadoAtual|_],
    (verifica_estado_final(EstadoAtual) 
        -> reverse(Caminho, Solucao)
        ;  findall([NovoEstado|Caminho], 
                   (movimento(EstadoAtual, NovoEstado),
                    \+ member(NovoEstado, Visitados)), 
                   NovosCaminhos),
           append(Fila, NovosCaminhos, NovaFila),
           bfs(NovaFila, [EstadoAtual|Visitados], Solucao)).


% BFS - Busca em Largura com contagem de estados visitados e memória usada
bfs_contagem(CaminhoFinal, EstadosVisitados, MaxFronteira) :-
    estado_inicial(X, Y, Objs),
    bfs_contagem_aux([[ (X, Y, Objs) ]], [(X, Y, Objs)], CaminhoFinal, 0, EstadosVisitados, 1, MaxFronteira).

bfs_contagem_aux([[EstadoAtual|Caminho]|_], _, Solucao, Visitados, Visitados, MaxFronteira, MaxFronteira) :-
    verifica_estado_final(EstadoAtual),
    reverse([EstadoAtual|Caminho], Solucao).

bfs_contagem_aux([Caminho|Fila], VisitadosAnt, Solucao, Visitados, EstadosVisitados, MaxFronteiraAnt, MaxFronteira) :-
    Caminho = [EstadoAtual|_],
    findall(
        [NovoEstado|Caminho],
        ( movimento(EstadoAtual, NovoEstado),
          \+ member(NovoEstado, Caminho),
          \+ member(NovoEstado, VisitadosAnt)
        ),
        NovosCaminhos
    ),
    findall(
        Estado,
        (member([Estado|_], NovosCaminhos)),
        EstadosNovos
    ),
    append(VisitadosAnt, EstadosNovos, NovoVisitados),
    append(Fila, NovosCaminhos, NovaFila),
    length(NovaFila, TamanhoFila),
    length(EstadosNovos, NovosEstadosCount), 
    MaxFronteiraAtualizada is max(MaxFronteiraAnt, TamanhoFila),  
    NovosVisitados is Visitados + NovosEstadosCount,  
    bfs_contagem_aux(NovaFila, NovoVisitados, Solucao, NovosVisitados, EstadosVisitados, MaxFronteiraAtualizada, MaxFronteira).

% Heuristica 1
h1((X,Y,_), H) :-  
    estado_final(XF, YF, _),
    H is abs(X - XF) + abs(Y - YF).

% Heuristica 2
h2((X,Y,Objetos), H) :-
    estado_final(XF, YF, ObjetosFinais),
    findall(Dist, (
        member(Obj, ObjetosFinais), 
        \+ member(Obj, Objetos),
        (   (Obj = a, objeto_a(OX, OY));
            (Obj = b, objeto_b(OX, OY)) ),
        Dist is abs(OX - X) + abs(OY - Y)
    ), DistsObjetos),
    (DistsObjetos = [] -> SomaObjetos is 0 ; min_list(DistsObjetos, SomaObjetos)),
    H is abs(X - XF) + abs(Y - YF) + SomaObjetos.


% Algoritmo A *
aestrela(CaminhoFinal, EstadosVisitados, MaxFronteira) :-
    estado_inicial(X, Y, Objetos),
    h2((X, Y, Objetos), H),
    aestrela_busca([((X, Y, Objetos), [(X, Y, Objetos)], 0, H)], 0, 1, CaminhoReverso, EstadosVisitados, MaxFronteira),
    reverse(CaminhoReverso, CaminhoFinal). 

aestrela_busca([((X, Y, Objetos), Caminho, _, _)|_], Visitados, MaxFronteira, Caminho, Visitados, MaxFronteira) :-
    verifica_estado_final((X, Y, Objetos)).  

aestrela_busca([((X, Y, Objetos), Caminho, G, _)|Fila], Visitados, MaxFronteiraAtual, Solucao, VisitadosFinais, MaxFronteiraFinal) :-
    findall(((NX, NY, NObjs), [(NX, NY, NObjs) | Caminho], NG, NF),
            (movimento((X, Y, Objetos), (NX, NY, NObjs)),
             \+ member((NX, NY, NObjs), Caminho),  % Evitar ciclos
             NG is G + 1,  
             h2((NX, NY, NObjs), H),  
             NF is NG + H  
            ),
            NovosNos),
    append(Fila, NovosNos, NovaFila),
    length(NovaFila, TamanhoFila),  
    MaxFronteiraNova is max(MaxFronteiraAtual, TamanhoFila), 
    EstadosVisitadosNovo is Visitados + 1, 
    ordenar_por_f(NovaFila, FilaOrdenada), 
    aestrela_busca(FilaOrdenada, EstadosVisitadosNovo, MaxFronteiraNova, Solucao, VisitadosFinais, MaxFronteiraFinal).

ordenar_por_f(Lista, ListaOrdenada) :-
    criar_pares(Lista, ListaPares),
    sort(ListaPares, ListaParesOrdenada),  % Ordena por F
    remover_pares(ListaParesOrdenada, ListaOrdenada).

criar_pares([], []).
criar_pares([((X, Y, Objs), Caminho, G, F) | Resto], [(F, (X, Y, Objs), Caminho, G, F) | Pares]) :-
    criar_pares(Resto, Pares).

remover_pares([], []).
remover_pares([(_, X, Caminho, G, F) | Resto], [(X, Caminho, G, F) | Lista]) :-
    remover_pares(Resto, Lista).

