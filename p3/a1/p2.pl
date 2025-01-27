e(lisboa, santarem).
e(santarem, coimbra).
e(santarem, caldas).
e(caldas, lisboa).
e(coimbra, porto).
e(lisboa, evora).
e(evora, beja).
e(lisboa, faro).
e(beja, faro).

existe(X,Y):-e(X,Y).

caminho(X,Y,1):-e(X,Y).

caminho(X, Y, [X, Y]) :- e(X, Y).
caminho(X, Y, [X|R]) :-
    e(X, Z),
    caminho(Z, Y, R).

comp(c(_),1).

comp(c(_,sub),L):-
    comp(sub,L1),
    L is L1 + 1.


