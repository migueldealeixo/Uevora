% Estado Inicial
posicao_branca(2,4).
posicao_branca(2,5).
posicao_branca(3,2).
posicao_branca(3,3).
posicao_branca(3,4).
posicao_branca(3,5).
posicao_branca(4,2).
posicao_branca(4,3).
posicao_branca(4,4).
posicao_branca(5,2).
posicao_branca(5,3).

% Segmentos de linha
segmento_linha(2, [posicao_branca(2,4), posicao_branca(2,5)], 13).
segmento_linha(3, [posicao_branca(3,2), posicao_branca(3,3), posicao_branca(3,4), posicao_branca(3,5)], 24).
segmento_linha(4, [posicao_branca(4,2), posicao_branca(4,3), posicao_branca(4,4)], 23).
segmento_linha(5, [posicao_branca(5,2), posicao_branca(5,3)], 11).

% Segmentos de coluna
segmento_coluna(2, [posicao_branca(3,2), posicao_branca(4,2), posicao_branca(5,2)], 23).
segmento_coluna(3, [posicao_branca(3,3), posicao_branca(4,3), posicao_branca(5,3)], 9).
segmento_coluna(4, [posicao_branca(2,4), posicao_branca(3,4), posicao_branca(4,4)], 24).
segmento_coluna(5, [posicao_branca(2,5), posicao_branca(3,5)], 15).

% Restrições
valor(V) :- between(1, 9, V).

todos_diferentes([]).
todos_diferentes([X|Xs]) :- \+ member(X, Xs), todos_diferentes(Xs).

soma_posicoes([], _, 0).
soma_posicoes([posicao_branca(L,C)|R], Atribs, Soma) :-
    member(posicao_valor(L,C,V), Atribs),
    soma_posicoes(R, Atribs, Resto),
    Soma is V + Resto.

valores_posicoes([], _, []).
valores_posicoes([posicao_branca(L,C)|R], Atribs, [V|VR]) :-
    member(posicao_valor(L,C,V), Atribs),
    valores_posicoes(R, Atribs, VR).

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

todas_atribuidas([], _).
todas_atribuidas([posicao_branca(L,C)|R], Atribs) :-
    member(posicao_valor(L,C,_), Atribs),
    todas_atribuidas(R, Atribs).

% Backtracking
backtracking([], Atribs, Atribs).
backtracking([posicao_branca(L,C)|Resto], AtribsAtual, Solucao) :-
    valor(V),
    NovoAtribs = [posicao_valor(L,C,V)|AtribsAtual],
    (   verifica_restricoes(NovoAtribs)
    ->  backtracking(Resto, NovoAtribs, Solucao)
    ;   fail).

resolver(Solucao) :-
    findall(posicao_branca(L,C), posicao_branca(L,C), Posicoes),
    backtracking(Posicoes, [], Solucao),
    write(Solucao), nl.

% Forward Checking
inicializar_dominios(Posicoes, Dominios) :-
    findall(dominio(L,C,[1,2,3,4,5,6,7,8,9]), 
            member(posicao_branca(L,C), Posicoes), 
            Dominios).

dominio_vazio(Dominios) :-
    member(dominio(_,_,[]), Dominios).

obter_dominio(L, C, Dominios, Dominio) :-
    member(dominio(L,C,Dominio), Dominios).

forward_checking(posicao_valor(L,C,V), AtribsAtual, Dominios, NovosDominios) :-
    propagar_restricoes_linha(L, C, V, AtribsAtual, Dominios, Dominios1),
    propagar_restricoes_coluna(L, C, V, AtribsAtual, Dominios1, NovosDominios),
    \+ dominio_vazio(NovosDominios).

propagar_restricoes_linha(L, C, V, Atribs, Dominios, NovosDominios) :-
    (   segmento_linha(_, Posicoes, Soma),
        member(posicao_branca(L,C), Posicoes)
    ->  filtrar_dominio_segmento(Posicoes, Soma, V, Atribs, Dominios, NovosDominios)
    ;   NovosDominios = Dominios
    ).

propagar_restricoes_coluna(L, C, V, Atribs, Dominios, NovosDominios) :-
    (   segmento_coluna(_, Posicoes, Soma),
        member(posicao_branca(L,C), Posicoes)
    ->  filtrar_dominio_segmento(Posicoes, Soma, V, Atribs, Dominios, NovosDominios)
    ;   NovosDominios = Dominios
    ).

filtrar_dominio_segmento(Posicoes, SomaTotal, ValorAtribuido, Atribs, Dominios, NovosDominios) :-
    posicoes_nao_atribuidas(Posicoes, Atribs, PosNaoAtrib),
    valores_ja_atribuidos(Posicoes, Atribs, ValoresUsados),
    append(ValoresUsados, [ValorAtribuido], TodosUsados),
    sum_list(TodosUsados, SomaUsados),
    SomaRestante is SomaTotal - SomaUsados,
    length(PosNaoAtrib, NumVarsRestantes),
    filtrar_dominios_posicoes(PosNaoAtrib, TodosUsados, SomaRestante, 
                             NumVarsRestantes, Dominios, NovosDominios).

filtrar_dominios_posicoes([], _, _, _, Dominios, Dominios).
filtrar_dominios_posicoes([posicao_branca(L,C)|Resto], ValoresUsados, 
                         SomaRestante, NumVars, Dominios, NovosDominios) :-
    obter_dominio(L, C, Dominios, DominioAtual),
    filtrar_valores_invalidos(DominioAtual, ValoresUsados, SomaRestante, 
                             NumVars, NovoDominio),
    atualizar_dominio(L, C, NovoDominio, Dominios, DominiosTemp),
    filtrar_dominios_posicoes(Resto, ValoresUsados, SomaRestante, 
                             NumVars, DominiosTemp, NovosDominios).

:- dynamic nos_visitados/1.

% Inicializar contagem de nós
iniciar_contagem :-
    retractall(nos_visitados(_)),
    assert(nos_visitados(0)).

incrementar_nos_visitados :-
    retract(nos_visitados(N)),
    N1 is N + 1,
    assert(nos_visitados(N1)).

selecionar_variavel(Dominios, Pos, DominioRestante, Outros) :-
    exclude(tem_dominio_vazio, Dominios, DominiosValidos),
    % Cria pares de comprimento-domínio
    maplist(create_length_pair, DominiosValidos, Pairs),
    keysort(Pairs, SortedPairs),
    % Seleciona o domínio com menor tamanho
    SortedPairs = [_-dominio(L,C,DominioRestante)|_],
    Pos = posicao_branca(L,C),
    exclude(igual_posicao(L,C), Dominios, Outros).

create_length_pair(dominio(L,C,D), Len-dominio(L,C,D)) :-
    length(D, Len).
tem_dominio_vazio(dominio(_,_,[])).

igual_posicao(L,C,dominio(L1,C1,_)) :- L =:= L1, C =:= C1.

% Backtracking com FC + MRV
backtracking_fc([], _, Atribs, Atribs).
backtracking_fc(Dominios, _, AtribsAtual, Solucao) :-
    selecionar_variavel(Dominios, posicao_branca(L,C), DominioAtual, DominiosRestantes),
    member(V, DominioAtual),
    incrementar_nos_visitados,
    NovoAtribs = [posicao_valor(L,C,V)|AtribsAtual],
    forward_checking(posicao_valor(L,C,V), AtribsAtual, Dominios, NovosDominios),
    remover_posicao_dominios(L, C, NovosDominios, DominiosReduzidos),
    backtracking_fc(DominiosReduzidos, _, NovoAtribs, Solucao).


posicoes_nao_atribuidas(Posicoes, Atribs, PosNaoAtrib) :-
    findall(Pos, 
            (member(Pos, Posicoes), \+ posicao_atribuida(Pos, Atribs)), 
            PosNaoAtrib).

posicao_atribuida(posicao_branca(L,C), Atribs) :-
    member(posicao_valor(L,C,_), Atribs).

valores_ja_atribuidos(Posicoes, Atribs, Valores) :-
    findall(V, 
            (member(posicao_branca(L,C), Posicoes), 
             member(posicao_valor(L,C,V), Atribs)), 
            Valores).

atualizar_dominio(L, C, NovoDominio, Dominios, DominiosTemp) :-
    maplist(replace_dominio(L,C,NovoDominio), Dominios, DominiosTemp).

replace_dominio(L,C,Novo, dominio(L,C,_), dominio(L,C,Novo)) :- !.
replace_dominio(_,_,_, Dom, Dom).

filtrar_valores_invalidos([], _, _, _, []).
filtrar_valores_invalidos([V|Vs], ValoresUsados, SomaRestante, NumVars, [V|Filtrados]) :-
    valid_value(ValoresUsados, SomaRestante, NumVars, V),
    filtrar_valores_invalidos(Vs, ValoresUsados, SomaRestante, NumVars, Filtrados).
filtrar_valores_invalidos([V|Vs], ValoresUsados, SomaRestante, NumVars, Filtrados) :-
    \+ valid_value(ValoresUsados, SomaRestante, NumVars, V),
    filtrar_valores_invalidos(Vs, ValoresUsados, SomaRestante, NumVars, Filtrados).

valid_value(ValoresUsados, SomaRestante, NumVars, V) :-
    \+ member(V, ValoresUsados),
    MinResto is max(1, (NumVars - 1) * 1),
    MaxResto is (NumVars - 1) * 9,
    SomaRestante - V >= MinResto,
    SomaRestante - V =< MaxResto.

remover_posicao_dominios(L, C, Dominios, DominiosReduzidos) :-
    exclude(eh_posicao(L,C), Dominios, DominiosReduzidos).

eh_posicao(L, C, dominio(L,C,_)).

resolver_fc_primeira(Solucao, Nos) :-
    iniciar_contagem,
    findall(posicao_branca(L,C), posicao_branca(L,C), Posicoes),
    inicializar_dominios(Posicoes, Dominios),
    backtracking_fc(Dominios, _, [], Solucao),
    nos_visitados(Nos),
    write('Solução: '), write(Solucao), nl,
    write('Nós visitados: '), write(Nos), nl.
resolver_fc_todas(Solucoes, Nos) :-
    iniciar_contagem,
    findall(posicao_branca(L,C), posicao_branca(L,C), Posicoes),
    inicializar_dominios(Posicoes, Dominios),
    findall(S, backtracking_fc(Dominios, _, [], S), Solucoes),
    nos_visitados(Nos),
    length(Solucoes, N),
    format('Foram encontradas ~w soluções com ~w nós visitados.~n', [N, Nos]).
