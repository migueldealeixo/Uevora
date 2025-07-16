estado_inicial(e([0,0,0,0])).

terminal(e(Linhas)):-
    soma_total(Linhas,16).

valor(E,-1):-
    terminal(E).

op(e(Linhas),joga(LinhaIndex,Num),e(Linhas2)):-
    between(1,4,Num),
    atualiza_linha(Linhas,LinhaIndex,Num,Linhas2),
    
