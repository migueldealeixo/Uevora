% Valores possíveis
valor(V) :- between(1,9,V).

% Estado inicial
posicao_branca(2,4). posicao_branca(2,5).
posicao_branca(3,2). posicao_branca(3,3). posicao_branca(3,4). posicao_branca(3,5).
posicao_branca(4,2). posicao_branca(4,3). posicao_branca(4,4).
posicao_branca(5,2). posicao_branca(5,3).

segmento_linha(2, [posicao_branca(2,4), posicao_branca(2,5)], 13).
segmento_linha(3, [posicao_branca(3,2), posicao_branca(3,3), posicao_branca(3,4), posicao_branca(3,5)], 24).
segmento_linha(4, [posicao_branca(4,2), posicao_branca(4,3), posicao_branca(4,4)], 23).
segmento_linha(5, [posicao_branca(5,2), posicao_branca(5,3)], 11).

segmento_coluna(2, [posicao_branca(3,2), posicao_branca(4,2), posicao_branca(5,2)], 23).
segmento_coluna(3, [posicao_branca(3,3), posicao_branca(4,3), posicao_branca(5,3)], 9).
segmento_coluna(4, [posicao_branca(2,4), posicao_branca(3,4), posicao_branca(4,4)], 24).
segmento_coluna(5, [posicao_branca(2,5), posicao_branca(3,5)], 15).

possibilidades_segmento(N, Soma, Combinacoes) :-
    findall(Comb,
        (
            length(Comb, N),
            Comb ins 1..9,
            all_different(Comb),
            sum(Comb, #=, Soma),
            label(Comb)
        ),
        Combinacoes)).




inicializar_dominios :-
    retractall(dom(_,_,_)),
    forall(posicao_branca(L,C), assertz(dom(L,C,[1,2,3,4,5,6,7,8,9]))),
    retractall(node_count(_)),
    assertz(node_count(0)).

obter_dominios(Doms) :-
    findall(dom(L,C,Vs), dom(L,C,Vs), Doms).

selecionar_variavel(dom(L,C,Vs)) :-
    obter_dominios(Doms),
    selecionar_menor(Doms, dom(L,C,Vs)).

selecionar_menor([D], D).
selecionar_menor([dom(L1,C1,Vs1), dom(L2,C2,Vs2)|Resto], Menor) :-
    length(Vs1, Len1),
    length(Vs2, Len2),
    ( Len1 =< Len2 ->
        selecionar_menor([dom(L1,C1,Vs1)|Resto], Menor)
    ;
        selecionar_menor([dom(L2,C2,Vs2)|Resto], Menor)
    ).

atualizar_dominios(L, C, V, Backup) :-
    findall(dom(OL,OC,OD),
        (
            (segmento_linha(_,Pos,Soma); segmento_coluna(_,Pos,Soma)),
            member(posicao_branca(L,C), Pos),
            member(posicao_branca(OL,OC), Pos),
            dom(OL,OC,OD),
            (OL \= L ; OC \= C)  % Exclui a célula atual
        ),
        Segmentos),
    atualizar_dominios_segmentos(Segmentos, L, C, V, Backup).

atualizar_dominios_segmentos([], _, _, _, []).
atualizar_dominios_segmentos([dom(OL,OC,OD)|Rest], L, C, V, [BackupItem|BackupRest]) :-
    (member(V, OD) -> 
        delete(OD, V, ND), 
        BackupItem = dom(OL,OC,OD),
        retract(dom(OL,OC,_)), 
        assertz(dom(OL,OC,ND)),
        verificar_soma_e_reduzir(OL, OC, ND)  % Nova verificação de soma
    ; 
        BackupItem = dom(OL,OC,OD),
        ND = OD
    ),
    atualizar_dominios_segmentos(Rest, L, C, V, BackupRest).

verificar_soma_e_reduzir(L, C, Dominio) :-
    (segmento_linha(Lin, Pos, Soma); segmento_coluna(Col, Pos, Soma)),
    member(posicao_branca(L,C), Pos),
    valores_atribuidos(Pos, ValoresAtribuidos),
    sum_list(ValoresAtribuidos, Parcial),
    SomaRestante is Soma - Parcial,
    length(Pos, Total),
    length(ValoresAtribuidos, Atribuidos),
    Remanescentes is Total - Atribuidos,
    (Remanescentes > 0 ->
        MinRemanescente is max(1, SomaRestante - (Remanescentes-1)*9),
        MaxRemanescente is min(9, SomaRestante - (Remanescentes-1)*1),
        findall(X, (member(X, Dominio), X >= MinRemanescente, X =< MaxRemanescente), NovoDominio),
        retract(dom(L,C,_)),
        assertz(dom(L,C,NovoDominio))
    ; true).