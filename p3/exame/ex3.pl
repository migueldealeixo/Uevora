linha(Pos, Linha):-
    Linha is (Pos-1)//8 + 1.

coluna(Pos, Coluna):-
    Coluna is (Pos-1) mod 8 + 1.

na(P, X):-
    linha(P, LinhaP),
    linha(X, LinhaX),
    coluna(P, ColunaP),
    coluna(X, ColunaX),
    LinhaP \= LinhaX,
    ColunaP \= ColunaX.

at(P, X):-
    linha(P, LinhaP),
    linha(X, LinhaX),
    coluna(P, ColunaP),
    coluna(X, ColunaX),
    (LinhaP = LinhaX ; ColunaP = ColunaX).
