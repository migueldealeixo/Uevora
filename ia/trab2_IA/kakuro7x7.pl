% Estado Inicial e Dominio
posicao_branca(2,2).
posicao_branca(2,3).
posicao_branca(3,2).
posicao_branca(3,3).
posicao_branca(3,4).
posicao_branca(3,6).
posicao_branca(4,3).
posicao_branca(4,4).
posicao_branca(4,5).
posicao_branca(4,6).
posicao_branca(5,2).
posicao_branca(5,5).
posicao_branca(6,2).
posicao_branca(6,4).

% Segmentos linha/coluna
segmento_linha(2, [posicao_branca(2,2), posicao_branca(2,3)], 20).
segmento_linha(3, [posicao_branca(3,2), posicao_branca(3,3), posicao_branca(3,4), posicao_branca(3,6)], 21).
segmento_linha(4, [posicao_branca(4,3), posicao_branca(4,4), posicao_branca(4,5), posicao_branca(4,6)], 17).
segmento_linha(5, [posicao_branca(5,2), posicao_branca(5,5)], 23).
segmento_linha(6, [posicao_branca(6,2), posicao_branca(6,4)], 19).


segmento_coluna(2, [posicao_branca(2,2), posicao_branca(3,2), posicao_branca(5,2), posicao_branca(6,2)], 13).
segmento_coluna(3, [posicao_branca(2,3), posicao_branca(3,3), posicao_branca(4,3)], 26).
segmento_coluna(4, [posicao_branca(3,4), posicao_branca(4,4), posicao_branca(6,4)], 28).
segmento_coluna(5, [posicao_branca(4,5), posicao_branca(5,5)], 16).
segmento_coluna(6, [posicao_branca(3,6), posicao_branca(4,6)], 6).


% Restrições
valor(V):- between(1,9,V).

todos_diferentes([]).
todos_diferentes([X|Xs]) :- \+ member(X, Xs), todos_diferentes(Xs).

valores_posicoes([], _, []).
valores_posicoes([posicao_branca(L,C)|R], Atribs, [V|VR]) :-
    member(posicao_valor(L,C,V), Atribs),
    valores_posicoes(R, Atribs, VR).

todas_atribuidas([], _).
todas_atribuidas([posicao_branca(L,C)|R], Atribs) :-
    member(posicao_valor(L,C,_), Atribs),
    todas_atribuidas(R, Atribs).

validar_segmento_linha(ID, Atribs) :-
    segmento_linha(ID, Posicoes, Soma),
    valores_posicoes(Posicoes, Atribs, Vs),
    length(Vs, Len),
    length(Posicoes, Len),
    sum_list(Vs, Soma),
    todos_diferentes(Vs).

validar_segmento_coluna(ID, Atribs) :-
    segmento_coluna(ID, Posicoes, Soma),
    valores_posicoes(Posicoes, Atribs, Vs),
    length(Vs, Len),
    length(Posicoes, Len),
    sum_list(Vs, Soma),
    todos_diferentes(Vs).

verifica_restricoes(Atribs) :-
    findall(ID, segmento_linha(ID, _, _), Linhas),
    findall(ID, segmento_coluna(ID, _, _), Colunas),
    verifica_todos(Linhas, Atribs, validar_segmento_linha),
    verifica_todos(Colunas, Atribs, validar_segmento_coluna).

verifica_todos([], _, _).
verifica_todos([ID|R], Atribs, Predicado) :-
    (   segmento_completo(ID, Atribs, Predicado)
    ->  Goal =.. [Predicado, ID, Atribs],
        call(Goal)
    ;   true
    ),
    verifica_todos(R, Atribs, Predicado).


segmento_completo(ID, Atribs, validar_segmento_linha) :-
    segmento_linha(ID, Posicoes, _),
    todas_atribuidas(Posicoes, Atribs).

segmento_completo(ID, Atribs, validar_segmento_coluna) :-
    segmento_coluna(ID, Posicoes, _),
    todas_atribuidas(Posicoes, Atribs).

% Resolver com backtracking

todas_posicoes_brancas(Posicoes) :-
    findall(posicao_branca(L,C), posicao_branca(L,C), Posicoes).

inicializa_atribs([], []).
inicializa_atribs([posicao_branca(L,C)|R], [posicao_valor(L,C,V)|RA]) :-
    valor(V),
    inicializa_atribs(R, RA).

resolve_kakuro(Atribs) :-
    todas_posicoes_brancas(Posicoes),
    resolve(Posicoes, [], Atribs).

resolve([], Atribs, Atribs) :-
    verifica_restricoes(Atribs).

resolve([posicao_branca(L,C)|Resto], Parcial, Final) :-
    valor(V),
    \+ member(posicao_valor(L,C,_), Parcial),
    Novo = [posicao_valor(L,C,V)|Parcial],
    verifica_restricoes(Novo),
    resolve(Resto, Novo, Final).
